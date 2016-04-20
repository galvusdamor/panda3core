package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.csp.*;
import de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlPanda3Visitor;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlParser;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.CausalLink;
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering;
import de.uniulm.ki.panda3.symbolic.search.NoFlaws$;
import de.uniulm.ki.panda3.symbolic.search.NoModifications$;
import scala.Option;
import scala.Some;
import scala.collection.Seq;
import scala.collection.immutable.Set;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractFunction1;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dhoeller on 25.06.15.
 */
public class internalTaskNetwork {
    seqProviderList<PlanStep> planStepBuilder = new seqProviderList<>();
    Seq<PlanStep> planStepSeq = null;
    TaskOrdering taskOderings;
    CSP csp;
    int nextId = 0;

    public internalTaskNetwork() {
        Seq<OrderingConstraint> leerCO = new VectorBuilder<OrderingConstraint>().result();
        Seq<PlanStep> leerPS = new VectorBuilder<PlanStep>().result();
        taskOderings = new TaskOrdering(leerCO, leerPS);
        Set<Variable> pVariables = new VectorBuilder<Variable>().result().toSet();
        Seq<VariableConstraint> pVC = new VectorBuilder<VariableConstraint>().result();
        csp = new CSP(pVariables, pVC);
    }

    public Seq<PlanStep> planSteps() {
        return planStepBuilder.result();
    }

    public Seq<CausalLink> causalLinks() {
        Seq<CausalLink> leerSeq = new VectorBuilder<CausalLink>().result();
        return leerSeq;
    }

    public TaskOrdering taskOrderings() {
        return taskOderings;
    }

    public CSP csp() {
        return csp;
    }

    public void addPlanStep(PlanStep ps) {
        this.planStepBuilder.add(ps);
        this.taskOderings = this.taskOderings.addPlanStep(ps);
    }

    public void addOrdering(PlanStep ps1, PlanStep ps2) {
        this.taskOderings = this.taskOderings.addOrdering(ps1, ps2);
    }

    public void addCspVariables(Seq<Variable> vars) {
        this.csp = this.csp.addVariables(vars);
    }

    public void addCspVariables(seqProviderList<Variable> vars) {
        for (int i = 0; i < vars.size(); i++) {
            Variable v = vars.get(i);
            addCspVariable(v);
        }
    }

    public void addCspVariable(Variable variable) {
        this.csp = this.csp.addVariable(variable);
    }

    public void addCspConstraint(VariableConstraint vc) {
        this.csp = this.csp.addConstraint(vc);
    }

    public void addCspConstraints(Seq<VariableConstraint> vc) {
        this.csp = this.csp.addConstraints(vc);
    }

    public boolean connectMethVarsAndTaskVars(Seq<Variable> methodParams, Task abstractTask, List<hddlParser.Var_or_constContext> givenParamsCtx) {
        boolean paramsOk = true;
        for (int i = 0; i < givenParamsCtx.size(); i++) {

            hddlParser.Var_or_constContext param = givenParamsCtx.get(i);

            if (param.NAME() != null) { // this is a consts
                System.out.println("ERROR: not yet implemented - a const is used in task definition");
            }

            Variable methodVar = parserUtil.getVarByName(methodParams, param.VAR_NAME().toString());
            if (methodVar == null) {
                System.out.println("ERROR: parameter used in method definition has not been found in method's parameter definition.");
                paramsOk = false;
                break;
            }
            Variable taskVar = abstractTask.parameters().apply(i);
            VariableConstraint vc = new Equal(taskVar, methodVar);
            this.addCspConstraint(vc);
        }
        return paramsOk;
    }

    public Plan readTaskNetwork(hddlParser.Tasknetwork_defContext tnCtx, Seq<Variable> parameters, Task abstractTask, Seq<Task> tasks, Seq<Sort> sorts) {

        ReducedTask initSchema = new ReducedTask("init", true, abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(new Vector<Literal>(0, 0, 0)));
        ReducedTask goalSchema = new ReducedTask("goal", true, abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), new And<Literal>(new Vector<Literal>(0, 0, 0)), new
                And<Literal>(new Vector<Literal>(0, 0, 0)));

        PlanStep psInit = new PlanStep(-1, initSchema, abstractTask.parameters());
        PlanStep psGoal = new PlanStep(-2, goalSchema, abstractTask.parameters());
        this.planStepBuilder.add(psInit);
        this.planStepBuilder.add(psGoal);
        this.taskOderings = this.taskOderings.addPlanStep(psInit).addPlanStep(psGoal);

        HashMap<String, PlanStep> idMap = new HashMap<>(); // used to define ordering constraints

