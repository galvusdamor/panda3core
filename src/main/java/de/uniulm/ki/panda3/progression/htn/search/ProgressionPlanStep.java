package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by dhoeller on 22.07.16.
 */
public class ProgressionPlanStep {
    private final GroundTask task;
    public Set<ProgressionPlanStep> successorList = new HashSet<>();
    public final boolean isPrimitive;

    public int action;
    public List<method> methods;

    public GroundTask getTask() {
        return task;
    }

    public ProgressionPlanStep(GroundTask task) {
        this.task = task;
        this.isPrimitive = task.task().isPrimitive();
    }
}
