package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.Map;
import java.util.Set;

/**
 * Created by dhoeller on 21.08.16.
 */
public interface IRPG {
    void build(Domain d, Plan p);

    void build(Domain d, Plan p, Map<Task, Set<GroundTask>> onlyUse);

    Set<GroundTask> getApplicableActions();

    Set<GroundLiteral> getReachableFacts();

    int numOfReachableFacts();
}
