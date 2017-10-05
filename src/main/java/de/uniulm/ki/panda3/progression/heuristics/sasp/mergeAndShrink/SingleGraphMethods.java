package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import de.uniulm.ki.util.EdgeLabelledGraphSingle;
import scala.Tuple2;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by biederma on 05.10.2017.
 */
public final class SingleGraphMethods {



    public static ArrayList<Tuple3<Integer,Integer,Integer>> convertSingleEdgesToMultiEdges(HashMap<Integer, ArrayList<Integer>> idMapping, ArrayList<Tuple3<Integer,Integer,Integer>> singleEdges){

        ArrayList<Tuple3<Integer,Integer,Integer>> multiEdges = new ArrayList<>();

        for (Tuple3<Integer,Integer,Integer> singleEdge : singleEdges){

            int startEdge = singleEdge._1();
            int opIndex = singleEdge._2();
            int endEdge = singleEdge._3();

            ArrayList<Integer> startContainingIDs = Utils.findContainedIDs(idMapping, startEdge);

            if (startEdge != endEdge) {

                ArrayList<Integer> endContainingIDs = Utils.findContainedIDs(idMapping, endEdge);

                for (int startIndex : startContainingIDs) {
                    for (int endIndex : endContainingIDs) {

                        Tuple3<Integer, Integer, Integer> multiEdge = new Tuple3<>(startIndex, opIndex, endIndex);
                        if (!Utils.containsEdge(multiEdges, multiEdge)) {
                            multiEdges.add(multiEdge);
                        }
                    }
                }
            }else {

                for (int startIndex : startContainingIDs) {

                    Tuple3<Integer, Integer, Integer> multiEdge = new Tuple3<>(startIndex, opIndex, startIndex);
                    if (!Utils.containsEdge(multiEdges, multiEdge)) {
                        multiEdges.add(multiEdge);
                    }

                }


            }

        }



        return multiEdges;

    }


    public static ArrayList<Tuple3<Integer,Integer,Integer>> getSingleEdgesForAllContainedIndexes(SasPlusProblem p, ArrayList<Integer> containedVarIndexes){

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer,Integer,Integer>> edgesForOp = getEdgesForOpSingle(p, i, containedVarIndexes);
            edges.addAll(edgesForOp);
        }

