// Generated from /home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/util/shop/shop.g4 by ANTLR 4.5.3
package de.uniulm.ki.panda3.util.shopReader;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link shopParser}.
 */
public interface shopListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link shopParser#domain}.
	 * @param ctx the parse tree
	 */
	void enterDomain(shopParser.DomainContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#domain}.
	 * @param ctx the parse tree
	 */
	void exitDomain(shopParser.DomainContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(shopParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(shopParser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#ifThen}.
	 * @param ctx the parse tree
	 */
	void enterIfThen(shopParser.IfThenContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#ifThen}.
	 * @param ctx the parse tree
	 */
	void exitIfThen(shopParser.IfThenContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#taskList}.
	 * @param ctx the parse tree
	 */
	void enterTaskList(shopParser.TaskListContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#taskList}.
	 * @param ctx the parse tree
	 */
	void exitTaskList(shopParser.TaskListContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#task}.
	 * @param ctx the parse tree
	 */
	void enterTask(shopParser.TaskContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#task}.
	 * @param ctx the parse tree
	 */
	void exitTask(shopParser.TaskContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#taskName}.
	 * @param ctx the parse tree
	 */
	void enterTaskName(shopParser.TaskNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#taskName}.
	 * @param ctx the parse tree
	 */
	void exitTaskName(shopParser.TaskNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(shopParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(shopParser.OperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#formulaList}.
	 * @param ctx the parse tree
	 */
	void enterFormulaList(shopParser.FormulaListContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#formulaList}.
	 * @param ctx the parse tree
	 */
	void exitFormulaList(shopParser.FormulaListContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(shopParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(shopParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#posFormula}.
	 * @param ctx the parse tree
	 */
	void enterPosFormula(shopParser.PosFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#posFormula}.
	 * @param ctx the parse tree
	 */
	void exitPosFormula(shopParser.PosFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#negFormula}.
	 * @param ctx the parse tree
	 */
	void enterNegFormula(shopParser.NegFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#negFormula}.
	 * @param ctx the parse tree
	 */
	void exitNegFormula(shopParser.NegFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#problem}.
	 * @param ctx the parse tree
	 */
	void enterProblem(shopParser.ProblemContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#problem}.
	 * @param ctx the parse tree
	 */
	void exitProblem(shopParser.ProblemContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#opName}.
	 * @param ctx the parse tree
	 */
	void enterOpName(shopParser.OpNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#opName}.
	 * @param ctx the parse tree
	 */
	void exitOpName(shopParser.OpNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link shopParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(shopParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link shopParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(shopParser.ParamContext ctx);
}