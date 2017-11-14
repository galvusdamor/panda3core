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



        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> testGraph = mergeAndShrinkProcess(p, 15);

        printMultiGraph(p, testGraph, "graph6.pdf");


        System.exit(0);



        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph = getMultiGraphUntilVarID(p, 1);

        graph = shrinkingStrategy1(p, graph);

        graph = mergeWithVar(p, graph,2);

        graph = shrinkingStrategy1(p, graph);

        graph = mergeWithVar(p, graph,3);


        printMultiGraph(p, graph, "graph4.pdf");

        /*HashMap<Integer,Integer> distancesFromStart = getDistancesFromStart(p, graph);

        System.out.println(distancesFromStart);

        HashMap<Integer,Integer> distancesFromClosestGoal = getDistancesFromGoal(p, graph);

        System.out.println(distancesFromClosestGoal);*/

        graph = shrinkingStrategy1(p, graph);


        printMultiGraph(p, graph, "graph5.pdf");


        System.exit(0);

        //printMultiGraph(p, graph, "graph4.pdf");

        ArrayList<ArrayList<Integer>> aggregatedIDs = new ArrayList<>();

        ArrayList<Integer> aggregatedIDs1 = new ArrayList<>();

        aggregatedIDs1.add(2);
        aggregatedIDs1.add(3);
        aggregatedIDs1.add(4);

        aggregatedIDs.add(aggregatedIDs1);


        //graph = shrinkingStep(p, graph, aggregatedIDs);


        graph = mergeWithVar(p, graph,2);

        //graph = shrinkingStep(p, graph, aggregatedIDs);

        graph = mergeWithVar(p, graph,3);

        //graph = mergeWithVar(p, graph,4);

        aggregatedIDs.clear();

        ArrayList<Integer> aggregatedIDs2 = new ArrayList<>();

        aggregatedIDs2.add(10);
        aggregatedIDs2.add(12);

        aggregatedIDs.add(aggregatedIDs2);

        //graph = shrinkingStep(p, graph, aggregatedIDs);

        //HashMap<Integer, Integer> distancesFromStart = getDistancesFromStart(p, graph);

        //System.out.println(distancesFromStart);


        printMultiGraph(p, graph, "graph4.pdf");


        Set<Integer> goalNodes = getGoalNodes(p, graph);

        System.out.println(goalNodes);

        graph = shrinkingStep(p, graph, aggregatedIDs);
        //graph = dismissNotReachableNodes(p, graph);

        printMultiGraph(p, graph, "graph5.pdf");


        //System.out.println("Startknoten: " + graph.startNodeID());


        //HashMap<Integer, ArrayList<Tuple3<Integer,Integer,Integer>>> outgoingEdgesMap = getIDToOutgoingEdgesMap(graph);

        //System.out.println(getOpIDToEdgesMap(p, graph));

        //System.out.println(graph.usedFactIndexes());

