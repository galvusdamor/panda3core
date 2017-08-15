package de.uniulm.ki.panda3.util.shopReader;

import de.uniulm.ki.panda3.symbolic.csp.CSP;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlPanda3Visitor;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.CausalLink;
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.plan.flaw.CausalThreat;
import de.uniulm.ki.panda3.symbolic.plan.flaw.OpenPrecondition;
import de.uniulm.ki.panda3.symbolic.plan.flaw.UnboundVariable;
import de.uniulm.ki.panda3.symbolic.plan.modification.AddOrdering;
import de.uniulm.ki.panda3.symbolic.plan.modification.BindVariableToValue;
import de.uniulm.ki.panda3.symbolic.plan.modification.InsertCausalLink;
import de.uniulm.ki.panda3.symbolic.plan.modification.MakeLiteralsUnUnifiable;
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering;
import de.uniulm.ki.panda3.symbolic.search.NoFlaws$;
import de.uniulm.ki.panda3.symbolic.search.NoModifications$;
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter;
import de.uniulm.ki.panda3.util.JavaToScala;
import de.uniulm.ki.panda3.util.seqProviderList;
import de.uniulm.ki.panda3.util.shopReader.internalModel.shMethod;
import de.uniulm.ki.panda3.util.shopReader.internalModel.shOperator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import scala.None$;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;

import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 02.08.17.
 */
public class shopDomainReader {
    private final static List<Class<?>> alwaysAllowedModificationsClasses = new LinkedList<>();
    private final static List<Class<?>> alwaysAllowedFlawClasses = new LinkedList<>();
    public static final Seq<CausalLink> emptyCausalLink = new seqProviderList<CausalLink>().result();
    And<Literal> emptyAnd = new And<Literal>(new Vector<Literal>(0, 0, 0));

    static {

        alwaysAllowedModificationsClasses.add(AddOrdering.class);
        alwaysAllowedModificationsClasses.add(BindVariableToValue.class);
        alwaysAllowedModificationsClasses.add(InsertCausalLink.class);
        alwaysAllowedModificationsClasses.add(MakeLiteralsUnUnifiable.class);

        alwaysAllowedFlawClasses.add(CausalThreat.class);
        alwaysAllowedFlawClasses.add(OpenPrecondition.class);
        alwaysAllowedFlawClasses.add(UnboundVariable.class);
    }

    public static void main(String[] strs) {
        String domainFileName = "/home/dh/Schreibtisch/shop-dom/dom.lisp";
        String problemFileName = "/home/dh/Schreibtisch/shop-dom/p1.lisp";
        shopDomainReader reader = new shopDomainReader();
        Tuple2<Domain, Plan> prob = reader.readShop(domainFileName, problemFileName);

        HDDLWriter w = HDDLWriter.apply("dummyDomainName","dummyProblemName");
        System.out.println(w.writeDomain(prob._1()));
        System.out.println(w.writeProblem(prob._1(),prob._2()));
    }

