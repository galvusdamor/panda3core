// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/parser/hddl/hddl.g4 by ANTLR 4.5
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
public class hddlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, REQUIRE_NAME=34, VAR_NAME=35, NAME=36, WS=37;
	public static final int
		RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3, 
		RULE_require_defs = 4, RULE_type_def = 5, RULE_one_def = 6, RULE_new_types = 7, 
		RULE_const_def = 8, RULE_predicates_def = 9, RULE_atomic_formula_skeleton = 10, 
		RULE_task_def = 11, RULE_task_symbol = 12, RULE_method_def = 13, RULE_method_symbol = 14, 
		RULE_subtask_defs = 15, RULE_subtask_def = 16, RULE_subtask_id = 17, RULE_ordering_defs = 18, 
		RULE_ordering_def = 19, RULE_constraint_defs = 20, RULE_constraint_def = 21, 
		RULE_action_def = 22, RULE_action_symbol = 23, RULE_gd = 24, RULE_gd_empty = 25, 
		RULE_gd_conjuction = 26, RULE_gd_disjuction = 27, RULE_gd_negation = 28, 
		RULE_effect_body = 29, RULE_eff_conjuntion = 30, RULE_eff_empty = 31, 
		RULE_c_effect = 32, RULE_forall_effect = 33, RULE_conditional_effect = 34, 
		RULE_literal = 35, RULE_neg_atomic_formula = 36, RULE_cond_effect = 37, 
		RULE_atomic_formula = 38, RULE_predicate = 39, RULE_typed_var_list = 40, 
		RULE_typed_obj_list = 41, RULE_typed_vars = 42, RULE_typed_objs = 43, 
		RULE_new_consts = 44, RULE_var_type = 45, RULE_var_or_const = 46, RULE_problem = 47, 
		RULE_p_object_declaration = 48, RULE_p_init = 49, RULE_p_goal = 50, RULE_p_htn = 51;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "one_def", "new_types", "const_def", "predicates_def", "atomic_formula_skeleton", 
		"task_def", "task_symbol", "method_def", "method_symbol", "subtask_defs", 
		"subtask_def", "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", 
		"constraint_def", "action_def", "action_symbol", "gd", "gd_empty", "gd_conjuction", 
		"gd_disjuction", "gd_negation", "effect_body", "eff_conjuntion", "eff_empty", 
		"c_effect", "forall_effect", "conditional_effect", "literal", "neg_atomic_formula", 
		"cond_effect", "atomic_formula", "predicate", "typed_var_list", "typed_obj_list", 
		"typed_vars", "typed_objs", "new_consts", "var_type", "var_or_const", 
		"problem", "p_object_declaration", "p_init", "p_goal", "p_htn"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':task'", "':parameters'", "':precondition'", 
		"':effect'", "':method'", "':subtasks'", "':ordering'", "':constraints'", 
		"'and'", "'<'", "'not'", "'='", "':action'", "'or'", "'forall'", "'when'", 
		"'problem'", "':domain'", "':objects'", "':init'", "':goal'", "':htn'", 
		"':htnti'", "':tasks'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "REQUIRE_NAME", 
		"VAR_NAME", "NAME", "WS"
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
	public String getGrammarFileName() { return "hddl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public hddlParser(TokenStream input) {
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
			setState(106);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(104); 
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(105); 
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
		public List<Task_defContext> task_def() {
			return getRuleContexts(Task_defContext.class);
		}
		public Task_defContext task_def(int i) {
			return getRuleContext(Task_defContext.class,i);
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
			setState(108); 
			match(T__0);
			setState(109); 
			match(T__1);
			setState(110); 
			match(T__0);
			setState(111); 
			match(T__2);
			setState(112); 
			domain_symbol();
			setState(113); 
			match(T__3);
			setState(115);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(114); 
				require_def();
				}
				break;
			}
			setState(118);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(117); 
				type_def();
				}
				break;
			}
			setState(121);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(120); 
				const_def();
				}
				break;
			}
			setState(124);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(123); 
				predicates_def();
				}
				break;
			}
			setState(129);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(126); 
					task_def();
					}
					} 
				}
				setState(131);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(135);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(132); 
					method_def();
					}
					} 
				}
				setState(137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(138); 
				action_def();
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(144); 
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
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
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
			setState(146); 
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
			setState(148); 
			match(T__0);
			setState(149); 
			match(T__4);
			setState(150); 
			require_defs();
			setState(151); 
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
		public List<TerminalNode> REQUIRE_NAME() { return getTokens(hddlParser.REQUIRE_NAME); }
		public TerminalNode REQUIRE_NAME(int i) {
			return getToken(hddlParser.REQUIRE_NAME, i);
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
			setState(154); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(153); 
				match(REQUIRE_NAME);
				}
				}
				setState(156); 
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
			setState(158); 
			match(T__0);
			setState(159); 
			match(T__5);
			setState(161); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(160); 
				one_def();
				}
				}
				setState(163); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(165); 
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167); 
			new_types();
			setState(168); 
			match(T__6);
			setState(169); 
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

	public static class New_typesContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(hddlParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(hddlParser.NAME, i);
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
			setState(172); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(171); 
				match(NAME);
				}
				}
				setState(174); 
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
			setState(176); 
			match(T__0);
			setState(177); 
			match(T__7);
			setState(178); 
			typed_obj_list();
			setState(179); 
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
			setState(181); 
			match(T__0);
			setState(182); 
			match(T__8);
			setState(184); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(183); 
				atomic_formula_skeleton();
				}
				}
				setState(186); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(188); 
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
			setState(190); 
			match(T__0);
			setState(191); 
			predicate();
			setState(192); 
			typed_var_list();
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
		enterRule(_localctx, 22, RULE_task_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195); 
			match(T__0);
			setState(196); 
			match(T__9);
			setState(197); 
			task_symbol();
			setState(198); 
			match(T__10);
			setState(199); 
			match(T__0);
			setState(200); 
			typed_var_list();
			setState(201); 
			match(T__3);
			setState(204);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(202); 
				match(T__11);
				setState(203); 
				gd();
				}
			}

			setState(208);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(206); 
				match(T__12);
				setState(207); 
				effect_body();
				}
			}

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

	public static class Task_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Task_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_task_symbol; }
	}

	public final Task_symbolContext task_symbol() throws RecognitionException {
		Task_symbolContext _localctx = new Task_symbolContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_task_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212); 
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
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Subtask_defsContext subtask_defs() {
			return getRuleContext(Subtask_defsContext.class,0);
		}
		public Ordering_defsContext ordering_defs() {
			return getRuleContext(Ordering_defsContext.class,0);
		}
		public Constraint_defsContext constraint_defs() {
			return getRuleContext(Constraint_defsContext.class,0);
		}
		public Method_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_def; }
	}

	public final Method_defContext method_def() throws RecognitionException {
		Method_defContext _localctx = new Method_defContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_method_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214); 
			match(T__0);
			setState(215); 
			match(T__13);
			setState(216); 
			method_symbol();
			setState(217); 
			match(T__10);
			setState(218); 
			match(T__0);
			setState(219); 
			typed_var_list();
			setState(220); 
			match(T__3);
			setState(221); 
			match(T__9);
			setState(222); 
			match(T__0);
			setState(223); 
			task_symbol();
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(224); 
				var_or_const();
				}
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(230); 
			match(T__3);
			setState(233);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(231); 
				match(T__11);
				setState(232); 
				gd();
				}
			}

			setState(237);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(235); 
				match(T__14);
				setState(236); 
				subtask_defs();
				}
			}

			setState(241);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(239); 
				match(T__15);
				setState(240); 
				ordering_defs();
				}
			}

			setState(245);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(243); 
				match(T__16);
				setState(244); 
				constraint_defs();
				}
			}

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

	public static class Method_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Method_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_symbol; }
	}

	public final Method_symbolContext method_symbol() throws RecognitionException {
		Method_symbolContext _localctx = new Method_symbolContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_method_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249); 
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
		enterRule(_localctx, 30, RULE_subtask_defs);
		int _la;
		try {
			setState(261);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(251); 
				subtask_def();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(252); 
				match(T__0);
				setState(253); 
				match(T__17);
				setState(255); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(254); 
					subtask_def();
					}
					}
					setState(257); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(259); 
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
		public Subtask_idContext subtask_id() {
			return getRuleContext(Subtask_idContext.class,0);
		}
		public Task_symbolContext task_symbol() {
			return getRuleContext(Task_symbolContext.class,0);
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
		enterRule(_localctx, 32, RULE_subtask_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263); 
			match(T__0);
			setState(264); 
			subtask_id();
			setState(265); 
			match(T__0);
			setState(266); 
			task_symbol();
			setState(268); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(267); 
				var_or_const();
				}
				}
				setState(270); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME || _la==NAME );
			setState(272); 
			match(T__3);
			setState(273); 
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

	public static class Subtask_idContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Subtask_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask_id; }
	}

	public final Subtask_idContext subtask_id() throws RecognitionException {
		Subtask_idContext _localctx = new Subtask_idContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_subtask_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275); 
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
		enterRule(_localctx, 36, RULE_ordering_defs);
		int _la;
		try {
			setState(289);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(277); 
				match(T__0);
				setState(278); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(279); 
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(280); 
				match(T__0);
				setState(281); 
				match(T__17);
				setState(283); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(282); 
					ordering_def();
					}
					}
					setState(285); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(287); 
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
		enterRule(_localctx, 38, RULE_ordering_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291); 
			match(T__0);
			setState(292); 
			subtask_id();
			setState(293); 
			match(T__18);
			setState(294); 
			subtask_id();
			setState(295); 
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
		enterRule(_localctx, 40, RULE_constraint_defs);
		int _la;
		try {
			setState(309);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(297); 
				match(T__0);
				setState(298); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(299); 
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(300); 
				match(T__0);
				setState(301); 
				match(T__17);
				setState(303); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(302); 
					constraint_def();
					}
					}
					setState(305); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(307); 
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
		enterRule(_localctx, 42, RULE_constraint_def);
		try {
			setState(328);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(311); 
				match(T__0);
				setState(312); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(313); 
				match(T__0);
				setState(314); 
				match(T__19);
				setState(315); 
				match(T__0);
				setState(316); 
				match(T__20);
				setState(317); 
				var_or_const();
				setState(318); 
				var_or_const();
				setState(319); 
				match(T__3);
				setState(320); 
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(322); 
				match(T__0);
				setState(323); 
				match(T__20);
				setState(324); 
				var_or_const();
				setState(325); 
				var_or_const();
				setState(326); 
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
		public Action_symbolContext action_symbol() {
			return getRuleContext(Action_symbolContext.class,0);
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
		public Action_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_def; }
	}

	public final Action_defContext action_def() throws RecognitionException {
		Action_defContext _localctx = new Action_defContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_action_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330); 
			match(T__0);
			setState(331); 
			match(T__21);
			setState(332); 
			action_symbol();
			setState(333); 
			match(T__10);
			setState(334); 
			match(T__0);
			setState(335); 
			typed_var_list();
			setState(336); 
			match(T__3);
			setState(339);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(337); 
				match(T__11);
				setState(338); 
				gd();
				}
			}

			setState(343);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(341); 
				match(T__12);
				setState(342); 
				effect_body();
				}
			}

			setState(345); 
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

	public static class Action_symbolContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Action_symbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_symbol; }
	}

	public final Action_symbolContext action_symbol() throws RecognitionException {
		Action_symbolContext _localctx = new Action_symbolContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_action_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(347); 
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
		public GdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd; }
	}

	public final GdContext gd() throws RecognitionException {
		GdContext _localctx = new GdContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_gd);
		try {
			setState(354);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(349); 
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(350); 
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(351); 
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(352); 
				gd_conjuction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(353); 
				gd_disjuction();
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
		enterRule(_localctx, 50, RULE_gd_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356); 
			match(T__0);
			setState(357); 
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
		enterRule(_localctx, 52, RULE_gd_conjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(359); 
			match(T__0);
			setState(360); 
			match(T__17);
			setState(362); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(361); 
				gd();
				}
				}
				setState(364); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(366); 
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
		enterRule(_localctx, 54, RULE_gd_disjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(368); 
			match(T__0);
			setState(369); 
			match(T__22);
			setState(371); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(370); 
				gd();
				}
				}
				setState(373); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(375); 
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
		enterRule(_localctx, 56, RULE_gd_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377); 
			match(T__0);
			setState(378); 
			match(T__19);
			setState(379); 
			gd();
			setState(380); 
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
		enterRule(_localctx, 58, RULE_effect_body);
		try {
			setState(385);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(382); 
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(383); 
				c_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(384); 
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
		enterRule(_localctx, 60, RULE_eff_conjuntion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387); 
			match(T__0);
			setState(388); 
			match(T__17);
			setState(390); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(389); 
				c_effect();
				}
				}
				setState(392); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(394); 
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
		enterRule(_localctx, 62, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396); 
			match(T__0);
			setState(397); 
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
		enterRule(_localctx, 64, RULE_c_effect);
		try {
			setState(402);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(399); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(400); 
				forall_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(401); 
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
		enterRule(_localctx, 66, RULE_forall_effect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404); 
			match(T__0);
			setState(405); 
			match(T__23);
			setState(406); 
			match(T__0);
			setState(410);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(407); 
				var_or_const();
				}
				}
				setState(412);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(413); 
			match(T__3);
			setState(414); 
			effect_body();
			setState(415); 
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
		enterRule(_localctx, 68, RULE_conditional_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417); 
			match(T__0);
			setState(418); 
			match(T__24);
			setState(419); 
			gd();
			setState(420); 
			cond_effect();
			setState(421); 
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
		enterRule(_localctx, 70, RULE_literal);
		try {
			setState(425);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(423); 
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(424); 
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
		enterRule(_localctx, 72, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427); 
			match(T__0);
			setState(428); 
			match(T__19);
			setState(429); 
			atomic_formula();
			setState(430); 
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
		enterRule(_localctx, 74, RULE_cond_effect);
		int _la;
		try {
			setState(442);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(432); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(433); 
				match(T__0);
				setState(434); 
				match(T__17);
				setState(436); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(435); 
					literal();
					}
					}
					setState(438); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(440); 
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
		enterRule(_localctx, 76, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444); 
			match(T__0);
			setState(445); 
			predicate();
			setState(449);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(446); 
				var_or_const();
				}
				}
				setState(451);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class PredicateContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454); 
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
		enterRule(_localctx, 80, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(456); 
				typed_vars();
				}
				}
				setState(461);
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
		enterRule(_localctx, 82, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(462); 
				typed_objs();
				}
				}
				setState(467);
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
		public List<TerminalNode> VAR_NAME() { return getTokens(hddlParser.VAR_NAME); }
		public TerminalNode VAR_NAME(int i) {
			return getToken(hddlParser.VAR_NAME, i);
		}
		public Typed_varsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_vars; }
	}

	public final Typed_varsContext typed_vars() throws RecognitionException {
		Typed_varsContext _localctx = new Typed_varsContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(469); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(468); 
				match(VAR_NAME);
				}
				}
				setState(471); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(473); 
			match(T__6);
			setState(474); 
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
		public New_constsContext new_consts() {
			return getRuleContext(New_constsContext.class,0);
		}
		public Var_typeContext var_type() {
			return getRuleContext(Var_typeContext.class,0);
		}
		public Typed_objsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typed_objs; }
	}

	public final Typed_objsContext typed_objs() throws RecognitionException {
		Typed_objsContext _localctx = new Typed_objsContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_typed_objs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476); 
			new_consts();
			setState(477); 
			match(T__6);
			setState(478); 
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
		public List<TerminalNode> NAME() { return getTokens(hddlParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(hddlParser.NAME, i);
		}
		public New_constsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_new_consts; }
	}

	public final New_constsContext new_consts() throws RecognitionException {
		New_constsContext _localctx = new New_constsContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_new_consts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(480); 
				match(NAME);
				}
				}
				setState(483); 
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

	public static class Var_typeContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Var_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_type; }
	}

	public final Var_typeContext var_type() throws RecognitionException {
		Var_typeContext _localctx = new Var_typeContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_var_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485); 
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
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public TerminalNode VAR_NAME() { return getToken(hddlParser.VAR_NAME, 0); }
		public Var_or_constContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_or_const; }
	}

	public final Var_or_constContext var_or_const() throws RecognitionException {
		Var_or_constContext _localctx = new Var_or_constContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(487);
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

	public static class ProblemContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(hddlParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(hddlParser.NAME, i);
		}
		public P_htnContext p_htn() {
			return getRuleContext(P_htnContext.class,0);
		}
		public P_initContext p_init() {
			return getRuleContext(P_initContext.class,0);
		}
		public P_goalContext p_goal() {
			return getRuleContext(P_goalContext.class,0);
		}
		public Require_defContext require_def() {
			return getRuleContext(Require_defContext.class,0);
		}
		public P_object_declarationContext p_object_declaration() {
			return getRuleContext(P_object_declarationContext.class,0);
		}
		public ProblemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_problem; }
	}

	public final ProblemContext problem() throws RecognitionException {
		ProblemContext _localctx = new ProblemContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_problem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(489); 
			match(T__0);
			setState(490); 
			match(T__1);
			setState(491); 
			match(T__0);
			setState(492); 
			match(T__25);
			setState(493); 
			match(NAME);
			setState(494); 
			match(T__3);
			setState(495); 
			match(T__0);
			setState(496); 
			match(T__26);
			setState(497); 
			match(NAME);
			setState(498); 
			match(T__3);
			setState(500);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(499); 
				require_def();
				}
				break;
			}
			setState(503);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(502); 
				p_object_declaration();
				}
				break;
			}
			setState(505); 
			p_htn();
			setState(506); 
			p_init();
			setState(507); 
			p_goal();
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
		enterRule(_localctx, 96, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510); 
			match(T__0);
			setState(511); 
			match(T__27);
			setState(512); 
			typed_obj_list();
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

	public static class P_initContext extends ParserRuleContext {
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public P_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_init; }
	}

	public final P_initContext p_init() throws RecognitionException {
		P_initContext _localctx = new P_initContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515); 
			match(T__0);
			setState(516); 
			match(T__28);
			setState(520);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(517); 
				literal();
				}
				}
				setState(522);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(523); 
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
		enterRule(_localctx, 100, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(525); 
			match(T__0);
			setState(526); 
			match(T__29);
			setState(527); 
			gd();
			setState(528); 
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
		public Subtask_defsContext subtask_defs() {
			return getRuleContext(Subtask_defsContext.class,0);
		}
		public Ordering_defsContext ordering_defs() {
			return getRuleContext(Ordering_defsContext.class,0);
		}
		public Constraint_defsContext constraint_defs() {
			return getRuleContext(Constraint_defsContext.class,0);
		}
		public P_htnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_htn; }
	}

	public final P_htnContext p_htn() throws RecognitionException {
		P_htnContext _localctx = new P_htnContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(530); 
			match(T__0);
			setState(531);
			_la = _input.LA(1);
			if ( !(_la==T__30 || _la==T__31) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(534);
			_la = _input.LA(1);
			if (_la==T__32) {
				{
				setState(532); 
				match(T__32);
				setState(533); 
				subtask_defs();
				}
			}

			setState(538);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(536); 
				match(T__15);
				setState(537); 
				ordering_defs();
				}
			}

			setState(542);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(540); 
				match(T__16);
				setState(541); 
				constraint_defs();
				}
			}

			setState(544); 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\'\u0225\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\3\2\3\2\5\2m\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3v\n\3\3"+
		"\3\5\3y\n\3\3\3\5\3|\n\3\3\3\5\3\177\n\3\3\3\7\3\u0082\n\3\f\3\16\3\u0085"+
		"\13\3\3\3\7\3\u0088\n\3\f\3\16\3\u008b\13\3\3\3\7\3\u008e\n\3\f\3\16\3"+
		"\u0091\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\6\6\u009d\n\6\r\6"+
		"\16\6\u009e\3\7\3\7\3\7\6\7\u00a4\n\7\r\7\16\7\u00a5\3\7\3\7\3\b\3\b\3"+
		"\b\3\b\3\t\6\t\u00af\n\t\r\t\16\t\u00b0\3\n\3\n\3\n\3\n\3\n\3\13\3\13"+
		"\3\13\6\13\u00bb\n\13\r\13\16\13\u00bc\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00cf\n\r\3\r\3\r\5\r\u00d3\n\r"+
		"\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\7\17\u00e4\n\17\f\17\16\17\u00e7\13\17\3\17\3\17\3\17\5\17\u00ec\n"+
		"\17\3\17\3\17\5\17\u00f0\n\17\3\17\3\17\5\17\u00f4\n\17\3\17\3\17\5\17"+
		"\u00f8\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\6\21\u0102\n\21\r"+
		"\21\16\21\u0103\3\21\3\21\5\21\u0108\n\21\3\22\3\22\3\22\3\22\3\22\6\22"+
		"\u010f\n\22\r\22\16\22\u0110\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\6\24\u011e\n\24\r\24\16\24\u011f\3\24\3\24\5\24\u0124\n"+
		"\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\6\26\u0132"+
		"\n\26\r\26\16\26\u0133\3\26\3\26\5\26\u0138\n\26\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u014b"+
		"\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0156\n\30\3\30"+
		"\3\30\5\30\u015a\n\30\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\5\32"+
		"\u0165\n\32\3\33\3\33\3\33\3\34\3\34\3\34\6\34\u016d\n\34\r\34\16\34\u016e"+
		"\3\34\3\34\3\35\3\35\3\35\6\35\u0176\n\35\r\35\16\35\u0177\3\35\3\35\3"+
		"\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\5\37\u0184\n\37\3 \3 \3 \6 \u0189"+
		"\n \r \16 \u018a\3 \3 \3!\3!\3!\3\"\3\"\3\"\5\"\u0195\n\"\3#\3#\3#\3#"+
		"\7#\u019b\n#\f#\16#\u019e\13#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3%\3%\5%\u01ac"+
		"\n%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\6\'\u01b7\n\'\r\'\16\'\u01b8\3\'\3"+
		"\'\5\'\u01bd\n\'\3(\3(\3(\7(\u01c2\n(\f(\16(\u01c5\13(\3(\3(\3)\3)\3*"+
		"\7*\u01cc\n*\f*\16*\u01cf\13*\3+\7+\u01d2\n+\f+\16+\u01d5\13+\3,\6,\u01d8"+
		"\n,\r,\16,\u01d9\3,\3,\3,\3-\3-\3-\3-\3.\6.\u01e4\n.\r.\16.\u01e5\3/\3"+
		"/\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\5\61"+
		"\u01f7\n\61\3\61\5\61\u01fa\n\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3"+
		"\62\3\62\3\62\3\63\3\63\3\63\7\63\u0209\n\63\f\63\16\63\u020c\13\63\3"+
		"\63\3\63\3\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\5\65\u0219\n\65"+
		"\3\65\3\65\5\65\u021d\n\65\3\65\3\65\5\65\u0221\n\65\3\65\3\65\3\65\2"+
		"\2\66\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>"+
		"@BDFHJLNPRTVXZ\\^`bdfh\2\4\3\2%&\3\2!\"\u022a\2l\3\2\2\2\4n\3\2\2\2\6"+
		"\u0094\3\2\2\2\b\u0096\3\2\2\2\n\u009c\3\2\2\2\f\u00a0\3\2\2\2\16\u00a9"+
		"\3\2\2\2\20\u00ae\3\2\2\2\22\u00b2\3\2\2\2\24\u00b7\3\2\2\2\26\u00c0\3"+
		"\2\2\2\30\u00c5\3\2\2\2\32\u00d6\3\2\2\2\34\u00d8\3\2\2\2\36\u00fb\3\2"+
		"\2\2 \u0107\3\2\2\2\"\u0109\3\2\2\2$\u0115\3\2\2\2&\u0123\3\2\2\2(\u0125"+
		"\3\2\2\2*\u0137\3\2\2\2,\u014a\3\2\2\2.\u014c\3\2\2\2\60\u015d\3\2\2\2"+
		"\62\u0164\3\2\2\2\64\u0166\3\2\2\2\66\u0169\3\2\2\28\u0172\3\2\2\2:\u017b"+
		"\3\2\2\2<\u0183\3\2\2\2>\u0185\3\2\2\2@\u018e\3\2\2\2B\u0194\3\2\2\2D"+
		"\u0196\3\2\2\2F\u01a3\3\2\2\2H\u01ab\3\2\2\2J\u01ad\3\2\2\2L\u01bc\3\2"+
		"\2\2N\u01be\3\2\2\2P\u01c8\3\2\2\2R\u01cd\3\2\2\2T\u01d3\3\2\2\2V\u01d7"+
		"\3\2\2\2X\u01de\3\2\2\2Z\u01e3\3\2\2\2\\\u01e7\3\2\2\2^\u01e9\3\2\2\2"+
		"`\u01eb\3\2\2\2b\u0200\3\2\2\2d\u0205\3\2\2\2f\u020f\3\2\2\2h\u0214\3"+
		"\2\2\2jm\5\4\3\2km\5`\61\2lj\3\2\2\2lk\3\2\2\2m\3\3\2\2\2no\7\3\2\2op"+
		"\7\4\2\2pq\7\3\2\2qr\7\5\2\2rs\5\6\4\2su\7\6\2\2tv\5\b\5\2ut\3\2\2\2u"+
		"v\3\2\2\2vx\3\2\2\2wy\5\f\7\2xw\3\2\2\2xy\3\2\2\2y{\3\2\2\2z|\5\22\n\2"+
		"{z\3\2\2\2{|\3\2\2\2|~\3\2\2\2}\177\5\24\13\2~}\3\2\2\2~\177\3\2\2\2\177"+
		"\u0083\3\2\2\2\u0080\u0082\5\30\r\2\u0081\u0080\3\2\2\2\u0082\u0085\3"+
		"\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0089\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0086\u0088\5\34\17\2\u0087\u0086\3\2\2\2\u0088\u008b\3"+
		"\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008f\3\2\2\2\u008b"+
		"\u0089\3\2\2\2\u008c\u008e\5.\30\2\u008d\u008c\3\2\2\2\u008e\u0091\3\2"+
		"\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2\2\2\u0091"+
		"\u008f\3\2\2\2\u0092\u0093\7\6\2\2\u0093\5\3\2\2\2\u0094\u0095\7&\2\2"+
		"\u0095\7\3\2\2\2\u0096\u0097\7\3\2\2\u0097\u0098\7\7\2\2\u0098\u0099\5"+
		"\n\6\2\u0099\u009a\7\6\2\2\u009a\t\3\2\2\2\u009b\u009d\7$\2\2\u009c\u009b"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\13\3\2\2\2\u00a0\u00a1\7\3\2\2\u00a1\u00a3\7\b\2\2\u00a2\u00a4\5\16\b"+
		"\2\u00a3\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6"+
		"\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8\7\6\2\2\u00a8\r\3\2\2\2\u00a9"+
		"\u00aa\5\20\t\2\u00aa\u00ab\7\t\2\2\u00ab\u00ac\5\\/\2\u00ac\17\3\2\2"+
		"\2\u00ad\u00af\7&\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00ae"+
		"\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\21\3\2\2\2\u00b2\u00b3\7\3\2\2\u00b3"+
		"\u00b4\7\n\2\2\u00b4\u00b5\5T+\2\u00b5\u00b6\7\6\2\2\u00b6\23\3\2\2\2"+
		"\u00b7\u00b8\7\3\2\2\u00b8\u00ba\7\13\2\2\u00b9\u00bb\5\26\f\2\u00ba\u00b9"+
		"\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00bf\7\6\2\2\u00bf\25\3\2\2\2\u00c0\u00c1\7\3\2"+
		"\2\u00c1\u00c2\5P)\2\u00c2\u00c3\5R*\2\u00c3\u00c4\7\6\2\2\u00c4\27\3"+
		"\2\2\2\u00c5\u00c6\7\3\2\2\u00c6\u00c7\7\f\2\2\u00c7\u00c8\5\32\16\2\u00c8"+
		"\u00c9\7\r\2\2\u00c9\u00ca\7\3\2\2\u00ca\u00cb\5R*\2\u00cb\u00ce\7\6\2"+
		"\2\u00cc\u00cd\7\16\2\2\u00cd\u00cf\5\62\32\2\u00ce\u00cc\3\2\2\2\u00ce"+
		"\u00cf\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00d1\7\17\2\2\u00d1\u00d3\5"+
		"<\37\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"\u00d5\7\6\2\2\u00d5\31\3\2\2\2\u00d6\u00d7\7&\2\2\u00d7\33\3\2\2\2\u00d8"+
		"\u00d9\7\3\2\2\u00d9\u00da\7\20\2\2\u00da\u00db\5\36\20\2\u00db\u00dc"+
		"\7\r\2\2\u00dc\u00dd\7\3\2\2\u00dd\u00de\5R*\2\u00de\u00df\7\6\2\2\u00df"+
		"\u00e0\7\f\2\2\u00e0\u00e1\7\3\2\2\u00e1\u00e5\5\32\16\2\u00e2\u00e4\5"+
		"^\60\2\u00e3\u00e2\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5"+
		"\u00e6\3\2\2\2\u00e6\u00e8\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00eb\7\6"+
		"\2\2\u00e9\u00ea\7\16\2\2\u00ea\u00ec\5\62\32\2\u00eb\u00e9\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00ee\7\21\2\2\u00ee\u00f0\5"+
		" \21\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1"+
		"\u00f2\7\22\2\2\u00f2\u00f4\5&\24\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3"+
		"\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f6\7\23\2\2\u00f6\u00f8\5*\26\2\u00f7"+
		"\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fa\7\6"+
		"\2\2\u00fa\35\3\2\2\2\u00fb\u00fc\7&\2\2\u00fc\37\3\2\2\2\u00fd\u0108"+
		"\5\"\22\2\u00fe\u00ff\7\3\2\2\u00ff\u0101\7\24\2\2\u0100\u0102\5\"\22"+
		"\2\u0101\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104"+
		"\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0106\7\6\2\2\u0106\u0108\3\2\2\2\u0107"+
		"\u00fd\3\2\2\2\u0107\u00fe\3\2\2\2\u0108!\3\2\2\2\u0109\u010a\7\3\2\2"+
		"\u010a\u010b\5$\23\2\u010b\u010c\7\3\2\2\u010c\u010e\5\32\16\2\u010d\u010f"+
		"\5^\60\2\u010e\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u010e\3\2\2\2\u0110"+
		"\u0111\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0113\7\6\2\2\u0113\u0114\7\6"+
		"\2\2\u0114#\3\2\2\2\u0115\u0116\7&\2\2\u0116%\3\2\2\2\u0117\u0118\7\3"+
		"\2\2\u0118\u0124\7\6\2\2\u0119\u0124\5(\25\2\u011a\u011b\7\3\2\2\u011b"+
		"\u011d\7\24\2\2\u011c\u011e\5(\25\2\u011d\u011c\3\2\2\2\u011e\u011f\3"+
		"\2\2\2\u011f\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0121\3\2\2\2\u0121"+
		"\u0122\7\6\2\2\u0122\u0124\3\2\2\2\u0123\u0117\3\2\2\2\u0123\u0119\3\2"+
		"\2\2\u0123\u011a\3\2\2\2\u0124\'\3\2\2\2\u0125\u0126\7\3\2\2\u0126\u0127"+
		"\5$\23\2\u0127\u0128\7\25\2\2\u0128\u0129\5$\23\2\u0129\u012a\7\6\2\2"+
		"\u012a)\3\2\2\2\u012b\u012c\7\3\2\2\u012c\u0138\7\6\2\2\u012d\u0138\5"+
		",\27\2\u012e\u012f\7\3\2\2\u012f\u0131\7\24\2\2\u0130\u0132\5,\27\2\u0131"+
		"\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0131\3\2\2\2\u0133\u0134\3\2"+
		"\2\2\u0134\u0135\3\2\2\2\u0135\u0136\7\6\2\2\u0136\u0138\3\2\2\2\u0137"+
		"\u012b\3\2\2\2\u0137\u012d\3\2\2\2\u0137\u012e\3\2\2\2\u0138+\3\2\2\2"+
		"\u0139\u013a\7\3\2\2\u013a\u014b\7\6\2\2\u013b\u013c\7\3\2\2\u013c\u013d"+
		"\7\26\2\2\u013d\u013e\7\3\2\2\u013e\u013f\7\27\2\2\u013f\u0140\5^\60\2"+
		"\u0140\u0141\5^\60\2\u0141\u0142\7\6\2\2\u0142\u0143\7\6\2\2\u0143\u014b"+
		"\3\2\2\2\u0144\u0145\7\3\2\2\u0145\u0146\7\27\2\2\u0146\u0147\5^\60\2"+
		"\u0147\u0148\5^\60\2\u0148\u0149\7\6\2\2\u0149\u014b\3\2\2\2\u014a\u0139"+
		"\3\2\2\2\u014a\u013b\3\2\2\2\u014a\u0144\3\2\2\2\u014b-\3\2\2\2\u014c"+
		"\u014d\7\3\2\2\u014d\u014e\7\30\2\2\u014e\u014f\5\60\31\2\u014f\u0150"+
		"\7\r\2\2\u0150\u0151\7\3\2\2\u0151\u0152\5R*\2\u0152\u0155\7\6\2\2\u0153"+
		"\u0154\7\16\2\2\u0154\u0156\5\62\32\2\u0155\u0153\3\2\2\2\u0155\u0156"+
		"\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0158\7\17\2\2\u0158\u015a\5<\37\2"+
		"\u0159\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015c"+
		"\7\6\2\2\u015c/\3\2\2\2\u015d\u015e\7&\2\2\u015e\61\3\2\2\2\u015f\u0165"+
		"\5\64\33\2\u0160\u0165\5N(\2\u0161\u0165\5:\36\2\u0162\u0165\5\66\34\2"+
		"\u0163\u0165\58\35\2\u0164\u015f\3\2\2\2\u0164\u0160\3\2\2\2\u0164\u0161"+
		"\3\2\2\2\u0164\u0162\3\2\2\2\u0164\u0163\3\2\2\2\u0165\63\3\2\2\2\u0166"+
		"\u0167\7\3\2\2\u0167\u0168\7\6\2\2\u0168\65\3\2\2\2\u0169\u016a\7\3\2"+
		"\2\u016a\u016c\7\24\2\2\u016b\u016d\5\62\32\2\u016c\u016b\3\2\2\2\u016d"+
		"\u016e\3\2\2\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\3\2"+
		"\2\2\u0170\u0171\7\6\2\2\u0171\67\3\2\2\2\u0172\u0173\7\3\2\2\u0173\u0175"+
		"\7\31\2\2\u0174\u0176\5\62\32\2\u0175\u0174\3\2\2\2\u0176\u0177\3\2\2"+
		"\2\u0177\u0175\3\2\2\2\u0177\u0178\3\2\2\2\u0178\u0179\3\2\2\2\u0179\u017a"+
		"\7\6\2\2\u017a9\3\2\2\2\u017b\u017c\7\3\2\2\u017c\u017d\7\26\2\2\u017d"+
		"\u017e\5\62\32\2\u017e\u017f\7\6\2\2\u017f;\3\2\2\2\u0180\u0184\5@!\2"+
		"\u0181\u0184\5B\"\2\u0182\u0184\5> \2\u0183\u0180\3\2\2\2\u0183\u0181"+
		"\3\2\2\2\u0183\u0182\3\2\2\2\u0184=\3\2\2\2\u0185\u0186\7\3\2\2\u0186"+
		"\u0188\7\24\2\2\u0187\u0189\5B\"\2\u0188\u0187\3\2\2\2\u0189\u018a\3\2"+
		"\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018c\3\2\2\2\u018c"+
		"\u018d\7\6\2\2\u018d?\3\2\2\2\u018e\u018f\7\3\2\2\u018f\u0190\7\6\2\2"+
		"\u0190A\3\2\2\2\u0191\u0195\5H%\2\u0192\u0195\5D#\2\u0193\u0195\5F$\2"+
		"\u0194\u0191\3\2\2\2\u0194\u0192\3\2\2\2\u0194\u0193\3\2\2\2\u0195C\3"+
		"\2\2\2\u0196\u0197\7\3\2\2\u0197\u0198\7\32\2\2\u0198\u019c\7\3\2\2\u0199"+
		"\u019b\5^\60\2\u019a\u0199\3\2\2\2\u019b\u019e\3\2\2\2\u019c\u019a\3\2"+
		"\2\2\u019c\u019d\3\2\2\2\u019d\u019f\3\2\2\2\u019e\u019c\3\2\2\2\u019f"+
		"\u01a0\7\6\2\2\u01a0\u01a1\5<\37\2\u01a1\u01a2\7\6\2\2\u01a2E\3\2\2\2"+
		"\u01a3\u01a4\7\3\2\2\u01a4\u01a5\7\33\2\2\u01a5\u01a6\5\62\32\2\u01a6"+
		"\u01a7\5L\'\2\u01a7\u01a8\7\6\2\2\u01a8G\3\2\2\2\u01a9\u01ac\5J&\2\u01aa"+
		"\u01ac\5N(\2\u01ab\u01a9\3\2\2\2\u01ab\u01aa\3\2\2\2\u01acI\3\2\2\2\u01ad"+
		"\u01ae\7\3\2\2\u01ae\u01af\7\26\2\2\u01af\u01b0\5N(\2\u01b0\u01b1\7\6"+
		"\2\2\u01b1K\3\2\2\2\u01b2\u01bd\5H%\2\u01b3\u01b4\7\3\2\2\u01b4\u01b6"+
		"\7\24\2\2\u01b5\u01b7\5H%\2\u01b6\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8"+
		"\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba\u01bb\7\6"+
		"\2\2\u01bb\u01bd\3\2\2\2\u01bc\u01b2\3\2\2\2\u01bc\u01b3\3\2\2\2\u01bd"+
		"M\3\2\2\2\u01be\u01bf\7\3\2\2\u01bf\u01c3\5P)\2\u01c0\u01c2\5^\60\2\u01c1"+
		"\u01c0\3\2\2\2\u01c2\u01c5\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c3\u01c4\3\2"+
		"\2\2\u01c4\u01c6\3\2\2\2\u01c5\u01c3\3\2\2\2\u01c6\u01c7\7\6\2\2\u01c7"+
		"O\3\2\2\2\u01c8\u01c9\7&\2\2\u01c9Q\3\2\2\2\u01ca\u01cc\5V,\2\u01cb\u01ca"+
		"\3\2\2\2\u01cc\u01cf\3\2\2\2\u01cd\u01cb\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce"+
		"S\3\2\2\2\u01cf\u01cd\3\2\2\2\u01d0\u01d2\5X-\2\u01d1\u01d0\3\2\2\2\u01d2"+
		"\u01d5\3\2\2\2\u01d3\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4U\3\2\2\2"+
		"\u01d5\u01d3\3\2\2\2\u01d6\u01d8\7%\2\2\u01d7\u01d6\3\2\2\2\u01d8\u01d9"+
		"\3\2\2\2\u01d9\u01d7\3\2\2\2\u01d9\u01da\3\2\2\2\u01da\u01db\3\2\2\2\u01db"+
		"\u01dc\7\t\2\2\u01dc\u01dd\5\\/\2\u01ddW\3\2\2\2\u01de\u01df\5Z.\2\u01df"+
		"\u01e0\7\t\2\2\u01e0\u01e1\5\\/\2\u01e1Y\3\2\2\2\u01e2\u01e4\7&\2\2\u01e3"+
		"\u01e2\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e5\u01e6\3\2"+
		"\2\2\u01e6[\3\2\2\2\u01e7\u01e8\7&\2\2\u01e8]\3\2\2\2\u01e9\u01ea\t\2"+
		"\2\2\u01ea_\3\2\2\2\u01eb\u01ec\7\3\2\2\u01ec\u01ed\7\4\2\2\u01ed\u01ee"+
		"\7\3\2\2\u01ee\u01ef\7\34\2\2\u01ef\u01f0\7&\2\2\u01f0\u01f1\7\6\2\2\u01f1"+
		"\u01f2\7\3\2\2\u01f2\u01f3\7\35\2\2\u01f3\u01f4\7&\2\2\u01f4\u01f6\7\6"+
		"\2\2\u01f5\u01f7\5\b\5\2\u01f6\u01f5\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7"+
		"\u01f9\3\2\2\2\u01f8\u01fa\5b\62\2\u01f9\u01f8\3\2\2\2\u01f9\u01fa\3\2"+
		"\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01fc\5h\65\2\u01fc\u01fd\5d\63\2\u01fd"+
		"\u01fe\5f\64\2\u01fe\u01ff\7\6\2\2\u01ffa\3\2\2\2\u0200\u0201\7\3\2\2"+
		"\u0201\u0202\7\36\2\2\u0202\u0203\5T+\2\u0203\u0204\7\6\2\2\u0204c\3\2"+
		"\2\2\u0205\u0206\7\3\2\2\u0206\u020a\7\37\2\2\u0207\u0209\5H%\2\u0208"+
		"\u0207\3\2\2\2\u0209\u020c\3\2\2\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2"+
		"\2\2\u020b\u020d\3\2\2\2\u020c\u020a\3\2\2\2\u020d\u020e\7\6\2\2\u020e"+
		"e\3\2\2\2\u020f\u0210\7\3\2\2\u0210\u0211\7 \2\2\u0211\u0212\5\62\32\2"+
		"\u0212\u0213\7\6\2\2\u0213g\3\2\2\2\u0214\u0215\7\3\2\2\u0215\u0218\t"+
		"\3\2\2\u0216\u0217\7#\2\2\u0217\u0219\5 \21\2\u0218\u0216\3\2\2\2\u0218"+
		"\u0219\3\2\2\2\u0219\u021c\3\2\2\2\u021a\u021b\7\22\2\2\u021b\u021d\5"+
		"&\24\2\u021c\u021a\3\2\2\2\u021c\u021d\3\2\2\2\u021d\u0220\3\2\2\2\u021e"+
		"\u021f\7\23\2\2\u021f\u0221\5*\26\2\u0220\u021e\3\2\2\2\u0220\u0221\3"+
		"\2\2\2\u0221\u0222\3\2\2\2\u0222\u0223\7\6\2\2\u0223i\3\2\2\2\64lux{~"+
		"\u0083\u0089\u008f\u009e\u00a5\u00b0\u00bc\u00ce\u00d2\u00e5\u00eb\u00ef"+
		"\u00f3\u00f7\u0103\u0107\u0110\u011f\u0123\u0133\u0137\u014a\u0155\u0159"+
		"\u0164\u016e\u0177\u0183\u018a\u0194\u019c\u01ab\u01b8\u01bc\u01c3\u01cd"+
		"\u01d3\u01d9\u01e5\u01f6\u01f9\u020a\u0218\u021c\u0220";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}