        return edges;
    }


    public static ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesForOpSingle(SasPlusProblem p, int OpIndex, ArrayList<Integer> containedVarIndexes){

        int[] pres = p.precLists[OpIndex];
        int[] adds = p.addLists[OpIndex];
        int[] dels = p.delLists[OpIndex];

        //Schritt 1: OpIndexes in den pres, adds und dels aussortieren, die nicht in den containedVarIndexes sind

        //Schritt 2:
        //1) von allen dels zu allen adds
        //2) self-loops von allen bei denens weder in dels noch in adds ist
        //3) self-loops von allen, bei denens in beiden ist (ist bereits in Punkt 1 enthalten)



        //Schritt 1:

        ArrayList<Integer> preListDismissed = Utils.dismissNotContainedIndexes(pres, containedVarIndexes);
        ArrayList<Integer> addListDismissed = Utils.dismissNotContainedIndexes(adds, containedVarIndexes);
        ArrayList<Integer> delListDismissed = Utils.dismissNotContainedIndexes(dels, containedVarIndexes);

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

        for (int startEdge : delListDismissed){

            if ((preListDismissed.size()==0) || preListDismissed.contains(startEdge)) {

                for (int endEdge : addListDismissed) {


                    //String labelEdge = "\"" + p.opNames[OpIndex] + "\"";

                    Tuple3<Integer, Integer, Integer> edge = new Tuple3<>(startEdge, OpIndex, endEdge);
                    if (!Utils.containsEdge(edges, edge)) {
                        edges.add(edge);
                    }

                }
            }

        }




        //Schritt 2:


/*        for (int index : containedVarIndexes){
            //Zusätzlich abklären?: nicht in pres enthalten
            if (!addListDismissed.contains(index) && !delListDismissed.contains(index)){
                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(index,OpIndex,index);
                if (!Utils.containsEdge(edges, edge)) edges.add(edge);
            }
        }*/


        if ((addListDismissed.size()==0) && (delListDismissed.size()==0)){

            for (int index : containedVarIndexes){

                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(index,OpIndex,index);
                if (!Utils.containsEdge(edges, edge)) edges.add(edge);
            }

        }


        return edges;

    }



    public static Tuple3[] convertSingleEdgesToStrings(SasPlusProblem p, Tuple3<Integer,Integer,Integer>[] oldEdges){


        ArrayList<Tuple3<String,String,String>> newEdges = new ArrayList<>();

        Map<Integer,ArrayList<Integer>> selfLoops = new HashMap<>();

        for (Tuple3<Integer,Integer,Integer> oldEdge : oldEdges){



            if (oldEdge._1() != oldEdge._3()) {


                //no self-loop

                String startEdge = getVarString(p, oldEdge._1());
                //                      "\"" + oldEdge._1() + ": "  + p.factStrs[oldEdge._1()] + "\"";
                String endEdge = getVarString(p, oldEdge._3());
                //              "\"" + oldEdge._3() + ": "  + p.factStrs[oldEdge._3()] + "\"";
                String labelEdge = getOpString(p,oldEdge._2());
                //"\"" + oldEdge._2() + ": " + p.opNames[oldEdge._2()] +  "\"";

                Tuple3<String, String, String> newEdge = new Tuple3<>(startEdge, labelEdge, endEdge);
                newEdges.add(newEdge);

            }else{

                //self-loop

                int varIndex = oldEdge._1();
                if (selfLoops.containsKey(varIndex)){

                    ArrayList<Integer> selfLoop = selfLoops.get(varIndex);
                    selfLoop.add(oldEdge._2());


                }else{
                    ArrayList<Integer> selfLoop = new ArrayList<>();
                    selfLoop.add(oldEdge._2());
                    selfLoops.put(varIndex,selfLoop);
                }

            }
        }

        for (int varIndex: selfLoops.keySet()){

            String varString = getVarString(p, varIndex);
            String labelEdge = "\"" + selfLoops.get(varIndex) + "\"";

            Tuple3<String, String, String> newEdge = new Tuple3<>(varString,labelEdge,varString);
            newEdges.add(newEdge);

        }

        Tuple3[] newEdgeArray = new Tuple3[newEdges.size()];
        for (int i=0; i<newEdges.size(); i++){
            newEdgeArray[i] = newEdges.get(i);
        }



        return newEdgeArray;
    }



    public static String[] convertSingleNodesToStrings(SasPlusProblem p, Integer[] containedIndexes){

        String[] newNodes = new String[containedIndexes.length];

        for (int i=0; i<newNodes.length; i++){
            newNodes[i] = getVarString(p, containedIndexes[i]);
            //"\"" + containedIndexes[i] + ": " + p.factStrs[containedIndexes[i]] + "\"";
        }

        return newNodes;
    }




    public static EdgeLabelledGraphSingle<String,String> convertSingleGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraphSingle<Integer,Integer> graph){

        String[] newNodes = convertSingleNodesToStrings(p, (Integer[]) graph.arrayVertices());
        Tuple3[] newEdges = convertSingleEdgesToStrings(p, graph.labelledEdges());

        EdgeLabelledGraphSingle<String,String> newGraph = new EdgeLabelledGraphSingle<>(newNodes, newEdges);


        return newGraph;
    }

    public static Tuple2<Integer[],Tuple3[]> getSingleNodesAndEdgesForVarIndex(SasPlusProblem p, int varIndex){

        int firstIndex = p.firstIndex[varIndex];
        int lastIndex = p.lastIndex[varIndex];

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i=firstIndex; i<=lastIndex; i++){
            containedIndexes.add(i);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = getSingleEdgesForAllContainedIndexes(p,containedIndexes);



        //System.out.println(edges3);

        Tuple3[] edgeArray = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeArray[i] = edges.get(i);
        }


        Integer[] containedIndexesArray = containedIndexes.toArray(new Integer[containedIndexes.size()]);


        return new Tuple2<>(containedIndexesArray, edgeArray);
    }

    public static void printSingleGraphForVarIndex(SasPlusProblem p, int varIndex, String outputfile){

        Tuple2<Integer[],Tuple3[]> graphData = getSingleNodesAndEdgesForVarIndex(p,varIndex);

        EdgeLabelledGraphSingle<Integer,Integer> graph = new EdgeLabelledGraphSingle(graphData._1(), graphData._2());

        EdgeLabelledGraphSingle<String,String> stringGraph = convertSingleGraphToStringGraph(p, graph);

        Dot2PdfCompiler.writeDotToFile(stringGraph,outputfile);


    }

    public static EdgeLabelledGraphSingle<Integer,Integer> getSingleGraph(SasPlusProblem p, int varIndex){

        Tuple2<Integer[],Tuple3[]> graphData = getSingleNodesAndEdgesForVarIndex(p,varIndex);

        EdgeLabelledGraphSingle<Integer,Integer> graph = new EdgeLabelledGraphSingle(graphData._1(), graphData._2());

        return graph;

    }


    public static EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> convertSingleGraphToMultiGraph(EdgeLabelledGraphSingle<Integer,Integer> singleGraph){

        Tuple3<Integer,Integer,Integer>[] singleEdges = singleGraph.labelledEdges();

        Integer[] singleNodes = (Integer[]) singleGraph.arrayVertices();

        HashMap<Integer, ArrayList<Integer>> idMapping = new HashMap<>();

        for (int i=0; i<singleNodes.length; i++){

            ArrayList<Integer> value = new ArrayList<>();
            value.add(singleNodes[i]);

            idMapping.put(i, value);

        }

        ArrayList<Tuple3<Integer,Integer,Integer>> singleEdgesArray = new ArrayList<>();

        for (Tuple3<Integer,Integer,Integer> singleEdge : singleEdges){
            singleEdgesArray.add(singleEdge);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> multiEdges = convertSingleEdgesToMultiEdges(idMapping, singleEdgesArray);

        //Integer[] nodeIDS = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);

        Integer[] nodeIDS = Utils.convertNodeIDArrayListToArray(idMapping);

        Tuple3[] edgeTuple = Utils.convertEdgeArrayListToTuple3(multiEdges);

        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph = new EdgeLabelledGraph(nodeIDS, edgeTuple, idMapping);


        return multiGraph;


    }


    public static String getVarString(SasPlusProblem p, int varIndex){

        String s =  varIndex + ": "   + p.factStrs[varIndex];

        return s;

    }

    public static String getOpString(SasPlusProblem p, int OpIndex){

        String s = "\"" + OpIndex + ": " + p.opNames[OpIndex] +  "\"";

        return s;

    }


}
