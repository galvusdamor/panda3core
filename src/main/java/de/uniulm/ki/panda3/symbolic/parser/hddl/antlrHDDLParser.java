// Generated from /mnt/swap/Workspace/panda3core/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4 by ANTLR 4.8
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
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

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
	private static String[] makeRuleNames() {
		return new String[] {
			"hddl_file", "domain", "domain_symbol", "require_def", "require_defs", 
			"type_def", "type_def_list", "new_types", "const_def", "predicates_def", 
			"atomic_formula_skeleton", "funtions_def", "comp_task_def", "task_def", 
			"task_symbol", "method_def", "tasknetwork_def", "method_symbol", "subtask_defs", 
			"subtask_def", "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", 
			"constraint_def", "causallink_defs", "causallink_def", "action_def", 
			"gd", "gd_empty", "gd_conjuction", "gd_disjuction", "gd_negation", "gd_implication", 
			"gd_existential", "gd_universal", "gd_equality_constraint", "gd_ltl_at_end", 
			"gd_ltl_always", "gd_ltl_sometime", "gd_ltl_at_most_once", "gd_ltl_sometime_after", 
			"gd_ltl_sometime_before", "gd_preference", "effect", "eff_empty", "eff_conjunction", 
			"eff_universal", "eff_conditional", "literal", "neg_atomic_formula", 
			"p_effect", "assign_op", "f_head", "f_exp", "bin_op", "multi_op", "atomic_formula", 
			"predicate", "equallity", "typed_var_list", "typed_obj_list", "typed_vars", 
			"typed_var", "typed_objs", "new_consts", "var_type", "var_or_const", 
			"term", "functionterm", "func_symbol", "problem", "p_object_declaration", 
			"p_init", "init_el", "num_init", "p_goal", "p_htn", "metric_spec", "optimization", 
			"ground_f_exp", "p_constraint"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
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
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "REQUIRE_NAME", 
			"VAR_NAME", "NAME", "COMMENT", "WS", "NUMBER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterHddl_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitHddl_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitHddl_file(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterDomain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitDomain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitDomain(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterDomain_symbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitDomain_symbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitDomain_symbol(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterRequire_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitRequire_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitRequire_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterRequire_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitRequire_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitRequire_defs(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterType_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitType_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitType_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterType_def_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitType_def_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitType_def_list(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterNew_types(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitNew_types(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitNew_types(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterConst_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitConst_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitConst_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterPredicates_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitPredicates_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitPredicates_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterAtomic_formula_skeleton(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitAtomic_formula_skeleton(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitAtomic_formula_skeleton(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterFuntions_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitFuntions_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitFuntions_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterComp_task_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitComp_task_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitComp_task_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTask_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTask_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTask_def(this);
			else return visitor.visitChildren(this);
		}
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
			_errHandler.sync(this);
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
			_errHandler.sync(this);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTask_symbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTask_symbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTask_symbol(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterMethod_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitMethod_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitMethod_def(this);
			else return visitor.visitChildren(this);
		}
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
			_errHandler.sync(this);
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
			_errHandler.sync(this);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTasknetwork_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTasknetwork_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTasknetwork_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Tasknetwork_defContext tasknetwork_def() throws RecognitionException {
		Tasknetwork_defContext _localctx = new Tasknetwork_defContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_tasknetwork_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(325);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(326);
				subtask_defs();
				}
			}

			setState(331);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20 || _la==T__21) {
				{
				setState(329);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__21) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(330);
				ordering_defs();
				}
			}

			setState(335);
			_errHandler.sync(this);
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
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__23 || _la==T__24) {
				{
				setState(337);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==T__24) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterMethod_symbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitMethod_symbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitMethod_symbol(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterSubtask_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitSubtask_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitSubtask_defs(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterSubtask_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitSubtask_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitSubtask_def(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterSubtask_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitSubtask_id(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitSubtask_id(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterOrdering_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitOrdering_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitOrdering_defs(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterOrdering_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitOrdering_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitOrdering_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ordering_defContext ordering_def() throws RecognitionException {
		Ordering_defContext _localctx = new Ordering_defContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_ordering_def);
		try {
			setState(412);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				match(T__0);
				setState(401);
				match(T__26);
				setState(402);
				subtask_id();
				setState(403);
				subtask_id();
				setState(404);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(406);
				match(T__0);
				setState(407);
				subtask_id();
				setState(408);
				match(T__26);
				setState(409);
				subtask_id();
				setState(410);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterConstraint_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitConstraint_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitConstraint_defs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constraint_defsContext constraint_defs() throws RecognitionException {
		Constraint_defsContext _localctx = new Constraint_defsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_constraint_defs);
		int _la;
		try {
			setState(426);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(414);
				match(T__0);
				setState(415);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(416);
				constraint_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(417);
				match(T__0);
				setState(418);
				match(T__25);
				setState(420); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(419);
					constraint_def();
					}
					}
					setState(422); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==T__54 );
				setState(424);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterConstraint_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitConstraint_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitConstraint_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constraint_defContext constraint_def() throws RecognitionException {
		Constraint_defContext _localctx = new Constraint_defContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_constraint_def);
		int _la;
		try {
			setState(456);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(428);
				match(T__0);
				setState(429);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				match(T__0);
				setState(431);
				match(T__27);
				setState(432);
				equallity();
				setState(433);
				var_or_const();
				setState(434);
				var_or_const();
				setState(435);
				match(T__3);
				setState(436);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(438);
				equallity();
				setState(439);
				var_or_const();
				setState(440);
				var_or_const();
				setState(441);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(443);
				match(T__0);
				setState(444);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(445);
				typed_var();
				setState(446);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(448);
				match(T__0);
				setState(449);
				match(T__27);
				setState(450);
				match(T__0);
				setState(451);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(452);
				typed_var();
				setState(453);
				match(T__3);
				setState(454);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterCausallink_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitCausallink_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitCausallink_defs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Causallink_defsContext causallink_defs() throws RecognitionException {
		Causallink_defsContext _localctx = new Causallink_defsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_causallink_defs);
		int _la;
		try {
			setState(470);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(458);
				match(T__0);
				setState(459);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(460);
				causallink_def();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(461);
				match(T__0);
				setState(462);
				match(T__25);
				setState(464); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(463);
					causallink_def();
					}
					}
					setState(466); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(468);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterCausallink_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitCausallink_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitCausallink_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Causallink_defContext causallink_def() throws RecognitionException {
		Causallink_defContext _localctx = new Causallink_defContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_causallink_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(T__0);
			setState(473);
			subtask_id();
			setState(474);
			literal();
			setState(475);
			subtask_id();
			setState(476);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterAction_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitAction_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitAction_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_defContext action_def() throws RecognitionException {
		Action_defContext _localctx = new Action_defContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_action_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			match(T__0);
			setState(479);
			match(T__32);
			setState(480);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GdContext gd() throws RecognitionException {
		GdContext _localctx = new GdContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_gd);
		try {
			setState(498);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(482);
				gd_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(483);
				atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(484);
				gd_negation();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(485);
				gd_implication();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(486);
				gd_conjuction();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(487);
				gd_disjuction();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(488);
				gd_existential();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(489);
				gd_universal();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(490);
				gd_equality_constraint();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(491);
				gd_ltl_at_end();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(492);
				gd_ltl_always();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(493);
				gd_ltl_sometime();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(494);
				gd_ltl_at_most_once();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(495);
				gd_ltl_sometime_after();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(496);
				gd_ltl_sometime_before();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(497);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_empty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_empty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_empty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_emptyContext gd_empty() throws RecognitionException {
		Gd_emptyContext _localctx = new Gd_emptyContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_gd_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(500);
			match(T__0);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_conjuction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_conjuction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_conjuction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_conjuctionContext gd_conjuction() throws RecognitionException {
		Gd_conjuctionContext _localctx = new Gd_conjuctionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_gd_conjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(503);
			match(T__0);
			setState(504);
			match(T__25);
			setState(506); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(505);
				gd();
				}
				}
				setState(508); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__54 );
			setState(510);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_disjuction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_disjuction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_disjuction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_disjuctionContext gd_disjuction() throws RecognitionException {
		Gd_disjuctionContext _localctx = new Gd_disjuctionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_gd_disjuction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			match(T__0);
			setState(513);
			match(T__33);
			setState(515); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(514);
				gd();
				}
				}
				setState(517); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 || _la==T__54 );
			setState(519);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_negation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_negation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_negation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_negationContext gd_negation() throws RecognitionException {
		Gd_negationContext _localctx = new Gd_negationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_gd_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(521);
			match(T__0);
			setState(522);
			match(T__27);
			setState(523);
			gd();
			setState(524);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_implication(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_implication(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_implication(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_implicationContext gd_implication() throws RecognitionException {
		Gd_implicationContext _localctx = new Gd_implicationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_gd_implication);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			match(T__0);
			setState(527);
			match(T__34);
			setState(528);
			gd();
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_existential(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_existential(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_existential(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_existentialContext gd_existential() throws RecognitionException {
		Gd_existentialContext _localctx = new Gd_existentialContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_gd_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(T__0);
			setState(533);
			match(T__35);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_universal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_universal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_universal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_universalContext gd_universal() throws RecognitionException {
		Gd_universalContext _localctx = new Gd_universalContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_gd_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			match(T__0);
			setState(541);
			match(T__36);
			setState(542);
			match(T__0);
			setState(543);
			typed_var_list();
			setState(544);
			match(T__3);
			setState(545);
			gd();
			setState(546);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_equality_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_equality_constraint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_equality_constraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_equality_constraintContext gd_equality_constraint() throws RecognitionException {
		Gd_equality_constraintContext _localctx = new Gd_equality_constraintContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_gd_equality_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(548);
			equallity();
			setState(549);
			var_or_const();
			setState(550);
			var_or_const();
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

	public static class Gd_ltl_at_endContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_at_endContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_at_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_at_end(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_at_end(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_at_end(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_at_endContext gd_ltl_at_end() throws RecognitionException {
		Gd_ltl_at_endContext _localctx = new Gd_ltl_at_endContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_gd_ltl_at_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			match(T__0);
			setState(554);
			match(T__37);
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

	public static class Gd_ltl_alwaysContext extends ParserRuleContext {
		public GdContext gd() {
			return getRuleContext(GdContext.class,0);
		}
		public Gd_ltl_alwaysContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gd_ltl_always; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_always(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_always(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_always(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_alwaysContext gd_ltl_always() throws RecognitionException {
		Gd_ltl_alwaysContext _localctx = new Gd_ltl_alwaysContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_gd_ltl_always);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(558);
			match(T__0);
			setState(559);
			match(T__38);
			setState(560);
			gd();
			setState(561);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_sometime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_sometime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_sometime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_sometimeContext gd_ltl_sometime() throws RecognitionException {
		Gd_ltl_sometimeContext _localctx = new Gd_ltl_sometimeContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_gd_ltl_sometime);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(563);
			match(T__0);
			setState(564);
			match(T__39);
			setState(565);
			gd();
			setState(566);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_at_most_once(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_at_most_once(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_at_most_once(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_at_most_onceContext gd_ltl_at_most_once() throws RecognitionException {
		Gd_ltl_at_most_onceContext _localctx = new Gd_ltl_at_most_onceContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_gd_ltl_at_most_once);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			match(T__0);
			setState(569);
			match(T__40);
			setState(570);
			gd();
			setState(571);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_sometime_after(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_sometime_after(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_sometime_after(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_sometime_afterContext gd_ltl_sometime_after() throws RecognitionException {
		Gd_ltl_sometime_afterContext _localctx = new Gd_ltl_sometime_afterContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_gd_ltl_sometime_after);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(573);
			match(T__0);
			setState(574);
			match(T__41);
			setState(575);
			gd();
			setState(576);
			gd();
			setState(577);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_ltl_sometime_before(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_ltl_sometime_before(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_ltl_sometime_before(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_ltl_sometime_beforeContext gd_ltl_sometime_before() throws RecognitionException {
		Gd_ltl_sometime_beforeContext _localctx = new Gd_ltl_sometime_beforeContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_gd_ltl_sometime_before);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(579);
			match(T__0);
			setState(580);
			match(T__42);
			setState(581);
			gd();
			setState(582);
			gd();
			setState(583);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGd_preference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGd_preference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGd_preference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Gd_preferenceContext gd_preference() throws RecognitionException {
		Gd_preferenceContext _localctx = new Gd_preferenceContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_gd_preference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			match(T__0);
			setState(586);
			match(T__43);
			setState(587);
			match(NAME);
			setState(588);
			gd();
			setState(589);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_effect);
		try {
			setState(597);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(591);
				eff_empty();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(592);
				eff_conjunction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(593);
				eff_universal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(594);
				eff_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(595);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(596);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEff_empty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEff_empty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEff_empty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eff_emptyContext eff_empty() throws RecognitionException {
		Eff_emptyContext _localctx = new Eff_emptyContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_eff_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(599);
			match(T__0);
			setState(600);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEff_conjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEff_conjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEff_conjunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eff_conjunctionContext eff_conjunction() throws RecognitionException {
		Eff_conjunctionContext _localctx = new Eff_conjunctionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_eff_conjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(602);
			match(T__0);
			setState(603);
			match(T__25);
			setState(605); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(604);
				effect();
				}
				}
				setState(607); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEff_universal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEff_universal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEff_universal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eff_universalContext eff_universal() throws RecognitionException {
		Eff_universalContext _localctx = new Eff_universalContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_eff_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(611);
			match(T__0);
			setState(612);
			match(T__36);
			setState(613);
			match(T__0);
			setState(614);
			typed_var_list();
			setState(615);
			match(T__3);
			setState(616);
			effect();
			setState(617);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEff_conditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEff_conditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEff_conditional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eff_conditionalContext eff_conditional() throws RecognitionException {
		Eff_conditionalContext _localctx = new Eff_conditionalContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_eff_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(619);
			match(T__0);
			setState(620);
			match(T__44);
			setState(621);
			gd();
			setState(622);
			effect();
			setState(623);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_literal);
		try {
			setState(627);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(625);
				neg_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(626);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterNeg_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitNeg_atomic_formula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitNeg_atomic_formula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Neg_atomic_formulaContext neg_atomic_formula() throws RecognitionException {
		Neg_atomic_formulaContext _localctx = new Neg_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_neg_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(629);
			match(T__0);
			setState(630);
			match(T__27);
			setState(631);
			atomic_formula();
			setState(632);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_effect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_effect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_effect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_effectContext p_effect() throws RecognitionException {
		P_effectContext _localctx = new P_effectContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_p_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(634);
			match(T__0);
			setState(635);
			assign_op();
			setState(636);
			f_head();
			setState(637);
			f_exp();
			setState(638);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterAssign_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitAssign_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitAssign_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49))) != 0)) ) {
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterF_head(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitF_head(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitF_head(this);
			else return visitor.visitChildren(this);
		}
	}

	public final F_headContext f_head() throws RecognitionException {
		F_headContext _localctx = new F_headContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_f_head);
		int _la;
		try {
			setState(653);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(642);
				func_symbol();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(643);
				match(T__0);
				setState(644);
				func_symbol();
				setState(648);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0 || _la==VAR_NAME || _la==NAME) {
					{
					{
					setState(645);
					term();
					}
					}
					setState(650);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(651);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterF_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitF_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitF_exp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final F_expContext f_exp() throws RecognitionException {
		F_expContext _localctx = new F_expContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_f_exp);
		int _la;
		try {
			setState(678);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(655);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(656);
				match(T__0);
				setState(657);
				bin_op();
				setState(658);
				f_exp();
				setState(659);
				f_exp();
				setState(660);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(662);
				match(T__0);
				setState(663);
				multi_op();
				setState(664);
				f_exp();
				setState(666); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(665);
					f_exp();
					}
					}
					setState(668); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME || _la==NUMBER );
				setState(670);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(672);
				match(T__0);
				setState(673);
				match(T__6);
				setState(674);
				f_exp();
				setState(675);
				match(T__3);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(677);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterBin_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitBin_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitBin_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bin_opContext bin_op() throws RecognitionException {
		Bin_opContext _localctx = new Bin_opContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_bin_op);
		try {
			setState(683);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__51:
			case T__52:
				enterOuterAlt(_localctx, 1);
				{
				setState(680);
				multi_op();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(681);
				match(T__6);
				}
				break;
			case T__50:
				enterOuterAlt(_localctx, 3);
				{
				setState(682);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterMulti_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitMulti_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitMulti_op(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multi_opContext multi_op() throws RecognitionException {
		Multi_opContext _localctx = new Multi_opContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_multi_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(685);
			_la = _input.LA(1);
			if ( !(_la==T__51 || _la==T__52) ) {
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterAtomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitAtomic_formula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitAtomic_formula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Atomic_formulaContext atomic_formula() throws RecognitionException {
		Atomic_formulaContext _localctx = new Atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_atomic_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(687);
			match(T__0);
			setState(688);
			predicate();
			setState(692);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME || _la==NAME) {
				{
				{
				setState(689);
				var_or_const();
				}
				}
				setState(694);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(695);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterEquallity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitEquallity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitEquallity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EquallityContext equallity() throws RecognitionException {
		EquallityContext _localctx = new EquallityContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_equallity);
		try {
			setState(702);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(699);
				match(T__0);
				setState(700);
				match(T__53);
				}
				break;
			case T__54:
				enterOuterAlt(_localctx, 2);
				{
				setState(701);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTyped_var_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTyped_var_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTyped_var_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typed_var_listContext typed_var_list() throws RecognitionException {
		Typed_var_listContext _localctx = new Typed_var_listContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_typed_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(707);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VAR_NAME) {
				{
				{
				setState(704);
				typed_vars();
				}
				}
				setState(709);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTyped_obj_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTyped_obj_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTyped_obj_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typed_obj_listContext typed_obj_list() throws RecognitionException {
		Typed_obj_listContext _localctx = new Typed_obj_listContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_typed_obj_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(710);
				typed_objs();
				}
				}
				setState(715);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTyped_vars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTyped_vars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTyped_vars(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typed_varsContext typed_vars() throws RecognitionException {
		Typed_varsContext _localctx = new Typed_varsContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_typed_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(717); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(716);
				match(VAR_NAME);
				}
				}
				setState(719); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME );
			setState(721);
			match(T__6);
			setState(722);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTyped_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTyped_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTyped_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typed_varContext typed_var() throws RecognitionException {
		Typed_varContext _localctx = new Typed_varContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_typed_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(724);
			match(VAR_NAME);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTyped_objs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTyped_objs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTyped_objs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typed_objsContext typed_objs() throws RecognitionException {
		Typed_objsContext _localctx = new Typed_objsContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_typed_objs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(729); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(728);
				new_consts();
				}
				}
				setState(731); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(733);
			match(T__6);
			setState(734);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterNew_consts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitNew_consts(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitNew_consts(this);
			else return visitor.visitChildren(this);
		}
	}

	public final New_constsContext new_consts() throws RecognitionException {
		New_constsContext _localctx = new New_constsContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_new_consts);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(736);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterVar_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitVar_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitVar_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_typeContext var_type() throws RecognitionException {
		Var_typeContext _localctx = new Var_typeContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_var_type);
		int _la;
		try {
			setState(748);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(738);
				match(NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(739);
				match(T__0);
				setState(740);
				match(T__55);
				setState(742); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(741);
					var_type();
					}
					}
					setState(744); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || _la==NAME );
				setState(746);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterVar_or_const(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitVar_or_const(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitVar_or_const(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_or_constContext var_or_const() throws RecognitionException {
		Var_or_constContext _localctx = new Var_or_constContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_var_or_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_term);
		try {
			setState(755);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(752);
				match(NAME);
				}
				break;
			case VAR_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(753);
				match(VAR_NAME);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(754);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterFunctionterm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitFunctionterm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitFunctionterm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctiontermContext functionterm() throws RecognitionException {
		FunctiontermContext _localctx = new FunctiontermContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_functionterm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(757);
			match(T__0);
			setState(758);
			func_symbol();
			setState(762);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==VAR_NAME || _la==NAME) {
				{
				{
				setState(759);
				term();
				}
				}
				setState(764);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(765);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterFunc_symbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitFunc_symbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitFunc_symbol(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Func_symbolContext func_symbol() throws RecognitionException {
		Func_symbolContext _localctx = new Func_symbolContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_func_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(767);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterProblem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitProblem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitProblem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProblemContext problem() throws RecognitionException {
		ProblemContext _localctx = new ProblemContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(769);
			match(T__0);
			setState(770);
			match(T__1);
			setState(771);
			match(T__0);
			setState(772);
			match(T__56);
			setState(773);
			match(NAME);
			setState(774);
			match(T__3);
			setState(775);
			match(T__0);
			setState(776);
			match(T__57);
			setState(777);
			match(NAME);
			setState(778);
			match(T__3);
			setState(780);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(779);
				require_def();
				}
				break;
			}
			setState(783);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(782);
				p_object_declaration();
				}
				break;
			}
			setState(786);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(785);
				p_htn();
				}
				break;
			}
			setState(788);
			p_init();
			setState(790);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(789);
				p_goal();
				}
				break;
			}
			setState(793);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				setState(792);
				p_constraint();
				}
				break;
			}
			setState(796);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(795);
				metric_spec();
				}
			}

			setState(798);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_object_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_object_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_object_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_object_declarationContext p_object_declaration() throws RecognitionException {
		P_object_declarationContext _localctx = new P_object_declarationContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_p_object_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(800);
			match(T__0);
			setState(801);
			match(T__58);
			setState(802);
			typed_obj_list();
			setState(803);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_init(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_init(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_init(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_initContext p_init() throws RecognitionException {
		P_initContext _localctx = new P_initContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_p_init);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(805);
			match(T__0);
			setState(806);
			match(T__59);
			setState(810);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__54) {
				{
				{
				setState(807);
				init_el();
				}
				}
				setState(812);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(813);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterInit_el(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitInit_el(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitInit_el(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Init_elContext init_el() throws RecognitionException {
		Init_elContext _localctx = new Init_elContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_init_el);
		try {
			setState(817);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(815);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(816);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterNum_init(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitNum_init(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitNum_init(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Num_initContext num_init() throws RecognitionException {
		Num_initContext _localctx = new Num_initContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_num_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
			equallity();
			setState(820);
			f_head();
			setState(821);
			match(NUMBER);
			setState(822);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_goal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_goal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_goal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_goalContext p_goal() throws RecognitionException {
		P_goalContext _localctx = new P_goalContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_p_goal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(824);
			match(T__0);
			setState(825);
			match(T__60);
			setState(826);
			gd();
			setState(827);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_htn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_htn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_htn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_htnContext p_htn() throws RecognitionException {
		P_htnContext _localctx = new P_htnContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_p_htn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(829);
			match(T__0);
			setState(830);
			_la = _input.LA(1);
			if ( !(_la==T__61 || _la==T__62) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(836);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(831);
				match(T__12);
				setState(832);
				match(T__0);
				setState(833);
				typed_var_list();
				setState(834);
				match(T__3);
				}
			}

			setState(838);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterMetric_spec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitMetric_spec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitMetric_spec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Metric_specContext metric_spec() throws RecognitionException {
		Metric_specContext _localctx = new Metric_specContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_metric_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(840);
			match(T__0);
			setState(841);
			match(T__63);
			setState(842);
			optimization();
			setState(843);
			ground_f_exp();
			setState(844);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterOptimization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitOptimization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitOptimization(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptimizationContext optimization() throws RecognitionException {
		OptimizationContext _localctx = new OptimizationContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_optimization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(846);
			_la = _input.LA(1);
			if ( !(_la==T__64 || _la==T__65) ) {
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterGround_f_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitGround_f_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitGround_f_exp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ground_f_expContext ground_f_exp() throws RecognitionException {
		Ground_f_expContext _localctx = new Ground_f_expContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_ground_f_exp);
		int _la;
		try {
			setState(885);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(848);
				match(T__0);
				setState(849);
				bin_op();
				setState(850);
				ground_f_exp();
				setState(851);
				ground_f_exp();
				setState(852);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(854);
				match(T__0);
				setState(855);
				multi_op();
				setState(856);
				ground_f_exp();
				setState(858); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(857);
					ground_f_exp();
					}
					}
					setState(860); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (T__66 - 67)) | (1L << (T__67 - 67)) | (1L << (NAME - 67)) | (1L << (NUMBER - 67)))) != 0) );
				setState(862);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(867);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(864);
					match(T__0);
					setState(865);
					match(T__6);
					}
					break;
				case T__66:
					{
					setState(866);
					match(T__66);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(869);
				ground_f_exp();
				setState(870);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(872);
				match(NUMBER);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(873);
				match(T__0);
				setState(874);
				func_symbol();
				setState(878);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NAME) {
					{
					{
					setState(875);
					match(NAME);
					}
					}
					setState(880);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(881);
				match(T__3);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(883);
				match(T__67);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(884);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).enterP_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof antlrHDDLListener ) ((antlrHDDLListener)listener).exitP_constraint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof antlrHDDLVisitor ) return ((antlrHDDLVisitor<? extends T>)visitor).visitP_constraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final P_constraintContext p_constraint() throws RecognitionException {
		P_constraintContext _localctx = new P_constraintContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_p_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(887);
			match(T__0);
			setState(888);
			match(T__22);
			setState(889);
			gd();
			setState(890);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3L\u037f\4\2\t\2\4"+
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
		"\27\5\27\u0191\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\5\30\u019f\n\30\3\31\3\31\3\31\3\31\3\31\3\31\6\31\u01a7\n"+
		"\31\r\31\16\31\u01a8\3\31\3\31\5\31\u01ad\n\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u01cb\n\32\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\6\33\u01d3\n\33\r\33\16\33\u01d4\3\33\3\33\5\33\u01d9"+
		"\n\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\5\36"+
		"\u01f5\n\36\3\37\3\37\3\37\3 \3 \3 \6 \u01fd\n \r \16 \u01fe\3 \3 \3!"+
		"\3!\3!\6!\u0206\n!\r!\16!\u0207\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#"+
		"\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&"+
		"\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3+\3"+
		"+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\5"+
		".\u0258\n.\3/\3/\3/\3\60\3\60\3\60\6\60\u0260\n\60\r\60\16\60\u0261\3"+
		"\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3"+
		"\62\3\62\3\63\3\63\5\63\u0276\n\63\3\64\3\64\3\64\3\64\3\64\3\65\3\65"+
		"\3\65\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\3\67\7\67\u0289\n\67\f\67"+
		"\16\67\u028c\13\67\3\67\3\67\5\67\u0290\n\67\38\38\38\38\38\38\38\38\3"+
		"8\38\38\68\u029d\n8\r8\168\u029e\38\38\38\38\38\38\38\38\58\u02a9\n8\3"+
		"9\39\39\59\u02ae\n9\3:\3:\3;\3;\3;\7;\u02b5\n;\f;\16;\u02b8\13;\3;\3;"+
		"\3<\3<\3=\3=\3=\5=\u02c1\n=\3>\7>\u02c4\n>\f>\16>\u02c7\13>\3?\7?\u02ca"+
		"\n?\f?\16?\u02cd\13?\3@\6@\u02d0\n@\r@\16@\u02d1\3@\3@\3@\3A\3A\3A\3A"+
		"\3B\6B\u02dc\nB\rB\16B\u02dd\3B\3B\3B\3C\3C\3D\3D\3D\3D\6D\u02e9\nD\r"+
		"D\16D\u02ea\3D\3D\5D\u02ef\nD\3E\3E\3F\3F\3F\5F\u02f6\nF\3G\3G\3G\7G\u02fb"+
		"\nG\fG\16G\u02fe\13G\3G\3G\3H\3H\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\5I\u030f"+
		"\nI\3I\5I\u0312\nI\3I\5I\u0315\nI\3I\3I\5I\u0319\nI\3I\5I\u031c\nI\3I"+
		"\5I\u031f\nI\3I\3I\3J\3J\3J\3J\3J\3K\3K\3K\7K\u032b\nK\fK\16K\u032e\13"+
		"K\3K\3K\3L\3L\5L\u0334\nL\3M\3M\3M\3M\3M\3N\3N\3N\3N\3N\3O\3O\3O\3O\3"+
		"O\3O\3O\5O\u0347\nO\3O\3O\3P\3P\3P\3P\3P\3P\3Q\3Q\3R\3R\3R\3R\3R\3R\3"+
		"R\3R\3R\3R\6R\u035d\nR\rR\16R\u035e\3R\3R\3R\3R\3R\5R\u0366\nR\3R\3R\3"+
		"R\3R\3R\3R\3R\7R\u036f\nR\fR\16R\u0372\13R\3R\3R\3R\3R\5R\u0378\nR\3S"+
		"\3S\3S\3S\3S\3S\2\2T\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e"+
		"\u00a0\u00a2\u00a4\2\13\3\2\23\26\3\2\27\30\3\2\32\33\3\2\37\"\3\2\60"+
		"\64\3\2\66\67\3\2HI\3\2@A\3\2CD\2\u0398\2\u00a8\3\2\2\2\4\u00aa\3\2\2"+
		"\2\6\u00d3\3\2\2\2\b\u00d5\3\2\2\2\n\u00db\3\2\2\2\f\u00df\3\2\2\2\16"+
		"\u00ef\3\2\2\2\20\u00f2\3\2\2\2\22\u00f6\3\2\2\2\24\u00fb\3\2\2\2\26\u0104"+
		"\3\2\2\2\30\u0109\3\2\2\2\32\u0117\3\2\2\2\34\u011b\3\2\2\2\36\u012a\3"+
		"\2\2\2 \u012c\3\2\2\2\"\u0149\3\2\2\2$\u0159\3\2\2\2&\u0167\3\2\2\2(\u0180"+
		"\3\2\2\2*\u0182\3\2\2\2,\u0190\3\2\2\2.\u019e\3\2\2\2\60\u01ac\3\2\2\2"+
		"\62\u01ca\3\2\2\2\64\u01d8\3\2\2\2\66\u01da\3\2\2\28\u01e0\3\2\2\2:\u01f4"+
		"\3\2\2\2<\u01f6\3\2\2\2>\u01f9\3\2\2\2@\u0202\3\2\2\2B\u020b\3\2\2\2D"+
		"\u0210\3\2\2\2F\u0216\3\2\2\2H\u021e\3\2\2\2J\u0226\3\2\2\2L\u022b\3\2"+
		"\2\2N\u0230\3\2\2\2P\u0235\3\2\2\2R\u023a\3\2\2\2T\u023f\3\2\2\2V\u0245"+
		"\3\2\2\2X\u024b\3\2\2\2Z\u0257\3\2\2\2\\\u0259\3\2\2\2^\u025c\3\2\2\2"+
		"`\u0265\3\2\2\2b\u026d\3\2\2\2d\u0275\3\2\2\2f\u0277\3\2\2\2h\u027c\3"+
		"\2\2\2j\u0282\3\2\2\2l\u028f\3\2\2\2n\u02a8\3\2\2\2p\u02ad\3\2\2\2r\u02af"+
		"\3\2\2\2t\u02b1\3\2\2\2v\u02bb\3\2\2\2x\u02c0\3\2\2\2z\u02c5\3\2\2\2|"+
		"\u02cb\3\2\2\2~\u02cf\3\2\2\2\u0080\u02d6\3\2\2\2\u0082\u02db\3\2\2\2"+
		"\u0084\u02e2\3\2\2\2\u0086\u02ee\3\2\2\2\u0088\u02f0\3\2\2\2\u008a\u02f5"+
		"\3\2\2\2\u008c\u02f7\3\2\2\2\u008e\u0301\3\2\2\2\u0090\u0303\3\2\2\2\u0092"+
		"\u0322\3\2\2\2\u0094\u0327\3\2\2\2\u0096\u0333\3\2\2\2\u0098\u0335\3\2"+
		"\2\2\u009a\u033a\3\2\2\2\u009c\u033f\3\2\2\2\u009e\u034a\3\2\2\2\u00a0"+
		"\u0350\3\2\2\2\u00a2\u0377\3\2\2\2\u00a4\u0379\3\2\2\2\u00a6\u00a9\5\4"+
		"\3\2\u00a7\u00a9\5\u0090I\2\u00a8\u00a6\3\2\2\2\u00a8\u00a7\3\2\2\2\u00a9"+
		"\3\3\2\2\2\u00aa\u00ab\7\3\2\2\u00ab\u00ac\7\4\2\2\u00ac\u00ad\7\3\2\2"+
		"\u00ad\u00ae\7\5\2\2\u00ae\u00af\5\6\4\2\u00af\u00b1\7\6\2\2\u00b0\u00b2"+
		"\5\b\5\2\u00b1\u00b0\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3"+
		"\u00b5\5\f\7\2\u00b4\u00b3\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7\3\2"+
		"\2\2\u00b6\u00b8\5\22\n\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8"+
		"\u00ba\3\2\2\2\u00b9\u00bb\5\24\13\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3"+
		"\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00be\5\30\r\2\u00bd\u00bc\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00c2\3\2\2\2\u00bf\u00c1\5\32\16\2\u00c0\u00bf\3"+
		"\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3"+
		"\u00c8\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c7\5 \21\2\u00c6\u00c5\3\2"+
		"\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00ce\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cd\58\35\2\u00cc\u00cb\3\2"+
		"\2\2\u00cd\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf"+
		"\u00d1\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d1\u00d2\7\6\2\2\u00d2\5\3\2\2\2"+
		"\u00d3\u00d4\7I\2\2\u00d4\7\3\2\2\2\u00d5\u00d6\7\3\2\2\u00d6\u00d7\7"+
		"\7\2\2\u00d7\u00d8\5\n\6\2\u00d8\u00d9\7\6\2\2\u00d9\t\3\2\2\2\u00da\u00dc"+
		"\7G\2\2\u00db\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd"+
		"\u00de\3\2\2\2\u00de\13\3\2\2\2\u00df\u00e0\7\3\2\2\u00e0\u00e1\7\b\2"+
		"\2\u00e1\u00e2\5\16\b\2\u00e2\u00e3\7\6\2\2\u00e3\r\3\2\2\2\u00e4\u00e6"+
		"\7I\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7"+
		"\u00e8\3\2\2\2\u00e8\u00f0\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\5\20"+
		"\t\2\u00eb\u00ec\7\t\2\2\u00ec\u00ed\5\u0086D\2\u00ed\u00ee\5\16\b\2\u00ee"+
		"\u00f0\3\2\2\2\u00ef\u00e7\3\2\2\2\u00ef\u00ea\3\2\2\2\u00f0\17\3\2\2"+
		"\2\u00f1\u00f3\7I\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2"+
		"\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\21\3\2\2\2\u00f6\u00f7\7\3\2\2\u00f7"+
		"\u00f8\7\n\2\2\u00f8\u00f9\5|?\2\u00f9\u00fa\7\6\2\2\u00fa\23\3\2\2\2"+
		"\u00fb\u00fc\7\3\2\2\u00fc\u00fe\7\13\2\2\u00fd\u00ff\5\26\f\2\u00fe\u00fd"+
		"\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\u0103\7\6\2\2\u0103\25\3\2\2\2\u0104\u0105\7\3\2"+
		"\2\u0105\u0106\5v<\2\u0106\u0107\5z>\2\u0107\u0108\7\6\2\2\u0108\27\3"+
		"\2\2\2\u0109\u010a\7\3\2\2\u010a\u0111\7\f\2\2\u010b\u010f\5\26\f\2\u010c"+
		"\u010d\7\t\2\2\u010d\u0110\7\r\2\2\u010e\u0110\5\u0086D\2\u010f\u010c"+
		"\3\2\2\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0112\3\2\2\2\u0111"+
		"\u010b\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2"+
		"\2\2\u0114\u0115\3\2\2\2\u0115\u0116\7\6\2\2\u0116\31\3\2\2\2\u0117\u0118"+
		"\7\3\2\2\u0118\u0119\7\16\2\2\u0119\u011a\5\34\17\2\u011a\33\3\2\2\2\u011b"+
		"\u011c\5\36\20\2\u011c\u011d\7\17\2\2\u011d\u011e\7\3\2\2\u011e\u011f"+
		"\5z>\2\u011f\u0122\7\6\2\2\u0120\u0121\7\20\2\2\u0121\u0123\5:\36\2\u0122"+
		"\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0126\3\2\2\2\u0124\u0125\7\21"+
		"\2\2\u0125\u0127\5Z.\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128"+
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
		"\2\2\u0191-\3\2\2\2\u0192\u0193\7\3\2\2\u0193\u0194\7\35\2\2\u0194\u0195"+
		"\5*\26\2\u0195\u0196\5*\26\2\u0196\u0197\7\6\2\2\u0197\u019f\3\2\2\2\u0198"+
		"\u0199\7\3\2\2\u0199\u019a\5*\26\2\u019a\u019b\7\35\2\2\u019b\u019c\5"+
		"*\26\2\u019c\u019d\7\6\2\2\u019d\u019f\3\2\2\2\u019e\u0192\3\2\2\2\u019e"+
		"\u0198\3\2\2\2\u019f/\3\2\2\2\u01a0\u01a1\7\3\2\2\u01a1\u01ad\7\6\2\2"+
		"\u01a2\u01ad\5\62\32\2\u01a3\u01a4\7\3\2\2\u01a4\u01a6\7\34\2\2\u01a5"+
		"\u01a7\5\62\32\2\u01a6\u01a5\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a6\3"+
		"\2\2\2\u01a8\u01a9\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01ab\7\6\2\2\u01ab"+
		"\u01ad\3\2\2\2\u01ac\u01a0\3\2\2\2\u01ac\u01a2\3\2\2\2\u01ac\u01a3\3\2"+
		"\2\2\u01ad\61\3\2\2\2\u01ae\u01af\7\3\2\2\u01af\u01cb\7\6\2\2\u01b0\u01b1"+
		"\7\3\2\2\u01b1\u01b2\7\36\2\2\u01b2\u01b3\5x=\2\u01b3\u01b4\5\u0088E\2"+
		"\u01b4\u01b5\5\u0088E\2\u01b5\u01b6\7\6\2\2\u01b6\u01b7\7\6\2\2\u01b7"+
		"\u01cb\3\2\2\2\u01b8\u01b9\5x=\2\u01b9\u01ba\5\u0088E\2\u01ba\u01bb\5"+
		"\u0088E\2\u01bb\u01bc\7\6\2\2\u01bc\u01cb\3\2\2\2\u01bd\u01be\7\3\2\2"+
		"\u01be\u01bf\t\5\2\2\u01bf\u01c0\5\u0080A\2\u01c0\u01c1\7\6\2\2\u01c1"+
		"\u01cb\3\2\2\2\u01c2\u01c3\7\3\2\2\u01c3\u01c4\7\36\2\2\u01c4\u01c5\7"+
		"\3\2\2\u01c5\u01c6\t\5\2\2\u01c6\u01c7\5\u0080A\2\u01c7\u01c8\7\6\2\2"+
		"\u01c8\u01c9\7\6\2\2\u01c9\u01cb\3\2\2\2\u01ca\u01ae\3\2\2\2\u01ca\u01b0"+
		"\3\2\2\2\u01ca\u01b8\3\2\2\2\u01ca\u01bd\3\2\2\2\u01ca\u01c2\3\2\2\2\u01cb"+
		"\63\3\2\2\2\u01cc\u01cd\7\3\2\2\u01cd\u01d9\7\6\2\2\u01ce\u01d9\5\66\34"+
		"\2\u01cf\u01d0\7\3\2\2\u01d0\u01d2\7\34\2\2\u01d1\u01d3\5\66\34\2\u01d2"+
		"\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u01d2\3\2\2\2\u01d4\u01d5\3\2"+
		"\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d7\7\6\2\2\u01d7\u01d9\3\2\2\2\u01d8"+
		"\u01cc\3\2\2\2\u01d8\u01ce\3\2\2\2\u01d8\u01cf\3\2\2\2\u01d9\65\3\2\2"+
		"\2\u01da\u01db\7\3\2\2\u01db\u01dc\5*\26\2\u01dc\u01dd\5d\63\2\u01dd\u01de"+
		"\5*\26\2\u01de\u01df\7\6\2\2\u01df\67\3\2\2\2\u01e0\u01e1\7\3\2\2\u01e1"+
		"\u01e2\7#\2\2\u01e2\u01e3\5\34\17\2\u01e39\3\2\2\2\u01e4\u01f5\5<\37\2"+
		"\u01e5\u01f5\5t;\2\u01e6\u01f5\5B\"\2\u01e7\u01f5\5D#\2\u01e8\u01f5\5"+
		"> \2\u01e9\u01f5\5@!\2\u01ea\u01f5\5F$\2\u01eb\u01f5\5H%\2\u01ec\u01f5"+
		"\5J&\2\u01ed\u01f5\5L\'\2\u01ee\u01f5\5N(\2\u01ef\u01f5\5P)\2\u01f0\u01f5"+
		"\5R*\2\u01f1\u01f5\5T+\2\u01f2\u01f5\5V,\2\u01f3\u01f5\5X-\2\u01f4\u01e4"+
		"\3\2\2\2\u01f4\u01e5\3\2\2\2\u01f4\u01e6\3\2\2\2\u01f4\u01e7\3\2\2\2\u01f4"+
		"\u01e8\3\2\2\2\u01f4\u01e9\3\2\2\2\u01f4\u01ea\3\2\2\2\u01f4\u01eb\3\2"+
		"\2\2\u01f4\u01ec\3\2\2\2\u01f4\u01ed\3\2\2\2\u01f4\u01ee\3\2\2\2\u01f4"+
		"\u01ef\3\2\2\2\u01f4\u01f0\3\2\2\2\u01f4\u01f1\3\2\2\2\u01f4\u01f2\3\2"+
		"\2\2\u01f4\u01f3\3\2\2\2\u01f5;\3\2\2\2\u01f6\u01f7\7\3\2\2\u01f7\u01f8"+
		"\7\6\2\2\u01f8=\3\2\2\2\u01f9\u01fa\7\3\2\2\u01fa\u01fc\7\34\2\2\u01fb"+
		"\u01fd\5:\36\2\u01fc\u01fb\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u01fc\3\2"+
		"\2\2\u01fe\u01ff\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u0201\7\6\2\2\u0201"+
		"?\3\2\2\2\u0202\u0203\7\3\2\2\u0203\u0205\7$\2\2\u0204\u0206\5:\36\2\u0205"+
		"\u0204\3\2\2\2\u0206\u0207\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0208\3\2"+
		"\2\2\u0208\u0209\3\2\2\2\u0209\u020a\7\6\2\2\u020aA\3\2\2\2\u020b\u020c"+
		"\7\3\2\2\u020c\u020d\7\36\2\2\u020d\u020e\5:\36\2\u020e\u020f\7\6\2\2"+
		"\u020fC\3\2\2\2\u0210\u0211\7\3\2\2\u0211\u0212\7%\2\2\u0212\u0213\5:"+
		"\36\2\u0213\u0214\5:\36\2\u0214\u0215\7\6\2\2\u0215E\3\2\2\2\u0216\u0217"+
		"\7\3\2\2\u0217\u0218\7&\2\2\u0218\u0219\7\3\2\2\u0219\u021a\5z>\2\u021a"+
		"\u021b\7\6\2\2\u021b\u021c\5:\36\2\u021c\u021d\7\6\2\2\u021dG\3\2\2\2"+
		"\u021e\u021f\7\3\2\2\u021f\u0220\7\'\2\2\u0220\u0221\7\3\2\2\u0221\u0222"+
		"\5z>\2\u0222\u0223\7\6\2\2\u0223\u0224\5:\36\2\u0224\u0225\7\6\2\2\u0225"+
		"I\3\2\2\2\u0226\u0227\5x=\2\u0227\u0228\5\u0088E\2\u0228\u0229\5\u0088"+
		"E\2\u0229\u022a\7\6\2\2\u022aK\3\2\2\2\u022b\u022c\7\3\2\2\u022c\u022d"+
		"\7(\2\2\u022d\u022e\5:\36\2\u022e\u022f\7\6\2\2\u022fM\3\2\2\2\u0230\u0231"+
		"\7\3\2\2\u0231\u0232\7)\2\2\u0232\u0233\5:\36\2\u0233\u0234\7\6\2\2\u0234"+
		"O\3\2\2\2\u0235\u0236\7\3\2\2\u0236\u0237\7*\2\2\u0237\u0238\5:\36\2\u0238"+
		"\u0239\7\6\2\2\u0239Q\3\2\2\2\u023a\u023b\7\3\2\2\u023b\u023c\7+\2\2\u023c"+
		"\u023d\5:\36\2\u023d\u023e\7\6\2\2\u023eS\3\2\2\2\u023f\u0240\7\3\2\2"+
		"\u0240\u0241\7,\2\2\u0241\u0242\5:\36\2\u0242\u0243\5:\36\2\u0243\u0244"+
		"\7\6\2\2\u0244U\3\2\2\2\u0245\u0246\7\3\2\2\u0246\u0247\7-\2\2\u0247\u0248"+
		"\5:\36\2\u0248\u0249\5:\36\2\u0249\u024a\7\6\2\2\u024aW\3\2\2\2\u024b"+
		"\u024c\7\3\2\2\u024c\u024d\7.\2\2\u024d\u024e\7I\2\2\u024e\u024f\5:\36"+
		"\2\u024f\u0250\7\6\2\2\u0250Y\3\2\2\2\u0251\u0258\5\\/\2\u0252\u0258\5"+
		"^\60\2\u0253\u0258\5`\61\2\u0254\u0258\5b\62\2\u0255\u0258\5d\63\2\u0256"+
		"\u0258\5h\65\2\u0257\u0251\3\2\2\2\u0257\u0252\3\2\2\2\u0257\u0253\3\2"+
		"\2\2\u0257\u0254\3\2\2\2\u0257\u0255\3\2\2\2\u0257\u0256\3\2\2\2\u0258"+
		"[\3\2\2\2\u0259\u025a\7\3\2\2\u025a\u025b\7\6\2\2\u025b]\3\2\2\2\u025c"+
		"\u025d\7\3\2\2\u025d\u025f\7\34\2\2\u025e\u0260\5Z.\2\u025f\u025e\3\2"+
		"\2\2\u0260\u0261\3\2\2\2\u0261\u025f\3\2\2\2\u0261\u0262\3\2\2\2\u0262"+
		"\u0263\3\2\2\2\u0263\u0264\7\6\2\2\u0264_\3\2\2\2\u0265\u0266\7\3\2\2"+
		"\u0266\u0267\7\'\2\2\u0267\u0268\7\3\2\2\u0268\u0269\5z>\2\u0269\u026a"+
		"\7\6\2\2\u026a\u026b\5Z.\2\u026b\u026c\7\6\2\2\u026ca\3\2\2\2\u026d\u026e"+
		"\7\3\2\2\u026e\u026f\7/\2\2\u026f\u0270\5:\36\2\u0270\u0271\5Z.\2\u0271"+
		"\u0272\7\6\2\2\u0272c\3\2\2\2\u0273\u0276\5f\64\2\u0274\u0276\5t;\2\u0275"+
		"\u0273\3\2\2\2\u0275\u0274\3\2\2\2\u0276e\3\2\2\2\u0277\u0278\7\3\2\2"+
		"\u0278\u0279\7\36\2\2\u0279\u027a\5t;\2\u027a\u027b\7\6\2\2\u027bg\3\2"+
		"\2\2\u027c\u027d\7\3\2\2\u027d\u027e\5j\66\2\u027e\u027f\5l\67\2\u027f"+
		"\u0280\5n8\2\u0280\u0281\7\6\2\2\u0281i\3\2\2\2\u0282\u0283\t\6\2\2\u0283"+
		"k\3\2\2\2\u0284\u0290\5\u008eH\2\u0285\u0286\7\3\2\2\u0286\u028a\5\u008e"+
		"H\2\u0287\u0289\5\u008aF\2\u0288\u0287\3\2\2\2\u0289\u028c\3\2\2\2\u028a"+
		"\u0288\3\2\2\2\u028a\u028b\3\2\2\2\u028b\u028d\3\2\2\2\u028c\u028a\3\2"+
		"\2\2\u028d\u028e\7\6\2\2\u028e\u0290\3\2\2\2\u028f\u0284\3\2\2\2\u028f"+
		"\u0285\3\2\2\2\u0290m\3\2\2\2\u0291\u02a9\7L\2\2\u0292\u0293\7\3\2\2\u0293"+
		"\u0294\5p9\2\u0294\u0295\5n8\2\u0295\u0296\5n8\2\u0296\u0297\7\6\2\2\u0297"+
		"\u02a9\3\2\2\2\u0298\u0299\7\3\2\2\u0299\u029a\5r:\2\u029a\u029c\5n8\2"+
		"\u029b\u029d\5n8\2\u029c\u029b\3\2\2\2\u029d\u029e\3\2\2\2\u029e\u029c"+
		"\3\2\2\2\u029e\u029f\3\2\2\2\u029f\u02a0\3\2\2\2\u02a0\u02a1\7\6\2\2\u02a1"+
		"\u02a9\3\2\2\2\u02a2\u02a3\7\3\2\2\u02a3\u02a4\7\t\2\2\u02a4\u02a5\5n"+
		"8\2\u02a5\u02a6\7\6\2\2\u02a6\u02a9\3\2\2\2\u02a7\u02a9\5l\67\2\u02a8"+
		"\u0291\3\2\2\2\u02a8\u0292\3\2\2\2\u02a8\u0298\3\2\2\2\u02a8\u02a2\3\2"+
		"\2\2\u02a8\u02a7\3\2\2\2\u02a9o\3\2\2\2\u02aa\u02ae\5r:\2\u02ab\u02ae"+
		"\7\t\2\2\u02ac\u02ae\7\65\2\2\u02ad\u02aa\3\2\2\2\u02ad\u02ab\3\2\2\2"+
		"\u02ad\u02ac\3\2\2\2\u02aeq\3\2\2\2\u02af\u02b0\t\7\2\2\u02b0s\3\2\2\2"+
		"\u02b1\u02b2\7\3\2\2\u02b2\u02b6\5v<\2\u02b3\u02b5\5\u0088E\2\u02b4\u02b3"+
		"\3\2\2\2\u02b5\u02b8\3\2\2\2\u02b6\u02b4\3\2\2\2\u02b6\u02b7\3\2\2\2\u02b7"+
		"\u02b9\3\2\2\2\u02b8\u02b6\3\2\2\2\u02b9\u02ba\7\6\2\2\u02bau\3\2\2\2"+
		"\u02bb\u02bc\7I\2\2\u02bcw\3\2\2\2\u02bd\u02be\7\3\2\2\u02be\u02c1\78"+
		"\2\2\u02bf\u02c1\79\2\2\u02c0\u02bd\3\2\2\2\u02c0\u02bf\3\2\2\2\u02c1"+
		"y\3\2\2\2\u02c2\u02c4\5~@\2\u02c3\u02c2\3\2\2\2\u02c4\u02c7\3\2\2\2\u02c5"+
		"\u02c3\3\2\2\2\u02c5\u02c6\3\2\2\2\u02c6{\3\2\2\2\u02c7\u02c5\3\2\2\2"+
		"\u02c8\u02ca\5\u0082B\2\u02c9\u02c8\3\2\2\2\u02ca\u02cd\3\2\2\2\u02cb"+
		"\u02c9\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc}\3\2\2\2\u02cd\u02cb\3\2\2\2"+
		"\u02ce\u02d0\7H\2\2\u02cf\u02ce\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02cf"+
		"\3\2\2\2\u02d1\u02d2\3\2\2\2\u02d2\u02d3\3\2\2\2\u02d3\u02d4\7\t\2\2\u02d4"+
		"\u02d5\5\u0086D\2\u02d5\177\3\2\2\2\u02d6\u02d7\7H\2\2\u02d7\u02d8\7\t"+
		"\2\2\u02d8\u02d9\5\u0086D\2\u02d9\u0081\3\2\2\2\u02da\u02dc\5\u0084C\2"+
		"\u02db\u02da\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02db\3\2\2\2\u02dd\u02de"+
		"\3\2\2\2\u02de\u02df\3\2\2\2\u02df\u02e0\7\t\2\2\u02e0\u02e1\5\u0086D"+
		"\2\u02e1\u0083\3\2\2\2\u02e2\u02e3\7I\2\2\u02e3\u0085\3\2\2\2\u02e4\u02ef"+
		"\7I\2\2\u02e5\u02e6\7\3\2\2\u02e6\u02e8\7:\2\2\u02e7\u02e9\5\u0086D\2"+
		"\u02e8\u02e7\3\2\2\2\u02e9\u02ea\3\2\2\2\u02ea\u02e8\3\2\2\2\u02ea\u02eb"+
		"\3\2\2\2\u02eb\u02ec\3\2\2\2\u02ec\u02ed\7\6\2\2\u02ed\u02ef\3\2\2\2\u02ee"+
		"\u02e4\3\2\2\2\u02ee\u02e5\3\2\2\2\u02ef\u0087\3\2\2\2\u02f0\u02f1\t\b"+
		"\2\2\u02f1\u0089\3\2\2\2\u02f2\u02f6\7I\2\2\u02f3\u02f6\7H\2\2\u02f4\u02f6"+
		"\5\u008cG\2\u02f5\u02f2\3\2\2\2\u02f5\u02f3\3\2\2\2\u02f5\u02f4\3\2\2"+
		"\2\u02f6\u008b\3\2\2\2\u02f7\u02f8\7\3\2\2\u02f8\u02fc\5\u008eH\2\u02f9"+
		"\u02fb\5\u008aF\2\u02fa\u02f9\3\2\2\2\u02fb\u02fe\3\2\2\2\u02fc\u02fa"+
		"\3\2\2\2\u02fc\u02fd\3\2\2\2\u02fd\u02ff\3\2\2\2\u02fe\u02fc\3\2\2\2\u02ff"+
		"\u0300\7\6\2\2\u0300\u008d\3\2\2\2\u0301\u0302\7I\2\2\u0302\u008f\3\2"+
		"\2\2\u0303\u0304\7\3\2\2\u0304\u0305\7\4\2\2\u0305\u0306\7\3\2\2\u0306"+
		"\u0307\7;\2\2\u0307\u0308\7I\2\2\u0308\u0309\7\6\2\2\u0309\u030a\7\3\2"+
		"\2\u030a\u030b\7<\2\2\u030b\u030c\7I\2\2\u030c\u030e\7\6\2\2\u030d\u030f"+
		"\5\b\5\2\u030e\u030d\3\2\2\2\u030e\u030f\3\2\2\2\u030f\u0311\3\2\2\2\u0310"+
		"\u0312\5\u0092J\2\u0311\u0310\3\2\2\2\u0311\u0312\3\2\2\2\u0312\u0314"+
		"\3\2\2\2\u0313\u0315\5\u009cO\2\u0314\u0313\3\2\2\2\u0314\u0315\3\2\2"+
		"\2\u0315\u0316\3\2\2\2\u0316\u0318\5\u0094K\2\u0317\u0319\5\u009aN\2\u0318"+
		"\u0317\3\2\2\2\u0318\u0319\3\2\2\2\u0319\u031b\3\2\2\2\u031a\u031c\5\u00a4"+
		"S\2\u031b\u031a\3\2\2\2\u031b\u031c\3\2\2\2\u031c\u031e\3\2\2\2\u031d"+
		"\u031f\5\u009eP\2\u031e\u031d\3\2\2\2\u031e\u031f\3\2\2\2\u031f\u0320"+
		"\3\2\2\2\u0320\u0321\7\6\2\2\u0321\u0091\3\2\2\2\u0322\u0323\7\3\2\2\u0323"+
		"\u0324\7=\2\2\u0324\u0325\5|?\2\u0325\u0326\7\6\2\2\u0326\u0093\3\2\2"+
		"\2\u0327\u0328\7\3\2\2\u0328\u032c\7>\2\2\u0329\u032b\5\u0096L\2\u032a"+
		"\u0329\3\2\2\2\u032b\u032e\3\2\2\2\u032c\u032a\3\2\2\2\u032c\u032d\3\2"+
		"\2\2\u032d\u032f\3\2\2\2\u032e\u032c\3\2\2\2\u032f\u0330\7\6\2\2\u0330"+
		"\u0095\3\2\2\2\u0331\u0334\5d\63\2\u0332\u0334\5\u0098M\2\u0333\u0331"+
		"\3\2\2\2\u0333\u0332\3\2\2\2\u0334\u0097\3\2\2\2\u0335\u0336\5x=\2\u0336"+
		"\u0337\5l\67\2\u0337\u0338\7L\2\2\u0338\u0339\7\6\2\2\u0339\u0099\3\2"+
		"\2\2\u033a\u033b\7\3\2\2\u033b\u033c\7?\2\2\u033c\u033d\5:\36\2\u033d"+
		"\u033e\7\6\2\2\u033e\u009b\3\2\2\2\u033f\u0340\7\3\2\2\u0340\u0346\t\t"+
		"\2\2\u0341\u0342\7\17\2\2\u0342\u0343\7\3\2\2\u0343\u0344\5z>\2\u0344"+
		"\u0345\7\6\2\2\u0345\u0347\3\2\2\2\u0346\u0341\3\2\2\2\u0346\u0347\3\2"+
		"\2\2\u0347\u0348\3\2\2\2\u0348\u0349\5\"\22\2\u0349\u009d\3\2\2\2\u034a"+
		"\u034b\7\3\2\2\u034b\u034c\7B\2\2\u034c\u034d\5\u00a0Q\2\u034d\u034e\5"+
		"\u00a2R\2\u034e\u034f\7\6\2\2\u034f\u009f\3\2\2\2\u0350\u0351\t\n\2\2"+
		"\u0351\u00a1\3\2\2\2\u0352\u0353\7\3\2\2\u0353\u0354\5p9\2\u0354\u0355"+
		"\5\u00a2R\2\u0355\u0356\5\u00a2R\2\u0356\u0357\7\6\2\2\u0357\u0378\3\2"+
		"\2\2\u0358\u0359\7\3\2\2\u0359\u035a\5r:\2\u035a\u035c\5\u00a2R\2\u035b"+
		"\u035d\5\u00a2R\2\u035c\u035b\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u035c"+
		"\3\2\2\2\u035e\u035f\3\2\2\2\u035f\u0360\3\2\2\2\u0360\u0361\7\6\2\2\u0361"+
		"\u0378\3\2\2\2\u0362\u0363\7\3\2\2\u0363\u0366\7\t\2\2\u0364\u0366\7E"+
		"\2\2\u0365\u0362\3\2\2\2\u0365\u0364\3\2\2\2\u0366\u0367\3\2\2\2\u0367"+
		"\u0368\5\u00a2R\2\u0368\u0369\7\6\2\2\u0369\u0378\3\2\2\2\u036a\u0378"+
		"\7L\2\2\u036b\u036c\7\3\2\2\u036c\u0370\5\u008eH\2\u036d\u036f\7I\2\2"+
		"\u036e\u036d\3\2\2\2\u036f\u0372\3\2\2\2\u0370\u036e\3\2\2\2\u0370\u0371"+
		"\3\2\2\2\u0371\u0373\3\2\2\2\u0372\u0370\3\2\2\2\u0373\u0374\7\6\2\2\u0374"+
		"\u0378\3\2\2\2\u0375\u0378\7F\2\2\u0376\u0378\5\u008eH\2\u0377\u0352\3"+
		"\2\2\2\u0377\u0358\3\2\2\2\u0377\u0365\3\2\2\2\u0377\u036a\3\2\2\2\u0377"+
		"\u036b\3\2\2\2\u0377\u0375\3\2\2\2\u0377\u0376\3\2\2\2\u0378\u00a3\3\2"+
		"\2\2\u0379\u037a\7\3\2\2\u037a\u037b\7\31\2\2\u037b\u037c\5:\36\2\u037c"+
		"\u037d\7\6\2\2\u037d\u00a5\3\2\2\2J\u00a8\u00b1\u00b4\u00b7\u00ba\u00bd"+
		"\u00c2\u00c8\u00ce\u00dd\u00e7\u00ef\u00f4\u0100\u010f\u0113\u0122\u0126"+
		"\u0139\u013f\u0143\u0149\u014d\u0151\u0155\u0163\u0167\u016e\u017a\u0180"+
		"\u018c\u0190\u019e\u01a8\u01ac\u01ca\u01d4\u01d8\u01f4\u01fe\u0207\u0257"+
		"\u0261\u0275\u028a\u028f\u029e\u02a8\u02ad\u02b6\u02c0\u02c5\u02cb\u02d1"+
		"\u02dd\u02ea\u02ee\u02f5\u02fc\u030e\u0311\u0314\u0318\u031b\u031e\u032c"+
		"\u0333\u0346\u035e\u0365\u0370\u0377";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}