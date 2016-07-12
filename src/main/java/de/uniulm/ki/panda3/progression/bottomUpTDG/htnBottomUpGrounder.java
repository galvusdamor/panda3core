package de.uniulm.ki.panda3.progression.bottomUpTDG;

import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.NotEqual;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Constant;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by dhoeller on 07.07.16.
 *
 * Not implemented yet:
 * - Variable constraints other than equal and unequal
 * - Full grounding of unconstraint variables (for both first and next parameters)
 * - Epsilon-methods
 */
public class htnBottomUpGrounder {
    Set<Task> reachableTasks = new HashSet<>();
    HashMap<Task, Set<DecompositionMethod>> needToReGround = new HashMap<>();
    Set<GroundedDecompositionMethod> groundMethods = new HashSet<>();
    HashMap<Task, Set<GroundTask>> groundingsByTask = new HashMap<>();

    public htnBottomUpGrounder(Domain d, Plan p, Set<GroundTask> groundActions) {
        // get reachable task symbols
        long time = System.currentTimeMillis();
        System.out.print("Inferring task symbols reachable from initial tn ");
        getReachableTaskSymbols(d, p);
        System.out.println("(" + (System.currentTimeMillis() - time) + " ms)");
        System.out.println("Number of reachable task symbols: " + reachableTasks.size());

        // get reachable ground methods and abstract tasks
        time = System.currentTimeMillis();
        LinkedList<DecompositionMethod> todoList = getMethodsWithSolelyPrimSubtasks(d);

        // generate lookup table
        actionsToLookupTable(groundActions);

        Set<Task> deltedByEpsilonMethods = getEpsilonMethods(d);
        if (deltedByEpsilonMethods.size() > 0) {
            System.out.println("Found " + deltedByEpsilonMethods.size() + " tasks that might be deleted by psilon-methods.");
            System.out.println("Don't know what to do with them");
        }
        int numTasks = 0;
        mainloop:
        while (!todoList.isEmpty()) {
            DecompositionMethod m = todoList.removeFirst();

            // only if subtask is primitive or seeded
            for (int i = 0; i < m.subPlan().planSteps().size(); i++) {
                PlanStep ps = m.subPlan().planSteps().apply(i);
                if ((ps.id() > 0) && (!groundingsByTask.containsKey(ps.schema())))
                    continue mainloop;
            }

            // get partial groundings
            List<List<Tuple2>> partialGroundings = null;
            boolean firstPS = true;
            for (int i = 0; i < m.subPlan().planSteps().size(); i++) {
                PlanStep ps = m.subPlan().planSteps().apply(i);
                if (ps.id() < 0) // init and goal
                    continue;
                if (firstPS) { // there is no list to combine
                    firstPS = false;
                    partialGroundings = getMappingOfFirstPS(ps, m.subPlan().variableConstraints().constraints());
                } else {
                    partialGroundings = combine(partialGroundings, ps, m.subPlan().variableConstraints().constraints());
                }
            }

            /* at this point, every element of partialGroundings contains a partial binding that is in line with all
             * sub-tasks. It might not cover all variables of the method nor the abstract task.
             */
            Set<GroundTask> currentGrounding = groundAbstractTask(partialGroundings, m);
            Set<GroundTask> existingGroundings;
            if (groundingsByTask.containsKey(m.abstractTask())) {
                existingGroundings = groundingsByTask.get(m.abstractTask());
            } else {
                existingGroundings = new HashSet<>();
                groundingsByTask.put(m.abstractTask(), existingGroundings);
            }
            int oldCount = existingGroundings.size();
            existingGroundings.addAll(currentGrounding);

            if ((oldCount < existingGroundings.size()) // has something been added?
                    && (needToReGround.containsKey(m.abstractTask()))) {
                numTasks += (existingGroundings.size() - oldCount);
                Set<DecompositionMethod> reGround = needToReGround.get(m.abstractTask());
                for (DecompositionMethod dm : reGround) {
                    if (!todoList.contains(dm))
                        todoList.add(dm);
                }
            }
            Set<GroundedDecompositionMethod> lMs = groundMethod(partialGroundings, m);
            groundMethods.addAll(lMs);
        }
        System.out.println("Grounded " + groundMethods.size() + " methods and " + numTasks + " abstract tasks ("
                + (System.currentTimeMillis() - time) + " ms)");
    }

    private Set<Task> getEpsilonMethods(Domain d) {
        Set<Task> result = new HashSet<>();
        for (int i = 0; i < d.decompositionMethods().size(); i++) {
            DecompositionMethod m = d.decompositionMethods().apply(i);
            if (m.subPlan().planSteps().size() == 2) {
                result.add(m.abstractTask());
            }
        }
        return result;
    }

