package de.uniulm.ki.panda3.progression.htn.representation;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

import java.util.List;

/**
 * Created by dhoeller on 22.07.16.
 */
public class ProSubtaskNetwork {
    private final ProgressionPlanStep[] steps;
    private final List<ProgressionPlanStep> firsts;
    private final List<ProgressionPlanStep> lasts;

    public ProSubtaskNetwork(ProgressionPlanStep[] steps, List<ProgressionPlanStep> firsts, List<ProgressionPlanStep> lasts) {
        this.steps = steps;
        this.firsts = firsts;
        this.lasts = lasts;
    }

    public List<ProgressionPlanStep> getLastNodes() {
        return this.lasts;
    }

    public List<ProgressionPlanStep> getFirstNodes() {
        return this.firsts;
    }

    public int size() {
        return steps.length;
    }
}
