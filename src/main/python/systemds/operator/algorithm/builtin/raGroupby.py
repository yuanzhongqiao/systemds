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
# Autogenerated From : scripts/builtin/raGroupby.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def raGroupby(X: Matrix,
              col: int,
              method: str):
    """
     This raGroupby-function takes a matrix dataset as input from where it performs
     relational operations : groupby
    
    
    
    :param X: Matrix of input data [shape: N x M]
    :param col: Integer indicating the column index to execute grupby command
    :param method: Groupby implemention method (nested-loop, permutation-matrix)
    :return: Matrix of selected data [shape N' x M] with N' <= N
    """

    params_dict = {'X': X, 'col': col, 'method': method}
    return Matrix(X.sds_context,
        'raGroupby',
        named_input_nodes=params_dict)
