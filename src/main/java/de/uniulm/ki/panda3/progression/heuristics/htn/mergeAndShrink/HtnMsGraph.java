package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTable;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class HtnMsGraph{

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph;

    public HashMap<Integer, NodeValue> idMapping;

    public int startNodeID;
    public Integer[] arrayVertices;
    public Tuple3<Integer, Integer, Integer>[] labelledEdges;

    public CascadingTables cascadingTables;


    public int getNodeIDFromActionSequence(int actualNodeID, LinkedList<Integer> actionSequence){


        HashMap<Integer,ArrayList<Tuple3<Integer,Integer,Integer>>> outgoingEdges = HtnShrinkingStrategy.getIDToOutgoingEdgesMap(this);

        if(actionSequence.size()==0) return actualNodeID;

        ArrayList<Tuple3<Integer,Integer,Integer>> outgoingEdgesFromNode = outgoingEdges.get(actualNodeID);


        LinkedList<Tuple3<Integer,Integer,Integer>> outgoingEdgesFromNode2 = new LinkedList<>();

        for (Tuple3<Integer,Integer,Integer> edge : outgoingEdgesFromNode){

            if (edge._1()==edge._3()){
                outgoingEdgesFromNode2.addLast(edge);
            }else {
                outgoingEdgesFromNode2.addFirst(edge);
            }

        }

        /*System.out.println("Outgoing Edges from Node " + actualNodeID + ": ");

        for(Tuple3<Integer,Integer,Integer> edge : outgoingEdgesFromNode){
            System.out.println(edge);
        }*/

        LinkedList<Integer> nextActionSequence = new LinkedList<>();

        nextActionSequence.addAll(actionSequence);
        nextActionSequence.removeFirst();

        for (Tuple3<Integer,Integer,Integer> outgoingEdge : outgoingEdgesFromNode){

            if (outgoingEdge._2()==actionSequence.getFirst()){

                int matchingIndex = getNodeIDFromActionSequence(outgoingEdge._3(), nextActionSequence);
                if(matchingIndex!=-1){
                    return matchingIndex;
                }


            }

        }


        return -1;
    }


    public boolean containsGoalNode(){

        for (NodeValue nodeValue: idMapping.values()){

            if(nodeValue.isGoalNode()) return true;
        }

        return false;
    }
}