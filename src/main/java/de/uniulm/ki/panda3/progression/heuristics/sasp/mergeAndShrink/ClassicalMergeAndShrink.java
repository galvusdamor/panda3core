package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;

import scala.Tuple2;
import scala.Tuple3;


import java.io.IOException;
import java.util.*;


public class ClassicalMergeAndShrink extends SasHeuristic {

    private SasPlusProblem p;

    public ClassicalMergeAndShrink(SasPlusProblem p) {
        this.p = p;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {

        Testing.printNiceGraphs(p);


        System.exit(0);


        int[] oldS0 = p.s0List;
        int[] oldG = p.gList;
        int[] newS0 = new int[s0.cardinality()];
        int j = 0;
        for(int i = s0.nextSetBit(0); i >= 0 ; i = s0.nextSetBit(i+1))
            newS0[j++] = i;
        p.s0Bitset = null;
        p.s0List = newS0;
        p.getS0();


        int[] newG = new int[g.cardinality()];
        j = 0;
        for(int i = g.nextSetBit(0); i >= 0 ; i = g.nextSetBit(i+1))
            newG[j++] = i;
        p.gList = newG;

        System.out.println(g);
        //System.exit(0);
        System.out.println(p.correctModel());


             ClassicalMSGraph testGraph =
                mergeAndShrinkProcess(p, 5000);

        //Utils.printMultiGraph(p, testGraph, "graph6.pdf");



        /*int[] startstate = p.s0List;

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

        System.out.println("Node ID: " + testGraph.cascadingTables().getNodeID(queryState));
        */

        HashMap<Integer,Integer> distancesFromClosestGoalNode = ShrinkingStrategy.getDistancesFromGoal(p, testGraph);
        //System.out.println(testGraph.startNodeID);
        System.out.println(distancesFromClosestGoalNode.get(testGraph.startNodeID));

        NodeValue val = testGraph.idMapping.get(testGraph.startNodeID);
        //System.out.println(val.longInfo());




        //System.exit(0);

        p.s0List = oldS0;
        p.s0Bitset = null;
        p.gList = oldG;



        return distancesFromClosestGoalNode.get(testGraph.startNodeID);
    }










    public ClassicalMSGraph mergeAndShrinkProcess(SasPlusProblem p, int shrinkingBound){


        MergingStrategy mergingStrategy = new MergingStrategy1();

        ClassicalMSGraph graph = mergingStrategy.merge(p, null, shrinkingBound, 0);

        int counter = 0;

        while (graph.notYetUsedVariables.size()!=0){


            /*while (graph.idMapping.keySet().size()>shrinkingBound){
                graph = shrinkingStrategy1(p, graph);
            }*/

            //System.out.println("MERGE step " + counter);
            graph = mergingStrategy.merge(p, graph,shrinkingBound, 0);

            counter++;

            String name = "testGraph" + counter + ".pdf";

            //Utils.printMultiGraph(p, graph, name);
            NodeValue superNode = graph.idMapping.get(1);
            //System.out.println("START is " + graph.startNodeID);
            HashMap<Integer,Integer> distancesFromClosestGoalNode = ShrinkingStrategy.getDistancesFromGoal(p, graph);
            //System.out.println(distancesFromClosestGoalNode.get(graph.startNodeID));
            //System.out.println();
            //System.out.println();
            //System.out.println();
        }
        //Utils.printMultiGraph(p, graph, "lastgraph.pdf");

        /*while (graph.idMapping.keySet().size()>shrinkingBound){
            graph = shrinkingStrategy1(p, graph);
        }*/


        return graph;

    }





}
