package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.gphRelaxedComposition;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hAddhFFEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hFilter;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hLmCutEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.hMaxEq;
import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncInfLmCut;
import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncrementInformation;
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.hCausalGraph;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMergeAndShrink;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
public class PureSASHeuristic extends GroundedProgressionHeuristic {
    protected IncrementInformation inc;
    private SasHeuristic heuristic;
    private SasPlusProblem flat;
    private int heuristicVal;

    private PureSASHeuristic(){

    }

    public PureSASHeuristic(SasPlusProblem flat,
                     SasHeuristic.SasHeuristics heuristic,
                     HashMap<Task, List<ProMethod>> methods,
                     List<ProgressionPlanStep> initialTasks){

        this.flat = flat;

        if (heuristic == SasHeuristic.SasHeuristics.hAdd) {
            this.heuristic = new hAddhFFEq(flat, SasHeuristic.SasHeuristics.hAdd);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMax) {
            this.heuristic = new hMaxEq(flat);
        } else if (heuristic == SasHeuristic.SasHeuristics.hFF) {
            this.heuristic = new hAddhFFEq(flat, SasHeuristic.SasHeuristics.hFF);
            supportsHelpfulActions = false;
        } else if (heuristic == SasHeuristic.SasHeuristics.hFFwithHA) {
            this.heuristic = new hAddhFFEq(flat, SasHeuristic.SasHeuristics.hFF);
            supportsHelpfulActions = true;
        } else if (heuristic == SasHeuristic.SasHeuristics.hCG) {
            this.heuristic = new hCausalGraph(flat);
        } else if ((heuristic == SasHeuristic.SasHeuristics.hLmCut)
                || (heuristic == SasHeuristic.SasHeuristics.hLmCutOpt)) {
            this.heuristic = new hLmCutEq(flat, false);
        } else if (heuristic == SasHeuristic.SasHeuristics.hIncLmCut) {
            this.inc = new IncInfLmCut();
            this.heuristic = new hLmCutEq(flat, true);
        } else if (heuristic == SasHeuristic.SasHeuristics.hFilter) {
            this.heuristic = new hFilter(flat);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMS) {
            this.heuristic = new ClassicalMergeAndShrink(flat);
        }

    }

    @Override
    public String getName() {
        return "Pure SAS+ Heuristic";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        BitSet g = new BitSet();
        // prepare g
        for (int fact : flat.gList) {
            g.set(fact);
        }

        BitSet s0 = new BitSet();
        s0.or(newTN.state);

        if (heuristic.isIncremental()) {
            int lastAction;
            if (m != null)
                lastAction = m.methodID;
            else
                lastAction = ps.taskIndex;

            PureSASHeuristic res = new PureSASHeuristic();
            res.heuristicVal = heuristic.calcHeu(lastAction, inc, s0, g);
            res.inc = this.heuristic.getIncInf();
            return res;
        } else {
            this.heuristicVal = heuristic.calcHeu(s0, g);
            return this;
        }
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