    private List<List<Tuple2>> combine(List<List<Tuple2>> currentBindings, PlanStep ps, Seq<VariableConstraint> constraints) {
        List<List<Tuple2>> res = new ArrayList<>();
        Set<GroundTask> psGroundings = groundingsByTask.get(ps.schema());

        currentBindingLoop:
        for (List<Tuple2> current : currentBindings) {

            psGroundingLoop:
            for (GroundTask psGrounding : psGroundings) {
                List<Tuple2> newCombination = new ArrayList<>();

                parameterLoop:
                for (int parNo = 0; parNo < psGrounding.arguments().size(); parNo++) {
                    Variable v = ps.arguments().apply(parNo);
                    Constant c = psGrounding.arguments().apply(parNo);
                    for (Tuple2 b : current) {
                        if (b._1().equals(v)) {
                            if (!(b._2().equals(c)))
                                // inconsistent bindings -> stop current grounding of ps
                                continue psGroundingLoop;
                            else {
                                continue parameterLoop; // identical bindings -> this parameter is fine
                            }
                        }
                    }
                    // if this is reached, the current binding is new and not inconsistent
                    newCombination.add(new Tuple2(v, c));
                    newCombination.addAll(propagateEquality(constraints, v, c));
                }
                // if this is reached, no parameter is inconsistent
                newCombination.addAll(current);
                if (constraintsFine(constraints, newCombination))
                    res.add(newCombination);
            }
        }
        return res;
    }

    private List<Tuple2> propagateEquality(Seq<VariableConstraint> constraints, Variable v, Constant c) {
        List<Tuple2> res = new ArrayList<>();
        for (int i = 0; i < constraints.size(); i++) {
            VariableConstraint vc = constraints.apply(i);
            if (vc instanceof Equal) {
                Equal vcEq = (Equal) vc;
                if (((Equal) vc).right() instanceof Variable) {
                    if (vcEq.left().equals(v)) {
                        res.add(new Tuple2(vcEq.right(), c));
                    } else if (vcEq.right().equals(v)) {
                        res.add(new Tuple2(vcEq.left(), c));
                    }
                }
            }
        }
        return res;
    }


    private List<List<Tuple2>> getMappingOfFirstPS(PlanStep ps, Seq<VariableConstraint> constraints) {
        List<List<Tuple2>> partialGroundingPerPS = new ArrayList<>();
        Set<GroundTask> listOfGroundings = groundingsByTask.get(ps.schema());
        for (GroundTask gt : listOfGroundings) {
            List<Tuple2> partialGrounding = new ArrayList<>();
            for (int j = 0; j < gt.arguments().size(); j++) {
                Constant c = gt.arguments().apply(j);
                Variable v = ps.arguments().apply(j);
                partialGrounding.add(new Tuple2(v, c));
                partialGrounding.addAll(propagateEquality(constraints, v, c));
            }
            if (constraintsFine(constraints, partialGrounding))
                partialGroundingPerPS.add(partialGrounding);
        }
        return partialGroundingPerPS;
    }

