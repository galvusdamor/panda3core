package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.util.DirectedGraph;
import scala.collection.JavaConverters;

import java.util.*;

public class Testing {



    public static void testGraphMinimization(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, Domain domain, int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy, boolean withVariables){


        HashMap<Integer,HtnMsGraph> presentGraphs = Testing.getAllGraphs(p, methods, domain, shrinkingBound, shrinkingStrategy, withVariables);

        HtnMsGraph testGraph = presentGraphs.get(16);

        HtnMsGraph testMinimizedGraph = GraphMinimation.minimizeGraph(p, testGraph);

        Utils.printHtnGraph(p, testMinimizedGraph, "MinimizedGraph.pdf");


        //Utils.printHtnGraph(p, testHtnGraph, "testHtnGraph.pdf");
    }



    public static HashMap<Integer,HtnMsGraph> getAllGraphs(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, Domain domain, int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy, boolean withVariables){

        HashMap<Integer,HtnMsGraph> presentGraphs = new HashMap<>();

        Map<Task, Integer> taskToIndexMapping = ProgressionNetwork.taskToIndex;

        int index =0;

        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            //System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {

                index++;

                int taskIndex = taskToIndexMapping.get(t);

                //System.out.println(index + ": Handle Task " + taskIndex);

                presentGraphs = HtnMerging.getHtnMsGraphForTaskIndex(p, methods, taskIndex, presentGraphs, shrinkingBound, shrinkingStrategy, withVariables);

                //System.out.println("Tasks in present Tasks: " + presentGraphs.keySet());

            }
        }

        return presentGraphs;

    }



    public static void testGraphs(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, Domain domain, int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy, boolean withVariables){


        HashMap<Integer,HtnMsGraph> presentGraphs = new HashMap<>();

        ArrayList<Integer> testIndexes = new ArrayList<>();

        testIndexes.add(30);
        testIndexes.add(32);
        testIndexes.add(33);
        testIndexes.add(35);
        testIndexes.add(109);
        testIndexes.add(105);


        for (int testTaskIndex : testIndexes) {

            presentGraphs = HtnMerging.getHtnMsGraphForTaskIndex(p, methods, testTaskIndex, presentGraphs, shrinkingBound, shrinkingStrategy, withVariables);

        }


        Utils.printAllHtnGraphs(p, presentGraphs, "Misc");

        HtnMsGraph testHtnGraph = HtnMerging.mergeGraphs(presentGraphs.get(109), presentGraphs.get(105), p);

        Utils.printHtnGraph(p, testHtnGraph, "testHtnGraph.pdf");



    }




    public static HashMap<Integer,HtnMsGraph> getxGraphs(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, Domain domain, int x,  int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy, boolean withVariables){

        HashMap<Integer,HtnMsGraph> presentGraphs = new HashMap<>();

        Map<Task, Integer> taskToIndexMapping = ProgressionNetwork.taskToIndex;

        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        int index =0;


        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            //System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {

                index++;

                int taskIndex = taskToIndexMapping.get(t);

                System.out.println(index + ": Handle Task " + taskIndex);

                //System.out.println("isPrimitive " + t.isPrimitive());

                presentGraphs = HtnMerging.getHtnMsGraphForTaskIndex(p, methods, taskIndex, presentGraphs, shrinkingBound, shrinkingStrategy, withVariables);

                //System.out.println("Tasks in present Tasks: " + presentGraphs.keySet());




                if (index>=x) break;

            }

            if (index>=x) break;
        }

        return presentGraphs;

    }

}
