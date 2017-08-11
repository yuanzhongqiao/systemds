/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysml.hops.codegen.opt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sysml.hops.Hop;
import org.apache.sysml.hops.codegen.template.CPlanMemoTable;
import org.apache.sysml.runtime.controlprogram.parfor.util.IDSequence;
import org.apache.sysml.runtime.util.UtilFunctions;
import org.apache.sysml.hops.codegen.opt.PlanSelection.VisitMarkCost;

/**
 *  
 */
public class ReachabilityGraph 
{
	private HashMap<Pair<Long,Long>,NodeLink> _matPoints = null;
	private NodeLink _root = null;
	
	private InterestingPoint[] _searchSpace;
	private CutSet[] _cutSets;
	
	public ReachabilityGraph(PlanPartition part, CPlanMemoTable memo) {
		//create repository of materialization points
		_matPoints = new HashMap<>();
		for( InterestingPoint p : part.getMatPointsExt() )
			_matPoints.put(Pair.of(p._fromHopID, p._toHopID), new NodeLink(p));
		
		//create reachability graph
		_root = new NodeLink(null);
		HashSet<VisitMarkCost> visited = new HashSet<>();
		for( Long hopID : part.getRoots() ) {
			Hop rootHop = memo.getHopRefs().get(hopID);
			addInputNodeLinks(rootHop, _root, part, memo, visited);
		}
		
		//create candidate cutsets 
		List<NodeLink> tmpCS = _matPoints.values().stream()
			.filter(p -> p._inputs.size() > 0 && p._p != null)
			.sorted().collect(Collectors.toList());
		
		//short-cut for partitions without cutsets
		if( tmpCS.isEmpty() ) {
			_cutSets = new CutSet[0];
			_searchSpace = part.getMatPointsExt();
			return;
		}
		
		//create composite cutsets 
		ArrayList<ArrayList<NodeLink>> candCS = new ArrayList<>();
		ArrayList<NodeLink> current = new ArrayList<>();
		for( NodeLink node : tmpCS ) {
			if( current.isEmpty() )
				current.add(node);
			else if( current.get(0).equals(node) )
				current.add(node);
			else {
				candCS.add(current);
				current = new ArrayList<>();
				current.add(node);
			}
		}
		if( !current.isEmpty() )
			candCS.add(current);
		
		//evaluate cutsets (single, and duplicate pairs)
		ArrayList<ArrayList<NodeLink>> remain = new ArrayList<>();
		ArrayList<Pair<CutSet,Double>> cutSets = evaluateCutSets(candCS, remain);
		if( !remain.isEmpty() && remain.size() < 5 ) {
			//second chance: for pairs for remaining candidates
			ArrayList<ArrayList<NodeLink>> candCS2 = new ArrayList<>();
			for( int i=0; i<remain.size()-1; i++)
				for( int j=i+1; j<remain.size(); j++) {
					ArrayList<NodeLink> tmp = new ArrayList<>();
					tmp.addAll(remain.get(i));
					tmp.addAll(remain.get(j));
					candCS2.add(tmp);
				}
			ArrayList<Pair<CutSet,Double>> cutSets2 = evaluateCutSets(candCS2, remain);
			//ensure constructed cutsets are disjoint
			HashSet<InterestingPoint> testDisjoint = new HashSet<>();
			for( Pair<CutSet,Double> cs : cutSets2 ) {
				if( !CollectionUtils.containsAny(testDisjoint, Arrays.asList(cs.getLeft().cut)) ) {
					cutSets.add(cs);
					CollectionUtils.addAll(testDisjoint, cs.getLeft().cut);
				}
			}
		}
		
		//sort and linearize search space according to scores
		_cutSets = cutSets.stream()
				.sorted(Comparator.comparing(p -> p.getRight()))
				.map(p -> p.getLeft()).toArray(CutSet[]::new);
	
		HashMap<InterestingPoint, Integer> probe = new HashMap<>();
		ArrayList<InterestingPoint> lsearchSpace = new ArrayList<>();
		for( CutSet cs : _cutSets ) {
			cs.updatePos(lsearchSpace.size());
			cs.updatePartitions(probe);
			CollectionUtils.addAll(lsearchSpace, cs.cut);
			for( InterestingPoint p: cs.cut )
				probe.put(p, probe.size()-1);
		}
		for( InterestingPoint p : part.getMatPointsExt() )
			if( !probe.containsKey(p) ) {
				lsearchSpace.add(p);
				probe.put(p, probe.size()-1);
			}
		_searchSpace = lsearchSpace.toArray(new InterestingPoint[0]);
		
		//materialize partition indices
		for( CutSet cs : _cutSets ) {
			cs.updatePartitionIndexes(probe);
			cs.finalizePartition();
		}
		
		//final sanity check of interesting points
		if( _searchSpace.length != part.getMatPointsExt().length )
			throw new RuntimeException("Corrupt linearized search space: " +
				_searchSpace.length+" vs "+part.getMatPointsExt().length);
	}
	
