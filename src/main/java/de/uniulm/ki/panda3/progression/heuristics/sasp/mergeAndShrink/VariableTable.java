package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;

public class VariableTable extends CascadingTable{

    public int varIndex;

    private HashMap<Integer, Integer> varValueToNodeIDMapping;

    public VariableTable(int index, int varIndex, HashMap<Integer, Integer> varValueToNodeIDMapping){

        this.index = index;
        this.varIndex = varIndex;
        this.varValueToNodeIDMapping = varValueToNodeIDMapping;

    }

    public int getNewNodeID(int[] state, HashMap<Integer, Integer> resultsFromFormerOperations){

        int variableValue = state[varIndex];

        int respectiveNodeID = varValueToNodeIDMapping.get(variableValue);
        resultsFromFormerOperations.put(index, respectiveNodeID);

        return respectiveNodeID;
    }
}