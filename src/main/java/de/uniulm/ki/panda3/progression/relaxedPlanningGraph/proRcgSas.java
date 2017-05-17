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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by dh on 10.05.17.
 */
public class proRcgSas implements htnGroundedProgressionHeuristic {
    protected HtnCompositionEncoding compEnc;
    private SasHeuristic heuristic;
    private int heuristicVal;

    @Override
    public String getName() {
        return "hhRelataxedComposition";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    protected proRcgSas(){}

    public proRcgSas(SasPlusProblem flat,
                     SasHeuristic.SasHeuristics heuristic,
                     HashMap<Task, List<method>> methods,
                     List<ProgressionPlanStep> initialTasks) {

        this.compEnc = new HtnCompositionEncoding(flat);
        this.compEnc.geneateTaskCompGraph(methods, initialTasks);
        System.out.println("Generating Relaxed Composition Model ...");
        System.out.println(this.compEnc.getStatistics());

        if (heuristic == SasHeuristic.SasHeuristics.hAdd) {
            this.heuristic = new hAdd(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hMax) {
            this.heuristic = new hMax(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hLmCut) {
            this.heuristic = new hLmCut(this.compEnc);
        }
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        // prepare s0 and g
        // need to modify the facts that define top-down-reachability
        BitSet reachableActions = new BitSet(compEnc.numOfOperators);
        BitSet htnGoal = new BitSet(compEnc.numOfOperators);

        for (ProgressionPlanStep ps2 : newTN.getFirstAbstractTasks())
            prepareS0andG(ps2, reachableActions, htnGoal);

        for (ProgressionPlanStep ps2 : newTN.getFirstPrimitiveTasks())
            prepareS0andG(ps2, reachableActions, htnGoal);

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
            goalFact = htnGoal.nextSetBit(goalFact + 1);
        }

        this.heuristicVal = heuristic.calcHeu(s0, g);
        return this;
    }

    protected void prepareS0andG(ProgressionPlanStep ps, BitSet r, BitSet g) {
        if (!ps.done) {
            ps.r = new BitSet(compEnc.numOfOperators);
            ps.g = new BitSet(compEnc.numOfStateFeatures);
            ps.r.or(compEnc.tdRechability.getReachableActions(ps.taskIndex));
            ps.g.set(ps.taskIndex);

            for (ProgressionPlanStep ps2 : ps.successorList) {
                prepareS0andG(ps2, ps.r, ps.g);
            }
            ps.done = true;
        }
        r.or(ps.r);
        g.or(ps.g);
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
