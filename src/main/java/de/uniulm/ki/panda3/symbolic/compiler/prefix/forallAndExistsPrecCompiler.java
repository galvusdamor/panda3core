// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.compiler.prefix;

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer;
import de.uniulm.ki.panda3.symbolic.csp.CSP;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangeVariable;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints.LTLTrue$;
import de.uniulm.ki.panda3.util.JavaToScala;
import de.uniulm.ki.panda3.util.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.None$;
import scala.Tuple2;
import scala.Tuple3;
import scala.Unit;
import scala.collection.Seq;
import scala.collection.immutable.Set;

/**
 * Created by dhoeller on 26.03.16.
 */
public class forallAndExistsPrecCompiler implements DomainTransformer<Unit> {
    @Override
    public Tuple2<Domain, Plan> transform(Domain dIn, Plan pIn, Unit info) {

        //Seq<Task> updatedTasks = updateTasks(dIn.sorts(), dIn.tasks());
        Seq<DecompositionMethod> updatedMethods = updateMethods(dIn.sorts(), dIn.decompositionMethods());

        Domain d = new Domain(dIn.sorts(), dIn.predicates(), dIn.tasks(), updatedMethods, dIn.decompositionAxioms(), None$.empty(),None$.empty());
        Plan p = new Plan(pIn.planSteps(), pIn.causalLinks(), pIn.orderingConstraints(), pIn.variableConstraints(), pIn.init(), pIn.goal(), pIn.isModificationAllowed(),pIn
                .isFlawAllowed(),pIn.planStepDecomposedByMethod(),pIn.planStepParentInDecompositionTree(),false, LTLTrue$.MODULE$);

        return new Tuple2<>(d, p);
    }

    private Seq<DecompositionMethod> updateMethods(Seq<Sort> sorts, Seq<DecompositionMethod> methods) {
        seqProviderList<DecompositionMethod> updatedMethods = new seqProviderList<>();

        for (int i = 0; i < methods.size(); i++) {
            DecompositionMethod m = methods.apply(i);
            if (m instanceof SimpleDecompositionMethod) {
                updatedMethods.add(m);
            } else { // SHOP-method with preconditions
                SHOPDecompositionMethod sdm = (SHOPDecompositionMethod) m;
                Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updated = updatePrec(sdm.subPlan().variableConstraints().variables(), sdm.subPlan().variableConstraints().constraints(), sdm.methodPrecondition(), sorts);
                CSP newCSP = new CSP(updated._1(), updated._2());
                Plan newSubPlan = new Plan(
                        sdm.subPlan().planStepsAndRemovedPlanSteps(),
                        sdm.subPlan().causalLinks(),
                        sdm.subPlan().orderingConstraints(),
                        newCSP,
                        sdm.subPlan().init(),
                        sdm.subPlan().goal(),
                        sdm.subPlan().isModificationAllowed(),
                        sdm.subPlan().isFlawAllowed(),
                        sdm.subPlan().planStepDecomposedByMethod(),
                        sdm.subPlan().planStepParentInDecompositionTree(),false, LTLTrue$.MODULE$);


                // TODO reat effects
                updatedMethods.add(new SHOPDecompositionMethod(sdm.abstractTask(), newSubPlan, updated._3(), sdm.methodEffect(), sdm.name()));
            }
        }
        return updatedMethods.result();
    }

