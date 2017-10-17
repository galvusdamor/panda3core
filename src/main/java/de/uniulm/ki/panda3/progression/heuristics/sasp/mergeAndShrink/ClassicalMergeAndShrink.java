package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import com.sun.applet2.AppletParameters;
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.MergeNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ShrinkNode;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;

import scala.Tuple2;
import scala.Tuple3;


import java.util.*;


public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph = getMultiGraphUntilVarID(p, 1);

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

        graph = mergeWithVar(p, graph,4);

        aggregatedIDs.clear();

        ArrayList<Integer> aggregatedIDs2 = new ArrayList<>();

        aggregatedIDs2.add(36);
        aggregatedIDs2.add(18);

        aggregatedIDs.add(aggregatedIDs2);

        //graph = shrinkingStep(p, graph, aggregatedIDs);


        printMultiGraph(p, graph, "graph4.pdf");

        //graph = shrinkingStep(p, graph, aggregatedIDs);
        //graph = dismissNotReachableNodes(p, graph);

        //printMultiGraph(p, graph, "graph5.pdf");

        //System.out.println("Startknoten: " + graph.startNodeID());


        //HashMap<Integer, ArrayList<Tuple3<Integer,Integer,Integer>>> outgoingEdgesMap = getIDToOutgoingEdgesMap(graph);

        //System.out.println(getOpIDToEdgesMap(p, graph));


        System.exit(0);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> singleNodeGraph = SingleGraphMethods.getSingleGraphForVarIndex(p,2);

        EdgeLabelledGraph<String , String, HashMap<Integer, NodeValue>, Integer> multiNodeGraph = convertMultiGraphToStringGraph(p, singleNodeGraph);

        Dot2PdfCompiler.writeDotToFile(multiNodeGraph, "graph3.pdf");



    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            graph1 = mergeWithVar(p, graph1, i);

        }

        return graph1;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> mergeWithVar(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph1, int varIndex){

        if (varIndex >= p.numOfVars){

            System.out.println("Variable " + varIndex + " does not exist.");
            return graph1;
        }


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, varIndex);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> newGraph = mergingStep(p, graph1, graph2);

        newGraph = dismissNotReachableNodes(newGraph);

        return newGraph;


    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> mergingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph1, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph2) {

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


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> newMultiGraph = new EdgeLabelledGraph<>(nodeIDS, newEdgeTuple, newIdMapping, newStartID);


        return newMultiGraph;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> shrinkingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> multiGraph, ArrayList<ArrayList<Integer>> aggregatedIDs) {

        HashMap<Integer, NodeValue> newIDMapping = new HashMap<>();

        ArrayList<Integer> indexesToReplace = new ArrayList<>();

        for (ArrayList<Integer> indexes : aggregatedIDs){

            indexesToReplace.addAll(indexes);

        }

        HashMap<Integer, Integer> tempReverseIDMapping  = new HashMap<>();

        Tuple3<Integer, Integer, Integer>[] oldEdges = multiGraph.labelledEdges();



        int index =0;

        Integer[] nodes = (Integer[]) multiGraph.arrayVertices();

        for (int i=0; i<nodes.length; i++){

            Integer id = nodes[i];

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


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(newIDMapping), newEdges, newIDMapping, newStartID);

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





    public EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer> convertMultiGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph) {


        HashMap<Integer, NodeValue> idMapping = graph.idMapping();

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges(), idMapping);


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping, graph.startNodeID());


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

    public void printMultiGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> multiGraph, String outputfile) {


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> dismissNotReachableNodes(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph){

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

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(dismissedIDMapping), dismissedEdges, dismissedIDMapping, graph.startNodeID());

        return newGraph;
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

        ArrayList<Integer> newNodesToKeep = new ArrayList<>(nodesToKeep);

        if (nextNodes.size()>0) {

            ArrayList<Tuple3<Integer, Integer, Integer>> outgoingEdges = outgoingEdgesMap.get(nextNodes.get(0));
            newNextNodes.remove(0);
            for (Tuple3<Integer, Integer, Integer> outgoingEdge : outgoingEdges){
                int endID = outgoingEdge._3();
                if (!newNodesToKeep.contains(endID)){
                    newNodesToKeep.add(endID);
                    newNextNodes.add(endID);
                }
            }

            return breadthSearch(newNextNodes, newNodesToKeep, outgoingEdgesMap);

        }


        return newNodesToKeep;
    }

    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToOutgoingEdgesMap(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph){

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


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getOpIDToEdgesMap(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> opIDToEdgesMap = new HashMap<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            opIDToEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges()){

            opIDToEdgesMap.get(edge._2()).add(edge);

        }

        return opIDToEdgesMap;

    }



}
