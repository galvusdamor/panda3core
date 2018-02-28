// Generated from /home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4 by ANTLR 4.5.3
package de.uniulm.ki.panda3.symbolic.parser.hddl;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class antlrHDDLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, T__64=65, T__65=66, 
		T__66=67, T__67=68, REQUIRE_NAME=69, VAR_NAME=70, NAME=71, COMMENT=72, 
		WS=73, NUMBER=74;
	public static final int
		RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3, 
		RULE_require_defs = 4, RULE_type_def = 5, RULE_type_def_list = 6, RULE_new_types = 7, 
		RULE_const_def = 8, RULE_predicates_def = 9, RULE_atomic_formula_skeleton = 10, 
		RULE_funtions_def = 11, RULE_comp_task_def = 12, RULE_task_def = 13, RULE_task_symbol = 14, 
		RULE_method_def = 15, RULE_tasknetwork_def = 16, RULE_method_symbol = 17, 
		RULE_subtask_defs = 18, RULE_subtask_def = 19, RULE_subtask_id = 20, RULE_ordering_defs = 21, 
		RULE_ordering_def = 22, RULE_constraint_defs = 23, RULE_constraint_def = 24, 
		RULE_causallink_defs = 25, RULE_causallink_def = 26, RULE_action_def = 27, 
		RULE_gd = 28, RULE_gd_empty = 29, RULE_gd_conjuction = 30, RULE_gd_disjuction = 31, 
		RULE_gd_negation = 32, RULE_gd_implication = 33, RULE_gd_existential = 34, 
		RULE_gd_universal = 35, RULE_gd_equality_constraint = 36, RULE_gd_ltl_at_end = 37, 
		RULE_gd_ltl_always = 38, RULE_gd_ltl_sometime = 39, RULE_gd_ltl_at_most_once = 40, 
		RULE_gd_ltl_sometime_after = 41, RULE_gd_ltl_sometime_before = 42, RULE_gd_preference = 43, 
		RULE_effect = 44, RULE_eff_empty = 45, RULE_eff_conjunction = 46, RULE_eff_universal = 47, 
		RULE_eff_conditional = 48, RULE_literal = 49, RULE_neg_atomic_formula = 50, 
		RULE_p_effect = 51, RULE_assign_op = 52, RULE_f_head = 53, RULE_f_exp = 54, 
		RULE_bin_op = 55, RULE_multi_op = 56, RULE_atomic_formula = 57, RULE_predicate = 58, 
		RULE_equallity = 59, RULE_typed_var_list = 60, RULE_typed_obj_list = 61, 
		RULE_typed_vars = 62, RULE_typed_var = 63, RULE_typed_objs = 64, RULE_new_consts = 65, 
		RULE_var_type = 66, RULE_var_or_const = 67, RULE_term = 68, RULE_functionterm = 69, 
		RULE_func_symbol = 70, RULE_problem = 71, RULE_p_object_declaration = 72, 
		RULE_p_init = 73, RULE_init_el = 74, RULE_num_init = 75, RULE_p_goal = 76, 
		RULE_p_htn = 77, RULE_metric_spec = 78, RULE_optimization = 79, RULE_ground_f_exp = 80, 
		RULE_p_constraint = 81;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "type_def_list", "new_types", "const_def", "predicates_def", 
		"atomic_formula_skeleton", "funtions_def", "comp_task_def", "task_def", 
		"task_symbol", "method_def", "tasknetwork_def", "method_symbol", "subtask_defs", 
		"subtask_def", "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", 
		"constraint_def", "causallink_defs", "causallink_def", "action_def", "gd", 
		"gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", "gd_implication", 
		"gd_existential", "gd_universal", "gd_equality_constraint", "gd_ltl_at_end", 
		"gd_ltl_always", "gd_ltl_sometime", "gd_ltl_at_most_once", "gd_ltl_sometime_after", 
		"gd_ltl_sometime_before", "gd_preference", "effect", "eff_empty", "eff_conjunction", 
		"eff_universal", "eff_conditional", "literal", "neg_atomic_formula", "p_effect", 
		"assign_op", "f_head", "f_exp", "bin_op", "multi_op", "atomic_formula", 
		"predicate", "equallity", "typed_var_list", "typed_obj_list", "typed_vars", 
		"typed_var", "typed_objs", "new_consts", "var_type", "var_or_const", "term", 
		"functionterm", "func_symbol", "problem", "p_object_declaration", "p_init", 
		"init_el", "num_init", "p_goal", "p_htn", "metric_spec", "optimization", 
		"ground_f_exp", "p_constraint"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':functions'", "'number'", "':task'", 
		"':parameters'", "':precondition'", "':effect'", "':method'", "':subtasks'", 
		"':tasks'", "':ordered-subtasks'", "':ordered-tasks'", "':ordering'", 
		"':order'", "':constraints'", "':causal-links'", "':causallinks'", "'and'", 
		"'<'", "'not'", "'type'", "'typeof'", "'sort'", "'sortof'", "':action'", 
		"'or'", "'imply'", "'exists'", "'forall'", "'at end'", "'always'", "'sometime'", 
		"'at-most-once'", "'sometime-after'", "'sometime-before'", "'preference'", 
		"'when'", "'assign'", "'scale-down'", "'scale-up'", "'increase'", "'decrease'", 
		"'/'", "'+'", "'*'", "'='", "'(='", "'either'", "'problem'", "':domain'", 
		"':objects'", "':init'", "':goal'", "':htn'", "':htnti'", "':metric'", 
		"'minimize'", "'maximize'", "'(-'", "'total-time'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, "REQUIRE_NAME", 
		"VAR_NAME", "NAME", "COMMENT", "WS", "NUMBER"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "antlrHDDL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public antlrHDDLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Hddl_fileContext extends ParserRuleContext {
		public DomainContext domain() {
			return getRuleContext(DomainContext.class,0);
		}
		public ProblemContext problem() {
			return getRuleContext(ProblemContext.class,0);
		}
		public Hddl_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hddl_file; }
	}

	public final Hddl_fileContext hddl_file() throws RecognitionException {
		Hddl_fileContext _localctx = new Hddl_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_hddl_file);
		try {
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				problem();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DomainContext extends ParserRuleContext {
		public Domain_symbolContext domain_symbol() {
			return getRuleContext(Domain_symbolContext.class,0);
		}
		public Require_defContext require_def() {
			return getRuleContext(Require_defContext.class,0);
		}
		public Type_defContext type_def() {
			return getRuleContext(Type_defContext.class,0);
		}
		public Const_defContext const_def() {
			return getRuleContext(Const_defContext.class,0);
		}
		public Predicates_defContext predicates_def() {
			return getRuleContext(Predicates_defContext.class,0);
		}
		public Funtions_defContext funtions_def() {
			return getRuleContext(Funtions_defContext.class,0);
		}
		public List<Comp_task_defContext> comp_task_def() {
			return getRuleContexts(Comp_task_defContext.class);
		}
		public Comp_task_defContext comp_task_def(int i) {
			return getRuleContext(Comp_task_defContext.class,i);
		}
		public List<Method_defContext> method_def() {
			return getRuleContexts(Method_defContext.class);
		}
		public Method_defContext method_def(int i) {
			return getRuleContext(Method_defContext.class,i);
		}
		public List<Action_defContext> action_def() {
			return getRuleContexts(Action_defContext.class);
		}
		public Action_defContext action_def(int i) {
			return getRuleContext(Action_defContext.class,i);
		}
		public DomainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domain; }
	}

	public final DomainContext domain() throws RecognitionException {
		DomainContext _localctx = new DomainContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_domain);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(T__0);
			setState(169);
			match(T__1);
			setState(170);
			match(T__0);
			setState(171);
			match(T__2);
			setState(172);
			domain_symbol();
			setState(173);
			match(T__3);
			setState(175);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(174);
				require_def();
				}
				break;
			}
			setState(178);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(177);
				type_def();
				}
				break;
			}
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(180);
				const_def();
				}
				break;
			}
			setState(184);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(183);
				predicates_def();
				}
				break;
			}
			setState(187);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(186);
				funtions_def();
				}
				break;
			}
			setState(192);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(189);
					comp_task_def();
					}
					} 
				}
				setState(194);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(198);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(195);
					method_def();
					}
					} 
				}
				setState(200);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(201);
				action_def();
				}
				}
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(207);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Domain_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public Domain_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domain_symbol; }
	}

	public final Domain_symbolContext domain_symbol() throws RecognitionException {
		Domain_symbolContext _localctx = new Domain_symbolContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_domain_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Require_defContext extends ParserRuleContext {
		public Require_defsContext require_defs() {
			return getRuleContext(Require_defsContext.class,0);
		}
		public Require_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_require_def; }
	}

	public final Require_defContext require_def() throws RecognitionException {
		Require_defContext _localctx = new Require_defContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_require_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(T__0);
			setState(212);
			match(T__4);
			setState(213);
			require_defs();
			setState(214);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Require_defsContext extends ParserRuleContext {
		public List<TerminalNode> REQUIRE_NAME() { return getTokens(antlrHDDLParser.REQUIRE_NAME); }
		public TerminalNode REQUIRE_NAME(int i) {
			return getToken(antlrHDDLParser.REQUIRE_NAME, i);
		}
		public Require_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_require_defs; }
	}

	public final Require_defsContext require_defs() throws RecognitionException {
		Require_defsContext _localctx = new Require_defsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_require_defs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(216);
				match(REQUIRE_NAME);
				}
				}
				setState(219); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==REQUIRE_NAME );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_defContext extends ParserRuleContext {
		public Type_def_listContext type_def_list() {
			return getRuleContext(Type_def_listContext.class,0);
		}
		public Type_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_def; }
	}

	public final Type_defContext type_def() throws RecognitionException {
		Type_defContext _localctx = new Type_defContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_type_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			match(T__0);
			setState(222);
			match(T__5);
			setState(223);
			type_def_list();
			setState(224);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_def_listContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(antlrHDDLParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(antlrHDDLParser.NAME, i);
		}
		public New_typesContext new_types() {
			return getRuleContext(New_typesContext.class,0);
		}
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public Type_def_listContext type_def_list() {
			return getRuleContext(Type_def_listContext.class,0);
		}
		public Type_def_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_def_list; }
	}

	public final Type_def_listContext type_def_list() throws RecognitionException {
		Type_def_listContext _localctx = new Type_def_listContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_type_def_list);
		int _la;
		try {
			setState(237);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(226);
					match(NAME);
					}
					}
					setState(231);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(232);
				new_types();
				setState(233);
				match(T__6);
				setState(234);
				var_type();
				setState(235);
				type_def_list();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class New_typesContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(antlrHDDLParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(antlrHDDLParser.NAME, i);
		}
		public New_typesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_new_types; }
	}

	public final New_typesContext new_types() throws RecognitionException {
		New_typesContext _localctx = new New_typesContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_new_types);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(239);
				match(NAME);
				}
				}
				setState(242); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Const_defContext extends ParserRuleContext {
		public Typed_obj_listContext typed_obj_list() {
			return getRuleContext(Typed_obj_listContext.class,0);
		}
		public Const_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_const_def; }
	}

	public final Const_defContext const_def() throws RecognitionException {
		Const_defContext _localctx = new Const_defContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_const_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			match(T__0);
			setState(245);
			match(T__7);
			setState(246);
			typed_obj_list();
			setState(247);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Predicates_defContext extends ParserRuleContext {
		public List<Atomic_formula_skeletonContext> atomic_formula_skeleton() {
			return getRuleContexts(Atomic_formula_skeletonContext.class);
		}
		public Atomic_formula_skeletonContext atomic_formula_skeleton(int i) {
			return getRuleContext(Atomic_formula_skeletonContext.class,i);
		}
		public Predicates_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicates_def; }
	}

	public final Predicates_defContext predicates_def() throws RecognitionException {
		Predicates_defContext _localctx = new Predicates_defContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_predicates_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			match(T__0);
			setState(250);
			match(T__8);
			setState(252); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(251);
				atomic_formula_skeleton();
				}
				}
				setState(254); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(256);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Atomic_formula_skeletonContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public Atomic_formula_skeletonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_formula_skeleton; }
	}

	public final Atomic_formula_skeletonContext atomic_formula_skeleton() throws RecognitionException {
		Atomic_formula_skeletonContext _localctx = new Atomic_formula_skeletonContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_atomic_formula_skeleton);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(T__0);
			setState(259);
			predicate();
			setState(260);
			typed_var_list();
			setState(261);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Funtions_defContext extends ParserRuleContext {
		public List<Atomic_formula_skeletonContext> atomic_formula_skeleton() {
			return getRuleContexts(Atomic_formula_skeletonContext.class);
		}
		public Atomic_formula_skeletonContext atomic_formula_skeleton(int i) {
			return getRuleContext(Atomic_formula_skeletonContext.class,i);
		}
		public List<Var_typeContext> var_type() {
			return getRuleContexts(Var_typeContext.class);
		}
		public Var_typeContext var_type(int i) {
			return getRuleContext(Var_typeContext.class,i);
		}
		public Funtions_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funtions_def; }
	}

	public final Funtions_defContext funtions_def() throws RecognitionException {
		Funtions_defContext _localctx = new Funtions_defContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_funtions_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			match(T__0);
			setState(264);
			match(T__9);
			setState(271); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(265);
				atomic_formula_skeleton();
				setState(269);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(266);
					match(T__6);
					setState(267);
					match(T__10);
					}
					break;
				case 2:
					{
					setState(268);
					var_type();
					}
					break;
				}
				}
				}
				setState(273); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(275);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Comp_task_defContext extends ParserRuleContext {
		public Task_defContext task_def() {
			return getRuleContext(Task_defContext.class,0);
		}
		public Comp_task_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comp_task_def; }
	}

	public final Comp_task_defContext comp_task_def() throws RecognitionException {
		Comp_task_defContext _localctx = new Comp_task_defContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_comp_task_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(T__0);
			setState(278);
			match(T__11);
			setState(279);
			task_def();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Task_defContext extends ParserRuleContext {
		public Task_symbolContext task_symbol() {
			return getRuleContext(Task_symbolContext.class,0);
		}
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public Task_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_task_def; }
	}

	public final Task_defContext task_def() throws RecognitionException {
		Task_defContext _localctx = new Task_defContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_task_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			task_symbol();
			setState(282);
			match(T__12);
			setState(283);
			match(T__0);
			setState(284);
			typed_var_list();
			setState(285);
			match(T__3);
			setState(288);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(286);
				match(T__13);
				setState(287);
				gd();
				}
			}

			setState(292);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(290);
				match(T__14);
				setState(291);
				effect();
				}
			}

			setState(294);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Task_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public Task_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_task_symbol; }
	}

	public final Task_symbolContext task_symbol() throws RecognitionException {
		Task_symbolContext _localctx = new Task_symbolContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_task_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Method_defContext extends ParserRuleContext {
		public Method_symbolContext method_symbol() {
			return getRuleContext(Method_symbolContext.class,0);
		}
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public Task_symbolContext task_symbol() {
			return getRuleContext(Task_symbolContext.class,0);
		}
		public Tasknetwork_defContext tasknetwork_def() {
			return getRuleContext(Tasknetwork_defContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public Method_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_def; }
	}

	public final Method_defContext method_def() throws RecognitionException {
		Method_defContext _localctx = new Method_defContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_method_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(T__0);
			setState(299);
			match(T__15);
			setState(300);
			method_symbol();
			setState(301);
			match(T__12);
			setState(302);
			match(T__0);
			setState(303);
			typed_var_list();
			setState(304);
			match(T__3);
			setState(305);
			match(T__11);
			setState(306);
			match(T__0);
			setState(307);
			task_symbol();
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(308);
				var_or_const();
				}
				}
				setState(313);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(314);
			match(T__3);
			setState(317);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(315);
				match(T__13);
				setState(316);
				gd();
				}
			}

			setState(321);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(319);
				match(T__14);
				setState(320);
				effect();
				}
			}

			setState(323);
			tasknetwork_def();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tasknetwork_defContext extends ParserRuleContext {
		public Subtask_defsContext subtask_defs() {
			return getRuleContext(Subtask_defsContext.class,0);
		}
		public Ordering_defsContext ordering_defs() {
			return getRuleContext(Ordering_defsContext.class,0);
		}
		public Constraint_defsContext constraint_defs() {
			return getRuleContext(Constraint_defsContext.class,0);
		}
		public Causallink_defsContext causallink_defs() {
			return getRuleContext(Causallink_defsContext.class,0);
		}
		public Tasknetwork_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tasknetwork_def; }
	}

	public final Tasknetwork_defContext tasknetwork_def() throws RecognitionException {
		Tasknetwork_defContext _localctx = new Tasknetwork_defContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_tasknetwork_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(325);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(326);
				subtask_defs();
				}
			}

			setState(331);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(329);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(330);
				ordering_defs();
				}
			}

			setState(335);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(333);
				match(T__22);
				setState(334);
				constraint_defs();
				}
			}

			setState(339);
			_la = _input.LA(1);
			if (_la==T__23 || _la==T__24) {
				{
				setState(337);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==T__24) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(338);
				causallink_defs();
				}
			}

			setState(341);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Method_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public Method_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_symbol; }
	}

	public final Method_symbolContext method_symbol() throws RecognitionException {
		Method_symbolContext _localctx = new Method_symbolContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_method_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subtask_defsContext extends ParserRuleContext {
		public List<Subtask_defContext> subtask_def() {
			return getRuleContexts(Subtask_defContext.class);
		}
		public Subtask_defContext subtask_def(int i) {
			return getRuleContext(Subtask_defContext.class,i);
		}
		public Subtask_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask_defs; }
	}

	public final Subtask_defsContext subtask_defs() throws RecognitionException {
		Subtask_defsContext _localctx = new Subtask_defsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_subtask_defs);
		int _la;
		try {
			setState(357);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				match(T__0);
				setState(346);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(347);
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(348);
				match(T__0);
				setState(349);
				match(T__25);
				setState(351); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(350);
					subtask_def();
					}
					}
					setState(353); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(355);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subtask_defContext extends ParserRuleContext {
		public Task_symbolContext task_symbol() {
			return getRuleContext(Task_symbolContext.class,0);
		}
		public Subtask_idContext subtask_id() {
			return getRuleContext(Subtask_idContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Subtask_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask_def; }
	}

	public final Subtask_defContext subtask_def() throws RecognitionException {
		Subtask_defContext _localctx = new Subtask_defContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_subtask_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(382);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(359);
				match(T__0);
				setState(360);
				task_symbol();
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(361);
					var_or_const();
					}
					}
					setState(366);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(367);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(369);
				match(T__0);
				setState(370);
				subtask_id();
				setState(371);
				match(T__0);
				setState(372);
				task_symbol();
				setState(376);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(373);
					var_or_const();
					}
					}
					setState(378);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(379);
				match(T__3);
				setState(380);
				match(T__3);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subtask_idContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public Subtask_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask_id; }
	}

	public final Subtask_idContext subtask_id() throws RecognitionException {
		Subtask_idContext _localctx = new Subtask_idContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_subtask_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ordering_defsContext extends ParserRuleContext {
		public List<Ordering_defContext> ordering_def() {
			return getRuleContexts(Ordering_defContext.class);
		}
		public Ordering_defContext ordering_def(int i) {
			return getRuleContext(Ordering_defContext.class,i);
		}
		public Ordering_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordering_defs; }
	}

	public final Ordering_defsContext ordering_defs() throws RecognitionException {
		Ordering_defsContext _localctx = new Ordering_defsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_ordering_defs);
		int _la;
		try {
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(386);
				match(T__0);
				setState(387);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(388);
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(389);
				match(T__0);
				setState(390);
				match(T__25);
				setState(392); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(391);
					ordering_def();
					}
					}
					setState(394); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(396);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ordering_defContext extends ParserRuleContext {
		public List<Subtask_idContext> subtask_id() {
			return getRuleContexts(Subtask_idContext.class);
		}
		public Subtask_idContext subtask_id(int i) {
			return getRuleContext(Subtask_idContext.class,i);
		}
		public Ordering_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordering_def; }
	}

	public final Ordering_defContext ordering_def() throws RecognitionException {
		Ordering_defContext _localctx = new Ordering_defContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_ordering_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			match(T__0);
			setState(401);
			subtask_id();
			setState(402);
			match(T__26);
			setState(403);
			subtask_id();
			setState(404);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Constraint_defsContext extends ParserRuleContext {
		public List<Constraint_defContext> constraint_def() {
			return getRuleContexts(Constraint_defContext.class);
		}
		public Constraint_defContext constraint_def(int i) {
			return getRuleContext(Constraint_defContext.class,i);
		}
		public Constraint_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint_defs; }
	}

	public final Constraint_defsContext constraint_defs() throws RecognitionException {
		Constraint_defsContext _localctx = new Constraint_defsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_constraint_defs);
		int _la;
		try {
			setState(418);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(406);
				match(T__0);
				setState(407);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(408);
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(409);
				match(T__0);
				setState(410);
				match(T__25);
				setState(412); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(411);
					constraint_def();
					}
					}
					setState(414); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__54 );
				setState(416);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Constraint_defContext extends ParserRuleContext {
		public EquallityContext equallity() {
			return getRuleContext(EquallityContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Typed_varContext typed_var() {
			return getRuleContext(Typed_varContext.class,0);
		}
		public Constraint_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint_def; }
	}

	public final Constraint_defContext constraint_def() throws RecognitionException {
		Constraint_defContext _localctx = new Constraint_defContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_constraint_def);
		int _la;
		try {
			setState(448);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(420);
				match(T__0);
				setState(421);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(422);
				match(T__0);
				setState(423);
				match(T__27);
				setState(424);
				equallity();
				setState(425);
				var_or_const();
				setState(426);
				var_or_const();
				setState(427);
				match(T__3);
				setState(428);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(430);
				equallity();
				setState(431);
				var_or_const();
				setState(432);
				var_or_const();
				setState(433);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(435);
				match(T__0);
				setState(436);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(437);
				typed_var();
				setState(438);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(440);
				match(T__0);
				setState(441);
				match(T__27);
				setState(442);
				match(T__0);
				setState(443);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(444);
				typed_var();
				setState(445);
				match(T__3);
				setState(446);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Causallink_defsContext extends ParserRuleContext {
		public List<Causallink_defContext> causallink_def() {
			return getRuleContexts(Causallink_defContext.class);
		}
		public Causallink_defContext causallink_def(int i) {
			return getRuleContext(Causallink_defContext.class,i);
		}
		public Causallink_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_causallink_defs; }
	}

	public final Causallink_defsContext causallink_defs() throws RecognitionException {
		Causallink_defsContext _localctx = new Causallink_defsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_causallink_defs);
		int _la;
		try {
			setState(462);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(450);
				match(T__0);
				setState(451);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(452);
				causallink_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(453);
				match(T__0);
				setState(454);
				match(T__25);
				setState(456); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(455);
					causallink_def();
					}
					}
					setState(458); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(460);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Causallink_defContext extends ParserRuleContext {
		public List<Subtask_idContext> subtask_id() {
			return getRuleContexts(Subtask_idContext.class);
		}
		public Subtask_idContext subtask_id(int i) {
			return getRuleContext(Subtask_idContext.class,i);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public Causallink_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_causallink_def; }
	}

	public final Causallink_defContext causallink_def() throws RecognitionException {
		Causallink_defContext _localctx = new Causallink_defContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_causallink_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
			match(T__0);
			setState(465);
			subtask_id();
			setState(466);
			literal();
			setState(467);
			subtask_id();
			setState(468);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Action_defContext extends ParserRuleContext {
		public Task_defContext task_def() {
			return getRuleContext(Task_defContext.class,0);
		}
		public Action_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_def; }
	}

	public final Action_defContext action_def() throws RecognitionException {
		Action_defContext _localctx = new Action_defContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_action_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			match(T__0);
			setState(471);
			match(T__32);
			setState(472);
			task_def();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GdContext extends ParserRuleContext {
		public Gd_emptyContext gd_empty() {
			return getRuleContext(Gd_emptyContext.class,0);
		}
		public Atomic_formulaContext atomic_formula() {
			return getRuleContext(Atomic_formulaContext.class,0);
		}
		public Gd_negationContext gd_negation() {
			return getRuleContext(Gd_negationContext.class,0);
		}
		public Gd_implicationContext gd_implication() {
			return getRuleContext(Gd_implicationContext.class,0);
		}
		public Gd_conjuctionContext gd_conjuction() {
			return getRuleContext(Gd_conjuctionContext.class,0);
		}
		public Gd_disjuctionContext gd_disjuction() {
			return getRuleContext(Gd_disjuctionContext.class,0);
		}
		public Gd_existentialContext gd_existential() {
			return getRuleContext(Gd_existentialContext.class,0);
		}
		public Gd_universalContext gd_universal() {
			return getRuleContext(Gd_universalContext.class,0);
		}
		public Gd_equality_constraintContext gd_equality_constraint() {
			return getRuleContext(Gd_equality_constraintContext.class,0);
		}
		public Gd_ltl_at_endContext gd_ltl_at_end() {
			return getRuleContext(Gd_ltl_at_endContext.class,0);
		}
		public Gd_ltl_alwaysContext gd_ltl_always() {
			return getRuleContext(Gd_ltl_alwaysContext.class,0);
		}
		public Gd_ltl_sometimeContext gd_ltl_sometime() {
			return getRuleContext(Gd_ltl_sometimeContext.class,0);
		}
		public Gd_ltl_at_most_onceContext gd_ltl_at_most_once() {
			return getRuleContext(Gd_ltl_at_most_onceContext.class,0);
		}
		public Gd_ltl_sometime_afterContext gd_ltl_sometime_after() {
			return getRuleContext(Gd_ltl_sometime_afterContext.class,0);
		}
		public Gd_ltl_sometime_beforeContext gd_ltl_sometime_before() {
			return getRuleContext(Gd_ltl_sometime_beforeContext.class,0);
		}
		public Gd_preferenceContext gd_preference() {
			return getRuleContext(Gd_preferenceContext.class,0);
		}
		public GdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd; }
	}

	public final GdContext gd() throws RecognitionException {
		GdContext _localctx = new GdContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_gd);
		try {
			setState(490);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(474);
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(475);
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(476);
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(477);
				gd_implication();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(478);
				gd_conjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(479);
				gd_disjuction();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(480);
				gd_existential();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(481);
				gd_universal();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(482);
				gd_equality_constraint();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(483);
				gd_ltl_at_end();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(484);
				gd_ltl_always();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(485);
				gd_ltl_sometime();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(486);
				gd_ltl_at_most_once();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(487);
				gd_ltl_sometime_after();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(488);
				gd_ltl_sometime_before();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(489);
				gd_preference();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_emptyContext extends ParserRuleContext {
		public Gd_emptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_empty; }
	}

	public final Gd_emptyContext gd_empty() throws RecognitionException {
		Gd_emptyContext _localctx = new Gd_emptyContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_gd_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(492);
			match(T__0);
			setState(493);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_conjuctionContext extends ParserRuleContext {
		public List<GdContext> gd() {
			return getRuleContexts(GdContext.class);
		}
		public GdContext gd(int i) {
			return getRuleContext(GdContext.class,i);
		}
		public Gd_conjuctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_conjuction; }
	}

	public final Gd_conjuctionContext gd_conjuction() throws RecognitionException {
		Gd_conjuctionContext _localctx = new Gd_conjuctionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_gd_conjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495);
			match(T__0);
			setState(496);
			match(T__25);
			setState(498); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(497);
				gd();
				}
				}
				setState(500); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__54 );
			setState(502);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_disjuctionContext extends ParserRuleContext {
		public List<GdContext> gd() {
			return getRuleContexts(GdContext.class);
		}
		public GdContext gd(int i) {
			return getRuleContext(GdContext.class,i);
		}
		public Gd_disjuctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_disjuction; }
	}

	public final Gd_disjuctionContext gd_disjuction() throws RecognitionException {
		Gd_disjuctionContext _localctx = new Gd_disjuctionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_gd_disjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(504);
			match(T__0);
			setState(505);
			match(T__33);
			setState(507); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(506);
				gd();
				}
				}
				setState(509); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__54 );
			setState(511);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_negationContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_negationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_negation; }
	}

	public final Gd_negationContext gd_negation() throws RecognitionException {
		Gd_negationContext _localctx = new Gd_negationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_gd_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
			match(T__0);
			setState(514);
			match(T__27);
			setState(515);
			gd();
			setState(516);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_implicationContext extends ParserRuleContext {
		public List<GdContext> gd() {
			return getRuleContexts(GdContext.class);
		}
		public GdContext gd(int i) {
			return getRuleContext(GdContext.class,i);
		}
		public Gd_implicationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_implication; }
	}

	public final Gd_implicationContext gd_implication() throws RecognitionException {
		Gd_implicationContext _localctx = new Gd_implicationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_gd_implication);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			match(T__0);
			setState(519);
			match(T__34);
			setState(520);
			gd();
			setState(521);
			gd();
			setState(522);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_existentialContext extends ParserRuleContext {
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_existentialContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_existential; }
	}

	public final Gd_existentialContext gd_existential() throws RecognitionException {
		Gd_existentialContext _localctx = new Gd_existentialContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_gd_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			match(T__0);
			setState(525);
			match(T__35);
			setState(526);
			match(T__0);
			setState(527);
			typed_var_list();
			setState(528);
			match(T__3);
			setState(529);
			gd();
			setState(530);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_universalContext extends ParserRuleContext {
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_universalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_universal; }
	}

	public final Gd_universalContext gd_universal() throws RecognitionException {
		Gd_universalContext _localctx = new Gd_universalContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_gd_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(T__0);
			setState(533);
			match(T__36);
			setState(534);
			match(T__0);
			setState(535);
			typed_var_list();
			setState(536);
			match(T__3);
			setState(537);
			gd();
			setState(538);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_equality_constraintContext extends ParserRuleContext {
		public EquallityContext equallity() {
			return getRuleContext(EquallityContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Gd_equality_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_equality_constraint; }
	}

	public final Gd_equality_constraintContext gd_equality_constraint() throws RecognitionException {
		Gd_equality_constraintContext _localctx = new Gd_equality_constraintContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_gd_equality_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			equallity();
			setState(541);
			var_or_const();
			setState(542);
			var_or_const();
			setState(543);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_at_endContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_at_endContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_at_end; }
	}

	public final Gd_ltl_at_endContext gd_ltl_at_end() throws RecognitionException {
		Gd_ltl_at_endContext _localctx = new Gd_ltl_at_endContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_gd_ltl_at_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(545);
			match(T__0);
			setState(546);
			match(T__37);
			setState(547);
			gd();
			setState(548);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_alwaysContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_alwaysContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_always; }
	}

	public final Gd_ltl_alwaysContext gd_ltl_always() throws RecognitionException {
		Gd_ltl_alwaysContext _localctx = new Gd_ltl_alwaysContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_gd_ltl_always);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550);
			match(T__0);
			setState(551);
			match(T__38);
			setState(552);
			gd();
			setState(553);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_sometimeContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_sometimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_sometime; }
	}

	public final Gd_ltl_sometimeContext gd_ltl_sometime() throws RecognitionException {
		Gd_ltl_sometimeContext _localctx = new Gd_ltl_sometimeContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_gd_ltl_sometime);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(555);
			match(T__0);
			setState(556);
			match(T__39);
			setState(557);
			gd();
			setState(558);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_at_most_onceContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_at_most_onceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_at_most_once; }
	}

	public final Gd_ltl_at_most_onceContext gd_ltl_at_most_once() throws RecognitionException {
		Gd_ltl_at_most_onceContext _localctx = new Gd_ltl_at_most_onceContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_gd_ltl_at_most_once);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(560);
			match(T__0);
			setState(561);
			match(T__40);
			setState(562);
			gd();
			setState(563);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_sometime_afterContext extends ParserRuleContext {
		public List<GdContext> gd() {
			return getRuleContexts(GdContext.class);
		}
		public GdContext gd(int i) {
			return getRuleContext(GdContext.class,i);
		}
		public Gd_ltl_sometime_afterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_sometime_after; }
	}

	public final Gd_ltl_sometime_afterContext gd_ltl_sometime_after() throws RecognitionException {
		Gd_ltl_sometime_afterContext _localctx = new Gd_ltl_sometime_afterContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_gd_ltl_sometime_after);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			match(T__0);
			setState(566);
			match(T__41);
			setState(567);
			gd();
			setState(568);
			gd();
			setState(569);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_ltl_sometime_beforeContext extends ParserRuleContext {
		public List<GdContext> gd() {
			return getRuleContexts(GdContext.class);
		}
		public GdContext gd(int i) {
			return getRuleContext(GdContext.class,i);
		}
		public Gd_ltl_sometime_beforeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_sometime_before; }
	}

	public final Gd_ltl_sometime_beforeContext gd_ltl_sometime_before() throws RecognitionException {
		Gd_ltl_sometime_beforeContext _localctx = new Gd_ltl_sometime_beforeContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_gd_ltl_sometime_before);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			match(T__0);
			setState(572);
			match(T__42);
			setState(573);
			gd();
			setState(574);
			gd();
			setState(575);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Gd_preferenceContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_preferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_preference; }
	}

	public final Gd_preferenceContext gd_preference() throws RecognitionException {
		Gd_preferenceContext _localctx = new Gd_preferenceContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_gd_preference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			match(T__0);
			setState(578);
			match(T__43);
			setState(579);
			match(NAME);
			setState(580);
			gd();
			setState(581);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EffectContext extends ParserRuleContext {
		public Eff_emptyContext eff_empty() {
			return getRuleContext(Eff_emptyContext.class,0);
		}
		public Eff_conjunctionContext eff_conjunction() {
			return getRuleContext(Eff_conjunctionContext.class,0);
		}
		public Eff_universalContext eff_universal() {
			return getRuleContext(Eff_universalContext.class,0);
		}
		public Eff_conditionalContext eff_conditional() {
			return getRuleContext(Eff_conditionalContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public P_effectContext p_effect() {
			return getRuleContext(P_effectContext.class,0);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_effect);
		try {
			setState(589);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(583);
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(584);
				eff_conjunction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(585);
				eff_universal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(586);
				eff_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(587);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(588);
				p_effect();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eff_emptyContext extends ParserRuleContext {
		public Eff_emptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_empty; }
	}

	public final Eff_emptyContext eff_empty() throws RecognitionException {
		Eff_emptyContext _localctx = new Eff_emptyContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(591);
			match(T__0);
			setState(592);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eff_conjunctionContext extends ParserRuleContext {
		public List<EffectContext> effect() {
			return getRuleContexts(EffectContext.class);
		}
		public EffectContext effect(int i) {
			return getRuleContext(EffectContext.class,i);
		}
		public Eff_conjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_conjunction; }
	}

	public final Eff_conjunctionContext eff_conjunction() throws RecognitionException {
		Eff_conjunctionContext _localctx = new Eff_conjunctionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_eff_conjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			match(T__0);
			setState(595);
			match(T__25);
			setState(597); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(596);
				effect();
				}
				}
				setState(599); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(601);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eff_universalContext extends ParserRuleContext {
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public Eff_universalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_universal; }
	}

	public final Eff_universalContext eff_universal() throws RecognitionException {
		Eff_universalContext _localctx = new Eff_universalContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_eff_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(T__0);
			setState(604);
			match(T__36);
			setState(605);
			match(T__0);
			setState(606);
			typed_var_list();
			setState(607);
			match(T__3);
			setState(608);
			effect();
			setState(609);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eff_conditionalContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public Eff_conditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_conditional; }
	}

	public final Eff_conditionalContext eff_conditional() throws RecognitionException {
		Eff_conditionalContext _localctx = new Eff_conditionalContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_eff_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(611);
			match(T__0);
			setState(612);
			match(T__44);
			setState(613);
			gd();
			setState(614);
			effect();
			setState(615);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public Neg_atomic_formulaContext neg_atomic_formula() {
			return getRuleContext(Neg_atomic_formulaContext.class,0);
		}
		public Atomic_formulaContext atomic_formula() {
			return getRuleContext(Atomic_formulaContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_literal);
		try {
			setState(619);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(618);
				atomic_formula();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Neg_atomic_formulaContext extends ParserRuleContext {
		public Atomic_formulaContext atomic_formula() {
			return getRuleContext(Atomic_formulaContext.class,0);
		}
		public Neg_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_neg_atomic_formula; }
	}

	public final Neg_atomic_formulaContext neg_atomic_formula() throws RecognitionException {
		Neg_atomic_formulaContext _localctx = new Neg_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			match(T__0);
			setState(622);
			match(T__27);
			setState(623);
			atomic_formula();
			setState(624);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_effectContext extends ParserRuleContext {
		public Assign_opContext assign_op() {
			return getRuleContext(Assign_opContext.class,0);
		}
		public F_headContext f_head() {
			return getRuleContext(F_headContext.class,0);
		}
		public F_expContext f_exp() {
			return getRuleContext(F_expContext.class,0);
		}
		public P_effectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_effect; }
	}

	public final P_effectContext p_effect() throws RecognitionException {
		P_effectContext _localctx = new P_effectContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_p_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			match(T__0);
			setState(627);
			assign_op();
			setState(628);
			f_head();
			setState(629);
			f_exp();
			setState(630);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_opContext extends ParserRuleContext {
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class F_headContext extends ParserRuleContext {
		public Func_symbolContext func_symbol() {
			return getRuleContext(Func_symbolContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public F_headContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_f_head; }
	}

	public final F_headContext f_head() throws RecognitionException {
		F_headContext _localctx = new F_headContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_f_head);
		int _la;
		try {
			setState(645);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(634);
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(635);
				match(T__0);
				setState(636);
				func_symbol();
				setState(640);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0 || _la==VAR_NAME || _la==NAME) {
					{
					{
					setState(637);
					term();
					}
					}
					setState(642);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(643);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class F_expContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(antlrHDDLParser.NUMBER, 0); }
		public Bin_opContext bin_op() {
			return getRuleContext(Bin_opContext.class,0);
		}
		public List<F_expContext> f_exp() {
			return getRuleContexts(F_expContext.class);
		}
		public F_expContext f_exp(int i) {
			return getRuleContext(F_expContext.class,i);
		}
		public Multi_opContext multi_op() {
			return getRuleContext(Multi_opContext.class,0);
		}
		public F_headContext f_head() {
			return getRuleContext(F_headContext.class,0);
		}
		public F_expContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_f_exp; }
	}

	public final F_expContext f_exp() throws RecognitionException {
		F_expContext _localctx = new F_expContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_f_exp);
		int _la;
		try {
			setState(670);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(647);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(648);
				match(T__0);
				setState(649);
				bin_op();
				setState(650);
				f_exp();
				setState(651);
				f_exp();
				setState(652);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(654);
				match(T__0);
				setState(655);
				multi_op();
				setState(656);
				f_exp();
				setState(658); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(657);
					f_exp();
					}
					}
					setState(660); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME || _la==NUMBER );
				setState(662);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(664);
				match(T__0);
				setState(665);
				match(T__6);
				setState(666);
				f_exp();
				setState(667);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(669);
				f_head();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bin_opContext extends ParserRuleContext {
		public Multi_opContext multi_op() {
			return getRuleContext(Multi_opContext.class,0);
		}
		public Bin_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bin_op; }
	}

	public final Bin_opContext bin_op() throws RecognitionException {
		Bin_opContext _localctx = new Bin_opContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_bin_op);
		try {
			setState(675);
			switch (_input.LA(1)) {
			case T__51:
			case T__52:
				enterOuterAlt(_localctx, 1);
				{
				setState(672);
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(673);
				match(T__6);
				}
				break;
			case T__50:
				enterOuterAlt(_localctx, 3);
				{
				setState(674);
				match(T__50);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Multi_opContext extends ParserRuleContext {
		public Multi_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multi_op; }
	}

	public final Multi_opContext multi_op() throws RecognitionException {
		Multi_opContext _localctx = new Multi_opContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_multi_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(677);
			_la = _input.LA(1);
			if ( !(_la==T__51 || _la==T__52) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Atomic_formulaContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_formula; }
	}

	public final Atomic_formulaContext atomic_formula() throws RecognitionException {
		Atomic_formulaContext _localctx = new Atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(679);
			match(T__0);
			setState(680);
			predicate();
			setState(684);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(681);
				var_or_const();
				}
				}
				setState(686);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(687);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(689);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EquallityContext extends ParserRuleContext {
		public EquallityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equallity; }
	}

	public final EquallityContext equallity() throws RecognitionException {
		EquallityContext _localctx = new EquallityContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_equallity);
		try {
			setState(694);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(691);
				match(T__0);
				setState(692);
				match(T__53);
				}
				break;
			case T__54:
				enterOuterAlt(_localctx, 2);
				{
				setState(693);
				match(T__54);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typed_var_listContext extends ParserRuleContext {
		public List<Typed_varsContext> typed_vars() {
			return getRuleContexts(Typed_varsContext.class);
		}
		public Typed_varsContext typed_vars(int i) {
			return getRuleContext(Typed_varsContext.class,i);
		}
		public Typed_var_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_var_list; }
	}

	public final Typed_var_listContext typed_var_list() throws RecognitionException {
		Typed_var_listContext _localctx = new Typed_var_listContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(696);
				typed_vars();
				}
				}
				setState(701);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typed_obj_listContext extends ParserRuleContext {
		public List<Typed_objsContext> typed_objs() {
			return getRuleContexts(Typed_objsContext.class);
		}
		public Typed_objsContext typed_objs(int i) {
			return getRuleContext(Typed_objsContext.class,i);
		}
		public Typed_obj_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_obj_list; }
	}

	public final Typed_obj_listContext typed_obj_list() throws RecognitionException {
		Typed_obj_listContext _localctx = new Typed_obj_listContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(705);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(702);
				typed_objs();
				}
				}
				setState(707);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typed_varsContext extends ParserRuleContext {
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public List<TerminalNode> VAR_NAME() { return getTokens(antlrHDDLParser.VAR_NAME); }
		public TerminalNode VAR_NAME(int i) {
			return getToken(antlrHDDLParser.VAR_NAME, i);
		}
		public Typed_varsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_vars; }
	}

	public final Typed_varsContext typed_vars() throws RecognitionException {
		Typed_varsContext _localctx = new Typed_varsContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(709); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(708);
				match(VAR_NAME);
				}
				}
				setState(711); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(713);
			match(T__6);
			setState(714);
			var_type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typed_varContext extends ParserRuleContext {
		public TerminalNode VAR_NAME() { return getToken(antlrHDDLParser.VAR_NAME, 0); }
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public Typed_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_var; }
	}

	public final Typed_varContext typed_var() throws RecognitionException {
		Typed_varContext _localctx = new Typed_varContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_typed_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			match(VAR_NAME);
			setState(717);
			match(T__6);
			setState(718);
			var_type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typed_objsContext extends ParserRuleContext {
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public List<New_constsContext> new_consts() {
			return getRuleContexts(New_constsContext.class);
		}
		public New_constsContext new_consts(int i) {
			return getRuleContext(New_constsContext.class,i);
		}
		public Typed_objsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_objs; }
	}

	public final Typed_objsContext typed_objs() throws RecognitionException {
		Typed_objsContext _localctx = new Typed_objsContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_typed_objs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(721); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(720);
				new_consts();
				}
				}
				setState(723); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(725);
			match(T__6);
			setState(726);
			var_type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class New_constsContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public New_constsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_new_consts; }
	}

	public final New_constsContext new_consts() throws RecognitionException {
		New_constsContext _localctx = new New_constsContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_new_consts);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(728);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Var_typeContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public List<Var_typeContext> var_type() {
			return getRuleContexts(Var_typeContext.class);
		}
		public Var_typeContext var_type(int i) {
			return getRuleContext(Var_typeContext.class,i);
		}
		public Var_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_type; }
	}

	public final Var_typeContext var_type() throws RecognitionException {
		Var_typeContext _localctx = new Var_typeContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_var_type);
		int _la;
		try {
			setState(740);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(730);
				match(NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(731);
				match(T__0);
				setState(732);
				match(T__55);
				setState(734); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(733);
					var_type();
					}
					}
					setState(736); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME );
				setState(738);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Var_or_constContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public TerminalNode VAR_NAME() { return getToken(antlrHDDLParser.VAR_NAME, 0); }
		public Var_or_constContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_or_const; }
	}

	public final Var_or_constContext var_or_const() throws RecognitionException {
		Var_or_constContext _localctx = new Var_or_constContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(742);
			_la = _input.LA(1);
			if ( !(_la==VAR_NAME || _la==NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public TerminalNode VAR_NAME() { return getToken(antlrHDDLParser.VAR_NAME, 0); }
		public FunctiontermContext functionterm() {
			return getRuleContext(FunctiontermContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_term);
		try {
			setState(747);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(744);
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(745);
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(746);
				functionterm();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctiontermContext extends ParserRuleContext {
		public Func_symbolContext func_symbol() {
			return getRuleContext(Func_symbolContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public FunctiontermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionterm; }
	}

	public final FunctiontermContext functionterm() throws RecognitionException {
		FunctiontermContext _localctx = new FunctiontermContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_functionterm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(749);
			match(T__0);
			setState(750);
			func_symbol();
			setState(754);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==VAR_NAME || _la==NAME) {
				{
				{
				setState(751);
				term();
				}
				}
				setState(756);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(757);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Func_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(antlrHDDLParser.NAME, 0); }
		public Func_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func_symbol; }
	}

	public final Func_symbolContext func_symbol() throws RecognitionException {
		Func_symbolContext _localctx = new Func_symbolContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_func_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(759);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProblemContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(antlrHDDLParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(antlrHDDLParser.NAME, i);
		}
		public P_initContext p_init() {
			return getRuleContext(P_initContext.class,0);
		}
		public Require_defContext require_def() {
			return getRuleContext(Require_defContext.class,0);
		}
		public P_object_declarationContext p_object_declaration() {
			return getRuleContext(P_object_declarationContext.class,0);
		}
		public P_htnContext p_htn() {
			return getRuleContext(P_htnContext.class,0);
		}
		public P_goalContext p_goal() {
			return getRuleContext(P_goalContext.class,0);
		}
		public P_constraintContext p_constraint() {
			return getRuleContext(P_constraintContext.class,0);
		}
		public Metric_specContext metric_spec() {
			return getRuleContext(Metric_specContext.class,0);
		}
		public ProblemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_problem; }
	}

	public final ProblemContext problem() throws RecognitionException {
		ProblemContext _localctx = new ProblemContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(761);
			match(T__0);
			setState(762);
			match(T__1);
			setState(763);
			match(T__0);
			setState(764);
			match(T__56);
			setState(765);
			match(NAME);
			setState(766);
			match(T__3);
			setState(767);
			match(T__0);
			setState(768);
			match(T__57);
			setState(769);
			match(NAME);
			setState(770);
			match(T__3);
			setState(772);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(771);
				require_def();
				}
				break;
			}
			setState(775);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(774);
				p_object_declaration();
				}
				break;
			}
			setState(778);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(777);
				p_htn();
				}
				break;
			}
			setState(780);
			p_init();
			setState(782);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(781);
				p_goal();
				}
				break;
			}
			setState(785);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(784);
				p_constraint();
				}
				break;
			}
			setState(788);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(787);
				metric_spec();
				}
			}

			setState(790);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_object_declarationContext extends ParserRuleContext {
		public Typed_obj_listContext typed_obj_list() {
			return getRuleContext(Typed_obj_listContext.class,0);
		}
		public P_object_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_object_declaration; }
	}

	public final P_object_declarationContext p_object_declaration() throws RecognitionException {
		P_object_declarationContext _localctx = new P_object_declarationContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(792);
			match(T__0);
			setState(793);
			match(T__58);
			setState(794);
			typed_obj_list();
			setState(795);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_initContext extends ParserRuleContext {
		public List<Init_elContext> init_el() {
			return getRuleContexts(Init_elContext.class);
		}
		public Init_elContext init_el(int i) {
			return getRuleContext(Init_elContext.class,i);
		}
		public P_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_init; }
	}

	public final P_initContext p_init() throws RecognitionException {
		P_initContext _localctx = new P_initContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			match(T__0);
			setState(798);
			match(T__59);
			setState(802);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__54) {
				{
				{
				setState(799);
				init_el();
				}
				}
				setState(804);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(805);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Init_elContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public Num_initContext num_init() {
			return getRuleContext(Num_initContext.class,0);
		}
		public Init_elContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_init_el; }
	}

	public final Init_elContext init_el() throws RecognitionException {
		Init_elContext _localctx = new Init_elContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_init_el);
		try {
			setState(809);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(807);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(808);
				num_init();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Num_initContext extends ParserRuleContext {
		public EquallityContext equallity() {
			return getRuleContext(EquallityContext.class,0);
		}
		public F_headContext f_head() {
			return getRuleContext(F_headContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(antlrHDDLParser.NUMBER, 0); }
		public Num_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_num_init; }
	}

	public final Num_initContext num_init() throws RecognitionException {
		Num_initContext _localctx = new Num_initContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_num_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(811);
			equallity();
			setState(812);
			f_head();
			setState(813);
			match(NUMBER);
			setState(814);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_goalContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public P_goalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_goal; }
	}

	public final P_goalContext p_goal() throws RecognitionException {
		P_goalContext _localctx = new P_goalContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(T__0);
			setState(817);
			match(T__60);
			setState(818);
			gd();
			setState(819);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_htnContext extends ParserRuleContext {
		public Tasknetwork_defContext tasknetwork_def() {
			return getRuleContext(Tasknetwork_defContext.class,0);
		}
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public P_htnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_htn; }
	}

	public final P_htnContext p_htn() throws RecognitionException {
		P_htnContext _localctx = new P_htnContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(821);
			match(T__0);
			setState(822);
			_la = _input.LA(1);
			if ( !(_la==T__61 || _la==T__62) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(828);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(823);
				match(T__12);
				setState(824);
				match(T__0);
				setState(825);
				typed_var_list();
				setState(826);
				match(T__3);
				}
			}

			setState(830);
			tasknetwork_def();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Metric_specContext extends ParserRuleContext {
		public OptimizationContext optimization() {
			return getRuleContext(OptimizationContext.class,0);
		}
		public Ground_f_expContext ground_f_exp() {
			return getRuleContext(Ground_f_expContext.class,0);
		}
		public Metric_specContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metric_spec; }
	}

	public final Metric_specContext metric_spec() throws RecognitionException {
		Metric_specContext _localctx = new Metric_specContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_metric_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(832);
			match(T__0);
			setState(833);
			match(T__63);
			setState(834);
			optimization();
			setState(835);
			ground_f_exp();
			setState(836);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptimizationContext extends ParserRuleContext {
		public OptimizationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optimization; }
	}

	public final OptimizationContext optimization() throws RecognitionException {
		OptimizationContext _localctx = new OptimizationContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_optimization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(838);
			_la = _input.LA(1);
			if ( !(_la==T__64 || _la==T__65) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ground_f_expContext extends ParserRuleContext {
		public Bin_opContext bin_op() {
			return getRuleContext(Bin_opContext.class,0);
		}
		public List<Ground_f_expContext> ground_f_exp() {
			return getRuleContexts(Ground_f_expContext.class);
		}
		public Ground_f_expContext ground_f_exp(int i) {
			return getRuleContext(Ground_f_expContext.class,i);
		}
		public Multi_opContext multi_op() {
			return getRuleContext(Multi_opContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(antlrHDDLParser.NUMBER, 0); }
		public Func_symbolContext func_symbol() {
			return getRuleContext(Func_symbolContext.class,0);
		}
		public List<TerminalNode> NAME() { return getTokens(antlrHDDLParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(antlrHDDLParser.NAME, i);
		}
		public Ground_f_expContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ground_f_exp; }
	}

	public final Ground_f_expContext ground_f_exp() throws RecognitionException {
		Ground_f_expContext _localctx = new Ground_f_expContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_ground_f_exp);
		int _la;
		try {
			setState(877);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(840);
				match(T__0);
				setState(841);
				bin_op();
				setState(842);
				ground_f_exp();
				setState(843);
				ground_f_exp();
				setState(844);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(846);
				match(T__0);
				setState(847);
				multi_op();
				setState(848);
				ground_f_exp();
				setState(850); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(849);
					ground_f_exp();
					}
					}
					setState(852); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (T__66 - 67)) | (1L << (T__67 - 67)) | (1L << (NAME - 67)) | (1L << (NUMBER - 67)))) != 0) );
				setState(854);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(859);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(856);
					match(T__0);
					setState(857);
					match(T__6);
					}
					break;
				case T__66:
					{
					setState(858);
					match(T__66);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(861);
				ground_f_exp();
				setState(862);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(864);
				match(NUMBER);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(865);
				match(T__0);
				setState(866);
				func_symbol();
				setState(870);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(867);
					match(NAME);
					}
					}
					setState(872);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(873);
				match(T__3);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(875);
				match(T__67);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(876);
				func_symbol();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_constraintContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public P_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_constraint; }
	}

	public final P_constraintContext p_constraint() throws RecognitionException {
		P_constraintContext _localctx = new P_constraintContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_p_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(879);
			match(T__0);
			setState(880);
			match(T__22);
			setState(881);
			gd();
			setState(882);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3L\u0377\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\3\2\3"+
		"\2\5\2\u00a9\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u00b2\n\3\3\3\5\3\u00b5"+
		"\n\3\3\3\5\3\u00b8\n\3\3\3\5\3\u00bb\n\3\3\3\5\3\u00be\n\3\3\3\7\3\u00c1"+
		"\n\3\f\3\16\3\u00c4\13\3\3\3\7\3\u00c7\n\3\f\3\16\3\u00ca\13\3\3\3\7\3"+
		"\u00cd\n\3\f\3\16\3\u00d0\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6"+
		"\6\6\u00dc\n\6\r\6\16\6\u00dd\3\7\3\7\3\7\3\7\3\7\3\b\7\b\u00e6\n\b\f"+
		"\b\16\b\u00e9\13\b\3\b\3\b\3\b\3\b\3\b\5\b\u00f0\n\b\3\t\6\t\u00f3\n\t"+
		"\r\t\16\t\u00f4\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\6\13\u00ff\n\13\r\13"+
		"\16\13\u0100\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\5\r"+
		"\u0110\n\r\6\r\u0112\n\r\r\r\16\r\u0113\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0123\n\17\3\17\3\17\5\17\u0127"+
		"\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\7\21\u0138\n\21\f\21\16\21\u013b\13\21\3\21\3\21\3\21\5\21"+
		"\u0140\n\21\3\21\3\21\5\21\u0144\n\21\3\21\3\21\3\22\3\22\5\22\u014a\n"+
		"\22\3\22\3\22\5\22\u014e\n\22\3\22\3\22\5\22\u0152\n\22\3\22\3\22\5\22"+
		"\u0156\n\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\6\24\u0162"+
		"\n\24\r\24\16\24\u0163\3\24\3\24\5\24\u0168\n\24\3\25\3\25\3\25\7\25\u016d"+
		"\n\25\f\25\16\25\u0170\13\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\7\25\u0179"+
		"\n\25\f\25\16\25\u017c\13\25\3\25\3\25\3\25\5\25\u0181\n\25\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\6\27\u018b\n\27\r\27\16\27\u018c\3\27\3"+
		"\27\5\27\u0191\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\6\31\u019f\n\31\r\31\16\31\u01a0\3\31\3\31\5\31\u01a5\n\31"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\5\32\u01c3\n\32\3\33\3\33\3\33\3\33\3\33\3\33\6\33\u01cb\n\33\r\33\16"+
		"\33\u01cc\3\33\3\33\5\33\u01d1\n\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35"+
		"\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\5\36\u01ed\n\36\3\37\3\37\3\37\3 \3 \3 \6 \u01f5"+
		"\n \r \16 \u01f6\3 \3 \3!\3!\3!\6!\u01fe\n!\r!\16!\u01ff\3!\3!\3\"\3\""+
		"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%"+
		"\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3"+
		")\3)\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3-\3-\3-\3-\3"+
		"-\3-\3.\3.\3.\3.\3.\3.\5.\u0250\n.\3/\3/\3/\3\60\3\60\3\60\6\60\u0258"+
		"\n\60\r\60\16\60\u0259\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3"+
		"\61\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\5\63\u026e\n\63\3\64\3\64"+
		"\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67"+
		"\3\67\7\67\u0281\n\67\f\67\16\67\u0284\13\67\3\67\3\67\5\67\u0288\n\67"+
		"\38\38\38\38\38\38\38\38\38\38\38\68\u0295\n8\r8\168\u0296\38\38\38\3"+
		"8\38\38\38\38\58\u02a1\n8\39\39\39\59\u02a6\n9\3:\3:\3;\3;\3;\7;\u02ad"+
		"\n;\f;\16;\u02b0\13;\3;\3;\3<\3<\3=\3=\3=\5=\u02b9\n=\3>\7>\u02bc\n>\f"+
		">\16>\u02bf\13>\3?\7?\u02c2\n?\f?\16?\u02c5\13?\3@\6@\u02c8\n@\r@\16@"+
		"\u02c9\3@\3@\3@\3A\3A\3A\3A\3B\6B\u02d4\nB\rB\16B\u02d5\3B\3B\3B\3C\3"+
		"C\3D\3D\3D\3D\6D\u02e1\nD\rD\16D\u02e2\3D\3D\5D\u02e7\nD\3E\3E\3F\3F\3"+
		"F\5F\u02ee\nF\3G\3G\3G\7G\u02f3\nG\fG\16G\u02f6\13G\3G\3G\3H\3H\3I\3I"+
		"\3I\3I\3I\3I\3I\3I\3I\3I\3I\5I\u0307\nI\3I\5I\u030a\nI\3I\5I\u030d\nI"+
		"\3I\3I\5I\u0311\nI\3I\5I\u0314\nI\3I\5I\u0317\nI\3I\3I\3J\3J\3J\3J\3J"+
		"\3K\3K\3K\7K\u0323\nK\fK\16K\u0326\13K\3K\3K\3L\3L\5L\u032c\nL\3M\3M\3"+
		"M\3M\3M\3N\3N\3N\3N\3N\3O\3O\3O\3O\3O\3O\3O\5O\u033f\nO\3O\3O\3P\3P\3"+
		"P\3P\3P\3P\3Q\3Q\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\6R\u0355\nR\rR\16R\u0356"+
		"\3R\3R\3R\3R\3R\5R\u035e\nR\3R\3R\3R\3R\3R\3R\3R\7R\u0367\nR\fR\16R\u036a"+
		"\13R\3R\3R\3R\3R\5R\u0370\nR\3S\3S\3S\3S\3S\3S\2\2T\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhj"+
		"lnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092"+
		"\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\2\13\3\2\23\26"+
		"\3\2\27\30\3\2\32\33\3\2\37\"\3\2\60\64\3\2\66\67\3\2HI\3\2@A\3\2CD\u038f"+
		"\2\u00a8\3\2\2\2\4\u00aa\3\2\2\2\6\u00d3\3\2\2\2\b\u00d5\3\2\2\2\n\u00db"+
		"\3\2\2\2\f\u00df\3\2\2\2\16\u00ef\3\2\2\2\20\u00f2\3\2\2\2\22\u00f6\3"+
		"\2\2\2\24\u00fb\3\2\2\2\26\u0104\3\2\2\2\30\u0109\3\2\2\2\32\u0117\3\2"+
		"\2\2\34\u011b\3\2\2\2\36\u012a\3\2\2\2 \u012c\3\2\2\2\"\u0149\3\2\2\2"+
		"$\u0159\3\2\2\2&\u0167\3\2\2\2(\u0180\3\2\2\2*\u0182\3\2\2\2,\u0190\3"+
		"\2\2\2.\u0192\3\2\2\2\60\u01a4\3\2\2\2\62\u01c2\3\2\2\2\64\u01d0\3\2\2"+
		"\2\66\u01d2\3\2\2\28\u01d8\3\2\2\2:\u01ec\3\2\2\2<\u01ee\3\2\2\2>\u01f1"+
		"\3\2\2\2@\u01fa\3\2\2\2B\u0203\3\2\2\2D\u0208\3\2\2\2F\u020e\3\2\2\2H"+
		"\u0216\3\2\2\2J\u021e\3\2\2\2L\u0223\3\2\2\2N\u0228\3\2\2\2P\u022d\3\2"+
		"\2\2R\u0232\3\2\2\2T\u0237\3\2\2\2V\u023d\3\2\2\2X\u0243\3\2\2\2Z\u024f"+
		"\3\2\2\2\\\u0251\3\2\2\2^\u0254\3\2\2\2`\u025d\3\2\2\2b\u0265\3\2\2\2"+
		"d\u026d\3\2\2\2f\u026f\3\2\2\2h\u0274\3\2\2\2j\u027a\3\2\2\2l\u0287\3"+
		"\2\2\2n\u02a0\3\2\2\2p\u02a5\3\2\2\2r\u02a7\3\2\2\2t\u02a9\3\2\2\2v\u02b3"+
		"\3\2\2\2x\u02b8\3\2\2\2z\u02bd\3\2\2\2|\u02c3\3\2\2\2~\u02c7\3\2\2\2\u0080"+
		"\u02ce\3\2\2\2\u0082\u02d3\3\2\2\2\u0084\u02da\3\2\2\2\u0086\u02e6\3\2"+
		"\2\2\u0088\u02e8\3\2\2\2\u008a\u02ed\3\2\2\2\u008c\u02ef\3\2\2\2\u008e"+
		"\u02f9\3\2\2\2\u0090\u02fb\3\2\2\2\u0092\u031a\3\2\2\2\u0094\u031f\3\2"+
		"\2\2\u0096\u032b\3\2\2\2\u0098\u032d\3\2\2\2\u009a\u0332\3\2\2\2\u009c"+
		"\u0337\3\2\2\2\u009e\u0342\3\2\2\2\u00a0\u0348\3\2\2\2\u00a2\u036f\3\2"+
		"\2\2\u00a4\u0371\3\2\2\2\u00a6\u00a9\5\4\3\2\u00a7\u00a9\5\u0090I\2\u00a8"+
		"\u00a6\3\2\2\2\u00a8\u00a7\3\2\2\2\u00a9\3\3\2\2\2\u00aa\u00ab\7\3\2\2"+
		"\u00ab\u00ac\7\4\2\2\u00ac\u00ad\7\3\2\2\u00ad\u00ae\7\5\2\2\u00ae\u00af"+
		"\5\6\4\2\u00af\u00b1\7\6\2\2\u00b0\u00b2\5\b\5\2\u00b1\u00b0\3\2\2\2\u00b1"+
		"\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3\u00b5\5\f\7\2\u00b4\u00b3\3\2"+
		"\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7\3\2\2\2\u00b6\u00b8\5\22\n\2\u00b7"+
		"\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00ba\3\2\2\2\u00b9\u00bb\5\24"+
		"\13\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc"+
		"\u00be\5\30\r\2\u00bd\u00bc\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00c2\3"+
		"\2\2\2\u00bf\u00c1\5\32\16\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2"+
		"\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c8\3\2\2\2\u00c4\u00c2\3\2"+
		"\2\2\u00c5\u00c7\5 \21\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8"+
		"\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00ce\3\2\2\2\u00ca\u00c8\3\2"+
		"\2\2\u00cb\u00cd\58\35\2\u00cc\u00cb\3\2\2\2\u00cd\u00d0\3\2\2\2\u00ce"+
		"\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d1\3\2\2\2\u00d0\u00ce\3\2"+
		"\2\2\u00d1\u00d2\7\6\2\2\u00d2\5\3\2\2\2\u00d3\u00d4\7I\2\2\u00d4\7\3"+
		"\2\2\2\u00d5\u00d6\7\3\2\2\u00d6\u00d7\7\7\2\2\u00d7\u00d8\5\n\6\2\u00d8"+
		"\u00d9\7\6\2\2\u00d9\t\3\2\2\2\u00da\u00dc\7G\2\2\u00db\u00da\3\2\2\2"+
		"\u00dc\u00dd\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\13"+
		"\3\2\2\2\u00df\u00e0\7\3\2\2\u00e0\u00e1\7\b\2\2\u00e1\u00e2\5\16\b\2"+
		"\u00e2\u00e3\7\6\2\2\u00e3\r\3\2\2\2\u00e4\u00e6\7I\2\2\u00e5\u00e4\3"+
		"\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8"+
		"\u00f0\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\5\20\t\2\u00eb\u00ec\7"+
		"\t\2\2\u00ec\u00ed\5\u0086D\2\u00ed\u00ee\5\16\b\2\u00ee\u00f0\3\2\2\2"+
		"\u00ef\u00e7\3\2\2\2\u00ef\u00ea\3\2\2\2\u00f0\17\3\2\2\2\u00f1\u00f3"+
		"\7I\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4"+
		"\u00f5\3\2\2\2\u00f5\21\3\2\2\2\u00f6\u00f7\7\3\2\2\u00f7\u00f8\7\n\2"+
		"\2\u00f8\u00f9\5|?\2\u00f9\u00fa\7\6\2\2\u00fa\23\3\2\2\2\u00fb\u00fc"+
		"\7\3\2\2\u00fc\u00fe\7\13\2\2\u00fd\u00ff\5\26\f\2\u00fe\u00fd\3\2\2\2"+
		"\u00ff\u0100\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0102"+
		"\3\2\2\2\u0102\u0103\7\6\2\2\u0103\25\3\2\2\2\u0104\u0105\7\3\2\2\u0105"+
		"\u0106\5v<\2\u0106\u0107\5z>\2\u0107\u0108\7\6\2\2\u0108\27\3\2\2\2\u0109"+
		"\u010a\7\3\2\2\u010a\u0111\7\f\2\2\u010b\u010f\5\26\f\2\u010c\u010d\7"+
		"\t\2\2\u010d\u0110\7\r\2\2\u010e\u0110\5\u0086D\2\u010f\u010c\3\2\2\2"+
		"\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0112\3\2\2\2\u0111\u010b"+
		"\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\u0116\7\6\2\2\u0116\31\3\2\2\2\u0117\u0118\7\3\2"+
		"\2\u0118\u0119\7\16\2\2\u0119\u011a\5\34\17\2\u011a\33\3\2\2\2\u011b\u011c"+
		"\5\36\20\2\u011c\u011d\7\17\2\2\u011d\u011e\7\3\2\2\u011e\u011f\5z>\2"+
		"\u011f\u0122\7\6\2\2\u0120\u0121\7\20\2\2\u0121\u0123\5:\36\2\u0122\u0120"+
		"\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0126\3\2\2\2\u0124\u0125\7\21\2\2"+
		"\u0125\u0127\5Z.\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128"+
		"\3\2\2\2\u0128\u0129\7\6\2\2\u0129\35\3\2\2\2\u012a\u012b\7I\2\2\u012b"+
		"\37\3\2\2\2\u012c\u012d\7\3\2\2\u012d\u012e\7\22\2\2\u012e\u012f\5$\23"+
		"\2\u012f\u0130\7\17\2\2\u0130\u0131\7\3\2\2\u0131\u0132\5z>\2\u0132\u0133"+
		"\7\6\2\2\u0133\u0134\7\16\2\2\u0134\u0135\7\3\2\2\u0135\u0139\5\36\20"+
		"\2\u0136\u0138\5\u0088E\2\u0137\u0136\3\2\2\2\u0138\u013b\3\2\2\2\u0139"+
		"\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013c\3\2\2\2\u013b\u0139\3\2"+
		"\2\2\u013c\u013f\7\6\2\2\u013d\u013e\7\20\2\2\u013e\u0140\5:\36\2\u013f"+
		"\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u0142\7\21"+
		"\2\2\u0142\u0144\5Z.\2\u0143\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145"+
		"\3\2\2\2\u0145\u0146\5\"\22\2\u0146!\3\2\2\2\u0147\u0148\t\2\2\2\u0148"+
		"\u014a\5&\24\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014d\3\2"+
		"\2\2\u014b\u014c\t\3\2\2\u014c\u014e\5,\27\2\u014d\u014b\3\2\2\2\u014d"+
		"\u014e\3\2\2\2\u014e\u0151\3\2\2\2\u014f\u0150\7\31\2\2\u0150\u0152\5"+
		"\60\31\2\u0151\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0155\3\2\2\2\u0153"+
		"\u0154\t\4\2\2\u0154\u0156\5\64\33\2\u0155\u0153\3\2\2\2\u0155\u0156\3"+
		"\2\2\2\u0156\u0157\3\2\2\2\u0157\u0158\7\6\2\2\u0158#\3\2\2\2\u0159\u015a"+
		"\7I\2\2\u015a%\3\2\2\2\u015b\u015c\7\3\2\2\u015c\u0168\7\6\2\2\u015d\u0168"+
		"\5(\25\2\u015e\u015f\7\3\2\2\u015f\u0161\7\34\2\2\u0160\u0162\5(\25\2"+
		"\u0161\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164"+
		"\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0166\7\6\2\2\u0166\u0168\3\2\2\2\u0167"+
		"\u015b\3\2\2\2\u0167\u015d\3\2\2\2\u0167\u015e\3\2\2\2\u0168\'\3\2\2\2"+
		"\u0169\u016a\7\3\2\2\u016a\u016e\5\36\20\2\u016b\u016d\5\u0088E\2\u016c"+
		"\u016b\3\2\2\2\u016d\u0170\3\2\2\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2"+
		"\2\2\u016f\u0171\3\2\2\2\u0170\u016e\3\2\2\2\u0171\u0172\7\6\2\2\u0172"+
		"\u0181\3\2\2\2\u0173\u0174\7\3\2\2\u0174\u0175\5*\26\2\u0175\u0176\7\3"+
		"\2\2\u0176\u017a\5\36\20\2\u0177\u0179\5\u0088E\2\u0178\u0177\3\2\2\2"+
		"\u0179\u017c\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u017d"+
		"\3\2\2\2\u017c\u017a\3\2\2\2\u017d\u017e\7\6\2\2\u017e\u017f\7\6\2\2\u017f"+
		"\u0181\3\2\2\2\u0180\u0169\3\2\2\2\u0180\u0173\3\2\2\2\u0181)\3\2\2\2"+
		"\u0182\u0183\7I\2\2\u0183+\3\2\2\2\u0184\u0185\7\3\2\2\u0185\u0191\7\6"+
		"\2\2\u0186\u0191\5.\30\2\u0187\u0188\7\3\2\2\u0188\u018a\7\34\2\2\u0189"+
		"\u018b\5.\30\2\u018a\u0189\3\2\2\2\u018b\u018c\3\2\2\2\u018c\u018a\3\2"+
		"\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018f\7\6\2\2\u018f"+
		"\u0191\3\2\2\2\u0190\u0184\3\2\2\2\u0190\u0186\3\2\2\2\u0190\u0187\3\2"+
		"\2\2\u0191-\3\2\2\2\u0192\u0193\7\3\2\2\u0193\u0194\5*\26\2\u0194\u0195"+
		"\7\35\2\2\u0195\u0196\5*\26\2\u0196\u0197\7\6\2\2\u0197/\3\2\2\2\u0198"+
		"\u0199\7\3\2\2\u0199\u01a5\7\6\2\2\u019a\u01a5\5\62\32\2\u019b\u019c\7"+
		"\3\2\2\u019c\u019e\7\34\2\2\u019d\u019f\5\62\32\2\u019e\u019d\3\2\2\2"+
		"\u019f\u01a0\3\2\2\2\u01a0\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a2"+
		"\3\2\2\2\u01a2\u01a3\7\6\2\2\u01a3\u01a5\3\2\2\2\u01a4\u0198\3\2\2\2\u01a4"+
		"\u019a\3\2\2\2\u01a4\u019b\3\2\2\2\u01a5\61\3\2\2\2\u01a6\u01a7\7\3\2"+
		"\2\u01a7\u01c3\7\6\2\2\u01a8\u01a9\7\3\2\2\u01a9\u01aa\7\36\2\2\u01aa"+
		"\u01ab\5x=\2\u01ab\u01ac\5\u0088E\2\u01ac\u01ad\5\u0088E\2\u01ad\u01ae"+
		"\7\6\2\2\u01ae\u01af\7\6\2\2\u01af\u01c3\3\2\2\2\u01b0\u01b1\5x=\2\u01b1"+
		"\u01b2\5\u0088E\2\u01b2\u01b3\5\u0088E\2\u01b3\u01b4\7\6\2\2\u01b4\u01c3"+
		"\3\2\2\2\u01b5\u01b6\7\3\2\2\u01b6\u01b7\t\5\2\2\u01b7\u01b8\5\u0080A"+
		"\2\u01b8\u01b9\7\6\2\2\u01b9\u01c3\3\2\2\2\u01ba\u01bb\7\3\2\2\u01bb\u01bc"+
		"\7\36\2\2\u01bc\u01bd\7\3\2\2\u01bd\u01be\t\5\2\2\u01be\u01bf\5\u0080"+
		"A\2\u01bf\u01c0\7\6\2\2\u01c0\u01c1\7\6\2\2\u01c1\u01c3\3\2\2\2\u01c2"+
		"\u01a6\3\2\2\2\u01c2\u01a8\3\2\2\2\u01c2\u01b0\3\2\2\2\u01c2\u01b5\3\2"+
		"\2\2\u01c2\u01ba\3\2\2\2\u01c3\63\3\2\2\2\u01c4\u01c5\7\3\2\2\u01c5\u01d1"+
		"\7\6\2\2\u01c6\u01d1\5\66\34\2\u01c7\u01c8\7\3\2\2\u01c8\u01ca\7\34\2"+
		"\2\u01c9\u01cb\5\66\34\2\u01ca\u01c9\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc"+
		"\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01cf\7\6"+
		"\2\2\u01cf\u01d1\3\2\2\2\u01d0\u01c4\3\2\2\2\u01d0\u01c6\3\2\2\2\u01d0"+
		"\u01c7\3\2\2\2\u01d1\65\3\2\2\2\u01d2\u01d3\7\3\2\2\u01d3\u01d4\5*\26"+
		"\2\u01d4\u01d5\5d\63\2\u01d5\u01d6\5*\26\2\u01d6\u01d7\7\6\2\2\u01d7\67"+
		"\3\2\2\2\u01d8\u01d9\7\3\2\2\u01d9\u01da\7#\2\2\u01da\u01db\5\34\17\2"+
		"\u01db9\3\2\2\2\u01dc\u01ed\5<\37\2\u01dd\u01ed\5t;\2\u01de\u01ed\5B\""+
		"\2\u01df\u01ed\5D#\2\u01e0\u01ed\5> \2\u01e1\u01ed\5@!\2\u01e2\u01ed\5"+
		"F$\2\u01e3\u01ed\5H%\2\u01e4\u01ed\5J&\2\u01e5\u01ed\5L\'\2\u01e6\u01ed"+
		"\5N(\2\u01e7\u01ed\5P)\2\u01e8\u01ed\5R*\2\u01e9\u01ed\5T+\2\u01ea\u01ed"+
		"\5V,\2\u01eb\u01ed\5X-\2\u01ec\u01dc\3\2\2\2\u01ec\u01dd\3\2\2\2\u01ec"+
		"\u01de\3\2\2\2\u01ec\u01df\3\2\2\2\u01ec\u01e0\3\2\2\2\u01ec\u01e1\3\2"+
		"\2\2\u01ec\u01e2\3\2\2\2\u01ec\u01e3\3\2\2\2\u01ec\u01e4\3\2\2\2\u01ec"+
		"\u01e5\3\2\2\2\u01ec\u01e6\3\2\2\2\u01ec\u01e7\3\2\2\2\u01ec\u01e8\3\2"+
		"\2\2\u01ec\u01e9\3\2\2\2\u01ec\u01ea\3\2\2\2\u01ec\u01eb\3\2\2\2\u01ed"+
		";\3\2\2\2\u01ee\u01ef\7\3\2\2\u01ef\u01f0\7\6\2\2\u01f0=\3\2\2\2\u01f1"+
		"\u01f2\7\3\2\2\u01f2\u01f4\7\34\2\2\u01f3\u01f5\5:\36\2\u01f4\u01f3\3"+
		"\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f4\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7"+
		"\u01f8\3\2\2\2\u01f8\u01f9\7\6\2\2\u01f9?\3\2\2\2\u01fa\u01fb\7\3\2\2"+
		"\u01fb\u01fd\7$\2\2\u01fc\u01fe\5:\36\2\u01fd\u01fc\3\2\2\2\u01fe\u01ff"+
		"\3\2\2\2\u01ff\u01fd\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u0201\3\2\2\2\u0201"+
		"\u0202\7\6\2\2\u0202A\3\2\2\2\u0203\u0204\7\3\2\2\u0204\u0205\7\36\2\2"+
		"\u0205\u0206\5:\36\2\u0206\u0207\7\6\2\2\u0207C\3\2\2\2\u0208\u0209\7"+
		"\3\2\2\u0209\u020a\7%\2\2\u020a\u020b\5:\36\2\u020b\u020c\5:\36\2\u020c"+
		"\u020d\7\6\2\2\u020dE\3\2\2\2\u020e\u020f\7\3\2\2\u020f\u0210\7&\2\2\u0210"+
		"\u0211\7\3\2\2\u0211\u0212\5z>\2\u0212\u0213\7\6\2\2\u0213\u0214\5:\36"+
		"\2\u0214\u0215\7\6\2\2\u0215G\3\2\2\2\u0216\u0217\7\3\2\2\u0217\u0218"+
		"\7\'\2\2\u0218\u0219\7\3\2\2\u0219\u021a\5z>\2\u021a\u021b\7\6\2\2\u021b"+
		"\u021c\5:\36\2\u021c\u021d\7\6\2\2\u021dI\3\2\2\2\u021e\u021f\5x=\2\u021f"+
		"\u0220\5\u0088E\2\u0220\u0221\5\u0088E\2\u0221\u0222\7\6\2\2\u0222K\3"+
		"\2\2\2\u0223\u0224\7\3\2\2\u0224\u0225\7(\2\2\u0225\u0226\5:\36\2\u0226"+
		"\u0227\7\6\2\2\u0227M\3\2\2\2\u0228\u0229\7\3\2\2\u0229\u022a\7)\2\2\u022a"+
		"\u022b\5:\36\2\u022b\u022c\7\6\2\2\u022cO\3\2\2\2\u022d\u022e\7\3\2\2"+
		"\u022e\u022f\7*\2\2\u022f\u0230\5:\36\2\u0230\u0231\7\6\2\2\u0231Q\3\2"+
		"\2\2\u0232\u0233\7\3\2\2\u0233\u0234\7+\2\2\u0234\u0235\5:\36\2\u0235"+
		"\u0236\7\6\2\2\u0236S\3\2\2\2\u0237\u0238\7\3\2\2\u0238\u0239\7,\2\2\u0239"+
		"\u023a\5:\36\2\u023a\u023b\5:\36\2\u023b\u023c\7\6\2\2\u023cU\3\2\2\2"+
		"\u023d\u023e\7\3\2\2\u023e\u023f\7-\2\2\u023f\u0240\5:\36\2\u0240\u0241"+
		"\5:\36\2\u0241\u0242\7\6\2\2\u0242W\3\2\2\2\u0243\u0244\7\3\2\2\u0244"+
		"\u0245\7.\2\2\u0245\u0246\7I\2\2\u0246\u0247\5:\36\2\u0247\u0248\7\6\2"+
		"\2\u0248Y\3\2\2\2\u0249\u0250\5\\/\2\u024a\u0250\5^\60\2\u024b\u0250\5"+
		"`\61\2\u024c\u0250\5b\62\2\u024d\u0250\5d\63\2\u024e\u0250\5h\65\2\u024f"+
		"\u0249\3\2\2\2\u024f\u024a\3\2\2\2\u024f\u024b\3\2\2\2\u024f\u024c\3\2"+
		"\2\2\u024f\u024d\3\2\2\2\u024f\u024e\3\2\2\2\u0250[\3\2\2\2\u0251\u0252"+
		"\7\3\2\2\u0252\u0253\7\6\2\2\u0253]\3\2\2\2\u0254\u0255\7\3\2\2\u0255"+
		"\u0257\7\34\2\2\u0256\u0258\5Z.\2\u0257\u0256\3\2\2\2\u0258\u0259\3\2"+
		"\2\2\u0259\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u025b\3\2\2\2\u025b"+
		"\u025c\7\6\2\2\u025c_\3\2\2\2\u025d\u025e\7\3\2\2\u025e\u025f\7\'\2\2"+
		"\u025f\u0260\7\3\2\2\u0260\u0261\5z>\2\u0261\u0262\7\6\2\2\u0262\u0263"+
		"\5Z.\2\u0263\u0264\7\6\2\2\u0264a\3\2\2\2\u0265\u0266\7\3\2\2\u0266\u0267"+
		"\7/\2\2\u0267\u0268\5:\36\2\u0268\u0269\5Z.\2\u0269\u026a\7\6\2\2\u026a"+
		"c\3\2\2\2\u026b\u026e\5f\64\2\u026c\u026e\5t;\2\u026d\u026b\3\2\2\2\u026d"+
		"\u026c\3\2\2\2\u026ee\3\2\2\2\u026f\u0270\7\3\2\2\u0270\u0271\7\36\2\2"+
		"\u0271\u0272\5t;\2\u0272\u0273\7\6\2\2\u0273g\3\2\2\2\u0274\u0275\7\3"+
		"\2\2\u0275\u0276\5j\66\2\u0276\u0277\5l\67\2\u0277\u0278\5n8\2\u0278\u0279"+
		"\7\6\2\2\u0279i\3\2\2\2\u027a\u027b\t\6\2\2\u027bk\3\2\2\2\u027c\u0288"+
		"\5\u008eH\2\u027d\u027e\7\3\2\2\u027e\u0282\5\u008eH\2\u027f\u0281\5\u008a"+
		"F\2\u0280\u027f\3\2\2\2\u0281\u0284\3\2\2\2\u0282\u0280\3\2\2\2\u0282"+
		"\u0283\3\2\2\2\u0283\u0285\3\2\2\2\u0284\u0282\3\2\2\2\u0285\u0286\7\6"+
		"\2\2\u0286\u0288\3\2\2\2\u0287\u027c\3\2\2\2\u0287\u027d\3\2\2\2\u0288"+
		"m\3\2\2\2\u0289\u02a1\7L\2\2\u028a\u028b\7\3\2\2\u028b\u028c\5p9\2\u028c"+
		"\u028d\5n8\2\u028d\u028e\5n8\2\u028e\u028f\7\6\2\2\u028f\u02a1\3\2\2\2"+
		"\u0290\u0291\7\3\2\2\u0291\u0292\5r:\2\u0292\u0294\5n8\2\u0293\u0295\5"+
		"n8\2\u0294\u0293\3\2\2\2\u0295\u0296\3\2\2\2\u0296\u0294\3\2\2\2\u0296"+
		"\u0297\3\2\2\2\u0297\u0298\3\2\2\2\u0298\u0299\7\6\2\2\u0299\u02a1\3\2"+
		"\2\2\u029a\u029b\7\3\2\2\u029b\u029c\7\t\2\2\u029c\u029d\5n8\2\u029d\u029e"+
		"\7\6\2\2\u029e\u02a1\3\2\2\2\u029f\u02a1\5l\67\2\u02a0\u0289\3\2\2\2\u02a0"+
		"\u028a\3\2\2\2\u02a0\u0290\3\2\2\2\u02a0\u029a\3\2\2\2\u02a0\u029f\3\2"+
		"\2\2\u02a1o\3\2\2\2\u02a2\u02a6\5r:\2\u02a3\u02a6\7\t\2\2\u02a4\u02a6"+
		"\7\65\2\2\u02a5\u02a2\3\2\2\2\u02a5\u02a3\3\2\2\2\u02a5\u02a4\3\2\2\2"+
		"\u02a6q\3\2\2\2\u02a7\u02a8\t\7\2\2\u02a8s\3\2\2\2\u02a9\u02aa\7\3\2\2"+
		"\u02aa\u02ae\5v<\2\u02ab\u02ad\5\u0088E\2\u02ac\u02ab\3\2\2\2\u02ad\u02b0"+
		"\3\2\2\2\u02ae\u02ac\3\2\2\2\u02ae\u02af\3\2\2\2\u02af\u02b1\3\2\2\2\u02b0"+
		"\u02ae\3\2\2\2\u02b1\u02b2\7\6\2\2\u02b2u\3\2\2\2\u02b3\u02b4\7I\2\2\u02b4"+
		"w\3\2\2\2\u02b5\u02b6\7\3\2\2\u02b6\u02b9\78\2\2\u02b7\u02b9\79\2\2\u02b8"+
		"\u02b5\3\2\2\2\u02b8\u02b7\3\2\2\2\u02b9y\3\2\2\2\u02ba\u02bc\5~@\2\u02bb"+
		"\u02ba\3\2\2\2\u02bc\u02bf\3\2\2\2\u02bd\u02bb\3\2\2\2\u02bd\u02be\3\2"+
		"\2\2\u02be{\3\2\2\2\u02bf\u02bd\3\2\2\2\u02c0\u02c2\5\u0082B\2\u02c1\u02c0"+
		"\3\2\2\2\u02c2\u02c5\3\2\2\2\u02c3\u02c1\3\2\2\2\u02c3\u02c4\3\2\2\2\u02c4"+
		"}\3\2\2\2\u02c5\u02c3\3\2\2\2\u02c6\u02c8\7H\2\2\u02c7\u02c6\3\2\2\2\u02c8"+
		"\u02c9\3\2\2\2\u02c9\u02c7\3\2\2\2\u02c9\u02ca\3\2\2\2\u02ca\u02cb\3\2"+
		"\2\2\u02cb\u02cc\7\t\2\2\u02cc\u02cd\5\u0086D\2\u02cd\177\3\2\2\2\u02ce"+
		"\u02cf\7H\2\2\u02cf\u02d0\7\t\2\2\u02d0\u02d1\5\u0086D\2\u02d1\u0081\3"+
		"\2\2\2\u02d2\u02d4\5\u0084C\2\u02d3\u02d2\3\2\2\2\u02d4\u02d5\3\2\2\2"+
		"\u02d5\u02d3\3\2\2\2\u02d5\u02d6\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02d8"+
		"\7\t\2\2\u02d8\u02d9\5\u0086D\2\u02d9\u0083\3\2\2\2\u02da\u02db\7I\2\2"+
		"\u02db\u0085\3\2\2\2\u02dc\u02e7\7I\2\2\u02dd\u02de\7\3\2\2\u02de\u02e0"+
		"\7:\2\2\u02df\u02e1\5\u0086D\2\u02e0\u02df\3\2\2\2\u02e1\u02e2\3\2\2\2"+
		"\u02e2\u02e0\3\2\2\2\u02e2\u02e3\3\2\2\2\u02e3\u02e4\3\2\2\2\u02e4\u02e5"+
		"\7\6\2\2\u02e5\u02e7\3\2\2\2\u02e6\u02dc\3\2\2\2\u02e6\u02dd\3\2\2\2\u02e7"+
		"\u0087\3\2\2\2\u02e8\u02e9\t\b\2\2\u02e9\u0089\3\2\2\2\u02ea\u02ee\7I"+
		"\2\2\u02eb\u02ee\7H\2\2\u02ec\u02ee\5\u008cG\2\u02ed\u02ea\3\2\2\2\u02ed"+
		"\u02eb\3\2\2\2\u02ed\u02ec\3\2\2\2\u02ee\u008b\3\2\2\2\u02ef\u02f0\7\3"+
		"\2\2\u02f0\u02f4\5\u008eH\2\u02f1\u02f3\5\u008aF\2\u02f2\u02f1\3\2\2\2"+
		"\u02f3\u02f6\3\2\2\2\u02f4\u02f2\3\2\2\2\u02f4\u02f5\3\2\2\2\u02f5\u02f7"+
		"\3\2\2\2\u02f6\u02f4\3\2\2\2\u02f7\u02f8\7\6\2\2\u02f8\u008d\3\2\2\2\u02f9"+
		"\u02fa\7I\2\2\u02fa\u008f\3\2\2\2\u02fb\u02fc\7\3\2\2\u02fc\u02fd\7\4"+
		"\2\2\u02fd\u02fe\7\3\2\2\u02fe\u02ff\7;\2\2\u02ff\u0300\7I\2\2\u0300\u0301"+
		"\7\6\2\2\u0301\u0302\7\3\2\2\u0302\u0303\7<\2\2\u0303\u0304\7I\2\2\u0304"+
		"\u0306\7\6\2\2\u0305\u0307\5\b\5\2\u0306\u0305\3\2\2\2\u0306\u0307\3\2"+
		"\2\2\u0307\u0309\3\2\2\2\u0308\u030a\5\u0092J\2\u0309\u0308\3\2\2\2\u0309"+
		"\u030a\3\2\2\2\u030a\u030c\3\2\2\2\u030b\u030d\5\u009cO\2\u030c\u030b"+
		"\3\2\2\2\u030c\u030d\3\2\2\2\u030d\u030e\3\2\2\2\u030e\u0310\5\u0094K"+
		"\2\u030f\u0311\5\u009aN\2\u0310\u030f\3\2\2\2\u0310\u0311\3\2\2\2\u0311"+
		"\u0313\3\2\2\2\u0312\u0314\5\u00a4S\2\u0313\u0312\3\2\2\2\u0313\u0314"+
		"\3\2\2\2\u0314\u0316\3\2\2\2\u0315\u0317\5\u009eP\2\u0316\u0315\3\2\2"+
		"\2\u0316\u0317\3\2\2\2\u0317\u0318\3\2\2\2\u0318\u0319\7\6\2\2\u0319\u0091"+
		"\3\2\2\2\u031a\u031b\7\3\2\2\u031b\u031c\7=\2\2\u031c\u031d\5|?\2\u031d"+
		"\u031e\7\6\2\2\u031e\u0093\3\2\2\2\u031f\u0320\7\3\2\2\u0320\u0324\7>"+
		"\2\2\u0321\u0323\5\u0096L\2\u0322\u0321\3\2\2\2\u0323\u0326\3\2\2\2\u0324"+
		"\u0322\3\2\2\2\u0324\u0325\3\2\2\2\u0325\u0327\3\2\2\2\u0326\u0324\3\2"+
		"\2\2\u0327\u0328\7\6\2\2\u0328\u0095\3\2\2\2\u0329\u032c\5d\63\2\u032a"+
		"\u032c\5\u0098M\2\u032b\u0329\3\2\2\2\u032b\u032a\3\2\2\2\u032c\u0097"+
		"\3\2\2\2\u032d\u032e\5x=\2\u032e\u032f\5l\67\2\u032f\u0330\7L\2\2\u0330"+
		"\u0331\7\6\2\2\u0331\u0099\3\2\2\2\u0332\u0333\7\3\2\2\u0333\u0334\7?"+
		"\2\2\u0334\u0335\5:\36\2\u0335\u0336\7\6\2\2\u0336\u009b\3\2\2\2\u0337"+
		"\u0338\7\3\2\2\u0338\u033e\t\t\2\2\u0339\u033a\7\17\2\2\u033a\u033b\7"+
		"\3\2\2\u033b\u033c\5z>\2\u033c\u033d\7\6\2\2\u033d\u033f\3\2\2\2\u033e"+
		"\u0339\3\2\2\2\u033e\u033f\3\2\2\2\u033f\u0340\3\2\2\2\u0340\u0341\5\""+
		"\22\2\u0341\u009d\3\2\2\2\u0342\u0343\7\3\2\2\u0343\u0344\7B\2\2\u0344"+
		"\u0345\5\u00a0Q\2\u0345\u0346\5\u00a2R\2\u0346\u0347\7\6\2\2\u0347\u009f"+
		"\3\2\2\2\u0348\u0349\t\n\2\2\u0349\u00a1\3\2\2\2\u034a\u034b\7\3\2\2\u034b"+
		"\u034c\5p9\2\u034c\u034d\5\u00a2R\2\u034d\u034e\5\u00a2R\2\u034e\u034f"+
		"\7\6\2\2\u034f\u0370\3\2\2\2\u0350\u0351\7\3\2\2\u0351\u0352\5r:\2\u0352"+
		"\u0354\5\u00a2R\2\u0353\u0355\5\u00a2R\2\u0354\u0353\3\2\2\2\u0355\u0356"+
		"\3\2\2\2\u0356\u0354\3\2\2\2\u0356\u0357\3\2\2\2\u0357\u0358\3\2\2\2\u0358"+
		"\u0359\7\6\2\2\u0359\u0370\3\2\2\2\u035a\u035b\7\3\2\2\u035b\u035e\7\t"+
		"\2\2\u035c\u035e\7E\2\2\u035d\u035a\3\2\2\2\u035d\u035c\3\2\2\2\u035e"+
		"\u035f\3\2\2\2\u035f\u0360\5\u00a2R\2\u0360\u0361\7\6\2\2\u0361\u0370"+
		"\3\2\2\2\u0362\u0370\7L\2\2\u0363\u0364\7\3\2\2\u0364\u0368\5\u008eH\2"+
		"\u0365\u0367\7I\2\2\u0366\u0365\3\2\2\2\u0367\u036a\3\2\2\2\u0368\u0366"+
		"\3\2\2\2\u0368\u0369\3\2\2\2\u0369\u036b\3\2\2\2\u036a\u0368\3\2\2\2\u036b"+
		"\u036c\7\6\2\2\u036c\u0370\3\2\2\2\u036d\u0370\7F\2\2\u036e\u0370\5\u008e"+
		"H\2\u036f\u034a\3\2\2\2\u036f\u0350\3\2\2\2\u036f\u035d\3\2\2\2\u036f"+
		"\u0362\3\2\2\2\u036f\u0363\3\2\2\2\u036f\u036d\3\2\2\2\u036f\u036e\3\2"+
		"\2\2\u0370\u00a3\3\2\2\2\u0371\u0372\7\3\2\2\u0372\u0373\7\31\2\2\u0373"+
		"\u0374\5:\36\2\u0374\u0375\7\6\2\2\u0375\u00a5\3\2\2\2I\u00a8\u00b1\u00b4"+
		"\u00b7\u00ba\u00bd\u00c2\u00c8\u00ce\u00dd\u00e7\u00ef\u00f4\u0100\u010f"+
		"\u0113\u0122\u0126\u0139\u013f\u0143\u0149\u014d\u0151\u0155\u0163\u0167"+
		"\u016e\u017a\u0180\u018c\u0190\u01a0\u01a4\u01c2\u01cc\u01d0\u01ec\u01f6"+
		"\u01ff\u024f\u0259\u026d\u0282\u0287\u0296\u02a0\u02a5\u02ae\u02b8\u02bd"+
		"\u02c3\u02c9\u02d5\u02e2\u02e6\u02ed\u02f4\u0306\u0309\u030c\u0310\u0313"+
		"\u0316\u0324\u032b\u033e\u0356\u035d\u0368\u036f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}