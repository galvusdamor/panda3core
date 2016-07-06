package de.uniulm.ki.panda3.progression.proSearch;

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.efficientRPG;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhoeller on 30.06.16.
 */
public class searchNode implements Comparable<searchNode> {

    public BitSet state;
    public EfficientPlan htn;
    int metric;
    List<Integer> tasks = new LinkedList<>();
    public efficientRPG rpg = new efficientRPG();

    public searchNode(BitSet state) {
        this.state = state;
        rpg.build(state);
        //this.metric = rpg.getFF() + tasks.size();
        this.metric = rpg.getFF();
        //this.metric =0;
    }

    public searchNode(BitSet state, EfficientPlan htn) {
        this.state = state;
        this.htn = htn;
        rpg.build(state);
        //this.metric = rpg.getFF() + tasks.size();
        this.metric = rpg.getFF();
        //this.metric =0;
    }

    public searchNode(BitSet state, List<Integer> as, int a) {
        this(state);
        this.tasks.addAll(as);
        this.tasks.add(a);
    }

    public List<Integer> getApplicableActions() {
        return rpg.actionDelta.get(1);
    }

    public List<GroundedDecompositionMethod> getApplicableMethods() {
        // todo
        return new LinkedList<>();
    }

    @Override
    public int compareTo(searchNode searchNode) {
        return (this.metric - searchNode.metric);
    }
}
