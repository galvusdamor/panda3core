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
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

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

        // kann ich darauf verzichten?: Set<GroundTask> allActions, Set<GroundLiteral> allLiterals
        // flags überdenken

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
        ProgressionNetwork.indexToTask = indexToTask;
        ProgressionNetwork.taskToIndex = taskToIndex;


        //long time = System.currentTimeMillis();

        //System.out.println("Calculating efficient representation...");
        // translate to efficient representation
        //operators.numStateFeatures = allLiterals.size();
        //operators.numActions = allActions.size();
        //ToCompactRepresentation(allLiterals, allActions);
        HashMap<Task, List<method>> methods = getEfficientMethodRep(methodsByTask);
        finalizeMethods(methods);
        ProgressionNetwork.methods = methods;

        /*
        Tuple2<BitSet, int[]> s0 = getBitVector(p.groundedInitialState());
        Tuple2<BitSet, int[]> g = getBitVector(p.groundedGoalState());
        operators.goalList = g._2();
        operators.goal = g._1();
        */

        //System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms.");
/*
        if (verbose) {
            System.out.println("\nList of grounded actions:");
            for (int i = 0; i < operators.numActions; i++) {
                System.out.println(proPrinter.actionTupleToStr(operators.IndexToAction[i],
                        operators.prec[i], operators.add[i], operators.del[i],
                        operators.numStateFeatures, operators.IndexToLiteral));
            }
        }*/

        // todo: this will only work with ground initial tn and without any ordering
        //Set<GroundTask> initialGroundings = groundingUtil.getFullyGroundTN(p);
        //assert (initialGroundings.size() == p.planStepsWithoutInitGoal().size());


        if (p.planStepsWithoutInitGoal().size() == 1) {
            System.out.println("Error: Progression search algorithm found more than one task in the initial task network.");
            System.exit(-1);
        }

        //java.util.Iterator<GroundTask> iter = initialGroundings.iterator();
        List<ProgressionPlanStep> initialTasks = new LinkedList<>();

        //while (iter.hasNext()) {
        //GroundTask n = iter.next();
        ProgressionPlanStep ps = new ProgressionPlanStep(p.planStepsWithoutInitGoal().apply(0).schema());
        /*if (ps.isPrimitive) {
            ps.action = operators.ActionToIndex.get(ps.getTask());
        } else {
            ps.methods = operators.methods.get(ps.getTask().task()).get(ps.getTask());
            if (ps.methods == null) {
                System.out.println("No method for initial task " + ps.getTask().longInfo());
                System.out.println("Problem unsolvable.");
                return false;
            }
        }*/
        initialTasks.add(ps);
        //}
        ProgressionNetwork initialNode = new ProgressionNetwork(ProgressionNetwork.flatProblem.getS0(), initialTasks);

        Set<Task> allActions;
        allActions = new HashSet<>();
        scala.collection.Iterator<Task> iter = d.primitiveTasks().iterator();
        while (iter.hasNext()) {
            allActions.add(iter.next());
        }

        if (doBFS)
            initialNode.heuristic = new proBFS();
        else if (doDFS)
            initialNode.heuristic = new proDFS();
            //else if (heuristic instanceof SimpleCompositionRPG$)
            //    initialNode.heuristic = new simpleCompositionRPG(operators.methods, allActions);
        else if (heuristic instanceof RelaxedCompositionGraph) {
            initialNode.heuristic = new proRcgSas(ProgressionNetwork.flatProblem, SasHeuristic.SasHeuristics.hLmCut, methods, initialTasks, allActions);
        } else if (heuristic instanceof RelaxedCompositionGraph) {
            RelaxedCompositionGraph heu = (RelaxedCompositionGraph) heuristic;
            initialNode.heuristic = new RCG(methods, initialTasks, allActions, heu.useTDReachability(), heu.producerSelectionStrategy(), heu.heuristicExtraction());
            //} else if (heuristic instanceof CompositionRPGHTN$)
            //    initialNode.heuristic = new cRpgHtn(operators.methods, allActions);
        } else if (heuristic instanceof GreedyProgression$)
            initialNode.heuristic = new greedyProgression();
            //else if (heuristic instanceof DeleteRelaxedHTN$)
            //    initialNode.heuristic = new delRelaxedHTN(operators.methods, allActions);
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
        //routine = new EnforcedHillClimbing();
        //routine = new CompleteEnforcedHillClimbing();
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
                    System.out.println(n + " " + proPrinter.actionToStr(ProgressionNetwork.indexToTask.get(a)));
                else
                    System.out.println(n + " " + ((GroundedDecompositionMethod) a).longInfo());
                n++;
            }
            /*
            time = System.currentTimeMillis();
            System.out.println("\nInferring least constraining causal links");
            inferCausalLinks(s0._1(), solution);
            System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms");*/
        } else System.out.println("Problem unsolvable.");
        System.out.println("Total program runtime: " + (System.currentTimeMillis() - totaltime) + " ms");

        return solution != null;
    }

    private void finalizeMethods(HashMap<Task, List<method>> methods) {
        for (List<method> y : methods.values()) {
            for (method z : y) {
                z.finalizeMethod();
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

/*
    private Tuple2<BitSet, int[]> getBitVector(Seq<GroundLiteral> groundLiteralSeq) {
        BitSet state = new BitSet(operators.numStateFeatures);
        int[] list = new int[groundLiteralSeq.size()];
        for (int j = 0; j < groundLiteralSeq.size(); j++) {
            GroundLiteral gl = groundLiteralSeq.apply(j);
            if (!gl.isPositive())
                continue;
            int index = operators.LiteralToIndex.get(gl);
            list[j] = index;
            state.set(index);
        }
        return new Tuple2<>(state, list);
    }

    private void ToCompactRepresentation(Set<GroundLiteral> allLiterals, Set<GroundTask> allActions) {
        operators.LiteralToIndex = new HashMap<>();
        operators.IndexToLiteral = new GroundLiteral[operators.numStateFeatures];

        java.util.Iterator<GroundLiteral> litIter = allLiterals.iterator();
        int i = 0;
        while (litIter.hasNext()) {
            GroundLiteral g = litIter.next();
            operators.LiteralToIndex.put(g, i);
            operators.IndexToLiteral[i] = g;
            i++;
        }

        operators.ActionToIndex = new HashMap<>();
        operators.IndexToAction = new GroundTask[operators.numActions];

        operators.prec = new BitSet[operators.numActions];
        operators.precList = new int[operators.numActions][];
        operators.add = new BitSet[operators.numActions];
        operators.addList = new int[operators.numActions][];
        operators.del = new BitSet[operators.numActions];

        java.util.Iterator<GroundTask> actionIter = allActions.iterator();
        i = 0;
        List<GroundLiteral> deletedEffects = new LinkedList<>();
        while (actionIter.hasNext()) {
            GroundTask action = actionIter.next();
            operators.ActionToIndex.put(action, i);
            operators.IndexToAction[i] = action;
            if (action.mediumInfo().startsWith("SHOP_")) {
                ProgressionNetwork.ShopPrecActions.add(i);
            }

            operators.prec[i] = new BitSet(operators.numStateFeatures);
            operators.precList[i] = new int[action.substitutedPreconditions().size()];
            for (int j = 0; j < action.substitutedPreconditions().size(); j++) {
                GroundLiteral gl = action.substitutedPreconditions().apply(j);
                int index = operators.LiteralToIndex.get(gl);
                operators.precList[i][j] = index;
                operators.prec[i].set(index);
            }

            operators.add[i] = new BitSet(operators.numStateFeatures);
            operators.addList[i] = new int[action.substitutedAddEffects().size()];
            for (int j = 0; j < action.substitutedAddEffects().size(); j++) {
                GroundLiteral gl = action.substitutedAddEffects().apply(j);
                int index = operators.LiteralToIndex.get(gl);
                operators.addList[i][j] = index;
                operators.add[i].set(index);
            }

            operators.del[i] = new BitSet(operators.numStateFeatures);
            for (int j = 0; j < action.substitutedDelEffects().size(); j++) {
                int index = -1;
                GroundLiteral gl = action.substitutedDelEffects().apply(j);
                for (int de = 0; de < operators.IndexToLiteral.length; de++) {
                    if (featureEqual(operators.IndexToLiteral[de], gl)) {
                        index = de;
                        break;
                    }
                }
                if (index == -1) {
                    deletedEffects.add(gl);
                } else
                    operators.del[i].set(index);
            }
            i++;

            // deleted delete-effects
            if (deletedEffects.size() > 0) {
                if (verbose) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Pruned the following delete-effects from action \'");
                    sb.append(proPrinter.actionToStr(action));
                    sb.append("\' because this facts will constantly be false: ");
                    for (int j = 0; j < deletedEffects.size(); j++) {
                        sb.append("\'");
                        sb.append(proPrinter.literalToStr(deletedEffects.get(j)));
                        sb.append("\'");
                        if (j < (deletedEffects.size() - 1))
                            sb.append(", ");
                    }
                    System.out.println(sb.toString());
                }
                deletedEffects.clear();
            }
        }
    }*/

    private boolean featureEqual(GroundLiteral g1, GroundLiteral g2) {
        if (!g1.predicate().equals(g2.predicate()))
            return false;
        if (!(g1.parameter().size() == g2.parameter().size()))
            return false;
        for (int i = 0; i < g1.parameter().size(); i++) {
            if (!g1.parameter().apply(i).equals(g2.parameter().apply(i)))
                return false;
        }
        return true;
    }
}
