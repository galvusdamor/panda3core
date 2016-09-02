package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.IRPG;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.NotEqual;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.seqProviderList;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by dhoeller on 21.08.16.
 */
public class hierarchyAwareRPG implements IRPG {
    private Set<GroundTask> reachableTasks = new HashSet<>();
    private Map<Predicate, Set<GroundLiteral>> reachableFacts = new HashMap<>();
    private final Predicate emptyPrec = new Predicate("emptyPrec", (new seqProviderList<Sort>().result()));

    @Override
    public void build(Domain domain, Plan plan) {
        this.build(domain, plan, null);
    }

    @Override
    public void build(Domain domain, Plan plan, Map<Task, Set<GroundTask>> onlyUse) {
        LitTaskCollection<Task> reachableTaskParamLists = new LitTaskCollection<>();

        // stores which operators need to be re-computated when a new fact is introduced
        Map<Predicate, List<Tuple2<List<PlanStep>, Seq<VariableConstraint>>>> precToOperator = new HashMap<>();

        // find methods containing primitive tasks
        getPrimMethods(domain, precToOperator);

        // init is added to the list of new facts
        Set<GroundLiteral> factsAddedLastRound = getS0(plan);
        addSetToMap(reachableFacts, factsAddedLastRound);

        boolean firstRun = true;
        boolean fullyCombineMetodPars = false;

        double time = System.currentTimeMillis();
        System.out.print("hRPG - actions: 0 -> ");
        while (factsAddedLastRound.size() > 0) {

            // get newly applicable operators
            Set<Tuple2<List<PlanStep>, Seq<VariableConstraint>>> operators = new HashSet<>();
            if (firstRun) {  // need to test operators without precondition
                if (precToOperator.containsKey(emptyPrec))
                    operators.addAll(precToOperator.get(emptyPrec));
                firstRun = false;
            }

            // calculate the set of operators that need to be applied
            for (GroundLiteral fact : factsAddedLastRound) {
                if (precToOperator.containsKey(fact.predicate())) {
                    operators.addAll(precToOperator.get(fact.predicate()));
                }
            }

            Set<GroundLiteral> factsAddedThisRound = new HashSet<>();
            for (Tuple2<List<PlanStep>, Seq<VariableConstraint>> operator : operators) {

                List<PlanStep> totalOrder = operator._1();
                List<Set<HashMap<Variable, Constant>>> grPerStep = new ArrayList<>();
                Set<HashMap<Variable, Constant>> groundings = new HashSet<>();
                groundings.add(new HashMap<>());

                operatorLoop:
                for (PlanStep step : totalOrder) {
                    // get possible groundings of preconditions
                    List<List<GroundLiteral>> precGroundings = getPrecLitGroundings(step);
                    assert (theseAreActuallySets(precGroundings));

                    if (fullyCombineMetodPars) {
                        groundings = combinePrecWithExistingGroundings(groundings, step, precGroundings, operator._2());
                    } else {
                        groundings = getSubset(grPerStep, step, operator._2());
                        groundings = combinePrecWithExistingGroundings(groundings, step, precGroundings, operator._2());
                        grPerStep.add(groundings);
                    }


                    // get possible groundings of this single task (that are in line with the partial groundings)
                    //groundings = combinePrecWithExistingGroundings(groundings, step, precGroundings, operator._2());

                    // add all effects to reachable facts
                    stepLoop:
                    for (HashMap<Variable, Constant> grounding : groundings) {
                        boolean contained = reachableTaskParamLists.containsNadd(step.schema(), step.arguments().iterator(), grounding);
                        assert (setEqualsTaskCollection(step, grounding, contained, onlyUse));
                        if (!contained) {
                            GroundTask t = getGroundTask(step, grounding);
                            boolean add = ((onlyUse == null) || ((onlyUse.containsKey(t.task())) && onlyUse.get(t.task()).contains(t)));
                            if (add) {
                                reachableTasks.add(t);
                                factsAddedThisRound.addAll(addEffectLiterals(t));
                            }
                        }
                    }
                    // be aware the difference between the following cases:
                    // - a list of groundings containing an empty grounding (look at initialization) can be combined with any other binding -> is fine
                    // - an empty list of bindings can neverever be refilled -> break
                    if (groundings.isEmpty()) {
                        break operatorLoop;
                    }
                }
            }
            addSetToMap(reachableFacts, factsAddedThisRound);
            factsAddedLastRound = factsAddedThisRound;
            System.out.print(reachableTasks.size() + " -> ");
        }
        System.out.println((System.currentTimeMillis() - time) + " ms");
    }

