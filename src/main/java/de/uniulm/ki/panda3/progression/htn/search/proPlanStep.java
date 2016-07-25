package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhoeller on 22.07.16.
 */
public class proPlanStep {
    private final GroundTask task;
    public List<proPlanStep> successorList = new LinkedList<>(); // todo: Array or LinkedList?
    public final boolean isPrimitive;

    public int action;
    public List<method> methods;

    public GroundTask getTask() {
        return task;
    }

    public proPlanStep(GroundTask task) {
        this.task = task;
        this.isPrimitive = task.task().isPrimitive();
    }
}
