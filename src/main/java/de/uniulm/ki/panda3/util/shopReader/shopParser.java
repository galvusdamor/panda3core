// Generated from /home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/util/shop/shop.g4 by ANTLR 4.5.3
package de.uniulm.ki.panda3.util.shopReader;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class shopParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, VAR_NAME=11, NAME=12, COMMENT=13, WS=14, NUMBER=15;
	public static final int
		RULE_domain = 0, RULE_method = 1, RULE_ifThen = 2, RULE_taskList = 3, 
		RULE_task = 4, RULE_taskName = 5, RULE_operator = 6, RULE_formulaList = 7, 
		RULE_formula = 8, RULE_posFormula = 9, RULE_negFormula = 10, RULE_problem = 11, 
		RULE_opName = 12, RULE_param = 13;
	public static final String[] ruleNames = {
		"domain", "method", "ifThen", "taskList", "task", "taskName", "operator", 
		"formulaList", "formula", "posFormula", "negFormula", "problem", "opName", 
		"param"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'(defdomain'", "'('", "')'", "'(:method'", "':task'", "'(:operator'", 
		"'not'", "'(defproblem'", "'(:unordered'", "'!'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "VAR_NAME", 
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

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "shop.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public shopParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DomainContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(shopParser.NAME, 0); }
		public List<OperatorContext> operator() {
			return getRuleContexts(OperatorContext.class);
		}
		public OperatorContext operator(int i) {
			return getRuleContext(OperatorContext.class,i);
		}
		public List<MethodContext> method() {
			return getRuleContexts(MethodContext.class);
		}
		public MethodContext method(int i) {
			return getRuleContext(MethodContext.class,i);
		}
		public DomainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterDomain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitDomain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitDomain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DomainContext domain() throws RecognitionException {
		DomainContext _localctx = new DomainContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_domain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(T__0);
			setState(29);
			match(NAME);
			setState(30);
			match(T__1);
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__5) {
				{
				setState(33);
				switch (_input.LA(1)) {
				case T__5:
					{
					setState(31);
					operator();
					}
					break;
				case T__3:
					{
					setState(32);
					method();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(38);
			match(T__2);
			setState(39);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodContext extends ParserRuleContext {
		public TaskContext task() {
			return getRuleContext(TaskContext.class,0);
		}
		public List<IfThenContext> ifThen() {
			return getRuleContexts(IfThenContext.class);
		}
		public IfThenContext ifThen(int i) {
			return getRuleContext(IfThenContext.class,i);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(T__3);
			setState(42);
			task();
			setState(44); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(43);
				ifThen();
				}
				}
				setState(46); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__1 );
			setState(48);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfThenContext extends ParserRuleContext {
		public FormulaListContext formulaList() {
			return getRuleContext(FormulaListContext.class,0);
		}
		public TaskListContext taskList() {
			return getRuleContext(TaskListContext.class,0);
		}
		public IfThenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifThen; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterIfThen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitIfThen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitIfThen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfThenContext ifThen() throws RecognitionException {
		IfThenContext _localctx = new IfThenContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_ifThen);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			formulaList();
			setState(51);
			taskList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TaskListContext extends ParserRuleContext {
		public List<TaskContext> task() {
			return getRuleContexts(TaskContext.class);
		}
		public TaskContext task(int i) {
			return getRuleContext(TaskContext.class,i);
		}
		public TaskListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_taskList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterTaskList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitTaskList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitTaskList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TaskListContext taskList() throws RecognitionException {
		TaskListContext _localctx = new TaskListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_taskList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(T__1);
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(54);
				task();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TaskContext extends ParserRuleContext {
		public TaskNameContext taskName() {
			return getRuleContext(TaskNameContext.class,0);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public TaskContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_task; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterTask(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitTask(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitTask(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TaskContext task() throws RecognitionException {
		TaskContext _localctx = new TaskContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_task);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(T__1);
			setState(64);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(63);
				match(T__4);
				}
			}

			setState(66);
			taskName();
			setState(68); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(67);
				param();
				}
				}
				setState(70); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME || _la==NAME );
			setState(72);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TaskNameContext extends ParserRuleContext {
		public OpNameContext opName() {
			return getRuleContext(OpNameContext.class,0);
		}
		public TerminalNode NAME() { return getToken(shopParser.NAME, 0); }
		public TaskNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_taskName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterTaskName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitTaskName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitTaskName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TaskNameContext taskName() throws RecognitionException {
		TaskNameContext _localctx = new TaskNameContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_taskName);
		try {
			setState(76);
			switch (_input.LA(1)) {
			case T__9:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				opName();
				}
				break;
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				match(NAME);
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

	public static class OperatorContext extends ParserRuleContext {
		public TaskContext task() {
			return getRuleContext(TaskContext.class,0);
		}
		public List<FormulaListContext> formulaList() {
			return getRuleContexts(FormulaListContext.class);
		}
		public FormulaListContext formulaList(int i) {
			return getRuleContext(FormulaListContext.class,i);
		}
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(T__5);
			setState(79);
			task();
			setState(80);
			formulaList();
			setState(81);
			formulaList();
			setState(82);
			formulaList();
			setState(83);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormulaListContext extends ParserRuleContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public FormulaListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formulaList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterFormulaList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitFormulaList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitFormulaList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaListContext formulaList() throws RecognitionException {
		FormulaListContext _localctx = new FormulaListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_formulaList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(T__1);
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(86);
				formula();
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(92);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormulaContext extends ParserRuleContext {
		public PosFormulaContext posFormula() {
			return getRuleContext(PosFormulaContext.class,0);
		}
		public NegFormulaContext negFormula() {
			return getRuleContext(NegFormulaContext.class,0);
		}
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_formula);
		try {
			setState(96);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				posFormula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(95);
				negFormula();
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

	public static class PosFormulaContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(shopParser.NAME, 0); }
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public PosFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_posFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterPosFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitPosFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitPosFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PosFormulaContext posFormula() throws RecognitionException {
		PosFormulaContext _localctx = new PosFormulaContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_posFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(T__1);
			setState(99);
			match(NAME);
			setState(101); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(100);
				param();
				}
				}
				setState(103); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VAR_NAME || _la==NAME );
			setState(105);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NegFormulaContext extends ParserRuleContext {
		public PosFormulaContext posFormula() {
			return getRuleContext(PosFormulaContext.class,0);
		}
		public NegFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterNegFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitNegFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitNegFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegFormulaContext negFormula() throws RecognitionException {
		NegFormulaContext _localctx = new NegFormulaContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_negFormula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(T__1);
			setState(108);
			match(T__6);
			setState(109);
			posFormula();
			setState(110);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
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
		public List<TerminalNode> NAME() { return getTokens(shopParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(shopParser.NAME, i);
		}
		public FormulaListContext formulaList() {
			return getRuleContext(FormulaListContext.class,0);
		}
		public List<TaskContext> task() {
			return getRuleContexts(TaskContext.class);
		}
		public TaskContext task(int i) {
			return getRuleContext(TaskContext.class,i);
		}
		public ProblemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_problem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterProblem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitProblem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitProblem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProblemContext problem() throws RecognitionException {
		ProblemContext _localctx = new ProblemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_problem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(T__7);
			setState(113);
			match(NAME);
			setState(114);
			match(NAME);
			setState(115);
			formulaList();
			setState(116);
			match(T__8);
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(117);
				task();
				}
				}
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			match(T__2);
			setState(124);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OpNameContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(shopParser.NAME, 0); }
		public OpNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_opName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterOpName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitOpName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitOpName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpNameContext opName() throws RecognitionException {
		OpNameContext _localctx = new OpNameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_opName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(126);
				match(T__9);
				}
				}
				setState(129); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__9 );
			setState(131);
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

	public static class ParamContext extends ParserRuleContext {
		public TerminalNode VAR_NAME() { return getToken(shopParser.VAR_NAME, 0); }
		public TerminalNode NAME() { return getToken(shopParser.NAME, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof shopListener ) ((shopListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof shopVisitor ) return ((shopVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\21\u008a\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\7\2$\n\2"+
		"\f\2\16\2\'\13\2\3\2\3\2\3\2\3\3\3\3\3\3\6\3/\n\3\r\3\16\3\60\3\3\3\3"+
		"\3\4\3\4\3\4\3\5\3\5\7\5:\n\5\f\5\16\5=\13\5\3\5\3\5\3\6\3\6\5\6C\n\6"+
		"\3\6\3\6\6\6G\n\6\r\6\16\6H\3\6\3\6\3\7\3\7\5\7O\n\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\7\tZ\n\t\f\t\16\t]\13\t\3\t\3\t\3\n\3\n\5\nc\n\n\3"+
		"\13\3\13\3\13\6\13h\n\13\r\13\16\13i\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\7\ry\n\r\f\r\16\r|\13\r\3\r\3\r\3\r\3\16\6\16\u0082"+
		"\n\16\r\16\16\16\u0083\3\16\3\16\3\17\3\17\3\17\2\2\20\2\4\6\b\n\f\16"+
		"\20\22\24\26\30\32\34\2\3\3\2\r\16\u0087\2\36\3\2\2\2\4+\3\2\2\2\6\64"+
		"\3\2\2\2\b\67\3\2\2\2\n@\3\2\2\2\fN\3\2\2\2\16P\3\2\2\2\20W\3\2\2\2\22"+
		"b\3\2\2\2\24d\3\2\2\2\26m\3\2\2\2\30r\3\2\2\2\32\u0081\3\2\2\2\34\u0087"+
		"\3\2\2\2\36\37\7\3\2\2\37 \7\16\2\2 %\7\4\2\2!$\5\16\b\2\"$\5\4\3\2#!"+
		"\3\2\2\2#\"\3\2\2\2$\'\3\2\2\2%#\3\2\2\2%&\3\2\2\2&(\3\2\2\2\'%\3\2\2"+
		"\2()\7\5\2\2)*\7\5\2\2*\3\3\2\2\2+,\7\6\2\2,.\5\n\6\2-/\5\6\4\2.-\3\2"+
		"\2\2/\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\62\3\2\2\2\62\63\7\5\2\2"+
		"\63\5\3\2\2\2\64\65\5\20\t\2\65\66\5\b\5\2\66\7\3\2\2\2\67;\7\4\2\28:"+
		"\5\n\6\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<>\3\2\2\2=;\3\2\2\2>"+
		"?\7\5\2\2?\t\3\2\2\2@B\7\4\2\2AC\7\7\2\2BA\3\2\2\2BC\3\2\2\2CD\3\2\2\2"+
		"DF\5\f\7\2EG\5\34\17\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IJ\3\2\2"+
		"\2JK\7\5\2\2K\13\3\2\2\2LO\5\32\16\2MO\7\16\2\2NL\3\2\2\2NM\3\2\2\2O\r"+
		"\3\2\2\2PQ\7\b\2\2QR\5\n\6\2RS\5\20\t\2ST\5\20\t\2TU\5\20\t\2UV\7\5\2"+
		"\2V\17\3\2\2\2W[\7\4\2\2XZ\5\22\n\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3"+
		"\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7\5\2\2_\21\3\2\2\2`c\5\24\13\2ac\5\26\f"+
		"\2b`\3\2\2\2ba\3\2\2\2c\23\3\2\2\2de\7\4\2\2eg\7\16\2\2fh\5\34\17\2gf"+
		"\3\2\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2\2\2jk\3\2\2\2kl\7\5\2\2l\25\3\2\2\2"+
		"mn\7\4\2\2no\7\t\2\2op\5\24\13\2pq\7\5\2\2q\27\3\2\2\2rs\7\n\2\2st\7\16"+
		"\2\2tu\7\16\2\2uv\5\20\t\2vz\7\13\2\2wy\5\n\6\2xw\3\2\2\2y|\3\2\2\2zx"+
		"\3\2\2\2z{\3\2\2\2{}\3\2\2\2|z\3\2\2\2}~\7\5\2\2~\177\7\5\2\2\177\31\3"+
		"\2\2\2\u0080\u0082\7\f\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0086\7\16"+
		"\2\2\u0086\33\3\2\2\2\u0087\u0088\t\2\2\2\u0088\35\3\2\2\2\16#%\60;BH"+
		"N[biz\u0083";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}