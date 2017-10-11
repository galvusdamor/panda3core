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

import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.*;

/**
 * Created by biederma on 05.10.2017.
 */
public final class SingleGraphMethods {



    public static void printSingleGraphForVarIndex(SasPlusProblem p, int varIndex, String outputfile){

        //Tuple2<Integer[],Tuple3[]> graphData = getSingleNodesAndEdgesForVarIndex(p,varIndex);

        EdgeLabelledGraph<Integer,Integer, HashMap<Integer, NodeValue>> graph = getSingleGraphForVarIndex(p,varIndex);

        //EdgeLabelledGraphSingle<String,String> stringGraph = convertSingleGraphToStringGraph(p, graph);

        Dot2PdfCompiler.writeDotToFile(graph,outputfile);


    }


    public static EdgeLabelledGraph<Integer,Integer, HashMap<Integer, NodeValue>> getSingleGraphForVarIndex(SasPlusProblem p, int varIndex){

        int firstIndex = p.firstIndex[varIndex];
        int lastIndex = p.lastIndex[varIndex];

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i=firstIndex; i<=lastIndex; i++){
            containedIndexes.add(i);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = getSingleEdgesForAllContainedIndexes(p,containedIndexes);


        Integer[] nodeIDs = new Integer[containedIndexes.size()];

        HashMap<Integer, NodeValue> idMapping = new HashMap<>();

        HashMap<Integer, Integer> tempReverseIDMapping = new HashMap<>();

        for (int i=0; i<containedIndexes.size(); i++){

            NodeValue nodeValue = new ElementaryNode(containedIndexes.get(i), p);
            tempReverseIDMapping.put(containedIndexes.get(i), i);

            nodeIDs[i] = i;

            idMapping.put(i, nodeValue);

        }

        Tuple3<Integer,Integer,Integer>[] multiEdges = Utils.convertEdgeArrayListToTuple3(convertSingleEdgesToMultiEdges(tempReverseIDMapping, edges));


        EdgeLabelledGraph<Integer,Integer, HashMap<Integer, NodeValue>> graph = new EdgeLabelledGraph<>(nodeIDs, multiEdges, idMapping);

        return graph;
    }


    public static ArrayList<Tuple3<Integer,Integer,Integer>> convertSingleEdgesToMultiEdges(HashMap<Integer, Integer> tempReverseIDMapping, ArrayList<Tuple3<Integer,Integer,Integer>> singleEdges){

        ArrayList<Tuple3<Integer,Integer,Integer>> multiEdges = new ArrayList<>();

        for (Tuple3<Integer,Integer,Integer> singleEdge : singleEdges){

            Tuple3<Integer,Integer,Integer> multiEdge = new Tuple3<>(tempReverseIDMapping.get(singleEdge._1()), singleEdge._2(), tempReverseIDMapping.get(singleEdge._3()));
            multiEdges.add(multiEdge);

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


}
