package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink.HtnMsGraph;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ElementaryNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnNodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.ScalaReflectionException;
import scala.Tuple3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


/**
 * Created by biederma on 07.09.2017.
 */
public final class Utils {

    private Utils(){

    }

    public static boolean contains(int[] array, int number){

        for (int i=0; i<array.length; i++){
            if (array[i] == number) return true;
        }

        return false;
    }

    public static boolean containsEdge(ArrayList<Tuple3<Integer,Integer,Integer>> edges, Tuple3 edge){

        boolean contained = false;

        for (Tuple3 e : edges){

            if (e._1().equals(edge._1()) && e._2().equals(edge._2()) && e._3().equals(edge._3())){
                contained = true;
                break;
            }

        }


        return contained;

    }





    public static ArrayList<Integer> dismissNotContainedIndexes(int[] allIndexes, ArrayList<Integer> containedIndexes){

        ArrayList<Integer> result = new ArrayList<>();

        for (int i : allIndexes){
            if (containedIndexes.contains(i)) result.add(i);
        }

        return result;
    }



    public static String getMultiIDString(SasPlusProblem p, int multiID, HashMap<Integer, NodeValue> idMapping){

        NodeValue nodeValue = idMapping.get(multiID);

        String s;

        if (nodeValue.isGoalNode()){

            s =  multiID + " (goal): \n(" + nodeValue.longInfo() + ")";

        }else{

            s =  multiID + " (no goal): \n(" + nodeValue.longInfo() + ")";

        }



        return s;

    }


    public static Tuple3[] convertEdgeArrayListToTuple3(ArrayList<Tuple3<Integer,Integer,Integer>> edges){

        Tuple3[] edgeTuple = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeTuple[i] = edges.get(i);
        }

        return edgeTuple;

    }


    public static Integer[] convertNodeIDArrayListToArray(HashMap<Integer, NodeValue> idMapping){

        Integer[] nodeIDS = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);

        return nodeIDS;
    }



    public static int randomIntGenerator(int count, long seed) {
        Random generator = new Random(seed);
        int number = generator.nextInt(count);

        return number;
    }

    public static String edgesToShortString(Tuple3<Integer,Integer,Integer>[] edges){

        String s = "";

        for (Tuple3<Integer,Integer,Integer> edge : edges){

            s += edge._1() + " -> " + edge._3() + "\n";
        }

        return s;
    }


    public static void printMultiGraph(SasPlusProblem p, ClassicalMSGraph multiGraph, String outputfile) {


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }

    public static void printHtnGraph(SasPlusProblem p, HtnMsGraph htnMsGraph, String outputfile) {


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> stringMultiGraph = convertHtnGraphToStringGraph(p, htnMsGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }

    public static void printAllHtnGraphs(SasPlusProblem p, HashMap<Integer, HtnMsGraph> graphs){


        for (int i: graphs.keySet()){

            String outputfile = "htnGraph" + i +  ".pdf";

            if (graphs.get(i).idMapping.keySet().size()<50) {

                System.out.println("Print Graph " + i);
                Utils.printHtnGraph(p, graphs.get(i), outputfile);
            }

        }

    }

    public static EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> convertMultiGraphToStringGraph(SasPlusProblem p, ClassicalMSGraph graph) {


        HashMap<Integer, NodeValue> idMapping = graph.idMapping;

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges, idMapping);



        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping, graph.startNodeID, graph.cascadingTables);


        return newGraph;
    }

    public static EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> convertHtnGraphToStringGraph(SasPlusProblem p, HtnMsGraph graph) {


        HashMap<Integer, NodeValue> idMapping = graph.idMapping;

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertHTNEdgesToStrings(p, graph.labelledEdges, idMapping);


        EdgeLabelledGraph<String, String, HashMap<Integer, NodeValue>, Integer, CascadingTables> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping, graph.startNodeID, graph.cascadingTables);


        return newGraph;
    }

    public static Tuple3[] convertEdgesToStrings(SasPlusProblem p, Tuple3<Integer, Integer, Integer>[] oldEdges, HashMap<Integer, NodeValue> idMapping) {


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


    public static Tuple3[] convertHTNEdgesToStrings(SasPlusProblem p, Tuple3<Integer, Integer, Integer>[] oldEdges, HashMap<Integer, NodeValue> idMapping) {

        Task[] allTasks = ProgressionNetwork.indexToTask;


        ArrayList<Tuple3<String, String, String>> newEdges = new ArrayList<>();

        Map<Integer, ArrayList<Integer>> selfLoops = new HashMap<>();

        for (Tuple3<Integer, Integer, Integer> oldEdge : oldEdges) {


            if (oldEdge._1() != oldEdge._3()) {


                //no self-loop

                String startEdge = Utils.getMultiIDString(p, oldEdge._1(), idMapping);
                //                      "\"" + oldEdge._1() + ": "  + p.factStrs[oldEdge._1()] + "\"";
                String endEdge = Utils.getMultiIDString(p, oldEdge._3(), idMapping);
                //              "\"" + oldEdge._3() + ": "  + p.factStrs[oldEdge._3()] + "\"";
                String labelEdge;
                if (oldEdge._2()==-1){
                    labelEdge = "";
                }else {
                    labelEdge = oldEdge._2() + ": " + allTasks[oldEdge._2()].shortInfo();
                }
                //String labelEdge = SingleGraphMethods.getOpString(p, oldEdge._2());
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

    public static String[] convertNodesToStrings(SasPlusProblem p, HashMap<Integer, NodeValue> idMapping) {


        String[] newNodes = new String[idMapping.size()];

        Integer[] mappingKeys = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);


        for (int i = 0; i < newNodes.length; i++) {
            newNodes[i] = Utils.getMultiIDString(p, mappingKeys[i], idMapping);
            //"\"" + containedIndexes[i] + ": " + p.factStrs[containedIndexes[i]] + "\"";
        }

        return newNodes;
    }



}
