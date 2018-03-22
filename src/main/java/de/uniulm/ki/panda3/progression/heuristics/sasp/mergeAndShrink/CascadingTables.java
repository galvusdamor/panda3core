package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andrea on 13.12.2017.
 */
public class CascadingTables {


    public LinkedList<CascadingTable> cascadingTables;

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


