package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import scala.Tuple3;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class NodeIdentificationByMethods {

    public static HashSet<Integer> identifyNodeByListOfProMethods(HtnMsGraphWithMethods graph, LinkedList<ProMethod> usedProMethods){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesOfHtnGraph = HtnMerging.getIDToOutgoingEdgesMap(graph);

        int startNodeID = graph.startNodeID;


        HashSet<Integer> correspondingNodes = correspondingNodes(new HashSet<>(), startNodeID, edgesOfHtnGraph, graph, usedProMethods);


        return correspondingNodes;
    }



    public static HashSet<Integer> correspondingNodes(HashSet<Integer> alreadyCorrespondingNodes, int actualNode, HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesOfHtnGraph,
                                               HtnMsGraphWithMethods graph, LinkedList<ProMethod> usedProMethods){

        ArrayList<Tuple3<Integer, Integer, Integer>> outgoingEdgesFromActualNode = edgesOfHtnGraph.get(actualNode);

        HashMap<Tuple3<Integer, Integer, Integer>, LinkedList<ProMethod>> linkedMethods = graph.linkedMethods;


        for (Tuple3<Integer, Integer, Integer> edge: outgoingEdgesFromActualNode){

            LinkedList<ProMethod> linkedProMethodsForEdge = linkedMethods.get(edge);

            if (linkedProMethodsForEdge.size()<=usedProMethods.size()){

                LinkedList<ProMethod> stillToFulfill = new LinkedList<>();
                stillToFulfill.addAll(usedProMethods);

                for (ProMethod linkedMethodForEdge : linkedProMethodsForEdge){

                    if (stillToFulfill.contains(linkedMethodForEdge)){

                        stillToFulfill.remove(linkedMethodForEdge);

                    }else {
                        break;
                    }

                    if (stillToFulfill.size()>0){

                    }else {

                        alreadyCorrespondingNodes.add(edge._3());

                        return alreadyCorrespondingNodes;
                    }

                }

            }

        }

        return alreadyCorrespondingNodes;

    }


}
