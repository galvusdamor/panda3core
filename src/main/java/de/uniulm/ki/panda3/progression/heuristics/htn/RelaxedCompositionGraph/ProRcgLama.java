package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedCompositionGraph;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dh on 17.05.17.
 * This is a implementation to test landmark generators or other heuristics working via preprocessing
 */
public class ProRcgLama extends ProRcgSas {
    private HtnCompositionEncoding compEnc;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }


    public ProRcgLama(SasPlusProblem flat,
                      HashMap<Task, List<ProMethod>> methods,
                      List<ProgressionPlanStep> initialTasks,
                      ProgressionNetwork init) {

        this.compEnc = new HtnCompositionEncoding(flat);
        this.compEnc.generateTaskCompGraph(methods, initialTasks);

        ProgressionPlanStep ps = init.getFirstAbstractTasks().get(0);
        ProgressionNetwork init2 = init.decompose(ps, ps.methods.get(0));
        prepareLandmarks(init2);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/dh/Schreibtisch/temp-sas/sas.out"));
            String s = this.compEnc.ourRepToSaspString();
            bw.write(s);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void prepareLandmarks(ProgressionNetwork init) {
        // prepare s0 and g
        // need to modify the facts that define top-down-reachability
        /*
        BitSet reachableActions = new BitSet(compEnc.numOfOperators);
        BitSet htnGoal = new BitSet(compEnc.numOfOperators);

        for (ProgressionPlanStep ps2 : init.getFirstAbstractTasks())
            prepareS0andG(ps2, reachableActions, htnGoal);

        for (ProgressionPlanStep ps2 : init.getFirstPrimitiveTasks())
            prepareS0andG(ps2, reachableActions, htnGoal);

        BitSet s0 = (BitSet) init.state.clone();
        BitSet g = new BitSet(compEnc.numOfStateFeatures);

        int reachable = reachableActions.nextSetBit(0);
        while (reachable >= 0) {
            s0.set(compEnc.firstTdrIndex + reachable);
            reachable = reachableActions.nextSetBit(reachable + 1);
        }

        int[] s0List = new int[s0.cardinality()];
        int s0Fact = s0.nextSetBit(0);
        int i = 0;
        while (s0Fact >= 0) {
            s0List[i++] = s0Fact;
            s0Fact = s0.nextSetBit(s0Fact + 1);
        }
        assert i == s0List.length;


        // prepare g
        for (int fact : compEnc.gList) {
            g.set(fact);
        }

        int goalFact = htnGoal.nextSetBit(0);
        while (goalFact >= 0) {
            g.set(goalFact + this.compEnc.firstTaskCompIndex);
            goalFact = htnGoal.nextSetBit(goalFact + 1);
        }

        int[] gList = new int[g.cardinality()];
        int gFact = g.nextSetBit(0);
        i = 0;
        while (gFact >= 0) {
            gList[i++] = gFact;
            gFact = g.nextSetBit(gFact + 1);
        }
        assert i == gList.length;
        this.compEnc.s0List = s0List;
        this.compEnc.gList = gList;*/
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        return null;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        return null;
    }

    @Override
    public int getHeuristic() {
        return 0;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return false;
    }
}
