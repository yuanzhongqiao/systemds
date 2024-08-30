# -------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# -------------------------------------------------------------

# Autogenerated By   : src/main/python/generator/generator.py
# Autogenerated From : scripts/builtin/confusionMatrix.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def confusionMatrix(P: Matrix,
                    Y: Matrix):
    """
     Computes the confusion matrix for input vectors of predictions
     and actual labels. We return both the counts and relative frequency
     (normalized by sum of true labels)
    
     .. code-block::
    
                       True Labels
                         1    2
                     1   TP | FP
       Predictions      ----+----
                     2   FN | TN
    
    
    
    :param P: vector of predictions (1-based, recoded)
    :param Y: vector of actual labels (1-based, recoded)
    :return: the confusion matrix as absolute counts
    :return: the confusion matrix as relative frequencies
    """

    params_dict = {'P': P, 'Y': Y}
    
    vX_0 = Matrix(P.sds_context, '')
    vX_1 = Matrix(P.sds_context, '')
    output_nodes = [vX_0, vX_1, ]

    op = MultiReturn(P.sds_context, 'confusionMatrix', output_nodes, named_input_nodes=params_dict)

    vX_0._unnamed_input_nodes = [op]
    vX_1._unnamed_input_nodes = [op]

    return op
