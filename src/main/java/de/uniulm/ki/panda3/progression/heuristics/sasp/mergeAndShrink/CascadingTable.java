package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.HashMap;

abstract public class CascadingTable{

    public int index;

    abstract int getNewNodeID(int[] state, HashMap<Integer, Integer> resultsFromFormerOperations);

}