    private Seq<Task> updateTasks(Seq<Sort> sorts, Seq<Task> tasks) {
        seqProviderList<Task> updatedTasks = new seqProviderList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.apply(i);
            if (t instanceof ReducedTask) {
                updatedTasks.add(t);
            } else {
                Set<Variable> inVars = t.parameters().toSet();
                Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updatedPrec = updatePrec(inVars, t.parameterConstraints(), t.precondition(), sorts);
                GeneralTask updatedT = new GeneralTask(t.name(), t.isPrimitive(), updatedPrec._1().toSeq(), JavaToScala.<Variable>nil(), updatedPrec._2(), updatedPrec._3(), t.effect());
                updatedTasks.add(updatedT);
            }
        }
        return updatedTasks.result();
    }

    private Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updatePrec(Set<Variable> vars, Seq<VariableConstraint> constr, Formula prec, Seq<Sort> sorts) {
        if (prec instanceof Forall) {
            int id = vars.size() + 1;
            Forall forall = (Forall) prec;

            seqProviderList<Variable> newVars = new seqProviderList<>(vars);
            seqProviderList<VariableConstraint> newConstraints = new seqProviderList<>(constr);

            seqProviderList<Formula> conj = new seqProviderList<>();
            Seq<Constant> allConstsOfType = forall.v().sort().elements();

            for (int i = 0; i < allConstsOfType.size(); i++) {
                Constant c = allConstsOfType.apply(i);
                Variable v = new Variable(id++, "varForConst" + c.name(), forall.v().sort());
                VariableConstraint vc = new Equal(v, c);
                newVars.add(v);
                newConstraints.add(vc);

                conj.add(forall.formula().update(new ExchangeVariable(forall.v(),v)));
                /*if (forall.formula() instanceof Literal) {
                    Literal inner = (Literal) forall.formula();
                    seqProviderList<Variable> innerParamList = new seqProviderList<>();
                    for (int j = 0; j < inner.parameterVariables().size(); j++) {
                        Variable litParam = inner.parameterVariables().apply(j);
                        if (litParam.equals(forall.v())) {
                            litParam = v;
                        }
                        innerParamList.add(litParam);
                    }
                    conj.add(new Literal(inner.predicate(), inner.isPositive(), innerParamList.result()));
                } else {
                    System.out.println("ERROR: Forall contains a formula that is no literal -> this is not implemented yet");
                    // todo: @Gregor: would like to replace a variable by another one in an arbitrary formula
                }*/
            }

            Formula conjunction = new And<Formula>(conj.result()); // Scala :-)
            return new Tuple3<>(newVars.resultSet(), newConstraints.result(), conjunction);
        } else if (prec instanceof Exists) {
            Exists exists = (Exists) prec;
            scala.collection.immutable.Set<Variable> vars2 = (Set<Variable>) vars.$plus(exists.v()); // what the hell??
            return new Tuple3<>(vars2, constr, exists.formula());
        } else if (prec instanceof And) {
            And pAnd = (And) prec;
            seqProviderList<Formula> updatedFormulas = new seqProviderList<>();
            Set<Variable> newVars = vars;
            Seq<VariableConstraint> newConstr = constr;
            for (int i = 0; i < pAnd.conjuncts().size(); i++) {
                Formula f = (Formula) pAnd.conjuncts().apply(i);
                Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updatedSubFormula = updatePrec(newVars, newConstr, f, sorts);
                newVars = updatedSubFormula._1();
                newConstr = updatedSubFormula._2();
                updatedFormulas.add(updatedSubFormula._3());
            }
            Formula newAnd = new And(updatedFormulas.result());
            return new Tuple3<>(newVars, newConstr, newAnd);
        } else if (prec instanceof Or) {
            Or pOr = (Or) prec;
            seqProviderList<Formula> updatedFormulas = new seqProviderList<>();
            Set<Variable> newVars = vars;
            Seq<VariableConstraint> newConstr = constr;
            for (int i = 0; i < pOr.disjuncts().size(); i++) {
                Formula f = (Formula) pOr.disjuncts().apply(i);
                Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updatedSubFormula = updatePrec(newVars, newConstr, f, sorts);
                newVars = updatedSubFormula._1();
                newConstr = updatedSubFormula._2();
                updatedFormulas.add(updatedSubFormula._3());
            }
            Formula newOr = new Or(updatedFormulas.result());
            return new Tuple3<>(newVars, newConstr, newOr);
        } else if (prec instanceof Not) {
            Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula> updatedSubFormula = updatePrec(vars, constr, ((Not) prec).formula(), sorts);
            return new Tuple3<Set<Variable>, Seq<VariableConstraint>, Formula>(updatedSubFormula._1(), updatedSubFormula._2(), new Not(updatedSubFormula._3()));
        } else if (prec instanceof Literal) {
            return new Tuple3<>(vars, constr, prec);
        } else {
            System.out.println("WARNING: Forall&Exists-Precondition-Compiler has found non-implemented structure in precondition. This part of the precondition will be left unchanged.");
            return new Tuple3<>(vars, constr, prec);
        }
    }

    @Override
    public Tuple2<Domain, Plan> transform(Tuple2<Domain, Plan> domainAndPlan, Unit info) {
        return transform(domainAndPlan._1(), domainAndPlan._2(), info);
    }

    @Override
    public Tuple2<Domain, Plan> apply(Domain domain, Plan plan, Unit info) {
        return null;
    }

    @Override
    public Tuple2<Domain, Plan> apply(Tuple2<Domain, Plan> domainAndPlan, Unit info) {
        return null;
    }
}