    private Set<HashMap<Variable, Constant>> getSubset(List<Set<HashMap<Variable, Constant>>> precGr, PlanStep step, Seq<VariableConstraint> variableConstraintSeq) {
        Set<HashMap<Variable, Constant>> groundings = new HashSet<>();
        if (precGr.size() == 0) {
            groundings.add(new HashMap<>());
            return groundings;
        }

        // prepare vars
        Variable[] vars = new Variable[step.arguments().size()];
        Iterator<Variable> iter2 = step.arguments().iterator();
        int j = 0;
        while (iter2.hasNext()) {
            vars[j] = iter2.next();
            j++;
        }

        // current combination
        HashMap<Variable, Constant>[] combination = new HashMap[precGr.size()];

        // prepare iterators
        java.util.Iterator<HashMap<Variable, Constant>>[] iters = new java.util.Iterator[precGr.size()];
        int[] intexOrder = getBestListOrder(precGr);
        for (int i : intexOrder) {
            iters[i] = precGr.get(i).iterator();
            combination[i] = iters[i].next();
        }

        outerLoop:
        while (iters[iters.length - 1].hasNext()) {
            HashMap<Variable, Constant> grounding = new HashMap<>();

            combinationLoop:
            for (Variable var : vars) {
                for (HashMap<Variable, Constant> partBinding : combination) {
                    if (partBinding.containsKey(var)) {
                        Constant c = partBinding.get(var);
                        if (grounding.containsKey(var)) {
                            if (!grounding.get(var).equals(c)) {
                                break combinationLoop;
                            }
                        } else {
                            grounding.put(var, c);
                        }
                    }
                }
            }
            if (checkConstraints(variableConstraintSeq, grounding))
                groundings.add(grounding);

            // count up
            countingLoop:
            for (int i = 0; i < iters.length; i++) {
                if (iters[i].hasNext()) {
                    combination[i] = iters[i].next();
                    break countingLoop;
                } else {
                    if (i == iters.length - 1)
                        break outerLoop;
                    iters[i] = precGr.get(i).iterator();
                    combination[i] = iters[i].next();
                }
            }
        }
        return groundings;
    }

    // todo: this is a dummy implementation, find best order
    private int[] getBestListOrder(List<Set<HashMap<Variable, Constant>>> precGr) {
        int[] indexOrder = new int[precGr.size()];
        for (int i = 0; i < precGr.size(); i++) {
            indexOrder[i] = i;
        }
        return indexOrder;
    }

    private boolean theseAreActuallySets(List<List<GroundLiteral>> precGroundings) {
        for (int i = 0; i < precGroundings.size(); i++) {
            HashSet<GroundLiteral> set = new HashSet<>();
            for (GroundLiteral elem : precGroundings.get(i)) {
                set.add(elem);
            }
            if (precGroundings.get(i).size() != set.size())
                return false;
        }
        return true;
    }

    // this is used in an assertion
    private boolean setEqualsTaskCollection(PlanStep step, HashMap<Variable, Constant> grounding, boolean contained, Map<Task, Set<GroundTask>> onlyUse) {
        GroundTask t = getGroundTask(step, grounding);
        boolean add = ((onlyUse == null) || ((onlyUse.containsKey(t.task())) && onlyUse.get(t.task()).contains(t)));
        return ((contained == reachableTasks.contains(t))
                || (!add));
    }

