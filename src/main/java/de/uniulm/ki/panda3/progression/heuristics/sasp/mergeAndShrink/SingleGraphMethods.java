package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Boolean;
import scala.Int;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.*;
import scala.collection.JavaConversions;

import javax.rmi.CORBA.Util;


/**
 * Created by biederma on 05.10.2017.
 */
public final class SingleGraphMethods {



    public static void printSingleGraphForVarIndex(SasPlusProblem p, int varIndex, String outputfile){

        //Tuple2<Integer[],Tuple3[]> graphData = getSingleNodesAndEdgesForVarIndex(p,varIndex);

        ClassicalMSGraph graph = getSingleGraphForVarIndex(p,varIndex);

        //EdgeLabelledGraphSingle<String,String> stringGraph = convertSingleGraphToStringGraph(p, graph);

        //Dot2PdfCompiler.writeDotToFile(graph.graph,outputfile);

        Utils.printMultiGraph(p,graph,outputfile);


    }


    public static ClassicalMSGraph getSingleGraphForVarIndex(SasPlusProblem p, int varIndex){

        int firstIndex = p.firstIndex[varIndex];
        int lastIndex = p.lastIndex[varIndex];

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i=firstIndex; i<=lastIndex; i++){
            containedIndexes.add(i);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = getSingleEdgesForAllContainedIndexes(p,containedIndexes);


        Integer[] nodeIDs = new Integer[containedIndexes.size()];

        HashMap<Integer, NodeValue> idMapping = new HashMap<>();

        HashMap<Integer, Integer> cascadingTable = new HashMap<>();

        int[] goals = p.gList;

        ArrayList<Integer> dismissedGoals = Utils.dismissNotContainedIndexes(goals, containedIndexes);


        for (int i=0; i<containedIndexes.size(); i++){

            int factID = containedIndexes.get(i);

            java.lang.Boolean isGoalNode;

            if (dismissedGoals.size()==0){

                isGoalNode = true;

            }else {
                isGoalNode = dismissedGoals.contains(factID);
            }

            NodeValue nodeValue = new ElementaryNode(factID, p, isGoalNode);
            cascadingTable.put(factID, i);

            nodeIDs[i] = i;

            idMapping.put(i, nodeValue);



        }

        Tuple3<Integer,Integer,Integer>[] multiEdges = Utils.convertEdgeArrayListToTuple3(convertSingleEdgesToMultiEdges(cascadingTable, edges));

        int startID = findStartNodeIDinSingleVarGraph(p, idMapping, containedIndexes);

        HashSet<Integer> usedFactIndexes = new HashSet<>(containedIndexes);

        HashSet<Integer> usedVariables = new HashSet<>();

        usedVariables.add(varIndex);

        HashSet<Integer> allVariables = new HashSet<>();

        for (int i = 0; i < p.numOfVars; i++) {
            allVariables.add(i);
        }

        HashSet<Integer> notYetUsedVariables = new HashSet<>(allVariables);
        notYetUsedVariables.remove(varIndex);

        CascadingTables cascadingTables = new CascadingTables();

        cascadingTables.addNewVariableTable(varIndex,cascadingTable);


        HashSet<Integer> goalVariables = getGoalVariables(p);



        ClassicalMSGraph graph = new ClassicalMSGraph(nodeIDs, multiEdges, idMapping, startID, usedFactIndexes, usedVariables, notYetUsedVariables, goalVariables, allVariables, cascadingTables);

        return graph;
    }


    public static HashSet<Integer> getGoalVariables(SasPlusProblem p){

        int[] goals = p.gList;

        HashSet<Integer> goalVariables = new HashSet<>();

        HashMap<Integer,Integer> mapping = new HashMap<>();

        for (int i=0; i<p.numOfVars; i++){
            int firstIndex = p.firstIndex[i];
            int lastIndex = p.lastIndex[i];
            for (int j=firstIndex; j<lastIndex; j++){
                mapping.put(j,i);
            }
        }


        for (int goal:goals){
            goalVariables.add(mapping.get(goal));
        }

        return goalVariables;
    }



    public static int findStartNodeIDinSingleVarGraph(SasPlusProblem p, HashMap<Integer, NodeValue> idMapping, ArrayList<Integer> containedIndexes){

        int[] s0 = p.s0List;

        ArrayList<Integer> s0Dismissed = Utils.dismissNotContainedIndexes(s0, containedIndexes);

        if (s0Dismissed.size()>1) System.out.println("The start state of this variable is not valid");
        if (s0Dismissed.size()<1) System.out.println("The start state of this variable is not existant");

        for (int id : idMapping.keySet()){

            NodeValue nodeValue = idMapping.get(id);

            if (nodeValue instanceof ElementaryNode){
                if (((ElementaryNode) nodeValue).value() == s0Dismissed.get(0)){
                    return id;
                }
            }
        }


        return -1;
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

            if (preListDismissed.size()==0) {

                for (int index : containedVarIndexes) {

                    Tuple3<Integer, Integer, Integer> edge = new Tuple3<>(index, OpIndex, index);
                    if (!Utils.containsEdge(edges, edge)) edges.add(edge);
                }
            }else{
                Tuple3<Integer, Integer, Integer> edge = new Tuple3<>(preListDismissed.get(0), OpIndex, preListDismissed.get(0));
                if (!Utils.containsEdge(edges, edge)) edges.add(edge);
            }

        }


        return edges;

    }

    public static String getOpString(SasPlusProblem p, int OpIndex){

        String s = "\"" + OpIndex + ": " + p.opNames[OpIndex] +  "\"";

        return s;

    }


}
