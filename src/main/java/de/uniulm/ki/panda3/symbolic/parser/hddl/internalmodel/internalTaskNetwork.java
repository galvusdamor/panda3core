package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.csp.*;
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.*;
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
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractFunction1;

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
    int nextId = 0;

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
        this.taskOderings = this.taskOderings.addPlanStep(ps);
    }

    public void addOrdering(PlanStep ps1, PlanStep ps2) {
        this.taskOderings = this.taskOderings.addOrdering(ps1, ps2);
    }

    public void addCspVariables(Seq<Variable> vars) {
        this.csp = this.csp.addVariables(vars);
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
        HashMap<String, PlanStep> idMap = new HashMap<>(); // used to define ordering constraints

        // read tasks
        if (tnCtx.subtask_defs() != null) {
            for (int i = 0; i < tnCtx.subtask_defs().subtask_def().size(); i++) {
                hddlParser.Subtask_defContext psCtx = tnCtx.subtask_defs().subtask_def().get(i);

                VectorBuilder<Variable> psVars = new VectorBuilder<>();
                readTaskParamsAndMatchToMethodParams(psCtx.var_or_const(), psVars, parameters, sorts);

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
        ReducedTask initSchema = new ReducedTask("init", true, abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(new Vector<Literal>(0, 0, 0)));
        ReducedTask goalSchema = new ReducedTask("goal", true, abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), new And<Literal>(new Vector<Literal>(0, 0, 0)), new
                And<Literal>(new Vector<Literal>(0, 0, 0)));

        PlanStep psInit = new PlanStep(-1, initSchema, abstractTask.parameters());
        PlanStep psGoal = new PlanStep(-2, goalSchema, abstractTask.parameters());
        this.planStepBuilder.$plus$eq(psInit);
        this.planStepBuilder.$plus$eq(psGoal);
        Seq<PlanStep> ps = this.planSteps();

        // read ordering
        String orderingMode = tnCtx.children.get(0).toString();
        if ((orderingMode.equals(":ordered-subtasks")) || (orderingMode.equals(":ordered-tasks"))) {
            final int twoTechnicalSteps = 2;
            for (int i = 1; i < ps.size() - twoTechnicalSteps; i++) {
                this.taskOderings = this.taskOderings.addOrdering(ps.apply(i - 1), ps.apply(i));
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
        Plan subPlan = new SymbolicPlan(ps, causalLinks, this.taskOderings, this.csp, psInit, psGoal);
        return subPlan;
    }

    private boolean readTaskParamsAndMatchToMethodParams(List<hddlParser.Var_or_constContext> vcCtx, VectorBuilder<Variable> psVars, Seq<Variable> parameters, Seq<Sort> sorts) {
        boolean everythingFine = true;
        for (int j = 0; j < vcCtx.size(); j++) {
            hddlParser.Var_or_constContext v = vcCtx.get(j);
            if (v.NAME() != null) { // this is a constant ...
                String constName = v.NAME().toString();
                final Constant c = new Constant(constName);
                Sort s = sorts.find(new AbstractFunction1<Sort, Object>() {
                    @Override
                    public Object apply(Sort v1) {
                        return v1.elements().contains(c) ? Boolean.TRUE : Boolean.FALSE;
                    }
                }).get();
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
