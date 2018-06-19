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

package de.uniulm.ki.panda3.util.shopReader;

import de.uniulm.ki.panda3.util.shopReader.internalModel.shMethod;
import de.uniulm.ki.panda3.util.shopReader.internalModel.shOperator;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * Created by dh on 02.08.17.
 */
public class readShop implements shopVisitor {

    public static final String cConstSymbol = "~";
    public static final String cPosLiteral = "pos";
    public static final String cNegLiteral = "neg";

    public Set<String> consts = new HashSet<>();
    public HashMap<String, Integer> predicates = new HashMap<>();
    public HashMap<String, Integer> tPrimitive = new HashMap<>();
    public HashMap<String, Integer> tAbstract = new HashMap<>();
    public List<shMethod> methods;
    public List<shOperator> actions;
    public List<String[]> initialState;
    public List<String[]> initialTN;

    @Override
    public Object visitDomain(shopParser.DomainContext ctx) {
        List<shMethod> methods = new ArrayList<>();
        List<shOperator> operators = new ArrayList<>();

        for (shopParser.MethodContext m : ctx.method()) {
            methods.add(visitMethod(m));
        }
        for (shopParser.OperatorContext o : ctx.operator()) {
            operators.add(visitOperator(o));
        }

        for (shMethod m : methods) {
            for (List<String[]>[] oneIfThen : m.ifThen) {
                for (String[] oneSubT : oneIfThen[1]) {
                    assert (tPrimitive.containsKey(oneSubT[0]) && oneSubT.length - 1 == tPrimitive.get(oneSubT[0])) ||
                            (tAbstract.containsKey(oneSubT[0]) && oneSubT.length - 1 == tAbstract.get(oneSubT[0]));
                }
            }
        }

        for (String absT : tAbstract.keySet()) {
            if (tPrimitive.containsKey(absT)) {
                if (tAbstract.get(absT) == tPrimitive.get(absT)) {
                    System.out.println("The task name " + absT + " is used for a primitive AND abstract task with same param size!");
                    assert false;
                }
            }
        }

        this.methods = methods;
        this.actions = operators;
        return null;
    }

    @Override
    public shMethod visitMethod(shopParser.MethodContext ctx) {
        String[] decompTask = visitTask(ctx.task());
        if (tAbstract.containsKey(decompTask[0])) {
            assert (decompTask.length - 1) == tAbstract.get(decompTask[0]);
        } else {
            tAbstract.put(decompTask[0], decompTask.length - 1);
        }

        shMethod m = new shMethod(decompTask);
        for (int i = 0; i < ctx.ifThen().size(); i++) {
            m.addIfThen(visitIfThen(ctx.ifThen(i)));
        }
        return m;
    }

    @Override
    public List<String[]>[] visitIfThen(shopParser.IfThenContext ctx) {
        List<String[]> prec = visitFormulaList(ctx.formulaList());
        List<String[]> subTasks = visitTaskList(ctx.taskList());
        List<String[]>[] res = new List[2];
        res[0] = prec;
        res[1] = subTasks;
        return res;
    }

    @Override
    public List<String[]> visitTaskList(shopParser.TaskListContext ctx) {
        List<String[]> res = new ArrayList<>();
        for (int i = 0; i < ctx.task().size(); i++)
            res.add(visitTask(ctx.task(i)));
        return res;
    }

    @Override
    public String[] visitTask(shopParser.TaskContext ctx) {
        String[] res = new String[1 + ctx.param().size()];
        res[0] = visitTaskName(ctx.taskName());
        for (int i = 0; i < ctx.param().size(); i++) {
            res[i + 1] = visitParam(ctx.param(i));
        }
        return res;
    }

    @Override
    public String visitTaskName(shopParser.TaskNameContext ctx) {
        if (ctx.opName() != null)
            return "prim_" + ctx.opName().NAME().getText();
        else
            return "abs_" + ctx.NAME().getText();
    }

    @Override
    public shOperator visitOperator(shopParser.OperatorContext ctx) {
        String[] name = visitTask(ctx.task());
        if (tPrimitive.containsKey(name[0])) {
            assert (name.length - 1) == tPrimitive.get(name[0]);
        } else {
            tPrimitive.put(name[0], name.length - 1);
        }
        List<String[]> pre = visitFormulaList(ctx.formulaList(0));
        List<String[]> del = visitFormulaList(ctx.formulaList(1));
        List<String[]> add = visitFormulaList(ctx.formulaList(2));
        shOperator op = new shOperator(name, pre, add, del);
        return op;
    }

    @Override
    public List<String[]> visitFormulaList(shopParser.FormulaListContext ctx) {
        List<String[]> formulaList = new ArrayList<>();
        for (int i = 0; i < ctx.formula().size(); i++) {
            formulaList.add(visitFormula(ctx.formula(i)));
        }
        return formulaList;
    }

    @Override
    public String[] visitFormula(shopParser.FormulaContext ctx) {
        String posNeg = "";
        String[] f = null;
        if (ctx.posFormula() != null) {
            posNeg = cPosLiteral;
            f = visitPosFormula(ctx.posFormula());
        } else if (ctx.negFormula() != null) {
            posNeg = cNegLiteral;
            f = visitPosFormula(ctx.negFormula().posFormula());
        }
        String[] res = new String[1 + f.length];
        res[0] = posNeg;
        for (int i = 0; i < f.length; i++)
            res[i + 1] = f[i];
        return res;
    }

    @Override
    public String[] visitPosFormula(shopParser.PosFormulaContext ctx) {
        String[] res = new String[1 + ctx.param().size()];
        String pred = ctx.NAME().getText();
        res[0] = pred;
        if (predicates.containsKey(pred)) {
            assert predicates.get(pred) == ctx.param().size();
        } else {
            predicates.put(pred, ctx.param().size());
        }
        for (int i = 0; i < ctx.param().size(); i++) {
            res[i + 1] = visitParam(ctx.param(i));
        }
        return res;
    }

    @Override
    public String[] visitNegFormula(shopParser.NegFormulaContext ctx) {
        return null;
    }

    @Override
    public Object visitProblem(shopParser.ProblemContext ctx) {
        List<String[]> init = visitFormulaList(ctx.formulaList());
        List<String[]> initialTN = new ArrayList<>();
        for (int i = 0; i < ctx.task().size(); i++)
            initialTN.add(visitTask(ctx.task(i)));
        this.initialState = init;
        this.initialTN = initialTN;
        return null;
    }

    @Override
    public String visitOpName(shopParser.OpNameContext ctx) {
        return ctx.NAME().getText();
    }

    @Override
    public String visitParam(shopParser.ParamContext ctx) {
        String s;
        if (ctx.VAR_NAME() != null)
            s = ctx.VAR_NAME().getText();
        else {
            String cName = ctx.NAME().getText();
            consts.add(cName);
            s = cConstSymbol + cName;
        }
        return s;
    }

    @Override
    public Object visit(ParseTree tree) {
        return null;
    }

    @Override
    public Object visitChildren(RuleNode node) {
        return null;
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        return null;
    }

    @Override
    public Object visitErrorNode(ErrorNode node) {
        return null;
    }
}
