package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.*;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.ClassicalNodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.MergeNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

public final class HTNandClassicalMerging {

    public static ClassicalMSGraph mergeHtnWithClassicalMSGraph(HtnMsGraph htnMsGraph, ClassicalMSGraph classicalMSGraph){








        return null;
    }



    public ClassicalMSGraph mergingStep(SasPlusProblem p, ClassicalMSGraph graph1, ClassicalMSGraph graph2) {

        Integer[] graph1Nodes = (Integer[]) graph1.arrayVertices;


        Integer[] graph2Nodes = (Integer[]) graph2.arrayVertices;


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

            //ArrayList<Integer> assignedFacts = new ArrayList<>(graph1.idMapping.get(oldGraph1ID));

            //if (!assignedFacts.contains(oldGraph2ID)) assignedFacts.add(oldGraph2ID);

            NodeValue newNodeValue1 = graph1.idMapping.get(oldGraph1ID);
            NodeValue newNodeValue2 = graph2.idMapping.get(oldGraph2ID);

            if ((newNodeValue1 instanceof ClassicalNodeValue) && (newNodeValue2 instanceof ClassicalNodeValue))  {
                ClassicalNodeValue newNodeValue12 = (ClassicalNodeValue) newNodeValue1;
                ClassicalNodeValue newNodeValue22 = (ClassicalNodeValue) newNodeValue2;
                NodeValue newNodeValue = new MergeNode(newNodeValue12, newNodeValue22, p);
                newIdMapping.put(i, newNodeValue);
            }else{
                System.out.println("Wrong type!!");
                System.exit(1);
            }





        }

        Tuple3<Integer, Integer, Integer>[] newEdgeTuple = Utils.convertEdgeArrayListToTuple3(newMultiEdges);

        Integer[] nodeIDS = Utils.convertNodeIDArrayListToArray(newIdMapping);

        Tuple2<Integer, Integer> oldStartIDs = new Tuple2<>(graph1.startNodeID, graph2.startNodeID);

        int newStartID = tempReverseIdMapping.get(oldStartIDs);

        HashSet<Integer> usedFactIndexes = new HashSet<>(graph1.usedFactIndexes);

        usedFactIndexes.addAll(graph2.usedFactIndexes);


        HashSet<Integer> usedVariables = new HashSet<>(graph1.usedVariables);

        usedVariables.addAll(graph2.usedVariables);

        HashSet<Integer> notYetUsedVariables = new HashSet<>(graph1.allVariables);

        notYetUsedVariables.removeAll(usedVariables);

        CascadingTables cascadingTables1 = graph1.cascadingTables;
        CascadingTables cascadingTables2 = graph2.cascadingTables;

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
        int[][] mergeCascadingTable = new int[graph1.idMapping.size()][graph2.idMapping.size()];
        for (int i=0; i<graph1.idMapping.size(); i++){

            for (int j=0; j<graph2.idMapping.size(); j++){

                Tuple2<Integer, Integer> combination = new Tuple2<>(i,j);
                int result = tempReverseIdMapping.get(combination);
                mergeCascadingTable[i][j] = result;
            }

        }

        cascadingTables1.addNewMergeTable(mergeIndex1, mergeIndex2, mergeCascadingTable);

        ClassicalMSGraph newGraph =
                new ClassicalMSGraph(nodeIDS, newEdgeTuple, newIdMapping, newStartID, usedFactIndexes, usedVariables, notYetUsedVariables, graph1.goalVariables, graph1.allVariables, cascadingTables1);


        return newGraph;

    }


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getOpIDToEdgesMap(SasPlusProblem p, ClassicalMSGraph graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> opIDToOutgoingEdgesMap = new HashMap<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            opIDToOutgoingEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges){

            opIDToOutgoingEdgesMap.get(edge._2()).add(edge);

        }

        return opIDToOutgoingEdgesMap;

    }



}
