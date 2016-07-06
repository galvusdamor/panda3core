package de.uniulm.ki.panda3.progression.proSearch;

import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.efficientRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.symbolicRPG;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.GroundedPlanningGraph;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import scala.Tuple2;
import scala.collection.Seq;

import java.util.*;
import java.util.BitSet;
import java.util.Set;

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
public class planningInstance {
    HashMap<GroundLiteral, Integer> LiteralToIndex;
    GroundLiteral[] IndexToLiteral;

    HashMap<GroundTask, Integer> ActionToIndex;
    GroundTask[] IndexToAction;

    int numStateFeatures;
    int numActions;

    // The following representation is used in state-transition.
    // Is is a standard Strips set representation.
    BitSet[] prec;
    BitSet[] add;
    BitSet[] del;

    // The following representation is used in rpg-calculation
    int[][] precList; // [0][1, 3] means that action 0 needs state-features 1 and 3 to be applicable
    int[][] addList;  // [1][2, 5] means that action 1 adds state-features 2 and 5

    BitSet goal;
    int[] goalList;

    public void plan(Domain d, Plan p) {
        boolean verbose = false;
        boolean relaxFirstGraph = false;

        long totaltime = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        Set<GroundLiteral> allLiterals;
        Set<GroundTask> allActions;
        if (relaxFirstGraph) {
            System.out.print("Building initial relaxed planning graph");
            symbolicRPG rpg = new symbolicRPG();
            rpg.build(d, p);
            allLiterals = rpg.facts.get(rpg.facts.size() - 1);
            allActions = rpg.actions.get(rpg.actions.size() - 1);
        } else {
            allLiterals = new HashSet<>();
            allActions = new HashSet<>();
            System.out.print("Building initial (non-relaxed) planning graph");
            GroundedPlanningGraph gpg = GroundedPlanningGraph.apply(d, p.groundedInitialStateOnlyPositive(), true, false);

            scala.collection.Iterator<GroundLiteral> iterAllFacts = gpg.reachableGroundLiterals().iterator();
            while (iterAllFacts.hasNext()) {
                allLiterals.add(iterAllFacts.next());
            }

            scala.collection.Iterator<GroundTask> iterAllActions = gpg.reachableGroundPrimitiveActions().iterator();
            while (iterAllActions.hasNext())
                allActions.add(iterAllActions.next());
        }

        System.out.println("(" + (System.currentTimeMillis() - time) + " ms)");
        time = System.currentTimeMillis();

        numStateFeatures = allLiterals.size();
        numActions = allActions.size();
        System.out.println("Graph contains " + numActions + " ground actions");
        System.out.println("Graph contains " + numStateFeatures + " environment facts");

        System.out.println("\nPreprocessing planning instance");
        ToCompactRepresentation(allLiterals, allActions);

        Tuple2<BitSet, int[]> s0 = getBitVector(p.groundedInitialState());
        Tuple2<BitSet, int[]> g = getBitVector(p.groundedGoalState());
        goalList = g._2();
        goal = g._1();
        System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms.");
        time = System.currentTimeMillis();

        if (verbose) {
            System.out.println("\nList of grounded actions:");
            for (int i = 0; i < numActions; i++) {
                System.out.println(proPrinter.actionTupleToStr(IndexToAction[i], prec[i], add[i], del[i], numStateFeatures, IndexToLiteral));
            }
        }

        System.out.println("\nStarting search");
        List<Integer> solution = null;
        int searchnodes = 1;

        efficientRPG.prec = prec;
        efficientRPG.add = add;
        efficientRPG.precList = precList;
        efficientRPG.addList = addList;
        efficientRPG.numActions = numActions;
        efficientRPG.numStateFeatures = numStateFeatures;
        efficientRPG.goalList = goalList;

        PriorityQueue<searchNode> fringe = new PriorityQueue<>();
        fringe.add(new searchNode(s0._1()));
        int bestMetric = Integer.MAX_VALUE;

        while (!fringe.isEmpty()) {
            searchNode n = fringe.poll();

            BitSet temp = (BitSet) n.state.clone();
            temp.and(goal);
            if (temp.equals(goal)) {
                solution = n.tasks;
                break;
            }
            for (Integer a : n.getApplicableActions()) {
                temp = (BitSet) n.state.clone();
                temp.and(prec[a]);
                if (!temp.equals(prec[a]))
                    continue;

                temp = (BitSet) n.state.clone();
                temp.andNot(del[a]);
                temp.or(add[a]);

                searchNode node = new searchNode(temp, n.tasks, a);
                if (node.rpg.goalRelaxedReachable) {
                    fringe.add(node);
                    if (node.metric < bestMetric) {
                        bestMetric = node.metric;
                        System.out.println("generated nodes: " + searchnodes + " - fringe size: " + fringe.size() + " - best heuristic: " + bestMetric + " - current heuristic: " + n.metric);
                    }
                }
                searchnodes++;
                if ((searchnodes % 1000) == 0)
                    System.out.println("generated nodes: " + searchnodes + " - fringe size: " + fringe.size() + " - best heuristic: " + bestMetric + " - current heuristic: " + n.metric);
            }
        }

        System.out.println("Generated search nodes (total): " + searchnodes);
        System.out.println("Search time: " + (System.currentTimeMillis() - time) + " ms");

        int n = 1;
        if (solution != null) {
            System.out.println("\nFound a solution:");
            for (Integer a : solution) {
                System.out.println(n + " " + proPrinter.actionToStr(IndexToAction[a]));
                n++;
            }
            //time = System.currentTimeMillis();
            //System.out.println("\nInferring least constraining causal links");
            //inferCausalLinks(s0._1(), solution);
            //System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms");
        } else System.out.println("Problem unsolvable.");
        System.out.println("Total program runtime: " + (System.currentTimeMillis() - totaltime) + " ms");
    }

