package de.uniulm.ki.panda3.progression.htn.operators;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by dhoeller on 22.07.16.
 */
public class method {

    public final GroundedDecompositionMethod m; // this is the original method that is saved for printing the solution
    public GroundTask[] tasks;

    // these are the modifications for the SUB-tasks
    int[] actionID;
    List<method>[] methods;

    List<int[]> orderings;
    HashSet<Integer> firsts;
    HashSet<Integer> lasts;

    public method(GroundedDecompositionMethod dm) {
        this.m = dm;
        Seq<PlanStep> steps = dm.decompositionMethod().subPlan().planStepsWithoutInitGoal();
        this.tasks = new GroundTask[steps.size()];
        this.actionID = new int[steps.size()];
        this.methods = new List[steps.size()];

        for (int i = 0; i < steps.size(); i++) {
            tasks[i] = dm.subPlanPlanStepsToGrounded().get(steps.apply(i)).get();
        }

        this.orderings = new ArrayList<>();
        Seq<OrderingConstraint> orderings = dm.decompositionMethod().subPlan().orderingConstraints().minimalOrderingConstraints();
        for (int i = 0; i < orderings.size(); i++) {
            OrderingConstraint ordering = orderings.apply(i);
            int pre = steps.indexOf(ordering.before());
            int dec = steps.indexOf(ordering.after());
            if ((pre == -1) || (dec == -1))
                continue;
            int[] o = new int[2];
            o[0] = pre;
            o[1] = dec;
            this.orderings.add(o);
        }

        firsts = new HashSet<>();
        lasts = new HashSet<>();

        for (int i = 0; i < tasks.length; i++) {
            firsts.add(i);
            lasts.add(i);
        }

        for (int[] o : this.orderings) {
            firsts.remove(o[1]);
            lasts.remove(o[0]);
        }
    }

    public void finalizeMethod() {
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i].task().isPrimitive()) {
                actionID[i] = operators.ActionToIndex.get(tasks[i]);
            } else {
                this.methods[i] = operators.methods.get(tasks[i].task()).get(tasks[i]);

                assert (this.methods[i] != null);
                assert (this.methods[i].size() > 0);
                actionID[i] = -1;
            }
        }

    }

    public subtaskNetwork instantiate() {
        ProgressionPlanStep[] steps = new ProgressionPlanStep[this.tasks.length];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = new ProgressionPlanStep(this.tasks[i]);
            steps[i].action = this.actionID[i];
            steps[i].methods = this.methods[i];
        }
        for (int[] o : this.orderings) {
            steps[o[0]].successorList.add(steps[o[1]]);
        }
        List<ProgressionPlanStep> f = new LinkedList<>();
        for (Integer i : this.firsts) {
            f.add(steps[i]);
        }
        List<ProgressionPlanStep> l = new LinkedList<>();
        for (Integer i : this.lasts) {
            l.add(steps[i]);
        }
        return new subtaskNetwork(steps, f, l);
    }

}
