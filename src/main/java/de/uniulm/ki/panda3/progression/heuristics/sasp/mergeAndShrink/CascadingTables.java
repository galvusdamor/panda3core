package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andrea on 13.12.2017.
 */
public class CascadingTables {


    LinkedList<CascadingTable> cascadingTables;

    public CascadingTables(){

        cascadingTables = new LinkedList<>();
    }

    public void addNewVariableTable(int varIndex, HashMap<Integer, Integer> varValueToNodeIDMapping){

        VariableTable table = new VariableTable(cascadingTables.size(), varIndex, varValueToNodeIDMapping);
        cascadingTables.add(table);

    }

    public void addNewShrinkTable(int indexOfTableBeforeShrinking, HashMap<Integer, Integer> nodeIDToNewNodeIDMapping){

        ShrinkTable table = new ShrinkTable(cascadingTables.size(), indexOfTableBeforeShrinking, nodeIDToNewNodeIDMapping);
        cascadingTables.add(table);

    }

    public void addNewMergeTable(int mergeIndex1, int mergeIndex2, int[][] mergeTable){

        MergeTable table = new MergeTable(cascadingTables.size(), mergeIndex1, mergeIndex2, mergeTable);
        cascadingTables.add(table);

    }




    public int getNodeID(int[] state){

        HashMap<Integer, Integer> resultsFromFormerOperations = new HashMap<>();

        int result = -1;




        for ( Iterator<CascadingTable> i = cascadingTables.iterator(); i.hasNext(); )
        {
            CascadingTable table = i.next();
            try {
                result = table.getNewNodeID(state, resultsFromFormerOperations);

            }catch (Exception e) {
                result = -1;

                break;
            }

        }
/*        for (int i=0; i<cascadingTables.size(); i++){

            CascadingTable table = cascadingTables.get(i);
            result = table.getNewNodeID(state, resultsFromFormerOperations);
            if (result == -1) break;

        }*/

        return result;

    }


}


abstract class CascadingTable{

    int index;

    abstract int getNewNodeID(int[] state, HashMap<Integer, Integer> resultsFromFormerOperations);

}


class VariableTable extends CascadingTable{

    private int varIndex;

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


class MergeTable extends CascadingTable{

    int mergeIndex1;
    int mergeIndex2;
    int[][] mergeTable;

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

class ShrinkTable extends CascadingTable{

    int indexOfTableBeforeShrinking;
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