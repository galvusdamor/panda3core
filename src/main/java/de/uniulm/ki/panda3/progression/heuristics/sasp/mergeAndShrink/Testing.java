package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;

import java.util.HashMap;
import java.util.Set;

public final class Testing {


    public static ClassicalMSGraph getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID) {

        ClassicalMSGraph graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            MergingStrategy mergingStrategy = new MergingStrategy1();

            graph1 = mergingStrategy.mergeWithVar(p, graph1, i, 1000000000, new ShrinkingStrategy1());

        }

        return graph1;

    }

    public static void printSingleGraphForVarIndex(SasPlusProblem p, int varIndex, String outputfile){


        SingleGraphMethods.printSingleGraphForVarIndex(p, varIndex, outputfile);


    }

    public static ClassicalMSGraph getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID, int shrinkingBound, MergingStrategy mergingStrategy, ShrinkingStrategy shrinkingStrategy) {

        ClassicalMSGraph graph1 = SingleGraphMethods.getSingleGraphForVarIndex(p,0);

        //EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>> graph2;

        for (int i = 1; ((i <= lastVarID) && (i < p.numOfVars)); i++) {

            //graph2 = SingleGraphMethods.getSingleGraphForVarIndex(p, i);

            //graph1 = mergingStep(p, graph1, graph2);

            graph1 = mergingStrategy.mergeWithVar(p, graph1, i, shrinkingBound, shrinkingStrategy);

        }

        return graph1;

    }

    public static void printNiceGraphs(SasPlusProblem p){

        printSingleGraphForVarIndex(p, 0, "Graphpics\\var0.pdf");
        printSingleGraphForVarIndex(p, 1, "Graphpics\\var1.pdf");
        printSingleGraphForVarIndex(p, 2, "Graphpics\\var2.pdf");



        MergingStrategy mergingStrategy = new MergingStrategy1();
        ShrinkingStrategy shrinkingStrategy = new ShrinkingStrategy1();

        int lastVarID = 1;

        ClassicalMSGraph graph = getMultiGraphUntilVarID(p, lastVarID, 100, mergingStrategy, shrinkingStrategy);

        String outputfile = "Graphpics\\graphUntilVar" + lastVarID + ".pdf";

        Utils.printMultiGraph(p, graph, outputfile);

    }








}
