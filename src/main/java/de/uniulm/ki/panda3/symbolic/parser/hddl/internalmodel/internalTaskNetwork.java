package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.csp.*;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlParser;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan;
import de.uniulm.ki.panda3.symbolic.plan.element.CausalLink;
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering;
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering;
import scala.collection.Seq;
import scala.collection.immutable.Set;
import scala.collection.immutable.VectorBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dhoeller on 25.06.15.
 */
public class internalTaskNetwork {
    VectorBuilder<PlanStep> planStepBuilder;
    Seq<PlanStep> planStepSeq = null;
    TaskOrdering taskOderings;
    CSP csp;

    public internalTaskNetwork() {
        this.planStepBuilder = new VectorBuilder<>();
        Seq<OrderingConstraint> leerCO = new VectorBuilder<OrderingConstraint>().result();
        Seq<PlanStep> leerPS = new VectorBuilder<PlanStep>().result();
        taskOderings = new SymbolicTaskOrdering(leerCO, leerPS);
        Set<Variable> pVariables = new VectorBuilder<Variable>().result().toSet();
        Seq<VariableConstraint> pVC = new VectorBuilder<VariableConstraint>().result();
        csp = new SymbolicCSP(pVariables, pVC);
    }

    public Seq<PlanStep> planSteps() {
        if (planStepSeq == null)
            planStepSeq = planStepBuilder.result();
        return planStepSeq;
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
        this.planStepBuilder.$plus$eq(ps);
        this.taskOderings.addPlanStep(ps);
    }

    public void addOrdering(PlanStep ps1, PlanStep ps2) {
        this.taskOderings.addOrdering(ps1, ps2);
    }

    public void addCspVariables(Seq<Variable> vars) {
        this.csp.addVariables(vars);
    }

    public void addCspConstraint(VariableConstraint vc) {
        this.csp.addConstraint(vc);
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

    public Plan readTaskNetwork(hddlParser.Tasknetwork_defContext tnCtx, Seq<Variable> parameters, Task abstractTask, Seq<Task> tasks) {
        HashMap<String, PlanStep> idMap = new HashMap<>(); // used to define ordering constraints

        // read tasks
        if (tnCtx.subtask_defs() != null) {
            for (int i = 0; i < tnCtx.subtask_defs().subtask_def().size(); i++) {
                hddlParser.Subtask_defContext psCtx = tnCtx.subtask_defs().subtask_def().get(i);

                VectorBuilder<Variable> psVars = new VectorBuilder<>();
                readTaskParamsAndMatchToMethodParams(psCtx.var_or_const(), psVars, parameters);

                String psName = psCtx.task_symbol().NAME().toString();
                Task schema = parserUtil.taskByName(psName, tasks);
                PlanStep ps = new PlanStep(i, schema, psVars.result());
                if (psCtx.subtask_id() != null) {
                    String id = psCtx.subtask_id().NAME().toString();
                    idMap.put(id, ps);
                }
                this.planStepBuilder.$plus$eq(ps);
            }
        }

        // read variable constraints
        if (tnCtx.constraint_defs() != null) {
            for (hddlParser.Constraint_defContext constraint : tnCtx.constraint_defs().constraint_def()) {
                VectorBuilder<Variable> vars = new VectorBuilder<>();
                readTaskParamsAndMatchToMethodParams(constraint.var_or_const(), vars, parameters);
                Seq<Variable> v = vars.result();
                VariableConstraint vc;
                //System.out.println(constraint.getRuleIndex());
                if (constraint.children.get(1).toString().equals("not")) { // this is an unequal constraint
                    vc = new NotEqual(v.apply(0), v.apply(1));
                } else {// this is an equal constraint
                    vc = new Equal(v.apply(0), v.apply(1));
                }
                this.csp.addConstraint(vc);
            }
        }

        PlanStep psInit = new PlanStep(-1, abstractTask, abstractTask.parameters());
        PlanStep psGoal = new PlanStep(-2, abstractTask, abstractTask.parameters());
        this.planStepBuilder.$plus$eq(psInit);
        this.planStepBuilder.$plus$eq(psGoal);

        for (hddlParser.Ordering_defContext o : tnCtx.ordering_defs().ordering_def()) {
            PlanStep left = idMap.get(o.subtask_id(0).NAME().toString());
            PlanStep right = idMap.get(o.subtask_id(1).NAME().toString());
            this.taskOderings.addOrdering(left, right);
        }
        Seq<CausalLink> causalLinks = (new VectorBuilder<CausalLink>()).result();
        Plan subPlan = new SymbolicPlan(this.planSteps(), causalLinks, this.taskOderings, this.csp, psInit, psGoal);
        return subPlan;
    }

    private boolean readTaskParamsAndMatchToMethodParams(List<hddlParser.Var_or_constContext> vcCtx, VectorBuilder<Variable> psVars, Seq<Variable> parameters) {
        boolean everythingFine = true;
        for (int j = 0; j < vcCtx.size(); j++) {
            hddlParser.Var_or_constContext v = vcCtx.get(j);
            if (v.NAME() != null) {
                System.out.println("ERROR: not yet implemented: a const is used in method's subtask definition");
                everythingFine = false;
            }
            String psVarName = v.VAR_NAME().toString();
            Variable psVar = parserUtil.getVarByName(parameters, psVarName);
            if (psVar == null) {
                System.out.println("ERROR: the variable " + psVarName + " was found in method body definition, but is not included in the method'S parameter list");
                everythingFine = false;
            }
            psVars.$plus$eq(psVar);
        }
        return everythingFine;
    }
}
