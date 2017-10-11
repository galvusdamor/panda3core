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


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph = getMultiGraphUntilVarID(p, 1);

        printMultiGraph(p, graph, "graph4.pdf");

        ArrayList<ArrayList<Integer>> aggregatedIDs = new ArrayList<>();

        ArrayList<Integer> aggregatedIDs1 = new ArrayList<>();

        aggregatedIDs1.add(2);
        aggregatedIDs1.add(3);
        aggregatedIDs1.add(4);

        aggregatedIDs.add(aggregatedIDs1);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> shrinkedGraph = shrinkingStep(p, graph, aggregatedIDs);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> newGraph = mergeWithVar(p, shrinkedGraph,2);

        newGraph = mergeWithVar(p, newGraph,3);

        newGraph = mergeWithVar(p, newGraph,4);

        aggregatedIDs.clear();

        ArrayList<Integer> aggregatedIDs2 = new ArrayList<>();

        aggregatedIDs2.add(36);
        aggregatedIDs2.add(18);

        aggregatedIDs.add(aggregatedIDs2);


        printMultiGraph(p, newGraph, "graph4.pdf");

        newGraph = shrinkingStep(p, newGraph, aggregatedIDs);

        printMultiGraph(p, newGraph, "graph5.pdf");


        System.exit(0);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> singleNodeGraph = SingleGraphMethods.getSingleGraphForVarIndex(p,2);

        EdgeLabelledGraph<String , String, HashMap<Integer, NodeValue>> multiNodeGraph = convertMultiGraphToStringGraph(p, singleNodeGraph);

        Dot2PdfCompiler.writeDotToFile(multiNodeGraph, "graph3.pdf");



    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            graph1 = mergeWithVar(p, graph1, i);

        }

        return graph1;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> mergeWithVar(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph1, int varIndex){

        if (varIndex >= p.numOfVars){

            System.out.println("Variable " + varIndex + " does not exist.");
            return graph1;
        }


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, varIndex);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> newGraph = mergingStep(p, graph1, graph2);

        return newGraph;


    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> mergingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph1, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2) {

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


        for (int opID = 0; opID < p.numOfOperators; opID++) {

            ArrayList<Tuple3<Integer, Integer, Integer>> tempGraph1Edges = new ArrayList<>();

            for (Tuple3<Integer, Integer, Integer> graph1Edge : graph1Edges) {

                if (graph1Edge._2() == opID) {

                    for (Tuple3<Integer, Integer, Integer> graph2Edge : graph2Edges) {

                        if (graph2Edge._2() == opID) {

                            Tuple2<Integer, Integer> startNodes = new Tuple2<>(graph1Edge._1(), graph2Edge._1());

                            Tuple2<Integer, Integer> endNodes = new Tuple2<>(graph1Edge._3(), graph2Edge._3());

                            int newStartID = tempReverseIdMapping.get(startNodes);

                            int newEndID = tempReverseIdMapping.get(endNodes);

                            Tuple3<Integer, Integer, Integer> newEdge = new Tuple3<>(newStartID, opID, newEndID);

                            tempGraph1Edges.add(newEdge);

                        }

                    }
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


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> newMultiGraph = new EdgeLabelledGraph<>(nodeIDS, newEdgeTuple, newIdMapping);


        return newMultiGraph;

    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> shrinkingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> multiGraph, ArrayList<ArrayList<Integer>> aggregatedIDs) {

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


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(newIDMapping), newEdges, newIDMapping);

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





    public EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>> convertMultiGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph) {


        HashMap<Integer, NodeValue> idMapping = graph.idMapping();

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges(), idMapping);


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping);


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
                String labelEdge = OldSingleGraph.getOpString(p, oldEdge._2());
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

    public void printMultiGraph(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> multiGraph, String outputfile) {


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }
}
