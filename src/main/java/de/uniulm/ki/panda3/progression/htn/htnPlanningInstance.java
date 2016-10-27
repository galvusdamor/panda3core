package de.uniulm.ki.panda3.progression.htn;

import de.uniulm.ki.panda3.progression.bottomUpGrounder.groundingUtil;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.*;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.ProgressionSearchRoutine;
import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.cRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.greedyProgression;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware.cRpgHtn;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware.delRelaxedHTN;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.proBFS;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.simpleCompositionRPG;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;
import scala.Tuple2;
import scala.collection.*;

import java.util.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by dhoeller on 01.07.16.
 * <p/>
 * - The representation of actions' preconditions and effects via Bit-Vectors might be suboptimal
 * whenever there are only a few state features effected - maybe implement a version that only
 * represents the states as Bit-Vectors, but uses Booleans for preconditions and effects.
 * (see also the efficientRPG-Class)
 * <p/>
 * - Especially the Bit-Vector representation seems to be suboptimal since it needs to copy the
 * vector before applying an AND-Operator and a comparison - have a look at the respective class
 * and look for more efficient implementations (but have a look at the comment above, first).
 * <p/>
 * - How to implement different search configurations? Via if-then-else? Or via factory-class to
 * help jump-prediction? A search node could produce its child by a next()-method this would
 * also be nice when having an iterative rpg-calculaton.
 */
public class htnPlanningInstance {

    final boolean verbose = false;

    public static Random random;
    public static int randomSeed = 42;

    public void plan(Plan p, Map<Task, Set<GroundedDecompositionMethod>> methodsByTask, Set<GroundTask> allActions, Set<GroundLiteral> allLiterals,
                     InformationCapsule ic, TimeCapsule tc) throws ExecutionException, InterruptedException {
        random = new Random(randomSeed);
        long totaltime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        // translate to efficient representation
        operators.numStateFeatures = allLiterals.size();
        operators.numActions = allActions.size();
        ToCompactRepresentation(allLiterals, allActions);
        operators.methods = getEfficientMethodRep(methodsByTask);
        operators.finalizeMethods();

        Tuple2<BitSet, int[]> s0 = getBitVector(p.groundedInitialState());
        Tuple2<BitSet, int[]> g = getBitVector(p.groundedGoalState());
        operators.goalList = g._2();
        operators.goal = g._1();

        System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms.");

        if (verbose) {
            System.out.println("\nList of grounded actions:");
            for (int i = 0; i < operators.numActions; i++) {
                System.out.println(proPrinter.actionTupleToStr(operators.IndexToAction[i],
                        operators.prec[i], operators.add[i], operators.del[i],
                        operators.numStateFeatures, operators.IndexToLiteral));
            }
        }


        // todo: this will only work with ground initial tn and without any ordering
        Set<GroundTask> initialGroundings = groundingUtil.getFullyGroundTN(p);
        assert (initialGroundings.size() == p.planStepsWithoutInitGoal().size());

        java.util.Iterator<GroundTask> iter = initialGroundings.iterator();
        List<ProgressionPlanStep> initialTasks = new LinkedList<>();
        while (iter.hasNext()) {
            GroundTask n = iter.next();
            ProgressionPlanStep ps = new ProgressionPlanStep(n);
            if (ps.isPrimitive) {
                ps.action = operators.ActionToIndex.get(ps.getTask());
            } else {
                ps.methods = operators.methods.get(ps.getTask().task()).get(ps.getTask());
                if (ps.methods == null) {
                    System.out.println("No method for initial task " + ps.getTask().longInfo());
                    System.out.println("Problem unsolvable.");
                    return;
                }
            }
            initialTasks.add(ps);
        }
        ProgressionNetwork initialNode = new ProgressionNetwork(s0._1(), initialTasks);

        // todo: change heuristic here
        //initialNode.heuristic = new simpleCompositionRPG(operators.methods, allActions);
        //initialNode.heuristic = new cRPG(operators.methods, allActions);
        //initialNode.heuristic = new cRpgHtn(operators.methods, allActions);
        initialNode.heuristic = new greedyProgression();
        //initialNode.heuristic = new delRelaxedHTN(operators.methods, allActions);
        //initialNode.heuristic = new proBFS();

        initialNode.heuristic.build(initialNode);
        initialNode.metric = initialNode.heuristic.getHeuristic();

        ProgressionSearchRoutine routine;
        boolean aStar = false;
        boolean deleteRelaxed = false;
        boolean printOutput = true;
        boolean findShortest = false;
        long quitAfterMs = 300000;

        routine = new PriorityQueueSearch(aStar, deleteRelaxed, printOutput, findShortest);
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

        if (deleteRelaxed) {
            System.out.println(" - DELETE-RELAXED actions");
        }
        if (quitAfterMs > 0) {
            System.out.println(" - time limit for search is " + (quitAfterMs / 1000) + " sec");
        }

        List<Object> solution = routine.search(initialNode, ic, tc);
        assert (isApplicable(solution, s0._1()));
        //System.out.println("###" + ic.keyValueListString() + ";" + tc.keyValueListString());

        int n = 1;
        if (solution != null) {
            System.out.println("\nFound a solution:");
            for (Object a : solution) {
                if (a instanceof Integer)
                    System.out.println(n + " " + proPrinter.actionToStr(operators.IndexToAction[(Integer) a]));
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
    }

    private boolean isApplicable(List<Object> solution, BitSet state) {
        if (solution == null)
            return true;
        for (Object mod : solution) {
            if (mod instanceof Integer) {
                int a = (Integer) mod;
                int pre = operators.prec[a].nextSetBit(0);
                while (pre > -1) {
                    if (!state.get(pre))
                        return false;
                    pre = operators.prec[a].nextSetBit(pre + 1);
                }
                state.andNot(operators.del[a]);
                state.or(operators.add[a]);
            }
        }
        return true;
    }

    private HashMap<Task, HashMap<GroundTask, List<method>>> getEfficientMethodRep(Map<Task, Set<GroundedDecompositionMethod>> methodsByTask) {
        HashMap<Task, HashMap<GroundTask, List<method>>> res = new HashMap<>();
        for (Task t : methodsByTask.keySet()) {
            HashMap<GroundTask, List<method>> oneSchema = new HashMap<>();
            res.put(t, oneSchema);
            for (GroundedDecompositionMethod m : methodsByTask.get(t)) {
                GroundTask gt = m.groundAbstractTask();
                List<method> methods;
                if (oneSchema.containsKey(gt)) {
                    methods = oneSchema.get(gt);
                } else {
                    methods = new ArrayList<>();
                    oneSchema.put(gt, methods);
                }
                methods.add(new method(m));
            }
        }
        return res;
    }


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
                operators.ShopPrecActions.add(i);
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
    }

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
