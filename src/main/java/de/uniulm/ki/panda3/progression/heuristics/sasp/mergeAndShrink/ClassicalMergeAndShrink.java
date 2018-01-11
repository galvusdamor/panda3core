package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;

import scala.Tuple2;
import scala.Tuple3;


import java.util.*;


public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {



        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> testGraph = mergeAndShrinkProcess(p, 20);

        Utils.printMultiGraph(p, testGraph, "graph6.pdf");



        int[] startstate = p.s0List;

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







        System.exit(0);






    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            MergingStrategy mergingStrategy = new MergingStrategy1();

            graph1 = mergingStrategy.mergeWithVar(p, graph1, i, 1000000000, new ShrinkingStrategy1());

        }

        return graph1;

    }








    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> mergeAndShrinkProcess(SasPlusProblem p, int shrinkingBound){


        MergingStrategy mergingStrategy = new MergingStrategy1();

        EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, Set<Integer>, Set<Integer>, Set<Integer>, Set<Integer>, CascadingTables> graph = mergingStrategy.merge(p, null, shrinkingBound, 0);

        int counter = 0;

        while (graph.notYetUsedVariables().size()!=0){


            /*while (graph.idMapping().keySet().size()>shrinkingBound){
                graph = shrinkingStrategy1(p, graph);
            }*/

            graph = mergingStrategy.merge(p, graph,shrinkingBound, 0);

            counter++;

            String name = "testGraph" + counter + ".pdf";

            Utils.printMultiGraph(p, graph, name);
        }

        /*while (graph.idMapping().keySet().size()>shrinkingBound){
            graph = shrinkingStrategy1(p, graph);
        }*/


        return graph;

    }





}
