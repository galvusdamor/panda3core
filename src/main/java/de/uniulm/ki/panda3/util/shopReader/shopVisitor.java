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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link shopParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface shopVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link shopParser#domain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomain(shopParser.DomainContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(shopParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#ifThen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfThen(shopParser.IfThenContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#taskList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTaskList(shopParser.TaskListContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#task}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTask(shopParser.TaskContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#taskName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTaskName(shopParser.TaskNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(shopParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#formulaList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormulaList(shopParser.FormulaListContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(shopParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#posFormula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosFormula(shopParser.PosFormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#negFormula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegFormula(shopParser.NegFormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#problem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProblem(shopParser.ProblemContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#opName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpName(shopParser.OpNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link shopParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(shopParser.ParamContext ctx);
}
