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
 * <p/>
 * Not implemented yet:
 * - Variable constraints other than equal and unequal
 * - Epsilon-methods
 */
public class htnBottomUpGrounder {
    HashMap<Task, Set<GroundTask>> groundingsByTask = new HashMap<>();
    HashMap<Task, Set<GroundedDecompositionMethod>> methodsByTask = new HashMap<>();

    public htnBottomUpGrounder(Domain d, Plan p, Set<GroundTask> groundActions) {
        // get reachable task symbols
        long time = System.currentTimeMillis();

        HashMap<Task, Set<DecompositionMethod>> needToReGround = new HashMap<>();
        Set<Task> reachableTasks = getReachableTaskSymbols(d, p, needToReGround);
        System.out.println("Inferred task symbols reachable from initial task network in " + (System.currentTimeMillis() - time) +
                " ms. " + reachableTasks.size() + " task symbols are reachable.");

        // get reachable ground methods and abstract tasks
        time = System.currentTimeMillis();
        LinkedList<DecompositionMethod> todoList = getMethodsWithSolelyPrimSubtasks(d);

        // generate lookup table
        addActionsToTaskSet(groundActions);

        mainloop:
        while (!todoList.isEmpty()) {
            DecompositionMethod m = todoList.removeFirst();

            // only if subtask is primitive or already generated
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
            Set<GroundTask> existingTaskGroundings;
            if (groundingsByTask.containsKey(m.abstractTask())) {
                existingTaskGroundings = groundingsByTask.get(m.abstractTask());
            } else {
                existingTaskGroundings = new HashSet<>();
                groundingsByTask.put(m.abstractTask(), existingTaskGroundings);
            }
            int oldCount = existingTaskGroundings.size();
            existingTaskGroundings.addAll(groundTask(partialGroundings, m.abstractTask(),
                    m.subPlan().variableConstraints().constraints()));

            if ((oldCount < existingTaskGroundings.size()) // has something been added?
                    && (needToReGround.containsKey(m.abstractTask()))) {
                Set<DecompositionMethod> reGround = needToReGround.get(m.abstractTask());
                for (DecompositionMethod dm : reGround) {
                    if (!todoList.contains(dm))
                        todoList.add(dm);
                }
            }

            Set<GroundedDecompositionMethod> existingMethodGroundings;
            if (methodsByTask.containsKey(m.abstractTask())) {
                existingMethodGroundings = methodsByTask.get(m.abstractTask());
            } else {
                existingMethodGroundings = new HashSet<>();
                methodsByTask.put(m.abstractTask(), existingMethodGroundings);
            }
            existingMethodGroundings.addAll(groundMethod(partialGroundings, m));
        }

        int taskCount = 0;
        for (Set<GroundTask> tasks : groundingsByTask.values()) {
            taskCount += tasks.size();
        }
        int methCount = 0;
        for (Set<GroundedDecompositionMethod> tasks : methodsByTask.values()) {
            methCount += tasks.size();
        }

        System.out.println("Grounded bottom-up in " + (System.currentTimeMillis() - time) + " ms. " + methCount
                + " methods and " + taskCount + " tasks are reachable.");

        time = System.currentTimeMillis();
        Set<GroundedDecompositionMethod> tdReachableMethods = new HashSet<>();
        Set<GroundTask> tdReachableTasks;

        // try to eliminate further tasks by additional top-down-grounding
        tdReachableTasks = getGroundInitialTN(p);

        // iterate over methods
        boolean changed = true;
        while (changed) {
            Set<GroundTask> newTasks = new HashSet<>();
            for (GroundTask gt : tdReachableTasks) {
                if (gt.task().isPrimitive())
                    continue;
                for (GroundedDecompositionMethod m : methodsByTask.get(gt.task())) {
                    if (m.groundAbstractTask().equals(gt)) {
                        tdReachableMethods.add(m);
                        Iterator<GroundTask> iter2 = m.subPlanGroundedTasksWithoutInitAndGoal().iterator();
                        while (iter2.hasNext()) {
                            GroundTask subtask = iter2.next();
                            newTasks.add(subtask);
                        }
                    }
                }
            }
            int oldSize = tdReachableTasks.size();
            tdReachableTasks.addAll(newTasks);
            changed = (tdReachableTasks.size() > oldSize);
        }

        taskCount = 0;
        for (Set<GroundTask> tasks : groundingsByTask.values()) {
            tasks.retainAll(tdReachableTasks);
            taskCount += tasks.size();
        }
        methCount = 0;
        for (Set<GroundedDecompositionMethod> tasks : methodsByTask.values()) {
            tasks.retainAll(tdReachableMethods);
            methCount += tasks.size();
        }
        System.out.println("Grounded top-down in " + (System.currentTimeMillis() - time) + " ms. " + methCount
                + " methods and " + taskCount + " tasks are reachable.");
    }

