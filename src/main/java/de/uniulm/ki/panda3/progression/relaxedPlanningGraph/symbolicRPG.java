package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by dhoeller on 29.06.16.
 */
public class symbolicRPG {

    public List<Set<GroundTask>> actions = new ArrayList<>();
    public List<Set<GroundLiteral>> facts = new ArrayList<>();
    public List<Set<GroundLiteral>> Gt = new ArrayList<>();

    public void build(Domain d, Plan p) {
        // init first fact layer

        Set<GroundLiteral> s0 = new HashSet<>();
        for (int i = 0; i < p.groundedInitialState().size(); i++) {
            GroundLiteral l = p.groundedInitialState().apply(i);
            if (l.isPositive())
                s0.add(l);
        }
        facts.add(s0);
        actions.add(new HashSet<GroundTask>()); // dummyTaskList

        int numGoals = p.groundedGoalState().size();
        Gt.add(getNewGoals(p.groundedGoalState(), new HashSet<GroundLiteral>(), s0));
        int fulfilled = Gt.get(0).size();
        boolean done = false;

        for (int i = 1; true; i++) {
            Set<GroundTask> a = getActionLayer(d, facts.get(i - 1));
            actions.add(a);

            Set<GroundLiteral> f = getFactLayer(a, facts.get(i - 1));
            facts.add(f);

            if (!done) {
                Gt.add(getNewGoals(p.groundedGoalState(), facts.get(i - 1), facts.get(i)));

                fulfilled += Gt.get(i).size();
                if (numGoals == fulfilled) {
                    done = true; // shall I break the loop?
                }
            }

            if (facts.get(i).size() == facts.get(i - 1).size())
                break;
        }
    }

    public int getFF() {
        List<String> actions = new ArrayList<>();
        int res = 0;
        for (int t = Gt.size() - 1; t >= 1; t--) {
            for (GroundLiteral g : Gt.get(t)) {
                GroundTask someAction = getAction(t, g);
                res++;
                actions.add(someAction.toString());
                for (int i = 0; i < someAction.substitutedPreconditions().size(); i++) {
                    GroundLiteral g2 = someAction.substitutedPreconditions().apply(i);
                    int fl = firstLayerContainingG(g2);
                    Gt.get(fl).add(g2);
                }
            }
        }
        /*for (int i = actionDelta.size() - 1; i >= 0; i--) {
            System.out.println(actionDelta.get(i));
        }*/
        return res;
    }

    private int firstLayerContainingG(GroundLiteral g) {
        for (int i = 0; i < facts.size(); i++) {
            Set<GroundLiteral> factList = facts.get(i);
            for (GroundLiteral gl : factList) {
                if (gl.equals(g)) {
                    return i;
                }
            }
        }
        return -1; // if I reach this point, I've got a problem...
    }

    private GroundTask getAction(int t, GroundLiteral g) {
        for (GroundTask gt : actions.get(t)) {
            for (int i = 0; i < gt.substitutedAddEffects().size(); i++) {
                GroundLiteral eff = gt.substitutedAddEffects().apply(i);
                if (eff.equals(g)) { // does that work?
                    return gt;
                }
            }
        }
        return null;// this should never happen
    }

    private Set<GroundLiteral> getNewGoals(Seq<GroundLiteral> goalLiterals, Set<GroundLiteral> prevLayer, Set<GroundLiteral> currLayer) {
        Set<GroundLiteral> res = new HashSet<>();
        for (int i = 0; i < goalLiterals.size(); i++) {
            GroundLiteral l = goalLiterals.apply(i);
            if ((!prevLayer.contains(l)) && (currLayer.contains(l))) {
                res.add(l);
            }
        }
        return res;
    }

    public Set<GroundTask> getActionLayer(Domain d, Set<GroundLiteral> f) {
        Set<GroundTask> result = new HashSet<>();
        for (int i = 0; i < d.primitiveTasks().size(); i++) {

            ReducedTask t = (ReducedTask) d.primitiveTasks().apply(i);
            for (int j = 0; j < t.instantiateGround().size(); j++) {
                GroundTask groundT = t.instantiateGround().apply(j);

                boolean fulfilled = true;
                for (int k = 0; k < groundT.substitutedPreconditions().size(); k++) {
                    if (!f.contains(groundT.substitutedPreconditions().apply(k))) {
                        fulfilled = false;
                        break;
                    }
                }
                if (fulfilled) {
                    result.add(groundT);
                }
            }
        }
        return result;
    }

    public Set<GroundLiteral> getFactLayer(Set<GroundTask> a, Set<GroundLiteral> groundLiterals) {
        Set<GroundLiteral> res = new HashSet<>();
        res.addAll(groundLiterals);
        for (GroundTask gt : a) {
            for (int i = 0; i < gt.substitutedAddEffects().size(); i++) {
                GroundLiteral gl = gt.substitutedAddEffects().apply(i);
                res.add(gl);
            }
        }
        return res;
    }
}
