// Generated from /home/gregor/Workspace/Panda3/panda3core/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4 by ANTLR 4.5.3
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
		T__59=60, T__60=61, REQUIRE_NAME=62, VAR_NAME=63, NAME=64, COMMENT=65, 
		WS=66, NUMBER=67;
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
		RULE_gd_negation = 32, RULE_gd_existential = 33, RULE_gd_univeral = 34, 
		RULE_gd_equality_constraint = 35, RULE_effect_body = 36, RULE_eff_conjuntion = 37, 
		RULE_eff_empty = 38, RULE_c_effect = 39, RULE_forall_effect = 40, RULE_conditional_effect = 41, 
		RULE_literal = 42, RULE_neg_atomic_formula = 43, RULE_cond_effect = 44, 
		RULE_p_effect = 45, RULE_assign_op = 46, RULE_f_head = 47, RULE_f_exp = 48, 
		RULE_bin_op = 49, RULE_multi_op = 50, RULE_atomic_formula = 51, RULE_predicate = 52, 
		RULE_equallity = 53, RULE_typed_var_list = 54, RULE_typed_obj_list = 55, 
		RULE_typed_vars = 56, RULE_typed_var = 57, RULE_typed_objs = 58, RULE_new_consts = 59, 
		RULE_var_type = 60, RULE_var_or_const = 61, RULE_term = 62, RULE_functionterm = 63, 
		RULE_func_symbol = 64, RULE_problem = 65, RULE_p_object_declaration = 66, 
		RULE_p_init = 67, RULE_init_el = 68, RULE_num_init = 69, RULE_p_goal = 70, 
		RULE_p_htn = 71, RULE_metric_spec = 72, RULE_optimization = 73, RULE_ground_f_exp = 74;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "type_def_list", "new_types", "const_def", "predicates_def", 
		"atomic_formula_skeleton", "funtions_def", "comp_task_def", "task_def", 
		"task_symbol", "method_def", "tasknetwork_def", "method_symbol", "subtask_defs", 
		"subtask_def", "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", 
		"constraint_def", "causallink_defs", "causallink_def", "action_def", "gd", 
		"gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", "gd_existential", 
		"gd_univeral", "gd_equality_constraint", "effect_body", "eff_conjuntion", 
		"eff_empty", "c_effect", "forall_effect", "conditional_effect", "literal", 
		"neg_atomic_formula", "cond_effect", "p_effect", "assign_op", "f_head", 
		"f_exp", "bin_op", "multi_op", "atomic_formula", "predicate", "equallity", 
		"typed_var_list", "typed_obj_list", "typed_vars", "typed_var", "typed_objs", 
		"new_consts", "var_type", "var_or_const", "term", "functionterm", "func_symbol", 
		"problem", "p_object_declaration", "p_init", "init_el", "num_init", "p_goal", 
		"p_htn", "metric_spec", "optimization", "ground_f_exp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':functions'", "'number'", "':task'", 
		"':parameters'", "':precondition'", "':effect'", "':method'", "':subtasks'", 
		"':tasks'", "':ordered-subtasks'", "':ordered-tasks'", "':ordering'", 
		"':order'", "':constraints'", "':causal-links'", "':causallinks'", "'and'", 
		"'<'", "'not'", "'type'", "'typeof'", "'sort'", "'sortof'", "':action'", 
		"'or'", "'(exists'", "'(forall'", "'forall'", "'when'", "'assign'", "'scale-down'", 
		"'scale-up'", "'increase'", "'decrease'", "'/'", "'+'", "'*'", "'='", 
		"'(='", "'either'", "'problem'", "':domain'", "':objects'", "':init'", 
		"':goal'", "':htn'", "':htnti'", "':metric'", "'minimize'", "'maximize'", 
		"'(-'", "'total-time'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "REQUIRE_NAME", "VAR_NAME", "NAME", "COMMENT", "WS", "NUMBER"
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
			setState(152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(150);
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
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
			setState(154);
			match(T__0);
			setState(155);
			match(T__1);
			setState(156);
			match(T__0);
			setState(157);
			match(T__2);
			setState(158);
			domain_symbol();
			setState(159);
			match(T__3);
			setState(161);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(160);
				require_def();
				}
				break;
			}
			setState(164);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(163);
				type_def();
				}
				break;
			}
			setState(167);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(166);
				const_def();
				}
				break;
			}
			setState(170);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(169);
				predicates_def();
				}
				break;
			}
			setState(173);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(172);
				funtions_def();
				}
				break;
			}
			setState(178);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(175);
					comp_task_def();
					}
					} 
				}
				setState(180);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(184);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(181);
					method_def();
					}
					} 
				}
				setState(186);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(187);
				action_def();
				}
				}
				setState(192);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(193);
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
			setState(195);
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
			setState(197);
			match(T__0);
			setState(198);
			match(T__4);
			setState(199);
			require_defs();
			setState(200);
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
			setState(203); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(202);
				match(REQUIRE_NAME);
				}
				}
				setState(205); 
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
			setState(207);
			match(T__0);
			setState(208);
			match(T__5);
			setState(209);
			type_def_list();
			setState(210);
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
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(212);
					match(NAME);
					}
					}
					setState(217);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(218);
				new_types();
				setState(219);
				match(T__6);
				setState(220);
				var_type();
				setState(221);
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
			setState(226); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(225);
				match(NAME);
				}
				}
				setState(228); 
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
			setState(230);
			match(T__0);
			setState(231);
			match(T__7);
			setState(232);
			typed_obj_list();
			setState(233);
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
			setState(235);
			match(T__0);
			setState(236);
			match(T__8);
			setState(238); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(237);
				atomic_formula_skeleton();
				}
				}
				setState(240); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(242);
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
			setState(244);
			match(T__0);
			setState(245);
			predicate();
			setState(246);
			typed_var_list();
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
			setState(249);
			match(T__0);
			setState(250);
			match(T__9);
			setState(257); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(251);
				atomic_formula_skeleton();
				setState(255);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(252);
					match(T__6);
					setState(253);
					match(T__10);
					}
					break;
				case 2:
					{
					setState(254);
					var_type();
					}
					break;
				}
				}
				}
				setState(259); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
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
			setState(263);
			match(T__0);
			setState(264);
			match(T__11);
			setState(265);
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
			setState(267);
			task_symbol();
			setState(268);
			match(T__12);
			setState(269);
			match(T__0);
			setState(270);
			typed_var_list();
			setState(271);
			match(T__3);
			setState(274);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(272);
				match(T__13);
				setState(273);
				gd();
				}
			}

			setState(278);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(276);
				match(T__14);
				setState(277);
				effect_body();
				}
			}

			setState(280);
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
			setState(282);
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
			setState(284);
			match(T__0);
			setState(285);
			match(T__15);
			setState(286);
			method_symbol();
			setState(287);
			match(T__12);
			setState(288);
			match(T__0);
			setState(289);
			typed_var_list();
			setState(290);
			match(T__3);
			setState(291);
			match(T__11);
			setState(292);
			match(T__0);
			setState(293);
			task_symbol();
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(294);
				var_or_const();
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(300);
			match(T__3);
			setState(303);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(301);
				match(T__13);
				setState(302);
				gd();
				}
			}

			setState(307);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(305);
				match(T__14);
				setState(306);
				effect_body();
				}
			}

			setState(309);
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
			setState(313);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(311);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(312);
				subtask_defs();
				}
			}

			setState(317);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(315);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(316);
				ordering_defs();
				}
			}

			setState(321);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(319);
				match(T__22);
				setState(320);
				constraint_defs();
				}
			}

			setState(325);
			_la = _input.LA(1);
			if (_la==T__23 || _la==T__24) {
				{
				setState(323);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==T__24) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(324);
				causallink_defs();
				}
			}

			setState(327);
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
			setState(329);
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
			setState(343);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(331);
				match(T__0);
				setState(332);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(333);
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(334);
				match(T__0);
				setState(335);
				match(T__25);
				setState(337); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(336);
					subtask_def();
					}
					}
					setState(339); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(341);
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
			setState(368);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(345);
				match(T__0);
				setState(346);
				task_symbol();
				setState(350);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(347);
					var_or_const();
					}
					}
					setState(352);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(353);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(355);
				match(T__0);
				setState(356);
				subtask_id();
				setState(357);
				match(T__0);
				setState(358);
				task_symbol();
				setState(362);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(359);
					var_or_const();
					}
					}
					setState(364);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(365);
				match(T__3);
				setState(366);
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
			setState(370);
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
			setState(384);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(372);
				match(T__0);
				setState(373);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(374);
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(375);
				match(T__0);
				setState(376);
				match(T__25);
				setState(378); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(377);
					ordering_def();
					}
					}
					setState(380); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(382);
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
			setState(386);
			match(T__0);
			setState(387);
			subtask_id();
			setState(388);
			match(T__26);
			setState(389);
			subtask_id();
			setState(390);
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
			setState(404);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(392);
				match(T__0);
				setState(393);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(394);
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(395);
				match(T__0);
				setState(396);
				match(T__25);
				setState(398); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(397);
					constraint_def();
					}
					}
					setState(400); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__47 );
				setState(402);
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
			setState(434);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
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
				match(T__0);
				setState(409);
				match(T__27);
				setState(410);
				equallity();
				setState(411);
				var_or_const();
				setState(412);
				var_or_const();
				setState(413);
				match(T__3);
				setState(414);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(416);
				equallity();
				setState(417);
				var_or_const();
				setState(418);
				var_or_const();
				setState(419);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(421);
				match(T__0);
				setState(422);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(423);
				typed_var();
				setState(424);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(426);
				match(T__0);
				setState(427);
				match(T__27);
				setState(428);
				match(T__0);
				setState(429);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(430);
				typed_var();
				setState(431);
				match(T__3);
				setState(432);
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
			setState(448);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(436);
				match(T__0);
				setState(437);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(438);
				causallink_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(439);
				match(T__0);
				setState(440);
				match(T__25);
				setState(442); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(441);
					causallink_def();
					}
					}
					setState(444); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
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
			setState(450);
			match(T__0);
			setState(451);
			subtask_id();
			setState(452);
			literal();
			setState(453);
			subtask_id();
			setState(454);
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
			setState(456);
			match(T__0);
			setState(457);
			match(T__32);
			setState(458);
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
			setState(468);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(460);
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(461);
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(462);
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(463);
				gd_conjuction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(464);
				gd_disjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(465);
				gd_existential();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(466);
				gd_univeral();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(467);
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
			setState(470);
			match(T__0);
			setState(471);
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
			setState(473);
			match(T__0);
			setState(474);
			match(T__25);
			setState(476); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(475);
				gd();
				}
				}
				setState(478); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__34) | (1L << T__35) | (1L << T__47))) != 0) );
			setState(480);
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
			setState(482);
			match(T__0);
			setState(483);
			match(T__33);
			setState(485); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(484);
				gd();
				}
				}
				setState(487); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__34) | (1L << T__35) | (1L << T__47))) != 0) );
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
			setState(491);
			match(T__0);
			setState(492);
			match(T__27);
			setState(493);
			gd();
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
			setState(496);
			match(T__34);
			setState(497);
			match(T__0);
			setState(498);
			typed_var_list();
			setState(499);
			match(T__3);
			setState(500);
			gd();
			setState(501);
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
			setState(503);
			match(T__35);
			setState(504);
			match(T__0);
			setState(505);
			typed_var_list();
			setState(506);
			match(T__3);
			setState(507);
			gd();
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
			setState(510);
			equallity();
			setState(511);
			var_or_const();
			setState(512);
			var_or_const();
			setState(513);
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
			setState(518);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(515);
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(516);
				c_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(517);
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
			setState(520);
			match(T__0);
			setState(521);
			match(T__25);
			setState(523); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(522);
				c_effect();
				}
				}
				setState(525); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(527);
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
			setState(529);
			match(T__0);
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
			setState(536);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(532);
				p_effect();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(533);
				literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(534);
				forall_effect();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(535);
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
			setState(538);
			match(T__0);
			setState(539);
			match(T__36);
			setState(540);
			match(T__0);
			setState(544);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(541);
				var_or_const();
				}
				}
				setState(546);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(547);
			match(T__3);
			setState(548);
			effect_body();
			setState(549);
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
			setState(551);
			match(T__0);
			setState(552);
			match(T__37);
			setState(553);
			gd();
			setState(554);
			cond_effect();
			setState(555);
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
			setState(559);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(557);
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(558);
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
			setState(561);
			match(T__0);
			setState(562);
			match(T__27);
			setState(563);
			atomic_formula();
			setState(564);
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
			setState(576);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(566);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(567);
				match(T__0);
				setState(568);
				match(T__25);
				setState(570); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(569);
					literal();
					}
					}
					setState(572); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(574);
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
			setState(578);
			match(T__0);
			setState(579);
			assign_op();
			setState(580);
			f_head();
			setState(581);
			f_exp();
			setState(582);
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
			setState(584);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42))) != 0)) ) {
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
			setState(597);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(586);
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(587);
				match(T__0);
				setState(588);
				func_symbol();
				setState(592);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (VAR_NAME - 1)) | (1L << (NAME - 1)))) != 0)) {
					{
					{
					setState(589);
					term();
					}
					}
					setState(594);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(595);
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
			setState(622);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(599);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(600);
				match(T__0);
				setState(601);
				bin_op();
				setState(602);
				f_exp();
				setState(603);
				f_exp();
				setState(604);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(606);
				match(T__0);
				setState(607);
				multi_op();
				setState(608);
				f_exp();
				setState(610); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(609);
					f_exp();
					}
					}
					setState(612); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME || _la==NUMBER );
				setState(614);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(616);
				match(T__0);
				setState(617);
				match(T__6);
				setState(618);
				f_exp();
				setState(619);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(621);
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
			setState(627);
			switch (_input.LA(1)) {
			case T__44:
			case T__45:
				enterOuterAlt(_localctx, 1);
				{
				setState(624);
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(625);
				match(T__6);
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 3);
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
			setState(629);
			_la = _input.LA(1);
			if ( !(_la==T__44 || _la==T__45) ) {
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
			setState(631);
			match(T__0);
			setState(632);
			predicate();
			setState(636);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(633);
				var_or_const();
				}
				}
				setState(638);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(639);
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
			setState(641);
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
			setState(646);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(643);
				match(T__0);
				setState(644);
				match(T__46);
				}
				break;
			case T__47:
				enterOuterAlt(_localctx, 2);
				{
				setState(645);
				match(T__47);
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
			setState(651);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(648);
				typed_vars();
				}
				}
				setState(653);
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
			setState(657);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(654);
				typed_objs();
				}
				}
				setState(659);
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
			setState(661); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(660);
				match(VAR_NAME);
				}
				}
				setState(663); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(665);
			match(T__6);
			setState(666);
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
		enterRule(_localctx, 114, RULE_typed_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(668);
			match(VAR_NAME);
			setState(669);
			match(T__6);
			setState(670);
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
		enterRule(_localctx, 116, RULE_typed_objs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(673); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(672);
				new_consts();
				}
				}
				setState(675); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(677);
			match(T__6);
			setState(678);
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
		enterRule(_localctx, 118, RULE_new_consts);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680);
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
		enterRule(_localctx, 120, RULE_var_type);
		int _la;
		try {
			setState(692);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(682);
				match(NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(683);
				match(T__0);
				setState(684);
				match(T__48);
				setState(686); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(685);
					var_type();
					}
					}
					setState(688); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME );
				setState(690);
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
		enterRule(_localctx, 122, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(694);
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
		enterRule(_localctx, 124, RULE_term);
		try {
			setState(699);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(696);
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(697);
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(698);
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
		enterRule(_localctx, 126, RULE_functionterm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(701);
			match(T__0);
			setState(702);
			func_symbol();
			setState(706);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (VAR_NAME - 1)) | (1L << (NAME - 1)))) != 0)) {
				{
				{
				setState(703);
				term();
				}
				}
				setState(708);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(709);
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
		enterRule(_localctx, 128, RULE_func_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(711);
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
		enterRule(_localctx, 130, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			match(T__0);
			setState(714);
			match(T__1);
			setState(715);
			match(T__0);
			setState(716);
			match(T__49);
			setState(717);
			match(NAME);
			setState(718);
			match(T__3);
			setState(719);
			match(T__0);
			setState(720);
			match(T__50);
			setState(721);
			match(NAME);
			setState(722);
			match(T__3);
			setState(724);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(723);
				require_def();
				}
				break;
			}
			setState(727);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				setState(726);
				p_object_declaration();
				}
				break;
			}
			setState(730);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				{
				setState(729);
				p_htn();
				}
				break;
			}
			setState(732);
			p_init();
			setState(734);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(733);
				p_goal();
				}
				break;
			}
			setState(737);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(736);
				metric_spec();
				}
			}

			setState(739);
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
		enterRule(_localctx, 132, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
			match(T__0);
			setState(742);
			match(T__51);
			setState(743);
			typed_obj_list();
			setState(744);
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
		enterRule(_localctx, 134, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(746);
			match(T__0);
			setState(747);
			match(T__52);
			setState(751);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__47) {
				{
				{
				setState(748);
				init_el();
				}
				}
				setState(753);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(754);
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
		enterRule(_localctx, 136, RULE_init_el);
		try {
			setState(758);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(756);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(757);
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
		enterRule(_localctx, 138, RULE_num_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			equallity();
			setState(761);
			f_head();
			setState(762);
			match(NUMBER);
			setState(763);
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
		enterRule(_localctx, 140, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(765);
			match(T__0);
			setState(766);
			match(T__53);
			setState(767);
			gd();
			setState(768);
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
		enterRule(_localctx, 142, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			match(T__0);
			setState(771);
			_la = _input.LA(1);
			if ( !(_la==T__54 || _la==T__55) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(777);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(772);
				match(T__12);
				setState(773);
				match(T__0);
				setState(774);
				typed_var_list();
				setState(775);
				match(T__3);
				}
			}

			setState(779);
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
		enterRule(_localctx, 144, RULE_metric_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(781);
			match(T__0);
			setState(782);
			match(T__56);
			setState(783);
			optimization();
			setState(784);
			ground_f_exp();
			setState(785);
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
		enterRule(_localctx, 146, RULE_optimization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(787);
			_la = _input.LA(1);
			if ( !(_la==T__57 || _la==T__58) ) {
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
		enterRule(_localctx, 148, RULE_ground_f_exp);
		int _la;
		try {
			setState(816);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(789);
				match(T__0);
				setState(790);
				bin_op();
				setState(791);
				ground_f_exp();
				setState(792);
				ground_f_exp();
				setState(793);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(798);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(795);
					match(T__0);
					setState(796);
					match(T__6);
					}
					break;
				case T__59:
					{
					setState(797);
					match(T__59);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(800);
				ground_f_exp();
				setState(801);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(803);
				match(NUMBER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(804);
				match(T__0);
				setState(805);
				func_symbol();
				setState(809);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(806);
					match(NAME);
					}
					}
					setState(811);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(812);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(814);
				match(T__60);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(815);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3E\u0335\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\3\2\3\2\5\2\u009b\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\5\3\u00a4\n\3\3\3\5\3\u00a7\n\3\3\3\5\3\u00aa\n\3\3\3\5\3\u00ad\n\3"+
		"\3\3\5\3\u00b0\n\3\3\3\7\3\u00b3\n\3\f\3\16\3\u00b6\13\3\3\3\7\3\u00b9"+
		"\n\3\f\3\16\3\u00bc\13\3\3\3\7\3\u00bf\n\3\f\3\16\3\u00c2\13\3\3\3\3\3"+
		"\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\6\6\u00ce\n\6\r\6\16\6\u00cf\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\7\b\u00d8\n\b\f\b\16\b\u00db\13\b\3\b\3\b\3\b\3\b\3\b"+
		"\5\b\u00e2\n\b\3\t\6\t\u00e5\n\t\r\t\16\t\u00e6\3\n\3\n\3\n\3\n\3\n\3"+
		"\13\3\13\3\13\6\13\u00f1\n\13\r\13\16\13\u00f2\3\13\3\13\3\f\3\f\3\f\3"+
		"\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0102\n\r\6\r\u0104\n\r\r\r\16\r\u0105"+
		"\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0115"+
		"\n\17\3\17\3\17\5\17\u0119\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u012a\n\21\f\21\16\21\u012d\13"+
		"\21\3\21\3\21\3\21\5\21\u0132\n\21\3\21\3\21\5\21\u0136\n\21\3\21\3\21"+
		"\3\22\3\22\5\22\u013c\n\22\3\22\3\22\5\22\u0140\n\22\3\22\3\22\5\22\u0144"+
		"\n\22\3\22\3\22\5\22\u0148\n\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\6\24\u0154\n\24\r\24\16\24\u0155\3\24\3\24\5\24\u015a\n\24"+
		"\3\25\3\25\3\25\7\25\u015f\n\25\f\25\16\25\u0162\13\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\7\25\u016b\n\25\f\25\16\25\u016e\13\25\3\25\3\25"+
		"\3\25\5\25\u0173\n\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\6\27\u017d"+
		"\n\27\r\27\16\27\u017e\3\27\3\27\5\27\u0183\n\27\3\30\3\30\3\30\3\30\3"+
		"\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\6\31\u0191\n\31\r\31\16\31\u0192"+
		"\3\31\3\31\5\31\u0197\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u01b5\n\32\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\6\33\u01bd\n\33\r\33\16\33\u01be\3\33\3\33\5\33\u01c3\n\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\5\36\u01d7\n\36\3\37\3\37\3\37\3 \3 \3 \6 \u01df\n \r \16 "+
		"\u01e0\3 \3 \3!\3!\3!\6!\u01e8\n!\r!\16!\u01e9\3!\3!\3\"\3\"\3\"\3\"\3"+
		"\"\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\5"+
		"&\u0209\n&\3\'\3\'\3\'\6\'\u020e\n\'\r\'\16\'\u020f\3\'\3\'\3(\3(\3(\3"+
		")\3)\3)\3)\5)\u021b\n)\3*\3*\3*\3*\7*\u0221\n*\f*\16*\u0224\13*\3*\3*"+
		"\3*\3*\3+\3+\3+\3+\3+\3+\3,\3,\5,\u0232\n,\3-\3-\3-\3-\3-\3.\3.\3.\3."+
		"\6.\u023d\n.\r.\16.\u023e\3.\3.\5.\u0243\n.\3/\3/\3/\3/\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\61\3\61\7\61\u0251\n\61\f\61\16\61\u0254\13\61\3\61\3\61"+
		"\5\61\u0258\n\61\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62"+
		"\6\62\u0265\n\62\r\62\16\62\u0266\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3"+
		"\62\5\62\u0271\n\62\3\63\3\63\3\63\5\63\u0276\n\63\3\64\3\64\3\65\3\65"+
		"\3\65\7\65\u027d\n\65\f\65\16\65\u0280\13\65\3\65\3\65\3\66\3\66\3\67"+
		"\3\67\3\67\5\67\u0289\n\67\38\78\u028c\n8\f8\168\u028f\138\39\79\u0292"+
		"\n9\f9\169\u0295\139\3:\6:\u0298\n:\r:\16:\u0299\3:\3:\3:\3;\3;\3;\3;"+
		"\3<\6<\u02a4\n<\r<\16<\u02a5\3<\3<\3<\3=\3=\3>\3>\3>\3>\6>\u02b1\n>\r"+
		">\16>\u02b2\3>\3>\5>\u02b7\n>\3?\3?\3@\3@\3@\5@\u02be\n@\3A\3A\3A\7A\u02c3"+
		"\nA\fA\16A\u02c6\13A\3A\3A\3B\3B\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C\u02d7"+
		"\nC\3C\5C\u02da\nC\3C\5C\u02dd\nC\3C\3C\5C\u02e1\nC\3C\5C\u02e4\nC\3C"+
		"\3C\3D\3D\3D\3D\3D\3E\3E\3E\7E\u02f0\nE\fE\16E\u02f3\13E\3E\3E\3F\3F\5"+
		"F\u02f9\nF\3G\3G\3G\3G\3G\3H\3H\3H\3H\3H\3I\3I\3I\3I\3I\3I\3I\5I\u030c"+
		"\nI\3I\3I\3J\3J\3J\3J\3J\3J\3K\3K\3L\3L\3L\3L\3L\3L\3L\3L\3L\5L\u0321"+
		"\nL\3L\3L\3L\3L\3L\3L\3L\7L\u032a\nL\fL\16L\u032d\13L\3L\3L\3L\3L\5L\u0333"+
		"\nL\3L\2\2M\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66"+
		"8:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\u0096\2\13\3\2\23\26\3\2\27\30\3\2\32\33"+
		"\3\2\37\"\3\2)-\3\2/\60\3\2AB\3\29:\3\2<=\u034c\2\u009a\3\2\2\2\4\u009c"+
		"\3\2\2\2\6\u00c5\3\2\2\2\b\u00c7\3\2\2\2\n\u00cd\3\2\2\2\f\u00d1\3\2\2"+
		"\2\16\u00e1\3\2\2\2\20\u00e4\3\2\2\2\22\u00e8\3\2\2\2\24\u00ed\3\2\2\2"+
		"\26\u00f6\3\2\2\2\30\u00fb\3\2\2\2\32\u0109\3\2\2\2\34\u010d\3\2\2\2\36"+
		"\u011c\3\2\2\2 \u011e\3\2\2\2\"\u013b\3\2\2\2$\u014b\3\2\2\2&\u0159\3"+
		"\2\2\2(\u0172\3\2\2\2*\u0174\3\2\2\2,\u0182\3\2\2\2.\u0184\3\2\2\2\60"+
		"\u0196\3\2\2\2\62\u01b4\3\2\2\2\64\u01c2\3\2\2\2\66\u01c4\3\2\2\28\u01ca"+
		"\3\2\2\2:\u01d6\3\2\2\2<\u01d8\3\2\2\2>\u01db\3\2\2\2@\u01e4\3\2\2\2B"+
		"\u01ed\3\2\2\2D\u01f2\3\2\2\2F\u01f9\3\2\2\2H\u0200\3\2\2\2J\u0208\3\2"+
		"\2\2L\u020a\3\2\2\2N\u0213\3\2\2\2P\u021a\3\2\2\2R\u021c\3\2\2\2T\u0229"+
		"\3\2\2\2V\u0231\3\2\2\2X\u0233\3\2\2\2Z\u0242\3\2\2\2\\\u0244\3\2\2\2"+
		"^\u024a\3\2\2\2`\u0257\3\2\2\2b\u0270\3\2\2\2d\u0275\3\2\2\2f\u0277\3"+
		"\2\2\2h\u0279\3\2\2\2j\u0283\3\2\2\2l\u0288\3\2\2\2n\u028d\3\2\2\2p\u0293"+
		"\3\2\2\2r\u0297\3\2\2\2t\u029e\3\2\2\2v\u02a3\3\2\2\2x\u02aa\3\2\2\2z"+
		"\u02b6\3\2\2\2|\u02b8\3\2\2\2~\u02bd\3\2\2\2\u0080\u02bf\3\2\2\2\u0082"+
		"\u02c9\3\2\2\2\u0084\u02cb\3\2\2\2\u0086\u02e7\3\2\2\2\u0088\u02ec\3\2"+
		"\2\2\u008a\u02f8\3\2\2\2\u008c\u02fa\3\2\2\2\u008e\u02ff\3\2\2\2\u0090"+
		"\u0304\3\2\2\2\u0092\u030f\3\2\2\2\u0094\u0315\3\2\2\2\u0096\u0332\3\2"+
		"\2\2\u0098\u009b\5\4\3\2\u0099\u009b\5\u0084C\2\u009a\u0098\3\2\2\2\u009a"+
		"\u0099\3\2\2\2\u009b\3\3\2\2\2\u009c\u009d\7\3\2\2\u009d\u009e\7\4\2\2"+
		"\u009e\u009f\7\3\2\2\u009f\u00a0\7\5\2\2\u00a0\u00a1\5\6\4\2\u00a1\u00a3"+
		"\7\6\2\2\u00a2\u00a4\5\b\5\2\u00a3\u00a2\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\u00a6\3\2\2\2\u00a5\u00a7\5\f\7\2\u00a6\u00a5\3\2\2\2\u00a6\u00a7\3\2"+
		"\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00aa\5\22\n\2\u00a9\u00a8\3\2\2\2\u00a9"+
		"\u00aa\3\2\2\2\u00aa\u00ac\3\2\2\2\u00ab\u00ad\5\24\13\2\u00ac\u00ab\3"+
		"\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae\u00b0\5\30\r\2\u00af"+
		"\u00ae\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b4\3\2\2\2\u00b1\u00b3\5\32"+
		"\16\2\u00b2\u00b1\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00ba\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00b9\5 "+
		"\21\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba"+
		"\u00bb\3\2\2\2\u00bb\u00c0\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd\u00bf\58"+
		"\35\2\u00be\u00bd\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0"+
		"\u00c1\3\2\2\2\u00c1\u00c3\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3\u00c4\7\6"+
		"\2\2\u00c4\5\3\2\2\2\u00c5\u00c6\7B\2\2\u00c6\7\3\2\2\2\u00c7\u00c8\7"+
		"\3\2\2\u00c8\u00c9\7\7\2\2\u00c9\u00ca\5\n\6\2\u00ca\u00cb\7\6\2\2\u00cb"+
		"\t\3\2\2\2\u00cc\u00ce\7@\2\2\u00cd\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2"+
		"\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\13\3\2\2\2\u00d1\u00d2"+
		"\7\3\2\2\u00d2\u00d3\7\b\2\2\u00d3\u00d4\5\16\b\2\u00d4\u00d5\7\6\2\2"+
		"\u00d5\r\3\2\2\2\u00d6\u00d8\7B\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3"+
		"\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00e2\3\2\2\2\u00db"+
		"\u00d9\3\2\2\2\u00dc\u00dd\5\20\t\2\u00dd\u00de\7\t\2\2\u00de\u00df\5"+
		"z>\2\u00df\u00e0\5\16\b\2\u00e0\u00e2\3\2\2\2\u00e1\u00d9\3\2\2\2\u00e1"+
		"\u00dc\3\2\2\2\u00e2\17\3\2\2\2\u00e3\u00e5\7B\2\2\u00e4\u00e3\3\2\2\2"+
		"\u00e5\u00e6\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\21"+
		"\3\2\2\2\u00e8\u00e9\7\3\2\2\u00e9\u00ea\7\n\2\2\u00ea\u00eb\5p9\2\u00eb"+
		"\u00ec\7\6\2\2\u00ec\23\3\2\2\2\u00ed\u00ee\7\3\2\2\u00ee\u00f0\7\13\2"+
		"\2\u00ef\u00f1\5\26\f\2\u00f0\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2"+
		"\u00f0\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f5\7\6"+
		"\2\2\u00f5\25\3\2\2\2\u00f6\u00f7\7\3\2\2\u00f7\u00f8\5j\66\2\u00f8\u00f9"+
		"\5n8\2\u00f9\u00fa\7\6\2\2\u00fa\27\3\2\2\2\u00fb\u00fc\7\3\2\2\u00fc"+
		"\u0103\7\f\2\2\u00fd\u0101\5\26\f\2\u00fe\u00ff\7\t\2\2\u00ff\u0102\7"+
		"\r\2\2\u0100\u0102\5z>\2\u0101\u00fe\3\2\2\2\u0101\u0100\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103\u00fd\3\2\2\2\u0104\u0105\3\2"+
		"\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0107\3\2\2\2\u0107"+
		"\u0108\7\6\2\2\u0108\31\3\2\2\2\u0109\u010a\7\3\2\2\u010a\u010b\7\16\2"+
		"\2\u010b\u010c\5\34\17\2\u010c\33\3\2\2\2\u010d\u010e\5\36\20\2\u010e"+
		"\u010f\7\17\2\2\u010f\u0110\7\3\2\2\u0110\u0111\5n8\2\u0111\u0114\7\6"+
		"\2\2\u0112\u0113\7\20\2\2\u0113\u0115\5:\36\2\u0114\u0112\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0117\7\21\2\2\u0117\u0119\5"+
		"J&\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a\3\2\2\2\u011a"+
		"\u011b\7\6\2\2\u011b\35\3\2\2\2\u011c\u011d\7B\2\2\u011d\37\3\2\2\2\u011e"+
		"\u011f\7\3\2\2\u011f\u0120\7\22\2\2\u0120\u0121\5$\23\2\u0121\u0122\7"+
		"\17\2\2\u0122\u0123\7\3\2\2\u0123\u0124\5n8\2\u0124\u0125\7\6\2\2\u0125"+
		"\u0126\7\16\2\2\u0126\u0127\7\3\2\2\u0127\u012b\5\36\20\2\u0128\u012a"+
		"\5|?\2\u0129\u0128\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012e\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0131\7\6"+
		"\2\2\u012f\u0130\7\20\2\2\u0130\u0132\5:\36\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0135\3\2\2\2\u0133\u0134\7\21\2\2\u0134\u0136\5"+
		"J&\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\3\2\2\2\u0137"+
		"\u0138\5\"\22\2\u0138!\3\2\2\2\u0139\u013a\t\2\2\2\u013a\u013c\5&\24\2"+
		"\u013b\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013e"+
		"\t\3\2\2\u013e\u0140\5,\27\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140"+
		"\u0143\3\2\2\2\u0141\u0142\7\31\2\2\u0142\u0144\5\60\31\2\u0143\u0141"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0147\3\2\2\2\u0145\u0146\t\4\2\2\u0146"+
		"\u0148\5\64\33\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149\3"+
		"\2\2\2\u0149\u014a\7\6\2\2\u014a#\3\2\2\2\u014b\u014c\7B\2\2\u014c%\3"+
		"\2\2\2\u014d\u014e\7\3\2\2\u014e\u015a\7\6\2\2\u014f\u015a\5(\25\2\u0150"+
		"\u0151\7\3\2\2\u0151\u0153\7\34\2\2\u0152\u0154\5(\25\2\u0153\u0152\3"+
		"\2\2\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156"+
		"\u0157\3\2\2\2\u0157\u0158\7\6\2\2\u0158\u015a\3\2\2\2\u0159\u014d\3\2"+
		"\2\2\u0159\u014f\3\2\2\2\u0159\u0150\3\2\2\2\u015a\'\3\2\2\2\u015b\u015c"+
		"\7\3\2\2\u015c\u0160\5\36\20\2\u015d\u015f\5|?\2\u015e\u015d\3\2\2\2\u015f"+
		"\u0162\3\2\2\2\u0160\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0163\3\2"+
		"\2\2\u0162\u0160\3\2\2\2\u0163\u0164\7\6\2\2\u0164\u0173\3\2\2\2\u0165"+
		"\u0166\7\3\2\2\u0166\u0167\5*\26\2\u0167\u0168\7\3\2\2\u0168\u016c\5\36"+
		"\20\2\u0169\u016b\5|?\2\u016a\u0169\3\2\2\2\u016b\u016e\3\2\2\2\u016c"+
		"\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e\u016c\3\2"+
		"\2\2\u016f\u0170\7\6\2\2\u0170\u0171\7\6\2\2\u0171\u0173\3\2\2\2\u0172"+
		"\u015b\3\2\2\2\u0172\u0165\3\2\2\2\u0173)\3\2\2\2\u0174\u0175\7B\2\2\u0175"+
		"+\3\2\2\2\u0176\u0177\7\3\2\2\u0177\u0183\7\6\2\2\u0178\u0183\5.\30\2"+
		"\u0179\u017a\7\3\2\2\u017a\u017c\7\34\2\2\u017b\u017d\5.\30\2\u017c\u017b"+
		"\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017c\3\2\2\2\u017e\u017f\3\2\2\2\u017f"+
		"\u0180\3\2\2\2\u0180\u0181\7\6\2\2\u0181\u0183\3\2\2\2\u0182\u0176\3\2"+
		"\2\2\u0182\u0178\3\2\2\2\u0182\u0179\3\2\2\2\u0183-\3\2\2\2\u0184\u0185"+
		"\7\3\2\2\u0185\u0186\5*\26\2\u0186\u0187\7\35\2\2\u0187\u0188\5*\26\2"+
		"\u0188\u0189\7\6\2\2\u0189/\3\2\2\2\u018a\u018b\7\3\2\2\u018b\u0197\7"+
		"\6\2\2\u018c\u0197\5\62\32\2\u018d\u018e\7\3\2\2\u018e\u0190\7\34\2\2"+
		"\u018f\u0191\5\62\32\2\u0190\u018f\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0190"+
		"\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0195\7\6\2\2\u0195"+
		"\u0197\3\2\2\2\u0196\u018a\3\2\2\2\u0196\u018c\3\2\2\2\u0196\u018d\3\2"+
		"\2\2\u0197\61\3\2\2\2\u0198\u0199\7\3\2\2\u0199\u01b5\7\6\2\2\u019a\u019b"+
		"\7\3\2\2\u019b\u019c\7\36\2\2\u019c\u019d\5l\67\2\u019d\u019e\5|?\2\u019e"+
		"\u019f\5|?\2\u019f\u01a0\7\6\2\2\u01a0\u01a1\7\6\2\2\u01a1\u01b5\3\2\2"+
		"\2\u01a2\u01a3\5l\67\2\u01a3\u01a4\5|?\2\u01a4\u01a5\5|?\2\u01a5\u01a6"+
		"\7\6\2\2\u01a6\u01b5\3\2\2\2\u01a7\u01a8\7\3\2\2\u01a8\u01a9\t\5\2\2\u01a9"+
		"\u01aa\5t;\2\u01aa\u01ab\7\6\2\2\u01ab\u01b5\3\2\2\2\u01ac\u01ad\7\3\2"+
		"\2\u01ad\u01ae\7\36\2\2\u01ae\u01af\7\3\2\2\u01af\u01b0\t\5\2\2\u01b0"+
		"\u01b1\5t;\2\u01b1\u01b2\7\6\2\2\u01b2\u01b3\7\6\2\2\u01b3\u01b5\3\2\2"+
		"\2\u01b4\u0198\3\2\2\2\u01b4\u019a\3\2\2\2\u01b4\u01a2\3\2\2\2\u01b4\u01a7"+
		"\3\2\2\2\u01b4\u01ac\3\2\2\2\u01b5\63\3\2\2\2\u01b6\u01b7\7\3\2\2\u01b7"+
		"\u01c3\7\6\2\2\u01b8\u01c3\5\66\34\2\u01b9\u01ba\7\3\2\2\u01ba\u01bc\7"+
		"\34\2\2\u01bb\u01bd\5\66\34\2\u01bc\u01bb\3\2\2\2\u01bd\u01be\3\2\2\2"+
		"\u01be\u01bc\3\2\2\2\u01be\u01bf\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0\u01c1"+
		"\7\6\2\2\u01c1\u01c3\3\2\2\2\u01c2\u01b6\3\2\2\2\u01c2\u01b8\3\2\2\2\u01c2"+
		"\u01b9\3\2\2\2\u01c3\65\3\2\2\2\u01c4\u01c5\7\3\2\2\u01c5\u01c6\5*\26"+
		"\2\u01c6\u01c7\5V,\2\u01c7\u01c8\5*\26\2\u01c8\u01c9\7\6\2\2\u01c9\67"+
		"\3\2\2\2\u01ca\u01cb\7\3\2\2\u01cb\u01cc\7#\2\2\u01cc\u01cd\5\34\17\2"+
		"\u01cd9\3\2\2\2\u01ce\u01d7\5<\37\2\u01cf\u01d7\5h\65\2\u01d0\u01d7\5"+
		"B\"\2\u01d1\u01d7\5> \2\u01d2\u01d7\5@!\2\u01d3\u01d7\5D#\2\u01d4\u01d7"+
		"\5F$\2\u01d5\u01d7\5H%\2\u01d6\u01ce\3\2\2\2\u01d6\u01cf\3\2\2\2\u01d6"+
		"\u01d0\3\2\2\2\u01d6\u01d1\3\2\2\2\u01d6\u01d2\3\2\2\2\u01d6\u01d3\3\2"+
		"\2\2\u01d6\u01d4\3\2\2\2\u01d6\u01d5\3\2\2\2\u01d7;\3\2\2\2\u01d8\u01d9"+
		"\7\3\2\2\u01d9\u01da\7\6\2\2\u01da=\3\2\2\2\u01db\u01dc\7\3\2\2\u01dc"+
		"\u01de\7\34\2\2\u01dd\u01df\5:\36\2\u01de\u01dd\3\2\2\2\u01df\u01e0\3"+
		"\2\2\2\u01e0\u01de\3\2\2\2\u01e0\u01e1\3\2\2\2\u01e1\u01e2\3\2\2\2\u01e2"+
		"\u01e3\7\6\2\2\u01e3?\3\2\2\2\u01e4\u01e5\7\3\2\2\u01e5\u01e7\7$\2\2\u01e6"+
		"\u01e8\5:\36\2\u01e7\u01e6\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01e7\3\2"+
		"\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb\u01ec\7\6\2\2\u01ec"+
		"A\3\2\2\2\u01ed\u01ee\7\3\2\2\u01ee\u01ef\7\36\2\2\u01ef\u01f0\5:\36\2"+
		"\u01f0\u01f1\7\6\2\2\u01f1C\3\2\2\2\u01f2\u01f3\7%\2\2\u01f3\u01f4\7\3"+
		"\2\2\u01f4\u01f5\5n8\2\u01f5\u01f6\7\6\2\2\u01f6\u01f7\5:\36\2\u01f7\u01f8"+
		"\7\6\2\2\u01f8E\3\2\2\2\u01f9\u01fa\7&\2\2\u01fa\u01fb\7\3\2\2\u01fb\u01fc"+
		"\5n8\2\u01fc\u01fd\7\6\2\2\u01fd\u01fe\5:\36\2\u01fe\u01ff\7\6\2\2\u01ff"+
		"G\3\2\2\2\u0200\u0201\5l\67\2\u0201\u0202\5|?\2\u0202\u0203\5|?\2\u0203"+
		"\u0204\7\6\2\2\u0204I\3\2\2\2\u0205\u0209\5N(\2\u0206\u0209\5P)\2\u0207"+
		"\u0209\5L\'\2\u0208\u0205\3\2\2\2\u0208\u0206\3\2\2\2\u0208\u0207\3\2"+
		"\2\2\u0209K\3\2\2\2\u020a\u020b\7\3\2\2\u020b\u020d\7\34\2\2\u020c\u020e"+
		"\5P)\2\u020d\u020c\3\2\2\2\u020e\u020f\3\2\2\2\u020f\u020d\3\2\2\2\u020f"+
		"\u0210\3\2\2\2\u0210\u0211\3\2\2\2\u0211\u0212\7\6\2\2\u0212M\3\2\2\2"+
		"\u0213\u0214\7\3\2\2\u0214\u0215\7\6\2\2\u0215O\3\2\2\2\u0216\u021b\5"+
		"\\/\2\u0217\u021b\5V,\2\u0218\u021b\5R*\2\u0219\u021b\5T+\2\u021a\u0216"+
		"\3\2\2\2\u021a\u0217\3\2\2\2\u021a\u0218\3\2\2\2\u021a\u0219\3\2\2\2\u021b"+
		"Q\3\2\2\2\u021c\u021d\7\3\2\2\u021d\u021e\7\'\2\2\u021e\u0222\7\3\2\2"+
		"\u021f\u0221\5|?\2\u0220\u021f\3\2\2\2\u0221\u0224\3\2\2\2\u0222\u0220"+
		"\3\2\2\2\u0222\u0223\3\2\2\2\u0223\u0225\3\2\2\2\u0224\u0222\3\2\2\2\u0225"+
		"\u0226\7\6\2\2\u0226\u0227\5J&\2\u0227\u0228\7\6\2\2\u0228S\3\2\2\2\u0229"+
		"\u022a\7\3\2\2\u022a\u022b\7(\2\2\u022b\u022c\5:\36\2\u022c\u022d\5Z."+
		"\2\u022d\u022e\7\6\2\2\u022eU\3\2\2\2\u022f\u0232\5X-\2\u0230\u0232\5"+
		"h\65\2\u0231\u022f\3\2\2\2\u0231\u0230\3\2\2\2\u0232W\3\2\2\2\u0233\u0234"+
		"\7\3\2\2\u0234\u0235\7\36\2\2\u0235\u0236\5h\65\2\u0236\u0237\7\6\2\2"+
		"\u0237Y\3\2\2\2\u0238\u0243\5V,\2\u0239\u023a\7\3\2\2\u023a\u023c\7\34"+
		"\2\2\u023b\u023d\5V,\2\u023c\u023b\3\2\2\2\u023d\u023e\3\2\2\2\u023e\u023c"+
		"\3\2\2\2\u023e\u023f\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0241\7\6\2\2\u0241"+
		"\u0243\3\2\2\2\u0242\u0238\3\2\2\2\u0242\u0239\3\2\2\2\u0243[\3\2\2\2"+
		"\u0244\u0245\7\3\2\2\u0245\u0246\5^\60\2\u0246\u0247\5`\61\2\u0247\u0248"+
		"\5b\62\2\u0248\u0249\7\6\2\2\u0249]\3\2\2\2\u024a\u024b\t\6\2\2\u024b"+
		"_\3\2\2\2\u024c\u0258\5\u0082B\2\u024d\u024e\7\3\2\2\u024e\u0252\5\u0082"+
		"B\2\u024f\u0251\5~@\2\u0250\u024f\3\2\2\2\u0251\u0254\3\2\2\2\u0252\u0250"+
		"\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0255\3\2\2\2\u0254\u0252\3\2\2\2\u0255"+
		"\u0256\7\6\2\2\u0256\u0258\3\2\2\2\u0257\u024c\3\2\2\2\u0257\u024d\3\2"+
		"\2\2\u0258a\3\2\2\2\u0259\u0271\7E\2\2\u025a\u025b\7\3\2\2\u025b\u025c"+
		"\5d\63\2\u025c\u025d\5b\62\2\u025d\u025e\5b\62\2\u025e\u025f\7\6\2\2\u025f"+
		"\u0271\3\2\2\2\u0260\u0261\7\3\2\2\u0261\u0262\5f\64\2\u0262\u0264\5b"+
		"\62\2\u0263\u0265\5b\62\2\u0264\u0263\3\2\2\2\u0265\u0266\3\2\2\2\u0266"+
		"\u0264\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0268\3\2\2\2\u0268\u0269\7\6"+
		"\2\2\u0269\u0271\3\2\2\2\u026a\u026b\7\3\2\2\u026b\u026c\7\t\2\2\u026c"+
		"\u026d\5b\62\2\u026d\u026e\7\6\2\2\u026e\u0271\3\2\2\2\u026f\u0271\5`"+
		"\61\2\u0270\u0259\3\2\2\2\u0270\u025a\3\2\2\2\u0270\u0260\3\2\2\2\u0270"+
		"\u026a\3\2\2\2\u0270\u026f\3\2\2\2\u0271c\3\2\2\2\u0272\u0276\5f\64\2"+
		"\u0273\u0276\7\t\2\2\u0274\u0276\7.\2\2\u0275\u0272\3\2\2\2\u0275\u0273"+
		"\3\2\2\2\u0275\u0274\3\2\2\2\u0276e\3\2\2\2\u0277\u0278\t\7\2\2\u0278"+
		"g\3\2\2\2\u0279\u027a\7\3\2\2\u027a\u027e\5j\66\2\u027b\u027d\5|?\2\u027c"+
		"\u027b\3\2\2\2\u027d\u0280\3\2\2\2\u027e\u027c\3\2\2\2\u027e\u027f\3\2"+
		"\2\2\u027f\u0281\3\2\2\2\u0280\u027e\3\2\2\2\u0281\u0282\7\6\2\2\u0282"+
		"i\3\2\2\2\u0283\u0284\7B\2\2\u0284k\3\2\2\2\u0285\u0286\7\3\2\2\u0286"+
		"\u0289\7\61\2\2\u0287\u0289\7\62\2\2\u0288\u0285\3\2\2\2\u0288\u0287\3"+
		"\2\2\2\u0289m\3\2\2\2\u028a\u028c\5r:\2\u028b\u028a\3\2\2\2\u028c\u028f"+
		"\3\2\2\2\u028d\u028b\3\2\2\2\u028d\u028e\3\2\2\2\u028eo\3\2\2\2\u028f"+
		"\u028d\3\2\2\2\u0290\u0292\5v<\2\u0291\u0290\3\2\2\2\u0292\u0295\3\2\2"+
		"\2\u0293\u0291\3\2\2\2\u0293\u0294\3\2\2\2\u0294q\3\2\2\2\u0295\u0293"+
		"\3\2\2\2\u0296\u0298\7A\2\2\u0297\u0296\3\2\2\2\u0298\u0299\3\2\2\2\u0299"+
		"\u0297\3\2\2\2\u0299\u029a\3\2\2\2\u029a\u029b\3\2\2\2\u029b\u029c\7\t"+
		"\2\2\u029c\u029d\5z>\2\u029ds\3\2\2\2\u029e\u029f\7A\2\2\u029f\u02a0\7"+
		"\t\2\2\u02a0\u02a1\5z>\2\u02a1u\3\2\2\2\u02a2\u02a4\5x=\2\u02a3\u02a2"+
		"\3\2\2\2\u02a4\u02a5\3\2\2\2\u02a5\u02a3\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6"+
		"\u02a7\3\2\2\2\u02a7\u02a8\7\t\2\2\u02a8\u02a9\5z>\2\u02a9w\3\2\2\2\u02aa"+
		"\u02ab\7B\2\2\u02aby\3\2\2\2\u02ac\u02b7\7B\2\2\u02ad\u02ae\7\3\2\2\u02ae"+
		"\u02b0\7\63\2\2\u02af\u02b1\5z>\2\u02b0\u02af\3\2\2\2\u02b1\u02b2\3\2"+
		"\2\2\u02b2\u02b0\3\2\2\2\u02b2\u02b3\3\2\2\2\u02b3\u02b4\3\2\2\2\u02b4"+
		"\u02b5\7\6\2\2\u02b5\u02b7\3\2\2\2\u02b6\u02ac\3\2\2\2\u02b6\u02ad\3\2"+
		"\2\2\u02b7{\3\2\2\2\u02b8\u02b9\t\b\2\2\u02b9}\3\2\2\2\u02ba\u02be\7B"+
		"\2\2\u02bb\u02be\7A\2\2\u02bc\u02be\5\u0080A\2\u02bd\u02ba\3\2\2\2\u02bd"+
		"\u02bb\3\2\2\2\u02bd\u02bc\3\2\2\2\u02be\177\3\2\2\2\u02bf\u02c0\7\3\2"+
		"\2\u02c0\u02c4\5\u0082B\2\u02c1\u02c3\5~@\2\u02c2\u02c1\3\2\2\2\u02c3"+
		"\u02c6\3\2\2\2\u02c4\u02c2\3\2\2\2\u02c4\u02c5\3\2\2\2\u02c5\u02c7\3\2"+
		"\2\2\u02c6\u02c4\3\2\2\2\u02c7\u02c8\7\6\2\2\u02c8\u0081\3\2\2\2\u02c9"+
		"\u02ca\7B\2\2\u02ca\u0083\3\2\2\2\u02cb\u02cc\7\3\2\2\u02cc\u02cd\7\4"+
		"\2\2\u02cd\u02ce\7\3\2\2\u02ce\u02cf\7\64\2\2\u02cf\u02d0\7B\2\2\u02d0"+
		"\u02d1\7\6\2\2\u02d1\u02d2\7\3\2\2\u02d2\u02d3\7\65\2\2\u02d3\u02d4\7"+
		"B\2\2\u02d4\u02d6\7\6\2\2\u02d5\u02d7\5\b\5\2\u02d6\u02d5\3\2\2\2\u02d6"+
		"\u02d7\3\2\2\2\u02d7\u02d9\3\2\2\2\u02d8\u02da\5\u0086D\2\u02d9\u02d8"+
		"\3\2\2\2\u02d9\u02da\3\2\2\2\u02da\u02dc\3\2\2\2\u02db\u02dd\5\u0090I"+
		"\2\u02dc\u02db\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02de\3\2\2\2\u02de\u02e0"+
		"\5\u0088E\2\u02df\u02e1\5\u008eH\2\u02e0\u02df\3\2\2\2\u02e0\u02e1\3\2"+
		"\2\2\u02e1\u02e3\3\2\2\2\u02e2\u02e4\5\u0092J\2\u02e3\u02e2\3\2\2\2\u02e3"+
		"\u02e4\3\2\2\2\u02e4\u02e5\3\2\2\2\u02e5\u02e6\7\6\2\2\u02e6\u0085\3\2"+
		"\2\2\u02e7\u02e8\7\3\2\2\u02e8\u02e9\7\66\2\2\u02e9\u02ea\5p9\2\u02ea"+
		"\u02eb\7\6\2\2\u02eb\u0087\3\2\2\2\u02ec\u02ed\7\3\2\2\u02ed\u02f1\7\67"+
		"\2\2\u02ee\u02f0\5\u008aF\2\u02ef\u02ee\3\2\2\2\u02f0\u02f3\3\2\2\2\u02f1"+
		"\u02ef\3\2\2\2\u02f1\u02f2\3\2\2\2\u02f2\u02f4\3\2\2\2\u02f3\u02f1\3\2"+
		"\2\2\u02f4\u02f5\7\6\2\2\u02f5\u0089\3\2\2\2\u02f6\u02f9\5V,\2\u02f7\u02f9"+
		"\5\u008cG\2\u02f8\u02f6\3\2\2\2\u02f8\u02f7\3\2\2\2\u02f9\u008b\3\2\2"+
		"\2\u02fa\u02fb\5l\67\2\u02fb\u02fc\5`\61\2\u02fc\u02fd\7E\2\2\u02fd\u02fe"+
		"\7\6\2\2\u02fe\u008d\3\2\2\2\u02ff\u0300\7\3\2\2\u0300\u0301\78\2\2\u0301"+
		"\u0302\5:\36\2\u0302\u0303\7\6\2\2\u0303\u008f\3\2\2\2\u0304\u0305\7\3"+
		"\2\2\u0305\u030b\t\t\2\2\u0306\u0307\7\17\2\2\u0307\u0308\7\3\2\2\u0308"+
		"\u0309\5n8\2\u0309\u030a\7\6\2\2\u030a\u030c\3\2\2\2\u030b\u0306\3\2\2"+
		"\2\u030b\u030c\3\2\2\2\u030c\u030d\3\2\2\2\u030d\u030e\5\"\22\2\u030e"+
		"\u0091\3\2\2\2\u030f\u0310\7\3\2\2\u0310\u0311\7;\2\2\u0311\u0312\5\u0094"+
		"K\2\u0312\u0313\5\u0096L\2\u0313\u0314\7\6\2\2\u0314\u0093\3\2\2\2\u0315"+
		"\u0316\t\n\2\2\u0316\u0095\3\2\2\2\u0317\u0318\7\3\2\2\u0318\u0319\5d"+
		"\63\2\u0319\u031a\5\u0096L\2\u031a\u031b\5\u0096L\2\u031b\u031c\7\6\2"+
		"\2\u031c\u0333\3\2\2\2\u031d\u031e\7\3\2\2\u031e\u0321\7\t\2\2\u031f\u0321"+
		"\7>\2\2\u0320\u031d\3\2\2\2\u0320\u031f\3\2\2\2\u0321\u0322\3\2\2\2\u0322"+
		"\u0323\5\u0096L\2\u0323\u0324\7\6\2\2\u0324\u0333\3\2\2\2\u0325\u0333"+
		"\7E\2\2\u0326\u0327\7\3\2\2\u0327\u032b\5\u0082B\2\u0328\u032a\7B\2\2"+
		"\u0329\u0328\3\2\2\2\u032a\u032d\3\2\2\2\u032b\u0329\3\2\2\2\u032b\u032c"+
		"\3\2\2\2\u032c\u032e\3\2\2\2\u032d\u032b\3\2\2\2\u032e\u032f\7\6\2\2\u032f"+
		"\u0333\3\2\2\2\u0330\u0333\7?\2\2\u0331\u0333\5\u0082B\2\u0332\u0317\3"+
		"\2\2\2\u0332\u0320\3\2\2\2\u0332\u0325\3\2\2\2\u0332\u0326\3\2\2\2\u0332"+
		"\u0330\3\2\2\2\u0332\u0331\3\2\2\2\u0333\u0097\3\2\2\2K\u009a\u00a3\u00a6"+
		"\u00a9\u00ac\u00af\u00b4\u00ba\u00c0\u00cf\u00d9\u00e1\u00e6\u00f2\u0101"+
		"\u0105\u0114\u0118\u012b\u0131\u0135\u013b\u013f\u0143\u0147\u0155\u0159"+
		"\u0160\u016c\u0172\u017e\u0182\u0192\u0196\u01b4\u01be\u01c2\u01d6\u01e0"+
		"\u01e9\u0208\u020f\u021a\u0222\u0231\u023e\u0242\u0252\u0257\u0266\u0270"+
		"\u0275\u027e\u0288\u028d\u0293\u0299\u02a5\u02b2\u02b6\u02bd\u02c4\u02d6"+
		"\u02d9\u02dc\u02e0\u02e3\u02f1\u02f8\u030b\u0320\u032b\u0332";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}