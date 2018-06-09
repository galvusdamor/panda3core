package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.*;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hAddhFFEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hFilter;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hLmCutEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hMaxEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncInfLmCut;
import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncrementInformation;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 10.05.17.
 */
public class gphRelaxedComposition extends GroundedProgressionHeuristic {
    protected RelaxedCompositionEncoding compEnc;
    private SasHeuristic heuristic;
    protected IncrementInformation inc;
    private int heuristicVal;

    @Override
    public BitSet helpfulOps() {
        return heuristic.helpfulOps;
    }

    @Override
    public String getName() {
        return "hhRelataxedComposition-with-" + heuristic.toString();
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    protected gphRelaxedComposition() {
    }

    public gphRelaxedComposition(SasPlusProblem flat,
                                 SasHeuristic.SasHeuristics heuristic,
                                 HashMap<Task, List<ProMethod>> methods,
                                 List<ProgressionPlanStep> initialTasks) {

        if (flat.createdFromStrips)
            this.compEnc = new RelaxedCompositionSTRIPS(flat);
        else
            this.compEnc = new RelaxedCompositionSAS(flat);

        if (heuristic == SasHeuristic.SasHeuristics.hLmCutOpt)
            this.compEnc.methodCosts = 0;

        System.out.println("Generating Relaxed Composition Model ...");
        this.compEnc.generateTaskCompGraph(methods, initialTasks);
        System.out.println(this.compEnc.getStatistics());

        if (heuristic == SasHeuristic.SasHeuristics.hAdd) {
            this.heuristic = new hAddhFFEq(this.compEnc, SasHeuristic.SasHeuristics.hAdd);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMax) {
            this.heuristic = new hMaxEq(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hFF) {
            this.heuristic = new hAddhFFEq(this.compEnc, SasHeuristic.SasHeuristics.hFF);
            supportsHelpfulActions = false;
        } else if (heuristic == SasHeuristic.SasHeuristics.hFFwithHA) {
            this.heuristic = new hAddhFFEq(this.compEnc, SasHeuristic.SasHeuristics.hFF);
            supportsHelpfulActions = true;
        } else if (heuristic == SasHeuristic.SasHeuristics.hCG) {
            this.heuristic = new hCausalGraph(this.compEnc);
        } else if ((heuristic == SasHeuristic.SasHeuristics.hLmCut)
                || (heuristic == SasHeuristic.SasHeuristics.hLmCutOpt)) {
            this.heuristic = new hLmCutEq(this.compEnc, false);
        } else if (heuristic == SasHeuristic.SasHeuristics.hIncLmCut) {
            this.inc = new IncInfLmCut();
            this.heuristic = new hLmCutEq(this.compEnc, true);
        } else if (heuristic == SasHeuristic.SasHeuristics.hFilter) {
            this.heuristic = new hFilter(this.compEnc);
        }
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        // prepare s0 and g
        // need to modify the facts that define top-down-reachability
        BitSet reachableActions = new BitSet(compEnc.numOfNonHtnActions);
        BitSet htnGoal = new BitSet(compEnc.numOfStateFeatures);

        for (ProgressionPlanStep first : newTN.getFirstAbstractTasks())
            prepareS0andG(first, reachableActions, htnGoal);

        for (ProgressionPlanStep first : newTN.getFirstPrimitiveTasks())
            prepareS0andG(first, reachableActions, htnGoal);

        BitSet s0 = compEnc.initS0(); // this is currently a dummy, i.e. an empty bitset
        for (int i = reachableActions.nextSetBit(0); i >= 0; i = reachableActions.nextSetBit(i + 1)) {
            compEnc.setReachable(s0, i);
        }
        s0.or(newTN.state);

        // prepare g
        BitSet g = new BitSet();
        for (int fact : compEnc.gList) { // the state-based goal
            g.set(fact);
        }

        for (int goalTask = htnGoal.nextSetBit(0); goalTask >= 0; goalTask = htnGoal.nextSetBit(goalTask + 1)) {
            compEnc.setReached(g, goalTask);
        }

        if (heuristic.isIncremental()) {
            int lastAction;
            if (m != null)
                lastAction = m.methodID;
            else
                lastAction = ps.taskIndex;

            gphRelaxedComposition res = new gphRelaxedComposition();
            res.heuristicVal = heuristic.calcHeu(lastAction, inc, s0, g);
            res.inc = this.heuristic.getIncInf();
            return res;
        } else {
            this.heuristicVal = heuristic.calcHeu(s0, g);
            return this;
        }
    }

    protected void prepareS0andG(ProgressionPlanStep ps, BitSet r, BitSet g) {
        if (!ps.done) {
            ps.reachableTasks = new BitSet();
            ps.goalFacts = new BitSet();
            ps.reachableTasks.or(compEnc.tdRechability.getReachableActions(ps.taskIndex));
            ps.goalFacts.set(ps.taskIndex);

            for (ProgressionPlanStep psSucc : ps.successorList) {
                prepareS0andG(psSucc, ps.reachableTasks, ps.goalFacts);
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
