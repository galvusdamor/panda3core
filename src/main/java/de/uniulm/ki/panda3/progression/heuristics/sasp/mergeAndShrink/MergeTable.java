package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;

public class MergeTable extends CascadingTable{

    public int mergeIndex1;
    public int mergeIndex2;
    public int[][] mergeTable;

    public MergeTable(int index, int mergeIndex1, int mergeIndex2, int[][] mergeTable){

        this.index = index;
        this.mergeIndex1 = mergeIndex1;
        this.mergeIndex2 = mergeIndex2;
        this.mergeTable = mergeTable;

    }


    public int getNewNodeID(int[] state, HashMap<Integer, Integer> resultsFromFormerOperations){


        int oldNodeID1 = resultsFromFormerOperations.get(mergeIndex1);
        int oldNodeID2 = resultsFromFormerOperations.get(mergeIndex2);

        int respectiveNodeID = mergeTable[oldNodeID1][oldNodeID2];

        resultsFromFormerOperations.remove(mergeIndex1);
        resultsFromFormerOperations.remove(mergeIndex2);
        resultsFromFormerOperations.put(index, respectiveNodeID);

        return respectiveNodeID;
    }


}
