package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import com.sun.applet2.AppletParameters;
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ElementaryNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.MergeNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ShrinkNode;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;

import scala.Tuple2;
import scala.Tuple3;


import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;


public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {



        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> testGraph = mergeAndShrinkProcess(p, 20);

        Utils.printMultiGraph(p, testGraph, "graph6.pdf");



        int[] startstate = p.s0List;

        int[] testState = new  int[4];
        testState[0] = 1;
        testState[1] = 3;
        testState[2] = 7;
        testState[3] = 8;


        int[] queryState = testState;


        System.out.println("State: ");
        for (int i=0; i<queryState.length; i++) {
            System.out.print(queryState[i] + " ");
        }

        System.out.println();

/*        CascadingTable table = testGraph.cascadingTables().cascadingTables.get(2);
        MergeTable mergeTable = (MergeTable) table;
        System.out.println(table instanceof MergeTable);

        for (int i=0; i<mergeTable.mergeTable.length; i++){
            for (int j=0; j<mergeTable.mergeTable.length; j++){
                System.out.print(mergeTable.mergeTable[i][j]);
            }
            System.out.println();
        }*/


        //System.out.println(testGraph.cascadingTables().getNodeID(p.s0List));
        System.out.println("Node ID: " + testGraph.cascadingTables().getNodeID(queryState));







        System.exit(0);






    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            MergingStrategy mergingStrategy = new MergingStrategy1();

            graph1 = mergingStrategy.mergeWithVar(p, graph1, i, 1000000000, new ShrinkingStrategy1());

        }

        return graph1;

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


    public static HashMap<Integer, Integer> getDistancesFromStart(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        int startNodeID = graph.startNodeID();

        ArrayList<Integer> nodesToKeep = new ArrayList<>();

        ArrayList<Integer> nextNodes = new ArrayList<>();

        nextNodes.add(startNodeID);

        nodesToKeep.add(startNodeID);

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = getIDToOutgoingEdgesMap(graph);

        HashMap<Integer, Integer> distancesFromStartMap = new HashMap<>();

        distancesFromStartMap.put(graph.startNodeID(), 0);

        Tuple2<ArrayList<Integer>,HashMap<Integer, Integer>> result = breadthSearchToFindDistances(p, nextNodes, nodesToKeep, outgoingEdgesMap, distancesFromStartMap);

        distancesFromStartMap = result._2();

        return distancesFromStartMap;
    }


    public HashMap<Integer, NodeValue> dismissIDMappingOfNotReachableNodeIDs(HashMap<Integer, NodeValue> oldIDMapping, ArrayList<Integer> nodesToDismiss){

        HashMap<Integer,NodeValue> newIDMapping = new HashMap<>();

        for(int id : oldIDMapping.keySet()){
            if (!nodesToDismiss.contains(id)){
                newIDMapping.put(id, oldIDMapping.get(id));
            }
        }

        return newIDMapping;
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

        return new Tuple2<ArrayList<Integer>,HashMap<Integer, Integer>>(reachedNodes, distancesFromStart);

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




    public static Set<Integer> getGoalNodes(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashSet<Integer> goalNodes = new HashSet<>();

        for (int id : graph.idMapping().keySet()){

            NodeValue nodeValue = graph.idMapping().get(id);
            if (nodeValue.isGoalNode()) goalNodes.add(id);

        }


        return goalNodes;


    }


/*    public static Set<Integer> getGoalNodes(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        Set<Integer> goalNodeIDs = new HashSet<>();

        int[] goals = p.gList;

        ArrayList<Integer> factIndexes = new ArrayList<>(graph.usedFactIndexes());

        ArrayList<Integer> dismissedGoals = Utils.dismissNotContainedIndexes(goals, factIndexes);

        if (dismissedGoals.size()==0){

            goalNodeIDs.addAll(graph.idMapping().keySet());

        }else{

            for (int id : graph.idMapping().keySet()){

                NodeValue nodeValue = graph.idMapping().get(id);

                if (satisfiesAllGoals(nodeValue, dismissedGoals)){

                    goalNodeIDs.add(id);

                }
            }

        }

        return goalNodeIDs;

    }

    public static boolean satisfiesAllGoals(NodeValue nodeValue, ArrayList<Integer> goals){

        Set<Integer> goalSet = new HashSet<>(goals);

        Set<Integer> notSatisfiedGoals = satisfiesGoals(nodeValue, goalSet);

        if (notSatisfiedGoals.size()==0){
            return true;
        }else return false;

    }


    //returned goal indexes are not satisfied
    public static Set<Integer> satisfiesGoals(NodeValue nodeValue, Set<Integer> notYesSatisfiedGoals){

        if (nodeValue instanceof ElementaryNode){

            Set<Integer> newGoals = new HashSet<>(notYesSatisfiedGoals);

            newGoals.remove(((ElementaryNode) nodeValue).value());

            return newGoals;

        }

        else if (nodeValue instanceof MergeNode){

            MergeNode mergeNode = (MergeNode) nodeValue;

            if (!mergeNode.containsShrink()){

                Set<Integer> newNotYesSatisfiedGoals = new HashSet<>();

                for (Integer goalFactID : notYesSatisfiedGoals){
                    if (!nodeValue.containsFactIndexes().contains(goalFactID)){
                        newNotYesSatisfiedGoals.add(goalFactID);
                    }
                }

                return newNotYesSatisfiedGoals;

            }else {

                Set<Integer> leftNotYetSatisfiedGoals = satisfiesGoals(mergeNode.left(), notYesSatisfiedGoals);

                if (leftNotYetSatisfiedGoals.size() == 0) {
                    return leftNotYetSatisfiedGoals;
                } else {
                    return satisfiesGoals(mergeNode.right(), leftNotYetSatisfiedGoals);
                }
            }

        }else if (nodeValue instanceof ShrinkNode){

            ShrinkNode mergeNode = (ShrinkNode) nodeValue;

            Set<Integer> leftNotYetSatisfiedGoals = satisfiesGoals(mergeNode.left(), notYesSatisfiedGoals);

            if (leftNotYetSatisfiedGoals.size()==0){
                return leftNotYetSatisfiedGoals;
            }else {
                return satisfiesGoals(mergeNode.right(), notYesSatisfiedGoals);
            }

        }

        return null;


    }
    */


/*    public HashMap<Integer, Integer> getDistancesFromGoal(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        int startNodeID = graph.startNodeID();

        ArrayList<Integer> nodesToKeep = new ArrayList<>();

        ArrayList<Integer> nextNodes = new ArrayList<>();

        nextNodes.add(startNodeID);

        nodesToKeep.add(startNodeID);

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = getIDToIncomingEdgesMap(graph);

        HashMap<Integer, Integer> distancesFromStartMap = new HashMap<>();

        distancesFromStartMap.put(graph.startNodeID(), 0);

        Tuple2<ArrayList<Integer>,HashMap<Integer, Integer>> result = reverseBreadthSearchToFindDistances(p, nextNodes, outgoingEdgesMap, distancesFromStartMap);

        distancesFromStartMap = result._2();

        return distancesFromStartMap;
    }*/

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

    //to find distances from the goal nodes
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




    public static ArrayList<Integer> getNodesFarthestFromStartAndGoal(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, HashMap<Integer,Integer> summedDistances){

        ArrayList<Integer> farthestNodes = new ArrayList<>();

        int maximumDistance = Collections.max(summedDistances.values());

        int counter = Collections.frequency(summedDistances.values(),maximumDistance);

        //System.out.println("Maximum: " + maximumDistance + ", Frequency: " + counter);

        if (counter>1) {
            for (int id : graph.idMapping().keySet()) {
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


            for (int id : graph.idMapping().keySet()) {
                if (summedDistances.get(id) == maximumDistance) {
                    farthestNodes.add(id);
                    summedDistancesWithoutHighest.remove(id);
                    break;
                }
            }


            //System.out.println(summedDistancesWithoutHighest);


            int secondMaximumDistance = Collections.max(summedDistancesWithoutHighest.values());

            //System.out.println("Second Maximum: " + secondMaximumDistance);

            for (int id : graph.idMapping().keySet()) {
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










    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> mergeAndShrinkProcess(SasPlusProblem p, int shrinkingBound){


        MergingStrategy mergingStrategy = new MergingStrategy1();

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph = mergingStrategy.merge(p, null, shrinkingBound, 0);

        int counter = 0;

        while (graph.notYetUsedVariables().size()!=0){


            /*while (graph.idMapping().keySet().size()>shrinkingBound){
                graph = shrinkingStrategy1(p, graph);
            }*/

            graph = mergingStrategy.merge(p, graph,shrinkingBound, 0);

            counter++;

            String name = "testGraph" + counter + ".pdf";

            Utils.printMultiGraph(p, graph, name);
        }

        /*while (graph.idMapping().keySet().size()>shrinkingBound){
            graph = shrinkingStrategy1(p, graph);
        }*/


        return graph;

    }





}
