// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/parser/hddl/hddl.g4 by ANTLR 4.5
package de.uniulm.ki.panda3.parser.hddl;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link hddlParser}.
 */
public interface hddlListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link hddlParser#hddl_file}.
     *
     * @param ctx the parse tree
     */
    void enterHddl_file(@NotNull hddlParser.Hddl_fileContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#hddl_file}.
     *
     * @param ctx the parse tree
     */
    void exitHddl_file(@NotNull hddlParser.Hddl_fileContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#domain}.
     *
     * @param ctx the parse tree
     */
    void enterDomain(@NotNull hddlParser.DomainContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#domain}.
     *
     * @param ctx the parse tree
     */
    void exitDomain(@NotNull hddlParser.DomainContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#domain_symbol}.
     *
     * @param ctx the parse tree
     */
    void enterDomain_symbol(@NotNull hddlParser.Domain_symbolContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#domain_symbol}.
     *
     * @param ctx the parse tree
     */
    void exitDomain_symbol(@NotNull hddlParser.Domain_symbolContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#require_def}.
     *
     * @param ctx the parse tree
     */
    void enterRequire_def(@NotNull hddlParser.Require_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#require_def}.
     *
     * @param ctx the parse tree
     */
    void exitRequire_def(@NotNull hddlParser.Require_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#require_defs}.
     *
     * @param ctx the parse tree
     */
    void enterRequire_defs(@NotNull hddlParser.Require_defsContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#require_defs}.
     *
     * @param ctx the parse tree
     */
    void exitRequire_defs(@NotNull hddlParser.Require_defsContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#type_def}.
     *
     * @param ctx the parse tree
     */
    void enterType_def(@NotNull hddlParser.Type_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#type_def}.
     *
     * @param ctx the parse tree
     */
    void exitType_def(@NotNull hddlParser.Type_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#predicates_def}.
     *
     * @param ctx the parse tree
     */
    void enterPredicates_def(@NotNull hddlParser.Predicates_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#predicates_def}.
     *
     * @param ctx the parse tree
     */
    void exitPredicates_def(@NotNull hddlParser.Predicates_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#atomic_formular_skeleton}.
     *
     * @param ctx the parse tree
     */
    void enterAtomic_formular_skeleton(@NotNull hddlParser.Atomic_formular_skeletonContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#atomic_formular_skeleton}.
     *
     * @param ctx the parse tree
     */
    void exitAtomic_formular_skeleton(@NotNull hddlParser.Atomic_formular_skeletonContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#task_def}.
     *
     * @param ctx the parse tree
     */
    void enterTask_def(@NotNull hddlParser.Task_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#task_def}.
     *
     * @param ctx the parse tree
     */
    void exitTask_def(@NotNull hddlParser.Task_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#task_symbol}.
     *
     * @param ctx the parse tree
     */
    void enterTask_symbol(@NotNull hddlParser.Task_symbolContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#task_symbol}.
     *
     * @param ctx the parse tree
     */
    void exitTask_symbol(@NotNull hddlParser.Task_symbolContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#method_def}.
     *
     * @param ctx the parse tree
     */
    void enterMethod_def(@NotNull hddlParser.Method_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#method_def}.
     *
     * @param ctx the parse tree
     */
    void exitMethod_def(@NotNull hddlParser.Method_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#method_symbol}.
     *
     * @param ctx the parse tree
     */
    void enterMethod_symbol(@NotNull hddlParser.Method_symbolContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#method_symbol}.
     *
     * @param ctx the parse tree
     */
    void exitMethod_symbol(@NotNull hddlParser.Method_symbolContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#subtask_defs}.
     *
     * @param ctx the parse tree
     */
    void enterSubtask_defs(@NotNull hddlParser.Subtask_defsContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#subtask_defs}.
     *
     * @param ctx the parse tree
     */
    void exitSubtask_defs(@NotNull hddlParser.Subtask_defsContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#subtask_def}.
     *
     * @param ctx the parse tree
     */
    void enterSubtask_def(@NotNull hddlParser.Subtask_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#subtask_def}.
     *
     * @param ctx the parse tree
     */
    void exitSubtask_def(@NotNull hddlParser.Subtask_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#subtask_id}.
     *
     * @param ctx the parse tree
     */
    void enterSubtask_id(@NotNull hddlParser.Subtask_idContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#subtask_id}.
     *
     * @param ctx the parse tree
     */
    void exitSubtask_id(@NotNull hddlParser.Subtask_idContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#ordering_defs}.
     *
     * @param ctx the parse tree
     */
    void enterOrdering_defs(@NotNull hddlParser.Ordering_defsContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#ordering_defs}.
     *
     * @param ctx the parse tree
     */
    void exitOrdering_defs(@NotNull hddlParser.Ordering_defsContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#ordering_def}.
     *
     * @param ctx the parse tree
     */
    void enterOrdering_def(@NotNull hddlParser.Ordering_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#ordering_def}.
     *
     * @param ctx the parse tree
     */
    void exitOrdering_def(@NotNull hddlParser.Ordering_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#constraint_defs}.
     *
     * @param ctx the parse tree
     */
    void enterConstraint_defs(@NotNull hddlParser.Constraint_defsContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#constraint_defs}.
     *
     * @param ctx the parse tree
     */
    void exitConstraint_defs(@NotNull hddlParser.Constraint_defsContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#constraint_def}.
     *
     * @param ctx the parse tree
     */
    void enterConstraint_def(@NotNull hddlParser.Constraint_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#constraint_def}.
     *
     * @param ctx the parse tree
     */
    void exitConstraint_def(@NotNull hddlParser.Constraint_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#action_def}.
     *
     * @param ctx the parse tree
     */
    void enterAction_def(@NotNull hddlParser.Action_defContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#action_def}.
     *
     * @param ctx the parse tree
     */
    void exitAction_def(@NotNull hddlParser.Action_defContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#action_symbol}.
     *
     * @param ctx the parse tree
     */
    void enterAction_symbol(@NotNull hddlParser.Action_symbolContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#action_symbol}.
     *
     * @param ctx the parse tree
     */
    void exitAction_symbol(@NotNull hddlParser.Action_symbolContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#gd}.
     *
     * @param ctx the parse tree
     */
    void enterGd(@NotNull hddlParser.GdContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#gd}.
     *
     * @param ctx the parse tree
     */
    void exitGd(@NotNull hddlParser.GdContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#effect_body}.
     *
     * @param ctx the parse tree
     */
    void enterEffect_body(@NotNull hddlParser.Effect_bodyContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#effect_body}.
     *
     * @param ctx the parse tree
     */
    void exitEffect_body(@NotNull hddlParser.Effect_bodyContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#c_effect}.
     *
     * @param ctx the parse tree
     */
    void enterC_effect(@NotNull hddlParser.C_effectContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#c_effect}.
     *
     * @param ctx the parse tree
     */
    void exitC_effect(@NotNull hddlParser.C_effectContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#p_effect}.
     *
     * @param ctx the parse tree
     */
    void enterP_effect(@NotNull hddlParser.P_effectContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#p_effect}.
     *
     * @param ctx the parse tree
     */
    void exitP_effect(@NotNull hddlParser.P_effectContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#cond_effect}.
     *
     * @param ctx the parse tree
     */
    void enterCond_effect(@NotNull hddlParser.Cond_effectContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#cond_effect}.
     *
     * @param ctx the parse tree
     */
    void exitCond_effect(@NotNull hddlParser.Cond_effectContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#atomic_formular}.
     *
     * @param ctx the parse tree
     */
    void enterAtomic_formular(@NotNull hddlParser.Atomic_formularContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#atomic_formular}.
     *
     * @param ctx the parse tree
     */
    void exitAtomic_formular(@NotNull hddlParser.Atomic_formularContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#predicate}.
     *
     * @param ctx the parse tree
     */
    void enterPredicate(@NotNull hddlParser.PredicateContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#predicate}.
     *
     * @param ctx the parse tree
     */
    void exitPredicate(@NotNull hddlParser.PredicateContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#typed_var_list}.
     *
     * @param ctx the parse tree
     */
    void enterTyped_var_list(@NotNull hddlParser.Typed_var_listContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#typed_var_list}.
     *
     * @param ctx the parse tree
     */
    void exitTyped_var_list(@NotNull hddlParser.Typed_var_listContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#typed_vars}.
     *
     * @param ctx the parse tree
     */
    void enterTyped_vars(@NotNull hddlParser.Typed_varsContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#typed_vars}.
     *
     * @param ctx the parse tree
     */
    void exitTyped_vars(@NotNull hddlParser.Typed_varsContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#var_type}.
     *
     * @param ctx the parse tree
     */
    void enterVar_type(@NotNull hddlParser.Var_typeContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#var_type}.
     *
     * @param ctx the parse tree
     */
    void exitVar_type(@NotNull hddlParser.Var_typeContext ctx);

    /**
     * Enter a parse tree produced by {@link hddlParser#problem}.
     *
     * @param ctx the parse tree
     */
    void enterProblem(@NotNull hddlParser.ProblemContext ctx);

    /**
     * Exit a parse tree produced by {@link hddlParser#problem}.
     *
     * @param ctx the parse tree
     */
    void exitProblem(@NotNull hddlParser.ProblemContext ctx);
}