        // read tasks
        if (tnCtx.subtask_defs() != null) {
            for (int i = 0; i < tnCtx.subtask_defs().subtask_def().size(); i++) {
                hddlParser.Subtask_defContext psCtx = tnCtx.subtask_defs().subtask_def().get(i);

                VectorBuilder<Variable> psVars = new VectorBuilder<>();
                readTaskParamsAndMatchToMethodParams(psCtx.var_or_const(), psVars, parameters, sorts);

                String psName = psCtx.task_symbol().NAME().toString();
                Task schema = parserUtil.taskByName(psName, tasks);

                Seq<Variable> psVarsSeq = psVars.result();
                if (schema == null) {
                    System.out.println("Task schema undefined: " + psName);
                    continue;
                }

                if (schema.parameters().size() != psVarsSeq.size()) {
                    System.out.println("The task schema " + schema.name() + " is defined with " + schema.parameters().size() + " but used with " + psVarsSeq.size()+" parameters.");
                    continue;
                }

                PlanStep ps = new PlanStep(i, schema, psVarsSeq);
                this.taskOderings = this.taskOderings.addPlanStep(ps).addOrdering(OrderingConstraint.apply(psInit,ps)).addOrdering(OrderingConstraint.apply(ps,psGoal));
                if (psCtx.subtask_id() != null) {
                    String id = psCtx.subtask_id().NAME().toString();
                    idMap.put(id, ps);
                }
                this.planStepBuilder.add(ps);
            }
        }

        // read variable constraints
        if (tnCtx.constraint_defs() != null) {
            for (hddlParser.Constraint_defContext constraint : tnCtx.constraint_defs().constraint_def()) {
                VectorBuilder<Variable> vars = new VectorBuilder<>();
                readTaskParamsAndMatchToMethodParams(constraint.var_or_const(), vars, parameters, sorts);
                Seq<Variable> v = vars.result();
                VariableConstraint vc;
                //System.out.println(constraint.getRuleIndex());
                if (constraint.children.get(1).toString().equals("not")) { // this is an unequal constraint
                    vc = new NotEqual(v.apply(0), v.apply(1));
                } else {// this is an equal constraint
                    vc = new Equal(v.apply(0), v.apply(1));
                }
                this.csp = this.csp.addConstraint(vc);
            }
        }
        Seq<PlanStep> ps = this.planSteps();

        // read ordering
        String orderingMode = tnCtx.children.get(0).toString();
        if ((orderingMode.equals(":ordered-subtasks")) || (orderingMode.equals(":ordered-tasks"))) {
            for (int i = 2; i < ps.size()-1; i++) {
                this.taskOderings = this.taskOderings.addOrdering(ps.apply(i), ps.apply(i+1));
            }
        } else { // i.e. :tasks or :subtasks
            if ((tnCtx.ordering_defs() != null) && (tnCtx.ordering_defs().ordering_def() != null)) {
                for (hddlParser.Ordering_defContext o : tnCtx.ordering_defs().ordering_def()) {
                    String idLeft = o.subtask_id(0).NAME().toString();
                    String idRight = o.subtask_id(1).NAME().toString();
                    if (!idMap.containsKey(idLeft)) {
                        System.out.println("ERROR: The ID \"" + idLeft + "\" is not a subtask ID, but used in the ordering constraints.");
                    } else if (!idMap.containsKey(idRight)) {
                        System.out.println("ERROR: The ID \"" + idRight + "\" is not a subtask ID, but used in the ordering constraints.");
                    } else {
                        PlanStep left = idMap.get(idLeft);
                        PlanStep right = idMap.get(idRight);
                        this.taskOderings = this.taskOderings.addOrdering(left, right);
                    }
                }
            }
        }
        Seq<CausalLink> causalLinks = (new VectorBuilder<CausalLink>()).result();
        Plan subPlan = new Plan(ps, causalLinks, this.taskOderings, this.csp, psInit, psGoal, NoModifications$.MODULE$, NoFlaws$.MODULE$,hddlPanda3Visitor.planStepsDecomposedBy,
                hddlPanda3Visitor.planStepsDecompositionParents);
        return subPlan;
    }

    private boolean readTaskParamsAndMatchToMethodParams(List<hddlParser.Var_or_constContext> vcCtx, VectorBuilder<Variable> psVars, Seq<Variable> parameters, Seq<Sort> sorts) {
        boolean everythingFine = true;
        for (int j = 0; j < vcCtx.size(); j++) {
            hddlParser.Var_or_constContext v = vcCtx.get(j);
            if (v.NAME() != null) { // this is a constant ...
                String constName = v.NAME().toString();
                final Constant c = new Constant(constName);

                Sort s = null;
                outer:
                for (int i = 0; i < sorts.size(); i++) {
                    Sort s1 = sorts.apply(i);
                    for (int k = 0; k < s1.elements().size(); k++) {
                        if (s1.elements().apply(k).name().equals(c.name())) {
                            s = s1;
                            break outer;
                        }
                    }
                }
                Variable var = new Variable(nextId++, "varFor" + constName, s);
                this.csp = this.csp.addVariable(var);
                this.csp = this.csp.addConstraint(new Equal(var, c));
                psVars.$plus$eq(var);
            } else {
                String psVarName = v.VAR_NAME().toString();
                Variable psVar = parserUtil.getVarByName(parameters, psVarName);
                if (psVar == null) {
                    System.out.println("ERROR: the variable " + psVarName + " was found in method body definition, but is not included in the method's parameter list");
                    everythingFine = false;
                }
                psVars.$plus$eq(psVar);
            }
        }
        return everythingFine;
    }
}
