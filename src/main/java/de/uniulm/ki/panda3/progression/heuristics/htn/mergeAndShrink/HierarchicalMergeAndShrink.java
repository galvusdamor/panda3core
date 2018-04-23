package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.*;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.StratificationPlotter$;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler$;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.JavaConverters;


import java.util.*;


public class HierarchicalMergeAndShrink extends GroundedProgressionHeuristic {

    HashMap<Task, List<ProMethod>> methods;
    List<ProgressionPlanStep> initialTasks;
    ClassicalMSGraph combinedGraph;
    HashMap<Integer,Integer> distancesFromGoal;


    public HierarchicalMergeAndShrink(SasPlusProblem flatProblem, HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, Domain domain) {

        super();

        /*System.out.println(combinedGraph.idMapping.keySet());

        for(Tuple3<Integer,Integer,Integer> edge:combinedGraph.labelledEdges){

            System.out.println(edge.toString());

        }*/

        int shrinkingBound = 50;
        MergingStrategy classicalMergingStrategy = new MergingStrategy1();
        ShrinkingStrategy classicalShrinkingStrategy = new ShrinkingStrategy1();
        HtnShrinkingStrategy HtnShrinkingStrategy = new HtnShrinkingStrategy1();

        Task goalTask = ProgressionNetwork.indexToTask[9];

        combinedGraph = getCombinedGraph( flatProblem, methods, initialTasks, domain, goalTask, shrinkingBound,
                classicalMergingStrategy, classicalShrinkingStrategy, HtnShrinkingStrategy);

       distancesFromGoal =  ShrinkingStrategy.getDistancesFromGoal(flatProblem, combinedGraph);


        System.exit(0);

/*
        //Utils.printHtnGraph(flatProblem, presentGraphs.get(16), "Transport\\Graph16.pdf");

        System.exit(0);



        Testing.testGraphMinimization(flatProblem,methods,domain, shrinkingBound, HtnShrinkingStrategy);

        System.exit(0);









        //HashMap<Integer,HtnMsGraph> presentGraphs = getAllGraphs(flatProblem, methods, domain);



        //




        //Utils.printAllHtnGraphs(flatProblem, presentGraphs);






        System.exit(0);

        Task testTask = ProgressionNetwork.indexToTask[90];

        System.out.println("Test Task: " + testTask.longInfo());

        List<ProMethod> proMethods = methods.get(testTask);






        System.exit(0);*/

    }


    public ClassicalMSGraph getCombinedGraph(SasPlusProblem flatProblem, HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, Domain domain, Task goalTask,
                                             int classicalShrinkingBound, MergingStrategy classicalMergingStrategy, ShrinkingStrategy classicalShrinkingStrategy,
                                             HtnShrinkingStrategy htnShrinkingStrategy){



        this.methods = methods;
        this.initialTasks = initialTasks;


        ClassicalMergeAndShrink classicalMergeAndShrink = new ClassicalMergeAndShrink(flatProblem);
        ClassicalMSGraph classicalMSGraph = classicalMergeAndShrink.mergeAndShrinkProcess(flatProblem, classicalShrinkingBound, classicalMergingStrategy, classicalShrinkingStrategy);

        Utils.printMultiGraph(flatProblem, classicalMSGraph, "Transport\\ClassicalMSGraph.pdf");


        Task[] allTasks = ProgressionNetwork.indexToTask;

        for (int i=0; i<allTasks.length;i++) {

            Task t = allTasks[i];

            System.out.println("\tTask: " + i + ": " + t.shortInfo());
        }


        //var i = 0
        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();
        Dot2PdfCompiler$.MODULE$.writeDotToFile(layerGraph, "decomp_hierarchy0.pdf");

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            //System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {
                int taskIndex = ProgressionNetwork.taskToIndex.get(t);
                //System.out.println("\tTask: " + taskIndex + ": " + t.shortInfo());
                //System.out.println("\tTask: " + t + " Index: " + taskIndex);
                List<ProMethod> methodsForTask = ProgressionNetwork.methods.get(t);
                if (t.isAbstract()) {
                    for (ProMethod pm : methodsForTask) {
                        //System.out.println("\t\tMethod: " + pm.m.name());
                        DirectedGraph<PlanStep> methodGraph = pm.m.subPlan().orderingConstraints().fullGraph();
                        //System.out.println("\t\t" + methodGraph);

                        if ((pm.subtasks.length>1) &&(pm.orderings.size() == 0)) System.out.println("Task " + taskIndex);



                    }
                }
            }
        }

        StratificationPlotter$.MODULE$.plotStratification(domain);







        //Testing.testGraphs(flatProblem, methods, domain);

        //HashMap<Integer,HtnMsGraph> presentGraphs = Testing.getAllGraphs(flatProblem, methods, domain);
        int upperBound = 165;
        int shrinkingBound = 30;
        HashMap<Integer,HtnMsGraph> presentGraphs = Testing.getAllGraphs(flatProblem, methods, domain, shrinkingBound, htnShrinkingStrategy);




        Utils.printAllHtnGraphs(flatProblem, presentGraphs, "Transport");

        int goalTaskIndex = ProgressionNetwork.taskToIndex.get(goalTask);
        HtnMsGraph htnMsGraph = presentGraphs.get(goalTaskIndex);


        ClassicalMSGraph combinedGraph = OverlayOfClassicalAndHTNGraph.findWaysThroughBothGraphs(flatProblem, classicalMSGraph, htnMsGraph);

        Utils.printMultiGraph(flatProblem, combinedGraph, "Transport\\combinedGraph.pdf");

        return combinedGraph;


    }



    public int calcHeu(int[] state){

        int nodeID = combinedGraph.cascadingTables.getNodeID(state);

        if (nodeID==-1) return -1;

        int distanceFromGoal=distancesFromGoal.get(nodeID);

        return distanceFromGoal;
    }


    @Override
    public String getName() {
        return "M&S";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        return null;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        return null;
    }

    @Override
    public int getHeuristic() {
        return 0;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return false;
    }
}
