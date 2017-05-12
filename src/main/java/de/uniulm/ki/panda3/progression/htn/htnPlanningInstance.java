package de.uniulm.ki.panda3.progression.htn;

import de.uniulm.ki.panda3.configuration.*;
import de.uniulm.ki.panda3.progression.htn.search.*;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.ProgressionSearchRoutine;
import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.*;
import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.heuristics.SasHeuristic;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.SimpleDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;
import scala.Tuple2;

import java.util.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by dhoeller on 01.07.16.
 */
public class htnPlanningInstance {

    public static SasPlusProblem sasp;

    final boolean verbose = false;

    public static Random random;
    public static int randomSeed = 42;

    public boolean plan(Domain d, Plan p, Map<Task, Set<SimpleDecompositionMethod>> methodsByTask,
                        InformationCapsule ic, TimeCapsule tc,
                        PriorityQueueSearch.abstractTaskSelection taskSelectionStrategy,
                        SearchHeuristic heuristic, boolean doBFS, boolean doDFS,
                        boolean aStar, boolean deleteRelaxed, long quitAfterMs) throws ExecutionException, InterruptedException {

        if (d.sasPlusRepresentation().isEmpty()) {
            System.out.println("Error: Progression search algorithm did not find action model.");
            System.exit(-1);
        }

        // Convert data structures
        long totaltime = System.currentTimeMillis();
        random = new Random(randomSeed);

        Map<Integer, Task> indexToTask = new HashMap<>();
        scala.collection.immutable.Map<Object, Task> k2t = d.sasPlusRepresentation().get().sasPlusIndexToTask();
        scala.collection.Iterator<Object> keys1 = k2t.keysIterator();
        while (keys1.hasNext()) {
            Integer k = (Integer) keys1.next();
            indexToTask.put(k, k2t.apply(k));
        }

        Map<Task, Integer> taskToIndex = new HashMap<>();
        scala.collection.immutable.Map<Task, Object> t2i = d.sasPlusRepresentation().get().taskToSASPlusIndex();
        scala.collection.Iterator<Task> keys2 = t2i.keysIterator();
        while (keys2.hasNext()) {
            Task t = keys2.next();
            taskToIndex.put(t, (Integer) t2i.apply(t));
        }

        ProgressionNetwork.flatProblem = d.sasPlusRepresentation().get().sasPlusProblem();
        Tuple2<Map<Integer, Task>, Map<Task, Integer>> x = ProgressionNetwork.flatProblem.restrictTo(indexToTask.keySet(), indexToTask);
        indexToTask = x._1();
        taskToIndex = x._2();

        // create permanent mappings
        ProgressionNetwork.taskToIndex = new HashMap<>();
        assert ((d.abstractTasks().size() + d.primitiveTasks().size()) == d.tasks().size());
        ProgressionNetwork.indexToTask = new Task[d.tasks().size()];

        int iAbs = taskToIndex.size(); // currently it contains solely the actions -> put abstracts behind
        scala.collection.Iterator<Task> iter = d.abstractTasks().iterator();
        while (iter.hasNext()) {
            Task t = iter.next();
            ProgressionNetwork.taskToIndex.put(t, iAbs);
            ProgressionNetwork.indexToTask[iAbs] = t;
            iAbs++;
        }
        ProgressionNetwork.taskToIndex.putAll(taskToIndex);

        for (int i = 0; i < indexToTask.size(); i++) {
            ProgressionNetwork.indexToTask[i] = indexToTask.get(i);
        }

        HashMap<Task, List<method>> methods = getEfficientMethodRep(methodsByTask);
        finalizeMethods(methods);
        //ProgressionNetwork.methods = methods;

        if (!(p.planStepsWithoutInitGoal().size() == 1)) {
            System.out.println("Error: Progression search algorithm found more than one task in the initial task network.");
            System.exit(-1);
        }

        List<ProgressionPlanStep> initialTasks = new LinkedList<>();

        ProgressionPlanStep ps = new ProgressionPlanStep(p.planStepsWithoutInitGoal().apply(0).schema());
        initialTasks.add(ps);
        ProgressionNetwork initialNode = new ProgressionNetwork(ProgressionNetwork.flatProblem.getS0(), initialTasks);

        assert (checkDomain(taskToIndex.keySet(), d.primitiveTasks().iterator(), indexToTask.keySet()));

        if (doBFS)
            initialNode.heuristic = new proBFS();
        else if (doDFS)
            initialNode.heuristic = new proDFS();
        else if (heuristic instanceof RelaxedCompositionGraph) {
            initialNode.heuristic = new proRcgSas(ProgressionNetwork.flatProblem, SasHeuristic.SasHeuristics.hLmCut, methods, initialTasks, taskToIndex.keySet());
        } else if (heuristic instanceof RelaxedCompositionGraph) {
            RelaxedCompositionGraph heu = (RelaxedCompositionGraph) heuristic;
            initialNode.heuristic = new RCG(methods, initialTasks, taskToIndex.keySet(), heu.useTDReachability(), heu.producerSelectionStrategy(), heu.heuristicExtraction());
        } else if (heuristic instanceof GreedyProgression$)
            initialNode.heuristic = new greedyProgression();
        else {
            throw new IllegalArgumentException("Heuristic " + heuristic + " is not supported");
        }

/*
        // todo: this is hacky!
        if ((taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.decompDepth) && (!TopDownReachabilityGraph.isInitialized())) {
            int taskNo = operators.numStateFeatures;
            HashMap<GroundTask, Integer> TaskToIndex = new HashMap<>();

            for (GroundTask a : allActions) {
                TaskToIndex.put(a, taskNo);
                taskNo++;
            }

            for (GroundTask t : RCG.getGroundTasks(operators.methods)) {
                TaskToIndex.put(t, taskNo);
                taskNo++;
            }

            new TopDownReachabilityGraph(methods, initialTasks, taskNo, flatProblem.numOfOperators, TaskToIndex);

        }*/
        initialNode.heuristic.build(initialNode);
        initialNode.metric = initialNode.heuristic.getHeuristic();

        ProgressionSearchRoutine routine;
        boolean printOutput = true;
        boolean findShortest = false;

        routine = new PriorityQueueSearch(aStar, deleteRelaxed, printOutput, findShortest, taskSelectionStrategy);
        routine.wallTime = quitAfterMs;

        System.out.println("Searching with \n - " + routine.SearchName() + " search routine");
        if (aStar) {
            System.out.println(" - A-Star search");
        } else {
            System.out.println(" - Greedy search");
        }
        System.out.println(" - " + initialNode.heuristic.getName() + " heuristic");

        if (taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.random) {
            System.out.println(" - Abstract task choice: randomly");
        } else if (taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.decompDepth) {
            System.out.println(" - Abstract task choice: via min decomposition depth left");
        } else if (taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.methodCount) {
            System.out.println(" - Abstract task choice: via min number of decomposition methods");
        } else if (taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.branchOverAll) {
            System.out.println(" - Abstract task choice: branch over all abstract tasks");
        }

        if (deleteRelaxed) {
            System.out.println(" - DELETE-RELAXED actions");
        }
        if (quitAfterMs > 0) {
            System.out.println(" - time limit for search is " + (quitAfterMs / 1000) + " sec");
        }

        List<Object> solution;
        if ((routine instanceof PriorityQueueSearch) && (taskSelectionStrategy == PriorityQueueSearch.abstractTaskSelection.branchOverAll)) {
            System.out.println(" - This is not a good configuration -- it BRANCHES over ALL abstract tasks. " +
                    "One should only only do that for evaluation purposes.");
            solution = ((PriorityQueueSearch) routine).searchWithAbstractBranching(initialNode, ic, tc);
        } else {
            solution = routine.search(initialNode, ic, tc);
        }

        assert (isApplicable(solution, ProgressionNetwork.flatProblem.getS0()));
        //System.out.println("###" + ic.keyValueListString() + ";" + tc.keyValueListString());

        int n = 1;
        if (solution != null) {
            System.out.println("\nFound a solution:");
            for (Object a : solution) {
                if (a instanceof Integer)
                    System.out.println(n + " " + proPrinter.actionToStr(ProgressionNetwork.indexToTask[(Integer)a]));
                else
                    System.out.println(n + " " + ((GroundedDecompositionMethod) a).longInfo());
                n++;
            }
        } else System.out.println("Problem unsolvable.");
        System.out.println("Total program runtime: " + (System.currentTimeMillis() - totaltime) + " ms");

        return solution != null;
    }

