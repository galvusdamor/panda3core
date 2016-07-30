package de.uniulm.ki.panda3.progression.htn;

import de.uniulm.ki.panda3.progression.bottomUpGrounder.groundingUtil;
import de.uniulm.ki.panda3.progression.bottomUpGrounder.htnBottomUpGrounder;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.proPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.progressionNetwork;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.cRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.simpleCompositionRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.symbolicRPG;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import scala.Tuple2;
import scala.collection.Seq;

import java.util.*;
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

    public void plan(Domain d, Plan p) throws ExecutionException, InterruptedException {
        long totaltime = System.currentTimeMillis();

        //
        // Get reachable groundings
        //
        long time = System.currentTimeMillis();
        Set<GroundLiteral> allLiterals;
        Set<GroundTask> allActions;
        htnBottomUpGrounder gr = null;
        boolean converged = false;
        symbolicRPG rpg = null;
        while (!converged) {
            System.out.print("Building relaxed planning graph");
            long time2 = System.currentTimeMillis();
            rpg = new symbolicRPG();
            if (gr == null)
                rpg.build(d, p);
            else
                rpg.build(d, p, gr.groundingsByTask);
            System.out.println(" (" + (System.currentTimeMillis() - time2) + " ms).");

            System.out.println(" - Graph contains " + rpg.getApplicableActions().size() + " ground actions.");
            System.out.println(" - Graph contains " + rpg.getReachableFacts().size() + " environment facts.");

            gr = new htnBottomUpGrounder(d, p, rpg.getApplicableActions());
            converged = !gr.deletedActions;
            if (gr.deletedActions) {
                System.out.println("Restart grounding ...");
            }
        }

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

        // translate to efficient representation
        ToCompactRepresentation(allLiterals, allActions);
        operators.methods = getEfficientMethodRep(gr.methodsByTask);
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

        Iterator<GroundTask> iter = initialGroundings.iterator();
        List<proPlanStep> initialTasks = new LinkedList<>();
        while (iter.hasNext()) {
            GroundTask n = iter.next();
            proPlanStep ps = new proPlanStep(n);
            if (ps.isPrimitive) {
                ps.action = operators.ActionToIndex.get(ps.getTask());
            } else {
                ps.methods = operators.methods.get(ps.getTask().task()).get(ps.getTask());
            }
            initialTasks.add(ps);
        }
        progressionNetwork initialNode = new de.uniulm.ki.panda3.progression.htn.search.progressionNetwork(s0._1(), initialTasks);

        // todo: change heuristic here
        //initialNode.heuristic = new simpleCompositionRPG(operators.methods, allActions);
        initialNode.heuristic = new cRPG(gr.abstTaskCount, operators.methods, allActions);
        initialNode.heuristic.build(initialNode);
        initialNode.metric = initialNode.heuristic.getHeuristic();


        List<Object> solution = priorityQueueSearch(initialNode);
        //List<Object> solution = enforcedHillClimbing(initialNode);
        //List<Object> solution = completeEnforcedHillClimbing(initialNode);

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

    private List<Object> priorityQueueSearch(progressionNetwork firstSearchNode) {
        System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int bestMetric = Integer.MAX_VALUE;
        List<Object> solution = null;
        long time = System.currentTimeMillis();
        PriorityQueue<progressionNetwork> fringe = new PriorityQueue<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (!fringe.isEmpty()) {
            progressionNetwork n = fringe.poll();
            operatorloop:
            for (proPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    progressionNetwork node = n.apply(ps);
                    if (node.goalRelaxedReachable) {
                        // early goal test - NON-OPTIMAL
                        if (node.isGoal()) {
                            solution = node.solution;
                            break planningloop;
                        }

                        fringe.add(node);
                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                        }
                    }
                    searchnodes++;
                    if ((searchnodes % 10000) == 0)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        if (node.goalRelaxedReachable) {

                            if (node.isGoal()) {
                                solution = node.solution;
                                break planningloop;
                            }

                            fringe.add(node);
                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                            }
                        }
                        searchnodes++;
                        if ((searchnodes % 10000) == 0)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                    }
                }
            }
        }

        System.out.println("Generated search nodes (total): " + searchnodes);
        System.out.println("Search time: " + (System.currentTimeMillis() - time) + " ms");
        return solution;
    }


    private List<Object> enforcedHillClimbing(progressionNetwork firstSearchNode) {
        System.out.println("\nStarting enforced hill climbing search");
        int searchnodes = 1;
        int bestMetric = firstSearchNode.metric;
        List<Object> solution = null;
        long time = System.currentTimeMillis();
        LinkedList<progressionNetwork> fringe = new LinkedList<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (true) {
            if (fringe.isEmpty()) // failure
                return null;

            progressionNetwork n = fringe.removeFirst();

            operatorloop:
            for (proPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    progressionNetwork node = n.apply(ps);
                    if (node.heuristic.goalRelaxedReachable()) {
                        // early goal test - NON-OPTIMAL
                        if (node.isGoal()) {
                            solution = node.solution;
                            break planningloop;
                        }


                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            fringe.clear();
                            fringe.add(node);
                            System.out.println("Found new best metric value: " + node.metric);
                            continue planningloop;
                        } else {
                            fringe.addLast(node);
                        }
                    }
                    searchnodes++;
                    if ((searchnodes % 10000) == 0)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        if (node.heuristic.goalRelaxedReachable()) {

                            if (node.isGoal()) {
                                solution = node.solution;
                                break planningloop;
                            }

                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                fringe.clear();
                                fringe.add(node);
                                System.out.println("Found new best metric value: " + node.metric);
                                continue planningloop;
                            } else {
                                fringe.addLast(node);
                            }
                        }
                        searchnodes++;
                        if ((searchnodes % 10000) == 0)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                    }
                }
            }
        }

        System.out.println("Generated search nodes (total): " + searchnodes);
        System.out.println("Search time: " + (System.currentTimeMillis() - time) + " ms");
        return solution;
    }

    int cehcSearchNodes = 1;

    private List<Object> completeEnforcedHillClimbing(progressionNetwork firstSearchNode) {
        int bestMetric = firstSearchNode.metric;
        LinkedList<progressionNetwork> fringe = new LinkedList<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (true) {
            if (fringe.isEmpty()) // failure
                return null;

            progressionNetwork n = fringe.removeFirst();

            operatorloop:
            for (proPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    progressionNetwork node = n.apply(ps);
                    if (node.heuristic.goalRelaxedReachable()) {
                        if (node.isGoal())
                            return node.solution;

                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            System.out.println("-> " + node.metric);
                            List<Object> solution = completeEnforcedHillClimbing(node);
                            if (solution != null)
                                return solution;

                            System.out.println("<- " + bestMetric);
                        }
                        fringe.addLast(node);
                    }
                    cehcSearchNodes++;
                    if ((cehcSearchNodes % 10000) == 0)
                        System.out.println("Searchnodes :" + cehcSearchNodes);
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        if (node.heuristic.goalRelaxedReachable()) {
                            if (node.isGoal())
                                return node.solution;

                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                System.out.println("-> " + node.metric);
                                List<Object> solution = completeEnforcedHillClimbing(node);
                                if (solution != null)
                                    return solution;

                                System.out.println("<- " + bestMetric);
                            }
                            fringe.addLast(node);
                        }
                        cehcSearchNodes++;
                        if ((cehcSearchNodes % 10000) == 0)
                            System.out.println("Searchnodes :" + cehcSearchNodes);

                    }
                }
            }
        }
    }

    private String getInfoStr(int searchnodes, int fringesize, int bestMetric, progressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0)) + " - generated nodes: " + searchnodes + " - fringe size: " + fringesize + " - best heuristic: " + bestMetric
                + " - current heuristic: " + n.solution.size() + " + " + (n.metric - n.solution.size()) + " = " + n.metric;
    }

    private HashMap<Task, HashMap<GroundTask, List<method>>> getEfficientMethodRep(HashMap<Task, Set<GroundedDecompositionMethod>> methodsByTask) {
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

        Iterator<GroundLiteral> litIter = allLiterals.iterator();
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

        Iterator<GroundTask> actionIter = allActions.iterator();
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
