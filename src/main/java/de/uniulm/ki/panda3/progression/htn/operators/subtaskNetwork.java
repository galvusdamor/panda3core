package de.uniulm.ki.panda3.progression.htn.operators;

import de.uniulm.ki.panda3.progression.htn.search.proPlanStep;

import java.util.List;

/**
 * Created by dhoeller on 22.07.16.
 */
public class subtaskNetwork {
    private final proPlanStep[] steps;
    private final List<proPlanStep> firsts;
    private final List<proPlanStep> lasts;

    public subtaskNetwork(proPlanStep[] steps, List<proPlanStep> firsts, List<proPlanStep> lasts) {
        this.steps = steps;
        this.firsts = firsts;
        this.lasts = lasts;
    }

    public List<proPlanStep> getLastNodes() {
        return this.lasts;
    }

    public List<proPlanStep> getFirstNodes() {
        return this.firsts;
    }
}
