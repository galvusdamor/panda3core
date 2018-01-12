package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ShrinkNode;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

//import static de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMergeAndShrink.*;

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

        Set<Tuple3<Integer, Integer, Integer>> shrinkedEdges = new HashSet();

        for (Tuple3<Integer, Integer, Integer> edge : oldEdges){

            Tuple3<Integer, Integer, Integer> shrinkedEdge = new Tuple3<>(tempReverseIDMapping.get(edge._1()), edge._2(), tempReverseIDMapping.get(edge._3()));
            if (!shrinkedEdges.contains(shrinkedEdge)) shrinkedEdges.add(shrinkedEdge);
        }


        ArrayList<Tuple3<Integer, Integer, Integer>> finalLists = new ArrayList<>();
        finalLists.addAll(shrinkedEdges);
        return Utils.convertEdgeArrayListToTuple3(finalLists);
    }


    public static HashMap<Integer,Integer> getDistancesFromGoal(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>,
            Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashMap<Integer, Integer> distancesToGoalMap = new HashMap<>();

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> incomingEdgesMap = getIDToIncomingEdgesMap(graph);


        ArrayList<Integer> nextNodes = new ArrayList<>();




        Set<Integer> goalNodes = getGoalNodes(graph);

        for (int i: graph.idMapping().keySet())
            distancesToGoalMap.put(i,Integer.MAX_VALUE);


        for (int i: goalNodes){
            distancesToGoalMap.put(i,0);
            nextNodes.add(i);
        }


        distancesToGoalMap = reverseBreadthSearchToFindDistances(p, nextNodes, incomingEdgesMap, distancesToGoalMap);



        return distancesToGoalMap;

    }


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToIncomingEdgesMap(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = new HashMap<>();

        for (int i: graph.idMapping().keySet()){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            outgoingEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges()){

            outgoingEdgesMap.get(edge._3()).add(edge);

        }

        return outgoingEdgesMap;

    }

    public static Set<Integer> getGoalNodes(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashSet<Integer> goalNodes = new HashSet<>();

        for (int id : graph.idMapping().keySet()){

            NodeValue nodeValue = graph.idMapping().get(id);
            if (nodeValue.isGoalNode()) goalNodes.add(id);

        }


        return goalNodes;


    }



    public static HashMap<Integer, Integer> reverseBreadthSearchToFindDistances(SasPlusProblem p, ArrayList<Integer> nextNodes,
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


    public static ArrayList<Integer> getNodesFarthestFromStartAndGoal(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, HashMap<Integer,Integer> summedDistances){

        ArrayList<Integer> farthestNodes = new ArrayList<>();

        int maximumDistance = Collections.max(summedDistances.values());

        int counter = Collections.frequency(summedDistances.values(),maximumDistance);

        //System.out.println("Maximum: " + maximumDistance + ", Frequency: " + counter);

        if (true || counter>1) {
            for (int id : summedDistances.keySet()) {
                if (summedDistances.get(id) == maximumDistance) {
                    farthestNodes.add(id);
                }
            }
        }else{

           /* ArrayList<Integer> summedDistancesWithoutHighest = new ArrayList<Integer>();
            summedDistancesWithoutHighest.addAll(summedDistances.values());
            System.out.println(summedDistancesWithoutHighest);
            summedDistancesWithoutHighest.remove(maximumDistance);
            System.out.println(summedDistancesWithoutHighest);*/


            HashMap<Integer,Integer> summedDistancesWithoutHighest = new HashMap<>();

            summedDistancesWithoutHighest.putAll(summedDistances);

            //System.out.println(summedDistances);
            //System.out.println(summedDistancesWithoutHighest);


            for (int id : summedDistances.keySet()) {
                if (summedDistances.get(id) == maximumDistance) {
                    farthestNodes.add(id);
                    summedDistancesWithoutHighest.remove(id);
                    break;
                }
            }


            //System.out.println(summedDistancesWithoutHighest);


            int secondMaximumDistance = Collections.max(summedDistancesWithoutHighest.values());

            //System.out.println("Second Maximum: " + secondMaximumDistance);

            for (int id : summedDistances.keySet()) {
                if (summedDistances.get(id) == secondMaximumDistance) {
                    farthestNodes.add(id);
                }
            }

        }

        //System.out.println(farthestNodes);

        return farthestNodes;

    }





    public static ArrayList<Integer> getNodesFarthestFromStartAndGoal2(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, HashMap<Integer,Integer> summedDistances){

        ArrayList<Integer> farthestNodes = new ArrayList<>();

        int maximumDistance = Collections.max(summedDistances.values());

        int counter = Collections.frequency(summedDistances.values(),maximumDistance);

        //System.out.println("Maximum: " + maximumDistance + ", Frequency: " + counter);

        while(counter<2){
            /* ArrayList<Integer> summedDistancesWithoutHighest = new ArrayList<Integer>();
            summedDistancesWithoutHighest.addAll(summedDistances.values());
            System.out.println(summedDistancesWithoutHighest);
            summedDistancesWithoutHighest.remove(maximumDistance);
            System.out.println(summedDistancesWithoutHighest);*/


            //System.out.println(summedDistances);
            //System.out.println(summedDistancesWithoutHighest);

            if (summedDistances.size()<1) break;


            for (int id : graph.idMapping().keySet()) {
                if (summedDistances.get(id) == maximumDistance) {
                    //farthestNodes.add(id);
                    summedDistances.remove(id);
                    break;
                }
            }


            //System.out.println(summedDistancesWithoutHighest);


            maximumDistance = Collections.max(summedDistances.values());

            counter = Collections.frequency(summedDistances.values(),maximumDistance);

            //System.out.println("Second Maximum: " + secondMaximumDistance);
        }


        if (counter>1) {
            for (int id : graph.idMapping().keySet()) {
                if (summedDistances.get(id) == maximumDistance) {
                    farthestNodes.add(id);
                }
            }
        }

        //System.out.println(farthestNodes);

        return farthestNodes;

    }

    public static HashMap<Integer, Integer> getDistancesFromStart(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        int startNodeID = graph.startNodeID();

        ArrayList<Integer> nodesToKeep = new ArrayList<>();

        ArrayList<Integer> nextNodes = new ArrayList<>();

        nextNodes.add(startNodeID);

        nodesToKeep.add(startNodeID);

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = getIDToOutgoingEdgesMap(graph);

        HashMap<Integer, Integer> distancesFromStartMap = new HashMap<>();

        for (int i: graph.idMapping().keySet())
            distancesFromStartMap.put(i,Integer.MAX_VALUE);

        distancesFromStartMap.put(graph.startNodeID(), 0);

        Tuple2<ArrayList<Integer>,HashMap<Integer, Integer>> result = breadthSearchToFindDistances(p, nextNodes, nodesToKeep, outgoingEdgesMap, distancesFromStartMap);

        distancesFromStartMap = result._2();

        return distancesFromStartMap;
    }

    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToOutgoingEdgesMap(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = new HashMap<>();

        for (int i: graph.idMapping().keySet()){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            outgoingEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges()){

            outgoingEdgesMap.get(edge._1()).add(edge);

        }

        return outgoingEdgesMap;

    }

    //to find distances from the start node
    public static Tuple2<ArrayList<Integer>,HashMap<Integer, Integer>> breadthSearchToFindDistances(SasPlusProblem p, ArrayList<Integer> nextNodes, ArrayList<Integer> nodesToKeep,
                                                                                                    HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap, HashMap<Integer, Integer> distancesFromStart){

        ArrayList<Integer> newNextNodes = new ArrayList<>(nextNodes);

        ArrayList<Integer> reachedNodes = new ArrayList<>(nodesToKeep);

        if (nextNodes.size()>0) {

            int nextNode = nextNodes.get(0);

            int distanceOfNextNodeFromStartNode = distancesFromStart.get(nextNode);

            ArrayList<Tuple3<Integer, Integer, Integer>> outgoingEdges = outgoingEdgesMap.get(nextNode);
            newNextNodes.remove(0);
            for (Tuple3<Integer, Integer, Integer> outgoingEdge : outgoingEdges){
                int endID = outgoingEdge._3();
                if (!reachedNodes.contains(endID)){
                    reachedNodes.add(endID);
                    newNextNodes.add(endID);
                    int nextDistance = distanceOfNextNodeFromStartNode + p.costs[outgoingEdge._2()];
                    //distanceOfNextNodeFromStartNode + 1;
                    distancesFromStart.put(endID, nextDistance);
                }
            }

            return breadthSearchToFindDistances(p, newNextNodes, reachedNodes, outgoingEdgesMap, distancesFromStart);

        }

        return new Tuple2<>(reachedNodes, distancesFromStart);

    }


}







