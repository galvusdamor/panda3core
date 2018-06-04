package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.RelaxedCompositionEncoding;
import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.RelaxedCompositionSAS;
import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.RelaxedCompositionSTRIPS;
import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.gphRelaxedComposition;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hAddhFFEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
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

    private final hAddhFFEq heuristic;
    private RelaxedCompositionEncoding compEnc;
    HashMap<Task, List<ProMethod>> methods;
    List<ProgressionPlanStep> initialTasks;
    ClassicalMSGraph classicalCombinedGraph;
    HtnMsGraph HtnCombinedGraph;
    HashMap<Integer, Integer> distancesFromGoal;
    boolean withMethods;
    LinkedList<ProMethod> steps;


    public HierarchicalMergeAndShrink(SasPlusProblem flatProblem, HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, Domain domain,
                                      boolean filterWithADD) {

        super();


        if (filterWithADD) {
            if (flatProblem.createdFromStrips)
                this.compEnc = new RelaxedCompositionSTRIPS(flatProblem);
            else
                this.compEnc = new RelaxedCompositionSAS(flatProblem);

            if (this.compEnc.methodCosts == 0)
                System.out.println("Using methodcosts = 0. This will be slow, but optimal ...");

            this.compEnc.generateTaskCompGraph(methods, initialTasks);
            System.out.println("Generating Relaxed Composition Model ...");
            System.out.println(this.compEnc.getStatistics());

            this.heuristic = new hAddhFFEq(this.compEnc, SasHeuristic.SasHeuristics.hAdd);
        } else this.heuristic = null;


        /*System.out.println(classicalCombinedGraph.idMapping.keySet());

        for(Tuple3<Integer,Integer,Integer> edge:classicalCombinedGraph.labelledEdges){

            System.out.println(edge.toString());

        }*/

        int shrinkingBound = 100;
        MergingStrategy classicalMergingStrategy = new MergingStrategy1();
        ShrinkingStrategy classicalShrinkingStrategy = new ShrinkingStrategy1();
        HtnShrinkingStrategy HtnShrinkingStrategy = new HtnShrinkingStrategy1();
        boolean withMethods = false;
        this.withMethods = withMethods;

        assert initialTasks.size() == 1;

        Task goalTask = initialTasks.get(0).getTask();


        if (withMethods == true) {

            steps = new LinkedList<>();

            classicalCombinedGraph = getCombinedGraph(flatProblem, methods, initialTasks, domain, goalTask, shrinkingBound,
                    classicalMergingStrategy, classicalShrinkingStrategy, HtnShrinkingStrategy, withMethods);

            distancesFromGoal = ShrinkingStrategy.getDistancesFromGoal(flatProblem, classicalCombinedGraph);

        } else {

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


        //Utils.printAllHtnGraphs(flatProblem, presentGraphs, "Transport");

        int goalTaskIndex = ProgressionNetwork.taskToIndex.get(goalTask);
        int testIndex = goalTaskIndex;


        //HtnMsGraph htnMsGraph = presentGraphs.get(goalTaskIndex);

        HtnMsGraph htnMsGraph = presentGraphs.get(testIndex);

        System.out.println("Task: " + ProgressionNetwork.indexToTask[testIndex]);

        //Utils.printHtnGraph(flatProblem,htnMsGraph,"Transport\\TestTask.pdf");

        //System.out.println("HashMap: ");

        //HtnMsGraphWithMethods htnMsGraphWithMethods = ((HtnMsGraphWithMethods) htnMsGraph);

        /*for(Tuple3<Integer,Integer,Integer> edge : htnMsGraphWithMethods.linkedMethods.keySet()){

            System.out.println("Edge:" + edge);

            LinkedList<ProMethod> proMethods = htnMsGraphWithMethods.linkedMethods.get(edge);

            for(ProMethod proMethod: proMethods){

                System.out.println("Promethod:" + proMethod.m.name());

            }

        }*/

        //Testing.testNodeIdentificationByMethods(presentGraphs, methods);

        //System.exit(0);


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


        if (withMethods == true) {

            int nodeID = classicalCombinedGraph.cascadingTables.getNodeID(state);

            //System.out.println(nodeID);

            if (nodeID == -1) return -1;


            distanceFromGoal = distancesFromGoal.get(nodeID);

        } else {
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

    protected void prepareS0andG(ProgressionPlanStep ps, BitSet r, BitSet g) {
        if (heuristic == null) return;

        if (!ps.done) {
            ps.reachableTasks = new BitSet(compEnc.numOfOperators);
            ps.goalFacts = new BitSet(compEnc.numOfStateFeatures);
            ps.reachableTasks.or(compEnc.tdRechability.getReachableActions(ps.taskIndex));
            ps.goalFacts.set(ps.taskIndex);

            for (ProgressionPlanStep ps2 : ps.successorList) {
                prepareS0andG(ps2, ps.reachableTasks, ps.goalFacts);
            }
            ps.done = true;
        }
        r.or(ps.reachableTasks);
        g.or(ps.goalFacts);
    }


    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {


        BitSet bs = newTN.state;
        int[] arrayState = new int[bs.size()];

        int i = 0;
        int j = 0;
        while ((i = bs.nextSetBit(i)) != -1) {
            arrayState[j++] = i++;
        }

        currentHeuristicValue = calcHeu(arrayState);

        if (heuristic != null) {
            BitSet reachableActions = new BitSet(compEnc.numOfNonHtnActions);
            BitSet htnGoal = new BitSet(compEnc.numOfStateFeatures);

            for (ProgressionPlanStep first : newTN.getFirstAbstractTasks())
                prepareS0andG(first, reachableActions, htnGoal);

            for (ProgressionPlanStep first : newTN.getFirstPrimitiveTasks())
                prepareS0andG(first, reachableActions, htnGoal);

            //BitSet s0 = (BitSet) compEnc.s0mask.clone();
            BitSet s0 = compEnc.initS0();
            for (i = reachableActions.nextSetBit(0); i >= 0; i = reachableActions.nextSetBit(i + 1)) {
                compEnc.setReachable(s0, i);
                //s0.set(compEnc.reachable[i]);
                //s0.set(compEnc.unreachable[i], false);
            }
            s0.or(newTN.state);

            BitSet g = new BitSet();

            // prepare g
            for (int fact : compEnc.gList) {
                g.set(fact);
            }

            for (int goalTask = htnGoal.nextSetBit(0); goalTask >= 0; goalTask = htnGoal.nextSetBit(goalTask + 1)) {
                compEnc.setReached(g, goalTask);
                //g.set(compEnc.reached[goalTask]);
                //g.set(compEnc.unreached[goalTask], false);
                //System.out.println(compEnc.factStrs[goalTask + this.compEnc.firstTaskCompIndex]); // for debugging
            }


            if (heuristic.calcHeu(s0, g) == SasHeuristic.cUnreachable) currentHeuristicValue = -1;
        }

        return this;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {

        return update(newTN, ps, null);
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
