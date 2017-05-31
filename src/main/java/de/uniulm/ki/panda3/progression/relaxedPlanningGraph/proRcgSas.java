package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.sasp.HtnCompositionEncoding;
import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.heuristics.*;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 10.05.17.
 */
public class proRcgSas implements htnGroundedProgressionHeuristic {
    static protected HtnCompositionEncoding compEnc;
    static private SasHeuristic heuristic;
    private IncrementInformation incInf;
    private int heuristicVal;

    @Override
    public String getName() {
        return "hhRelataxedComposition";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    protected proRcgSas() {
    }

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
        } else if (heuristic == SasHeuristic.SasHeuristics.hFF) {
            this.heuristic = new hFF(this.compEnc);
        } else if (heuristic == SasHeuristic.SasHeuristics.hLmCut) {
            this.heuristic = new hLmCut(this.compEnc, false);
        } else if (heuristic == SasHeuristic.SasHeuristics.hIncLmCut) {
            this.heuristic = new hLmCut(this.compEnc, true);
            this.incInf = new IncInfLmCut();
        }
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
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

        //this.heuristicVal = heuristic.calcHeu(s0, g, ps.taskIndex);
        if (heuristic.isIncremental()) {
            int lastAction;
            if (m != null)
                lastAction = compEnc.MethodToIndex.get(m);
            else
                lastAction = ps.taskIndex;
            heuristic.setIncrement(lastAction, this.incInf);
        }
        int heuVal = heuristic.calcHeu(s0, g);
        if (heuristic.isIncremental()) {
            proRcgSas res = new proRcgSas();
            res.incInf = heuristic.getIncrement();
            res.heuristicVal = heuVal;
            return res;
        } else {
            this.heuristicVal = heuVal;
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
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        return update(newTN, ps, null);
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
