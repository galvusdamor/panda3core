// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4 by ANTLR 4.5
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
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

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
		T__52=53, T__53=54, REQUIRE_NAME=55, VAR_NAME=56, NAME=57, COMMENT=58, 
		WS=59, NUMBER=60;
	public static final int
		RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3, 
		RULE_require_defs = 4, RULE_type_def = 5, RULE_one_def = 6, RULE_new_types = 7, 
		RULE_const_def = 8, RULE_funtions_def = 9, RULE_predicates_def = 10, RULE_atomic_formula_skeleton = 11, 
		RULE_comp_task_def = 12, RULE_task_def = 13, RULE_task_symbol = 14, RULE_method_def = 15, 
		RULE_tasknetwork_def = 16, RULE_method_symbol = 17, RULE_subtask_defs = 18, 
		RULE_subtask_def = 19, RULE_subtask_id = 20, RULE_ordering_defs = 21, 
		RULE_ordering_def = 22, RULE_constraint_defs = 23, RULE_constraint_def = 24, 
		RULE_action_def = 25, RULE_gd = 26, RULE_gd_empty = 27, RULE_gd_conjuction = 28, 
		RULE_gd_disjuction = 29, RULE_gd_negation = 30, RULE_gd_existential = 31, 
		RULE_gd_univeral = 32, RULE_gd_equality_constraint = 33, RULE_effect_body = 34, 
		RULE_eff_conjuntion = 35, RULE_eff_empty = 36, RULE_c_effect = 37, RULE_forall_effect = 38, 
		RULE_conditional_effect = 39, RULE_literal = 40, RULE_neg_atomic_formula = 41, 
		RULE_cond_effect = 42, RULE_p_effect = 43, RULE_assign_op = 44, RULE_f_head = 45, 
		RULE_f_exp = 46, RULE_bin_op = 47, RULE_multi_op = 48, RULE_atomic_formula = 49, 
		RULE_predicate = 50, RULE_equallity = 51, RULE_typed_var_list = 52, RULE_typed_obj_list = 53, 
		RULE_typed_vars = 54, RULE_typed_objs = 55, RULE_new_consts = 56, RULE_var_type = 57, 
		RULE_var_or_const = 58, RULE_term = 59, RULE_functionterm = 60, RULE_func_symbol = 61, 
		RULE_problem = 62, RULE_p_object_declaration = 63, RULE_p_init = 64, RULE_init_el = 65, 
		RULE_num_init = 66, RULE_p_goal = 67, RULE_p_htn = 68, RULE_metric_spec = 69, 
		RULE_optimization = 70, RULE_ground_f_exp = 71;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "one_def", "new_types", "const_def", "funtions_def", "predicates_def", 
		"atomic_formula_skeleton", "comp_task_def", "task_def", "task_symbol", 
		"method_def", "tasknetwork_def", "method_symbol", "subtask_defs", "subtask_def", 
		"subtask_id", "ordering_defs", "ordering_def", "constraint_defs", "constraint_def", 
		"action_def", "gd", "gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", 
		"gd_existential", "gd_univeral", "gd_equality_constraint", "effect_body", 
		"eff_conjuntion", "eff_empty", "c_effect", "forall_effect", "conditional_effect", 
		"literal", "neg_atomic_formula", "cond_effect", "p_effect", "assign_op", 
		"f_head", "f_exp", "bin_op", "multi_op", "atomic_formula", "predicate", 
		"equallity", "typed_var_list", "typed_obj_list", "typed_vars", "typed_objs", 
		"new_consts", "var_type", "var_or_const", "term", "functionterm", "func_symbol", 
		"problem", "p_object_declaration", "p_init", "init_el", "num_init", "p_goal", 
		"p_htn", "metric_spec", "optimization", "ground_f_exp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':functions'", "'number'", "':predicates'", "':task'", 
		"':parameters'", "':precondition'", "':effect'", "':method'", "':subtasks'", 
		"':tasks'", "':ordered-subtasks'", "':ordered-tasks'", "':ordering'", 
		"':order'", "':constraints'", "'and'", "'<'", "'not'", "':action'", "'or'", 
		"'(exists'", "'(forall'", "'forall'", "'when'", "'assign'", "'scale-down'", 
		"'scale-up'", "'increase'", "'decrease'", "'/'", "'+'", "'*'", "'='", 
		"'(='", "'problem'", "':domain'", "':objects'", "':init'", "':goal'", 
		"':htn'", "':htnti'", "':metric'", "'minimize'", "'maximize'", "'(-'", 
		"'total-time'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, "REQUIRE_NAME", "VAR_NAME", 
		"NAME", "COMMENT", "WS", "NUMBER"
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
	@NotNull
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
			setState(146);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144); 
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(145); 
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
			setState(148); 
			match(T__0);
			setState(149); 
			match(T__1);
			setState(150); 
			match(T__0);
			setState(151); 
			match(T__2);
			setState(152); 
			domain_symbol();
			setState(153); 
			match(T__3);
			setState(155);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(154); 
				require_def();
				}
				break;
			}
			setState(158);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(157); 
				type_def();
				}
				break;
			}
			setState(161);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(160); 
				const_def();
				}
				break;
			}
			setState(164);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(163); 
				predicates_def();
				}
				break;
			}
			setState(167);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(166); 
				funtions_def();
				}
				break;
			}
			setState(172);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(169); 
					comp_task_def();
					}
					} 
				}
				setState(174);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(178);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(175); 
					method_def();
					}
					} 
				}
				setState(180);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(181); 
				action_def();
				}
				}
				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(187); 
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
			setState(189); 
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
			setState(191); 
			match(T__0);
			setState(192); 
			match(T__4);
			setState(193); 
			require_defs();
			setState(194); 
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
			setState(197); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(196); 
				match(REQUIRE_NAME);
				}
				}
				setState(199); 
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
			setState(201); 
			match(T__0);
			setState(202); 
			match(T__5);
			setState(204); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(203); 
				one_def();
				}
				}
				setState(206); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(208); 
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
			setState(210); 
			new_types();
			setState(213);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(211); 
				match(T__6);
				setState(212); 
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
			setState(216); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(215); 
					match(NAME);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(218); 
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
			setState(220); 
			match(T__0);
			setState(221); 
			match(T__7);
			setState(222); 
			typed_obj_list();
			setState(223); 
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
		enterRule(_localctx, 18, RULE_funtions_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225); 
			match(T__0);
			setState(226); 
			match(T__8);
			setState(232); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(232);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(227); 
					atomic_formula_skeleton();
					setState(228); 
					match(T__6);
					setState(229); 
					match(T__9);
					}
					break;
				case NAME:
					{
					setState(231); 
					var_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(234); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==NAME );
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
		enterRule(_localctx, 20, RULE_predicates_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238); 
			match(T__0);
			setState(239); 
			match(T__10);
			setState(241); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(240); 
				atomic_formula_skeleton();
				}
				}
				setState(243); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(245); 
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
		enterRule(_localctx, 22, RULE_atomic_formula_skeleton);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247); 
			match(T__0);
			setState(248); 
			predicate();
			setState(249); 
			typed_var_list();
			setState(250); 
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
			setState(252); 
			match(T__0);
			setState(253); 
			match(T__11);
			setState(254); 
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
			setState(256); 
			task_symbol();
			setState(257); 
			match(T__12);
			setState(258); 
			match(T__0);
			setState(259); 
			typed_var_list();
			setState(260); 
			match(T__3);
			setState(263);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(261); 
				match(T__13);
				setState(262); 
				gd();
				}
			}

			setState(267);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(265); 
				match(T__14);
				setState(266); 
				effect_body();
				}
			}

			setState(269); 
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
			setState(271); 
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
			setState(273); 
			match(T__0);
			setState(274); 
			match(T__15);
			setState(275); 
			method_symbol();
			setState(276); 
			match(T__12);
			setState(277); 
			match(T__0);
			setState(278); 
			typed_var_list();
			setState(279); 
			match(T__3);
			setState(280); 
			match(T__11);
			setState(281); 
			match(T__0);
			setState(282); 
			task_symbol();
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(283); 
				var_or_const();
				}
				}
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(289); 
			match(T__3);
			setState(292);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(290); 
				match(T__13);
				setState(291); 
				gd();
				}
			}

			setState(294); 
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
			setState(298);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(296);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(297); 
				subtask_defs();
				}
			}

			setState(302);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(300);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(301); 
				ordering_defs();
				}
			}

			setState(306);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(304); 
				match(T__22);
				setState(305); 
				constraint_defs();
				}
			}

			setState(308); 
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
			setState(310); 
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
			setState(324);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(312); 
				match(T__0);
				setState(313); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(314); 
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(315); 
				match(T__0);
				setState(316); 
				match(T__23);
				setState(318); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(317); 
					subtask_def();
					}
					}
					setState(320); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(322); 
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
			setState(349);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(326); 
				match(T__0);
				setState(327); 
				task_symbol();
				setState(331);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(328); 
					var_or_const();
					}
					}
					setState(333);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(334); 
				match(T__3);
				}
				break;
			case 2:
				{
				setState(336); 
				match(T__0);
				setState(337); 
				subtask_id();
				setState(338); 
				match(T__0);
				setState(339); 
				task_symbol();
				setState(343);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(340); 
					var_or_const();
					}
					}
					setState(345);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(346); 
				match(T__3);
				setState(347); 
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
			setState(351); 
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
			setState(365);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(353); 
				match(T__0);
				setState(354); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(355); 
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(356); 
				match(T__0);
				setState(357); 
				match(T__23);
				setState(359); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(358); 
					ordering_def();
					}
					}
					setState(361); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(363); 
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
			setState(367); 
			match(T__0);
			setState(368); 
			subtask_id();
			setState(369); 
			match(T__24);
			setState(370); 
			subtask_id();
			setState(371); 
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
			setState(385);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(373); 
				match(T__0);
				setState(374); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(375); 
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(376); 
				match(T__0);
				setState(377); 
				match(T__23);
				setState(379); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(378); 
					constraint_def();
					}
					}
					setState(381); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__41 );
				setState(383); 
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
			setState(402);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(387); 
				match(T__0);
				setState(388); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(389); 
				match(T__0);
				setState(390); 
				match(T__25);
				setState(391); 
				equallity();
				setState(392); 
				var_or_const();
				setState(393); 
				var_or_const();
				setState(394); 
				match(T__3);
				setState(395); 
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(397); 
				equallity();
				setState(398); 
				var_or_const();
				setState(399); 
				var_or_const();
				setState(400); 
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
		enterRule(_localctx, 50, RULE_action_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404); 
			match(T__0);
			setState(405); 
			match(T__26);
			setState(406); 
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
		enterRule(_localctx, 52, RULE_gd);
		try {
			setState(416);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(408); 
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(409); 
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(410); 
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(411); 
				gd_conjuction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(412); 
				gd_disjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(413); 
				gd_existential();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(414); 
				gd_univeral();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(415); 
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
		enterRule(_localctx, 54, RULE_gd_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418); 
			match(T__0);
			setState(419); 
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
		enterRule(_localctx, 56, RULE_gd_conjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421); 
			match(T__0);
			setState(422); 
			match(T__23);
			setState(424); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(423); 
				gd();
				}
				}
				setState(426); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__28) | (1L << T__29) | (1L << T__41))) != 0) );
			setState(428); 
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
		enterRule(_localctx, 58, RULE_gd_disjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430); 
			match(T__0);
			setState(431); 
			match(T__27);
			setState(433); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(432); 
				gd();
				}
				}
				setState(435); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__28) | (1L << T__29) | (1L << T__41))) != 0) );
			setState(437); 
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
		enterRule(_localctx, 60, RULE_gd_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439); 
			match(T__0);
			setState(440); 
			match(T__25);
			setState(441); 
			gd();
			setState(442); 
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
		enterRule(_localctx, 62, RULE_gd_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444); 
			match(T__28);
			setState(445); 
			match(T__0);
			setState(446); 
			typed_var_list();
			setState(447); 
			match(T__3);
			setState(448); 
			gd();
			setState(449); 
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
		enterRule(_localctx, 64, RULE_gd_univeral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451); 
			match(T__29);
			setState(452); 
			match(T__0);
			setState(453); 
			typed_var_list();
			setState(454); 
			match(T__3);
			setState(455); 
			gd();
			setState(456); 
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
		enterRule(_localctx, 66, RULE_gd_equality_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(458); 
			equallity();
			setState(459); 
			var_or_const();
			setState(460); 
			var_or_const();
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
		enterRule(_localctx, 68, RULE_effect_body);
		try {
			setState(466);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(463); 
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(464); 
				c_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(465); 
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
		enterRule(_localctx, 70, RULE_eff_conjuntion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468); 
			match(T__0);
			setState(469); 
			match(T__23);
			setState(471); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(470); 
				c_effect();
				}
				}
				setState(473); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
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

	public static class Eff_emptyContext extends ParserRuleContext {
		public Eff_emptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eff_empty; }
	}

	public final Eff_emptyContext eff_empty() throws RecognitionException {
		Eff_emptyContext _localctx = new Eff_emptyContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477); 
			match(T__0);
			setState(478); 
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
		enterRule(_localctx, 74, RULE_c_effect);
		try {
			setState(484);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(480); 
				p_effect();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(481); 
				literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(482); 
				forall_effect();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(483); 
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
		enterRule(_localctx, 76, RULE_forall_effect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(486); 
			match(T__0);
			setState(487); 
			match(T__30);
			setState(488); 
			match(T__0);
			setState(492);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(489); 
				var_or_const();
				}
				}
				setState(494);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(495); 
			match(T__3);
			setState(496); 
			effect_body();
			setState(497); 
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
		enterRule(_localctx, 78, RULE_conditional_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(499); 
			match(T__0);
			setState(500); 
			match(T__31);
			setState(501); 
			gd();
			setState(502); 
			cond_effect();
			setState(503); 
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
		enterRule(_localctx, 80, RULE_literal);
		try {
			setState(507);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(505); 
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(506); 
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
		enterRule(_localctx, 82, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509); 
			match(T__0);
			setState(510); 
			match(T__25);
			setState(511); 
			atomic_formula();
			setState(512); 
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
		enterRule(_localctx, 84, RULE_cond_effect);
		int _la;
		try {
			setState(524);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(514); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(515); 
				match(T__0);
				setState(516); 
				match(T__23);
				setState(518); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(517); 
					literal();
					}
					}
					setState(520); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(522); 
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
		enterRule(_localctx, 86, RULE_p_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526); 
			match(T__0);
			setState(527); 
			assign_op();
			setState(528); 
			f_head();
			setState(529); 
			f_exp();
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

	public static class Assign_opContext extends ParserRuleContext {
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		enterRule(_localctx, 90, RULE_f_head);
		int _la;
		try {
			setState(545);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(534); 
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(535); 
				match(T__0);
				setState(536); 
				func_symbol();
				setState(540);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << VAR_NAME) | (1L << NAME))) != 0)) {
					{
					{
					setState(537); 
					term();
					}
					}
					setState(542);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(543); 
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
		enterRule(_localctx, 92, RULE_f_exp);
		int _la;
		try {
			setState(570);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(547); 
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(548); 
				match(T__0);
				setState(549); 
				bin_op();
				setState(550); 
				f_exp();
				setState(551); 
				f_exp();
				setState(552); 
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(554); 
				match(T__0);
				setState(555); 
				multi_op();
				setState(556); 
				f_exp();
				setState(558); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(557); 
					f_exp();
					}
					}
					setState(560); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << NAME) | (1L << NUMBER))) != 0) );
				setState(562); 
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(564); 
				match(T__0);
				setState(565); 
				match(T__6);
				setState(566); 
				f_exp();
				setState(567); 
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(569); 
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
		enterRule(_localctx, 94, RULE_bin_op);
		try {
			setState(575);
			switch (_input.LA(1)) {
			case T__38:
			case T__39:
				enterOuterAlt(_localctx, 1);
				{
				setState(572); 
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(573); 
				match(T__6);
				}
				break;
			case T__37:
				enterOuterAlt(_localctx, 3);
				{
				setState(574); 
				match(T__37);
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
		enterRule(_localctx, 96, RULE_multi_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			_la = _input.LA(1);
			if ( !(_la==T__38 || _la==T__39) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		enterRule(_localctx, 98, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(579); 
			match(T__0);
			setState(580); 
			predicate();
			setState(584);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(581); 
				var_or_const();
				}
				}
				setState(586);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(587); 
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
		enterRule(_localctx, 100, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589); 
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
		enterRule(_localctx, 102, RULE_equallity);
		try {
			setState(594);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(591); 
				match(T__0);
				setState(592); 
				match(T__40);
				}
				break;
			case T__41:
				enterOuterAlt(_localctx, 2);
				{
				setState(593); 
				match(T__41);
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
		enterRule(_localctx, 104, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(599);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(596); 
				typed_vars();
				}
				}
				setState(601);
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
		enterRule(_localctx, 106, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(605);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(602); 
				typed_objs();
				}
				}
				setState(607);
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
		enterRule(_localctx, 108, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(608); 
				match(VAR_NAME);
				}
				}
				setState(611); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(613); 
			match(T__6);
			setState(614); 
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
		enterRule(_localctx, 110, RULE_typed_objs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(616); 
				new_consts();
				}
				}
				setState(619); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(621); 
			match(T__6);
			setState(622); 
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
		enterRule(_localctx, 112, RULE_new_consts);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(624); 
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
		enterRule(_localctx, 114, RULE_var_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626); 
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
		enterRule(_localctx, 116, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
			_la = _input.LA(1);
			if ( !(_la==VAR_NAME || _la==NAME) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		enterRule(_localctx, 118, RULE_term);
		try {
			setState(633);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(630); 
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(631); 
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(632); 
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
		enterRule(_localctx, 120, RULE_functionterm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(635); 
			match(T__0);
			setState(636); 
			func_symbol();
			setState(640);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << VAR_NAME) | (1L << NAME))) != 0)) {
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
		enterRule(_localctx, 122, RULE_func_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(645); 
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
		enterRule(_localctx, 124, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(647); 
			match(T__0);
			setState(648); 
			match(T__1);
			setState(649); 
			match(T__0);
			setState(650); 
			match(T__42);
			setState(651); 
			match(NAME);
			setState(652); 
			match(T__3);
			setState(653); 
			match(T__0);
			setState(654); 
			match(T__43);
			setState(655); 
			match(NAME);
			setState(656); 
			match(T__3);
			setState(658);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(657); 
				require_def();
				}
				break;
			}
			setState(661);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(660); 
				p_object_declaration();
				}
				break;
			}
			setState(664);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(663); 
				p_htn();
				}
				break;
			}
			setState(666); 
			p_init();
			setState(668);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(667); 
				p_goal();
				}
				break;
			}
			setState(671);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(670); 
				metric_spec();
				}
			}

			setState(673); 
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
		enterRule(_localctx, 126, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(675); 
			match(T__0);
			setState(676); 
			match(T__44);
			setState(677); 
			typed_obj_list();
			setState(678); 
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
		enterRule(_localctx, 128, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680); 
			match(T__0);
			setState(681); 
			match(T__45);
			setState(685);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__41) {
				{
				{
				setState(682); 
				init_el();
				}
				}
				setState(687);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(688); 
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
		enterRule(_localctx, 130, RULE_init_el);
		try {
			setState(692);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(690); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(691); 
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
		enterRule(_localctx, 132, RULE_num_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(694); 
			equallity();
			setState(695); 
			f_head();
			setState(696); 
			match(NUMBER);
			setState(697); 
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
		enterRule(_localctx, 134, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699); 
			match(T__0);
			setState(700); 
			match(T__46);
			setState(701); 
			gd();
			setState(702); 
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
		enterRule(_localctx, 136, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(704); 
			match(T__0);
			setState(705);
			_la = _input.LA(1);
			if ( !(_la==T__47 || _la==T__48) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(706); 
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
		enterRule(_localctx, 138, RULE_metric_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708); 
			match(T__0);
			setState(709); 
			match(T__49);
			setState(710); 
			optimization();
			setState(711); 
			ground_f_exp();
			setState(712); 
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
		enterRule(_localctx, 140, RULE_optimization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(714);
			_la = _input.LA(1);
			if ( !(_la==T__50 || _la==T__51) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		enterRule(_localctx, 142, RULE_ground_f_exp);
		int _la;
		try {
			setState(743);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(716); 
				match(T__0);
				setState(717); 
				bin_op();
				setState(718); 
				ground_f_exp();
				setState(719); 
				ground_f_exp();
				setState(720); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(725);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(722); 
					match(T__0);
					setState(723); 
					match(T__6);
					}
					break;
				case T__52:
					{
					setState(724); 
					match(T__52);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(727); 
				ground_f_exp();
				setState(728); 
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(730); 
				match(NUMBER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(731); 
				match(T__0);
				setState(732); 
				func_symbol();
				setState(736);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(733); 
					match(NAME);
					}
					}
					setState(738);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(739); 
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(741); 
				match(T__53);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(742); 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3>\u02ec\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\3\2\3\2\5\2\u0095\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u009e\n\3\3"+
		"\3\5\3\u00a1\n\3\3\3\5\3\u00a4\n\3\3\3\5\3\u00a7\n\3\3\3\5\3\u00aa\n\3"+
		"\3\3\7\3\u00ad\n\3\f\3\16\3\u00b0\13\3\3\3\7\3\u00b3\n\3\f\3\16\3\u00b6"+
		"\13\3\3\3\7\3\u00b9\n\3\f\3\16\3\u00bc\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\6\6\6\u00c8\n\6\r\6\16\6\u00c9\3\7\3\7\3\7\6\7\u00cf\n\7"+
		"\r\7\16\7\u00d0\3\7\3\7\3\b\3\b\3\b\5\b\u00d8\n\b\3\t\6\t\u00db\n\t\r"+
		"\t\16\t\u00dc\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\6"+
		"\13\u00eb\n\13\r\13\16\13\u00ec\3\13\3\13\3\f\3\f\3\f\6\f\u00f4\n\f\r"+
		"\f\16\f\u00f5\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\5\17\u010a\n\17\3\17\3\17\5\17\u010e\n\17\3"+
		"\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\7\21\u011f\n\21\f\21\16\21\u0122\13\21\3\21\3\21\3\21\5\21\u0127\n"+
		"\21\3\21\3\21\3\22\3\22\5\22\u012d\n\22\3\22\3\22\5\22\u0131\n\22\3\22"+
		"\3\22\5\22\u0135\n\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\6\24\u0141\n\24\r\24\16\24\u0142\3\24\3\24\5\24\u0147\n\24\3\25\3\25"+
		"\3\25\7\25\u014c\n\25\f\25\16\25\u014f\13\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\7\25\u0158\n\25\f\25\16\25\u015b\13\25\3\25\3\25\3\25\5\25"+
		"\u0160\n\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\6\27\u016a\n\27\r"+
		"\27\16\27\u016b\3\27\3\27\5\27\u0170\n\27\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\6\31\u017e\n\31\r\31\16\31\u017f\3\31\3"+
		"\31\5\31\u0184\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u0195\n\32\3\33\3\33\3\33\3\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u01a3\n\34\3\35\3\35\3\35\3\36\3\36"+
		"\3\36\6\36\u01ab\n\36\r\36\16\36\u01ac\3\36\3\36\3\37\3\37\3\37\6\37\u01b4"+
		"\n\37\r\37\16\37\u01b5\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3$\3$\3$\5$\u01d5\n$\3%\3%\3"+
		"%\6%\u01da\n%\r%\16%\u01db\3%\3%\3&\3&\3&\3\'\3\'\3\'\3\'\5\'\u01e7\n"+
		"\'\3(\3(\3(\3(\7(\u01ed\n(\f(\16(\u01f0\13(\3(\3(\3(\3(\3)\3)\3)\3)\3"+
		")\3)\3*\3*\5*\u01fe\n*\3+\3+\3+\3+\3+\3,\3,\3,\3,\6,\u0209\n,\r,\16,\u020a"+
		"\3,\3,\5,\u020f\n,\3-\3-\3-\3-\3-\3-\3.\3.\3/\3/\3/\3/\7/\u021d\n/\f/"+
		"\16/\u0220\13/\3/\3/\5/\u0224\n/\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3"+
		"\60\3\60\3\60\3\60\6\60\u0231\n\60\r\60\16\60\u0232\3\60\3\60\3\60\3\60"+
		"\3\60\3\60\3\60\3\60\5\60\u023d\n\60\3\61\3\61\3\61\5\61\u0242\n\61\3"+
		"\62\3\62\3\63\3\63\3\63\7\63\u0249\n\63\f\63\16\63\u024c\13\63\3\63\3"+
		"\63\3\64\3\64\3\65\3\65\3\65\5\65\u0255\n\65\3\66\7\66\u0258\n\66\f\66"+
		"\16\66\u025b\13\66\3\67\7\67\u025e\n\67\f\67\16\67\u0261\13\67\38\68\u0264"+
		"\n8\r8\168\u0265\38\38\38\39\69\u026c\n9\r9\169\u026d\39\39\39\3:\3:\3"+
		";\3;\3<\3<\3=\3=\3=\5=\u027c\n=\3>\3>\3>\7>\u0281\n>\f>\16>\u0284\13>"+
		"\3>\3>\3?\3?\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\5@\u0295\n@\3@\5@\u0298"+
		"\n@\3@\5@\u029b\n@\3@\3@\5@\u029f\n@\3@\5@\u02a2\n@\3@\3@\3A\3A\3A\3A"+
		"\3A\3B\3B\3B\7B\u02ae\nB\fB\16B\u02b1\13B\3B\3B\3C\3C\5C\u02b7\nC\3D\3"+
		"D\3D\3D\3D\3E\3E\3E\3E\3E\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3H\3H\3I\3I\3"+
		"I\3I\3I\3I\3I\3I\3I\5I\u02d8\nI\3I\3I\3I\3I\3I\3I\3I\7I\u02e1\nI\fI\16"+
		"I\u02e4\13I\3I\3I\3I\3I\5I\u02ea\nI\3I\2\2J\2\4\6\b\n\f\16\20\22\24\26"+
		"\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|"+
		"~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\2\t\3\2\23\26"+
		"\3\2\27\30\3\2#\'\3\2)*\3\2:;\3\2\62\63\3\2\65\66\u02fb\2\u0094\3\2\2"+
		"\2\4\u0096\3\2\2\2\6\u00bf\3\2\2\2\b\u00c1\3\2\2\2\n\u00c7\3\2\2\2\f\u00cb"+
		"\3\2\2\2\16\u00d4\3\2\2\2\20\u00da\3\2\2\2\22\u00de\3\2\2\2\24\u00e3\3"+
		"\2\2\2\26\u00f0\3\2\2\2\30\u00f9\3\2\2\2\32\u00fe\3\2\2\2\34\u0102\3\2"+
		"\2\2\36\u0111\3\2\2\2 \u0113\3\2\2\2\"\u012c\3\2\2\2$\u0138\3\2\2\2&\u0146"+
		"\3\2\2\2(\u015f\3\2\2\2*\u0161\3\2\2\2,\u016f\3\2\2\2.\u0171\3\2\2\2\60"+
		"\u0183\3\2\2\2\62\u0194\3\2\2\2\64\u0196\3\2\2\2\66\u01a2\3\2\2\28\u01a4"+
		"\3\2\2\2:\u01a7\3\2\2\2<\u01b0\3\2\2\2>\u01b9\3\2\2\2@\u01be\3\2\2\2B"+
		"\u01c5\3\2\2\2D\u01cc\3\2\2\2F\u01d4\3\2\2\2H\u01d6\3\2\2\2J\u01df\3\2"+
		"\2\2L\u01e6\3\2\2\2N\u01e8\3\2\2\2P\u01f5\3\2\2\2R\u01fd\3\2\2\2T\u01ff"+
		"\3\2\2\2V\u020e\3\2\2\2X\u0210\3\2\2\2Z\u0216\3\2\2\2\\\u0223\3\2\2\2"+
		"^\u023c\3\2\2\2`\u0241\3\2\2\2b\u0243\3\2\2\2d\u0245\3\2\2\2f\u024f\3"+
		"\2\2\2h\u0254\3\2\2\2j\u0259\3\2\2\2l\u025f\3\2\2\2n\u0263\3\2\2\2p\u026b"+
		"\3\2\2\2r\u0272\3\2\2\2t\u0274\3\2\2\2v\u0276\3\2\2\2x\u027b\3\2\2\2z"+
		"\u027d\3\2\2\2|\u0287\3\2\2\2~\u0289\3\2\2\2\u0080\u02a5\3\2\2\2\u0082"+
		"\u02aa\3\2\2\2\u0084\u02b6\3\2\2\2\u0086\u02b8\3\2\2\2\u0088\u02bd\3\2"+
		"\2\2\u008a\u02c2\3\2\2\2\u008c\u02c6\3\2\2\2\u008e\u02cc\3\2\2\2\u0090"+
		"\u02e9\3\2\2\2\u0092\u0095\5\4\3\2\u0093\u0095\5~@\2\u0094\u0092\3\2\2"+
		"\2\u0094\u0093\3\2\2\2\u0095\3\3\2\2\2\u0096\u0097\7\3\2\2\u0097\u0098"+
		"\7\4\2\2\u0098\u0099\7\3\2\2\u0099\u009a\7\5\2\2\u009a\u009b\5\6\4\2\u009b"+
		"\u009d\7\6\2\2\u009c\u009e\5\b\5\2\u009d\u009c\3\2\2\2\u009d\u009e\3\2"+
		"\2\2\u009e\u00a0\3\2\2\2\u009f\u00a1\5\f\7\2\u00a0\u009f\3\2\2\2\u00a0"+
		"\u00a1\3\2\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a4\5\22\n\2\u00a3\u00a2\3"+
		"\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a7\5\26\f\2\u00a6"+
		"\u00a5\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00aa\5\24"+
		"\13\2\u00a9\u00a8\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ae\3\2\2\2\u00ab"+
		"\u00ad\5\32\16\2\u00ac\u00ab\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3"+
		"\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b4\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1"+
		"\u00b3\5 \21\2\u00b2\u00b1\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2"+
		"\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00ba\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7"+
		"\u00b9\5\64\33\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3"+
		"\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd"+
		"\u00be\7\6\2\2\u00be\5\3\2\2\2\u00bf\u00c0\7;\2\2\u00c0\7\3\2\2\2\u00c1"+
		"\u00c2\7\3\2\2\u00c2\u00c3\7\7\2\2\u00c3\u00c4\5\n\6\2\u00c4\u00c5\7\6"+
		"\2\2\u00c5\t\3\2\2\2\u00c6\u00c8\79\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00c9"+
		"\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\13\3\2\2\2\u00cb"+
		"\u00cc\7\3\2\2\u00cc\u00ce\7\b\2\2\u00cd\u00cf\5\16\b\2\u00ce\u00cd\3"+
		"\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d3\7\6\2\2\u00d3\r\3\2\2\2\u00d4\u00d7\5\20\t"+
		"\2\u00d5\u00d6\7\t\2\2\u00d6\u00d8\5t;\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8"+
		"\3\2\2\2\u00d8\17\3\2\2\2\u00d9\u00db\7;\2\2\u00da\u00d9\3\2\2\2\u00db"+
		"\u00dc\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\21\3\2\2"+
		"\2\u00de\u00df\7\3\2\2\u00df\u00e0\7\n\2\2\u00e0\u00e1\5l\67\2\u00e1\u00e2"+
		"\7\6\2\2\u00e2\23\3\2\2\2\u00e3\u00e4\7\3\2\2\u00e4\u00ea\7\13\2\2\u00e5"+
		"\u00e6\5\30\r\2\u00e6\u00e7\7\t\2\2\u00e7\u00e8\7\f\2\2\u00e8\u00eb\3"+
		"\2\2\2\u00e9\u00eb\5t;\2\u00ea\u00e5\3\2\2\2\u00ea\u00e9\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\3\2"+
		"\2\2\u00ee\u00ef\7\6\2\2\u00ef\25\3\2\2\2\u00f0\u00f1\7\3\2\2\u00f1\u00f3"+
		"\7\r\2\2\u00f2\u00f4\5\30\r\2\u00f3\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2"+
		"\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8"+
		"\7\6\2\2\u00f8\27\3\2\2\2\u00f9\u00fa\7\3\2\2\u00fa\u00fb\5f\64\2\u00fb"+
		"\u00fc\5j\66\2\u00fc\u00fd\7\6\2\2\u00fd\31\3\2\2\2\u00fe\u00ff\7\3\2"+
		"\2\u00ff\u0100\7\16\2\2\u0100\u0101\5\34\17\2\u0101\33\3\2\2\2\u0102\u0103"+
		"\5\36\20\2\u0103\u0104\7\17\2\2\u0104\u0105\7\3\2\2\u0105\u0106\5j\66"+
		"\2\u0106\u0109\7\6\2\2\u0107\u0108\7\20\2\2\u0108\u010a\5\66\34\2\u0109"+
		"\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u010c\7\21"+
		"\2\2\u010c\u010e\5F$\2\u010d\u010b\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u010f"+
		"\3\2\2\2\u010f\u0110\7\6\2\2\u0110\35\3\2\2\2\u0111\u0112\7;\2\2\u0112"+
		"\37\3\2\2\2\u0113\u0114\7\3\2\2\u0114\u0115\7\22\2\2\u0115\u0116\5$\23"+
		"\2\u0116\u0117\7\17\2\2\u0117\u0118\7\3\2\2\u0118\u0119\5j\66\2\u0119"+
		"\u011a\7\6\2\2\u011a\u011b\7\16\2\2\u011b\u011c\7\3\2\2\u011c\u0120\5"+
		"\36\20\2\u011d\u011f\5v<\2\u011e\u011d\3\2\2\2\u011f\u0122\3\2\2\2\u0120"+
		"\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0123\3\2\2\2\u0122\u0120\3\2"+
		"\2\2\u0123\u0126\7\6\2\2\u0124\u0125\7\20\2\2\u0125\u0127\5\66\34\2\u0126"+
		"\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\5\""+
		"\22\2\u0129!\3\2\2\2\u012a\u012b\t\2\2\2\u012b\u012d\5&\24\2\u012c\u012a"+
		"\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012f\t\3\2\2\u012f"+
		"\u0131\5,\27\2\u0130\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0134\3\2"+
		"\2\2\u0132\u0133\7\31\2\2\u0133\u0135\5\60\31\2\u0134\u0132\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\7\6\2\2\u0137#\3\2\2\2"+
		"\u0138\u0139\7;\2\2\u0139%\3\2\2\2\u013a\u013b\7\3\2\2\u013b\u0147\7\6"+
		"\2\2\u013c\u0147\5(\25\2\u013d\u013e\7\3\2\2\u013e\u0140\7\32\2\2\u013f"+
		"\u0141\5(\25\2\u0140\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0142\u0143\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\7\6\2\2\u0145"+
		"\u0147\3\2\2\2\u0146\u013a\3\2\2\2\u0146\u013c\3\2\2\2\u0146\u013d\3\2"+
		"\2\2\u0147\'\3\2\2\2\u0148\u0149\7\3\2\2\u0149\u014d\5\36\20\2\u014a\u014c"+
		"\5v<\2\u014b\u014a\3\2\2\2\u014c\u014f\3\2\2\2\u014d\u014b\3\2\2\2\u014d"+
		"\u014e\3\2\2\2\u014e\u0150\3\2\2\2\u014f\u014d\3\2\2\2\u0150\u0151\7\6"+
		"\2\2\u0151\u0160\3\2\2\2\u0152\u0153\7\3\2\2\u0153\u0154\5*\26\2\u0154"+
		"\u0155\7\3\2\2\u0155\u0159\5\36\20\2\u0156\u0158\5v<\2\u0157\u0156\3\2"+
		"\2\2\u0158\u015b\3\2\2\2\u0159\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a"+
		"\u015c\3\2\2\2\u015b\u0159\3\2\2\2\u015c\u015d\7\6\2\2\u015d\u015e\7\6"+
		"\2\2\u015e\u0160\3\2\2\2\u015f\u0148\3\2\2\2\u015f\u0152\3\2\2\2\u0160"+
		")\3\2\2\2\u0161\u0162\7;\2\2\u0162+\3\2\2\2\u0163\u0164\7\3\2\2\u0164"+
		"\u0170\7\6\2\2\u0165\u0170\5.\30\2\u0166\u0167\7\3\2\2\u0167\u0169\7\32"+
		"\2\2\u0168\u016a\5.\30\2\u0169\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b"+
		"\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016e\7\6"+
		"\2\2\u016e\u0170\3\2\2\2\u016f\u0163\3\2\2\2\u016f\u0165\3\2\2\2\u016f"+
		"\u0166\3\2\2\2\u0170-\3\2\2\2\u0171\u0172\7\3\2\2\u0172\u0173\5*\26\2"+
		"\u0173\u0174\7\33\2\2\u0174\u0175\5*\26\2\u0175\u0176\7\6\2\2\u0176/\3"+
		"\2\2\2\u0177\u0178\7\3\2\2\u0178\u0184\7\6\2\2\u0179\u0184\5\62\32\2\u017a"+
		"\u017b\7\3\2\2\u017b\u017d\7\32\2\2\u017c\u017e\5\62\32\2\u017d\u017c"+
		"\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180"+
		"\u0181\3\2\2\2\u0181\u0182\7\6\2\2\u0182\u0184\3\2\2\2\u0183\u0177\3\2"+
		"\2\2\u0183\u0179\3\2\2\2\u0183\u017a\3\2\2\2\u0184\61\3\2\2\2\u0185\u0186"+
		"\7\3\2\2\u0186\u0195\7\6\2\2\u0187\u0188\7\3\2\2\u0188\u0189\7\34\2\2"+
		"\u0189\u018a\5h\65\2\u018a\u018b\5v<\2\u018b\u018c\5v<\2\u018c\u018d\7"+
		"\6\2\2\u018d\u018e\7\6\2\2\u018e\u0195\3\2\2\2\u018f\u0190\5h\65\2\u0190"+
		"\u0191\5v<\2\u0191\u0192\5v<\2\u0192\u0193\7\6\2\2\u0193\u0195\3\2\2\2"+
		"\u0194\u0185\3\2\2\2\u0194\u0187\3\2\2\2\u0194\u018f\3\2\2\2\u0195\63"+
		"\3\2\2\2\u0196\u0197\7\3\2\2\u0197\u0198\7\35\2\2\u0198\u0199\5\34\17"+
		"\2\u0199\65\3\2\2\2\u019a\u01a3\58\35\2\u019b\u01a3\5d\63\2\u019c\u01a3"+
		"\5> \2\u019d\u01a3\5:\36\2\u019e\u01a3\5<\37\2\u019f\u01a3\5@!\2\u01a0"+
		"\u01a3\5B\"\2\u01a1\u01a3\5D#\2\u01a2\u019a\3\2\2\2\u01a2\u019b\3\2\2"+
		"\2\u01a2\u019c\3\2\2\2\u01a2\u019d\3\2\2\2\u01a2\u019e\3\2\2\2\u01a2\u019f"+
		"\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a1\3\2\2\2\u01a3\67\3\2\2\2\u01a4"+
		"\u01a5\7\3\2\2\u01a5\u01a6\7\6\2\2\u01a69\3\2\2\2\u01a7\u01a8\7\3\2\2"+
		"\u01a8\u01aa\7\32\2\2\u01a9\u01ab\5\66\34\2\u01aa\u01a9\3\2\2\2\u01ab"+
		"\u01ac\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01ae\3\2"+
		"\2\2\u01ae\u01af\7\6\2\2\u01af;\3\2\2\2\u01b0\u01b1\7\3\2\2\u01b1\u01b3"+
		"\7\36\2\2\u01b2\u01b4\5\66\34\2\u01b3\u01b2\3\2\2\2\u01b4\u01b5\3\2\2"+
		"\2\u01b5\u01b3\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8"+
		"\7\6\2\2\u01b8=\3\2\2\2\u01b9\u01ba\7\3\2\2\u01ba\u01bb\7\34\2\2\u01bb"+
		"\u01bc\5\66\34\2\u01bc\u01bd\7\6\2\2\u01bd?\3\2\2\2\u01be\u01bf\7\37\2"+
		"\2\u01bf\u01c0\7\3\2\2\u01c0\u01c1\5j\66\2\u01c1\u01c2\7\6\2\2\u01c2\u01c3"+
		"\5\66\34\2\u01c3\u01c4\7\6\2\2\u01c4A\3\2\2\2\u01c5\u01c6\7 \2\2\u01c6"+
		"\u01c7\7\3\2\2\u01c7\u01c8\5j\66\2\u01c8\u01c9\7\6\2\2\u01c9\u01ca\5\66"+
		"\34\2\u01ca\u01cb\7\6\2\2\u01cbC\3\2\2\2\u01cc\u01cd\5h\65\2\u01cd\u01ce"+
		"\5v<\2\u01ce\u01cf\5v<\2\u01cf\u01d0\7\6\2\2\u01d0E\3\2\2\2\u01d1\u01d5"+
		"\5J&\2\u01d2\u01d5\5L\'\2\u01d3\u01d5\5H%\2\u01d4\u01d1\3\2\2\2\u01d4"+
		"\u01d2\3\2\2\2\u01d4\u01d3\3\2\2\2\u01d5G\3\2\2\2\u01d6\u01d7\7\3\2\2"+
		"\u01d7\u01d9\7\32\2\2\u01d8\u01da\5L\'\2\u01d9\u01d8\3\2\2\2\u01da\u01db"+
		"\3\2\2\2\u01db\u01d9\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd"+
		"\u01de\7\6\2\2\u01deI\3\2\2\2\u01df\u01e0\7\3\2\2\u01e0\u01e1\7\6\2\2"+
		"\u01e1K\3\2\2\2\u01e2\u01e7\5X-\2\u01e3\u01e7\5R*\2\u01e4\u01e7\5N(\2"+
		"\u01e5\u01e7\5P)\2\u01e6\u01e2\3\2\2\2\u01e6\u01e3\3\2\2\2\u01e6\u01e4"+
		"\3\2\2\2\u01e6\u01e5\3\2\2\2\u01e7M\3\2\2\2\u01e8\u01e9\7\3\2\2\u01e9"+
		"\u01ea\7!\2\2\u01ea\u01ee\7\3\2\2\u01eb\u01ed\5v<\2\u01ec\u01eb\3\2\2"+
		"\2\u01ed\u01f0\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ee\u01ef\3\2\2\2\u01ef\u01f1"+
		"\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f1\u01f2\7\6\2\2\u01f2\u01f3\5F$\2\u01f3"+
		"\u01f4\7\6\2\2\u01f4O\3\2\2\2\u01f5\u01f6\7\3\2\2\u01f6\u01f7\7\"\2\2"+
		"\u01f7\u01f8\5\66\34\2\u01f8\u01f9\5V,\2\u01f9\u01fa\7\6\2\2\u01faQ\3"+
		"\2\2\2\u01fb\u01fe\5T+\2\u01fc\u01fe\5d\63\2\u01fd\u01fb\3\2\2\2\u01fd"+
		"\u01fc\3\2\2\2\u01feS\3\2\2\2\u01ff\u0200\7\3\2\2\u0200\u0201\7\34\2\2"+
		"\u0201\u0202\5d\63\2\u0202\u0203\7\6\2\2\u0203U\3\2\2\2\u0204\u020f\5"+
		"R*\2\u0205\u0206\7\3\2\2\u0206\u0208\7\32\2\2\u0207\u0209\5R*\2\u0208"+
		"\u0207\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2"+
		"\2\2\u020b\u020c\3\2\2\2\u020c\u020d\7\6\2\2\u020d\u020f\3\2\2\2\u020e"+
		"\u0204\3\2\2\2\u020e\u0205\3\2\2\2\u020fW\3\2\2\2\u0210\u0211\7\3\2\2"+
		"\u0211\u0212\5Z.\2\u0212\u0213\5\\/\2\u0213\u0214\5^\60\2\u0214\u0215"+
		"\7\6\2\2\u0215Y\3\2\2\2\u0216\u0217\t\4\2\2\u0217[\3\2\2\2\u0218\u0224"+
		"\5|?\2\u0219\u021a\7\3\2\2\u021a\u021e\5|?\2\u021b\u021d\5x=\2\u021c\u021b"+
		"\3\2\2\2\u021d\u0220\3\2\2\2\u021e\u021c\3\2\2\2\u021e\u021f\3\2\2\2\u021f"+
		"\u0221\3\2\2\2\u0220\u021e\3\2\2\2\u0221\u0222\7\6\2\2\u0222\u0224\3\2"+
		"\2\2\u0223\u0218\3\2\2\2\u0223\u0219\3\2\2\2\u0224]\3\2\2\2\u0225\u023d"+
		"\7>\2\2\u0226\u0227\7\3\2\2\u0227\u0228\5`\61\2\u0228\u0229\5^\60\2\u0229"+
		"\u022a\5^\60\2\u022a\u022b\7\6\2\2\u022b\u023d\3\2\2\2\u022c\u022d\7\3"+
		"\2\2\u022d\u022e\5b\62\2\u022e\u0230\5^\60\2\u022f\u0231\5^\60\2\u0230"+
		"\u022f\3\2\2\2\u0231\u0232\3\2\2\2\u0232\u0230\3\2\2\2\u0232\u0233\3\2"+
		"\2\2\u0233\u0234\3\2\2\2\u0234\u0235\7\6\2\2\u0235\u023d\3\2\2\2\u0236"+
		"\u0237\7\3\2\2\u0237\u0238\7\t\2\2\u0238\u0239\5^\60\2\u0239\u023a\7\6"+
		"\2\2\u023a\u023d\3\2\2\2\u023b\u023d\5\\/\2\u023c\u0225\3\2\2\2\u023c"+
		"\u0226\3\2\2\2\u023c\u022c\3\2\2\2\u023c\u0236\3\2\2\2\u023c\u023b\3\2"+
		"\2\2\u023d_\3\2\2\2\u023e\u0242\5b\62\2\u023f\u0242\7\t\2\2\u0240\u0242"+
		"\7(\2\2\u0241\u023e\3\2\2\2\u0241\u023f\3\2\2\2\u0241\u0240\3\2\2\2\u0242"+
		"a\3\2\2\2\u0243\u0244\t\5\2\2\u0244c\3\2\2\2\u0245\u0246\7\3\2\2\u0246"+
		"\u024a\5f\64\2\u0247\u0249\5v<\2\u0248\u0247\3\2\2\2\u0249\u024c\3\2\2"+
		"\2\u024a\u0248\3\2\2\2\u024a\u024b\3\2\2\2\u024b\u024d\3\2\2\2\u024c\u024a"+
		"\3\2\2\2\u024d\u024e\7\6\2\2\u024ee\3\2\2\2\u024f\u0250\7;\2\2\u0250g"+
		"\3\2\2\2\u0251\u0252\7\3\2\2\u0252\u0255\7+\2\2\u0253\u0255\7,\2\2\u0254"+
		"\u0251\3\2\2\2\u0254\u0253\3\2\2\2\u0255i\3\2\2\2\u0256\u0258\5n8\2\u0257"+
		"\u0256\3\2\2\2\u0258\u025b\3\2\2\2\u0259\u0257\3\2\2\2\u0259\u025a\3\2"+
		"\2\2\u025ak\3\2\2\2\u025b\u0259\3\2\2\2\u025c\u025e\5p9\2\u025d\u025c"+
		"\3\2\2\2\u025e\u0261\3\2\2\2\u025f\u025d\3\2\2\2\u025f\u0260\3\2\2\2\u0260"+
		"m\3\2\2\2\u0261\u025f\3\2\2\2\u0262\u0264\7:\2\2\u0263\u0262\3\2\2\2\u0264"+
		"\u0265\3\2\2\2\u0265\u0263\3\2\2\2\u0265\u0266\3\2\2\2\u0266\u0267\3\2"+
		"\2\2\u0267\u0268\7\t\2\2\u0268\u0269\5t;\2\u0269o\3\2\2\2\u026a\u026c"+
		"\5r:\2\u026b\u026a\3\2\2\2\u026c\u026d\3\2\2\2\u026d\u026b\3\2\2\2\u026d"+
		"\u026e\3\2\2\2\u026e\u026f\3\2\2\2\u026f\u0270\7\t\2\2\u0270\u0271\5t"+
		";\2\u0271q\3\2\2\2\u0272\u0273\7;\2\2\u0273s\3\2\2\2\u0274\u0275\7;\2"+
		"\2\u0275u\3\2\2\2\u0276\u0277\t\6\2\2\u0277w\3\2\2\2\u0278\u027c\7;\2"+
		"\2\u0279\u027c\7:\2\2\u027a\u027c\5z>\2\u027b\u0278\3\2\2\2\u027b\u0279"+
		"\3\2\2\2\u027b\u027a\3\2\2\2\u027cy\3\2\2\2\u027d\u027e\7\3\2\2\u027e"+
		"\u0282\5|?\2\u027f\u0281\5x=\2\u0280\u027f\3\2\2\2\u0281\u0284\3\2\2\2"+
		"\u0282\u0280\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0285\3\2\2\2\u0284\u0282"+
		"\3\2\2\2\u0285\u0286\7\6\2\2\u0286{\3\2\2\2\u0287\u0288\7;\2\2\u0288}"+
		"\3\2\2\2\u0289\u028a\7\3\2\2\u028a\u028b\7\4\2\2\u028b\u028c\7\3\2\2\u028c"+
		"\u028d\7-\2\2\u028d\u028e\7;\2\2\u028e\u028f\7\6\2\2\u028f\u0290\7\3\2"+
		"\2\u0290\u0291\7.\2\2\u0291\u0292\7;\2\2\u0292\u0294\7\6\2\2\u0293\u0295"+
		"\5\b\5\2\u0294\u0293\3\2\2\2\u0294\u0295\3\2\2\2\u0295\u0297\3\2\2\2\u0296"+
		"\u0298\5\u0080A\2\u0297\u0296\3\2\2\2\u0297\u0298\3\2\2\2\u0298\u029a"+
		"\3\2\2\2\u0299\u029b\5\u008aF\2\u029a\u0299\3\2\2\2\u029a\u029b\3\2\2"+
		"\2\u029b\u029c\3\2\2\2\u029c\u029e\5\u0082B\2\u029d\u029f\5\u0088E\2\u029e"+
		"\u029d\3\2\2\2\u029e\u029f\3\2\2\2\u029f\u02a1\3\2\2\2\u02a0\u02a2\5\u008c"+
		"G\2\u02a1\u02a0\3\2\2\2\u02a1\u02a2\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a3"+
		"\u02a4\7\6\2\2\u02a4\177\3\2\2\2\u02a5\u02a6\7\3\2\2\u02a6\u02a7\7/\2"+
		"\2\u02a7\u02a8\5l\67\2\u02a8\u02a9\7\6\2\2\u02a9\u0081\3\2\2\2\u02aa\u02ab"+
		"\7\3\2\2\u02ab\u02af\7\60\2\2\u02ac\u02ae\5\u0084C\2\u02ad\u02ac\3\2\2"+
		"\2\u02ae\u02b1\3\2\2\2\u02af\u02ad\3\2\2\2\u02af\u02b0\3\2\2\2\u02b0\u02b2"+
		"\3\2\2\2\u02b1\u02af\3\2\2\2\u02b2\u02b3\7\6\2\2\u02b3\u0083\3\2\2\2\u02b4"+
		"\u02b7\5R*\2\u02b5\u02b7\5\u0086D\2\u02b6\u02b4\3\2\2\2\u02b6\u02b5\3"+
		"\2\2\2\u02b7\u0085\3\2\2\2\u02b8\u02b9\5h\65\2\u02b9\u02ba\5\\/\2\u02ba"+
		"\u02bb\7>\2\2\u02bb\u02bc\7\6\2\2\u02bc\u0087\3\2\2\2\u02bd\u02be\7\3"+
		"\2\2\u02be\u02bf\7\61\2\2\u02bf\u02c0\5\66\34\2\u02c0\u02c1\7\6\2\2\u02c1"+
		"\u0089\3\2\2\2\u02c2\u02c3\7\3\2\2\u02c3\u02c4\t\7\2\2\u02c4\u02c5\5\""+
		"\22\2\u02c5\u008b\3\2\2\2\u02c6\u02c7\7\3\2\2\u02c7\u02c8\7\64\2\2\u02c8"+
		"\u02c9\5\u008eH\2\u02c9\u02ca\5\u0090I\2\u02ca\u02cb\7\6\2\2\u02cb\u008d"+
		"\3\2\2\2\u02cc\u02cd\t\b\2\2\u02cd\u008f\3\2\2\2\u02ce\u02cf\7\3\2\2\u02cf"+
		"\u02d0\5`\61\2\u02d0\u02d1\5\u0090I\2\u02d1\u02d2\5\u0090I\2\u02d2\u02d3"+
		"\7\6\2\2\u02d3\u02ea\3\2\2\2\u02d4\u02d5\7\3\2\2\u02d5\u02d8\7\t\2\2\u02d6"+
		"\u02d8\7\67\2\2\u02d7\u02d4\3\2\2\2\u02d7\u02d6\3\2\2\2\u02d8\u02d9\3"+
		"\2\2\2\u02d9\u02da\5\u0090I\2\u02da\u02db\7\6\2\2\u02db\u02ea\3\2\2\2"+
		"\u02dc\u02ea\7>\2\2\u02dd\u02de\7\3\2\2\u02de\u02e2\5|?\2\u02df\u02e1"+
		"\7;\2\2\u02e0\u02df\3\2\2\2\u02e1\u02e4\3\2\2\2\u02e2\u02e0\3\2\2\2\u02e2"+
		"\u02e3\3\2\2\2\u02e3\u02e5\3\2\2\2\u02e4\u02e2\3\2\2\2\u02e5\u02e6\7\6"+
		"\2\2\u02e6\u02ea\3\2\2\2\u02e7\u02ea\78\2\2\u02e8\u02ea\5|?\2\u02e9\u02ce"+
		"\3\2\2\2\u02e9\u02d7\3\2\2\2\u02e9\u02dc\3\2\2\2\u02e9\u02dd\3\2\2\2\u02e9"+
		"\u02e7\3\2\2\2\u02e9\u02e8\3\2\2\2\u02ea\u0091\3\2\2\2D\u0094\u009d\u00a0"+
		"\u00a3\u00a6\u00a9\u00ae\u00b4\u00ba\u00c9\u00d0\u00d7\u00dc\u00ea\u00ec"+
		"\u00f5\u0109\u010d\u0120\u0126\u012c\u0130\u0134\u0142\u0146\u014d\u0159"+
		"\u015f\u016b\u016f\u017f\u0183\u0194\u01a2\u01ac\u01b5\u01d4\u01db\u01e6"+
		"\u01ee\u01fd\u020a\u020e\u021e\u0223\u0232\u023c\u0241\u024a\u0254\u0259"+
		"\u025f\u0265\u026d\u027b\u0282\u0294\u0297\u029a\u029e\u02a1\u02af\u02b6"+
		"\u02d7\u02e2\u02e9";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}