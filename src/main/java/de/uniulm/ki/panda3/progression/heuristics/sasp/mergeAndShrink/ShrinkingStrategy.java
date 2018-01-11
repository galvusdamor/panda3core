package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ShrinkNode;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import javax.rmi.CORBA.Util;
import java.util.*;

import static de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMergeAndShrink.*;

/**
 * Created by biederma on 11.01.2018.
 */
abstract class ShrinkingStrategy {


    abstract EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrink(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph);


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrinkingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, ArrayList<ArrayList<Integer>> aggregatedIDs) {


        CascadingTables cascadingTables = graph.cascadingTables();
        int oldIndex = cascadingTables.cascadingTables.size() -1;

        HashMap<Integer, NodeValue> newIDMapping = new HashMap<>();

        ArrayList<Integer> indexesToReplace = new ArrayList<>();

        for (ArrayList<Integer> indexes : aggregatedIDs){

            indexesToReplace.addAll(indexes);

        }

        HashMap<Integer, Integer> tempReverseIDMapping  = new HashMap<>();

        Tuple3<Integer, Integer, Integer>[] oldEdges = graph.labelledEdges();



        int index =0;

        Integer[] nodes = (Integer[]) graph.arrayVertices();

        for (int id: nodes){

            if (!indexesToReplace.contains(id)){

                newIDMapping.put(index, graph.idMapping().get(id));
                tempReverseIDMapping.put(id, index);
                index++;

            }

        }

        for (ArrayList<Integer> toAggregate : aggregatedIDs){

            NodeValue newNodeValue = graph.idMapping().get(toAggregate.get(0));

            tempReverseIDMapping.put(toAggregate.get(0), index);


            for (int j=1; j<toAggregate.size(); j++) {

                newNodeValue = new ShrinkNode(newNodeValue, graph.idMapping().get(toAggregate.get(j)), p);
                tempReverseIDMapping.put(toAggregate.get(j), index);

            }

            newIDMapping.put(index, newNodeValue);
            index++;

        }

        Tuple3<Integer, Integer, Integer>[] newEdges = shrinkEdges(oldEdges, tempReverseIDMapping);

        int newStartID = tempReverseIDMapping.get(graph.startNodeID());



        Set<Integer> usedFactIndexes = new HashSet<>(graph.usedFactIndexes());

        Set<Integer> usedVariables = new HashSet<>(graph.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph.notYetUsedVariables());


        cascadingTables.addNewShrinkTable(oldIndex, tempReverseIDMapping);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(newIDMapping), newEdges, newIDMapping, newStartID, usedFactIndexes, usedVariables, notYetUsedVariables, graph.allVariables(), cascadingTables);

        return newGraph;
    }

    public Tuple3<Integer, Integer, Integer>[] shrinkEdges(Tuple3<Integer, Integer, Integer>[] oldEdges, HashMap<Integer, Integer> tempReverseIDMapping){

        ArrayList<Tuple3<Integer, Integer, Integer>> shrinkedEdges = new ArrayList();

        for (Tuple3<Integer, Integer, Integer> edge : oldEdges){

            Tuple3<Integer, Integer, Integer> shrinkedEdge = new Tuple3<>(tempReverseIDMapping.get(edge._1()), edge._2(), tempReverseIDMapping.get(edge._3()));
            if (!shrinkedEdges.contains(shrinkedEdge)) shrinkedEdges.add(shrinkedEdge);
        }


        return Utils.convertEdgeArrayListToTuple3(shrinkedEdges);
    }


    public HashMap<Integer,Integer> getDistancesFromGoal(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashMap<Integer, Integer> distancesToGoalMap = new HashMap<>();

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> incomingEdgesMap = getIDToIncomingEdgesMap(graph);


        ArrayList<Integer> nextNodes = new ArrayList<>();




        Set<Integer> goalNodes = getGoalNodes(p, graph);


        for (int i: goalNodes){
            distancesToGoalMap.put(i,0);
            nextNodes.add(i);
        }


        distancesToGoalMap = reverseBreadthSearchToFindDistances(p, nextNodes, incomingEdgesMap, distancesToGoalMap);



        return distancesToGoalMap;

    }



    public HashMap<Integer, Integer> reverseBreadthSearchToFindDistances(SasPlusProblem p, ArrayList<Integer> nextNodes,
                                                                         HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> incomingEdgesMap, HashMap<Integer, Integer> distancesFromGoal){

        ArrayList<Integer> newNextNodes = new ArrayList<>(nextNodes);

        if (nextNodes.size()>0) {

            int nextNode = nextNodes.get(0);

            int distanceOfNextNodeFromGoalNode = distancesFromGoal.get(nextNode);

            ArrayList<Tuple3<Integer, Integer, Integer>> incomingEdges = incomingEdgesMap.get(nextNode);
            newNextNodes.remove(0);
            for (Tuple3<Integer, Integer, Integer> incomingEdge : incomingEdges){
                int startID = incomingEdge._1();
                int nextDistance = distanceOfNextNodeFromGoalNode + p.costs[incomingEdge._2()];
                if (!distancesFromGoal.keySet().contains(startID) || (nextDistance<distancesFromGoal.get(startID))){
                    newNextNodes.add(startID);

                    //distanceOfNextNodeFromGoalNode + 1;
                    distancesFromGoal.put(startID, nextDistance);
                }
            }

            return reverseBreadthSearchToFindDistances(p, newNextNodes, incomingEdgesMap, distancesFromGoal);

        }

        return distancesFromGoal;

    }


}







class ShrinkingStrategy1 extends ShrinkingStrategy{



    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrink(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){



        HashMap<Integer,Integer> distancesFromClosestGoalNode = getDistancesFromGoal(p, graph);

        HashMap<Integer,Integer> distancesFromStartNode = getDistancesFromStart(p, graph);


        HashMap<Integer,Integer> summedDistances = new HashMap<>();

        for (int id:graph.idMapping().keySet()){

            int distanceFromStart = distancesFromStartNode.get(id);
            int distanceFromGoal = distancesFromClosestGoalNode.get(id);
            summedDistances.put(id,distanceFromStart+distanceFromGoal);
        }

        //System.out.println(summedDistances);

        ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, summedDistances);

        ArrayList<ArrayList<Integer>> nodesToShrink = new ArrayList<>();

        nodesToShrink.add(farthestNodesFromStartAndGoal);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrinkedGraph = shrinkingStep(p,graph, nodesToShrink);


        return shrinkedGraph;

    }





}


class ShrinkingStrategy2 extends ShrinkingStrategy {


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrink(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph) {


        HashMap<Integer,Integer> distancesFromClosestGoalNode = getDistancesFromGoal(p, graph);

        HashMap<Integer,Integer> distancesFromStartNode = getDistancesFromStart(p, graph);


        HashMap<Integer,Integer> summedDistances = new HashMap<>();

        for (int id:graph.idMapping().keySet()){

            int distanceFromStart = distancesFromStartNode.get(id);
            int distanceFromGoal = distancesFromClosestGoalNode.get(id);
            summedDistances.put(id,distanceFromStart+distanceFromGoal);
        }

        //System.out.println(summedDistances);

        ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, summedDistances);

        ArrayList<ArrayList<Integer>> nodesToShrink = new ArrayList<>();

        nodesToShrink.add(farthestNodesFromStartAndGoal);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrinkedGraph = shrinkingStep(p,graph, nodesToShrink);


        return shrinkedGraph;
    }
}