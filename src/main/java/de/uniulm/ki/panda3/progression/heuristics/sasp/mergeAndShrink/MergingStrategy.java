package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.MergeNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

/**
 * Created by biederma on 11.01.2018.
 */
abstract class MergingStrategy {



    abstract EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> merge(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, int shrinkingBound, long seed);



    public static HashSet<Integer> getNotYetUsedVariables(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

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


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> mergeWithVar(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph1, int varIndex, int shrinkingBound, ShrinkingStrategy shrinkingStrategy){

        if (varIndex >= p.numOfVars){

            System.out.println("Variable " + varIndex + " does not exist.");
            return graph1;
        }


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, varIndex);


        int sizeOfGraph1 = graph1.idMapping().keySet().size();
        int sizeOfGraph2 = graph2.idMapping().keySet().size();

        //System.out.println("Shrinking Bound: " + shrinkingBound);

        int sizeOfNewGraph = sizeOfGraph1*sizeOfGraph2;

        while (sizeOfNewGraph>shrinkingBound){




            //System.out.println("Size of new Graph: " + sizeOfNewGraph);

            //System.out.println("Size of Graph 1: " + sizeOfGraph1);
            //System.out.println("Size of Graph 2: " + sizeOfGraph2);




            if (sizeOfGraph2>sizeOfGraph1){
                graph2 = shrinkingStrategy.shrink(p, graph2);
            }else {
                graph1 = shrinkingStrategy.shrink(p, graph1);
            }

            sizeOfGraph1 = graph1.idMapping().keySet().size();
            sizeOfGraph2 = graph2.idMapping().keySet().size();

            sizeOfNewGraph = sizeOfGraph1*sizeOfGraph2;


        }


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> newGraph = mergingStep(p, graph1, graph2);

        newGraph = dismissNotReachableNodes(newGraph);

        return newGraph;


    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> dismissNotReachableNodes(EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

        HashMap<Integer, Integer> cascadingTable = new HashMap<>();

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


        HashMap<Integer, NodeValue> dismissedIDMapping = new HashMap<>();

        for (int i=0; i<nodesToKeep.size(); i++){
            int id = nodesToKeep.get(i);
            cascadingTable.put(id, i);
            dismissedIDMapping.put(i,graph.idMapping().get(id));
        }
        for (int i: nodesToDismiss){
            cascadingTable.put(i, -1);
        }

        Tuple3<Integer,Integer,Integer>[] dismissedEdges = dismissEdgesOfNotReachableNodes(edges, nodesToDismiss, cascadingTable);


        HashSet<Integer> usedFactIndexes = new HashSet<>(graph.usedFactIndexes());

        HashSet<Integer> usedVariables = new HashSet<>(graph.usedVariables());

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph.notYetUsedVariables());


        CascadingTables cascadingTables = graph.cascadingTables();

        int indexOfTableBeforeShrinking = cascadingTables.cascadingTables.size()-1;

        cascadingTables.addNewShrinkTable(indexOfTableBeforeShrinking,cascadingTable);


        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> newGraph = new EdgeLabelledGraph<>(Utils.convertNodeIDArrayListToArray(dismissedIDMapping), dismissedEdges, dismissedIDMapping, cascadingTable.get(graph.startNodeID()), usedFactIndexes, usedVariables, notYetUsedVariables, graph.allVariables(), cascadingTables);



        return newGraph;
    }