    private Set<GroundLiteral> addEffectLiterals(GroundTask task) {
        Set<GroundLiteral> newlyReachable = new HashSet<>();

        Seq<GroundLiteral> effs = task.substitutedAddEffects();
        Iterator<GroundLiteral> effectIter = effs.iterator();
        while (effectIter.hasNext()) {
            GroundLiteral eff = effectIter.next();

            if (reachableFacts.containsKey(eff.predicate())) {
                if (!reachableFacts.get(eff.predicate()).contains(eff)) {
                    newlyReachable.add(eff);
                }
            } else {
                newlyReachable.add(eff);
            }
        }
        return newlyReachable;
    }

    private GroundTask getGroundTask(PlanStep ps, HashMap<Variable, Constant> grounding) {
        seqProviderList<Constant> params = new seqProviderList();
        Iterator<Variable> iter = ps.arguments().iterator();
        while (iter.hasNext()) {
            params.add(grounding.get(iter.next()));
        }
        return new GroundTask(ps.schema(), params.result());
    }

    private Set<HashMap<Variable, Constant>> combinePrecWithExistingGroundings(Set<HashMap<Variable, Constant>> inPartialGrs, PlanStep ps, List<List<GroundLiteral>> precLitGroundings, Seq<VariableConstraint> constraints) {
        // if some prec is not fulfilled, return that there is no valid binding
        for (int i = 0; i < precLitGroundings.size(); i++) {
            if (precLitGroundings.get(i).size() == 0)
                return new HashSet<>();
        }

        // generate lookup-table
        HashMap<Variable, HashMap<Constant, Set<Integer>>> lookupTable = new HashMap<>();
        List<HashMap<Variable, Constant>> inPartialGrsList = new ArrayList<>();
        createLookupTable(inPartialGrs, lookupTable, inPartialGrsList);

        // prepare index-set for precondition-permutation
        int[] precIndex = new int[precLitGroundings.size()];
        for (int i = 0; i < precIndex.length - 1; i++) {
            precIndex[i] = 0;
        }
        if (precIndex.length > 0)
            precIndex[precIndex.length - 1] = -1; // will be incremented before testing

        Set<HashMap<Variable, Constant>> outGroundings = new HashSet<>();
        HashMap<Variable, Constant> newParGr;
        if (precLitGroundings.size() == 0) {
            newParGr = new HashMap<>();
        } else {
            newParGr = nextGroundingFromPrecondition(ps, precLitGroundings, precIndex);
        }

        while (newParGr != null) {
            boolean noBindingsYet = true;
            Set<Integer> conformGroundings = null;
            HashMap<Variable, Constant> needToAdd = new HashMap<>();

            lookuploop:
            for (Variable v : newParGr.keySet()) {
                if (lookupTable.containsKey(v)) {
                    Constant c = newParGr.get(v);
                    HashMap<Constant, Set<Integer>> constsForV = lookupTable.get(v);
                    if (constsForV.containsKey(c)) {
                        Set<Integer> instances = constsForV.get(c);
                        if (noBindingsYet) {
                            noBindingsYet = false;
                            conformGroundings = new HashSet<>();
                            conformGroundings.addAll(instances);
                        } else {
                            conformGroundings.retainAll(instances);
                        }
                    } else { // the variable is bound, but only to other consts
                        break lookuploop;
                    }
                } else { // the variable was not bound before, everything is fine
                    needToAdd.put(v, newParGr.get(v));
                }
            }

            if (noBindingsYet) { // not a single variable was included in the variables set so far -> all groundings fine
                HashMap<Variable, Constant> combinedGrounding = new HashMap<>();
                combinedGrounding.putAll(needToAdd);
                groundPlanStep(combinedGrounding, ps, constraints, outGroundings);
            } else if (!noBindingsYet && (conformGroundings.size() > 0)) { // there have been conform mappings
                for (int conformGrIndex : conformGroundings) {
                    HashMap<Variable, Constant> combinedGrounding = new HashMap<>();
                    HashMap<Variable, Constant> oldGrounding = inPartialGrsList.get(conformGrIndex);
                    combinedGrounding.putAll(oldGrounding);
                    combinedGrounding.putAll(needToAdd);
                    groundPlanStep(combinedGrounding, ps, constraints, outGroundings);
                }
            }

            newParGr = nextGroundingFromPrecondition(ps, precLitGroundings, precIndex);
        }
        return outGroundings;
    }

