package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.*;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler$;
import scala.Tuple3;
import scala.collection.JavaConverters;


import java.util.*;


public class HierarchicalMergeAndShrink extends GroundedProgressionHeuristic {

    HashMap<Task, List<ProMethod>> methods;
    List<ProgressionPlanStep> initialTasks;
    ClassicalMSGraph classicalCombinedGraph;
    HtnMsGraph HtnCombinedGraph;
    HashMap<Integer, Integer> distancesFromGoal;
    boolean withMethods;
    LinkedList<ProMethod> steps;


    public HierarchicalMergeAndShrink(SasPlusProblem flatProblem, HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, Domain domain) {

        super();

        /*System.out.println(classicalCombinedGraph.idMapping.keySet());

        for(Tuple3<Integer,Integer,Integer> edge:classicalCombinedGraph.labelledEdges){

            System.out.println(edge.toString());

        }*/

        int shrinkingBound = 100;
        MergingStrategy classicalMergingStrategy = new MergingStrategy1();
        ShrinkingStrategy classicalShrinkingStrategy = new ShrinkingStrategy1();
        HtnShrinkingStrategy HtnShrinkingStrategy = new HtnShrinkingStrategy1();
        boolean withMethods = true;
        this.withMethods=withMethods;

        assert initialTasks.size() == 1;

        Task goalTask = initialTasks.get(0).getTask();


        if(withMethods==true){

            steps = new LinkedList<>();

            classicalCombinedGraph = getCombinedGraph(flatProblem, methods, initialTasks, domain, goalTask, shrinkingBound,
                    classicalMergingStrategy, classicalShrinkingStrategy, HtnShrinkingStrategy, withMethods);

            distancesFromGoal = ShrinkingStrategy.getDistancesFromGoal(flatProblem, classicalCombinedGraph);

        }else {

            steps = new LinkedList<>();

            classicalCombinedGraph = getCombinedGraph(flatProblem, methods, initialTasks, domain, goalTask, shrinkingBound,
                    classicalMergingStrategy, classicalShrinkingStrategy, HtnShrinkingStrategy, withMethods);

            distancesFromGoal = ShrinkingStrategy.getDistancesFromGoal(flatProblem, classicalCombinedGraph);
        }


       /* int[] testState = new int[3];
        testState[0] = 1;
        testState[1] = 3;
        testState[2] = 7;


        int[] state = testState;
        //int[] state = flatProblem.s0List;

        int heuristicValue = calcHeu(state);*/



        //System.exit(0);

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
                                             int shrinkingBound, MergingStrategy classicalMergingStrategy, ShrinkingStrategy classicalShrinkingStrategy,
                                             HtnShrinkingStrategy htnShrinkingStrategy, boolean withVariables) {


        this.methods = methods;
        this.initialTasks = initialTasks;


        ClassicalMergeAndShrink classicalMergeAndShrink = new ClassicalMergeAndShrink(flatProblem);
        ClassicalMSGraph classicalMSGraph = classicalMergeAndShrink.mergeAndShrinkProcess(flatProblem, shrinkingBound, classicalMergingStrategy, classicalShrinkingStrategy);

        //Utils.printMultiGraph(flatProblem, classicalMSGraph, "Transport\\ClassicalMSGraph.pdf");


        Task[] allTasks = ProgressionNetwork.indexToTask;

        for (int i = 0; i < allTasks.length; i++) {

            Task t = allTasks[i];

            System.out.println("\tTask: " + i + ": " + t.shortInfo());
        }


        //var i = 0
        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();
        Dot2PdfCompiler$.MODULE$.writeDotToFile(layerGraph, "decomp_hierarchy1.pdf");

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

                        //if ((pm.subtasks.length > 1) && (pm.orderings.size() == 0)) System.out.println("Task " + taskIndex);


                    }
                }
            }
        }

        //StratificationPlotter$.MODULE$.plotStratification(domain);


        //Testing.testGraphs(flatProblem, methods, domain);

        //HashMap<Integer,HtnMsGraph> presentGraphs = Testing.getAllGraphs(flatProblem, methods, domain);
        //int upperBound = 165;
        //int shrinkingBound = 30;
        HashMap<Integer, HtnMsGraph> presentGraphs = Testing.getAllGraphs(flatProblem, methods, domain, shrinkingBound, htnShrinkingStrategy, withVariables);


        Utils.printAllHtnGraphs(flatProblem, presentGraphs, "Transport");

        int goalTaskIndex = ProgressionNetwork.taskToIndex.get(goalTask);
        int testIndex = 16;


        //HtnMsGraph htnMsGraph = presentGraphs.get(goalTaskIndex);

        HtnMsGraph htnMsGraph = presentGraphs.get(testIndex);

        System.out.println("Task: " + ProgressionNetwork.indexToTask[testIndex]);

        Utils.printHtnGraph(flatProblem,htnMsGraph,"Transport\\TestTask.pdf");

        System.out.println("HashMap: ");

        HtnMsGraphWithMethods htnMsGraphWithMethods = ((HtnMsGraphWithMethods) htnMsGraph);

        for(Tuple3<Integer,Integer,Integer> edge : htnMsGraphWithMethods.linkedMethods.keySet()){

            System.out.println("Edge:" + edge);

            LinkedList<ProMethod> proMethods = htnMsGraphWithMethods.linkedMethods.get(edge);

            for(ProMethod proMethod: proMethods){

                System.out.println("Promethod:" + proMethod.m.name());

            }

        }

        System.exit(0);


        //System.out.println("Test");


        ClassicalMSGraph combinedGraph = OverlayOfClassicalAndHTNGraph.findWaysThroughBothGraphs(flatProblem, classicalMSGraph, htnMsGraph);

        //System.out.println("Size: " + combinedGraph.idMapping.size());

        //Utils.printMultiGraph(flatProblem, classicalCombinedGraph, "D:\\IdeaProjects\\panda3core\\classicalCombinedGraph.pdf");

        return combinedGraph;


    }


    public int calcHeu(int[] state) {


        /*int index = 0;
        for (int i : state) {
            System.out.println("Variable " + index + ": " + i);
            index++;
        }*/
        int distanceFromGoal;


        if(withMethods==true){

            int nodeID = classicalCombinedGraph.cascadingTables.getNodeID(state);

            //System.out.println(nodeID);

            if (nodeID == -1) return -1;


            distanceFromGoal = distancesFromGoal.get(nodeID);

        }else {
            int nodeID = classicalCombinedGraph.cascadingTables.getNodeID(state);

            //System.out.println(nodeID);

            if (nodeID == -1) return -1;


            distanceFromGoal = distancesFromGoal.get(nodeID);

        }

        //System.out.println("Node : " + nodeID);
        //System.out.println("Heuristic Value: " + distanceFromGoal);

        return distanceFromGoal;
    }


    @Override
    public String getName() {
        return "M&S";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }


    private int currentHeuristicValue = 0;

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        //System.out.println("ProMethod: " + m);
        steps.addLast(m);
        //System.out.println("Steps Size: " + steps.size());

        /*if(steps.size()==39){
            System.out.println(steps);
        }*/

        return update(newTN, ps);
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        BitSet bs = newTN.state;
        int[] arrayState = new int[bs.size()];

        int i = 0;
        int j = 0;
        while ((i = bs.nextSetBit(i)) != -1) {
            arrayState[j++] = i++;
        }

        currentHeuristicValue = calcHeu(arrayState);


        return this;
    }

    @Override
    public int getHeuristic() {
        return currentHeuristicValue;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return currentHeuristicValue != -1;
    }
}