class ShrinkingStrategy1 extends ShrinkingStrategy{



    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> shrink(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){



        HashMap<Integer,Integer> distancesFromClosestGoalNode = getDistancesFromGoal(p, graph);

        HashMap<Integer,Integer> distancesFromStartNode = getDistancesFromStart(p, graph);


        HashMap<Integer,Integer> summedDistances = new HashMap<>();

        for (int id:graph.idMapping().keySet()){

            //System.out.println("ID: " + id);
            //System.out.println(distancesFromClosestGoalNode);
            //System.out.println(distancesFromStartNode);
            //System.out.println(distancesFromClosestGoalNode.containsKey(id));
            //System.out.println(distancesFromStartNode.containsKey(id));

            int distanceFromStart = distancesFromStartNode.get(id);
            int distanceFromGoal = distancesFromClosestGoalNode.get(id);
            summedDistances.put(id,distanceFromStart+distanceFromGoal);
        }

        //System.out.println(summedDistances);

        HashMap<Integer,Integer> originalSummedDistances = new HashMap<>();
        originalSummedDistances.putAll(summedDistances);


        ArrayList<Integer> currentMaxDistanceNodes;
        while (true) {
            if (summedDistances.isEmpty()){
                int first = -1;
                int second = -1;

                while (true) {
                    if (originalSummedDistances.isEmpty()){
                        currentMaxDistanceNodes = new ArrayList<>();
                        currentMaxDistanceNodes.add(first);
                        currentMaxDistanceNodes.add(second);
                        break;
                    }
                    ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, originalSummedDistances);
                    if (farthestNodesFromStartAndGoal.size() == 1){
                        int maxNode = farthestNodesFromStartAndGoal.get(0);
                        if (first == -1) first = maxNode;
                        else if (second == -1) second = maxNode;
                        originalSummedDistances.remove(maxNode);
                    } else {
                        currentMaxDistanceNodes = farthestNodesFromStartAndGoal;
                        break;
                    }
                }
                break;
            }

            ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, summedDistances);

            int maxDistanceToInit = 0;
            currentMaxDistanceNodes = new ArrayList<Integer>();
            for (int i : farthestNodesFromStartAndGoal) {
                int distToStart = distancesFromStartNode.get(i);
                if (distToStart > maxDistanceToInit) {
                    maxDistanceToInit = distToStart;
                    currentMaxDistanceNodes.clear();
                }

                if (distToStart == maxDistanceToInit)
                    currentMaxDistanceNodes.add(i);
            }

            if (currentMaxDistanceNodes.size() == 1){
                int maxNode = currentMaxDistanceNodes.get(0);
                summedDistances.remove(maxNode);
                distancesFromStartNode.remove(maxNode);
            } else break;
        }


        ArrayList<ArrayList<Integer>> nodesToShrink = new ArrayList<>();
        nodesToShrink.add(currentMaxDistanceNodes);

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