    private void createLookupTable(Set<HashMap<Variable, Constant>> inPartialGrs, HashMap<Variable, HashMap<Constant, Set<Integer>>> lookupTable, List<HashMap<Variable, Constant>> inPartialGrsList) {
        int index = 0;
        assert (sameVarsAreSet(inPartialGrs));
        for (HashMap<Variable, Constant> inGrounding : inPartialGrs) {
            inPartialGrsList.add(inGrounding);
            for (Variable v : inGrounding.keySet()) {
                Constant c = inGrounding.get(v);
                HashMap<Constant, Set<Integer>> varMap;
                if (lookupTable.containsKey(v)) {
                    varMap = lookupTable.get(v);
                } else {
                    varMap = new HashMap<>();
                    lookupTable.put(v, varMap);
                }
                Set<Integer> indices;
                if (varMap.containsKey(c)) {
                    indices = varMap.get(c);
                } else {
                    indices = new HashSet<>();
                    varMap.put(c, indices);
                }
                indices.add(index);
            }
            index++;
        }
    }

    // this is used in an assertion
    private boolean sameVarsAreSet(Set<HashMap<Variable, Constant>> inPartialGrs) {
        Set<Variable> first = null;
        for (HashMap<Variable, Constant> gr : inPartialGrs) {
            if (first == null) {
                first = gr.keySet();
            } else {
                if (!gr.keySet().equals(first))
                    return false;
            }
        }
        return true;
    }

    private void groundPlanStep(HashMap<Variable, Constant> combinedGrounding, PlanStep ps, Seq<VariableConstraint> constraints, Set<HashMap<Variable, Constant>> outGrs) {
        boolean groundingIsPartial = false;
        Iterator<Variable> argIter = ps.arguments().iterator();
        while (argIter.hasNext()) {
            if (!combinedGrounding.containsKey(argIter.next())) {
                groundingIsPartial = true;
                break;
            }
        }

        if (groundingIsPartial) { // not fully grounded
            generateFullGrounding(combinedGrounding, ps, constraints, outGrs);
        } else { // it is a full grounding
            if (checkConstraints(constraints, combinedGrounding))
                outGrs.add(combinedGrounding);
        }
    }

    private void generateFullGrounding(HashMap<Variable, Constant> inPartGrounding, PlanStep ps, Seq<VariableConstraint> constraints, Set<HashMap<Variable, Constant>> outGroundings) {
        List<Variable> vars = new ArrayList<>();
        List<Iterator<Constant>> missingVals = new ArrayList<>();

        Iterator<Variable> iterArg = ps.arguments().iterator();
        while (iterArg.hasNext()) { // todo: this might be more efficient when it is reordered
            Variable arg = iterArg.next();
            if (!inPartGrounding.containsKey(arg)) {
                vars.add(arg);
            }
        }
        Constant[] combination = new Constant[vars.size()];
        for (int i = 0; i < vars.size(); i++) {
            missingVals.add(vars.get(i).sort().elements().iterator());
            if (missingVals.get(i).hasNext()) {
                combination[i] = missingVals.get(i).next();
            } else { // nothing to add to output groundings
                return;
            }
        }

        groundingLoop:
        while (true) {
            HashMap<Variable, Constant> newGrouding = new HashMap<>();
            newGrouding.putAll(inPartGrounding);
            for (int i = 0; i < vars.size(); i++) {
                newGrouding.put(vars.get(i), combination[i]);
            }
            if (checkConstraints(constraints, newGrouding)) {
                outGroundings.add(newGrouding);
            }

            // count up
            countingLoop:
            for (int i = 0; i < missingVals.size(); i++) {
                if (missingVals.get(i).hasNext()) {
                    combination[i] = missingVals.get(i).next();
                    break countingLoop;
                } else {
                    if (i == missingVals.size() - 1)
                        break groundingLoop;
                    missingVals.remove(i);
                    missingVals.add(i, vars.get(i).sort().elements().iterator());
                    combination[i] = missingVals.get(i).next();
                }
            }
        }
    }