    public Tuple2<Domain, Plan> readShop(String domainFileName, String problemFileName) {
        FileReader inDomain = null;
        FileReader inProblem = null;
        try {

            inDomain = new FileReader(domainFileName);
            inProblem = new FileReader(problemFileName);

            shopLexer lDomain = new shopLexer(new ANTLRInputStream(inDomain));
            shopLexer lProblem = new shopLexer(new ANTLRInputStream(inProblem));

            shopParser pDomain = new shopParser(new CommonTokenStream(lDomain));
            shopParser pProblem = new shopParser(new CommonTokenStream(lProblem));

            readShop plInstance = new readShop();
            plInstance.visitDomain(pDomain.domain());
            plInstance.visitProblem(pProblem.problem());

            seqProviderList<Sort> tSorts = new seqProviderList<>();
            seqProviderList<Constant> consts = new seqProviderList<>();
            for (String c : plInstance.consts) {
                consts.add(new Constant(c));
            }

            // sorts
            Sort topSort = new Sort("Object", consts.result(), new VectorBuilder<Sort>().result());
            tSorts.add(topSort);
            Seq<Sort> sorts = tSorts.result();

            // predicates
            HashMap<String, Predicate> predsInDecentDataStructure = new HashMap<>();
            VectorBuilder<Predicate> tPredicates = new VectorBuilder<>();
            for (String pred : plInstance.predicates.keySet()) {
                int arity = plInstance.predicates.get(pred);
                VectorBuilder<Sort> pSorts = new VectorBuilder<>();
                for (int i = 0; i < arity; i++)
                    pSorts.$plus$eq(topSort);
                Predicate p = new Predicate(pred, pSorts.result());
                tPredicates.$plus$eq(p);
                assert !predsInDecentDataStructure.containsKey(pred);
                predsInDecentDataStructure.put(pred, p);
            }
            Vector<Predicate> predicates = tPredicates.result();

            // init and goal
            seqProviderList<VariableConstraint> varConstraints = new seqProviderList<>();
            hddlPanda3Visitor.VarContext vctx = new hddlPanda3Visitor.VarContext();

            HashMap<String, Variable> varsInDecentDataStructure = this.getVariableForEveryConst(sorts, varConstraints);
            for (Variable v : varsInDecentDataStructure.values()) {
                vctx.addParameter(v);
            }

            // todo @Gregor: muss ich im POCL-Sinne die negativen Effekte setzen?
            seqProviderList<Literal> initEffects = getLiterals(predsInDecentDataStructure, varsInDecentDataStructure, plInstance.initialState);

            ReducedTask init = new ReducedTask("init(<Instance>)", true, JavaToScala.toScalaSeq(vctx.parameters), JavaToScala.toScalaSeq(vctx.parameters), varConstraints.result(), emptyAnd, new And<Literal>(initEffects.result()));
            GeneralTask goal = new GeneralTask("goal(<Instance>)", true, JavaToScala.toScalaSeq(vctx.parameters), JavaToScala.toScalaSeq(vctx.parameters), varConstraints.result(), emptyAnd, emptyAnd);

            // task definitions
            HashMap<String, Task> tasksInDecentDataStructure = new HashMap<>();
            seqProviderList<Task> tTasks = new seqProviderList<>();
            for (shOperator op : plInstance.actions) {
                String name = op.name[0];
                HashMap<String, Variable> vars = new HashMap<>();
                seqProviderList<Variable> params = new seqProviderList<>();
                for (int i = 1; i < op.name.length; i++) {
                    String vName = op.name[i];
                    Variable v = new Variable(++currentVarId, vName, topSort);
                    vars.put(vName, v);
                    params.add(v);
                }
                seqProviderList<Literal> precs = getConjunction(predsInDecentDataStructure, op.pre, vars, false);
                And<Literal> fPrec = new And<Literal>(precs.result());
                seqProviderList<Literal> adds = getConjunction(predsInDecentDataStructure, op.add, vars, false);
                seqProviderList<Literal> dels = getConjunction(predsInDecentDataStructure, op.del, vars, true);
                adds.add(dels.getIterator());
                And<Literal> effs = new And<>(adds.result());
                ReducedTask task = new ReducedTask(name, true, params.result(),
                        new seqProviderList<Variable>().result(),
                        new seqProviderList<VariableConstraint>().result(),
                        fPrec, effs);
                tTasks.add(task);
                tasksInDecentDataStructure.put(name, task);
            }
            for (String taskName : plInstance.tAbstract.keySet()) {
                int arity = plInstance.tAbstract.get(taskName);
                seqProviderList<Variable> params = new seqProviderList<>();
                for (int i = 1; i < arity + 1; i++) {
                    String vName = "?v" + i;
                    Variable v = new Variable(++currentVarId, vName, topSort);
                    params.add(v);
                }

                Task t = new ReducedTask(taskName, false, params.result(),
                        new seqProviderList<Variable>().result(),
                        new seqProviderList<VariableConstraint>().result(),
                        emptyAnd, emptyAnd);
                tTasks.add(t);
                tasksInDecentDataStructure.put(taskName, t);
            }

            Seq<Task> tasks = tTasks.result();
            seqProviderList<DecompositionMethod> tMethods = new seqProviderList<>();
            for (shMethod m : plInstance.methods) {

                // method definitions
                Task abs = tasksInDecentDataStructure.get(m.decompTask[0]);
                for (int mCase = 0; mCase < m.ifThen.size(); mCase++) {
                    List<String[]> precs = m.ifThen.get(mCase)[0];
                    List<String[]> tn = m.ifThen.get(mCase)[1];

                    // Parameter of abstract task
                    seqProviderList<Variable> methodParams = new seqProviderList<>();
                    HashMap<String, Variable> paramsInDecentDataStructure = new HashMap<>();
                    for (String var : m.varsOfTask()) {
                        Variable v = new Variable(++currentVarId, var, topSort);
                        methodParams.add(v);
                        paramsInDecentDataStructure.put(var, v);
                    }

                    // Vars of this case
                    for (String var : m.addedVarsInLayer(mCase)) {
                        Variable v = new Variable(++currentVarId, var, topSort);
                        methodParams.add(v);
                        paramsInDecentDataStructure.put(var, v);
                    }

                    // Bind vars of abstract task
                    seqProviderList<Variable> tnVars = new seqProviderList<>();
                    seqProviderList<VariableConstraint> tnConstraints = new seqProviderList<>();
                    tnVars.add(methodParams.result());
                    tnVars.add(abs.parameters());

                    for (int i = 1; i < m.decompTask.length; i++) {
                        Variable v1 = paramsInDecentDataStructure.get(m.decompTask[i]);
                        Variable v2 = abs.parameters().apply(i - 1);
                        tnConstraints.add(new Equal(v1, v2));
                    }

                    // Preconditions of this round
                    seqProviderList<Literal> precLits = new seqProviderList<>();
                    for (String[] prec : precs) {
                        Literal l = getLiteral(predsInDecentDataStructure, paramsInDecentDataStructure, prec);
                        precLits.add(l);
                    }
                    Formula precThisRound = new And<>(precLits.result());

                    // add negated precs of other cases
                    seqProviderList<Formula> allCases = new seqProviderList<>();
                    for (int otherCase = 0; otherCase < mCase; otherCase++) {
                        HashMap<String, Variable> vars = new HashMap<>();
                        for (String var : m.addedVarsInLayer(otherCase)) {
                            vars.put(var, new Variable(++currentVarId, "c-" + otherCase + var.replace('?','_'), topSort));
                        }
                        HashMap<String, Variable> tempVars = new HashMap<>();
                        tempVars.putAll(paramsInDecentDataStructure);
                        tempVars.putAll(vars);
                        Seq<Literal> someCase = getConjunction(predsInDecentDataStructure, m.ifThen.get(otherCase)[0], tempVars, false).result();
                        Formula f = new And<>(someCase);
                        for(Variable v : vars.values()){
                            f = new Exists(v,f);
                        }
                        f = new Not(f);
                        allCases.add(f);
                    }
                    allCases.add(precThisRound);
                    Formula precFormula = new And(allCases.result());

                    // Create subplan
                    GeneralTask initSchema = new GeneralTask("init", true,
                            abs.parameters(),
                            abs.parameters(),
                            new Vector<>(0, 0, 0),
                            emptyAnd,
                            abs.precondition());
                    GeneralTask goalSchema = new GeneralTask("goal", true,
                            abs.parameters(),
                            abs.parameters(),
                            new Vector<>(0, 0, 0),
                            abs.effect(),
                            emptyAnd);

                    PlanStep psInit = new PlanStep(-1, initSchema, abs.parameters());
                    PlanStep psGoal = new PlanStep(-2, goalSchema, abs.parameters());
                    seqProviderList<PlanStep> subtasks = new seqProviderList<>();
                    subtasks.add(psInit);
                    for (String[] somet : tn) {
                        Task t = tasksInDecentDataStructure.get(somet[0]);
                        seqProviderList<Variable> params = new seqProviderList<>();
                        for (int i = 1; i < somet.length; i++) {
                            params.add(paramsInDecentDataStructure.get(somet[i]));
                        }
                        subtasks.add(new PlanStep(++currentVarId, t, params.result()));
                    }
                    subtasks.add(psGoal);

                    Seq<PlanStep> subTaskSeq = subtasks.result();
                    TaskOrdering to = TaskOrdering.totalOrdering(subTaskSeq);
                    Plan subPlan = new Plan(subTaskSeq, emptyCausalLink, to,
                            new CSP(tnVars.resultSet(), tnConstraints.result()), psInit, psGoal,
                            NoModifications$.MODULE$,
                            NoFlaws$.MODULE$,
                            hddlPanda3Visitor.planStepsDecomposedBy,
                            hddlPanda3Visitor.planStepsDecompositionParents);

                    SHOPDecompositionMethod method = new SHOPDecompositionMethod(abs, subPlan, precFormula, emptyAnd, m.getName() + "-" + mCase);
                    tMethods.add(method);
                }
            }
            Domain d = new Domain(sorts, predicates, tasks, tMethods.result(),
                    new seqProviderList<DecompositionAxiom>().result(), None$.empty(), None$.empty());

            PlanStep tniInit = new PlanStep(++currentVarId, init, JavaToScala.toScalaSeq(vctx.parameters));
            PlanStep tniGoal = new PlanStep(++currentVarId, goal, JavaToScala.toScalaSeq(vctx.parameters));

            seqProviderList<PlanStep> tTni = new seqProviderList<>();
            tTni.add(tniInit);
            tTni.add(tniGoal);
            TaskOrdering emptyOrdering = new TaskOrdering(new seqProviderList<OrderingConstraint>().result(), new seqProviderList<PlanStep>().result());
            TaskOrdering to = emptyOrdering;
            for (String[] iTask : plInstance.initialTN) {
                Task t = tasksInDecentDataStructure.get(iTask[0]);
                seqProviderList<Variable> params = new seqProviderList<>();
                for (int i = 1; i < iTask.length; i++) {
                    params.add(varsInDecentDataStructure.get(iTask[i].substring(1)));
                }
                PlanStep newStep = new PlanStep(++currentVarId, t, params.result());
                tTni.add(newStep);
                to = to.addOrdering(tniInit, newStep);
                to = to.addOrdering(newStep, tniGoal);
            }


            CSP tniCSP = new CSP(JavaToScala.toScalaSet(vctx.parameters), varConstraints.result());
            Plan p = new Plan(tTni.result(), emptyCausalLink, to, tniCSP, tniInit, tniGoal, NoModifications$.MODULE$,
                    NoFlaws$.MODULE$,
                    hddlPanda3Visitor.planStepsDecomposedBy,
                    hddlPanda3Visitor.planStepsDecompositionParents);

            return new Tuple2<>(d, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Literal getLiteral(HashMap<String, Predicate> predsInDecentDataStructure, HashMap<String, Variable> mParams, String[] prec) {
        boolean isPos = prec[0].equals("pos");
        Predicate p = predsInDecentDataStructure.get(prec[1]);
        seqProviderList<Variable> params = new seqProviderList<>();
        for (int i = 2; i < prec.length; i++) {
            params.add(mParams.get(prec[i]));

        }
        return new Literal(p, isPos, params.result());
    }

    private seqProviderList<Literal> getConjunction(HashMap<String, Predicate> predsInDecentDataStructure, List<String[]> someList, HashMap<String, Variable> vars, boolean negate) {
        seqProviderList<Literal> conj = new seqProviderList<>();
        for (String[] elem : someList) {
            boolean isPos = elem[0].equals("pos");
            if (negate)
                isPos = !isPos;
            Predicate pred = predsInDecentDataStructure.get(elem[1]);
            seqProviderList<Variable> params = new seqProviderList<>();
            for (int i = 2; i < elem.length; i++) {
                assert vars.containsKey(elem[i]);
                params.add(vars.get(elem[i]));
            }
            conj.add(new Literal(pred, isPos, params.result()));
        }
        return conj;
    }

    private seqProviderList<Literal> getLiterals(HashMap<String, Predicate> predsInDecentDataStructure, HashMap<String, Variable> varsInDecentDataStructure, List<String[]> initialState) {
        seqProviderList<Literal> initEffects = new seqProviderList<>();
        for (String[] formula : initialState) {
            boolean isPos = formula[0].equals(readShop.cPosLiteral);
            Predicate p = predsInDecentDataStructure.get(formula[1]);
            seqProviderList<Variable> params = new seqProviderList<>();
            for (int i = 2; i < formula.length; i++) {
                Variable var = varsInDecentDataStructure.get(formula[i].substring(1));
                params.add(var);
            }
            initEffects.add(new Literal(p, isPos, params.result()));
        }
        return initEffects;
    }

    int currentVarId = 0;

    private HashMap<String, Variable> getVariableForEveryConst(Seq<Sort> sorts, seqProviderList<VariableConstraint> varConstraints) {
        HashMap<String, Variable> taskParameter = new HashMap<>();
        for (int i = 0; i < sorts.length(); i++) {
            Sort s = sorts.apply(i);
            for (int j = 0; j < s.elements().length(); j++) {
                Constant c = s.elements().apply(j);
                Variable v = new Variable(++currentVarId, "varFor" + c.name(), s);

                assert !taskParameter.containsKey(c.name());
                taskParameter.put(c.name(), v);
                Equal eq = new Equal(v, c);
                varConstraints.add(eq);
            }
        }
        return taskParameter;
    }
}
