// Generated from /home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/util/shop/shop.g4 by ANTLR 4.5.3
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