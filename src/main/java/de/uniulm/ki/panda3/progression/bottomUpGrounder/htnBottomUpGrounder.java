package de.uniulm.ki.panda3.progression.bottomUpGrounder;

import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.NotEqual;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Constant;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.JavaToScala;
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
    public final int methodCount;
    public final int abstTaskCount;
    public final int primTaskCount;

    public HashMap<Task, Set<GroundTask>> groundingsByTask = new HashMap<>();
    public HashMap<Task, Set<GroundedDecompositionMethod>> methodsByTask = new HashMap<>();
    public boolean deletedActions = false;

    public htnBottomUpGrounder(Domain d, Plan p, Set<GroundTask> groundActions) {
        // get reachable task symbols
        System.out.println("Grounding HTN ...");
        long time = System.currentTimeMillis();

        HashMap<Task, Set<DecompositionMethod>> needToReGround = new HashMap<>();
        Set<Task> reachableTasks = getReachableTaskSymbols(d, p, needToReGround);
        System.out.println(" - Found " + reachableTasks.size() + " task symbols reachable from the initial task network.");

        // get reachable ground methods and abstract tasks
        LinkedList<DecompositionMethod> todoList = getMethodsWithSolelyPrimSubtasks(d);

        // generate lookup table
        addActionsToTaskSet(groundActions);
        assert (allPrimitive(groundActions));

        mainloop:
        while (!todoList.isEmpty()) {
            DecompositionMethod m = todoList.removeFirst();

            // only if subtask is primitive or already generated
            for (int i = 0; i < m.subPlan().planSteps().size(); i++) {
                PlanStep ps = m.subPlan().planSteps().apply(i);
                if ((ps.id() > 0) && (!groundingsByTask.containsKey(ps.schema())))
                    continue mainloop;
            }

            System.out.print(" - Grounding " + m.name());

            // get partial groundings
            List<List<Tuple2>> partialGroundings = null;
            boolean firstPS = true;
            List<Integer> subTaskOrder = getSubTaskOrder(m.subPlan().planStepsWithoutInitGoal());
            for (int i : subTaskOrder) {
                PlanStep ps = m.subPlan().planStepsWithoutInitGoal().apply(i);
                System.out.print(".");
                if (firstPS) { // there is no list to combine
                    firstPS = false;
                    partialGroundings = getMappingOfFirstPS(ps, m.subPlan().variableConstraints().constraints(), m);
                } else {
                    partialGroundings = combine(partialGroundings, ps, m.subPlan().variableConstraints().constraints(), m);
                }
            }
            System.out.println();

            /* at this point, every element of partialGroundings contains a partial binding that is in line with all
             * sub-tasks. It might not cover all variables of the method nor the abstract task.
             */

            Set<GroundedDecompositionMethod> existingMethodGroundings;
            if (methodsByTask.containsKey(m.abstractTask())) {
                existingMethodGroundings = methodsByTask.get(m.abstractTask());
            } else {
                existingMethodGroundings = new HashSet<>();
                methodsByTask.put(m.abstractTask(), existingMethodGroundings);
            }
            Set<GroundedDecompositionMethod> newMethods = groundMethod(partialGroundings, m);
            existingMethodGroundings.addAll(newMethods);

            // ground tasks -> just collect the abstract tasks from the methods
            Set<GroundTask> existingTaskGroundings;
            if (groundingsByTask.containsKey(m.abstractTask())) {
                existingTaskGroundings = groundingsByTask.get(m.abstractTask());
            } else {
                existingTaskGroundings = new HashSet<>();
                groundingsByTask.put(m.abstractTask(), existingTaskGroundings);
            }
            int oldCount = existingTaskGroundings.size();

            java.util.Iterator<GroundedDecompositionMethod> taskIter = newMethods.iterator();
            while (taskIter.hasNext()) {
                existingTaskGroundings.add(taskIter.next().groundAbstractTask());
            }

            assert (testSubtasks());

            if ((oldCount < existingTaskGroundings.size()) // has something been added?
                    && (needToReGround.containsKey(m.abstractTask()))) {
                Set<DecompositionMethod> reGround = needToReGround.get(m.abstractTask());
                for (DecompositionMethod dm : reGround) {
                    if (!todoList.contains(dm))
                        todoList.add(dm);
                }
            }
        }

        // count groundings
        int taskCount = 0;
        for (Set<GroundTask> tasks : groundingsByTask.values()) {
            taskCount += tasks.size();
        }
        int methCount = 0;
        for (Set<GroundedDecompositionMethod> tasks : methodsByTask.values()) {
            methCount += tasks.size();
        }

        System.out.println(" - Grounded bottom-up in " + (System.currentTimeMillis() - time) + " ms. " + methCount
                + " methods and " + taskCount + " tasks are reachable.");

        assert (testAbstractTasks());
        assert (testSubtasks());

        time = System.currentTimeMillis();
        Set<GroundedDecompositionMethod> tdReachableMethods = new HashSet<>();
        Set<GroundTask> tdReachableTasks;

        // try to eliminate further tasks by additional top-down-grounding
        tdReachableTasks = groundingUtil.getFullyGroundTN(p);

        // iterate over methods
        int oldSize = 0;
        while (oldSize != tdReachableTasks.size()) {
            oldSize = tdReachableTasks.size();
            for (Set<GroundedDecompositionMethod> classes : methodsByTask.values()) {
                for (GroundedDecompositionMethod m : classes) {
                    if (tdReachableTasks.contains(m.groundAbstractTask())) {
                        tdReachableMethods.add(m);
                        Iterator<GroundTask> iter2 = m.subPlanGroundedTasksWithoutInitAndGoal().iterator();
                        while (iter2.hasNext()) {
                            tdReachableTasks.add(iter2.next());
                        }
                    }
                }
            }
        }

        int deletedActions = 0;
        int absTaskCount = 0;
        int primTaskCount = 0;

        for (Task key : groundingsByTask.keySet()) {
            int oldNum = 0;
            Set<GroundTask> tasks = groundingsByTask.get(key);
            if (key.isPrimitive()) {
                oldNum = tasks.size();
            }
            tasks.retainAll(tdReachableTasks);
            if (key.isPrimitive()) {
                deletedActions += (oldNum - tasks.size());
                primTaskCount += tasks.size();
            } else {
                absTaskCount += tasks.size();
            }
        }
        methCount = 0;
        for (Set<GroundedDecompositionMethod> tasks : methodsByTask.values()) {
            tasks.retainAll(tdReachableMethods);
            methCount += tasks.size();
        }
        System.out.println(" - Grounded top-down in " + (System.currentTimeMillis() - time) + " ms. " + methCount
                + " methods, " + absTaskCount + " abstract and " + primTaskCount + " primitive tasks are reachable.");
        if (deletedActions > 0) {
            System.out.println(" - Deleted " + deletedActions + " ground actions.");
            this.deletedActions = true;
        }

        this.methodCount = methCount;
        this.abstTaskCount = absTaskCount;
        this.primTaskCount = primTaskCount;

        assert (testAbstractTasks());
        assert (testSubtasks());
    }

    private boolean allPrimitive(Set<GroundTask> groundActions) {
        for (GroundTask t : groundActions)
            if (!t.task().isPrimitive())
                return false;
        return true;
    }

    private boolean testAbstractTasks() {
        System.out.println("   Testing assertions about grounding");
        boolean allFine = true;
        // test if there is a method for every task
        for (Task t : this.groundingsByTask.keySet()) {
            if (t.isPrimitive())
                continue;

            Set<GroundTask> gts = this.groundingsByTask.get(t);
            java.util.Iterator<GroundTask> iter = gts.iterator();

            taskLoop:
            while (iter.hasNext()) {
                GroundTask gt = iter.next();
                for (GroundedDecompositionMethod m : methodsByTask.get(t)) {
                    if (m.groundAbstractTask().equals(gt)) {
                        continue taskLoop;
                    }
                }
                System.out.println("   Did not find method for task " + gt.longInfo());
                allFine = false;
            }
        }

        return allFine;
    }

    private boolean testMethod(GroundedDecompositionMethod method) {
        for (int i = 0; i < method.subPlanGroundedTasksWithoutInitAndGoal().size(); i++) {
            GroundTask subtask = method.subPlanGroundedTasksWithoutInitAndGoal().apply(i);
            if (!groundingsByTask.get(subtask.task()).contains(subtask)) {
                System.out.println("   Did not find subtask " + subtask.longInfo() + " of method " + method.mediumInfo() + " in list of reachable tasks");
                return false;
            }
        }
        return true;
    }


    private boolean testPartialMethod(DecompositionMethod method, List<Tuple2<Variable, Constant>> binding) {
        Seq<GroundTask> tasks = method.getAllGroundedPlanStepsFromPartialMapping(JavaToScala.toScalaSeq(binding));


        for (int i = 0; i < tasks.size(); i++) {
            GroundTask subtask = tasks.apply(i);
            if (!groundingsByTask.get(subtask.task()).contains(subtask)) {
                System.out.println("   Did not find subtask " + subtask.longInfo() + " of method " + method.name() + " in list of reachable tasks");
                return false;
            }
        }
        return true;
    }


    private boolean testSubtasks() {
        System.out.println("   Test if all subtasks of all methods are reachable");
        boolean allFine = true;
        // test if all subtasks of all methods are reachable
        for (Set<GroundedDecompositionMethod> t : methodsByTask.values()) {
            java.util.Iterator<GroundedDecompositionMethod> iter = t.iterator();
            while (iter.hasNext()) {
                allFine &= testMethod(iter.next());
            }
        }

        return allFine;
    }

    /*
     * Any order is correct, but a "good" order reduces runtime;
     */
    private List<Integer> getSubTaskOrder(Seq<PlanStep> planStepSeq) {
        if (planStepSeq.size() == 0)
            return new ArrayList<>();

        List<Integer> res = new ArrayList<>();
        List<Integer> todo = new ArrayList<>();
        for (int i = 0; i < planStepSeq.size(); i++)
            todo.add(i);

        int[] numParameter = new int[todo.size()];
        int[] numGroundings = new int[todo.size()];

        for (int i = 0; i < todo.size(); i++) {
            numGroundings[i] = groundingsByTask.get(planStepSeq.apply(i).schema()).size();
            numParameter[i] = planStepSeq.apply(i).schema().parameters().size();
        }

        for (int i = 0; i < todo.size(); i++) {
            int index = -1;
            int numP = Integer.MIN_VALUE;
            int numG = Integer.MAX_VALUE;
            for (int j = 0; j < todo.size(); j++) {
                if ((numParameter[j] > numP)
                        || ((numParameter[j] == numP) && (numGroundings[j] < numG))) {
                    index = j;
                    numP = numParameter[j];
                    numG = numGroundings[j];
                }
            }
            res.add(index);
            numParameter[index] = Integer.MIN_VALUE;
        }

        return res;
    }

    private List<List<Tuple2>> combine(List<List<Tuple2>> currentBindings, PlanStep ps, Seq<VariableConstraint> constraints, DecompositionMethod m) {
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
                    if (!v.sort().elements().contains(c)) {
                        continue psGroundingLoop;
                    }
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
                if (constraintsFine(constraints, newCombination)) {
                    assert (testPartialMethod(m, (List<Tuple2<Variable, Constant>>) (Object) newCombination));
                    res.add(newCombination);
                }
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


    private List<List<Tuple2>> getMappingOfFirstPS(PlanStep ps, Seq<VariableConstraint> constraints, DecompositionMethod m) {
        List<List<Tuple2>> partialGroundingPerPS = new ArrayList<>();
        Set<GroundTask> listOfGroundings = groundingsByTask.get(ps.schema());
        continueLoop:
        for (GroundTask gt : listOfGroundings) {
            List<Tuple2> partialGrounding = new ArrayList<>();
            for (int j = 0; j < gt.arguments().size(); j++) {
                Constant c = gt.arguments().apply(j);
                Variable v = ps.arguments().apply(j);
                if (v.sort().elements().contains(c)) {
                    partialGrounding.add(new Tuple2(v, c));
                    partialGrounding.addAll(propagateEquality(constraints, v, c));
                } else continue continueLoop;
            }
            if (constraintsFine(constraints, partialGrounding)) {
                assert (testPartialMethod(m, (List<Tuple2<Variable, Constant>>) (Object) partialGrounding));
                partialGroundingPerPS.add(partialGrounding);
            }
        }
        return partialGroundingPerPS;
    }

    private boolean constraintsFine(Seq<VariableConstraint> constraints, List<Tuple2> partialGrounding) {
        for (int i = 0; i < constraints.size(); i++) {
            VariableConstraint vc = constraints.apply(i);
            if (vc instanceof Equal) {
                if (((Equal) vc).right() instanceof Variable) {
                    Constant c1 = getValue(partialGrounding, (Variable) ((Equal) vc).right());
                    Constant c2 = getValue(partialGrounding, ((Equal) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (!c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = getValue(partialGrounding, ((Equal) vc).left());
                    if (c == null)
                        continue;
                    if (!c.equals(((Equal) vc).right()))
                        return false;
                }
            } else if (vc instanceof NotEqual) {
                if (((NotEqual) vc).right() instanceof Variable) {
                    Constant c1 = getValue(partialGrounding, (Variable) ((NotEqual) vc).right());
                    Constant c2 = getValue(partialGrounding, ((NotEqual) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = getValue(partialGrounding, ((Equal) vc).left());
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
        if (partialGrounding == null) { // methods do not have a parameter list, so this is the only check possible to identify methods without parameters
            res.add(new GroundedDecompositionMethod(m, toScalaSet(new LinkedList<Tuple2>())));
        } else {
            mappingLoop:
            for (List<Tuple2> varConstMapping : partialGrounding) {
                List<Variable> unsetVars = getUnsetVars(varConstMapping, m);
                if (unsetVars.size() > 0) { // partially grounded
                    // need to ground unconstraint variables
                    List<List<Tuple2>> all = new ArrayList<>();
                    all.add(varConstMapping);

                    // test if there is some variable that contains no constant
                    for (Variable v : unsetVars) {
                        if (v.sort().elements().size() == 0) {
                            continue mappingLoop;
                        }
                    }

                    for (Variable v : unsetVars) {
                        all = getFullGrounding(all, v, m.subPlan().variableConstraints().constraints());
                    }
                    for (List<Tuple2> elem : all) {
                        GroundedDecompositionMethod gm = new GroundedDecompositionMethod(m, toScalaSet(elem));
                        assert (testMethod(gm));
                        res.add(gm);
                    }
                } else { // totally grounded
                    GroundedDecompositionMethod gm = new GroundedDecompositionMethod(m, toScalaSet(varConstMapping));
                    assert (testMethod(gm));
                    res.add(gm);
                }
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
            scala.collection.Iterator<Constant> iter = v.sort().elements().iterator();
            while (iter.hasNext()) {
                Constant c = iter.next();
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