    private boolean constraintsFine(Seq<VariableConstraint> constraints, List<Tuple2> partialGrounding) {
        for (int i = 0; i < constraints.size(); i++) {
            VariableConstraint vc = constraints.apply(i);
            if (vc instanceof Equal) {
                if (((Equal) vc).right() instanceof Variable) {
                    Constant c1 = getValue(partialGrounding, (Variable) ((Equal) vc).right());
                    Constant c2 = getValue(partialGrounding, (Variable) ((Equal) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (!c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = getValue(partialGrounding, (Variable) ((Equal) vc).left());
                    if (c == null)
                        continue;
                    if (!c.equals(((Equal) vc).right()))
                        return false;
                }
            } else if (vc instanceof NotEqual) {
                if (((Equal) vc).right() instanceof Variable) {
                    Constant c1 = getValue(partialGrounding, (Variable) ((Equal) vc).right());
                    Constant c2 = getValue(partialGrounding, ((Equal) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = getValue(partialGrounding, (Variable) ((Equal) vc).left());
                    if (c == null)
                        continue;
                    if (c.equals(((Equal) vc).right()))
                        return false;
                }
            } else
                System.out.println("Error: found the following type of variable constraint, that is not implemented: " + vc.toString());
        }
        return true;
    }

    private Constant getValue(List<Tuple2> partialGrounding, Variable par) {
        for (Tuple2 t : partialGrounding)
            if (t._1().equals(par))
                return (Constant) t._2();
        return null;
    }

    private void actionsToLookupTable(Set<GroundTask> groundActions) {
        java.util.Iterator<GroundTask> iter = groundActions.iterator();
        while (iter.hasNext()) {
            GroundTask gt = iter.next();
            Set<GroundTask> l;
            if (groundingsByTask.containsKey(gt.task())) {
                l = groundingsByTask.get(gt.task());
            } else {
                l = new HashSet<>();
                groundingsByTask.put(gt.task(), l);
            }
            l.add(gt);
        }
    }

    private LinkedList<DecompositionMethod> getMethodsWithSolelyPrimSubtasks(Domain d) {
        LinkedList<DecompositionMethod> todoList = new LinkedList<>();
        Iterator<DecompositionMethod> iter = d.decompositionMethods().iterator();
        while (iter.hasNext()) {
            DecompositionMethod m = iter.next();
            if (m.subPlan().abstractPlanSteps().size() == 0)
                todoList.add(m);
        }
        return todoList;
    }

    private Set<GroundedDecompositionMethod> groundMethod(List<List<Tuple2>> partialGrounding, DecompositionMethod m) {
        Set<GroundedDecompositionMethod> res = new HashSet<>();
        for (List<Tuple2> varConstMapping : partialGrounding) {
            scala.collection.immutable.Map<Variable, Constant> set = new scala.collection.immutable.HashMap<>();
            List<Variable> unsetVars = getUnsetVars(varConstMapping, m);
            if (unsetVars.size() > 0) {
                System.out.println("Error while grounding");
            }
            for (Tuple2 b : varConstMapping) {
                set = set.$plus(new Tuple2<Variable, Constant>((Variable) b._1(), (Constant) b._2()));
            }
            GroundedDecompositionMethod gm = new GroundedDecompositionMethod(m, set);
            res.add(gm);
        }
        return res;
    }

    private List<Variable> getUnsetVars(List<Tuple2> varConstMapping, DecompositionMethod m) {
        List<Variable> notFound = new ArrayList<>();
        Iterator<Variable> iter = m.subPlan().variableConstraints().variables().iterator();
        while (iter.hasNext()) {
            Variable v = iter.next();
            boolean found = false;
            for (Tuple2 mapping : varConstMapping) {
                if (mapping._1().equals(v)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFound.add(v);
            }
        }
        return notFound;
    }

    private Set<GroundTask> groundAbstractTask(List<List<Tuple2>> partialGrounding, DecompositionMethod m) {
        Set<GroundTask> res = new HashSet<>();
        Task t = m.abstractTask();
        for (List<Tuple2> varConstMapping : partialGrounding) {
            seqProviderList<Constant> paramList = new seqProviderList<>();
            List<Integer> notIncluded = new ArrayList<>();
            for (int i = 0; i < t.parameters().size(); i++) {
                Variable taskPar = t.parameters().apply(i);
                Constant c = getConst(taskPar, varConstMapping);
                if (c != null) {
                    paramList.add(c);
                } else {
                    notIncluded.add(i);
                }
            }
            if (notIncluded.size() == 0) {
                res.add(new GroundTask(t, paramList.result()));
            } else {
                System.out.println("Error while grounding");
            }
        }
        return res;
    }

    private Constant getConst(Variable methPar, List<Tuple2> grounding) {
        for (Tuple2 g : grounding) {
            if (g._1().equals(methPar))
                return (Constant) g._2();
        }
        return null;
    }

    private void getReachableTaskSymbols(Domain d, Plan p) {
        // initial task network
        for (int j = 0; j < p.planSteps().size(); j++) {
            PlanStep subtask = p.planSteps().apply(j);
            reachableTasks.add(subtask.schema());
        }

        // reachability
        int oldSize = 0;
        while (oldSize < reachableTasks.size()) {
            oldSize = reachableTasks.size();
            for (int i = 0; i < d.decompositionMethods().size(); i++) {
                DecompositionMethod m = d.decompositionMethods().apply(i);
                if (reachableTasks.contains(m.abstractTask())) {
                    for (int j = 0; j < m.subPlan().planSteps().size(); j++) {
                        PlanStep subtask = m.subPlan().planSteps().apply(j);
                        if (subtask.id() < 0)
                            continue;
                        reachableTasks.add(subtask.schema());
                        Set<DecompositionMethod> l;
                        if (needToReGround.containsKey(subtask.schema())) {
                            l = needToReGround.get(subtask.schema());
                        } else {
                            l = new HashSet<>();
                            needToReGround.put(subtask.schema(), l);
                        }
                        l.add(m);
                    }
                }
            }
        }
    }
}