	public InterestingPoint[] getSortedSearchSpace() {
		return _searchSpace;
	}

	public boolean isCutSet(boolean[] plan) {
		for( CutSet cs : _cutSets )
			if( isCutSet(cs, plan) )
				return true;
		return false;
	}
	
	public boolean isCutSet(CutSet cs, boolean[] plan) {
		boolean ret = true;
		for(int i=0; i<cs.posCut.length && ret; i++)
			ret &= plan[cs.posCut[i]];
		return ret;
	}
	
	public CutSet getCutSet(boolean[] plan) {
		for( CutSet cs : _cutSets )
			if( isCutSet(cs, plan) )
				return cs;
		throw new RuntimeException("No valid cut set found.");
	}

	public long getNumSkipPlans(boolean[] plan) {
		for( CutSet cs : _cutSets )
			if( isCutSet(cs, plan) ) {
				int pos = cs.posCut[cs.posCut.length-1];				
				return UtilFunctions.pow(2, plan.length-pos-1);
			}
		throw new RuntimeException("Failed to compute "
			+ "number of skip plans for plan without cutset.");
	}


	public SubProblem[] getSubproblems(boolean[] plan) {
		CutSet cs = getCutSet(plan);
		return new SubProblem[] {
				new SubProblem(cs.cut.length, cs.posLeft, cs.left), 
				new SubProblem(cs.cut.length, cs.posRight, cs.right)};
	}
	
	@Override
	public String toString() {
		return "ReachabilityGraph("+_matPoints.size()+"):\n"
			+ _root.explain(new HashSet<>());
	}
	
	private void addInputNodeLinks(Hop current, NodeLink parent, PlanPartition part, 
		CPlanMemoTable memo, HashSet<VisitMarkCost> visited) 
	{
		if( visited.contains(new VisitMarkCost(current.getHopID(), parent._ID)) )
			return;
		
		//process children
		for( Hop in : current.getInput() ) {
			if( InterestingPoint.isMatPoint(part.getMatPointsExt(), current.getHopID(), in.getHopID()) ) {
				NodeLink tmp = _matPoints.get(Pair.of(current.getHopID(), in.getHopID()));
				parent.addInput(tmp);
				addInputNodeLinks(in, tmp, part, memo, visited);
			}
			else
				addInputNodeLinks(in, parent, part, memo, visited);
		}
		
		visited.add(new VisitMarkCost(current.getHopID(), parent._ID));
	}
	
	private void rCollectInputs(NodeLink current, HashSet<NodeLink> probe, HashSet<NodeLink> inputs) {
		for( NodeLink c : current._inputs ) 
			if( !probe.contains(c) ) {
				rCollectInputs(c, probe, inputs);
				inputs.add(c);
			}
	}
	
	private ArrayList<Pair<CutSet,Double>> evaluateCutSets(ArrayList<ArrayList<NodeLink>> candCS, ArrayList<ArrayList<NodeLink>> remain) {
		ArrayList<Pair<CutSet,Double>> cutSets = new ArrayList<>();
		
		for( ArrayList<NodeLink> cand : candCS ) {
			HashSet<NodeLink> probe = new HashSet<>(cand);
			
			//determine subproblems for cutset candidates
			HashSet<NodeLink> part1 = new HashSet<>();
			rCollectInputs(_root, probe, part1);
			HashSet<NodeLink> part2 = new HashSet<>();
			for( NodeLink rNode : cand )
				rCollectInputs(rNode, probe, part2);
			
			//select, score and create cutsets
			if( !CollectionUtils.containsAny(part1, part2) 
				&& !part1.isEmpty() && !part2.isEmpty()) {
				//score cutsets (smaller is better)
				double base = UtilFunctions.pow(2, _matPoints.size());
				double numComb = UtilFunctions.pow(2, cand.size());
				double score = (numComb-1)/numComb * base
					+ 1/numComb * UtilFunctions.pow(2, part1.size())
					+ 1/numComb * UtilFunctions.pow(2, part2.size());
				
				//construct cutset
				cutSets.add(Pair.of(new CutSet(
					cand.stream().map(p->p._p).toArray(InterestingPoint[]::new), 
					part1.stream().map(p->p._p).toArray(InterestingPoint[]::new), 
					part2.stream().map(p->p._p).toArray(InterestingPoint[]::new)), score));
			}
			else {
				remain.add(cand);
			}
		}
		
		return cutSets;
	}
		
