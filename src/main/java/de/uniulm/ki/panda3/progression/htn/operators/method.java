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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("task ");
        sb.append(this.m.groundAbstractTask().longInfo());
        sb.append("\n t { 0: ");
        if (subtasks.length > 0) {
            sb.append(subtasks[0].longInfo());
        }
        for (int i = 1; i < subtasks.length; i++) {
            sb.append("\n     ");
            sb.append(i);
            sb.append(": ");
            sb.append(subtasks[i].longInfo());
        }
        sb.append("}\n < { ");
        if (orderings.size() > 0) {
            int[] ord = orderings.get(0);
            sb.append(ord[0]);
            sb.append("<");
            sb.append(ord[1]);

        }
        for (int i = 1; i < orderings.size(); i++) {
            int[] ord = orderings.get(i);
            sb.append(", ");
            sb.append(ord[0]);
            sb.append("<");
            sb.append(ord[1]);
        }
        sb.append(" }\n");
        return sb.toString();
    }

    public final GroundedDecompositionMethod m; // this is the original method that is saved for printing the solution
    public GroundTask[] subtasks; // these are the subtasks
    public int numDistinctSubTasks = 0;

    // these are the modifications for the SUB-tasks
    int[] actionID;
    List<method>[] methods;

    List<int[]> orderings;
    HashSet<Integer> firsts;
    HashSet<Integer> lasts;
    public int numberOfPrimSubtasks = 0;
    public int numberOfAbsSubtasks;

    public method(GroundedDecompositionMethod dm) {
        this.m = dm;
        Seq<PlanStep> steps = dm.decompositionMethod().subPlan().planStepsWithoutInitGoal();
        this.subtasks = new GroundTask[steps.size()];
        this.actionID = new int[steps.size()];
        this.methods = new List[steps.size()];

        for (int i = 0; i < steps.size(); i++) {
            subtasks[i] = dm.subPlanPlanStepsToGrounded().get(steps.apply(i)).get();
            if (subtasks[i].task().isPrimitive())
                numberOfPrimSubtasks++;
        }
        numberOfAbsSubtasks = steps.size() - numberOfPrimSubtasks;

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

        for (int i = 0; i < subtasks.length; i++) {
            firsts.add(i);
            lasts.add(i);
        }

        for (int[] o : this.orderings) {
            firsts.remove(o[1]);
            lasts.remove(o[0]);
        }
    }

    public void finalizeMethod() {
        Set<GroundTask> distinctTasks = new HashSet<>();
        for(GroundTask t : this.subtasks){
            distinctTasks.add(t);
        }
        this.numDistinctSubTasks = distinctTasks.size();
        for (int i = 0; i < subtasks.length; i++) {
            if (subtasks[i].task().isPrimitive()) {
                actionID[i] = operators.ActionToIndex.get(subtasks[i]);
            } else {
                this.methods[i] = operators.methods.get(subtasks[i].task()).get(subtasks[i]);

                assert (this.methods[i] != null);
                assert (this.methods[i].size() > 0);
                actionID[i] = -1;
            }
        }
    }

    public subtaskNetwork instantiate() {
        ProgressionPlanStep[] steps = new ProgressionPlanStep[this.subtasks.length];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = new ProgressionPlanStep(this.subtasks[i]);
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