    private void inferCausalLinks(BitSet s0, List<Integer> solution) {
        List<Integer> sol = new LinkedList<>();
        sol.addAll(solution);
        sol.add(0, -1); // add dummy to simplify index-magic
        for (int consumerI = 1; consumerI < sol.size(); consumerI++) {
            int consumer = sol.get(consumerI);
            int fact = prec[consumer].nextSetBit(0);
            boolean found = false;
            while (fact >= 0) {
                // Bit lookup tables
                BitSet relevantAdds = new BitSet(sol.size());
                if (s0.get(fact))
                    relevantAdds.set(0);
                BitSet relevantDels = new BitSet(sol.size());
                for (int ai = 1; ai < sol.size(); ai++) {
                    int a = sol.get(ai);
                    if (add[a].get(fact))
                        relevantAdds.set(ai);
                    if (del[a].get(fact))
                        relevantDels.set(ai);
                }
                int offset = 0;
                while (offset < consumerI) { // the equality is due to the first bit in the vectors that represents s0
                    int supporterI = relevantAdds.nextSetBit(offset);
                    boolean nothreat = ((supporterI == (consumerI - 1)) || (relevantDels.get(supporterI + 1, consumerI - 1).nextSetBit(0) == -1));
                    if (nothreat) {
                        System.out.println(supporterI + " -> " + consumerI + " " + proPrinter.literalToStr(IndexToLiteral[fact]));
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
                        offset = relevantDels.get(supporterI + 1, consumerI - 1).nextSetBit(0) + 1;
                }
                fact = prec[consumer].nextSetBit(fact + 1);
            }
            if (!found) {
                System.out.println("There is no supporter for a precondition");
            }
        }
    }

    private boolean noThreat(List<Integer> solution, int producer, int consumer, int effI) {
        for (int i = producer + 1; i < consumer; i++) {
            int action = solution.get(i);
            if (del[action].get(effI)) {
                return false;
            }
        }
        return true;
    }

    private Tuple2<BitSet, int[]> getBitVector(Seq<GroundLiteral> groundLiteralSeq) {
        BitSet state = new BitSet(numStateFeatures);
        int[] list = new int[groundLiteralSeq.size()];
        for (int j = 0; j < groundLiteralSeq.size(); j++) {
            GroundLiteral gl = groundLiteralSeq.apply(j);
            if (!gl.isPositive())
                continue;
            int index = LiteralToIndex.get(gl);
            list[j] = index;
            state.set(index);
        }
        return new Tuple2<>(state, list);
    }

    private void ToCompactRepresentation(Set<GroundLiteral> allLiterals, Set<GroundTask> allActions) {
        LiteralToIndex = new HashMap<>();
        IndexToLiteral = new GroundLiteral[numStateFeatures];

        Iterator<GroundLiteral> litIter = allLiterals.iterator();
        int i = 0;
        while (litIter.hasNext()) {
            GroundLiteral g = litIter.next();
            LiteralToIndex.put(g, i);
            IndexToLiteral[i] = g;
            i++;
        }

        ActionToIndex = new HashMap<>();
        IndexToAction = new GroundTask[numActions];

        prec = new BitSet[numActions];
        precList = new int[numActions][];
        add = new BitSet[numActions];
        addList = new int[numActions][];
        del = new BitSet[numActions];

        Iterator<GroundTask> actionIter = allActions.iterator();
        i = 0;
        List<GroundLiteral> deletedEffects = new LinkedList<>();
        while (actionIter.hasNext()) {
            GroundTask action = actionIter.next();
            ActionToIndex.put(action, i);
            IndexToAction[i] = action;

            prec[i] = new BitSet(numStateFeatures);
            precList[i] = new int[action.substitutedPreconditions().size()];
            for (int j = 0; j < action.substitutedPreconditions().size(); j++) {
                GroundLiteral gl = action.substitutedPreconditions().apply(j);
                int index = LiteralToIndex.get(gl);
                precList[i][j] = index;
                prec[i].set(index);
            }

            add[i] = new BitSet(numStateFeatures);
            addList[i] = new int[action.substitutedAddEffects().size()];
            for (int j = 0; j < action.substitutedAddEffects().size(); j++) {
                GroundLiteral gl = action.substitutedAddEffects().apply(j);
                int index = LiteralToIndex.get(gl);
                addList[i][j] = index;
                add[i].set(index);
            }

            del[i] = new BitSet(numStateFeatures);
            for (int j = 0; j < action.substitutedDelEffects().size(); j++) {
                int index = -1;
                GroundLiteral gl = action.substitutedDelEffects().apply(j);
                for (int de = 0; de < IndexToLiteral.length; de++) {
                    if (featureEqual(IndexToLiteral[de], gl)) {
                        index = de;
                        break;
                    }
                }
                if (index == -1) {
                    deletedEffects.add(gl);
                } else
                    del[i].set(index);
            }
            i++;

            // deleted delete-effects
            if (deletedEffects.size() > 0) {
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