	public static class SubProblem {
		public int offset;
		public int[] freePos;
		public InterestingPoint[] freeMat;
		
		public SubProblem(int off, int[] pos, InterestingPoint[] mat) {
			offset = off;
			freePos = pos;
			freeMat = mat;
		}
	}
	
	public static class CutSet {
		public InterestingPoint[] cut;
		public InterestingPoint[] left;
		public InterestingPoint[] right;
		public int[] posCut;
		public int[] posLeft;
		public int[] posRight;
		
		public CutSet(InterestingPoint[] cutPoints, 
				InterestingPoint[] l, InterestingPoint[] r) {
			cut = cutPoints;
			left = l;
			right = r;
		}
		
		public void updatePos(int index) {
			posCut = new int[cut.length];
			for(int i=0; i<posCut.length; i++)
				posCut[i] = index + i;
		}
		
		public void updatePartitions(HashMap<InterestingPoint,Integer> blacklist) {
			left = Arrays.stream(left).filter(p -> !blacklist.containsKey(p))
				.toArray(InterestingPoint[]::new);
			right = Arrays.stream(right).filter(p -> !blacklist.containsKey(p))
				.toArray(InterestingPoint[]::new);
		}
		
		public void updatePartitionIndexes(HashMap<InterestingPoint,Integer> probe) {
			posLeft = new int[left.length];
			for(int i=0; i<left.length; i++)
				posLeft[i] = probe.get(left[i]);
			posRight = new int[right.length];
			for(int i=0; i<right.length; i++)
				posRight[i] = probe.get(right[i]);
		}
		
		public void finalizePartition() {
			left = (InterestingPoint[]) ArrayUtils.addAll(cut, left);
			right = (InterestingPoint[]) ArrayUtils.addAll(cut, right);
		}
		
		@Override
		public String toString() {
			return "Cut : "+Arrays.toString(cut);
		}
	}
		
	private static class NodeLink implements Comparable<NodeLink>
	{
		private static final IDSequence _seqID = new IDSequence();
		
		private ArrayList<NodeLink> _inputs = new ArrayList<>();
		private long _ID;
		private InterestingPoint _p;
		
		public NodeLink(InterestingPoint p) {
			_ID = _seqID.getNextID();
			_p = p;
		} 
		
		public void addInput(NodeLink in) {
			_inputs.add(in);
		}
		
		@Override
		public boolean equals(Object o) {
			if( !(o instanceof NodeLink) )
				return false;
			NodeLink that = (NodeLink) o;
			boolean ret = (_inputs.size() == that._inputs.size());
			for( int i=0; i<_inputs.size() && ret; i++ )
				ret &= (_inputs.get(i)._ID == that._inputs.get(i)._ID);
			return ret;
		}
		
		@Override
		public int compareTo(NodeLink that) {
			if( _inputs.size() > that._inputs.size() )
				return -1;
			else if( _inputs.size() < that._inputs.size() )
				return 1;
			for( int i=0; i<_inputs.size(); i++ ) {
				int comp = Long.compare(_inputs.get(i)._ID, 
					that._inputs.get(i)._ID);
				if( comp != 0 )
					return comp;
			}
			return 0;
		}
		
		@Override
		public String toString() {
			StringBuilder inputs = new StringBuilder();
			for(NodeLink in : _inputs) {
				if( inputs.length() > 0 )
					inputs.append(",");
				inputs.append(in._ID);
			}
			return _ID+" ("+inputs.toString()+") "+((_p!=null)?_p:"null");
		}
		
		private String explain(HashSet<Long> visited) {
			if( visited.contains(_ID) )
				return "";
			//add children
			StringBuilder sb = new StringBuilder();
			StringBuilder inputs = new StringBuilder();
			for(NodeLink in : _inputs) {
				String tmp = in.explain(visited);
				if( !tmp.isEmpty() )
					sb.append(tmp + "\n");
				if( inputs.length() > 0 )
					inputs.append(",");
				inputs.append(in._ID);
			}
			//add node itself
			sb.append(_ID+" ("+inputs+") "+((_p!=null)?_p:"null"));
			visited.add(_ID);
			
			return sb.toString();
		}
	}
}
