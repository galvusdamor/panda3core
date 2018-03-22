package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;

public class ShrinkTable extends CascadingTable{

    public int indexOfTableBeforeShrinking;
    HashMap<Integer, Integer> nodeIDToNewNodeIDMapping;

    public ShrinkTable(int index, int indexOfTableBeforeShrinking, HashMap<Integer, Integer> nodeIDToNewNodeIDMapping){

        this.index = index;
        this.indexOfTableBeforeShrinking = indexOfTableBeforeShrinking;
        this.nodeIDToNewNodeIDMapping = nodeIDToNewNodeIDMapping;


    }

    public int getNewNodeID(int[] state, HashMap<Integer, Integer> resultsFromFormerOperations){

        int oldNodeID = resultsFromFormerOperations.get(indexOfTableBeforeShrinking);

        int respectiveNodeID = nodeIDToNewNodeIDMapping.get(oldNodeID);

        resultsFromFormerOperations.remove(indexOfTableBeforeShrinking);
        resultsFromFormerOperations.put(index, respectiveNodeID);

        return respectiveNodeID;

    }

}