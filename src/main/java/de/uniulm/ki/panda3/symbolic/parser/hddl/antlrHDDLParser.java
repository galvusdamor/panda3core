// Generated from /home/mars/uni/git/ki/panda3core/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4 by ANTLR 4.7
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
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

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
		RULE_gd_negation = 32, RULE_gd_implication = 33, RULE_gd_existential = 34, 
		RULE_gd_universal = 35, RULE_gd_equality_constraint = 36, RULE_effect = 37, 
		RULE_eff_empty = 38, RULE_eff_conjunction = 39, RULE_eff_universal = 40, 
		RULE_eff_conditional = 41, RULE_literal = 42, RULE_neg_atomic_formula = 43, 
		RULE_p_effect = 44, RULE_assign_op = 45, RULE_f_head = 46, RULE_f_exp = 47, 
		RULE_bin_op = 48, RULE_multi_op = 49, RULE_atomic_formula = 50, RULE_predicate = 51, 
		RULE_equallity = 52, RULE_typed_var_list = 53, RULE_typed_obj_list = 54, 
		RULE_typed_vars = 55, RULE_typed_var = 56, RULE_typed_objs = 57, RULE_new_consts = 58, 
		RULE_var_type = 59, RULE_var_or_const = 60, RULE_term = 61, RULE_functionterm = 62, 
		RULE_func_symbol = 63, RULE_problem = 64, RULE_p_object_declaration = 65, 
		RULE_p_init = 66, RULE_init_el = 67, RULE_num_init = 68, RULE_p_goal = 69, 
		RULE_p_htn = 70, RULE_metric_spec = 71, RULE_optimization = 72, RULE_ground_f_exp = 73;
	public static final String[] ruleNames = {
		"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
		"type_def", "type_def_list", "new_types", "const_def", "predicates_def", 
		"atomic_formula_skeleton", "funtions_def", "comp_task_def", "task_def", 
		"task_symbol", "method_def", "tasknetwork_def", "method_symbol", "subtask_defs", 
		"subtask_def", "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", 
		"constraint_def", "causallink_defs", "causallink_def", "action_def", "gd", 
		"gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", "gd_implication", 
		"gd_existential", "gd_universal", "gd_equality_constraint", "effect", 
		"eff_empty", "eff_conjunction", "eff_universal", "eff_conditional", "literal", 
		"neg_atomic_formula", "p_effect", "assign_op", "f_head", "f_exp", "bin_op", 
		"multi_op", "atomic_formula", "predicate", "equallity", "typed_var_list", 
		"typed_obj_list", "typed_vars", "typed_var", "typed_objs", "new_consts", 
		"var_type", "var_or_const", "term", "functionterm", "func_symbol", "problem", 
		"p_object_declaration", "p_init", "init_el", "num_init", "p_goal", "p_htn", 
		"metric_spec", "optimization", "ground_f_exp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'", 
		"'-'", "':constants'", "':predicates'", "':functions'", "'number'", "':task'", 
		"':parameters'", "':precondition'", "':effect'", "':method'", "':subtasks'", 
		"':tasks'", "':ordered-subtasks'", "':ordered-tasks'", "':ordering'", 
		"':order'", "':constraints'", "':causal-links'", "':causallinks'", "'and'", 
		"'<'", "'not'", "'type'", "'typeof'", "'sort'", "'sortof'", "':action'", 
		"'or'", "'imply'", "'exists'", "'forall'", "'when'", "'assign'", "'scale-down'", 
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
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
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
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
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
			setState(205);
			match(T__0);
			setState(206);
			match(T__5);
			setState(207);
			type_def_list();
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
			setState(221);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(210);
					match(NAME);
					}
					}
					setState(215);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(216);
				new_types();
				setState(217);
				match(T__6);
				setState(218);
				var_type();
				setState(219);
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
			setState(224); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(223);
				match(NAME);
				}
				}
				setState(226); 
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
			setState(228);
			match(T__0);
			setState(229);
			match(T__7);
			setState(230);
			typed_obj_list();
			setState(231);
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
			setState(233);
			match(T__0);
			setState(234);
			match(T__8);
			setState(236); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(235);
				atomic_formula_skeleton();
				}
				}
				setState(238); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(240);
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
			setState(242);
			match(T__0);
			setState(243);
			predicate();
			setState(244);
			typed_var_list();
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
			setState(247);
			match(T__0);
			setState(248);
			match(T__9);
			setState(255); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(249);
				atomic_formula_skeleton();
				setState(253);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(250);
					match(T__6);
					setState(251);
					match(T__10);
					}
					break;
				case 2:
					{
					setState(252);
					var_type();
					}
					break;
				}
				}
				}
				setState(257); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(259);
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
			setState(261);
			match(T__0);
			setState(262);
			match(T__11);
			setState(263);
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
			setState(265);
			task_symbol();
			setState(266);
			match(T__12);
			setState(267);
			match(T__0);
			setState(268);
			typed_var_list();
			setState(269);
			match(T__3);
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(270);
				match(T__13);
				setState(271);
				gd();
				}
			}

			setState(276);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(274);
				match(T__14);
				setState(275);
				effect();
				}
			}

			setState(278);
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
			setState(280);
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
			setState(282);
			match(T__0);
			setState(283);
			match(T__15);
			setState(284);
			method_symbol();
			setState(285);
			match(T__12);
			setState(286);
			match(T__0);
			setState(287);
			typed_var_list();
			setState(288);
			match(T__3);
			setState(289);
			match(T__11);
			setState(290);
			match(T__0);
			setState(291);
			task_symbol();
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(292);
				var_or_const();
				}
				}
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(298);
			match(T__3);
			setState(301);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(299);
				match(T__13);
				setState(300);
				gd();
				}
			}

			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(303);
				match(T__14);
				setState(304);
				effect();
				}
			}

			setState(307);
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
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(309);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(310);
				subtask_defs();
				}
			}

			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(313);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(314);
				ordering_defs();
				}
			}

			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(317);
				match(T__22);
				setState(318);
				constraint_defs();
				}
			}

			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__23 || _la==T__24) {
				{
				setState(321);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==T__24) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(322);
				causallink_defs();
				}
			}

			setState(325);
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
			setState(327);
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
			setState(341);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(329);
				match(T__0);
				setState(330);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(331);
				subtask_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(332);
				match(T__0);
				setState(333);
				match(T__25);
				setState(335); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(334);
					subtask_def();
					}
					}
					setState(337); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(339);
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
			setState(366);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(343);
				match(T__0);
				setState(344);
				task_symbol();
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(345);
					var_or_const();
					}
					}
					setState(350);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(351);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(353);
				match(T__0);
				setState(354);
				subtask_id();
				setState(355);
				match(T__0);
				setState(356);
				task_symbol();
				setState(360);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VAR_NAME || _la==NAME) {
					{
					{
					setState(357);
					var_or_const();
					}
					}
					setState(362);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(363);
				match(T__3);
				setState(364);
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
			setState(368);
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
			setState(382);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(370);
				match(T__0);
				setState(371);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(372);
				ordering_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(373);
				match(T__0);
				setState(374);
				match(T__25);
				setState(376); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(375);
					ordering_def();
					}
					}
					setState(378); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(380);
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
			setState(384);
			match(T__0);
			setState(385);
			subtask_id();
			setState(386);
			match(T__26);
			setState(387);
			subtask_id();
			setState(388);
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
			setState(402);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(390);
				match(T__0);
				setState(391);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(392);
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(393);
				match(T__0);
				setState(394);
				match(T__25);
				setState(396); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(395);
					constraint_def();
					}
					}
					setState(398); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__47 );
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
			setState(432);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(404);
				match(T__0);
				setState(405);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(406);
				match(T__0);
				setState(407);
				match(T__27);
				setState(408);
				equallity();
				setState(409);
				var_or_const();
				setState(410);
				var_or_const();
				setState(411);
				match(T__3);
				setState(412);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(414);
				equallity();
				setState(415);
				var_or_const();
				setState(416);
				var_or_const();
				setState(417);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(419);
				match(T__0);
				setState(420);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(421);
				typed_var();
				setState(422);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(424);
				match(T__0);
				setState(425);
				match(T__27);
				setState(426);
				match(T__0);
				setState(427);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(428);
				typed_var();
				setState(429);
				match(T__3);
				setState(430);
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
			setState(446);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(434);
				match(T__0);
				setState(435);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(436);
				causallink_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(437);
				match(T__0);
				setState(438);
				match(T__25);
				setState(440); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(439);
					causallink_def();
					}
					}
					setState(442); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(444);
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
			setState(448);
			match(T__0);
			setState(449);
			subtask_id();
			setState(450);
			literal();
			setState(451);
			subtask_id();
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
			setState(454);
			match(T__0);
			setState(455);
			match(T__32);
			setState(456);
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
		public GdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd; }
	}

	public final GdContext gd() throws RecognitionException {
		GdContext _localctx = new GdContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_gd);
		try {
			setState(467);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(458);
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(459);
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(460);
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(461);
				gd_implication();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(462);
				gd_conjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(463);
				gd_disjuction();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(464);
				gd_existential();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(465);
				gd_universal();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(466);
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
			setState(469);
			match(T__0);
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
			setState(472);
			match(T__0);
			setState(473);
			match(T__25);
			setState(475); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(474);
				gd();
				}
				}
				setState(477); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__47 );
			setState(479);
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
			setState(481);
			match(T__0);
			setState(482);
			match(T__33);
			setState(484); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(483);
				gd();
				}
				}
				setState(486); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__47 );
			setState(488);
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
			setState(490);
			match(T__0);
			setState(491);
			match(T__27);
			setState(492);
			gd();
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
			setState(495);
			match(T__0);
			setState(496);
			match(T__34);
			setState(497);
			gd();
			setState(498);
			gd();
			setState(499);
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
			setState(501);
			match(T__0);
			setState(502);
			match(T__35);
			setState(503);
			match(T__0);
			setState(504);
			typed_var_list();
			setState(505);
			match(T__3);
			setState(506);
			gd();
			setState(507);
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
			setState(509);
			match(T__0);
			setState(510);
			match(T__36);
			setState(511);
			match(T__0);
			setState(512);
			typed_var_list();
			setState(513);
			match(T__3);
			setState(514);
			gd();
			setState(515);
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
			setState(517);
			equallity();
			setState(518);
			var_or_const();
			setState(519);
			var_or_const();
			setState(520);
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
		enterRule(_localctx, 74, RULE_effect);
		try {
			setState(528);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(522);
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(523);
				eff_conjunction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(524);
				eff_universal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(525);
				eff_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(526);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(527);
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
		enterRule(_localctx, 76, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(530);
			match(T__0);
			setState(531);
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
		enterRule(_localctx, 78, RULE_eff_conjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(533);
			match(T__0);
			setState(534);
			match(T__25);
			setState(536); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(535);
				effect();
				}
				}
				setState(538); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(540);
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
		enterRule(_localctx, 80, RULE_eff_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			match(T__0);
			setState(543);
			match(T__36);
			setState(544);
			match(T__0);
			setState(545);
			typed_var_list();
			setState(546);
			match(T__3);
			setState(547);
			effect();
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
		enterRule(_localctx, 82, RULE_eff_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550);
			match(T__0);
			setState(551);
			match(T__37);
			setState(552);
			gd();
			setState(553);
			effect();
			setState(554);
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
			setState(558);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(556);
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(557);
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
			setState(560);
			match(T__0);
			setState(561);
			match(T__27);
			setState(562);
			atomic_formula();
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
		enterRule(_localctx, 88, RULE_p_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			match(T__0);
			setState(566);
			assign_op();
			setState(567);
			f_head();
			setState(568);
			f_exp();
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

	public static class Assign_opContext extends ParserRuleContext {
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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
		enterRule(_localctx, 92, RULE_f_head);
		int _la;
		try {
			setState(584);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(573);
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(574);
				match(T__0);
				setState(575);
				func_symbol();
				setState(579);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (VAR_NAME - 1)) | (1L << (NAME - 1)))) != 0)) {
					{
					{
					setState(576);
					term();
					}
					}
					setState(581);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(582);
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
		enterRule(_localctx, 94, RULE_f_exp);
		int _la;
		try {
			setState(609);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(586);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(587);
				match(T__0);
				setState(588);
				bin_op();
				setState(589);
				f_exp();
				setState(590);
				f_exp();
				setState(591);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(593);
				match(T__0);
				setState(594);
				multi_op();
				setState(595);
				f_exp();
				setState(597); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(596);
					f_exp();
					}
					}
					setState(599); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME || _la==NUMBER );
				setState(601);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(603);
				match(T__0);
				setState(604);
				match(T__6);
				setState(605);
				f_exp();
				setState(606);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(608);
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
		enterRule(_localctx, 96, RULE_bin_op);
		try {
			setState(614);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__44:
			case T__45:
				enterOuterAlt(_localctx, 1);
				{
				setState(611);
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(612);
				match(T__6);
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 3);
				{
				setState(613);
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
		enterRule(_localctx, 98, RULE_multi_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(616);
			_la = _input.LA(1);
			if ( !(_la==T__44 || _la==T__45) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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
		enterRule(_localctx, 100, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
			match(T__0);
			setState(619);
			predicate();
			setState(623);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(620);
				var_or_const();
				}
				}
				setState(625);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(626);
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
		enterRule(_localctx, 102, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
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
		enterRule(_localctx, 104, RULE_equallity);
		try {
			setState(633);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(630);
				match(T__0);
				setState(631);
				match(T__46);
				}
				break;
			case T__47:
				enterOuterAlt(_localctx, 2);
				{
				setState(632);
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
		enterRule(_localctx, 106, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(635);
				typed_vars();
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
		enterRule(_localctx, 108, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(644);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(641);
				typed_objs();
				}
				}
				setState(646);
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
		enterRule(_localctx, 110, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(648); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(647);
				match(VAR_NAME);
				}
				}
				setState(650); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(652);
			match(T__6);
			setState(653);
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
		enterRule(_localctx, 112, RULE_typed_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(655);
			match(VAR_NAME);
			setState(656);
			match(T__6);
			setState(657);
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
			setState(660); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(659);
				new_consts();
				}
				}
				setState(662); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(664);
			match(T__6);
			setState(665);
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
			setState(667);
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
		enterRule(_localctx, 118, RULE_var_type);
		int _la;
		try {
			setState(679);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(669);
				match(NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				match(T__0);
				setState(671);
				match(T__48);
				setState(673); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(672);
					var_type();
					}
					}
					setState(675); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME );
				setState(677);
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
		enterRule(_localctx, 120, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(681);
			_la = _input.LA(1);
			if ( !(_la==VAR_NAME || _la==NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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
			setState(686);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(683);
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(684);
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(685);
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
			setState(688);
			match(T__0);
			setState(689);
			func_symbol();
			setState(693);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (VAR_NAME - 1)) | (1L << (NAME - 1)))) != 0)) {
				{
				{
				setState(690);
				term();
				}
				}
				setState(695);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(696);
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
			setState(698);
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
			setState(700);
			match(T__0);
			setState(701);
			match(T__1);
			setState(702);
			match(T__0);
			setState(703);
			match(T__49);
			setState(704);
			match(NAME);
			setState(705);
			match(T__3);
			setState(706);
			match(T__0);
			setState(707);
			match(T__50);
			setState(708);
			match(NAME);
			setState(709);
			match(T__3);
			setState(711);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(710);
				require_def();
				}
				break;
			}
			setState(714);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(713);
				p_object_declaration();
				}
				break;
			}
			setState(717);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(716);
				p_htn();
				}
				break;
			}
			setState(719);
			p_init();
			setState(721);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(720);
				p_goal();
				}
				break;
			}
			setState(724);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(723);
				metric_spec();
				}
			}

			setState(726);
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
			setState(728);
			match(T__0);
			setState(729);
			match(T__51);
			setState(730);
			typed_obj_list();
			setState(731);
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
			setState(733);
			match(T__0);
			setState(734);
			match(T__52);
			setState(738);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__47) {
				{
				{
				setState(735);
				init_el();
				}
				}
				setState(740);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(741);
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
			setState(745);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(743);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(744);
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
			setState(747);
			equallity();
			setState(748);
			f_head();
			setState(749);
			match(NUMBER);
			setState(750);
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
			setState(752);
			match(T__0);
			setState(753);
			match(T__53);
			setState(754);
			gd();
			setState(755);
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
		enterRule(_localctx, 140, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(757);
			match(T__0);
			setState(758);
			_la = _input.LA(1);
			if ( !(_la==T__54 || _la==T__55) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(764);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(759);
				match(T__12);
				setState(760);
				match(T__0);
				setState(761);
				typed_var_list();
				setState(762);
				match(T__3);
				}
			}

			setState(766);
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
			setState(768);
			match(T__0);
			setState(769);
			match(T__56);
			setState(770);
			optimization();
			setState(771);
			ground_f_exp();
			setState(772);
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
			setState(774);
			_la = _input.LA(1);
			if ( !(_la==T__57 || _la==T__58) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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
			setState(803);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(776);
				match(T__0);
				setState(777);
				bin_op();
				setState(778);
				ground_f_exp();
				setState(779);
				ground_f_exp();
				setState(780);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(785);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(782);
					match(T__0);
					setState(783);
					match(T__6);
					}
					break;
				case T__59:
					{
					setState(784);
					match(T__59);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(787);
				ground_f_exp();
				setState(788);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(790);
				match(NUMBER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(791);
				match(T__0);
				setState(792);
				func_symbol();
				setState(796);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(793);
					match(NAME);
					}
					}
					setState(798);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(799);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(801);
				match(T__60);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(802);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3E\u0328\4\2\t\2\4"+
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
		"\4\3\5\3\5\3\5\3\5\3\5\3\6\6\6\u00cc\n\6\r\6\16\6\u00cd\3\7\3\7\3\7\3"+
		"\7\3\7\3\b\7\b\u00d6\n\b\f\b\16\b\u00d9\13\b\3\b\3\b\3\b\3\b\3\b\5\b\u00e0"+
		"\n\b\3\t\6\t\u00e3\n\t\r\t\16\t\u00e4\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3"+
		"\13\6\13\u00ef\n\13\r\13\16\13\u00f0\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\5\r\u0100\n\r\6\r\u0102\n\r\r\r\16\r\u0103\3\r\3"+
		"\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0113\n"+
		"\17\3\17\3\17\5\17\u0117\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u0128\n\21\f\21\16\21\u012b\13"+
		"\21\3\21\3\21\3\21\5\21\u0130\n\21\3\21\3\21\5\21\u0134\n\21\3\21\3\21"+
		"\3\22\3\22\5\22\u013a\n\22\3\22\3\22\5\22\u013e\n\22\3\22\3\22\5\22\u0142"+
		"\n\22\3\22\3\22\5\22\u0146\n\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\6\24\u0152\n\24\r\24\16\24\u0153\3\24\3\24\5\24\u0158\n\24"+
		"\3\25\3\25\3\25\7\25\u015d\n\25\f\25\16\25\u0160\13\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\7\25\u0169\n\25\f\25\16\25\u016c\13\25\3\25\3\25"+
		"\3\25\5\25\u0171\n\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\6\27\u017b"+
		"\n\27\r\27\16\27\u017c\3\27\3\27\5\27\u0181\n\27\3\30\3\30\3\30\3\30\3"+
		"\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\6\31\u018f\n\31\r\31\16\31\u0190"+
		"\3\31\3\31\5\31\u0195\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u01b3\n\32\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\6\33\u01bb\n\33\r\33\16\33\u01bc\3\33\3\33\5\33\u01c1\n\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\5\36\u01d6\n\36\3\37\3\37\3\37\3 \3 \3 \6 \u01de\n \r"+
		" \16 \u01df\3 \3 \3!\3!\3!\6!\u01e7\n!\r!\16!\u01e8\3!\3!\3\"\3\"\3\""+
		"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3"+
		"%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0213\n\'\3(\3(\3(\3)"+
		"\3)\3)\6)\u021b\n)\r)\16)\u021c\3)\3)\3*\3*\3*\3*\3*\3*\3*\3*\3+\3+\3"+
		"+\3+\3+\3+\3,\3,\5,\u0231\n,\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/\3"+
		"\60\3\60\3\60\3\60\7\60\u0244\n\60\f\60\16\60\u0247\13\60\3\60\3\60\5"+
		"\60\u024b\n\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61"+
		"\6\61\u0258\n\61\r\61\16\61\u0259\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3"+
		"\61\5\61\u0264\n\61\3\62\3\62\3\62\5\62\u0269\n\62\3\63\3\63\3\64\3\64"+
		"\3\64\7\64\u0270\n\64\f\64\16\64\u0273\13\64\3\64\3\64\3\65\3\65\3\66"+
		"\3\66\3\66\5\66\u027c\n\66\3\67\7\67\u027f\n\67\f\67\16\67\u0282\13\67"+
		"\38\78\u0285\n8\f8\168\u0288\138\39\69\u028b\n9\r9\169\u028c\39\39\39"+
		"\3:\3:\3:\3:\3;\6;\u0297\n;\r;\16;\u0298\3;\3;\3;\3<\3<\3=\3=\3=\3=\6"+
		"=\u02a4\n=\r=\16=\u02a5\3=\3=\5=\u02aa\n=\3>\3>\3?\3?\3?\5?\u02b1\n?\3"+
		"@\3@\3@\7@\u02b6\n@\f@\16@\u02b9\13@\3@\3@\3A\3A\3B\3B\3B\3B\3B\3B\3B"+
		"\3B\3B\3B\3B\5B\u02ca\nB\3B\5B\u02cd\nB\3B\5B\u02d0\nB\3B\3B\5B\u02d4"+
		"\nB\3B\5B\u02d7\nB\3B\3B\3C\3C\3C\3C\3C\3D\3D\3D\7D\u02e3\nD\fD\16D\u02e6"+
		"\13D\3D\3D\3E\3E\5E\u02ec\nE\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3H\3H\3H\3"+
		"H\3H\3H\3H\5H\u02ff\nH\3H\3H\3I\3I\3I\3I\3I\3I\3J\3J\3K\3K\3K\3K\3K\3"+
		"K\3K\3K\3K\5K\u0314\nK\3K\3K\3K\3K\3K\3K\3K\7K\u031d\nK\fK\16K\u0320\13"+
		"K\3K\3K\3K\3K\5K\u0326\nK\3K\2\2L\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082"+
		"\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\2\13\3\2\23\26"+
		"\3\2\27\30\3\2\32\33\3\2\37\"\3\2)-\3\2/\60\3\2AB\3\29:\3\2<=\2\u033e"+
		"\2\u0098\3\2\2\2\4\u009a\3\2\2\2\6\u00c3\3\2\2\2\b\u00c5\3\2\2\2\n\u00cb"+
		"\3\2\2\2\f\u00cf\3\2\2\2\16\u00df\3\2\2\2\20\u00e2\3\2\2\2\22\u00e6\3"+
		"\2\2\2\24\u00eb\3\2\2\2\26\u00f4\3\2\2\2\30\u00f9\3\2\2\2\32\u0107\3\2"+
		"\2\2\34\u010b\3\2\2\2\36\u011a\3\2\2\2 \u011c\3\2\2\2\"\u0139\3\2\2\2"+
		"$\u0149\3\2\2\2&\u0157\3\2\2\2(\u0170\3\2\2\2*\u0172\3\2\2\2,\u0180\3"+
		"\2\2\2.\u0182\3\2\2\2\60\u0194\3\2\2\2\62\u01b2\3\2\2\2\64\u01c0\3\2\2"+
		"\2\66\u01c2\3\2\2\28\u01c8\3\2\2\2:\u01d5\3\2\2\2<\u01d7\3\2\2\2>\u01da"+
		"\3\2\2\2@\u01e3\3\2\2\2B\u01ec\3\2\2\2D\u01f1\3\2\2\2F\u01f7\3\2\2\2H"+
		"\u01ff\3\2\2\2J\u0207\3\2\2\2L\u0212\3\2\2\2N\u0214\3\2\2\2P\u0217\3\2"+
		"\2\2R\u0220\3\2\2\2T\u0228\3\2\2\2V\u0230\3\2\2\2X\u0232\3\2\2\2Z\u0237"+
		"\3\2\2\2\\\u023d\3\2\2\2^\u024a\3\2\2\2`\u0263\3\2\2\2b\u0268\3\2\2\2"+
		"d\u026a\3\2\2\2f\u026c\3\2\2\2h\u0276\3\2\2\2j\u027b\3\2\2\2l\u0280\3"+
		"\2\2\2n\u0286\3\2\2\2p\u028a\3\2\2\2r\u0291\3\2\2\2t\u0296\3\2\2\2v\u029d"+
		"\3\2\2\2x\u02a9\3\2\2\2z\u02ab\3\2\2\2|\u02b0\3\2\2\2~\u02b2\3\2\2\2\u0080"+
		"\u02bc\3\2\2\2\u0082\u02be\3\2\2\2\u0084\u02da\3\2\2\2\u0086\u02df\3\2"+
		"\2\2\u0088\u02eb\3\2\2\2\u008a\u02ed\3\2\2\2\u008c\u02f2\3\2\2\2\u008e"+
		"\u02f7\3\2\2\2\u0090\u0302\3\2\2\2\u0092\u0308\3\2\2\2\u0094\u0325\3\2"+
		"\2\2\u0096\u0099\5\4\3\2\u0097\u0099\5\u0082B\2\u0098\u0096\3\2\2\2\u0098"+
		"\u0097\3\2\2\2\u0099\3\3\2\2\2\u009a\u009b\7\3\2\2\u009b\u009c\7\4\2\2"+
		"\u009c\u009d\7\3\2\2\u009d\u009e\7\5\2\2\u009e\u009f\5\6\4\2\u009f\u00a1"+
		"\7\6\2\2\u00a0\u00a2\5\b\5\2\u00a1\u00a0\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\u00a4\3\2\2\2\u00a3\u00a5\5\f\7\2\u00a4\u00a3\3\2\2\2\u00a4\u00a5\3\2"+
		"\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a8\5\22\n\2\u00a7\u00a6\3\2\2\2\u00a7"+
		"\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00ab\5\24\13\2\u00aa\u00a9\3"+
		"\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00ae\5\30\r\2\u00ad"+
		"\u00ac\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00b2\3\2\2\2\u00af\u00b1\5\32"+
		"\16\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2"+
		"\u00b3\3\2\2\2\u00b3\u00b8\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b7\5 "+
		"\21\2\u00b6\u00b5\3\2\2\2\u00b7\u00ba\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00be\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb\u00bd\58"+
		"\35\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be"+
		"\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c2\7\6"+
		"\2\2\u00c2\5\3\2\2\2\u00c3\u00c4\7B\2\2\u00c4\7\3\2\2\2\u00c5\u00c6\7"+
		"\3\2\2\u00c6\u00c7\7\7\2\2\u00c7\u00c8\5\n\6\2\u00c8\u00c9\7\6\2\2\u00c9"+
		"\t\3\2\2\2\u00ca\u00cc\7@\2\2\u00cb\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2"+
		"\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\13\3\2\2\2\u00cf\u00d0"+
		"\7\3\2\2\u00d0\u00d1\7\b\2\2\u00d1\u00d2\5\16\b\2\u00d2\u00d3\7\6\2\2"+
		"\u00d3\r\3\2\2\2\u00d4\u00d6\7B\2\2\u00d5\u00d4\3\2\2\2\u00d6\u00d9\3"+
		"\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00e0\3\2\2\2\u00d9"+
		"\u00d7\3\2\2\2\u00da\u00db\5\20\t\2\u00db\u00dc\7\t\2\2\u00dc\u00dd\5"+
		"x=\2\u00dd\u00de\5\16\b\2\u00de\u00e0\3\2\2\2\u00df\u00d7\3\2\2\2\u00df"+
		"\u00da\3\2\2\2\u00e0\17\3\2\2\2\u00e1\u00e3\7B\2\2\u00e2\u00e1\3\2\2\2"+
		"\u00e3\u00e4\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\21"+
		"\3\2\2\2\u00e6\u00e7\7\3\2\2\u00e7\u00e8\7\n\2\2\u00e8\u00e9\5n8\2\u00e9"+
		"\u00ea\7\6\2\2\u00ea\23\3\2\2\2\u00eb\u00ec\7\3\2\2\u00ec\u00ee\7\13\2"+
		"\2\u00ed\u00ef\5\26\f\2\u00ee\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		"\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f3\7\6"+
		"\2\2\u00f3\25\3\2\2\2\u00f4\u00f5\7\3\2\2\u00f5\u00f6\5h\65\2\u00f6\u00f7"+
		"\5l\67\2\u00f7\u00f8\7\6\2\2\u00f8\27\3\2\2\2\u00f9\u00fa\7\3\2\2\u00fa"+
		"\u0101\7\f\2\2\u00fb\u00ff\5\26\f\2\u00fc\u00fd\7\t\2\2\u00fd\u0100\7"+
		"\r\2\2\u00fe\u0100\5x=\2\u00ff\u00fc\3\2\2\2\u00ff\u00fe\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u0102\3\2\2\2\u0101\u00fb\3\2\2\2\u0102\u0103\3\2"+
		"\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105"+
		"\u0106\7\6\2\2\u0106\31\3\2\2\2\u0107\u0108\7\3\2\2\u0108\u0109\7\16\2"+
		"\2\u0109\u010a\5\34\17\2\u010a\33\3\2\2\2\u010b\u010c\5\36\20\2\u010c"+
		"\u010d\7\17\2\2\u010d\u010e\7\3\2\2\u010e\u010f\5l\67\2\u010f\u0112\7"+
		"\6\2\2\u0110\u0111\7\20\2\2\u0111\u0113\5:\36\2\u0112\u0110\3\2\2\2\u0112"+
		"\u0113\3\2\2\2\u0113\u0116\3\2\2\2\u0114\u0115\7\21\2\2\u0115\u0117\5"+
		"L\'\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2\2\2\u0118"+
		"\u0119\7\6\2\2\u0119\35\3\2\2\2\u011a\u011b\7B\2\2\u011b\37\3\2\2\2\u011c"+
		"\u011d\7\3\2\2\u011d\u011e\7\22\2\2\u011e\u011f\5$\23\2\u011f\u0120\7"+
		"\17\2\2\u0120\u0121\7\3\2\2\u0121\u0122\5l\67\2\u0122\u0123\7\6\2\2\u0123"+
		"\u0124\7\16\2\2\u0124\u0125\7\3\2\2\u0125\u0129\5\36\20\2\u0126\u0128"+
		"\5z>\2\u0127\u0126\3\2\2\2\u0128\u012b\3\2\2\2\u0129\u0127\3\2\2\2\u0129"+
		"\u012a\3\2\2\2\u012a\u012c\3\2\2\2\u012b\u0129\3\2\2\2\u012c\u012f\7\6"+
		"\2\2\u012d\u012e\7\20\2\2\u012e\u0130\5:\36\2\u012f\u012d\3\2\2\2\u012f"+
		"\u0130\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u0132\7\21\2\2\u0132\u0134\5"+
		"L\'\2\u0133\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"\u0136\5\"\22\2\u0136!\3\2\2\2\u0137\u0138\t\2\2\2\u0138\u013a\5&\24\2"+
		"\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u013c"+
		"\t\3\2\2\u013c\u013e\5,\27\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u0141\3\2\2\2\u013f\u0140\7\31\2\2\u0140\u0142\5\60\31\2\u0141\u013f"+
		"\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0145\3\2\2\2\u0143\u0144\t\4\2\2\u0144"+
		"\u0146\5\64\33\2\u0145\u0143\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147\3"+
		"\2\2\2\u0147\u0148\7\6\2\2\u0148#\3\2\2\2\u0149\u014a\7B\2\2\u014a%\3"+
		"\2\2\2\u014b\u014c\7\3\2\2\u014c\u0158\7\6\2\2\u014d\u0158\5(\25\2\u014e"+
		"\u014f\7\3\2\2\u014f\u0151\7\34\2\2\u0150\u0152\5(\25\2\u0151\u0150\3"+
		"\2\2\2\u0152\u0153\3\2\2\2\u0153\u0151\3\2\2\2\u0153\u0154\3\2\2\2\u0154"+
		"\u0155\3\2\2\2\u0155\u0156\7\6\2\2\u0156\u0158\3\2\2\2\u0157\u014b\3\2"+
		"\2\2\u0157\u014d\3\2\2\2\u0157\u014e\3\2\2\2\u0158\'\3\2\2\2\u0159\u015a"+
		"\7\3\2\2\u015a\u015e\5\36\20\2\u015b\u015d\5z>\2\u015c\u015b\3\2\2\2\u015d"+
		"\u0160\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u0161\3\2"+
		"\2\2\u0160\u015e\3\2\2\2\u0161\u0162\7\6\2\2\u0162\u0171\3\2\2\2\u0163"+
		"\u0164\7\3\2\2\u0164\u0165\5*\26\2\u0165\u0166\7\3\2\2\u0166\u016a\5\36"+
		"\20\2\u0167\u0169\5z>\2\u0168\u0167\3\2\2\2\u0169\u016c\3\2\2\2\u016a"+
		"\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016d\3\2\2\2\u016c\u016a\3\2"+
		"\2\2\u016d\u016e\7\6\2\2\u016e\u016f\7\6\2\2\u016f\u0171\3\2\2\2\u0170"+
		"\u0159\3\2\2\2\u0170\u0163\3\2\2\2\u0171)\3\2\2\2\u0172\u0173\7B\2\2\u0173"+
		"+\3\2\2\2\u0174\u0175\7\3\2\2\u0175\u0181\7\6\2\2\u0176\u0181\5.\30\2"+
		"\u0177\u0178\7\3\2\2\u0178\u017a\7\34\2\2\u0179\u017b\5.\30\2\u017a\u0179"+
		"\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d"+
		"\u017e\3\2\2\2\u017e\u017f\7\6\2\2\u017f\u0181\3\2\2\2\u0180\u0174\3\2"+
		"\2\2\u0180\u0176\3\2\2\2\u0180\u0177\3\2\2\2\u0181-\3\2\2\2\u0182\u0183"+
		"\7\3\2\2\u0183\u0184\5*\26\2\u0184\u0185\7\35\2\2\u0185\u0186\5*\26\2"+
		"\u0186\u0187\7\6\2\2\u0187/\3\2\2\2\u0188\u0189\7\3\2\2\u0189\u0195\7"+
		"\6\2\2\u018a\u0195\5\62\32\2\u018b\u018c\7\3\2\2\u018c\u018e\7\34\2\2"+
		"\u018d\u018f\5\62\32\2\u018e\u018d\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u018e"+
		"\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0193\7\6\2\2\u0193"+
		"\u0195\3\2\2\2\u0194\u0188\3\2\2\2\u0194\u018a\3\2\2\2\u0194\u018b\3\2"+
		"\2\2\u0195\61\3\2\2\2\u0196\u0197\7\3\2\2\u0197\u01b3\7\6\2\2\u0198\u0199"+
		"\7\3\2\2\u0199\u019a\7\36\2\2\u019a\u019b\5j\66\2\u019b\u019c\5z>\2\u019c"+
		"\u019d\5z>\2\u019d\u019e\7\6\2\2\u019e\u019f\7\6\2\2\u019f\u01b3\3\2\2"+
		"\2\u01a0\u01a1\5j\66\2\u01a1\u01a2\5z>\2\u01a2\u01a3\5z>\2\u01a3\u01a4"+
		"\7\6\2\2\u01a4\u01b3\3\2\2\2\u01a5\u01a6\7\3\2\2\u01a6\u01a7\t\5\2\2\u01a7"+
		"\u01a8\5r:\2\u01a8\u01a9\7\6\2\2\u01a9\u01b3\3\2\2\2\u01aa\u01ab\7\3\2"+
		"\2\u01ab\u01ac\7\36\2\2\u01ac\u01ad\7\3\2\2\u01ad\u01ae\t\5\2\2\u01ae"+
		"\u01af\5r:\2\u01af\u01b0\7\6\2\2\u01b0\u01b1\7\6\2\2\u01b1\u01b3\3\2\2"+
		"\2\u01b2\u0196\3\2\2\2\u01b2\u0198\3\2\2\2\u01b2\u01a0\3\2\2\2\u01b2\u01a5"+
		"\3\2\2\2\u01b2\u01aa\3\2\2\2\u01b3\63\3\2\2\2\u01b4\u01b5\7\3\2\2\u01b5"+
		"\u01c1\7\6\2\2\u01b6\u01c1\5\66\34\2\u01b7\u01b8\7\3\2\2\u01b8\u01ba\7"+
		"\34\2\2\u01b9\u01bb\5\66\34\2\u01ba\u01b9\3\2\2\2\u01bb\u01bc\3\2\2\2"+
		"\u01bc\u01ba\3\2\2\2\u01bc\u01bd\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01bf"+
		"\7\6\2\2\u01bf\u01c1\3\2\2\2\u01c0\u01b4\3\2\2\2\u01c0\u01b6\3\2\2\2\u01c0"+
		"\u01b7\3\2\2\2\u01c1\65\3\2\2\2\u01c2\u01c3\7\3\2\2\u01c3\u01c4\5*\26"+
		"\2\u01c4\u01c5\5V,\2\u01c5\u01c6\5*\26\2\u01c6\u01c7\7\6\2\2\u01c7\67"+
		"\3\2\2\2\u01c8\u01c9\7\3\2\2\u01c9\u01ca\7#\2\2\u01ca\u01cb\5\34\17\2"+
		"\u01cb9\3\2\2\2\u01cc\u01d6\5<\37\2\u01cd\u01d6\5f\64\2\u01ce\u01d6\5"+
		"B\"\2\u01cf\u01d6\5D#\2\u01d0\u01d6\5> \2\u01d1\u01d6\5@!\2\u01d2\u01d6"+
		"\5F$\2\u01d3\u01d6\5H%\2\u01d4\u01d6\5J&\2\u01d5\u01cc\3\2\2\2\u01d5\u01cd"+
		"\3\2\2\2\u01d5\u01ce\3\2\2\2\u01d5\u01cf\3\2\2\2\u01d5\u01d0\3\2\2\2\u01d5"+
		"\u01d1\3\2\2\2\u01d5\u01d2\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d5\u01d4\3\2"+
		"\2\2\u01d6;\3\2\2\2\u01d7\u01d8\7\3\2\2\u01d8\u01d9\7\6\2\2\u01d9=\3\2"+
		"\2\2\u01da\u01db\7\3\2\2\u01db\u01dd\7\34\2\2\u01dc\u01de\5:\36\2\u01dd"+
		"\u01dc\3\2\2\2\u01de\u01df\3\2\2\2\u01df\u01dd\3\2\2\2\u01df\u01e0\3\2"+
		"\2\2\u01e0\u01e1\3\2\2\2\u01e1\u01e2\7\6\2\2\u01e2?\3\2\2\2\u01e3\u01e4"+
		"\7\3\2\2\u01e4\u01e6\7$\2\2\u01e5\u01e7\5:\36\2\u01e6\u01e5\3\2\2\2\u01e7"+
		"\u01e8\3\2\2\2\u01e8\u01e6\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01ea\3\2"+
		"\2\2\u01ea\u01eb\7\6\2\2\u01ebA\3\2\2\2\u01ec\u01ed\7\3\2\2\u01ed\u01ee"+
		"\7\36\2\2\u01ee\u01ef\5:\36\2\u01ef\u01f0\7\6\2\2\u01f0C\3\2\2\2\u01f1"+
		"\u01f2\7\3\2\2\u01f2\u01f3\7%\2\2\u01f3\u01f4\5:\36\2\u01f4\u01f5\5:\36"+
		"\2\u01f5\u01f6\7\6\2\2\u01f6E\3\2\2\2\u01f7\u01f8\7\3\2\2\u01f8\u01f9"+
		"\7&\2\2\u01f9\u01fa\7\3\2\2\u01fa\u01fb\5l\67\2\u01fb\u01fc\7\6\2\2\u01fc"+
		"\u01fd\5:\36\2\u01fd\u01fe\7\6\2\2\u01feG\3\2\2\2\u01ff\u0200\7\3\2\2"+
		"\u0200\u0201\7\'\2\2\u0201\u0202\7\3\2\2\u0202\u0203\5l\67\2\u0203\u0204"+
		"\7\6\2\2\u0204\u0205\5:\36\2\u0205\u0206\7\6\2\2\u0206I\3\2\2\2\u0207"+
		"\u0208\5j\66\2\u0208\u0209\5z>\2\u0209\u020a\5z>\2\u020a\u020b\7\6\2\2"+
		"\u020bK\3\2\2\2\u020c\u0213\5N(\2\u020d\u0213\5P)\2\u020e\u0213\5R*\2"+
		"\u020f\u0213\5T+\2\u0210\u0213\5V,\2\u0211\u0213\5Z.\2\u0212\u020c\3\2"+
		"\2\2\u0212\u020d\3\2\2\2\u0212\u020e\3\2\2\2\u0212\u020f\3\2\2\2\u0212"+
		"\u0210\3\2\2\2\u0212\u0211\3\2\2\2\u0213M\3\2\2\2\u0214\u0215\7\3\2\2"+
		"\u0215\u0216\7\6\2\2\u0216O\3\2\2\2\u0217\u0218\7\3\2\2\u0218\u021a\7"+
		"\34\2\2\u0219\u021b\5L\'\2\u021a\u0219\3\2\2\2\u021b\u021c\3\2\2\2\u021c"+
		"\u021a\3\2\2\2\u021c\u021d\3\2\2\2\u021d\u021e\3\2\2\2\u021e\u021f\7\6"+
		"\2\2\u021fQ\3\2\2\2\u0220\u0221\7\3\2\2\u0221\u0222\7\'\2\2\u0222\u0223"+
		"\7\3\2\2\u0223\u0224\5l\67\2\u0224\u0225\7\6\2\2\u0225\u0226\5L\'\2\u0226"+
		"\u0227\7\6\2\2\u0227S\3\2\2\2\u0228\u0229\7\3\2\2\u0229\u022a\7(\2\2\u022a"+
		"\u022b\5:\36\2\u022b\u022c\5L\'\2\u022c\u022d\7\6\2\2\u022dU\3\2\2\2\u022e"+
		"\u0231\5X-\2\u022f\u0231\5f\64\2\u0230\u022e\3\2\2\2\u0230\u022f\3\2\2"+
		"\2\u0231W\3\2\2\2\u0232\u0233\7\3\2\2\u0233\u0234\7\36\2\2\u0234\u0235"+
		"\5f\64\2\u0235\u0236\7\6\2\2\u0236Y\3\2\2\2\u0237\u0238\7\3\2\2\u0238"+
		"\u0239\5\\/\2\u0239\u023a\5^\60\2\u023a\u023b\5`\61\2\u023b\u023c\7\6"+
		"\2\2\u023c[\3\2\2\2\u023d\u023e\t\6\2\2\u023e]\3\2\2\2\u023f\u024b\5\u0080"+
		"A\2\u0240\u0241\7\3\2\2\u0241\u0245\5\u0080A\2\u0242\u0244\5|?\2\u0243"+
		"\u0242\3\2\2\2\u0244\u0247\3\2\2\2\u0245\u0243\3\2\2\2\u0245\u0246\3\2"+
		"\2\2\u0246\u0248\3\2\2\2\u0247\u0245\3\2\2\2\u0248\u0249\7\6\2\2\u0249"+
		"\u024b\3\2\2\2\u024a\u023f\3\2\2\2\u024a\u0240\3\2\2\2\u024b_\3\2\2\2"+
		"\u024c\u0264\7E\2\2\u024d\u024e\7\3\2\2\u024e\u024f\5b\62\2\u024f\u0250"+
		"\5`\61\2\u0250\u0251\5`\61\2\u0251\u0252\7\6\2\2\u0252\u0264\3\2\2\2\u0253"+
		"\u0254\7\3\2\2\u0254\u0255\5d\63\2\u0255\u0257\5`\61\2\u0256\u0258\5`"+
		"\61\2\u0257\u0256\3\2\2\2\u0258\u0259\3\2\2\2\u0259\u0257\3\2\2\2\u0259"+
		"\u025a\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025c\7\6\2\2\u025c\u0264\3\2"+
		"\2\2\u025d\u025e\7\3\2\2\u025e\u025f\7\t\2\2\u025f\u0260\5`\61\2\u0260"+
		"\u0261\7\6\2\2\u0261\u0264\3\2\2\2\u0262\u0264\5^\60\2\u0263\u024c\3\2"+
		"\2\2\u0263\u024d\3\2\2\2\u0263\u0253\3\2\2\2\u0263\u025d\3\2\2\2\u0263"+
		"\u0262\3\2\2\2\u0264a\3\2\2\2\u0265\u0269\5d\63\2\u0266\u0269\7\t\2\2"+
		"\u0267\u0269\7.\2\2\u0268\u0265\3\2\2\2\u0268\u0266\3\2\2\2\u0268\u0267"+
		"\3\2\2\2\u0269c\3\2\2\2\u026a\u026b\t\7\2\2\u026be\3\2\2\2\u026c\u026d"+
		"\7\3\2\2\u026d\u0271\5h\65\2\u026e\u0270\5z>\2\u026f\u026e\3\2\2\2\u0270"+
		"\u0273\3\2\2\2\u0271\u026f\3\2\2\2\u0271\u0272\3\2\2\2\u0272\u0274\3\2"+
		"\2\2\u0273\u0271\3\2\2\2\u0274\u0275\7\6\2\2\u0275g\3\2\2\2\u0276\u0277"+
		"\7B\2\2\u0277i\3\2\2\2\u0278\u0279\7\3\2\2\u0279\u027c\7\61\2\2\u027a"+
		"\u027c\7\62\2\2\u027b\u0278\3\2\2\2\u027b\u027a\3\2\2\2\u027ck\3\2\2\2"+
		"\u027d\u027f\5p9\2\u027e\u027d\3\2\2\2\u027f\u0282\3\2\2\2\u0280\u027e"+
		"\3\2\2\2\u0280\u0281\3\2\2\2\u0281m\3\2\2\2\u0282\u0280\3\2\2\2\u0283"+
		"\u0285\5t;\2\u0284\u0283\3\2\2\2\u0285\u0288\3\2\2\2\u0286\u0284\3\2\2"+
		"\2\u0286\u0287\3\2\2\2\u0287o\3\2\2\2\u0288\u0286\3\2\2\2\u0289\u028b"+
		"\7A\2\2\u028a\u0289\3\2\2\2\u028b\u028c\3\2\2\2\u028c\u028a\3\2\2\2\u028c"+
		"\u028d\3\2\2\2\u028d\u028e\3\2\2\2\u028e\u028f\7\t\2\2\u028f\u0290\5x"+
		"=\2\u0290q\3\2\2\2\u0291\u0292\7A\2\2\u0292\u0293\7\t\2\2\u0293\u0294"+
		"\5x=\2\u0294s\3\2\2\2\u0295\u0297\5v<\2\u0296\u0295\3\2\2\2\u0297\u0298"+
		"\3\2\2\2\u0298\u0296\3\2\2\2\u0298\u0299\3\2\2\2\u0299\u029a\3\2\2\2\u029a"+
		"\u029b\7\t\2\2\u029b\u029c\5x=\2\u029cu\3\2\2\2\u029d\u029e\7B\2\2\u029e"+
		"w\3\2\2\2\u029f\u02aa\7B\2\2\u02a0\u02a1\7\3\2\2\u02a1\u02a3\7\63\2\2"+
		"\u02a2\u02a4\5x=\2\u02a3\u02a2\3\2\2\2\u02a4\u02a5\3\2\2\2\u02a5\u02a3"+
		"\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a7\3\2\2\2\u02a7\u02a8\7\6\2\2\u02a8"+
		"\u02aa\3\2\2\2\u02a9\u029f\3\2\2\2\u02a9\u02a0\3\2\2\2\u02aay\3\2\2\2"+
		"\u02ab\u02ac\t\b\2\2\u02ac{\3\2\2\2\u02ad\u02b1\7B\2\2\u02ae\u02b1\7A"+
		"\2\2\u02af\u02b1\5~@\2\u02b0\u02ad\3\2\2\2\u02b0\u02ae\3\2\2\2\u02b0\u02af"+
		"\3\2\2\2\u02b1}\3\2\2\2\u02b2\u02b3\7\3\2\2\u02b3\u02b7\5\u0080A\2\u02b4"+
		"\u02b6\5|?\2\u02b5\u02b4\3\2\2\2\u02b6\u02b9\3\2\2\2\u02b7\u02b5\3\2\2"+
		"\2\u02b7\u02b8\3\2\2\2\u02b8\u02ba\3\2\2\2\u02b9\u02b7\3\2\2\2\u02ba\u02bb"+
		"\7\6\2\2\u02bb\177\3\2\2\2\u02bc\u02bd\7B\2\2\u02bd\u0081\3\2\2\2\u02be"+
		"\u02bf\7\3\2\2\u02bf\u02c0\7\4\2\2\u02c0\u02c1\7\3\2\2\u02c1\u02c2\7\64"+
		"\2\2\u02c2\u02c3\7B\2\2\u02c3\u02c4\7\6\2\2\u02c4\u02c5\7\3\2\2\u02c5"+
		"\u02c6\7\65\2\2\u02c6\u02c7\7B\2\2\u02c7\u02c9\7\6\2\2\u02c8\u02ca\5\b"+
		"\5\2\u02c9\u02c8\3\2\2\2\u02c9\u02ca\3\2\2\2\u02ca\u02cc\3\2\2\2\u02cb"+
		"\u02cd\5\u0084C\2\u02cc\u02cb\3\2\2\2\u02cc\u02cd\3\2\2\2\u02cd\u02cf"+
		"\3\2\2\2\u02ce\u02d0\5\u008eH\2\u02cf\u02ce\3\2\2\2\u02cf\u02d0\3\2\2"+
		"\2\u02d0\u02d1\3\2\2\2\u02d1\u02d3\5\u0086D\2\u02d2\u02d4\5\u008cG\2\u02d3"+
		"\u02d2\3\2\2\2\u02d3\u02d4\3\2\2\2\u02d4\u02d6\3\2\2\2\u02d5\u02d7\5\u0090"+
		"I\2\u02d6\u02d5\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8"+
		"\u02d9\7\6\2\2\u02d9\u0083\3\2\2\2\u02da\u02db\7\3\2\2\u02db\u02dc\7\66"+
		"\2\2\u02dc\u02dd\5n8\2\u02dd\u02de\7\6\2\2\u02de\u0085\3\2\2\2\u02df\u02e0"+
		"\7\3\2\2\u02e0\u02e4\7\67\2\2\u02e1\u02e3\5\u0088E\2\u02e2\u02e1\3\2\2"+
		"\2\u02e3\u02e6\3\2\2\2\u02e4\u02e2\3\2\2\2\u02e4\u02e5\3\2\2\2\u02e5\u02e7"+
		"\3\2\2\2\u02e6\u02e4\3\2\2\2\u02e7\u02e8\7\6\2\2\u02e8\u0087\3\2\2\2\u02e9"+
		"\u02ec\5V,\2\u02ea\u02ec\5\u008aF\2\u02eb\u02e9\3\2\2\2\u02eb\u02ea\3"+
		"\2\2\2\u02ec\u0089\3\2\2\2\u02ed\u02ee\5j\66\2\u02ee\u02ef\5^\60\2\u02ef"+
		"\u02f0\7E\2\2\u02f0\u02f1\7\6\2\2\u02f1\u008b\3\2\2\2\u02f2\u02f3\7\3"+
		"\2\2\u02f3\u02f4\78\2\2\u02f4\u02f5\5:\36\2\u02f5\u02f6\7\6\2\2\u02f6"+
		"\u008d\3\2\2\2\u02f7\u02f8\7\3\2\2\u02f8\u02fe\t\t\2\2\u02f9\u02fa\7\17"+
		"\2\2\u02fa\u02fb\7\3\2\2\u02fb\u02fc\5l\67\2\u02fc\u02fd\7\6\2\2\u02fd"+
		"\u02ff\3\2\2\2\u02fe\u02f9\3\2\2\2\u02fe\u02ff\3\2\2\2\u02ff\u0300\3\2"+
		"\2\2\u0300\u0301\5\"\22\2\u0301\u008f\3\2\2\2\u0302\u0303\7\3\2\2\u0303"+
		"\u0304\7;\2\2\u0304\u0305\5\u0092J\2\u0305\u0306\5\u0094K\2\u0306\u0307"+
		"\7\6\2\2\u0307\u0091\3\2\2\2\u0308\u0309\t\n\2\2\u0309\u0093\3\2\2\2\u030a"+
		"\u030b\7\3\2\2\u030b\u030c\5b\62\2\u030c\u030d\5\u0094K\2\u030d\u030e"+
		"\5\u0094K\2\u030e\u030f\7\6\2\2\u030f\u0326\3\2\2\2\u0310\u0311\7\3\2"+
		"\2\u0311\u0314\7\t\2\2\u0312\u0314\7>\2\2\u0313\u0310\3\2\2\2\u0313\u0312"+
		"\3\2\2\2\u0314\u0315\3\2\2\2\u0315\u0316\5\u0094K\2\u0316\u0317\7\6\2"+
		"\2\u0317\u0326\3\2\2\2\u0318\u0326\7E\2\2\u0319\u031a\7\3\2\2\u031a\u031e"+
		"\5\u0080A\2\u031b\u031d\7B\2\2\u031c\u031b\3\2\2\2\u031d\u0320\3\2\2\2"+
		"\u031e\u031c\3\2\2\2\u031e\u031f\3\2\2\2\u031f\u0321\3\2\2\2\u0320\u031e"+
		"\3\2\2\2\u0321\u0322\7\6\2\2\u0322\u0326\3\2\2\2\u0323\u0326\7?\2\2\u0324"+
		"\u0326\5\u0080A\2\u0325\u030a\3\2\2\2\u0325\u0313\3\2\2\2\u0325\u0318"+
		"\3\2\2\2\u0325\u0319\3\2\2\2\u0325\u0323\3\2\2\2\u0325\u0324\3\2\2\2\u0326"+
		"\u0095\3\2\2\2G\u0098\u00a1\u00a4\u00a7\u00aa\u00ad\u00b2\u00b8\u00be"+
		"\u00cd\u00d7\u00df\u00e4\u00f0\u00ff\u0103\u0112\u0116\u0129\u012f\u0133"+
		"\u0139\u013d\u0141\u0145\u0153\u0157\u015e\u016a\u0170\u017c\u0180\u0190"+
		"\u0194\u01b2\u01bc\u01c0\u01d5\u01df\u01e8\u0212\u021c\u0230\u0245\u024a"+
		"\u0259\u0263\u0268\u0271\u027b\u0280\u0286\u028c\u0298\u02a5\u02a9\u02b0"+
		"\u02b7\u02c9\u02cc\u02cf\u02d3\u02d6\u02e4\u02eb\u02fe\u0313\u031e\u0325";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}