    public Tuple3<Integer,Integer,Integer>[] dismissEdgesOfNotReachableNodes(Tuple3<Integer,Integer,Integer>[] oldEdges, ArrayList<Integer> nodesToDismiss, HashMap<Integer, Integer> cascadingTable){

        ArrayList<Tuple3<Integer,Integer,Integer>> newEdgeArrayList = new ArrayList<>();

        for(Tuple3<Integer,Integer,Integer> edge : oldEdges){
            if ((!nodesToDismiss.contains(edge._1())) && (!nodesToDismiss.contains(edge._3()))){
                int newStartEdge = cascadingTable.get(edge._1());
                int newEndEdge = cascadingTable.get(edge._3());
                Tuple3<Integer,Integer,Integer> newEdge = new Tuple3<>(newStartEdge, edge._2(), newEndEdge);
                newEdgeArrayList.add(newEdge);
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

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> mergingStep(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph1, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph2) {

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

        CascadingTables cascadingTables1 = graph1.cascadingTables();
        CascadingTables cascadingTables2 = graph2.cascadingTables();

        int correctionTerm = cascadingTables1.cascadingTables.size();

        int mergeIndex1 = cascadingTables1.cascadingTables.size()-1;
        int mergeIndex2 = correctionTerm+cascadingTables2.cascadingTables.size()-1;



        int index = cascadingTables1.cascadingTables.size();

        for (CascadingTable table : cascadingTables2.cascadingTables){
            //add to cascadingTables1
            //update index
            //update indexes of indexBeforeShrinking, etc.
            if (table instanceof VariableTable){

                table.index = index;
                index++;
                cascadingTables1.cascadingTables.add(table);

            }
            if (table instanceof ShrinkTable){

                table.index = index;
                index++;
                ShrinkTable shrinkTable = ((ShrinkTable) table);
                int oldIndexOfTableBeforeShrinking = shrinkTable.indexOfTableBeforeShrinking;
                int newIndexOfTableBeforeShrinking = oldIndexOfTableBeforeShrinking + correctionTerm;
                shrinkTable.indexOfTableBeforeShrinking = newIndexOfTableBeforeShrinking;
                cascadingTables1.cascadingTables.add(shrinkTable);

            }
            if (table instanceof MergeTable){

                table.index = index;
                index++;
                MergeTable mergeTable = ((MergeTable) table);
                int oldMergeIndex1 = mergeTable.mergeIndex1;
                int newMergeIndex1 = oldMergeIndex1 + correctionTerm;
                int oldMergeIndex2 = mergeTable.mergeIndex2;
                int newMergeIndex2 = oldMergeIndex2 + correctionTerm;
                mergeTable.mergeIndex1 = newMergeIndex1;
                mergeTable.mergeIndex2 = newMergeIndex2;
                cascadingTables1.cascadingTables.add(mergeTable);

            }
        }

        //add new merge table to cascadingTables1
        int[][] mergeCascadingTable = new int[graph1.idMapping().size()][graph1.idMapping().size()];
        for (int i=0; i<graph1.idMapping().size(); i++){

            for (int j=0; j<graph2.idMapping().size(); j++){

                Tuple2<Integer, Integer> combination = new Tuple2<>(i,j);
                int result = tempReverseIdMapping.get(combination);
                mergeCascadingTable[i][j] = result;
            }

        }

        cascadingTables1.addNewMergeTable(mergeIndex1, mergeIndex2, mergeCascadingTable);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> newGraph = new EdgeLabelledGraph<>(nodeIDS, newEdgeTuple, newIdMapping, newStartID, usedFactIndexes, usedVariables, notYetUsedVariables, graph1.allVariables(), cascadingTables1);


        return newGraph;

    }


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getOpIDToEdgesMap(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph){

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

}







class MergingStrategy1 extends MergingStrategy{


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> merge(SasPlusProblem p, EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph, int shrinkingBound, long seed){


        Set<Integer> notYetUsedVariables = getNotYetUsedVariables(p, graph);

        ArrayList<Integer> variablesToMerge = new ArrayList<>(notYetUsedVariables);

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> newGraph;

        if (!(graph==null)) {

            //System.out.println("Not yet used Variables: " + notYetUsedVariables);

            int randomVarIndex = Utils.randomIntGenerator(notYetUsedVariables.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            System.out.println("Merge with Variable: " + randomVar);


            newGraph = mergeWithVar(p, graph, randomVar, shrinkingBound, new ShrinkingStrategy1());


        }else{

            int randomVarIndex = Utils.randomIntGenerator(notYetUsedVariables.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            System.out.println("Start with Variable: " + randomVar);

            newGraph = SingleGraphMethods.getSingleGraphForVarIndex(p, randomVar);

        }

        return newGraph;


    }



}
