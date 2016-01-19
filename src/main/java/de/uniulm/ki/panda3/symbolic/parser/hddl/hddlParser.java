// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/hddl.g4 by ANTLR 4.5
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
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		REQUIRE_NAME=39, VAR_NAME=40, NAME=41, COMMENT=42, WS=43;
	public static final int
		RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3, 
		RULE_require_defs = 4, RULE_type_def = 5, RULE_one_def = 6, RULE_new_types = 7, 
		RULE_const_def = 8, RULE_predicates_def = 9, RULE_atomic_formula_skeleton = 10, 
		RULE_comp_task_def = 11, RULE_task_def = 12, RULE_task_symbol = 13, RULE_method_def = 14, 
		RULE_tasknetwork_def = 15, RULE_method_symbol = 16, RULE_subtask_defs = 17, 
		RULE_subtask_def = 18, RULE_subtask_id = 19, RULE_ordering_defs = 20, 
		RULE_ordering_def = 21, RULE_constraint_defs = 22, RULE_constraint_def = 23, 
		RULE_action_def = 24, RULE_gd = 25, RULE_gd_empty = 26, RULE_gd_conjuction = 27, 
		RULE_gd_disjuction = 28, RULE_gd_negation = 29, RULE_gd_existential = 30, 
		RULE_effect_body = 31, RULE_eff_conjuntion = 32, RULE_eff_empty = 33, 
		RULE_c_effect = 34, RULE_forall_effect = 35, RULE_conditional_effect = 36, 
		RULE_literal = 37, RULE_neg_atomic_formula = 38, RULE_cond_effect = 39, 
		RULE_atomic_formula = 40, RULE_gd_predicate_equality = 41, RULE_predicate = 42, 
		RULE_typed_var_list = 43, RULE_typed_obj_list = 44, RULE_typed_vars = 45, 
		RULE_typed_objs = 46, RULE_new_consts = 47, RULE_var_type = 48, RULE_var_or_const = 49, 
		RULE_problem = 50, RULE_p_object_declaration = 51, RULE_p_init = 52, RULE_p_goal = 53, 
		RULE_p_htn = 54;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "one_def", "new_types", "const_def", "predicates_def", "atomic_formula_skeleton", 
		"comp_task_def", "task_def", "task_symbol", "method_def", "tasknetwork_def", 
		"method_symbol", "subtask_defs", "subtask_def", "subtask_id", "ordering_defs", 
		"ordering_def", "constraint_defs", "constraint_def", "action_def", "gd", 
		"gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", "gd_existential", 
		"effect_body", "eff_conjuntion", "eff_empty", "c_effect", "forall_effect", 
		"conditional_effect", "literal", "neg_atomic_formula", "cond_effect", 
		"atomic_formula", "gd_predicate_equality", "predicate", "typed_var_list", 
		"typed_obj_list", "typed_vars", "typed_objs", "new_consts", "var_type", 
		"var_or_const", "problem", "p_object_declaration", "p_init", "p_goal", 
		"p_htn"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':task'", "':parameters'", "':precondition'", 
		"':effect'", "':method'", "':subtasks'", "':tasks'", "':ordered-subtasks'", 
		"':ordered-tasks'", "':ordering'", "':order'", "':constraints'", "'and'", 
		"'<'", "'not'", "'='", "':action'", "'or'", "'(exists'", "'forall'", "'when'", 
		"'(='", "'problem'", "':domain'", "':objects'", "':init'", "':goal'", 
		"':htn'", "':htnti'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "REQUIRE_NAME", "VAR_NAME", "NAME", "COMMENT", "WS"
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
			setState(112);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(110); 
				domain();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(111); 
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
			setState(114); 
			match(T__0);
			setState(115); 
			match(T__1);
			setState(116); 
			match(T__0);
			setState(117); 
			match(T__2);
			setState(118); 
			domain_symbol();
			setState(119); 
			match(T__3);
			setState(121);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(120); 
				require_def();
				}
				break;
			}
			setState(124);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(123); 
				type_def();
				}
				break;
			}
			setState(127);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(126); 
				const_def();
				}
				break;
			}
			setState(130);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(129); 
				predicates_def();
				}
				break;
			}
			setState(135);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(132); 
					comp_task_def();
					}
					} 
				}
				setState(137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(141);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(138); 
					method_def();
					}
					} 
				}
				setState(143);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(144); 
				action_def();
				}
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(150); 
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
			setState(152); 
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
			setState(154); 
			match(T__0);
			setState(155); 
			match(T__4);
			setState(156); 
			require_defs();
			setState(157); 
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
			setState(160); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(159); 
				match(REQUIRE_NAME);
				}
				}
				setState(162); 
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
			setState(164); 
			match(T__0);
			setState(165); 
			match(T__5);
			setState(167); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(166); 
				one_def();
				}
				}
				setState(169); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(171); 
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
			setState(173); 
			new_types();
			setState(174); 
			match(T__6);
			setState(175); 
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
			setState(178); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(177); 
				match(NAME);
				}
				}
				setState(180); 
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
			setState(182); 
			match(T__0);
			setState(183); 
			match(T__7);
			setState(184); 
			typed_obj_list();
			setState(185); 
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
			setState(187); 
			match(T__0);
			setState(188); 
			match(T__8);
			setState(190); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(189); 
				atomic_formula_skeleton();
				}
				}
				setState(192); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
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
			setState(196); 
			match(T__0);
			setState(197); 
			predicate();
			setState(198); 
			typed_var_list();
			setState(199); 
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
		enterRule(_localctx, 22, RULE_comp_task_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201); 
			match(T__0);
			setState(202); 
			match(T__9);
			setState(203); 
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
		enterRule(_localctx, 24, RULE_task_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205); 
			task_symbol();
			setState(206); 
			match(T__10);
			setState(207); 
			match(T__0);
			setState(208); 
			typed_var_list();
			setState(209); 
			match(T__3);
			setState(212);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(210); 
				match(T__11);
				setState(211); 
				gd();
				}
			}

			setState(216);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(214); 
				match(T__12);
				setState(215); 
				effect_body();
				}
			}

			setState(218); 
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
		enterRule(_localctx, 26, RULE_task_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220); 
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
		enterRule(_localctx, 28, RULE_method_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222); 
			match(T__0);
			setState(223); 
			match(T__13);
			setState(224); 
			method_symbol();
			setState(225); 
			match(T__10);
			setState(226); 
			match(T__0);
			setState(227); 
			typed_var_list();
			setState(228); 
			match(T__3);
			setState(229); 
			match(T__9);
			setState(230); 
			match(T__0);
			setState(231); 
			task_symbol();
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(232); 
				var_or_const();
				}
				}
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(238); 
			match(T__3);
			setState(241);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(239); 
				match(T__11);
				setState(240); 
				gd();
				}
			}

			setState(243); 
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
		enterRule(_localctx, 30, RULE_tasknetwork_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17))) != 0)) {
				{
				setState(245);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(246); 
				subtask_defs();
				}
			}

			setState(251);
			_la = _input.LA(1);
			if (_la==T__18 || _la==T__19) {
				{
				setState(249);
				_la = _input.LA(1);
				if ( !(_la==T__18 || _la==T__19) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(250); 
				ordering_defs();
				}
			}

			setState(255);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(253); 
				match(T__20);
				setState(254); 
				constraint_defs();
				}
			}

			setState(257); 
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
		enterRule(_localctx, 32, RULE_method_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259); 
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
		enterRule(_localctx, 34, RULE_subtask_defs);
		int _la;
		try {
			setState(273);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(261); 
				match(T__0);
				setState(262); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(263); 
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(264); 
				match(T__0);
				setState(265); 
				match(T__21);
				setState(267); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(266); 
					subtask_def();
					}
					}
					setState(269); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(271); 
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
		enterRule(_localctx, 36, RULE_subtask_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(275); 
				match(T__0);
				setState(276); 
				task_symbol();
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(277); 
					var_or_const();
					}
					}
					setState(282);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(283); 
				match(T__3);
				}
				break;
			case 2:
				{
				setState(285); 
				match(T__0);
				setState(286); 
				subtask_id();
				setState(287); 
				match(T__0);
				setState(288); 
				task_symbol();
				setState(292);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(289); 
					var_or_const();
					}
					}
					setState(294);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(295); 
				match(T__3);
				setState(296); 
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
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public Subtask_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask_id; }
	}

	public final Subtask_idContext subtask_id() throws RecognitionException {
		Subtask_idContext _localctx = new Subtask_idContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_subtask_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300); 
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
		enterRule(_localctx, 40, RULE_ordering_defs);
		int _la;
		try {
			setState(314);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(302); 
				match(T__0);
				setState(303); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(304); 
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(305); 
				match(T__0);
				setState(306); 
				match(T__21);
				setState(308); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(307); 
					ordering_def();
					}
					}
					setState(310); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(312); 
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
		enterRule(_localctx, 42, RULE_ordering_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316); 
			match(T__0);
			setState(317); 
			subtask_id();
			setState(318); 
			match(T__22);
			setState(319); 
			subtask_id();
			setState(320); 
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
		enterRule(_localctx, 44, RULE_constraint_defs);
		int _la;
		try {
			setState(334);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(322); 
				match(T__0);
				setState(323); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(324); 
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(325); 
				match(T__0);
				setState(326); 
				match(T__21);
				setState(328); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(327); 
					constraint_def();
					}
					}
					setState(330); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(332); 
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
		enterRule(_localctx, 46, RULE_constraint_def);
		try {
			setState(353);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(336); 
				match(T__0);
				setState(337); 
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(338); 
				match(T__0);
				setState(339); 
				match(T__23);
				setState(340); 
				match(T__0);
				setState(341); 
				match(T__24);
				setState(342); 
				var_or_const();
				setState(343); 
				var_or_const();
				setState(344); 
				match(T__3);
				setState(345); 
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(347); 
				match(T__0);
				setState(348); 
				match(T__24);
				setState(349); 
				var_or_const();
				setState(350); 
				var_or_const();
				setState(351); 
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
		enterRule(_localctx, 48, RULE_action_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355); 
			match(T__0);
			setState(356); 
			match(T__25);
			setState(357); 
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
		public GdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd; }
	}

	public final GdContext gd() throws RecognitionException {
		GdContext _localctx = new GdContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_gd);
		try {
			setState(365);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(359); 
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(360); 
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(361); 
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(362); 
				gd_conjuction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(363); 
				gd_disjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(364); 
				gd_existential();
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
		enterRule(_localctx, 52, RULE_gd_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367); 
			match(T__0);
			setState(368); 
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
		enterRule(_localctx, 54, RULE_gd_conjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(370); 
			match(T__0);
			setState(371); 
			match(T__21);
			setState(373); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(372); 
				gd();
				}
				}
				setState(375); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__27) | (1L << T__30))) != 0) );
			setState(377); 
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
		enterRule(_localctx, 56, RULE_gd_disjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379); 
			match(T__0);
			setState(380); 
			match(T__26);
			setState(382); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(381); 
				gd();
				}
				}
				setState(384); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__27) | (1L << T__30))) != 0) );
			setState(386); 
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
		enterRule(_localctx, 58, RULE_gd_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388); 
			match(T__0);
			setState(389); 
			match(T__23);
			setState(390); 
			gd();
			setState(391); 
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
		enterRule(_localctx, 60, RULE_gd_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393); 
			match(T__27);
			setState(394); 
			match(T__0);
			setState(395); 
			typed_var_list();
			setState(396); 
			match(T__3);
			setState(397); 
			gd();
			setState(398); 
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
		enterRule(_localctx, 62, RULE_effect_body);
		try {
			setState(403);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(400); 
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(401); 
				c_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(402); 
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
		enterRule(_localctx, 64, RULE_eff_conjuntion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405); 
			match(T__0);
			setState(406); 
			match(T__21);
			setState(408); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(407); 
				c_effect();
				}
				}
				setState(410); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__30 );
			setState(412); 
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
		enterRule(_localctx, 66, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414); 
			match(T__0);
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
		enterRule(_localctx, 68, RULE_c_effect);
		try {
			setState(420);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(417); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(418); 
				forall_effect();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(419); 
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
		enterRule(_localctx, 70, RULE_forall_effect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422); 
			match(T__0);
			setState(423); 
			match(T__28);
			setState(424); 
			match(T__0);
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(425); 
				var_or_const();
				}
				}
				setState(430);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(431); 
			match(T__3);
			setState(432); 
			effect_body();
			setState(433); 
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
		enterRule(_localctx, 72, RULE_conditional_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435); 
			match(T__0);
			setState(436); 
			match(T__29);
			setState(437); 
			gd();
			setState(438); 
			cond_effect();
			setState(439); 
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
		enterRule(_localctx, 74, RULE_literal);
		try {
			setState(443);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(441); 
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(442); 
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
		enterRule(_localctx, 76, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(445); 
			match(T__0);
			setState(446); 
			match(T__23);
			setState(447); 
			atomic_formula();
			setState(448); 
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
		enterRule(_localctx, 78, RULE_cond_effect);
		int _la;
		try {
			setState(460);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(450); 
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(451); 
				match(T__0);
				setState(452); 
				match(T__21);
				setState(454); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(453); 
					literal();
					}
					}
					setState(456); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__30 );
				setState(458); 
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
		public Gd_predicate_equalityContext gd_predicate_equality() {
			return getRuleContext(Gd_predicate_equalityContext.class,0);
		}
		public Atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_formula; }
	}

	public final Atomic_formulaContext atomic_formula() throws RecognitionException {
		Atomic_formulaContext _localctx = new Atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_atomic_formula);
		int _la;
		try {
			setState(473);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(462); 
				match(T__0);
				setState(463); 
				predicate();
				setState(467);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(464); 
					var_or_const();
					}
					}
					setState(469);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(470); 
				match(T__3);
				}
				break;
			case T__30:
				enterOuterAlt(_localctx, 2);
				{
				setState(472); 
				gd_predicate_equality();
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

	public static class Gd_predicate_equalityContext extends ParserRuleContext {
		public List<Var_or_constContext> var_or_const() {
			return getRuleContexts(Var_or_constContext.class);
		}
		public Var_or_constContext var_or_const(int i) {
			return getRuleContext(Var_or_constContext.class,i);
		}
		public Gd_predicate_equalityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_predicate_equality; }
	}

	public final Gd_predicate_equalityContext gd_predicate_equality() throws RecognitionException {
		Gd_predicate_equalityContext _localctx = new Gd_predicate_equalityContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_gd_predicate_equality);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475); 
			match(T__30);
			setState(476); 
			var_or_const();
			setState(477); 
			var_or_const();
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

	public static class PredicateContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(hddlParser.NAME, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(480); 
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
		enterRule(_localctx, 86, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(482); 
				typed_vars();
				}
				}
				setState(487);
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
		enterRule(_localctx, 88, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(491);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(488); 
				typed_objs();
				}
				}
				setState(493);
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
		enterRule(_localctx, 90, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(494); 
				match(VAR_NAME);
				}
				}
				setState(497); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(499); 
			match(T__6);
			setState(500); 
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
		enterRule(_localctx, 92, RULE_typed_objs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(502); 
			new_consts();
			setState(503); 
			match(T__6);
			setState(504); 
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
		enterRule(_localctx, 94, RULE_new_consts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(506); 
				match(NAME);
				}
				}
				setState(509); 
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
		enterRule(_localctx, 96, RULE_var_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(511); 
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
		enterRule(_localctx, 98, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
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
		public Require_defContext require_def() {
			return getRuleContext(Require_defContext.class,0);
		}
		public P_object_declarationContext p_object_declaration() {
			return getRuleContext(P_object_declarationContext.class,0);
		}
		public P_goalContext p_goal() {
			return getRuleContext(P_goalContext.class,0);
		}
		public ProblemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_problem; }
	}

	public final ProblemContext problem() throws RecognitionException {
		ProblemContext _localctx = new ProblemContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515); 
			match(T__0);
			setState(516); 
			match(T__1);
			setState(517); 
			match(T__0);
			setState(518); 
			match(T__31);
			setState(519); 
			match(NAME);
			setState(520); 
			match(T__3);
			setState(521); 
			match(T__0);
			setState(522); 
			match(T__32);
			setState(523); 
			match(NAME);
			setState(524); 
			match(T__3);
			setState(526);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(525); 
				require_def();
				}
				break;
			}
			setState(529);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(528); 
				p_object_declaration();
				}
				break;
			}
			setState(531); 
			p_htn();
			setState(532); 
			p_init();
			setState(534);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(533); 
				p_goal();
				}
			}

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
		enterRule(_localctx, 102, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538); 
			match(T__0);
			setState(539); 
			match(T__33);
			setState(540); 
			typed_obj_list();
			setState(541); 
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
		enterRule(_localctx, 104, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(543); 
			match(T__0);
			setState(544); 
			match(T__34);
			setState(548);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__30) {
				{
				{
				setState(545); 
				literal();
				}
				}
				setState(550);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(551); 
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
		enterRule(_localctx, 106, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553); 
			match(T__0);
			setState(554); 
			match(T__35);
			setState(555); 
			gd();
			setState(556); 
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
		enterRule(_localctx, 108, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(558); 
			match(T__0);
			setState(559);
			_la = _input.LA(1);
			if ( !(_la==T__36 || _la==T__37) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(560); 
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3-\u0235\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\3\2\5\2s\n\2\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\5\3|\n\3\3\3\5\3\177\n\3\3\3\5\3\u0082\n\3\3\3\5\3\u0085"+
		"\n\3\3\3\7\3\u0088\n\3\f\3\16\3\u008b\13\3\3\3\7\3\u008e\n\3\f\3\16\3"+
		"\u0091\13\3\3\3\7\3\u0094\n\3\f\3\16\3\u0097\13\3\3\3\3\3\3\4\3\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\6\6\6\u00a3\n\6\r\6\16\6\u00a4\3\7\3\7\3\7\6\7\u00aa"+
		"\n\7\r\7\16\7\u00ab\3\7\3\7\3\b\3\b\3\b\3\b\3\t\6\t\u00b5\n\t\r\t\16\t"+
		"\u00b6\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\6\13\u00c1\n\13\r\13\16\13\u00c2"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\5\16\u00d7\n\16\3\16\3\16\5\16\u00db\n\16\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00ec"+
		"\n\20\f\20\16\20\u00ef\13\20\3\20\3\20\3\20\5\20\u00f4\n\20\3\20\3\20"+
		"\3\21\3\21\5\21\u00fa\n\21\3\21\3\21\5\21\u00fe\n\21\3\21\3\21\5\21\u0102"+
		"\n\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\6\23\u010e\n\23"+
		"\r\23\16\23\u010f\3\23\3\23\5\23\u0114\n\23\3\24\3\24\3\24\7\24\u0119"+
		"\n\24\f\24\16\24\u011c\13\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\7\24\u0125"+
		"\n\24\f\24\16\24\u0128\13\24\3\24\3\24\3\24\5\24\u012d\n\24\3\25\3\25"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\6\26\u0137\n\26\r\26\16\26\u0138\3\26\3"+
		"\26\5\26\u013d\n\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\6\30\u014b\n\30\r\30\16\30\u014c\3\30\3\30\5\30\u0151\n\30"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\5\31\u0164\n\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\5\33\u0170\n\33\3\34\3\34\3\34\3\35\3\35\3\35\6\35\u0178\n"+
		"\35\r\35\16\35\u0179\3\35\3\35\3\36\3\36\3\36\6\36\u0181\n\36\r\36\16"+
		"\36\u0182\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3!\3"+
		"!\3!\5!\u0196\n!\3\"\3\"\3\"\6\"\u019b\n\"\r\"\16\"\u019c\3\"\3\"\3#\3"+
		"#\3#\3$\3$\3$\5$\u01a7\n$\3%\3%\3%\3%\7%\u01ad\n%\f%\16%\u01b0\13%\3%"+
		"\3%\3%\3%\3&\3&\3&\3&\3&\3&\3\'\3\'\5\'\u01be\n\'\3(\3(\3(\3(\3(\3)\3"+
		")\3)\3)\6)\u01c9\n)\r)\16)\u01ca\3)\3)\5)\u01cf\n)\3*\3*\3*\7*\u01d4\n"+
		"*\f*\16*\u01d7\13*\3*\3*\3*\5*\u01dc\n*\3+\3+\3+\3+\3+\3,\3,\3-\7-\u01e6"+
		"\n-\f-\16-\u01e9\13-\3.\7.\u01ec\n.\f.\16.\u01ef\13.\3/\6/\u01f2\n/\r"+
		"/\16/\u01f3\3/\3/\3/\3\60\3\60\3\60\3\60\3\61\6\61\u01fe\n\61\r\61\16"+
		"\61\u01ff\3\62\3\62\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64"+
		"\3\64\3\64\3\64\5\64\u0211\n\64\3\64\5\64\u0214\n\64\3\64\3\64\3\64\5"+
		"\64\u0219\n\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\7\66"+
		"\u0225\n\66\f\66\16\66\u0228\13\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67"+
		"\38\38\38\38\38\2\29\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjln\2\6\3\2\21\24\3\2\25\26\3\2*+\3"+
		"\2\'(\u0238\2r\3\2\2\2\4t\3\2\2\2\6\u009a\3\2\2\2\b\u009c\3\2\2\2\n\u00a2"+
		"\3\2\2\2\f\u00a6\3\2\2\2\16\u00af\3\2\2\2\20\u00b4\3\2\2\2\22\u00b8\3"+
		"\2\2\2\24\u00bd\3\2\2\2\26\u00c6\3\2\2\2\30\u00cb\3\2\2\2\32\u00cf\3\2"+
		"\2\2\34\u00de\3\2\2\2\36\u00e0\3\2\2\2 \u00f9\3\2\2\2\"\u0105\3\2\2\2"+
		"$\u0113\3\2\2\2&\u012c\3\2\2\2(\u012e\3\2\2\2*\u013c\3\2\2\2,\u013e\3"+
		"\2\2\2.\u0150\3\2\2\2\60\u0163\3\2\2\2\62\u0165\3\2\2\2\64\u016f\3\2\2"+
		"\2\66\u0171\3\2\2\28\u0174\3\2\2\2:\u017d\3\2\2\2<\u0186\3\2\2\2>\u018b"+
		"\3\2\2\2@\u0195\3\2\2\2B\u0197\3\2\2\2D\u01a0\3\2\2\2F\u01a6\3\2\2\2H"+
		"\u01a8\3\2\2\2J\u01b5\3\2\2\2L\u01bd\3\2\2\2N\u01bf\3\2\2\2P\u01ce\3\2"+
		"\2\2R\u01db\3\2\2\2T\u01dd\3\2\2\2V\u01e2\3\2\2\2X\u01e7\3\2\2\2Z\u01ed"+
		"\3\2\2\2\\\u01f1\3\2\2\2^\u01f8\3\2\2\2`\u01fd\3\2\2\2b\u0201\3\2\2\2"+
		"d\u0203\3\2\2\2f\u0205\3\2\2\2h\u021c\3\2\2\2j\u0221\3\2\2\2l\u022b\3"+
		"\2\2\2n\u0230\3\2\2\2ps\5\4\3\2qs\5f\64\2rp\3\2\2\2rq\3\2\2\2s\3\3\2\2"+
		"\2tu\7\3\2\2uv\7\4\2\2vw\7\3\2\2wx\7\5\2\2xy\5\6\4\2y{\7\6\2\2z|\5\b\5"+
		"\2{z\3\2\2\2{|\3\2\2\2|~\3\2\2\2}\177\5\f\7\2~}\3\2\2\2~\177\3\2\2\2\177"+
		"\u0081\3\2\2\2\u0080\u0082\5\22\n\2\u0081\u0080\3\2\2\2\u0081\u0082\3"+
		"\2\2\2\u0082\u0084\3\2\2\2\u0083\u0085\5\24\13\2\u0084\u0083\3\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\u0089\3\2\2\2\u0086\u0088\5\30\r\2\u0087\u0086\3"+
		"\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\u008f\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008e\5\36\20\2\u008d\u008c\3"+
		"\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090"+
		"\u0095\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0094\5\62\32\2\u0093\u0092\3"+
		"\2\2\2\u0094\u0097\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096"+
		"\u0098\3\2\2\2\u0097\u0095\3\2\2\2\u0098\u0099\7\6\2\2\u0099\5\3\2\2\2"+
		"\u009a\u009b\7+\2\2\u009b\7\3\2\2\2\u009c\u009d\7\3\2\2\u009d\u009e\7"+
		"\7\2\2\u009e\u009f\5\n\6\2\u009f\u00a0\7\6\2\2\u00a0\t\3\2\2\2\u00a1\u00a3"+
		"\7)\2\2\u00a2\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\13\3\2\2\2\u00a6\u00a7\7\3\2\2\u00a7\u00a9\7\b\2"+
		"\2\u00a8\u00aa\5\16\b\2\u00a9\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\7\6"+
		"\2\2\u00ae\r\3\2\2\2\u00af\u00b0\5\20\t\2\u00b0\u00b1\7\t\2\2\u00b1\u00b2"+
		"\5b\62\2\u00b2\17\3\2\2\2\u00b3\u00b5\7+\2\2\u00b4\u00b3\3\2\2\2\u00b5"+
		"\u00b6\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\21\3\2\2"+
		"\2\u00b8\u00b9\7\3\2\2\u00b9\u00ba\7\n\2\2\u00ba\u00bb\5Z.\2\u00bb\u00bc"+
		"\7\6\2\2\u00bc\23\3\2\2\2\u00bd\u00be\7\3\2\2\u00be\u00c0\7\13\2\2\u00bf"+
		"\u00c1\5\26\f\2\u00c0\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c0\3"+
		"\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\7\6\2\2\u00c5"+
		"\25\3\2\2\2\u00c6\u00c7\7\3\2\2\u00c7\u00c8\5V,\2\u00c8\u00c9\5X-\2\u00c9"+
		"\u00ca\7\6\2\2\u00ca\27\3\2\2\2\u00cb\u00cc\7\3\2\2\u00cc\u00cd\7\f\2"+
		"\2\u00cd\u00ce\5\32\16\2\u00ce\31\3\2\2\2\u00cf\u00d0\5\34\17\2\u00d0"+
		"\u00d1\7\r\2\2\u00d1\u00d2\7\3\2\2\u00d2\u00d3\5X-\2\u00d3\u00d6\7\6\2"+
		"\2\u00d4\u00d5\7\16\2\2\u00d5\u00d7\5\64\33\2\u00d6\u00d4\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d9\7\17\2\2\u00d9\u00db\5"+
		"@!\2\u00da\u00d8\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc"+
		"\u00dd\7\6\2\2\u00dd\33\3\2\2\2\u00de\u00df\7+\2\2\u00df\35\3\2\2\2\u00e0"+
		"\u00e1\7\3\2\2\u00e1\u00e2\7\20\2\2\u00e2\u00e3\5\"\22\2\u00e3\u00e4\7"+
		"\r\2\2\u00e4\u00e5\7\3\2\2\u00e5\u00e6\5X-\2\u00e6\u00e7\7\6\2\2\u00e7"+
		"\u00e8\7\f\2\2\u00e8\u00e9\7\3\2\2\u00e9\u00ed\5\34\17\2\u00ea\u00ec\5"+
		"d\63\2\u00eb\u00ea\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed"+
		"\u00ee\3\2\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0\u00f3\7\6"+
		"\2\2\u00f1\u00f2\7\16\2\2\u00f2\u00f4\5\64\33\2\u00f3\u00f1\3\2\2\2\u00f3"+
		"\u00f4\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\5 \21\2\u00f6\37\3\2\2"+
		"\2\u00f7\u00f8\t\2\2\2\u00f8\u00fa\5$\23\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa"+
		"\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00fc\t\3\2\2\u00fc\u00fe\5*\26\2\u00fd"+
		"\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u0100\7\27"+
		"\2\2\u0100\u0102\5.\30\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102"+
		"\u0103\3\2\2\2\u0103\u0104\7\6\2\2\u0104!\3\2\2\2\u0105\u0106\7+\2\2\u0106"+
		"#\3\2\2\2\u0107\u0108\7\3\2\2\u0108\u0114\7\6\2\2\u0109\u0114\5&\24\2"+
		"\u010a\u010b\7\3\2\2\u010b\u010d\7\30\2\2\u010c\u010e\5&\24\2\u010d\u010c"+
		"\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"\u0111\3\2\2\2\u0111\u0112\7\6\2\2\u0112\u0114\3\2\2\2\u0113\u0107\3\2"+
		"\2\2\u0113\u0109\3\2\2\2\u0113\u010a\3\2\2\2\u0114%\3\2\2\2\u0115\u0116"+
		"\7\3\2\2\u0116\u011a\5\34\17\2\u0117\u0119\5d\63\2\u0118\u0117\3\2\2\2"+
		"\u0119\u011c\3\2\2\2\u011a\u0118\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u011d"+
		"\3\2\2\2\u011c\u011a\3\2\2\2\u011d\u011e\7\6\2\2\u011e\u012d\3\2\2\2\u011f"+
		"\u0120\7\3\2\2\u0120\u0121\5(\25\2\u0121\u0122\7\3\2\2\u0122\u0126\5\34"+
		"\17\2\u0123\u0125\5d\63\2\u0124\u0123\3\2\2\2\u0125\u0128\3\2\2\2\u0126"+
		"\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0129\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0129\u012a\7\6\2\2\u012a\u012b\7\6\2\2\u012b\u012d\3\2\2\2\u012c"+
		"\u0115\3\2\2\2\u012c\u011f\3\2\2\2\u012d\'\3\2\2\2\u012e\u012f\7+\2\2"+
		"\u012f)\3\2\2\2\u0130\u0131\7\3\2\2\u0131\u013d\7\6\2\2\u0132\u013d\5"+
		",\27\2\u0133\u0134\7\3\2\2\u0134\u0136\7\30\2\2\u0135\u0137\5,\27\2\u0136"+
		"\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0136\3\2\2\2\u0138\u0139\3\2"+
		"\2\2\u0139\u013a\3\2\2\2\u013a\u013b\7\6\2\2\u013b\u013d\3\2\2\2\u013c"+
		"\u0130\3\2\2\2\u013c\u0132\3\2\2\2\u013c\u0133\3\2\2\2\u013d+\3\2\2\2"+
		"\u013e\u013f\7\3\2\2\u013f\u0140\5(\25\2\u0140\u0141\7\31\2\2\u0141\u0142"+
		"\5(\25\2\u0142\u0143\7\6\2\2\u0143-\3\2\2\2\u0144\u0145\7\3\2\2\u0145"+
		"\u0151\7\6\2\2\u0146\u0151\5\60\31\2\u0147\u0148\7\3\2\2\u0148\u014a\7"+
		"\30\2\2\u0149\u014b\5\60\31\2\u014a\u0149\3\2\2\2\u014b\u014c\3\2\2\2"+
		"\u014c\u014a\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014f"+
		"\7\6\2\2\u014f\u0151\3\2\2\2\u0150\u0144\3\2\2\2\u0150\u0146\3\2\2\2\u0150"+
		"\u0147\3\2\2\2\u0151/\3\2\2\2\u0152\u0153\7\3\2\2\u0153\u0164\7\6\2\2"+
		"\u0154\u0155\7\3\2\2\u0155\u0156\7\32\2\2\u0156\u0157\7\3\2\2\u0157\u0158"+
		"\7\33\2\2\u0158\u0159\5d\63\2\u0159\u015a\5d\63\2\u015a\u015b\7\6\2\2"+
		"\u015b\u015c\7\6\2\2\u015c\u0164\3\2\2\2\u015d\u015e\7\3\2\2\u015e\u015f"+
		"\7\33\2\2\u015f\u0160\5d\63\2\u0160\u0161\5d\63\2\u0161\u0162\7\6\2\2"+
		"\u0162\u0164\3\2\2\2\u0163\u0152\3\2\2\2\u0163\u0154\3\2\2\2\u0163\u015d"+
		"\3\2\2\2\u0164\61\3\2\2\2\u0165\u0166\7\3\2\2\u0166\u0167\7\34\2\2\u0167"+
		"\u0168\5\32\16\2\u0168\63\3\2\2\2\u0169\u0170\5\66\34\2\u016a\u0170\5"+
		"R*\2\u016b\u0170\5<\37\2\u016c\u0170\58\35\2\u016d\u0170\5:\36\2\u016e"+
		"\u0170\5> \2\u016f\u0169\3\2\2\2\u016f\u016a\3\2\2\2\u016f\u016b\3\2\2"+
		"\2\u016f\u016c\3\2\2\2\u016f\u016d\3\2\2\2\u016f\u016e\3\2\2\2\u0170\65"+
		"\3\2\2\2\u0171\u0172\7\3\2\2\u0172\u0173\7\6\2\2\u0173\67\3\2\2\2\u0174"+
		"\u0175\7\3\2\2\u0175\u0177\7\30\2\2\u0176\u0178\5\64\33\2\u0177\u0176"+
		"\3\2\2\2\u0178\u0179\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a"+
		"\u017b\3\2\2\2\u017b\u017c\7\6\2\2\u017c9\3\2\2\2\u017d\u017e\7\3\2\2"+
		"\u017e\u0180\7\35\2\2\u017f\u0181\5\64\33\2\u0180\u017f\3\2\2\2\u0181"+
		"\u0182\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2"+
		"\2\2\u0184\u0185\7\6\2\2\u0185;\3\2\2\2\u0186\u0187\7\3\2\2\u0187\u0188"+
		"\7\32\2\2\u0188\u0189\5\64\33\2\u0189\u018a\7\6\2\2\u018a=\3\2\2\2\u018b"+
		"\u018c\7\36\2\2\u018c\u018d\7\3\2\2\u018d\u018e\5X-\2\u018e\u018f\7\6"+
		"\2\2\u018f\u0190\5\64\33\2\u0190\u0191\7\6\2\2\u0191?\3\2\2\2\u0192\u0196"+
		"\5D#\2\u0193\u0196\5F$\2\u0194\u0196\5B\"\2\u0195\u0192\3\2\2\2\u0195"+
		"\u0193\3\2\2\2\u0195\u0194\3\2\2\2\u0196A\3\2\2\2\u0197\u0198\7\3\2\2"+
		"\u0198\u019a\7\30\2\2\u0199\u019b\5F$\2\u019a\u0199\3\2\2\2\u019b\u019c"+
		"\3\2\2\2\u019c\u019a\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019e\3\2\2\2\u019e"+
		"\u019f\7\6\2\2\u019fC\3\2\2\2\u01a0\u01a1\7\3\2\2\u01a1\u01a2\7\6\2\2"+
		"\u01a2E\3\2\2\2\u01a3\u01a7\5L\'\2\u01a4\u01a7\5H%\2\u01a5\u01a7\5J&\2"+
		"\u01a6\u01a3\3\2\2\2\u01a6\u01a4\3\2\2\2\u01a6\u01a5\3\2\2\2\u01a7G\3"+
		"\2\2\2\u01a8\u01a9\7\3\2\2\u01a9\u01aa\7\37\2\2\u01aa\u01ae\7\3\2\2\u01ab"+
		"\u01ad\5d\63\2\u01ac\u01ab\3\2\2\2\u01ad\u01b0\3\2\2\2\u01ae\u01ac\3\2"+
		"\2\2\u01ae\u01af\3\2\2\2\u01af\u01b1\3\2\2\2\u01b0\u01ae\3\2\2\2\u01b1"+
		"\u01b2\7\6\2\2\u01b2\u01b3\5@!\2\u01b3\u01b4\7\6\2\2\u01b4I\3\2\2\2\u01b5"+
		"\u01b6\7\3\2\2\u01b6\u01b7\7 \2\2\u01b7\u01b8\5\64\33\2\u01b8\u01b9\5"+
		"P)\2\u01b9\u01ba\7\6\2\2\u01baK\3\2\2\2\u01bb\u01be\5N(\2\u01bc\u01be"+
		"\5R*\2\u01bd\u01bb\3\2\2\2\u01bd\u01bc\3\2\2\2\u01beM\3\2\2\2\u01bf\u01c0"+
		"\7\3\2\2\u01c0\u01c1\7\32\2\2\u01c1\u01c2\5R*\2\u01c2\u01c3\7\6\2\2\u01c3"+
		"O\3\2\2\2\u01c4\u01cf\5L\'\2\u01c5\u01c6\7\3\2\2\u01c6\u01c8\7\30\2\2"+
		"\u01c7\u01c9\5L\'\2\u01c8\u01c7\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u01c8"+
		"\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cd\7\6\2\2\u01cd"+
		"\u01cf\3\2\2\2\u01ce\u01c4\3\2\2\2\u01ce\u01c5\3\2\2\2\u01cfQ\3\2\2\2"+
		"\u01d0\u01d1\7\3\2\2\u01d1\u01d5\5V,\2\u01d2\u01d4\5d\63\2\u01d3\u01d2"+
		"\3\2\2\2\u01d4\u01d7\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6"+
		"\u01d8\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d8\u01d9\7\6\2\2\u01d9\u01dc\3\2"+
		"\2\2\u01da\u01dc\5T+\2\u01db\u01d0\3\2\2\2\u01db\u01da\3\2\2\2\u01dcS"+
		"\3\2\2\2\u01dd\u01de\7!\2\2\u01de\u01df\5d\63\2\u01df\u01e0\5d\63\2\u01e0"+
		"\u01e1\7\6\2\2\u01e1U\3\2\2\2\u01e2\u01e3\7+\2\2\u01e3W\3\2\2\2\u01e4"+
		"\u01e6\5\\/\2\u01e5\u01e4\3\2\2\2\u01e6\u01e9\3\2\2\2\u01e7\u01e5\3\2"+
		"\2\2\u01e7\u01e8\3\2\2\2\u01e8Y\3\2\2\2\u01e9\u01e7\3\2\2\2\u01ea\u01ec"+
		"\5^\60\2\u01eb\u01ea\3\2\2\2\u01ec\u01ef\3\2\2\2\u01ed\u01eb\3\2\2\2\u01ed"+
		"\u01ee\3\2\2\2\u01ee[\3\2\2\2\u01ef\u01ed\3\2\2\2\u01f0\u01f2\7*\2\2\u01f1"+
		"\u01f0\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f1\3\2\2\2\u01f3\u01f4\3\2"+
		"\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f6\7\t\2\2\u01f6\u01f7\5b\62\2\u01f7"+
		"]\3\2\2\2\u01f8\u01f9\5`\61\2\u01f9\u01fa\7\t\2\2\u01fa\u01fb\5b\62\2"+
		"\u01fb_\3\2\2\2\u01fc\u01fe\7+\2\2\u01fd\u01fc\3\2\2\2\u01fe\u01ff\3\2"+
		"\2\2\u01ff\u01fd\3\2\2\2\u01ff\u0200\3\2\2\2\u0200a\3\2\2\2\u0201\u0202"+
		"\7+\2\2\u0202c\3\2\2\2\u0203\u0204\t\4\2\2\u0204e\3\2\2\2\u0205\u0206"+
		"\7\3\2\2\u0206\u0207\7\4\2\2\u0207\u0208\7\3\2\2\u0208\u0209\7\"\2\2\u0209"+
		"\u020a\7+\2\2\u020a\u020b\7\6\2\2\u020b\u020c\7\3\2\2\u020c\u020d\7#\2"+
		"\2\u020d\u020e\7+\2\2\u020e\u0210\7\6\2\2\u020f\u0211\5\b\5\2\u0210\u020f"+
		"\3\2\2\2\u0210\u0211\3\2\2\2\u0211\u0213\3\2\2\2\u0212\u0214\5h\65\2\u0213"+
		"\u0212\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u0215\3\2\2\2\u0215\u0216\5n"+
		"8\2\u0216\u0218\5j\66\2\u0217\u0219\5l\67\2\u0218\u0217\3\2\2\2\u0218"+
		"\u0219\3\2\2\2\u0219\u021a\3\2\2\2\u021a\u021b\7\6\2\2\u021bg\3\2\2\2"+
		"\u021c\u021d\7\3\2\2\u021d\u021e\7$\2\2\u021e\u021f\5Z.\2\u021f\u0220"+
		"\7\6\2\2\u0220i\3\2\2\2\u0221\u0222\7\3\2\2\u0222\u0226\7%\2\2\u0223\u0225"+
		"\5L\'\2\u0224\u0223\3\2\2\2\u0225\u0228\3\2\2\2\u0226\u0224\3\2\2\2\u0226"+
		"\u0227\3\2\2\2\u0227\u0229\3\2\2\2\u0228\u0226\3\2\2\2\u0229\u022a\7\6"+
		"\2\2\u022ak\3\2\2\2\u022b\u022c\7\3\2\2\u022c\u022d\7&\2\2\u022d\u022e"+
		"\5\64\33\2\u022e\u022f\7\6\2\2\u022fm\3\2\2\2\u0230\u0231\7\3\2\2\u0231"+
		"\u0232\t\5\2\2\u0232\u0233\5 \21\2\u0233o\3\2\2\2\63r{~\u0081\u0084\u0089"+
		"\u008f\u0095\u00a4\u00ab\u00b6\u00c2\u00d6\u00da\u00ed\u00f3\u00f9\u00fd"+
		"\u0101\u010f\u0113\u011a\u0126\u012c\u0138\u013c\u014c\u0150\u0163\u016f"+
		"\u0179\u0182\u0195\u019c\u01a6\u01ae\u01bd\u01ca\u01ce\u01d5\u01db\u01e7"+
		"\u01ed\u01f3\u01ff\u0210\u0213\u0218\u0226";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}