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
		T__52=53, T__53=54, T__54=55, T__55=56, REQUIRE_NAME=57, VAR_NAME=58, 
		NAME=59, COMMENT=60, WS=61, NUMBER=62;
	public static final int
		RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3, 
		RULE_require_defs = 4, RULE_type_def = 5, RULE_one_def = 6, RULE_new_types = 7, 
		RULE_const_def = 8, RULE_predicates_def = 9, RULE_atomic_formula_skeleton = 10, 
		RULE_funtions_def = 11, RULE_comp_task_def = 12, RULE_task_def = 13, RULE_task_symbol = 14, 
		RULE_method_def = 15, RULE_tasknetwork_def = 16, RULE_method_symbol = 17, 
		RULE_subtask_defs = 18, RULE_subtask_def = 19, RULE_subtask_id = 20, RULE_ordering_defs = 21, 
		RULE_ordering_def = 22, RULE_constraint_defs = 23, RULE_constraint_def = 24, 
		RULE_causallink_defs = 25, RULE_causallink_def = 26, RULE_action_def = 27, 
		RULE_gd = 28, RULE_gd_empty = 29, RULE_gd_conjuction = 30, RULE_gd_disjuction = 31, 
		RULE_gd_negation = 32, RULE_gd_existential = 33, RULE_gd_univeral = 34, 
		RULE_gd_equality_constraint = 35, RULE_effect_body = 36, RULE_eff_conjuntion = 37, 
		RULE_eff_empty = 38, RULE_c_effect = 39, RULE_forall_effect = 40, RULE_conditional_effect = 41, 
		RULE_literal = 42, RULE_neg_atomic_formula = 43, RULE_cond_effect = 44, 
		RULE_p_effect = 45, RULE_assign_op = 46, RULE_f_head = 47, RULE_f_exp = 48, 
		RULE_bin_op = 49, RULE_multi_op = 50, RULE_atomic_formula = 51, RULE_predicate = 52, 
		RULE_equallity = 53, RULE_typed_var_list = 54, RULE_typed_obj_list = 55, 
		RULE_typed_vars = 56, RULE_typed_objs = 57, RULE_new_consts = 58, RULE_var_type = 59, 
		RULE_var_or_const = 60, RULE_term = 61, RULE_functionterm = 62, RULE_func_symbol = 63, 
		RULE_problem = 64, RULE_p_object_declaration = 65, RULE_p_init = 66, RULE_init_el = 67, 
		RULE_num_init = 68, RULE_p_goal = 69, RULE_p_htn = 70, RULE_metric_spec = 71, 
		RULE_optimization = 72, RULE_ground_f_exp = 73;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "one_def", "new_types", "const_def", "predicates_def", "atomic_formula_skeleton", 
		"funtions_def", "comp_task_def", "task_def", "task_symbol", "method_def", 
		"tasknetwork_def", "method_symbol", "subtask_defs", "subtask_def", "subtask_id", 
		"ordering_defs", "ordering_def", "constraint_defs", "constraint_def", 
		"causallink_defs", "causallink_def", "action_def", "gd", "gd_empty", "gd_conjuction", 
		"gd_disjuction", "gd_negation", "gd_existential", "gd_univeral", "gd_equality_constraint", 
		"effect_body", "eff_conjuntion", "eff_empty", "c_effect", "forall_effect", 
		"conditional_effect", "literal", "neg_atomic_formula", "cond_effect", 
		"p_effect", "assign_op", "f_head", "f_exp", "bin_op", "multi_op", "atomic_formula", 
		"predicate", "equallity", "typed_var_list", "typed_obj_list", "typed_vars", 
		"typed_objs", "new_consts", "var_type", "var_or_const", "term", "functionterm", 
		"func_symbol", "problem", "p_object_declaration", "p_init", "init_el", 
		"num_init", "p_goal", "p_htn", "metric_spec", "optimization", "ground_f_exp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':functions'", "'number'", "':task'", 
		"':parameters'", "':precondition'", "':effect'", "':method'", "':subtasks'", 
		"':tasks'", "':ordered-subtasks'", "':ordered-tasks'", "':ordering'", 
		"':order'", "':constraints'", "':causal-links'", "':causallinks'", "'and'", 
		"'<'", "'not'", "':action'", "'or'", "'(exists'", "'(forall'", "'forall'", 
		"'when'", "'assign'", "'scale-down'", "'scale-up'", "'increase'", "'decrease'", 
		"'/'", "'+'", "'*'", "'='", "'(='", "'problem'", "':domain'", "':objects'", 
		"':init'", "':goal'", "':htn'", "':htnti'", "':metric'", "'minimize'", 
		"'maximize'", "'(-'", "'total-time'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
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
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(148);
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(149);
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
			setState(152);
			match(T__0);
			setState(153);
			match(T__1);
			setState(154);
			match(T__0);
			setState(155);
			match(T__2);
			setState(156);
			domain_symbol();
			setState(157);
			match(T__3);
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(158);
				require_def();
				}
				break;
			}
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(161);
				type_def();
				}
				break;
			}
			setState(165);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(164);
				const_def();
				}
				break;
			}
			setState(168);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(167);
				predicates_def();
				}
				break;
			}
			setState(171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(170);
				funtions_def();
				}
				break;
			}
			setState(176);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(173);
					comp_task_def();
					}
					} 
				}
				setState(178);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(182);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(179);
					method_def();
					}
					} 
				}
				setState(184);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(185);
				action_def();
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
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
			setState(193);
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
			setState(195);
			match(T__0);
			setState(196);
			match(T__4);
			setState(197);
			require_defs();
			setState(198);
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
			setState(201); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(200);
				match(REQUIRE_NAME);
				}
				}
				setState(203); 
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
		public List<One_defContext> one_def() {
			return getRuleContexts(One_defContext.class);
		}
		public One_defContext one_def(int i) {
			return getRuleContext(One_defContext.class,i);
		}
		public Type_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_def; }
	}

	public final Type_defContext type_def() throws RecognitionException {
		Type_defContext _localctx = new Type_defContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_type_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(T__0);
			setState(206);
			match(T__5);
			setState(208); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(207);
				one_def();
				}
				}
				setState(210); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(212);
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

	public static class One_defContext extends ParserRuleContext {
		public New_typesContext new_types() {
			return getRuleContext(New_typesContext.class,0);
		}
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public One_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_one_def; }
	}

	public final One_defContext one_def() throws RecognitionException {
		One_defContext _localctx = new One_defContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_one_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			new_types();
			setState(217);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(215);
				match(T__6);
				setState(216);
				var_type();
				}
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(219);
					match(NAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(222); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
			setState(224);
			match(T__0);
			setState(225);
			match(T__7);
			setState(226);
			typed_obj_list();
			setState(227);
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
			setState(229);
			match(T__0);
			setState(230);
			match(T__8);
			setState(232); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(231);
				atomic_formula_skeleton();
				}
				}
				setState(234); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(236);
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
			setState(238);
			match(T__0);
			setState(239);
			predicate();
			setState(240);
			typed_var_list();
			setState(241);
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
			setState(243);
			match(T__0);
			setState(244);
			match(T__9);
			setState(251); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(245);
				atomic_formula_skeleton();
				setState(249);
				switch (_input.LA(1)) {
				case T__6:
					{
					setState(246);
					match(T__6);
					setState(247);
					match(T__10);
					}
					break;
				case NAME:
					{
					setState(248);
					var_type();
					}
					break;
				case T__0:
				case T__3:
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(253); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(255);
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
			setState(257);
			match(T__0);
			setState(258);
			match(T__11);
			setState(259);
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
		public Effect_bodyContext effect_body() {
			return getRuleContext(Effect_bodyContext.class,0);
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
			setState(261);
			task_symbol();
			setState(262);
			match(T__12);
			setState(263);
			match(T__0);
			setState(264);
			typed_var_list();
			setState(265);
			match(T__3);
			setState(268);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(266);
				match(T__13);
				setState(267);
				gd();
				}
			}

			setState(272);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(270);
				match(T__14);
				setState(271);
				effect_body();
				}
			}

			setState(274);
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
			setState(276);
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
		public Effect_bodyContext effect_body() {
			return getRuleContext(Effect_bodyContext.class,0);
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
			setState(278);
			match(T__0);
			setState(279);
			match(T__15);
			setState(280);
			method_symbol();
			setState(281);
			match(T__12);
			setState(282);
			match(T__0);
			setState(283);
			typed_var_list();
			setState(284);
			match(T__3);
			setState(285);
			match(T__11);
			setState(286);
			match(T__0);
			setState(287);
			task_symbol();
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(288);
				var_or_const();
				}
				}
				setState(293);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(294);
			match(T__3);
			setState(297);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(295);
				match(T__13);
				setState(296);
				gd();
				}
			}

			setState(301);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(299);
				match(T__14);
				setState(300);
				effect_body();
				}
			}

			setState(303);
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
			setState(307);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(305);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(306);
				subtask_defs();
				}
			}

			setState(311);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(309);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(310);
				ordering_defs();
				}
			}

			setState(315);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(313);
				match(T__22);
				setState(314);
				constraint_defs();
				}
			}

			setState(319);
			_la = _input.LA(1);
			if (_la==T__23 || _la==T__24) {
				{
				setState(317);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==T__24) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(318);
				causallink_defs();
				}
			}

			setState(321);
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
			setState(323);
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
			setState(337);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(325);
				match(T__0);
				setState(326);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(327);
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(328);
				match(T__0);
				setState(329);
				match(T__25);
				setState(331); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(330);
					subtask_def();
					}
					}
					setState(333); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(335);
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
			setState(362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(339);
				match(T__0);
				setState(340);
				task_symbol();
				setState(344);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(341);
					var_or_const();
					}
					}
					setState(346);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(347);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(349);
				match(T__0);
				setState(350);
				subtask_id();
				setState(351);
				match(T__0);
				setState(352);
				task_symbol();
				setState(356);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(353);
					var_or_const();
					}
					}
					setState(358);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(359);
				match(T__3);
				setState(360);
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
			setState(364);
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
			setState(378);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(366);
				match(T__0);
				setState(367);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(368);
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(369);
				match(T__0);
				setState(370);
				match(T__25);
				setState(372); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(371);
					ordering_def();
					}
					}
					setState(374); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(376);
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
			setState(380);
			match(T__0);
			setState(381);
			subtask_id();
			setState(382);
			match(T__26);
			setState(383);
			subtask_id();
			setState(384);
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
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
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
				constraint_def();
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
					constraint_def();
					}
					}
					setState(394); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__43 );
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
		public Constraint_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint_def; }
	}

	public final Constraint_defContext constraint_def() throws RecognitionException {
		Constraint_defContext _localctx = new Constraint_defContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_constraint_def);
		try {
			setState(415);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				match(T__0);
				setState(401);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(402);
				match(T__0);
				setState(403);
				match(T__27);
				setState(404);
				equallity();
				setState(405);
				var_or_const();
				setState(406);
				var_or_const();
				setState(407);
				match(T__3);
				setState(408);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(410);
				equallity();
				setState(411);
				var_or_const();
				setState(412);
				var_or_const();
				setState(413);
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
			setState(429);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				match(T__0);
				setState(418);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(419);
				causallink_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(420);
				match(T__0);
				setState(421);
				match(T__25);
				setState(423); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(422);
					causallink_def();
					}
					}
					setState(425); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(427);
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
			setState(431);
			match(T__0);
			setState(432);
			subtask_id();
			setState(433);
			literal();
			setState(434);
			subtask_id();
			setState(435);
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
			setState(437);
			match(T__0);
			setState(438);
			match(T__28);
			setState(439);
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
		public Gd_conjuctionContext gd_conjuction() {
			return getRuleContext(Gd_conjuctionContext.class,0);
		}
		public Gd_disjuctionContext gd_disjuction() {
			return getRuleContext(Gd_disjuctionContext.class,0);
		}
		public Gd_existentialContext gd_existential() {
			return getRuleContext(Gd_existentialContext.class,0);
		}
		public Gd_univeralContext gd_univeral() {
			return getRuleContext(Gd_univeralContext.class,0);
		}
		public Gd_equality_constraintContext gd_equality_constraint() {
			return getRuleContext(Gd_equality_constraintContext.class,0);
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
			setState(449);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(441);
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(442);
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(443);
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(444);
				gd_conjuction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(445);
				gd_disjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(446);
				gd_existential();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(447);
				gd_univeral();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(448);
				gd_equality_constraint();
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
			setState(451);
			match(T__0);
			setState(452);
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
			setState(454);
			match(T__0);
			setState(455);
			match(T__25);
			setState(457); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(456);
				gd();
				}
				}
				setState(459); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__30) | (1L << T__31) | (1L << T__43))) != 0) );
			setState(461);
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
			setState(463);
			match(T__0);
			setState(464);
			match(T__29);
			setState(466); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(465);
				gd();
				}
				}
				setState(468); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__30) | (1L << T__31) | (1L << T__43))) != 0) );
			setState(470);
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
			setState(472);
			match(T__0);
			setState(473);
			match(T__27);
			setState(474);
			gd();
			setState(475);
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
		enterRule(_localctx, 66, RULE_gd_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477);
			match(T__30);
			setState(478);
			match(T__0);
			setState(479);
			typed_var_list();
			setState(480);
			match(T__3);
			setState(481);
			gd();
			setState(482);
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

	public static class Gd_univeralContext extends ParserRuleContext {
		public Typed_var_listContext typed_var_list() {
			return getRuleContext(Typed_var_listContext.class,0);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_univeralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_univeral; }
	}

	public final Gd_univeralContext gd_univeral() throws RecognitionException {
		Gd_univeralContext _localctx = new Gd_univeralContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_gd_univeral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			match(T__31);
			setState(485);
			match(T__0);
			setState(486);
			typed_var_list();
			setState(487);
			match(T__3);
			setState(488);
			gd();
			setState(489);
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
		enterRule(_localctx, 70, RULE_gd_equality_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(491);
			equallity();
			setState(492);
			var_or_const();
			setState(493);
			var_or_const();
			setState(494);
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

	public static class Effect_bodyContext extends ParserRuleContext {
		public Eff_emptyContext eff_empty() {
			return getRuleContext(Eff_emptyContext.class,0);
		}
		public C_effectContext c_effect() {
			return getRuleContext(C_effectContext.class,0);
		}
		public Eff_conjuntionContext eff_conjuntion() {
			return getRuleContext(Eff_conjuntionContext.class,0);
		}
		public Effect_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect_body; }
	}

	public final Effect_bodyContext effect_body() throws RecognitionException {
		Effect_bodyContext _localctx = new Effect_bodyContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_effect_body);
		try {
			setState(499);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(496);
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(497);
				c_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(498);
				eff_conjuntion();
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

	public static class Eff_conjuntionContext extends ParserRuleContext {
		public List<C_effectContext> c_effect() {
			return getRuleContexts(C_effectContext.class);
		}
		public C_effectContext c_effect(int i) {
			return getRuleContext(C_effectContext.class,i);
		}
		public Eff_conjuntionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_conjuntion; }
	}

	public final Eff_conjuntionContext eff_conjuntion() throws RecognitionException {
		Eff_conjuntionContext _localctx = new Eff_conjuntionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_eff_conjuntion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(501);
			match(T__0);
			setState(502);
			match(T__25);
			setState(504); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(503);
				c_effect();
				}
				}
				setState(506); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(508);
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

	public static class Eff_emptyContext extends ParserRuleContext {
		public Eff_emptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_empty; }
	}

	public final Eff_emptyContext eff_empty() throws RecognitionException {
		Eff_emptyContext _localctx = new Eff_emptyContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			match(T__0);
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

	public static class C_effectContext extends ParserRuleContext {
		public P_effectContext p_effect() {
			return getRuleContext(P_effectContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public Forall_effectContext forall_effect() {
			return getRuleContext(Forall_effectContext.class,0);
		}
		public Conditional_effectContext conditional_effect() {
			return getRuleContext(Conditional_effectContext.class,0);
		}
		public C_effectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_c_effect; }
	}

	public final C_effectContext c_effect() throws RecognitionException {
		C_effectContext _localctx = new C_effectContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_c_effect);
		try {
			setState(517);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(513);
				p_effect();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(514);
				literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(515);
				forall_effect();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(516);
				conditional_effect();
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

	public static class Forall_effectContext extends ParserRuleContext {
		public Effect_bodyContext effect_body() {
			return getRuleContext(Effect_bodyContext.class,0);
		}
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Forall_effectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forall_effect; }
	}

	public final Forall_effectContext forall_effect() throws RecognitionException {
		Forall_effectContext _localctx = new Forall_effectContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_forall_effect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(T__0);
			setState(520);
			match(T__32);
			setState(521);
			match(T__0);
			setState(525);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(522);
				var_or_const();
				}
				}
				setState(527);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(528);
			match(T__3);
			setState(529);
			effect_body();
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

	public static class Conditional_effectContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Cond_effectContext cond_effect() {
			return getRuleContext(Cond_effectContext.class,0);
		}
		public Conditional_effectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional_effect; }
	}

	public final Conditional_effectContext conditional_effect() throws RecognitionException {
		Conditional_effectContext _localctx = new Conditional_effectContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_conditional_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(T__0);
			setState(533);
			match(T__33);
			setState(534);
			gd();
			setState(535);
			cond_effect();
			setState(536);
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
		enterRule(_localctx, 84, RULE_literal);
		try {
			setState(540);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(538);
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(539);
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
		enterRule(_localctx, 86, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			match(T__0);
			setState(543);
			match(T__27);
			setState(544);
			atomic_formula();
			setState(545);
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

	public static class Cond_effectContext extends ParserRuleContext {
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public Cond_effectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond_effect; }
	}

	public final Cond_effectContext cond_effect() throws RecognitionException {
		Cond_effectContext _localctx = new Cond_effectContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_cond_effect);
		int _la;
		try {
			setState(557);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(547);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(548);
				match(T__0);
				setState(549);
				match(T__25);
				setState(551); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(550);
					literal();
					}
					}
					setState(553); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(555);
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
		enterRule(_localctx, 90, RULE_p_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(559);
			match(T__0);
			setState(560);
			assign_op();
			setState(561);
			f_head();
			setState(562);
			f_exp();
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

	public static class Assign_opContext extends ParserRuleContext {
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38))) != 0)) ) {
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
		enterRule(_localctx, 94, RULE_f_head);
		int _la;
		try {
			setState(578);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(567);
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(568);
				match(T__0);
				setState(569);
				func_symbol();
				setState(573);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << VAR_NAME) | (1L << NAME))) != 0)) {
					{
					{
					setState(570);
					term();
					}
					}
					setState(575);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(576);
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
		enterRule(_localctx, 96, RULE_f_exp);
		int _la;
		try {
			setState(603);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(580);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(581);
				match(T__0);
				setState(582);
				bin_op();
				setState(583);
				f_exp();
				setState(584);
				f_exp();
				setState(585);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(587);
				match(T__0);
				setState(588);
				multi_op();
				setState(589);
				f_exp();
				setState(591); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(590);
					f_exp();
					}
					}
					setState(593); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << NAME) | (1L << NUMBER))) != 0) );
				setState(595);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(597);
				match(T__0);
				setState(598);
				match(T__6);
				setState(599);
				f_exp();
				setState(600);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(602);
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
		enterRule(_localctx, 98, RULE_bin_op);
		try {
			setState(608);
			switch (_input.LA(1)) {
			case T__40:
			case T__41:
				enterOuterAlt(_localctx, 1);
				{
				setState(605);
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(606);
				match(T__6);
				}
				break;
			case T__39:
				enterOuterAlt(_localctx, 3);
				{
				setState(607);
				match(T__39);
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
		enterRule(_localctx, 100, RULE_multi_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			_la = _input.LA(1);
			if ( !(_la==T__40 || _la==T__41) ) {
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
		enterRule(_localctx, 102, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(612);
			match(T__0);
			setState(613);
			predicate();
			setState(617);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(614);
				var_or_const();
				}
				}
				setState(619);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(620);
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
		enterRule(_localctx, 104, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(622);
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
		enterRule(_localctx, 106, RULE_equallity);
		try {
			setState(627);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(624);
				match(T__0);
				setState(625);
				match(T__42);
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 2);
				{
				setState(626);
				match(T__43);
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
		enterRule(_localctx, 108, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(629);
				typed_vars();
				}
				}
				setState(634);
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
		enterRule(_localctx, 110, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(635);
				typed_objs();
				}
				}
				setState(640);
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
		enterRule(_localctx, 112, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(642); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(641);
				match(VAR_NAME);
				}
				}
				setState(644); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(646);
			match(T__6);
			setState(647);
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
		enterRule(_localctx, 114, RULE_typed_objs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(650); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(649);
				new_consts();
				}
				}
				setState(652); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(654);
			match(T__6);
			setState(655);
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
		enterRule(_localctx, 116, RULE_new_consts);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(657);
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
		public Var_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_type; }
	}

	public final Var_typeContext var_type() throws RecognitionException {
		Var_typeContext _localctx = new Var_typeContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_var_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
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
		enterRule(_localctx, 120, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(661);
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
		enterRule(_localctx, 122, RULE_term);
		try {
			setState(666);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(663);
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(664);
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(665);
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
		enterRule(_localctx, 124, RULE_functionterm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(668);
			match(T__0);
			setState(669);
			func_symbol();
			setState(673);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << VAR_NAME) | (1L << NAME))) != 0)) {
				{
				{
				setState(670);
				term();
				}
				}
				setState(675);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(676);
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
		enterRule(_localctx, 126, RULE_func_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(678);
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
		enterRule(_localctx, 128, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680);
			match(T__0);
			setState(681);
			match(T__1);
			setState(682);
			match(T__0);
			setState(683);
			match(T__44);
			setState(684);
			match(NAME);
			setState(685);
			match(T__3);
			setState(686);
			match(T__0);
			setState(687);
			match(T__45);
			setState(688);
			match(NAME);
			setState(689);
			match(T__3);
			setState(691);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(690);
				require_def();
				}
				break;
			}
			setState(694);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(693);
				p_object_declaration();
				}
				break;
			}
			setState(697);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(696);
				p_htn();
				}
				break;
			}
			setState(699);
			p_init();
			setState(701);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				setState(700);
				p_goal();
				}
				break;
			}
			setState(704);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(703);
				metric_spec();
				}
			}

			setState(706);
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
		enterRule(_localctx, 130, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
			match(T__0);
			setState(709);
			match(T__46);
			setState(710);
			typed_obj_list();
			setState(711);
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
		enterRule(_localctx, 132, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			match(T__0);
			setState(714);
			match(T__47);
			setState(718);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__43) {
				{
				{
				setState(715);
				init_el();
				}
				}
				setState(720);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(721);
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
		enterRule(_localctx, 134, RULE_init_el);
		try {
			setState(725);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(723);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(724);
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
		enterRule(_localctx, 136, RULE_num_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			equallity();
			setState(728);
			f_head();
			setState(729);
			match(NUMBER);
			setState(730);
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
		enterRule(_localctx, 138, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(732);
			match(T__0);
			setState(733);
			match(T__48);
			setState(734);
			gd();
			setState(735);
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
		public P_htnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_htn; }
	}

	public final P_htnContext p_htn() throws RecognitionException {
		P_htnContext _localctx = new P_htnContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(737);
			match(T__0);
			setState(738);
			_la = _input.LA(1);
			if ( !(_la==T__49 || _la==T__50) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(739);
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
		enterRule(_localctx, 142, RULE_metric_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
			match(T__0);
			setState(742);
			match(T__51);
			setState(743);
			optimization();
			setState(744);
			ground_f_exp();
			setState(745);
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
		enterRule(_localctx, 144, RULE_optimization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(747);
			_la = _input.LA(1);
			if ( !(_la==T__52 || _la==T__53) ) {
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
		enterRule(_localctx, 146, RULE_ground_f_exp);
		int _la;
		try {
			setState(776);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(749);
				match(T__0);
				setState(750);
				bin_op();
				setState(751);
				ground_f_exp();
				setState(752);
				ground_f_exp();
				setState(753);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(758);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(755);
					match(T__0);
					setState(756);
					match(T__6);
					}
					break;
				case T__54:
					{
					setState(757);
					match(T__54);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(760);
				ground_f_exp();
				setState(761);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(763);
				match(NUMBER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(764);
				match(T__0);
				setState(765);
				func_symbol();
				setState(769);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(766);
					match(NAME);
					}
					}
					setState(771);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(772);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(774);
				match(T__55);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(775);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3@\u030d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\3\2\3\2\5\2\u0099\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"\u00a2\n\3\3\3\5\3\u00a5\n\3\3\3\5\3\u00a8\n\3\3\3\5\3\u00ab\n\3\3\3\5"+
		"\3\u00ae\n\3\3\3\7\3\u00b1\n\3\f\3\16\3\u00b4\13\3\3\3\7\3\u00b7\n\3\f"+
		"\3\16\3\u00ba\13\3\3\3\7\3\u00bd\n\3\f\3\16\3\u00c0\13\3\3\3\3\3\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\6\6\6\u00cc\n\6\r\6\16\6\u00cd\3\7\3\7\3\7\6"+
		"\7\u00d3\n\7\r\7\16\7\u00d4\3\7\3\7\3\b\3\b\3\b\5\b\u00dc\n\b\3\t\6\t"+
		"\u00df\n\t\r\t\16\t\u00e0\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\6\13\u00eb"+
		"\n\13\r\13\16\13\u00ec\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\5\r\u00fc\n\r\6\r\u00fe\n\r\r\r\16\r\u00ff\3\r\3\r\3\16\3\16\3"+
		"\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u010f\n\17\3\17\3\17"+
		"\5\17\u0113\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\7\21\u0124\n\21\f\21\16\21\u0127\13\21\3\21\3\21"+
		"\3\21\5\21\u012c\n\21\3\21\3\21\5\21\u0130\n\21\3\21\3\21\3\22\3\22\5"+
		"\22\u0136\n\22\3\22\3\22\5\22\u013a\n\22\3\22\3\22\5\22\u013e\n\22\3\22"+
		"\3\22\5\22\u0142\n\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\6\24\u014e\n\24\r\24\16\24\u014f\3\24\3\24\5\24\u0154\n\24\3\25\3\25"+
		"\3\25\7\25\u0159\n\25\f\25\16\25\u015c\13\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\7\25\u0165\n\25\f\25\16\25\u0168\13\25\3\25\3\25\3\25\5\25"+
		"\u016d\n\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\6\27\u0177\n\27\r"+
		"\27\16\27\u0178\3\27\3\27\5\27\u017d\n\27\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\6\31\u018b\n\31\r\31\16\31\u018c\3\31\3"+
		"\31\5\31\u0191\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u01a2\n\32\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\6\33\u01aa\n\33\r\33\16\33\u01ab\3\33\3\33\5\33\u01b0\n\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\5\36\u01c4\n\36\3\37\3\37\3\37\3 \3 \3 \6 \u01cc\n \r \16 "+
		"\u01cd\3 \3 \3!\3!\3!\6!\u01d5\n!\r!\16!\u01d6\3!\3!\3\"\3\"\3\"\3\"\3"+
		"\"\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\5"+
		"&\u01f6\n&\3\'\3\'\3\'\6\'\u01fb\n\'\r\'\16\'\u01fc\3\'\3\'\3(\3(\3(\3"+
		")\3)\3)\3)\5)\u0208\n)\3*\3*\3*\3*\7*\u020e\n*\f*\16*\u0211\13*\3*\3*"+
		"\3*\3*\3+\3+\3+\3+\3+\3+\3,\3,\5,\u021f\n,\3-\3-\3-\3-\3-\3.\3.\3.\3."+
		"\6.\u022a\n.\r.\16.\u022b\3.\3.\5.\u0230\n.\3/\3/\3/\3/\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\61\3\61\7\61\u023e\n\61\f\61\16\61\u0241\13\61\3\61\3\61"+
		"\5\61\u0245\n\61\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62"+
		"\6\62\u0252\n\62\r\62\16\62\u0253\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3"+
		"\62\5\62\u025e\n\62\3\63\3\63\3\63\5\63\u0263\n\63\3\64\3\64\3\65\3\65"+
		"\3\65\7\65\u026a\n\65\f\65\16\65\u026d\13\65\3\65\3\65\3\66\3\66\3\67"+
		"\3\67\3\67\5\67\u0276\n\67\38\78\u0279\n8\f8\168\u027c\138\39\79\u027f"+
		"\n9\f9\169\u0282\139\3:\6:\u0285\n:\r:\16:\u0286\3:\3:\3:\3;\6;\u028d"+
		"\n;\r;\16;\u028e\3;\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3?\5?\u029d\n?\3@\3"+
		"@\3@\7@\u02a2\n@\f@\16@\u02a5\13@\3@\3@\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B"+
		"\3B\3B\3B\5B\u02b6\nB\3B\5B\u02b9\nB\3B\5B\u02bc\nB\3B\3B\5B\u02c0\nB"+
		"\3B\5B\u02c3\nB\3B\3B\3C\3C\3C\3C\3C\3D\3D\3D\7D\u02cf\nD\fD\16D\u02d2"+
		"\13D\3D\3D\3E\3E\5E\u02d8\nE\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3H\3H\3H\3"+
		"H\3I\3I\3I\3I\3I\3I\3J\3J\3K\3K\3K\3K\3K\3K\3K\3K\3K\5K\u02f9\nK\3K\3"+
		"K\3K\3K\3K\3K\3K\7K\u0302\nK\fK\16K\u0305\13K\3K\3K\3K\3K\5K\u030b\nK"+
		"\3K\2\2L\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668"+
		":<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\2\n\3\2\23\26\3\2\27\30\3\2\32\33\3\2%"+
		")\3\2+,\3\2<=\3\2\64\65\3\2\678\u0320\2\u0098\3\2\2\2\4\u009a\3\2\2\2"+
		"\6\u00c3\3\2\2\2\b\u00c5\3\2\2\2\n\u00cb\3\2\2\2\f\u00cf\3\2\2\2\16\u00d8"+
		"\3\2\2\2\20\u00de\3\2\2\2\22\u00e2\3\2\2\2\24\u00e7\3\2\2\2\26\u00f0\3"+
		"\2\2\2\30\u00f5\3\2\2\2\32\u0103\3\2\2\2\34\u0107\3\2\2\2\36\u0116\3\2"+
		"\2\2 \u0118\3\2\2\2\"\u0135\3\2\2\2$\u0145\3\2\2\2&\u0153\3\2\2\2(\u016c"+
		"\3\2\2\2*\u016e\3\2\2\2,\u017c\3\2\2\2.\u017e\3\2\2\2\60\u0190\3\2\2\2"+
		"\62\u01a1\3\2\2\2\64\u01af\3\2\2\2\66\u01b1\3\2\2\28\u01b7\3\2\2\2:\u01c3"+
		"\3\2\2\2<\u01c5\3\2\2\2>\u01c8\3\2\2\2@\u01d1\3\2\2\2B\u01da\3\2\2\2D"+
		"\u01df\3\2\2\2F\u01e6\3\2\2\2H\u01ed\3\2\2\2J\u01f5\3\2\2\2L\u01f7\3\2"+
		"\2\2N\u0200\3\2\2\2P\u0207\3\2\2\2R\u0209\3\2\2\2T\u0216\3\2\2\2V\u021e"+
		"\3\2\2\2X\u0220\3\2\2\2Z\u022f\3\2\2\2\\\u0231\3\2\2\2^\u0237\3\2\2\2"+
		"`\u0244\3\2\2\2b\u025d\3\2\2\2d\u0262\3\2\2\2f\u0264\3\2\2\2h\u0266\3"+
		"\2\2\2j\u0270\3\2\2\2l\u0275\3\2\2\2n\u027a\3\2\2\2p\u0280\3\2\2\2r\u0284"+
		"\3\2\2\2t\u028c\3\2\2\2v\u0293\3\2\2\2x\u0295\3\2\2\2z\u0297\3\2\2\2|"+
		"\u029c\3\2\2\2~\u029e\3\2\2\2\u0080\u02a8\3\2\2\2\u0082\u02aa\3\2\2\2"+
		"\u0084\u02c6\3\2\2\2\u0086\u02cb\3\2\2\2\u0088\u02d7\3\2\2\2\u008a\u02d9"+
		"\3\2\2\2\u008c\u02de\3\2\2\2\u008e\u02e3\3\2\2\2\u0090\u02e7\3\2\2\2\u0092"+
		"\u02ed\3\2\2\2\u0094\u030a\3\2\2\2\u0096\u0099\5\4\3\2\u0097\u0099\5\u0082"+
		"B\2\u0098\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\3\3\2\2\2\u009a\u009b"+
		"\7\3\2\2\u009b\u009c\7\4\2\2\u009c\u009d\7\3\2\2\u009d\u009e\7\5\2\2\u009e"+
		"\u009f\5\6\4\2\u009f\u00a1\7\6\2\2\u00a0\u00a2\5\b\5\2\u00a1\u00a0\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3\u00a5\5\f\7\2\u00a4"+
		"\u00a3\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a8\5\22"+
		"\n\2\u00a7\u00a6\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9"+
		"\u00ab\5\24\13\2\u00aa\u00a9\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\3"+
		"\2\2\2\u00ac\u00ae\5\30\r\2\u00ad\u00ac\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae"+
		"\u00b2\3\2\2\2\u00af\u00b1\5\32\16\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3"+
		"\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b8\3\2\2\2\u00b4"+
		"\u00b2\3\2\2\2\u00b5\u00b7\5 \21\2\u00b6\u00b5\3\2\2\2\u00b7\u00ba\3\2"+
		"\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00be\3\2\2\2\u00ba"+
		"\u00b8\3\2\2\2\u00bb\u00bd\58\35\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2"+
		"\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0"+
		"\u00be\3\2\2\2\u00c1\u00c2\7\6\2\2\u00c2\5\3\2\2\2\u00c3\u00c4\7=\2\2"+
		"\u00c4\7\3\2\2\2\u00c5\u00c6\7\3\2\2\u00c6\u00c7\7\7\2\2\u00c7\u00c8\5"+
		"\n\6\2\u00c8\u00c9\7\6\2\2\u00c9\t\3\2\2\2\u00ca\u00cc\7;\2\2\u00cb\u00ca"+
		"\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\13\3\2\2\2\u00cf\u00d0\7\3\2\2\u00d0\u00d2\7\b\2\2\u00d1\u00d3\5\16\b"+
		"\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5"+
		"\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d7\7\6\2\2\u00d7\r\3\2\2\2\u00d8"+
		"\u00db\5\20\t\2\u00d9\u00da\7\t\2\2\u00da\u00dc\5x=\2\u00db\u00d9\3\2"+
		"\2\2\u00db\u00dc\3\2\2\2\u00dc\17\3\2\2\2\u00dd\u00df\7=\2\2\u00de\u00dd"+
		"\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\21\3\2\2\2\u00e2\u00e3\7\3\2\2\u00e3\u00e4\7\n\2\2\u00e4\u00e5\5p9\2"+
		"\u00e5\u00e6\7\6\2\2\u00e6\23\3\2\2\2\u00e7\u00e8\7\3\2\2\u00e8\u00ea"+
		"\7\13\2\2\u00e9\u00eb\5\26\f\2\u00ea\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2"+
		"\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef"+
		"\7\6\2\2\u00ef\25\3\2\2\2\u00f0\u00f1\7\3\2\2\u00f1\u00f2\5j\66\2\u00f2"+
		"\u00f3\5n8\2\u00f3\u00f4\7\6\2\2\u00f4\27\3\2\2\2\u00f5\u00f6\7\3\2\2"+
		"\u00f6\u00fd\7\f\2\2\u00f7\u00fb\5\26\f\2\u00f8\u00f9\7\t\2\2\u00f9\u00fc"+
		"\7\r\2\2\u00fa\u00fc\5x=\2\u00fb\u00f8\3\2\2\2\u00fb\u00fa\3\2\2\2\u00fb"+
		"\u00fc\3\2\2\2\u00fc\u00fe\3\2\2\2\u00fd\u00f7\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0101\3\2\2\2\u0101"+
		"\u0102\7\6\2\2\u0102\31\3\2\2\2\u0103\u0104\7\3\2\2\u0104\u0105\7\16\2"+
		"\2\u0105\u0106\5\34\17\2\u0106\33\3\2\2\2\u0107\u0108\5\36\20\2\u0108"+
		"\u0109\7\17\2\2\u0109\u010a\7\3\2\2\u010a\u010b\5n8\2\u010b\u010e\7\6"+
		"\2\2\u010c\u010d\7\20\2\2\u010d\u010f\5:\36\2\u010e\u010c\3\2\2\2\u010e"+
		"\u010f\3\2\2\2\u010f\u0112\3\2\2\2\u0110\u0111\7\21\2\2\u0111\u0113\5"+
		"J&\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"\u0115\7\6\2\2\u0115\35\3\2\2\2\u0116\u0117\7=\2\2\u0117\37\3\2\2\2\u0118"+
		"\u0119\7\3\2\2\u0119\u011a\7\22\2\2\u011a\u011b\5$\23\2\u011b\u011c\7"+
		"\17\2\2\u011c\u011d\7\3\2\2\u011d\u011e\5n8\2\u011e\u011f\7\6\2\2\u011f"+
		"\u0120\7\16\2\2\u0120\u0121\7\3\2\2\u0121\u0125\5\36\20\2\u0122\u0124"+
		"\5z>\2\u0123\u0122\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0125"+
		"\u0126\3\2\2\2\u0126\u0128\3\2\2\2\u0127\u0125\3\2\2\2\u0128\u012b\7\6"+
		"\2\2\u0129\u012a\7\20\2\2\u012a\u012c\5:\36\2\u012b\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012f\3\2\2\2\u012d\u012e\7\21\2\2\u012e\u0130\5"+
		"J&\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0131\3\2\2\2\u0131"+
		"\u0132\5\"\22\2\u0132!\3\2\2\2\u0133\u0134\t\2\2\2\u0134\u0136\5&\24\2"+
		"\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0139\3\2\2\2\u0137\u0138"+
		"\t\3\2\2\u0138\u013a\5,\27\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013d\3\2\2\2\u013b\u013c\7\31\2\2\u013c\u013e\5\60\31\2\u013d\u013b"+
		"\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u0141\3\2\2\2\u013f\u0140\t\4\2\2\u0140"+
		"\u0142\5\64\33\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\3"+
		"\2\2\2\u0143\u0144\7\6\2\2\u0144#\3\2\2\2\u0145\u0146\7=\2\2\u0146%\3"+
		"\2\2\2\u0147\u0148\7\3\2\2\u0148\u0154\7\6\2\2\u0149\u0154\5(\25\2\u014a"+
		"\u014b\7\3\2\2\u014b\u014d\7\34\2\2\u014c\u014e\5(\25\2\u014d\u014c\3"+
		"\2\2\2\u014e\u014f\3\2\2\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2\2\2\u0150"+
		"\u0151\3\2\2\2\u0151\u0152\7\6\2\2\u0152\u0154\3\2\2\2\u0153\u0147\3\2"+
		"\2\2\u0153\u0149\3\2\2\2\u0153\u014a\3\2\2\2\u0154\'\3\2\2\2\u0155\u0156"+
		"\7\3\2\2\u0156\u015a\5\36\20\2\u0157\u0159\5z>\2\u0158\u0157\3\2\2\2\u0159"+
		"\u015c\3\2\2\2\u015a\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015d\3\2"+
		"\2\2\u015c\u015a\3\2\2\2\u015d\u015e\7\6\2\2\u015e\u016d\3\2\2\2\u015f"+
		"\u0160\7\3\2\2\u0160\u0161\5*\26\2\u0161\u0162\7\3\2\2\u0162\u0166\5\36"+
		"\20\2\u0163\u0165\5z>\2\u0164\u0163\3\2\2\2\u0165\u0168\3\2\2\2\u0166"+
		"\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0169\3\2\2\2\u0168\u0166\3\2"+
		"\2\2\u0169\u016a\7\6\2\2\u016a\u016b\7\6\2\2\u016b\u016d\3\2\2\2\u016c"+
		"\u0155\3\2\2\2\u016c\u015f\3\2\2\2\u016d)\3\2\2\2\u016e\u016f\7=\2\2\u016f"+
		"+\3\2\2\2\u0170\u0171\7\3\2\2\u0171\u017d\7\6\2\2\u0172\u017d\5.\30\2"+
		"\u0173\u0174\7\3\2\2\u0174\u0176\7\34\2\2\u0175\u0177\5.\30\2\u0176\u0175"+
		"\3\2\2\2\u0177\u0178\3\2\2\2\u0178\u0176\3\2\2\2\u0178\u0179\3\2\2\2\u0179"+
		"\u017a\3\2\2\2\u017a\u017b\7\6\2\2\u017b\u017d\3\2\2\2\u017c\u0170\3\2"+
		"\2\2\u017c\u0172\3\2\2\2\u017c\u0173\3\2\2\2\u017d-\3\2\2\2\u017e\u017f"+
		"\7\3\2\2\u017f\u0180\5*\26\2\u0180\u0181\7\35\2\2\u0181\u0182\5*\26\2"+
		"\u0182\u0183\7\6\2\2\u0183/\3\2\2\2\u0184\u0185\7\3\2\2\u0185\u0191\7"+
		"\6\2\2\u0186\u0191\5\62\32\2\u0187\u0188\7\3\2\2\u0188\u018a\7\34\2\2"+
		"\u0189\u018b\5\62\32\2\u018a\u0189\3\2\2\2\u018b\u018c\3\2\2\2\u018c\u018a"+
		"\3\2\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018f\7\6\2\2\u018f"+
		"\u0191\3\2\2\2\u0190\u0184\3\2\2\2\u0190\u0186\3\2\2\2\u0190\u0187\3\2"+
		"\2\2\u0191\61\3\2\2\2\u0192\u0193\7\3\2\2\u0193\u01a2\7\6\2\2\u0194\u0195"+
		"\7\3\2\2\u0195\u0196\7\36\2\2\u0196\u0197\5l\67\2\u0197\u0198\5z>\2\u0198"+
		"\u0199\5z>\2\u0199\u019a\7\6\2\2\u019a\u019b\7\6\2\2\u019b\u01a2\3\2\2"+
		"\2\u019c\u019d\5l\67\2\u019d\u019e\5z>\2\u019e\u019f\5z>\2\u019f\u01a0"+
		"\7\6\2\2\u01a0\u01a2\3\2\2\2\u01a1\u0192\3\2\2\2\u01a1\u0194\3\2\2\2\u01a1"+
		"\u019c\3\2\2\2\u01a2\63\3\2\2\2\u01a3\u01a4\7\3\2\2\u01a4\u01b0\7\6\2"+
		"\2\u01a5\u01b0\5\66\34\2\u01a6\u01a7\7\3\2\2\u01a7\u01a9\7\34\2\2\u01a8"+
		"\u01aa\5\66\34\2\u01a9\u01a8\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01a9\3"+
		"\2\2\2\u01ab\u01ac\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01ae\7\6\2\2\u01ae"+
		"\u01b0\3\2\2\2\u01af\u01a3\3\2\2\2\u01af\u01a5\3\2\2\2\u01af\u01a6\3\2"+
		"\2\2\u01b0\65\3\2\2\2\u01b1\u01b2\7\3\2\2\u01b2\u01b3\5*\26\2\u01b3\u01b4"+
		"\5V,\2\u01b4\u01b5\5*\26\2\u01b5\u01b6\7\6\2\2\u01b6\67\3\2\2\2\u01b7"+
		"\u01b8\7\3\2\2\u01b8\u01b9\7\37\2\2\u01b9\u01ba\5\34\17\2\u01ba9\3\2\2"+
		"\2\u01bb\u01c4\5<\37\2\u01bc\u01c4\5h\65\2\u01bd\u01c4\5B\"\2\u01be\u01c4"+
		"\5> \2\u01bf\u01c4\5@!\2\u01c0\u01c4\5D#\2\u01c1\u01c4\5F$\2\u01c2\u01c4"+
		"\5H%\2\u01c3\u01bb\3\2\2\2\u01c3\u01bc\3\2\2\2\u01c3\u01bd\3\2\2\2\u01c3"+
		"\u01be\3\2\2\2\u01c3\u01bf\3\2\2\2\u01c3\u01c0\3\2\2\2\u01c3\u01c1\3\2"+
		"\2\2\u01c3\u01c2\3\2\2\2\u01c4;\3\2\2\2\u01c5\u01c6\7\3\2\2\u01c6\u01c7"+
		"\7\6\2\2\u01c7=\3\2\2\2\u01c8\u01c9\7\3\2\2\u01c9\u01cb\7\34\2\2\u01ca"+
		"\u01cc\5:\36\2\u01cb\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u01cb\3\2"+
		"\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d0\7\6\2\2\u01d0"+
		"?\3\2\2\2\u01d1\u01d2\7\3\2\2\u01d2\u01d4\7 \2\2\u01d3\u01d5\5:\36\2\u01d4"+
		"\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d6\u01d7\3\2"+
		"\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9\7\6\2\2\u01d9A\3\2\2\2\u01da\u01db"+
		"\7\3\2\2\u01db\u01dc\7\36\2\2\u01dc\u01dd\5:\36\2\u01dd\u01de\7\6\2\2"+
		"\u01deC\3\2\2\2\u01df\u01e0\7!\2\2\u01e0\u01e1\7\3\2\2\u01e1\u01e2\5n"+
		"8\2\u01e2\u01e3\7\6\2\2\u01e3\u01e4\5:\36\2\u01e4\u01e5\7\6\2\2\u01e5"+
		"E\3\2\2\2\u01e6\u01e7\7\"\2\2\u01e7\u01e8\7\3\2\2\u01e8\u01e9\5n8\2\u01e9"+
		"\u01ea\7\6\2\2\u01ea\u01eb\5:\36\2\u01eb\u01ec\7\6\2\2\u01ecG\3\2\2\2"+
		"\u01ed\u01ee\5l\67\2\u01ee\u01ef\5z>\2\u01ef\u01f0\5z>\2\u01f0\u01f1\7"+
		"\6\2\2\u01f1I\3\2\2\2\u01f2\u01f6\5N(\2\u01f3\u01f6\5P)\2\u01f4\u01f6"+
		"\5L\'\2\u01f5\u01f2\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f5\u01f4\3\2\2\2\u01f6"+
		"K\3\2\2\2\u01f7\u01f8\7\3\2\2\u01f8\u01fa\7\34\2\2\u01f9\u01fb\5P)\2\u01fa"+
		"\u01f9\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fa\3\2\2\2\u01fc\u01fd\3\2"+
		"\2\2\u01fd\u01fe\3\2\2\2\u01fe\u01ff\7\6\2\2\u01ffM\3\2\2\2\u0200\u0201"+
		"\7\3\2\2\u0201\u0202\7\6\2\2\u0202O\3\2\2\2\u0203\u0208\5\\/\2\u0204\u0208"+
		"\5V,\2\u0205\u0208\5R*\2\u0206\u0208\5T+\2\u0207\u0203\3\2\2\2\u0207\u0204"+
		"\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0206\3\2\2\2\u0208Q\3\2\2\2\u0209"+
		"\u020a\7\3\2\2\u020a\u020b\7#\2\2\u020b\u020f\7\3\2\2\u020c\u020e\5z>"+
		"\2\u020d\u020c\3\2\2\2\u020e\u0211\3\2\2\2\u020f\u020d\3\2\2\2\u020f\u0210"+
		"\3\2\2\2\u0210\u0212\3\2\2\2\u0211\u020f\3\2\2\2\u0212\u0213\7\6\2\2\u0213"+
		"\u0214\5J&\2\u0214\u0215\7\6\2\2\u0215S\3\2\2\2\u0216\u0217\7\3\2\2\u0217"+
		"\u0218\7$\2\2\u0218\u0219\5:\36\2\u0219\u021a\5Z.\2\u021a\u021b\7\6\2"+
		"\2\u021bU\3\2\2\2\u021c\u021f\5X-\2\u021d\u021f\5h\65\2\u021e\u021c\3"+
		"\2\2\2\u021e\u021d\3\2\2\2\u021fW\3\2\2\2\u0220\u0221\7\3\2\2\u0221\u0222"+
		"\7\36\2\2\u0222\u0223\5h\65\2\u0223\u0224\7\6\2\2\u0224Y\3\2\2\2\u0225"+
		"\u0230\5V,\2\u0226\u0227\7\3\2\2\u0227\u0229\7\34\2\2\u0228\u022a\5V,"+
		"\2\u0229\u0228\3\2\2\2\u022a\u022b\3\2\2\2\u022b\u0229\3\2\2\2\u022b\u022c"+
		"\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u022e\7\6\2\2\u022e\u0230\3\2\2\2\u022f"+
		"\u0225\3\2\2\2\u022f\u0226\3\2\2\2\u0230[\3\2\2\2\u0231\u0232\7\3\2\2"+
		"\u0232\u0233\5^\60\2\u0233\u0234\5`\61\2\u0234\u0235\5b\62\2\u0235\u0236"+
		"\7\6\2\2\u0236]\3\2\2\2\u0237\u0238\t\5\2\2\u0238_\3\2\2\2\u0239\u0245"+
		"\5\u0080A\2\u023a\u023b\7\3\2\2\u023b\u023f\5\u0080A\2\u023c\u023e\5|"+
		"?\2\u023d\u023c\3\2\2\2\u023e\u0241\3\2\2\2\u023f\u023d\3\2\2\2\u023f"+
		"\u0240\3\2\2\2\u0240\u0242\3\2\2\2\u0241\u023f\3\2\2\2\u0242\u0243\7\6"+
		"\2\2\u0243\u0245\3\2\2\2\u0244\u0239\3\2\2\2\u0244\u023a\3\2\2\2\u0245"+
		"a\3\2\2\2\u0246\u025e\7@\2\2\u0247\u0248\7\3\2\2\u0248\u0249\5d\63\2\u0249"+
		"\u024a\5b\62\2\u024a\u024b\5b\62\2\u024b\u024c\7\6\2\2\u024c\u025e\3\2"+
		"\2\2\u024d\u024e\7\3\2\2\u024e\u024f\5f\64\2\u024f\u0251\5b\62\2\u0250"+
		"\u0252\5b\62\2\u0251\u0250\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0251\3\2"+
		"\2\2\u0253\u0254\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\7\6\2\2\u0256"+
		"\u025e\3\2\2\2\u0257\u0258\7\3\2\2\u0258\u0259\7\t\2\2\u0259\u025a\5b"+
		"\62\2\u025a\u025b\7\6\2\2\u025b\u025e\3\2\2\2\u025c\u025e\5`\61\2\u025d"+
		"\u0246\3\2\2\2\u025d\u0247\3\2\2\2\u025d\u024d\3\2\2\2\u025d\u0257\3\2"+
		"\2\2\u025d\u025c\3\2\2\2\u025ec\3\2\2\2\u025f\u0263\5f\64\2\u0260\u0263"+
		"\7\t\2\2\u0261\u0263\7*\2\2\u0262\u025f\3\2\2\2\u0262\u0260\3\2\2\2\u0262"+
		"\u0261\3\2\2\2\u0263e\3\2\2\2\u0264\u0265\t\6\2\2\u0265g\3\2\2\2\u0266"+
		"\u0267\7\3\2\2\u0267\u026b\5j\66\2\u0268\u026a\5z>\2\u0269\u0268\3\2\2"+
		"\2\u026a\u026d\3\2\2\2\u026b\u0269\3\2\2\2\u026b\u026c\3\2\2\2\u026c\u026e"+
		"\3\2\2\2\u026d\u026b\3\2\2\2\u026e\u026f\7\6\2\2\u026fi\3\2\2\2\u0270"+
		"\u0271\7=\2\2\u0271k\3\2\2\2\u0272\u0273\7\3\2\2\u0273\u0276\7-\2\2\u0274"+
		"\u0276\7.\2\2\u0275\u0272\3\2\2\2\u0275\u0274\3\2\2\2\u0276m\3\2\2\2\u0277"+
		"\u0279\5r:\2\u0278\u0277\3\2\2\2\u0279\u027c\3\2\2\2\u027a\u0278\3\2\2"+
		"\2\u027a\u027b\3\2\2\2\u027bo\3\2\2\2\u027c\u027a\3\2\2\2\u027d\u027f"+
		"\5t;\2\u027e\u027d\3\2\2\2\u027f\u0282\3\2\2\2\u0280\u027e\3\2\2\2\u0280"+
		"\u0281\3\2\2\2\u0281q\3\2\2\2\u0282\u0280\3\2\2\2\u0283\u0285\7<\2\2\u0284"+
		"\u0283\3\2\2\2\u0285\u0286\3\2\2\2\u0286\u0284\3\2\2\2\u0286\u0287\3\2"+
		"\2\2\u0287\u0288\3\2\2\2\u0288\u0289\7\t\2\2\u0289\u028a\5x=\2\u028as"+
		"\3\2\2\2\u028b\u028d\5v<\2\u028c\u028b\3\2\2\2\u028d\u028e\3\2\2\2\u028e"+
		"\u028c\3\2\2\2\u028e\u028f\3\2\2\2\u028f\u0290\3\2\2\2\u0290\u0291\7\t"+
		"\2\2\u0291\u0292\5x=\2\u0292u\3\2\2\2\u0293\u0294\7=\2\2\u0294w\3\2\2"+
		"\2\u0295\u0296\7=\2\2\u0296y\3\2\2\2\u0297\u0298\t\7\2\2\u0298{\3\2\2"+
		"\2\u0299\u029d\7=\2\2\u029a\u029d\7<\2\2\u029b\u029d\5~@\2\u029c\u0299"+
		"\3\2\2\2\u029c\u029a\3\2\2\2\u029c\u029b\3\2\2\2\u029d}\3\2\2\2\u029e"+
		"\u029f\7\3\2\2\u029f\u02a3\5\u0080A\2\u02a0\u02a2\5|?\2\u02a1\u02a0\3"+
		"\2\2\2\u02a2\u02a5\3\2\2\2\u02a3\u02a1\3\2\2\2\u02a3\u02a4\3\2\2\2\u02a4"+
		"\u02a6\3\2\2\2\u02a5\u02a3\3\2\2\2\u02a6\u02a7\7\6\2\2\u02a7\177\3\2\2"+
		"\2\u02a8\u02a9\7=\2\2\u02a9\u0081\3\2\2\2\u02aa\u02ab\7\3\2\2\u02ab\u02ac"+
		"\7\4\2\2\u02ac\u02ad\7\3\2\2\u02ad\u02ae\7/\2\2\u02ae\u02af\7=\2\2\u02af"+
		"\u02b0\7\6\2\2\u02b0\u02b1\7\3\2\2\u02b1\u02b2\7\60\2\2\u02b2\u02b3\7"+
		"=\2\2\u02b3\u02b5\7\6\2\2\u02b4\u02b6\5\b\5\2\u02b5\u02b4\3\2\2\2\u02b5"+
		"\u02b6\3\2\2\2\u02b6\u02b8\3\2\2\2\u02b7\u02b9\5\u0084C\2\u02b8\u02b7"+
		"\3\2\2\2\u02b8\u02b9\3\2\2\2\u02b9\u02bb\3\2\2\2\u02ba\u02bc\5\u008eH"+
		"\2\u02bb\u02ba\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02bd\3\2\2\2\u02bd\u02bf"+
		"\5\u0086D\2\u02be\u02c0\5\u008cG\2\u02bf\u02be\3\2\2\2\u02bf\u02c0\3\2"+
		"\2\2\u02c0\u02c2\3\2\2\2\u02c1\u02c3\5\u0090I\2\u02c2\u02c1\3\2\2\2\u02c2"+
		"\u02c3\3\2\2\2\u02c3\u02c4\3\2\2\2\u02c4\u02c5\7\6\2\2\u02c5\u0083\3\2"+
		"\2\2\u02c6\u02c7\7\3\2\2\u02c7\u02c8\7\61\2\2\u02c8\u02c9\5p9\2\u02c9"+
		"\u02ca\7\6\2\2\u02ca\u0085\3\2\2\2\u02cb\u02cc\7\3\2\2\u02cc\u02d0\7\62"+
		"\2\2\u02cd\u02cf\5\u0088E\2\u02ce\u02cd\3\2\2\2\u02cf\u02d2\3\2\2\2\u02d0"+
		"\u02ce\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02d3\3\2\2\2\u02d2\u02d0\3\2"+
		"\2\2\u02d3\u02d4\7\6\2\2\u02d4\u0087\3\2\2\2\u02d5\u02d8\5V,\2\u02d6\u02d8"+
		"\5\u008aF\2\u02d7\u02d5\3\2\2\2\u02d7\u02d6\3\2\2\2\u02d8\u0089\3\2\2"+
		"\2\u02d9\u02da\5l\67\2\u02da\u02db\5`\61\2\u02db\u02dc\7@\2\2\u02dc\u02dd"+
		"\7\6\2\2\u02dd\u008b\3\2\2\2\u02de\u02df\7\3\2\2\u02df\u02e0\7\63\2\2"+
		"\u02e0\u02e1\5:\36\2\u02e1\u02e2\7\6\2\2\u02e2\u008d\3\2\2\2\u02e3\u02e4"+
		"\7\3\2\2\u02e4\u02e5\t\b\2\2\u02e5\u02e6\5\"\22\2\u02e6\u008f\3\2\2\2"+
		"\u02e7\u02e8\7\3\2\2\u02e8\u02e9\7\66\2\2\u02e9\u02ea\5\u0092J\2\u02ea"+
		"\u02eb\5\u0094K\2\u02eb\u02ec\7\6\2\2\u02ec\u0091\3\2\2\2\u02ed\u02ee"+
		"\t\t\2\2\u02ee\u0093\3\2\2\2\u02ef\u02f0\7\3\2\2\u02f0\u02f1\5d\63\2\u02f1"+
		"\u02f2\5\u0094K\2\u02f2\u02f3\5\u0094K\2\u02f3\u02f4\7\6\2\2\u02f4\u030b"+
		"\3\2\2\2\u02f5\u02f6\7\3\2\2\u02f6\u02f9\7\t\2\2\u02f7\u02f9\79\2\2\u02f8"+
		"\u02f5\3\2\2\2\u02f8\u02f7\3\2\2\2\u02f9\u02fa\3\2\2\2\u02fa\u02fb\5\u0094"+
		"K\2\u02fb\u02fc\7\6\2\2\u02fc\u030b\3\2\2\2\u02fd\u030b\7@\2\2\u02fe\u02ff"+
		"\7\3\2\2\u02ff\u0303\5\u0080A\2\u0300\u0302\7=\2\2\u0301\u0300\3\2\2\2"+
		"\u0302\u0305\3\2\2\2\u0303\u0301\3\2\2\2\u0303\u0304\3\2\2\2\u0304\u0306"+
		"\3\2\2\2\u0305\u0303\3\2\2\2\u0306\u0307\7\6\2\2\u0307\u030b\3\2\2\2\u0308"+
		"\u030b\7:\2\2\u0309\u030b\5\u0080A\2\u030a\u02ef\3\2\2\2\u030a\u02f8\3"+
		"\2\2\2\u030a\u02fd\3\2\2\2\u030a\u02fe\3\2\2\2\u030a\u0308\3\2\2\2\u030a"+
		"\u0309\3\2\2\2\u030b\u0095\3\2\2\2H\u0098\u00a1\u00a4\u00a7\u00aa\u00ad"+
		"\u00b2\u00b8\u00be\u00cd\u00d4\u00db\u00e0\u00ec\u00fb\u00ff\u010e\u0112"+
		"\u0125\u012b\u012f\u0135\u0139\u013d\u0141\u014f\u0153\u015a\u0166\u016c"+
		"\u0178\u017c\u018c\u0190\u01a1\u01ab\u01af\u01c3\u01cd\u01d6\u01f5\u01fc"+
		"\u0207\u020f\u021e\u022b\u022f\u023f\u0244\u0253\u025d\u0262\u026b\u0275"+
		"\u027a\u0280\u0286\u028e\u029c\u02a3\u02b5\u02b8\u02bb\u02bf\u02c2\u02d0"+
		"\u02d7\u02f8\u0303\u030a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}