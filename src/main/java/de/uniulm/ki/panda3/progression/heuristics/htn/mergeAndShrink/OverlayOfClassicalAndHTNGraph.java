package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMSGraph;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.MergingStrategy;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

public final class OverlayOfClassicalAndHTNGraph {


    public static ClassicalMSGraph findWaysThroughBothGraphs(SasPlusProblem p, ClassicalMSGraph classicalMSGraph, HtnMsGraph htnMsGraph){


        //System.out.println("Overlay starts now");

        HashMap<Integer, NodeCombination> nodeAssignments = new HashMap<>();
        LinkedList<Tuple2<Integer,Integer>> nextQueue = new LinkedList<>();

        NodeCombination startNodes = new NodeCombination(classicalMSGraph.startNodeID ,htnMsGraph.startNodeID);
        nodeAssignments.put(startNodes.classicalNodeID,startNodes);

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesOfClassicalGraph = MergingStrategy.getIDToOutgoingEdgesMap(classicalMSGraph);
        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesOfHtnGraph = HtnMerging.getIDToOutgoingEdgesMap(htnMsGraph);

        HashSet<Tuple3<Integer, Integer, Integer>> newEdges = new HashSet<>();
        HashSet<Tuple2<Integer,Integer>> alreadyVisitedCombinations = new HashSet<>();
        HashSet<Tuple2<Integer,Integer>> alreadyQueuedCombinations = new HashSet<>();


        //neuen, leeren Graphen erstellen


        Tuple2<Integer,Integer> startCombination = new Tuple2<>(classicalMSGraph.startNodeID ,htnMsGraph.startNodeID);
        nextQueue.add(startCombination);


        while (nextQueue.size()>0){

            Tuple2<Integer,Integer> next = nextQueue.poll();
            alreadyQueuedCombinations.add(next);

            //System.out.println("At Node " + next._1 + ", " + next._2);

            ArrayList<Tuple3<Integer, Integer, Integer>> classicalOutgoingEdges = edgesOfClassicalGraph.get(next._1);


            ArrayList<Tuple3<Integer, Integer, Integer>> htnOutgoingEdges = edgesOfHtnGraph.get(next._2);




            for (Tuple3<Integer, Integer, Integer> classicalEdge : classicalOutgoingEdges){

                int taskID = classicalEdge._2();

                for(Tuple3<Integer, Integer, Integer> htnEdge : htnOutgoingEdges){

                    if (taskID==htnEdge._2()){

                        //System.out.println("Shared Edge:\n" + "classical: " + classicalEdge + "\nHTN: " + htnEdge);

                        newEdges.add(classicalEdge);

                        int classicalEndNode = classicalEdge._3();
                        int htnEndeNode = htnEdge._3();

                        Tuple2<Integer, Integer> endCombination = new Tuple2<>(classicalEndNode,htnEndeNode);

                        if(!alreadyQueuedCombinations.contains(endCombination)){



                            if (nodeAssignments.containsKey(classicalEndNode)){

                                nodeAssignments.get(classicalEndNode).addHtnNodeID(htnEndeNode);

                            }else {
                                NodeCombination nextNode = new NodeCombination(classicalEndNode, htnEndeNode);
                                nodeAssignments.put(classicalEndNode, nextNode);
                            }

                            nextQueue.addLast(endCombination);
                            alreadyQueuedCombinations.add(endCombination);

                        }

                    }

                }

            }

        }

        HashMap<Integer,NodeValue> newIDMapping = new HashMap<>();

        /*for(int nodeID:nodeAssignments.keySet()){
            newIDMapping.put(nodeID, classicalMSGraph.idMapping.get(nodeID));

        }*/

        HashMap<Integer, Integer> shrinkingTableInfo = new HashMap<>();

        int index=0;
        for (int nodeID : classicalMSGraph.idMapping.keySet()){


            if (nodeAssignments.keySet().contains(nodeID)){

                newIDMapping.put(index, classicalMSGraph.idMapping.get(nodeID));
                shrinkingTableInfo.put(nodeID, index);
                index++;

            }else {

                shrinkingTableInfo.put(nodeID, -1);

            }
        }


        ArrayList<Tuple3<Integer, Integer, Integer>> newEdgesArraylist = new ArrayList<>();
        newEdgesArraylist.addAll(newEdges);

        Tuple3<Integer, Integer, Integer>[] newEdges2 = Utils.convertEdgeArrayListToTuple3(newEdgesArraylist);


        Tuple3<Integer, Integer, Integer>[] newEdgeTuple = Shrinking.shrinkEdges(newEdges2, shrinkingTableInfo);

        int newStartID = shrinkingTableInfo.get(classicalMSGraph.startNodeID);

        int oldIndex = classicalMSGraph.cascadingTables.cascadingTables.size() -1;

        classicalMSGraph.cascadingTables.addNewShrinkTable(oldIndex,shrinkingTableInfo);

        Integer[] nodeIDs = Utils.convertNodeIDArrayListToArray(newIDMapping);

        //ArrayList<Tuple3<Integer, Integer, Integer>> edgeArrayList = new ArrayList<>();
        //edgeArrayList.addAll(newEdges);

        //Tuple3<Integer, Integer, Integer>[] edgeTuple = Utils.convertEdgeArrayListToTuple3(edgeArrayList);


        ClassicalMSGraph combinedGraph = new ClassicalMSGraph(nodeIDs, newEdgeTuple, newIDMapping, newStartID,
                classicalMSGraph.usedFactIndexes, classicalMSGraph.usedVariables, classicalMSGraph.notYetUsedVariables, classicalMSGraph.goalVariables, classicalMSGraph.allVariables, classicalMSGraph.cascadingTables);



        return combinedGraph;

    }

}



class NodeCombination {


    int classicalNodeID;
    Set<Integer> hTNNodeIDs;

    public NodeCombination(int classicalNodeID, int hTNNodeID){

        this.classicalNodeID =classicalNodeID;

        hTNNodeIDs = new HashSet<>();
        hTNNodeIDs.add(hTNNodeID);

    }


    public void addHtnNodeID(int hTNNodeID){

        hTNNodeIDs.add(hTNNodeID);

    }



}