    private boolean checkDomain(Set<Task> tasks, scala.collection.Iterator<Task> iter, Set<Integer> integers) {
        if (integers.size() != ProgressionNetwork.flatProblem.numOfOperators)
            return false;
        for (int i = 0; i < ProgressionNetwork.flatProblem.numOfOperators; i++)
            if (!integers.contains(i)) {
                System.out.println("Index mapping is discontiguous: " + i + "is not included");
                return false;
            }


        Set<Task> allActions;
        allActions = new HashSet<>();
        while (iter.hasNext()) {
            allActions.add(iter.next());
        }
        Set<Task> newSet = new HashSet<>();
        newSet.addAll(tasks);
        newSet.addAll(allActions);
        return (tasks.size() == allActions.size()) && (allActions.size() == newSet.size());
    }

    private void finalizeMethods(HashMap<Task, List<method>> methods) {
        for (List<method> y : methods.values()) {
            for (method z : y) {
                z.finalizeMethod(methods);
            }
        }
    }

    private boolean isApplicable(List<Object> solution, BitSet state) {
        if (solution == null)
            return true;
        for (Object mod : solution) {
            if (mod instanceof Integer) {
                int a = (Integer) mod;
                for (int pre : ProgressionNetwork.flatProblem.precLists[a]) {
                    if (!state.get(pre))
                        return false;
                }

                for (int df : ProgressionNetwork.flatProblem.delLists[a])
                    state.set(df, false);
                for (int af : ProgressionNetwork.flatProblem.addLists[a])
                    state.set(af, true);
            }
        }
        return true;
    }

    private HashMap<Task, List<method>> getEfficientMethodRep(Map<Task, Set<SimpleDecompositionMethod>> methodsByTask) {
        HashMap<Task, List<method>> res = new HashMap<>();
        for (Task t : methodsByTask.keySet()) {
            List<method> oneSchema = new ArrayList<>();
            res.put(t, oneSchema);
            for (SimpleDecompositionMethod m : methodsByTask.get(t)) {
                oneSchema.add(new method(m));
            }
        }
        return res;
    }
}