    private Set<GroundTask> getGroundInitialTN(Plan p) {
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
                    System.out.println("Error while grounding the initial TN");
                }
                parameter.add(c);
            }
            tdReachableTasks.add(new GroundTask(ps.schema(), parameter.result()));
        }
        return tdReachableTasks;
    }

    private List<List<Tuple2>> combine(List<List<Tuple2>> currentBindings, PlanStep ps, Seq<VariableConstraint> constraints) {
        List<List<Tuple2>> res = new ArrayList<>();
        Set<GroundTask> psGroundings = groundingsByTask.get(ps.schema());

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

    private void addActionsToTaskSet(Set<GroundTask> groundActions) {
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
            List<Variable> unsetVars = getUnsetVars(varConstMapping, m);
            if (unsetVars.size() > 0) { // partially grounded
                // need to ground unconstraint variables
                List<List<Tuple2>> all = new ArrayList<>();
                all.add(varConstMapping);

                for (Variable v : unsetVars) {
                    all = getFullGrounding(all, v, m.subPlan().variableConstraints().constraints());
                }
                for (List<Tuple2> elem : all) {
                    res.add(new GroundedDecompositionMethod(m, toScalaSet(elem)));
                }
            } else { // totally grounded
                res.add(new GroundedDecompositionMethod(m, toScalaSet(varConstMapping)));
            }
        }
        return res;
    }

    // The following method is somehow equal to the 'combine'-method, but somehow not
    private List<List<Tuple2>> getFullGrounding(List<List<Tuple2>> partialGroundings, Variable v, Seq<VariableConstraint> constraints) {
        if (partialGroundings.size() == 0) {
            // need to return a full grounding, e.g. for initial task network. To reach inner loop, this loop needs to be non-empty
            partialGroundings = new ArrayList<>(); // keep changes local
            partialGroundings.add(new ArrayList<Tuple2>());
        }
        List<List<Tuple2>> all = new ArrayList<>();
        for (List<Tuple2> varConstMapping : partialGroundings) {
            for (int i = 0; i < v.sort().elements().size(); i++) {
                Constant c = v.sort().elements().apply(i);
                List<Tuple2> newMapping = new ArrayList<>();
                newMapping.addAll(varConstMapping);
                newMapping.add(new Tuple2(v, c));
                newMapping.addAll(propagateEquality(constraints, v, c));
                if (constraintsFine(constraints, newMapping)) {
                    all.add(newMapping);
                }
            }
        }
        return all;
    }

    private scala.collection.immutable.Map<Variable, Constant> toScalaSet(List<Tuple2> varConstMapping) {
        scala.collection.immutable.Map<Variable, Constant> set = new scala.collection.immutable.HashMap<>();
        for (Tuple2 b : varConstMapping) {
            set = set.$plus(new Tuple2<>((Variable) b._1(), (Constant) b._2()));
        }
        return set;
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

    private Set<GroundTask> groundTask(List<List<Tuple2>> partialGrounding, Task t, Seq<VariableConstraint> constraints) {
        Set<GroundTask> res = new HashSet<>();
        for (List<Tuple2> varConstMapping : partialGrounding) {
            List<Variable> notIncluded = new ArrayList<>();
            for (int i = 0; i < t.parameters().size(); i++) {
                Variable taskPar = t.parameters().apply(i);
                Constant c = getConst(taskPar, varConstMapping);
                if (c == null) {
                    notIncluded.add(taskPar);
                }
            }
            List<List<Tuple2>> all = new ArrayList<>();
            all.add(varConstMapping);

            for (Variable v : notIncluded) {
                all = getFullGrounding(all, v, constraints);
            }

            for (List<Tuple2> elem : all) {
                seqProviderList<Constant> paramList = new seqProviderList<>();
                for (int i = 0; i < t.parameters().size(); i++) {
                    Variable taskPar = t.parameters().apply(i);
                    Constant c = getConst(taskPar, elem);
                    paramList.add(c);
                }
                res.add(new GroundTask(t, paramList.result()));
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

    private Set<Task> getReachableTaskSymbols(Domain d, Plan p, HashMap<Task, Set<DecompositionMethod>> needToReGround) {
        Set<Task> reachableTasks = new HashSet<>();
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
        return reachableTasks;
    }
}
