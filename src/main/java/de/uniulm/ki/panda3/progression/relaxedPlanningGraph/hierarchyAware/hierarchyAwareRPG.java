package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.IRPG;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import scala.Tuple2;
import scala.collection.Iterator;

import java.util.*;

/**
 * Created by dhoeller on 21.08.16.
 */
public class hierarchyAwareRPG implements IRPG {
    Set<GroundTask> reachableTasks = new HashSet<>();
    Map<Predicate, Set<GroundLiteral>> reachableFacts = new HashMap<>();

    @Override
    public void build(Domain d, Plan p) {
        assert (singlePrimOrPartialOrder(d));
        Map<Predicate, List<DecompositionMethod>> precToMethod = getPrimMethods(d);

        Set<GroundLiteral> newlyReachable = getInit(p);
        addSetToMap(newlyReachable, reachableFacts);

        while (newlyReachable.size() > 0) {
            for (GroundLiteral newFact : newlyReachable) {
                List<DecompositionMethod> methods = precToMethod.get(newFact.predicate());
                for (DecompositionMethod m : methods) {
                    List<Integer> completed = new ArrayList<>();
                    List<PlanStep> possFirst = getPossFirst(m, completed);
                    List<List<Tuple2<Variable, Constant>>> partialGrounding = new ArrayList<>();

                    for (PlanStep ps : possFirst) {
                        ReducedTask reducedSchema = (ReducedTask) ps.schema();

                        for (int i = 0; i < reducedSchema.precondition().conjuncts().size(); i++) {
                            Literal lit = reducedSchema.precondition().conjuncts().apply(i);
                            Set<GroundLiteral> groundings = reachableFacts.get(lit.predicate());
                            java.util.Iterator<GroundLiteral> iterGr = groundings.iterator();
                            while (iterGr.hasNext()) {

                            }
                        }
                    }
                }
            }
        }
    }

    private boolean singlePrimOrPartialOrder(Domain d) {
        Iterator<DecompositionMethod> iter = d.decompositionMethods().iterator();
        while (iter.hasNext()) {
            DecompositionMethod m = iter.next();
            List<PlanStep> primPSs = containsPrim(m);
            if (primPSs.size() <= 1)
                continue;
            for (PlanStep ps1 : primPSs) {
                for (PlanStep ps2 : primPSs) {
                    if (!ps1.equals(ps2)) {
                        boolean pred = m.subPlan().orderingConstraints().lt(ps1, ps2);
                        boolean succ = m.subPlan().orderingConstraints().lt(ps2, ps1);

                        if ((!pred) && (!succ)) {
                            System.out.println("Step " + ps1.longInfo() + " not ordered to " + ps2.longInfo() + " in method " + m.name());
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /*
     * Returns all subtasks of the method's subtask network that might be next after the ids in completed have been executed
     */
    private List<PlanStep> getPossFirst(DecompositionMethod dm, List<Integer> completed) {
        List<PlanStep> res = new ArrayList<>();
        Iterator<PlanStep> psIter = dm.subPlan().planStepsWithoutInitGoal().iterator();

        while (psIter.hasNext()) {
            PlanStep ps = psIter.next();
            if ((ps.schema().isPrimitive()) && (!completed.contains(ps.id()))) {
                res.add(ps);
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = res.size() - 1; i > 0; i--) {
                PlanStep y = res.get(i);
                for (int j = 0; j < i; j++) {
                    PlanStep x = res.get(j);
                    if (dm.subPlan().orderingConstraints().lt(x, y)) {
                        res.remove(i);
                        changed = true;
                        continue;
                    }
                }
            }
        }

        return res;
    }

    private void addSetToMap(Set<GroundLiteral> s, Map<Predicate, Set<GroundLiteral>> m) {
        java.util.Iterator<GroundLiteral> iter = s.iterator();
        while (iter.hasNext()) {
            GroundLiteral e = iter.next();
            Set<GroundLiteral> groundings;
            if (m.containsKey(e.predicate())) {
                groundings = m.get(e);
            } else {
                groundings = new HashSet<>();
                m.put(e.predicate(), groundings);
            }
            groundings.add(e);
        }
    }

    private Set<GroundLiteral> getInit(Plan p) {
        Set<GroundLiteral> newlyReachable = new HashSet<>();
        for (int i = 0; i < p.groundedInitialState().size(); i++) {
            newlyReachable.add(p.groundedInitialState().apply(i));
        }
        return newlyReachable;
    }

    private Map<Predicate, List<DecompositionMethod>> getPrimMethods(Domain d) {
        Map<Predicate, List<DecompositionMethod>> precToMethod = new HashMap<>();
        Iterator<DecompositionMethod> iter = d.decompositionMethods().iterator();
        while (iter.hasNext()) {
            DecompositionMethod m = iter.next();
            List<PlanStep> primPSs = containsPrim(m);
            if ((symbolReachable(m.abstractTask())) && (primPSs.size() > 0)) {
                for (PlanStep ps : primPSs) {
                    if (!(ps.schema() instanceof ReducedTask)) {
                        System.out.println("ERROR: Task schema " + ps.schema().longInfo() + " is not a ReducedTask, please use a different grounding routine.");
                    }
                    ReducedTask reducedSchema = (ReducedTask) ps.schema();
                    for (int i = 0; i < reducedSchema.precondition().conjuncts().size(); i++) {
                        Literal lit = reducedSchema.precondition().conjuncts().apply(i);
                        List<DecompositionMethod> mList;
                        if (precToMethod.containsKey(lit.predicate())) {
                            mList = precToMethod.get(lit.predicate());
                        } else {
                            mList = new ArrayList<>();
                            precToMethod.put(lit.predicate(), mList);
                        }
                        mList.add(m);
                    }
                }
            }
        }
        return precToMethod;
    }

    private List<PlanStep> containsPrim(DecompositionMethod m) {
        List<PlanStep> primitiveTasks = new ArrayList<>();
        Iterator<PlanStep> iter = m.subPlan().planStepsWithoutInitGoal().iterator();
        while (iter.hasNext()) {
            PlanStep ps = iter.next();
            if (ps.schema().isPrimitive()) {
                primitiveTasks.add(ps);
            }
        }
        return primitiveTasks;
    }

    private boolean symbolReachable(Task task) {
        return true;
    }

    @Override
    public void build(Domain d, Plan p, Map<Task, Set<GroundTask>> onlyUse) {

    }

    @Override
    public Set<GroundTask> getApplicableActions() {
        return null;
    }

    @Override
    public Set<GroundLiteral> getReachableFacts() {
        return null;
    }
}
