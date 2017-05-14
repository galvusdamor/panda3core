package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.sasp.HtnCompositionEncoding;
import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.heuristics.SasHeuristic;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hAdd;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hLmCut;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hMax;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 10.05.17.
 */
public class proRcgSas implements htnGroundedProgressionHeuristic {
    private HtnCompositionEncoding compEnc;
    private SasHeuristic heuristic;
    private int heuristicVal;

    @Override
    public String getName() {
        return "hhRelataxedComposition";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    public proRcgSas(SasPlusProblem flat,
                     SasHeuristic.SasHeuristics heuristic,
                     HashMap<Task, List<method>> methods,
                     List<ProgressionPlanStep> initialTasks,
                     Set<Task> allActions) {

        this.compEnc = new HtnCompositionEncoding(flat);
        this.compEnc.geneateTaskCompGraph(methods, initialTasks);

        if (heuristic == SasHeuristic.SasHeuristics.hAdd) {
            this.heuristic = new hAdd(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMax) {
            this.heuristic = new hMax(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hLmCut) {
            this.heuristic = new hLmCut(this.compEnc);
        }
    }

    HashMap<ProgressionPlanStep, BitSet> reachableFrom = new HashMap<>();
    HashMap<ProgressionPlanStep, BitSet> goalPartOf = new HashMap<>();
    BitSet g;

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        // cleanup
        reachableFrom.remove(ps);
        goalPartOf.remove(ps);

        // prepare s0 and g
        // need to modify the facts that define top-down-reachability
        BitSet reachableActions = new BitSet(compEnc.numOfOperators);
        BitSet htnGoal = new BitSet(compEnc.numOfOperators);
        for (ProgressionPlanStep psI : newTN.getFirstAbstractTasks()) {
            collectTNiNodes(reachableActions, htnGoal, psI);
        }
        for (ProgressionPlanStep psI : newTN.getFirstPrimitiveTasks()) {
            collectTNiNodes(reachableActions, htnGoal, psI);
        }

        BitSet s0 = (BitSet) newTN.state.clone();
        int reachable = reachableActions.nextSetBit(0);
        while (reachable >= 0) {
            s0.set(compEnc.firstTdrIndex + reachable);
            reachable = reachableActions.nextSetBit(reachable + 1);
        }

        // prepare g
        BitSet g = new BitSet(compEnc.numOfStateFeatures);
        for (int fact : compEnc.gList) {
            g.set(fact);
        }

        int goalFact = htnGoal.nextSetBit(0);
        while (goalFact >= 0) {
            //System.out.println(compEnc.factStrs[goalFact]);
            g.set(goalFact);
            goalFact = htnGoal.nextSetBit(goalFact + 1);
        }

        this.heuristicVal = heuristic.calcHeu(s0, g);
        //if (this.goalRelaxedReachable() && this.heuristicVal > 5)
        //    System.out.println("h: " + this.heuristicVal);
        return this;
    }

    private void collectTNiNodes(BitSet reachableActions, BitSet htnGoal, ProgressionPlanStep ps) {
        BitSet partReach = reachableFrom.get(ps);
        BitSet partGoal;
        if (partReach == null) {
            partReach = new BitSet(compEnc.numOfOperators);
            partGoal = new BitSet(compEnc.numOfStateFeatures);
            LinkedList<ProgressionPlanStep> temp = new LinkedList<>();
            temp.add(ps);
            while (!temp.isEmpty()) {
                ProgressionPlanStep psInit = temp.removeFirst();
                int taskIndex = ProgressionNetwork.taskToIndex.get(psInit.getTask());
                partGoal.set(compEnc.firstTaskCompIndex + taskIndex);
                partReach.or(compEnc.tdRechability.getReachableActions(taskIndex));
                temp.addAll(psInit.successorList);
            }
            reachableFrom.put(ps, partReach);
            goalPartOf.put(ps, partGoal);
        } else
            partGoal = goalPartOf.get(ps);

        reachableActions.or(partReach);
        htnGoal.or(partGoal);
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
        return update(newTN, ps);
    }

    @Override
    public int getHeuristic() {
        return heuristicVal;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return heuristicVal < Integer.MAX_VALUE;
    }
}
