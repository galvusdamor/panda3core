package de.uniulm.ki.panda3.progression.bottomUpGrounder;

import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.logic.Constant;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.seqProviderList;
import scala.collection.Iterator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dhoeller on 19.07.16.
 */
public class groundingUtil {

    public static Set<GroundTask> getFullyGroundTN(Plan p) {
        Set<GroundTask> tdReachableTasks = new HashSet<>();
        for (int i = 0; i < p.planStepsWithoutInitGoal().size(); i++) {
            PlanStep ps = p.planStepsWithoutInitGoal().apply(i);
            Iterator<Variable> iter = ps.arguments().iterator();
            seqProviderList<Constant> parameter = new seqProviderList<>();
            while (iter.hasNext()) {
                Variable v = iter.next();
                Constant c = null;
                for (int j = 0; j < p.variableConstraints().constraints().size(); j++) {
                    VariableConstraint vc = p.variableConstraints().constraints().apply(j);
                    if ((vc instanceof Equal)
                            && (((Equal) vc).left().equals(v))
                            && (((Equal) vc).right() instanceof Constant)) {
                        c = (Constant) ((Equal) vc).right();
                        break;
                    }
                }
                if (c == null) {
                    System.out.println("Error while fully grounding task network");
                }
                parameter.add(c);
            }
            tdReachableTasks.add(new GroundTask(ps.schema(), parameter.result()));
        }
        return tdReachableTasks;
    }
}
