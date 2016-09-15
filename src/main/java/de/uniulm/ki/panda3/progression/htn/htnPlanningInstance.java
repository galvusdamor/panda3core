package de.uniulm.ki.panda3.progression.htn;

import de.uniulm.ki.panda3.progression.bottomUpGrounder.groundingUtil;
import de.uniulm.ki.panda3.progression.bottomUpGrounder.htnBottomUpGrounder;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.*;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.IRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.cRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware.hierarchyAwareRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.symbolicRPG;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TopDownTaskDecompositionGraph;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.DebuggingMode;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.GroundedForwardSearchReachabilityAnalysis;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.GroundedPlanningGraph;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.GroundedPlanningGraphConfiguration;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.JavaToScala;
import scala.Tuple2;
import scala.collection.*;
import scala.collection.Iterator;
import scala.collection.concurrent.Debug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    final boolean foo = true;
    final private boolean writeGroundingForDebug = false;


    public void plan(Plan p, Map<Task, Set<GroundedDecompositionMethod>> methodsByTask, Set<GroundTask> allActions, Set<GroundLiteral> allLiterals) throws ExecutionException, InterruptedException {
        long totaltime = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        //
        // Get reachable groundings
        //
/*        Set<GroundLiteral> allLiterals;
        Set<GroundTask> allActions;

        if (foo) {
            GroundedPlanningGraphConfiguration pgConf = new GroundedPlanningGraphConfiguration(false, false,
                    new scala.collection.immutable.HashSet<>(), new scala.collection.immutable.HashSet<>(),
                    true, DebuggingMode.Disabled());
            GroundedForwardSearchReachabilityAnalysis pg = GroundedForwardSearchReachabilityAnalysis.apply(d,p);
                    //new GroundedPlanningGraph(d, p.groundedInitialStateOnlyPositiveSet(), pgConf);
            System.out.println("ParteiGenosse");
            TopDownTaskDecompositionGraph tdg = new TopDownTaskDecompositionGraph(d, p, pg, true);
            System.out.println("TDG");
            scala.collection.immutable.Map<Task, scala.collection.immutable.Set<GroundedDecompositionMethod>> groundFoo =
                    tdg.reachableGroundMethodsByGroundAbstractTask();

            HashMap<Task, Set<GroundedDecompositionMethod>> groundMap = new HashMap<>();

            Iterator<Tuple2<Task, scala.collection.immutable.Set<GroundedDecompositionMethod>>> iter1 = groundFoo.iterator();
            while (iter1.hasNext()) {
                Tuple2<Task, scala.collection.immutable.Set<GroundedDecompositionMethod>> tup = iter1.next();
                groundMap.put(tup._1(), JavaConversions.setAsJavaSet(tup._2()));
            }
            Iterator<GroundLiteral> iter2 = tdg.reachableGroundLiterals().iterator();
            allLiterals = new HashSet<>();
            while (iter2.hasNext()) {
                allLiterals.add(iter2.next());
            }

            Iterator<GroundTask> iter3 = tdg.reachableGroundPrimitiveActions().iterator();
            allActions = new HashSet<>();
            while (iter3.hasNext()) {
                allActions.add(iter3.next());
            }

            ToCompactRepresentation(allLiterals, allActions);
            operators.methods = getEfficientMethodRep(groundMap);

        } else {

            htnBottomUpGrounder gr = null;
            boolean converged = false;
            IRPG rpg = null;
            try {
                System.out.println("<PRESS KEY>");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!converged) {
                System.out.println("Building relaxed planning graph");
                long time2 = System.currentTimeMillis();
                rpg = new symbolicRPG();
                //rpg = new hierarchyAwareRPG();
                if (gr == null)
                    rpg.build(d, p);
                else
                    rpg.build(d, p, gr.groundingsByTask);
                System.out.println(" (" + (System.currentTimeMillis() - time2) + " ms).");

                System.out.println(" - Graph contains " + rpg.getApplicableActions().size() + " ground actions.");
                System.out.println(" - Graph contains " + rpg.numOfReachableFacts() + " environment facts.");

                gr = new htnBottomUpGrounder(d, p, rpg.getApplicableActions());
                converged = !gr.deletedActions;
                if (gr.deletedActions) {
                    System.out.println("Restart grounding ...");
                }
            }

        //if (writeGroundingForDebug) {
       //     writeGroundingToFile(gr, rpg);
        //}

            allLiterals = rpg.getReachableFacts();
            allActions = rpg.getApplicableActions();
            operators.numStateFeatures = allLiterals.size();
            operators.numActions = allActions.size();

            System.out.println("Grounding completed in " + (System.currentTimeMillis() - time) + " ms.");
            time = System.currentTimeMillis();

            System.out.println(" - Found " + gr.abstTaskCount + " reachable ground abstract tasks.");
            System.out.println(" - Found " + gr.methodCount + " reachable ground methods.");
            System.out.println(" - Found " + operators.numActions + " reachable ground actions.");
            System.out.println(" - Found " + operators.numStateFeatures + " reachable environment facts.");
            System.out.println("\nPreprocessing planning instance");
            ToCompactRepresentation(allLiterals, allActions);
            operators.methods = getEfficientMethodRep(gr.methodsByTask);
        }*/
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
        List<proPlanStep> initialTasks = new LinkedList<>();
        while (iter.hasNext()) {
            GroundTask n = iter.next();
            proPlanStep ps = new proPlanStep(n);
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
        progressionNetwork initialNode = new de.uniulm.ki.panda3.progression.htn.search.progressionNetwork(s0._1(), initialTasks);

        // todo: change heuristic here
        //initialNode.heuristic = new simpleCompositionRPG(operators.methods, allActions);
        //initialNode.heuristic = new cRPG(operators.methods, allActions);
        //initialNode.heuristic.build(initialNode);
        //initialNode.metric = initialNode.heuristic.getHeuristic();
        initialNode.metric = 1;

        ProgressionSearchRoutine routine;
        routine = new PriorityQueueSearch();
        //routine = new EnforcedHillClimbing();
        //routine = new CompleteEnforcedHillClimbing();

        List<Object> solution = routine.search(initialNode);

        int n = 1;
        if (solution != null) {
            System.out.println("\nFound a solution:");
            for (Object a : solution) {
                if (a instanceof Integer)
                    System.out.println(n + " " + proPrinter.actionToStr(operators.IndexToAction[(Integer) a]));
                else
                    System.out.println(n + " " + ((GroundedDecompositionMethod) a).mediumInfo());
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

    private void writeGroundingToFile(htnBottomUpGrounder gr, IRPG rpg) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/temp/groundings.txt"));
            for (Task task : gr.groundingsByTask.keySet()) {
                for (GroundTask gt : gr.groundingsByTask.get(task)) {
                    bw.write(gt.mediumInfo() + "\n");
                }
            }

            bw.write("\n");
            for (Task task : gr.methodsByTask.keySet()) {
                for (GroundedDecompositionMethod meth : gr.methodsByTask.get(task)) {
                    bw.write(meth.mediumInfo() + "\n");
                }
            }

            bw.write("\n");
            for (GroundTask ac : rpg.getApplicableActions()) {
                bw.write(ac.mediumInfo() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void inferCausalLinks(BitSet s0, List<Integer> solution) {
        List<Integer> sol = new LinkedList<>(); // copy array to keep changes local to the function
        sol.addAll(solution);
        sol.add(0, -1); // add dummy to simplify index-magic

        for (int consumerI = 1; consumerI < sol.size(); consumerI++) {
            int consumer = sol.get(consumerI);
            int fact = operators.prec[consumer].nextSetBit(0);
            boolean found = false;
            while (fact >= 0) {
                // Bit lookup tables
                BitSet relevantAdds = new BitSet(sol.size());
                if (s0.get(fact))
                    relevantAdds.set(0);
                BitSet relevantDels = new BitSet(sol.size());
                for (int ai = 1; ai < sol.size(); ai++) {
                    int a = sol.get(ai);
                    if (operators.add[a].get(fact))
                        relevantAdds.set(ai);
                    if (operators.del[a].get(fact))
                        relevantDels.set(ai);
                }
                int offset = 0;
                while (offset < consumerI) { // the equality is due to the first bit in the vectors that represents s0
                    int supporterI = relevantAdds.nextSetBit(offset);
                    boolean nothreat = ((supporterI == (consumerI - 1)) || (relevantDels.get(supporterI + 1, consumerI - 1).nextSetBit(0) == -1));
                    if (nothreat) {
                        System.out.println(supporterI + " -> " + consumerI + " " + proPrinter.literalToStr(operators.IndexToLiteral[fact]));
                        found = true;
                        if (supporterI > 0) { // only when the support is NOT s0
                            BitSet preds = relevantDels.get(0, supporterI - 1);
                            int predI = preds.nextSetBit(0);
                            while (predI >= 0) {
                                System.out.println(fact + " < " + supporterI);
                                predI = preds.nextSetBit(fact + 1);
                            }
                        }
                        BitSet succs = relevantDels.get(consumerI + 1, relevantDels.size() - 1);
                        int succI = succs.nextSetBit(0);
                        while (succI >= 0) {
                            System.out.println(consumerI + " < " + fact);
                            succI = succs.nextSetBit(succI + 1);
                        }
                        break;
                    } else
                        offset = relevantDels.nextSetBit(supporterI + 1) + 1;
                }
                fact = operators.prec[consumer].nextSetBit(fact + 1);
            }
            if (!found) {
                System.out.println("There is no supporter for a precondition");
            }
        }
    }

    private boolean noThreat(List<Integer> solution, int producer, int consumer, int effI) {
        for (int i = producer + 1; i < consumer; i++) {
            int action = solution.get(i);
            if (operators.del[action].get(effI)) {
                return false;
            }
        }
        return true;
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