/*        for(int id : graph.idMapping().keySet()){

            NodeValue nodeValue = graph.idMapping().get(id);
            System.out.println(id + ": " + nodeValue.containsFactIndexes());
        }*/

        goalNodes = getGoalNodes(p, graph);

        System.out.println(goalNodes);

        HashMap<Integer,Integer> distancesFromStart2 = getDistancesFromStart(p, graph);

        System.out.println(distancesFromStart2);

        HashMap<Integer,Integer> distancesFromClosestGoal2 = getDistancesFromGoal(p, graph);

        System.out.println(distancesFromClosestGoal2);

        shrinkingStrategy1(p,graph);


        System.exit(0);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> singleNodeGraph = SingleGraphMethods.getSingleGraphForVarIndex(p,2);

        EdgeLabelledGraph<String , String, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> multiNodeGraph = convertMultiGraphToStringGraph(p, singleNodeGraph);

        Dot2PdfCompiler.writeDotToFile(multiNodeGraph, "graph3.pdf");



    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            graph1 = mergeWithVar(p, graph1, i);

        }

        return graph1;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> mergeWithVar(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph1, int varIndex){

        if (varIndex >= p.numOfVars){

            System.out.println("Variable " + varIndex + " does not exist.");
            return graph1;
        }


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, varIndex);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newGraph = mergingStep(p, graph1, graph2);

        newGraph = dismissNotReachableNodes(newGraph);

        return newGraph;


    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> mergingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph1, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph2) {

        Integer[] graph1Nodes = (Integer[]) graph1.arrayVertices();

        Tuple3<Integer, Integer, Integer>[] graph1Edges = graph1.labelledEdges();


        Tuple3<Integer, Integer, Integer>[] graph2Edges = graph2.labelledEdges();

        Integer[] graph2Nodes = (Integer[]) graph2.arrayVertices();


        ArrayList<Tuple3<Integer, Integer, Integer>> newMultiEdges = new ArrayList<>();

        HashMap<Integer, Tuple2<Integer, Integer>> tempIdMapping = new HashMap<>();

        HashMap<Tuple2<Integer, Integer>, Integer> tempReverseIdMapping = new HashMap<>();

        List<Integer> a = Arrays.asList(graph1Nodes);

        List<Integer> b = Arrays.asList(graph2Nodes);

        Integer[][] combinations = a.stream().flatMap(ai -> b.stream().map(bi -> new Integer[]{ai, bi})).toArray(Integer[][]::new);

        for (int i = 0; i < combinations.length; i++) {

            Tuple2<Integer, Integer> combi = new Tuple2<>(combinations[i][0], combinations[i][1]);

            tempIdMapping.put(i, combi);
            tempReverseIdMapping.put(combi, i);
        }

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> opIDToEdgesMapOfGraph1 = getOpIDToEdgesMap(p, graph1);
        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> opIDToEdgesMapOfGraph2 = getOpIDToEdgesMap(p, graph2);


        for (int opID = 0; opID < p.numOfOperators; opID++) {

            ArrayList<Tuple3<Integer, Integer, Integer>> graph1EdgesOfOP = opIDToEdgesMapOfGraph1.get(opID);

            ArrayList<Tuple3<Integer, Integer, Integer>> graph2EdgesOfOP = opIDToEdgesMapOfGraph2.get(opID);


            ArrayList<Tuple3<Integer, Integer, Integer>> tempGraph1Edges = new ArrayList<>();

            for (Tuple3<Integer, Integer, Integer> graph1Edge : graph1EdgesOfOP) {

                for (Tuple3<Integer, Integer, Integer> graph2Edge : graph2EdgesOfOP) {

                    Tuple2<Integer, Integer> startNodes = new Tuple2<>(graph1Edge._1(), graph2Edge._1());

                    Tuple2<Integer, Integer> endNodes = new Tuple2<>(graph1Edge._3(), graph2Edge._3());

                    int newStartID = tempReverseIdMapping.get(startNodes);

                    int newEndID = tempReverseIdMapping.get(endNodes);

                    Tuple3<Integer, Integer, Integer> newEdge = new Tuple3<>(newStartID, opID, newEndID);

                    tempGraph1Edges.add(newEdge);

                }

            }


            newMultiEdges.addAll(tempGraph1Edges);


        }


        HashMap<Integer, NodeValue> newIdMapping = new HashMap<>();


        for (int i : tempIdMapping.keySet()) {

            Tuple2<Integer, Integer> oldIDs = tempIdMapping.get(i);

            int oldGraph1ID = oldIDs._1();

            int oldGraph2ID = oldIDs._2();

            //ArrayList<Integer> assignedFacts = new ArrayList<>(graph1.idMapping().get(oldGraph1ID));

            //if (!assignedFacts.contains(oldGraph2ID)) assignedFacts.add(oldGraph2ID);

            NodeValue newNodeValue = new MergeNode(graph1.idMapping().get(oldGraph1ID), graph2.idMapping().get(oldGraph2ID), p);

            newIdMapping.put(i, newNodeValue);

        }

        Tuple3<Integer, Integer, Integer>[] newEdgeTuple = Utils.convertEdgeArrayListToTuple3(newMultiEdges);

        Integer[] nodeIDS = Utils.convertNodeIDArrayListToArray(newIdMapping);

        Tuple2<Integer, Integer> oldStartIDs = new Tuple2<>(graph1.startNodeID(), graph2.startNodeID());

        int newStartID = tempReverseIdMapping.get(oldStartIDs);

        Set<Integer> usedFactIndexes = new HashSet<>(graph1.usedFactIndexes());

        usedFactIndexes.addAll(graph2.usedFactIndexes());


        HashSet<Integer> usedVariables = new HashSet<>(graph1.usedVariables());

        usedVariables.addAll(graph2.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph1.allVariables());

        notYetUsedVariables.removeAll(usedVariables);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newMultiGraph = new EdgeLabelledGraph<>(nodeIDS, newEdgeTuple, newIdMapping, newStartID, usedFactIndexes, usedVariables, notYetUsedVariables, graph1.allVariables());


        return newMultiGraph;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> shrinkingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> multiGraph, ArrayList<ArrayList<Integer>> aggregatedIDs) {

        HashMap<Integer, NodeValue> newIDMapping = new HashMap<>();

        ArrayList<Integer> indexesToReplace = new ArrayList<>();

        for (ArrayList<Integer> indexes : aggregatedIDs){

            indexesToReplace.addAll(indexes);

        }

        HashMap<Integer, Integer> tempReverseIDMapping  = new HashMap<>();

        Tuple3<Integer, Integer, Integer>[] oldEdges = multiGraph.labelledEdges();



        int index =0;

        Integer[] nodes = (Integer[]) multiGraph.arrayVertices();

        for (int id: nodes){

            if (!indexesToReplace.contains(id)){

                newIDMapping.put(index, multiGraph.idMapping().get(id));
                tempReverseIDMapping.put(id, index);
                index++;

            }

        }

        for (ArrayList<Integer> toAggregate : aggregatedIDs){

            NodeValue newNodeValue = multiGraph.idMapping().get(toAggregate.get(0));
            tempReverseIDMapping.put(toAggregate.get(0), index);


            for (int j=1; j<toAggregate.size(); j++) {

                newNodeValue = new ShrinkNode(newNodeValue, multiGraph.idMapping().get(toAggregate.get(j)), p);
                tempReverseIDMapping.put(toAggregate.get(j), index);

            }

            newIDMapping.put(index, newNodeValue);
            index++;

        }

        Tuple3<Integer, Integer, Integer>[] newEdges = shrinkEdges(oldEdges, tempReverseIDMapping);

        int newStartID = tempReverseIDMapping.get(multiGraph.startNodeID());



        Set<Integer> usedFactIndexes = new HashSet<>(multiGraph.usedFactIndexes());

        Set<Integer> usedVariables = new HashSet<>(multiGraph.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(multiGraph.notYetUsedVariables());



        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(newIDMapping), newEdges, newIDMapping, newStartID, usedFactIndexes, usedVariables, notYetUsedVariables, multiGraph.allVariables());

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





    public EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> convertMultiGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph) {


        HashMap<Integer, NodeValue> idMapping = graph.idMapping();

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges(), idMapping);

        Set<Integer> usedFactIndexes = new HashSet<>(graph.usedFactIndexes());

        Set<Integer> usedVariables = new HashSet<>(graph.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph.notYetUsedVariables());



        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping, graph.startNodeID(), usedFactIndexes, usedVariables, notYetUsedVariables, graph.allVariables());


        return newGraph;
    }

    public static String[] convertNodesToStrings(SasPlusProblem p, HashMap<Integer, NodeValue> idMapping) {


        String[] newNodes = new String[idMapping.size()];

        Integer[] mappingKeys = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);


        for (int i = 0; i < newNodes.length; i++) {
            newNodes[i] = Utils.getMultiIDString(p, mappingKeys[i], idMapping);
            //"\"" + containedIndexes[i] + ": " + p.factStrs[containedIndexes[i]] + "\"";
        }

        return newNodes;
    }

    public Tuple3[] convertEdgesToStrings(SasPlusProblem p, Tuple3<Integer, Integer, Integer>[] oldEdges, HashMap<Integer, NodeValue> idMapping) {


        ArrayList<Tuple3<String, String, String>> newEdges = new ArrayList<>();

        Map<Integer, ArrayList<Integer>> selfLoops = new HashMap<>();

        for (Tuple3<Integer, Integer, Integer> oldEdge : oldEdges) {


            if (oldEdge._1() != oldEdge._3()) {


                //no self-loop

                String startEdge = Utils.getMultiIDString(p, oldEdge._1(), idMapping);
                //                      "\"" + oldEdge._1() + ": "  + p.factStrs[oldEdge._1()] + "\"";
                String endEdge = Utils.getMultiIDString(p, oldEdge._3(), idMapping);
                //              "\"" + oldEdge._3() + ": "  + p.factStrs[oldEdge._3()] + "\"";
                String labelEdge = SingleGraphMethods.getOpString(p, oldEdge._2());
                //"\"" + oldEdge._2() + ": " + p.opNames[oldEdge._2()] +  "\"";

                Tuple3<String, String, String> newEdge = new Tuple3<>(startEdge, labelEdge, endEdge);
                newEdges.add(newEdge);

            } else {

                int varIndex = oldEdge._1();
                if (selfLoops.containsKey(varIndex)) {

                    ArrayList<Integer> selfLoop = selfLoops.get(varIndex);
                    selfLoop.add(oldEdge._2());


                } else {
                    ArrayList<Integer> selfLoop = new ArrayList<>();
                    selfLoop.add(oldEdge._2());
                    selfLoops.put(varIndex, selfLoop);
                }

            }
        }

        for (int nodeID : selfLoops.keySet()) {

            String varString = Utils.getMultiIDString(p, nodeID, idMapping);
            String labelEdge = "\"" + selfLoops.get(nodeID) + "\"";

            Tuple3<String, String, String> newEdge = new Tuple3<>(varString, labelEdge, varString);
            newEdges.add(newEdge);

        }

        Tuple3[] newEdgeArray = new Tuple3[newEdges.size()];
        for (int i = 0; i < newEdges.size(); i++) {
            newEdgeArray[i] = newEdges.get(i);
        }


        return newEdgeArray;
    }

    public void printMultiGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> multiGraph, String outputfile) {


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> dismissNotReachableNodes(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

        int startNodeID = graph.startNodeID();

        ArrayList<Integer> allNodeIDs = new ArrayList<>(graph.idMapping().keySet());

        ArrayList<Integer> nodesToKeep = new ArrayList<>();

        ArrayList<Integer> nextNodes = new ArrayList<>();

        Tuple3<Integer,Integer,Integer>[] edges = graph.labelledEdges();

        nextNodes.add(startNodeID);

        nodesToKeep.add(startNodeID);

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = getIDToOutgoingEdgesMap(graph);

        nodesToKeep = breadthSearch(nextNodes, nodesToKeep, outgoingEdgesMap);

        ArrayList<Integer> nodesToDismiss = new ArrayList<>(allNodeIDs);
        nodesToDismiss.removeAll(nodesToKeep);

        Tuple3<Integer,Integer,Integer>[] dismissedEdges = dismissEdgesOfNotReachableNodes(edges, nodesToDismiss);

        HashMap<Integer, NodeValue> dismissedIDMapping = dismissIDMappingOfNotReachableNodeIDs(graph.idMapping(), nodesToDismiss);

        HashSet<Integer> usedFactIndexes = new HashSet<>(graph.usedFactIndexes());

        HashSet<Integer> usedVariables = new HashSet<>(graph.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph.notYetUsedVariables());


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(dismissedIDMapping), dismissedEdges, dismissedIDMapping, graph.startNodeID(), usedFactIndexes, usedVariables, notYetUsedVariables, graph.allVariables());



        return newGraph;
    }

    public static HashMap<Integer, Integer> getDistancesFromStart(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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

    public Tuple3<Integer,Integer,Integer>[] dismissEdgesOfNotReachableNodes(Tuple3<Integer,Integer,Integer>[] oldEdges, ArrayList<Integer> nodesToDismiss){

        ArrayList<Tuple3<Integer,Integer,Integer>> newEdgeArrayList = new ArrayList<>();

        for(Tuple3<Integer,Integer,Integer> edge : oldEdges){
            if ((!nodesToDismiss.contains(edge._1())) && (!nodesToDismiss.contains(edge._3()))){
                newEdgeArrayList.add(edge);
            }
        }


        return Utils.convertEdgeArrayListToTuple3(newEdgeArrayList);

    }

    public ArrayList<Integer> breadthSearch(ArrayList<Integer> nextNodes, ArrayList<Integer> nodesToKeep,
                                                                       HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap){

        ArrayList<Integer> newNextNodes = new ArrayList<>(nextNodes);

        ArrayList<Integer> reachedNodes = new ArrayList<>(nodesToKeep);

        if (nextNodes.size()>0) {

            int nextNode = nextNodes.get(0);


            ArrayList<Tuple3<Integer, Integer, Integer>> outgoingEdges = outgoingEdgesMap.get(nextNode);
            newNextNodes.remove(0);
            for (Tuple3<Integer, Integer, Integer> outgoingEdge : outgoingEdges){
                int endID = outgoingEdge._3();
                if (!reachedNodes.contains(endID)){
                    reachedNodes.add(endID);
                    newNextNodes.add(endID);
                }
            }

            return breadthSearch(newNextNodes, reachedNodes, outgoingEdgesMap);

        }

        return reachedNodes;

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



    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToOutgoingEdgesMap(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToIncomingEdgesMap(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getOpIDToEdgesMap(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> opIDToOutgoingEdgesMap = new HashMap<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            opIDToOutgoingEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges()){

            opIDToOutgoingEdgesMap.get(edge._2()).add(edge);

        }

        return opIDToOutgoingEdgesMap;

    }

    public static Set<Integer> getGoalNodes(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

        HashSet<Integer> goalNodes = new HashSet<>();

        for (int id : graph.idMapping().keySet()){

            NodeValue nodeValue = graph.idMapping().get(id);
            if (nodeValue.isGoalNode()) goalNodes.add(id);

        }


        return goalNodes;


    }


/*    public static Set<Integer> getGoalNodes(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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


/*    public HashMap<Integer, Integer> getDistancesFromGoal(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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

    public HashMap<Integer,Integer> getDistancesFromGoal(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

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



    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> shrinkingStrategy1(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){



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

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> shrinkedGraph = shrinkingStep(p,graph, nodesToShrink);


        return shrinkedGraph;

    }

    public static ArrayList<Integer> getNodesFarthestFromStartAndGoal(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph, HashMap<Integer,Integer> summedDistances){

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



    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> mergingStrategy1(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph, long seed){



        Set<Integer> notYetUsedVariables = getNotYetUsedVariables(p, graph);

        ArrayList<Integer> variablesToMerge = new ArrayList<>(notYetUsedVariables);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> newGraph;

        if (!(graph==null)) {

            //System.out.println("Not yet used Variables: " + notYetUsedVariables);

            int randomVarIndex = Utils.randomIntGenerator(notYetUsedVariables.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            System.out.println("Merge with Variable: " + randomVar);


            newGraph = mergeWithVar(p, graph, randomVar);


        }else{

            int randomVarIndex = Utils.randomIntGenerator(notYetUsedVariables.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            System.out.println("Start with Variable: " + randomVar);

            newGraph = SingleGraphMethods.getSingleGraphForVarIndex(p, randomVar);

        }

        return newGraph;


    }

    public static HashSet<Integer> getNotYetUsedVariables(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph){

        HashSet<Integer> notYetUsedVariables;

        if (!(graph==null)) {

            //System.out.println("All Variables: " + graph.allVariables());
            notYetUsedVariables = new HashSet<>(graph.allVariables());

            Set<Integer> usedVariables = graph.usedVariables();

            //System.out.println("Used Variables: " + usedVariables);

            notYetUsedVariables.removeAll(graph.usedVariables());

            //System.out.println("Not yet Used Variables: " + notYetUsedVariables);


            /*for (int i = 0; i < p.numOfVars; i++) {
                if (!usedVariables.contains(i)) notYetUsedVariables.add(i);
            }*/

        }else{

            notYetUsedVariables = new HashSet<>();

            for (int i = 0; i < p.numOfVars; i++) {
                notYetUsedVariables.add(i);
            }

        }

        return notYetUsedVariables;

    }




    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> mergeAndShrinkProcess(SasPlusProblem p, int shrinkingBound){


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>> graph = mergingStrategy1(p, null, 0);

        while (graph.notYetUsedVariables().size()!=0){

            while (graph.idMapping().keySet().size()>shrinkingBound){
                graph = shrinkingStrategy1(p, graph);
            }

            graph = mergingStrategy1(p, graph, 0);

        }

        while (graph.idMapping().keySet().size()>shrinkingBound){
            graph = shrinkingStrategy1(p, graph);
        }


        return graph;

    }





}
