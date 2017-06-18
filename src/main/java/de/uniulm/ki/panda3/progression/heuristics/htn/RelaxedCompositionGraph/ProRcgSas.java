package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedCompositionGraph;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.*;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 10.05.17.
 */
public class ProRcgSas implements GroundedProgressionHeuristic {
    static protected HtnCompositionEncoding compEnc;
    static private SasHeuristic heuristic;
    protected IncrementInformation inc;
    private int heuristicVal;

    @Override
    public String getName() {
        return "hhRelataxedComposition";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    protected ProRcgSas() {
    }

    public ProRcgSas(SasPlusProblem flat,
                     SasHeuristic.SasHeuristics heuristic,
                     HashMap<Task, List<ProMethod>> methods,
                     List<ProgressionPlanStep> initialTasks) {

        this.compEnc = new HtnCompositionEncoding(flat);
        this.compEnc.generateTaskCompGraph(methods, initialTasks);
        System.out.println("Generating Relaxed Composition Model ...");
        System.out.println(this.compEnc.getStatistics());

        if (heuristic == SasHeuristic.SasHeuristics.hAdd) {
            this.heuristic = new hAdd(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMax) {
            this.heuristic = new hMax(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hFF) {
            this.heuristic = new hFF(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hLmCut) {
            this.heuristic = new hLmCut(this.compEnc, false);
        } else if (heuristic == SasHeuristic.SasHeuristics.hIncLmCut) {
            this.heuristic = new hLmCut(this.compEnc, true);
        }
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        // prepare s0 and g
        // need to modify the facts that define top-down-reachability
        BitSet reachableActions = new BitSet(compEnc.numOfOperators);
        BitSet htnGoal = new BitSet(compEnc.numOfStateFeatures);

        for (ProgressionPlanStep first : newTN.getFirstAbstractTasks())
            prepareS0andG(first, reachableActions, htnGoal);

        for (ProgressionPlanStep first : newTN.getFirstPrimitiveTasks())
            prepareS0andG(first, reachableActions, htnGoal);

        BitSet s0 = (BitSet) newTN.state.clone();
        BitSet g = new BitSet(compEnc.numOfStateFeatures);

        int reachable = reachableActions.nextSetBit(0);
        while (reachable >= 0) {
            s0.set(compEnc.firstTdrIndex + reachable);
            reachable = reachableActions.nextSetBit(reachable + 1);
        }

        // prepare g
        for (int fact : compEnc.gList) {
            g.set(fact);
        }

        int goalFact = htnGoal.nextSetBit(0);
        while (goalFact >= 0) {
            g.set(goalFact + this.compEnc.firstTaskCompIndex);
            //System.out.println(compEnc.factStrs[goalFact + this.compEnc.firstTaskCompIndex]); // for debugging
            goalFact = htnGoal.nextSetBit(goalFact + 1);
        }

        if (heuristic.isIncremental()) {
            int lastAction;
            if (m != null)
                lastAction = compEnc.MethodToIndex.get(m);
            else
                lastAction = ps.taskIndex;

            ProRcgSas res = new ProRcgSas();
            res.heuristicVal = this.heuristic.calcHeu(lastAction, inc, s0, g);
            res.inc = this.heuristic.getIncInf();
            return res;
        } else {
            this.heuristicVal = this.heuristic.calcHeu(s0, g);
            return this;
        }
    }

    protected void prepareS0andG(ProgressionPlanStep ps, BitSet r, BitSet g) {
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
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        return update(newTN, ps, null);
    }

    @Override
    public int getHeuristic() {
        return this.heuristicVal;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return heuristicVal != SasHeuristic.cUnreachable;
    }
}