    public boolean checkConstraints(Seq<VariableConstraint> constraints, HashMap<Variable, Constant> partialGrounding) {
        for (Variable v : partialGrounding.keySet()) {
            if (!v.sort().elementSet().contains(partialGrounding.get(v)))
                return false;
        }

        Iterator<VariableConstraint> iter = constraints.iterator();
        while (iter.hasNext()) {
            VariableConstraint vc = iter.next();
            if (vc instanceof Equal) {
                if (((Equal) vc).right() instanceof Variable) {
                    Constant c1 = partialGrounding.get(((Equal) vc).right());
                    Constant c2 = partialGrounding.get(((Equal) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (!c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = partialGrounding.get(((Equal) vc).left());
                    if (c == null)
                        continue;
                    if (!c.equals(((Equal) vc).right()))
                        return false;
                }
            } else if (vc instanceof NotEqual) {
                if (((NotEqual) vc).right() instanceof Variable) {
                    Constant c1 = partialGrounding.get(((NotEqual) vc).right());
                    Constant c2 = partialGrounding.get(((NotEqual) vc).left());
                    if ((c1 == null) || (c2 == null))
                        continue;
                    if (c1.equals(c2)) {
                        return false;
                    }
                } else if (((Equal) vc).right() instanceof Constant) {
                    Constant c = partialGrounding.get(((Equal) vc).left());
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

    // todo this code is (nearly) equivalent to the code used in the bottom-up-grounder -> merge
    private void propagateEquality(HashMap<Variable, Constant> binding, Seq<VariableConstraint> constraints, Variable v, Constant c) {
        for (int i = 0; i < constraints.size(); i++) {
            VariableConstraint vc = constraints.apply(i);
            if (vc instanceof Equal) {
                Equal vcEq = (Equal) vc;
                if (((Equal) vc).right() instanceof Variable) {
                    if (vcEq.left().equals(v)) {
                        assert (!binding.containsKey(vcEq.right()));
                        binding.put((Variable) vcEq.right(), c);
                    } else if (vcEq.right().equals(v)) {
                        assert (!binding.containsKey(vcEq.left()));
                        binding.put(vcEq.left(), c);
                    }
                }
            }
        }
    }

    private HashMap<Variable, Constant> nextGroundingFromPrecondition(PlanStep ps, List<List<GroundLiteral>> precGroundings, int[] currentPrecI) {
        if (precGroundings.size() == 0) {
            return null;
        }
        HashMap<Variable, Constant> grounding = new HashMap<>();
        int i = precGroundings.size() - 1; // the position that be be incremented next

        getNextCombinationLoop:
        while (true) {
            // increment the counter
            for (int j = i + 1; j < precGroundings.size(); j++) {
                currentPrecI[j] = 0;
            }
            incLoop:
            while (i >= 0) {
                currentPrecI[i]++;
                if (currentPrecI[i] < precGroundings.get(i).size()) {
                    break incLoop;
                } else {
                    currentPrecI[i] = 0;
                    i--;
                }
            }
            if (i == -1) {
                return null;
            }

            // create grounding over preconditions
            grounding.clear();

            for (i = 0; i < precGroundings.size(); i++) { // todo this index could be reordered!
                Iterator<Constant> constIter = precGroundings.get(i).get(currentPrecI[i]).parameter().iterator();
                Iterator<Variable> varIter = ps.substitutedPreconditions().apply(i).parameterVariables().iterator();

                while (varIter.hasNext()) {
                    Variable v = varIter.next();
                    Constant c = constIter.next();
                    if (!v.sort().elementSet().contains(c)) {
                        continue getNextCombinationLoop;
                    }

                    if (!grounding.containsKey(v)) { // newly bound variable
                        grounding.put(v, c);
                    } else if (!grounding.get(v).equals(c)) { // it is already in and bound to another constant
                        continue getNextCombinationLoop; // this tuple of precondition-groundings can not be combined
                    }

                }
            }
            return grounding;
        }
    }

    private List<List<GroundLiteral>> getPrecLitGroundings(PlanStep ps) {
        List<List<GroundLiteral>> res = new ArrayList<>();
        Iterator<Literal> precIter = ps.substitutedPreconditions().iterator();
        while (precIter.hasNext()) {
            Literal prec = precIter.next();
            if (!reachableFacts.containsKey(prec.predicate())) {
                res.add(new ArrayList<>());
                return res; // there will be no possible combinations, anyway
            } else {
                // test types of included constants
                Iterator<Variable> varIter = prec.parameterVariables().iterator();
                List<scala.collection.immutable.Set<Constant>> paramSorts = new ArrayList<>();
                while (varIter.hasNext()) {
                    paramSorts.add(varIter.next().sort().elementSet());
                }
                java.util.Iterator<GroundLiteral> reachableFacts = this.reachableFacts.get(prec.predicate()).iterator();
                List<GroundLiteral> precFacts = new ArrayList<>();
                while (reachableFacts.hasNext()) {
                    GroundLiteral fact = reachableFacts.next();
                    boolean fine = true;

                    int parInd = 0;
                    Iterator<Constant> constIter = fact.parameter().iterator();
                    paramLoop:
                    while (constIter.hasNext()) {
                        if (!paramSorts.get(parInd).contains(constIter.next())) {
                            fine = false;
                            break paramLoop;
                        }
                        parInd++;
                    }

                    if (fine) {
                        precFacts.add(fact);
                    }
                }
                res.add(precFacts);
            }
        }
        return res;
    }


    private void addSetToMap(Map<Predicate, Set<GroundLiteral>> oldFacts, Set<GroundLiteral> newFacts) {
        java.util.Iterator<GroundLiteral> iter = newFacts.iterator();
        while (iter.hasNext()) {
            GroundLiteral e = iter.next();
            Set<GroundLiteral> groundings;
            if (oldFacts.containsKey(e.predicate())) {
                groundings = oldFacts.get(e.predicate());
            } else {
                groundings = new HashSet<>();
                oldFacts.put(e.predicate(), groundings);
            }
            groundings.add(e);
        }
    }

    private Set<GroundLiteral> getS0(Plan p) {
        Set<GroundLiteral> newlyReachable = new HashSet<>();
        for (int i = 0; i < p.groundedInitialState().size(); i++) {
            GroundLiteral lit = p.groundedInitialState().apply(i);
            if (lit.isPositive())
                newlyReachable.add(lit);
        }
        return newlyReachable;
    }

    private void getPrimMethods(Domain d, Map<Predicate, List<Tuple2<List<PlanStep>, Seq<VariableConstraint>>>> precToOperator) {
        Iterator<DecompositionMethod> iter = d.decompositionMethods().iterator();
        while (iter.hasNext()) {
            DecompositionMethod m = iter.next();
            List<PlanStep> primPSs = containedPrimSteps(m);
            if ((symbolReachable(m.abstractTask())) && (primPSs.size() > 0)) {
                List<PlanStep> ordering = getTotalOrdering(m, primPSs);
                if (ordering != null) {
                    assert (printOperator(m, ordering));
                    createPrecToOperatorMapping(precToOperator, ordering, m.subPlan().variableConstraints().constraints());
                } else {
                    for (PlanStep ps : primPSs) {
                        List<PlanStep> singleStepList = new ArrayList<>();
                        singleStepList.add(ps);
                        assert (printOperator(m, singleStepList));
                        createPrecToOperatorMapping(precToOperator, singleStepList, m.subPlan().variableConstraints().constraints());
                    }
                }
            }
        }
    }

    private boolean printOperator(DecompositionMethod m, List<PlanStep> ordering) {
        System.out.print(m.name() + " -> ");
        for (PlanStep ps : ordering) {
            System.out.print(ps.schema().name());
            System.out.print(" ");
        }
        System.out.println();
        return true;
    }

    private void createPrecToOperatorMapping(Map<Predicate, List<Tuple2<List<PlanStep>, Seq<VariableConstraint>>>> precToOperator, List<PlanStep> orderedPrimSteps, Seq<VariableConstraint> constraints) {
        for (int i = 0; i < orderedPrimSteps.size(); i++) {
            PlanStep ps = orderedPrimSteps.get(i);
            if (!(ps.schema() instanceof ReducedTask)) {
                System.out.println("ERROR: Task schema " + ps.schema().longInfo() + " is not a ReducedTask, please use a different grounding routine.");
            }
            ReducedTask reducedSchema = (ReducedTask) ps.schema();

            if ((i == 0) && (reducedSchema.precondition().conjuncts().size() == 0)) {
                List<Tuple2<List<PlanStep>, Seq<VariableConstraint>>> mList;
                if (precToOperator.containsKey(emptyPrec)) {
                    mList = precToOperator.get(emptyPrec);
                } else {
                    mList = new ArrayList<>();
                    precToOperator.put(emptyPrec, mList);
                }
                mList.add(new Tuple2(orderedPrimSteps, constraints));
            }

            Iterator<Literal> iter = reducedSchema.precondition().conjuncts().iterator();
            while (iter.hasNext()) {
                Literal lit = iter.next();
                List<Tuple2<List<PlanStep>, Seq<VariableConstraint>>> mList;
                if (precToOperator.containsKey(lit.predicate())) {
                    mList = precToOperator.get(lit.predicate());
                } else {
                    mList = new ArrayList<>();
                    precToOperator.put(lit.predicate(), mList);
                }
                mList.add(new Tuple2(orderedPrimSteps, constraints));
            }
        }
    }

    private List<PlanStep> getTotalOrdering(DecompositionMethod m, List<PlanStep> primPSs) {
        List<PlanStep> res = new ArrayList<>();
        outerloop:
        while (res.size() < primPSs.size()) {
            selectNextLoop:
            for (PlanStep possibleNextPS : primPSs) {
                if (res.contains(possibleNextPS))
                    continue;

                for (PlanStep otherPS : primPSs) {
                    if (res.contains(otherPS))
                        continue;
                    if (possibleNextPS.equals(otherPS))
                        continue;

                    boolean pred = m.subPlan().orderingConstraints().lt(possibleNextPS, otherPS);
                    if (!pred) {
                        continue selectNextLoop;
                    }
                }
                res.add(possibleNextPS);
                continue outerloop;
            }
            System.out.println("Warning: No total ordering for method \"" + m.name() + "\": grounding will be fine, but might be inefficient.");
            return null;
        }
        return res;
    }

    private List<PlanStep> containedPrimSteps(DecompositionMethod m) {
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
    public Set<GroundTask> getApplicableActions() {
        return reachableTasks;
    }

    private Set<GroundLiteral> reachableFactsSet = null;

    @Override
    public Set<GroundLiteral> getReachableFacts() {

        if (reachableFactsSet == null) {
            reachableFactsSet = new HashSet<>();

            for (Set<GroundLiteral> val : reachableFacts.values()) {
                reachableFactsSet.addAll(val);
            }
        }
        return reachableFactsSet;

    }

    @Override
    public int numOfReachableFacts() {
        return getReachableFacts().size();
    }
}