// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/parser/hddl/hddl.g4 by ANTLR 4.5
package de.uniulm.ki.panda3.parser.hddl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class hddlParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
            T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, T__16 = 17,
            T__17 = 18, T__18 = 19, T__19 = 20, T__20 = 21, T__21 = 22, T__22 = 23, T__23 = 24,
            T__24 = 25, REQUIRE_NAME = 26, VAR_NAME = 27, NAME = 28, WS = 29;
    public static final int
            RULE_hddl_file = 0, RULE_domain = 1, RULE_domain_symbol = 2, RULE_require_def = 3,
            RULE_require_defs = 4, RULE_type_def = 5, RULE_predicates_def = 6, RULE_atomic_formular_skeleton = 7,
            RULE_task_def = 8, RULE_task_symbol = 9, RULE_method_def = 10, RULE_method_symbol = 11,
            RULE_subtask_defs = 12, RULE_subtask_def = 13, RULE_subtask_id = 14, RULE_ordering_defs = 15,
            RULE_ordering_def = 16, RULE_constraint_defs = 17, RULE_constraint_def = 18,
            RULE_action_def = 19, RULE_action_symbol = 20, RULE_gd = 21, RULE_effect_body = 22,
            RULE_c_effect = 23, RULE_p_effect = 24, RULE_cond_effect = 25, RULE_atomic_formular = 26,
            RULE_predicate = 27, RULE_typed_var_list = 28, RULE_typed_vars = 29, RULE_var_type = 30,
            RULE_problem = 31;
    public static final String[] ruleNames = {
            "hddl_file", "domain", "domain_symbol", "require_def", "require_defs",
            "type_def", "predicates_def", "atomic_formular_skeleton", "task_def",
            "task_symbol", "method_def", "method_symbol", "subtask_defs", "subtask_def",
            "subtask_id", "ordering_defs", "ordering_def", "constraint_defs", "constraint_def",
            "action_def", "action_symbol", "gd", "effect_body", "c_effect", "p_effect",
            "cond_effect", "atomic_formular", "predicate", "typed_var_list", "typed_vars",
            "var_type", "problem"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'('", "'define'", "'domain'", "')'", "':requirements'", "':types'",
            "'-'", "':predicates'", "':task'", "':parameters'", "':precondition'",
            "':effect'", "':method'", "':subtasks'", "':ordering'", "':constraints'",
            "'and'", "'<'", "'not'", "'='", "':action'", "'or'", "'forall'", "'when'",
            "''"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, "REQUIRE_NAME", "VAR_NAME", "NAME", "WS"
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
    public String getGrammarFileName() {
        return "hddl.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public hddlParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class Hddl_fileContext extends ParserRuleContext {
        public DomainContext domain() {
            return getRuleContext(DomainContext.class, 0);
        }

        public ProblemContext problem() {
            return getRuleContext(ProblemContext.class, 0);
        }

        public Hddl_fileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hddl_file;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterHddl_file(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitHddl_file(this);
        }
    }

    public final Hddl_fileContext hddl_file() throws RecognitionException {
        Hddl_fileContext _localctx = new Hddl_fileContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_hddl_file);
        try {
            setState(66);
            switch (_input.LA(1)) {
                case T__0:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(64);
                    domain();
                }
                break;
                case T__24:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(65);
                    problem();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DomainContext extends ParserRuleContext {
        public Domain_symbolContext domain_symbol() {
            return getRuleContext(Domain_symbolContext.class, 0);
        }

        public Require_defContext require_def() {
            return getRuleContext(Require_defContext.class, 0);
        }

        public Type_defContext type_def() {
            return getRuleContext(Type_defContext.class, 0);
        }

        public Predicates_defContext predicates_def() {
            return getRuleContext(Predicates_defContext.class, 0);
        }

        public List<Task_defContext> task_def() {
            return getRuleContexts(Task_defContext.class);
        }

        public Task_defContext task_def(int i) {
            return getRuleContext(Task_defContext.class, i);
        }

        public List<Method_defContext> method_def() {
            return getRuleContexts(Method_defContext.class);
        }

        public Method_defContext method_def(int i) {
            return getRuleContext(Method_defContext.class, i);
        }

        public List<Action_defContext> action_def() {
            return getRuleContexts(Action_defContext.class);
        }

        public Action_defContext action_def(int i) {
            return getRuleContext(Action_defContext.class, i);
        }

        public DomainContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_domain;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterDomain(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitDomain(this);
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
                setState(68);
                match(T__0);
                setState(69);
                match(T__1);
                setState(70);
                match(T__0);
                setState(71);
                match(T__2);
                setState(72);
                domain_symbol();
                setState(73);
                match(T__3);
                setState(75);
                switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                    case 1: {
                        setState(74);
                        require_def();
                    }
                    break;
                }
                setState(78);
                switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
                    case 1: {
                        setState(77);
                        type_def();
                    }
                    break;
                }
                setState(81);
                switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                    case 1: {
                        setState(80);
                        predicates_def();
                    }
                    break;
                }
                setState(86);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(83);
                                task_def();
                            }
                        }
                    }
                    setState(88);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
                }
                setState(92);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(89);
                                method_def();
                            }
                        }
                    }
                    setState(94);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                }
                setState(98);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__0) {
                    {
                        {
                            setState(95);
                            action_def();
                        }
                    }
                    setState(100);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(101);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Domain_symbolContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Domain_symbolContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_domain_symbol;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterDomain_symbol(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitDomain_symbol(this);
        }
    }

    public final Domain_symbolContext domain_symbol() throws RecognitionException {
        Domain_symbolContext _localctx = new Domain_symbolContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_domain_symbol);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(103);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Require_defContext extends ParserRuleContext {
        public Require_defsContext alldefs;

        public Require_defsContext require_defs() {
            return getRuleContext(Require_defsContext.class, 0);
        }

        public Require_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_require_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterRequire_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitRequire_def(this);
        }
    }

    public final Require_defContext require_def() throws RecognitionException {
        Require_defContext _localctx = new Require_defContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_require_def);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(105);
                match(T__0);
                setState(106);
                match(T__4);
                setState(107);
                ((Require_defContext) _localctx).alldefs = require_defs();
                setState(108);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Require_defsContext extends ParserRuleContext {
        public List<TerminalNode> REQUIRE_NAME() {
            return getTokens(hddlParser.REQUIRE_NAME);
        }

        public TerminalNode REQUIRE_NAME(int i) {
            return getToken(hddlParser.REQUIRE_NAME, i);
        }

        public Require_defsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_require_defs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterRequire_defs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitRequire_defs(this);
        }
    }

    public final Require_defsContext require_defs() throws RecognitionException {
        Require_defsContext _localctx = new Require_defsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_require_defs);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(111);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(110);
                            match(REQUIRE_NAME);
                        }
                    }
                    setState(113);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == REQUIRE_NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Type_defContext extends ParserRuleContext {
        public List<Var_typeContext> var_type() {
            return getRuleContexts(Var_typeContext.class);
        }

        public Var_typeContext var_type(int i) {
            return getRuleContext(Var_typeContext.class, i);
        }

        public List<TerminalNode> NAME() {
            return getTokens(hddlParser.NAME);
        }

        public TerminalNode NAME(int i) {
            return getToken(hddlParser.NAME, i);
        }

        public Type_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterType_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitType_def(this);
        }
    }

    public final Type_defContext type_def() throws RecognitionException {
        Type_defContext _localctx = new Type_defContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_type_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(115);
                match(T__0);
                setState(116);
                match(T__5);
                setState(124);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(118);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(117);
                                        match(NAME);
                                    }
                                }
                                setState(120);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == NAME);
                            setState(122);
                            match(T__6);
                            setState(123);
                            var_type();
                        }
                    }
                    setState(126);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(128);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Predicates_defContext extends ParserRuleContext {
        public List<Atomic_formular_skeletonContext> atomic_formular_skeleton() {
            return getRuleContexts(Atomic_formular_skeletonContext.class);
        }

        public Atomic_formular_skeletonContext atomic_formular_skeleton(int i) {
            return getRuleContext(Atomic_formular_skeletonContext.class, i);
        }

        public Predicates_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_predicates_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterPredicates_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitPredicates_def(this);
        }
    }

    public final Predicates_defContext predicates_def() throws RecognitionException {
        Predicates_defContext _localctx = new Predicates_defContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_predicates_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(130);
                match(T__0);
                setState(131);
                match(T__7);
                setState(133);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(132);
                            atomic_formular_skeleton();
                        }
                    }
                    setState(135);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == T__0);
                setState(137);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Atomic_formular_skeletonContext extends ParserRuleContext {
        public PredicateContext predicate() {
            return getRuleContext(PredicateContext.class, 0);
        }

        public Typed_var_listContext typed_var_list() {
            return getRuleContext(Typed_var_listContext.class, 0);
        }

        public Atomic_formular_skeletonContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_atomic_formular_skeleton;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterAtomic_formular_skeleton(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitAtomic_formular_skeleton(this);
        }
    }

    public final Atomic_formular_skeletonContext atomic_formular_skeleton() throws RecognitionException {
        Atomic_formular_skeletonContext _localctx = new Atomic_formular_skeletonContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_atomic_formular_skeleton);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(139);
                match(T__0);
                setState(140);
                predicate();
                setState(141);
                typed_var_list();
                setState(142);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Task_defContext extends ParserRuleContext {
        public Task_symbolContext task_symbol() {
            return getRuleContext(Task_symbolContext.class, 0);
        }

        public Typed_var_listContext typed_var_list() {
            return getRuleContext(Typed_var_listContext.class, 0);
        }

        public GdContext gd() {
            return getRuleContext(GdContext.class, 0);
        }

        public Effect_bodyContext effect_body() {
            return getRuleContext(Effect_bodyContext.class, 0);
        }

        public Task_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_task_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterTask_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitTask_def(this);
        }
    }

    public final Task_defContext task_def() throws RecognitionException {
        Task_defContext _localctx = new Task_defContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_task_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(144);
                match(T__0);
                setState(145);
                match(T__8);
                setState(146);
                task_symbol();
                setState(147);
                match(T__9);
                setState(148);
                match(T__0);
                setState(149);
                typed_var_list();
                setState(150);
                match(T__3);
                setState(153);
                _la = _input.LA(1);
                if (_la == T__10) {
                    {
                        setState(151);
                        match(T__10);
                        setState(152);
                        gd();
                    }
                }

                setState(157);
                _la = _input.LA(1);
                if (_la == T__11) {
                    {
                        setState(155);
                        match(T__11);
                        setState(156);
                        effect_body();
                    }
                }

                setState(159);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Task_symbolContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Task_symbolContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_task_symbol;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterTask_symbol(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitTask_symbol(this);
        }
    }

    public final Task_symbolContext task_symbol() throws RecognitionException {
        Task_symbolContext _localctx = new Task_symbolContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_task_symbol);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(161);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Method_defContext extends ParserRuleContext {
        public Method_symbolContext method_symbol() {
            return getRuleContext(Method_symbolContext.class, 0);
        }

        public Typed_var_listContext typed_var_list() {
            return getRuleContext(Typed_var_listContext.class, 0);
        }

        public Task_symbolContext task_symbol() {
            return getRuleContext(Task_symbolContext.class, 0);
        }

        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public GdContext gd() {
            return getRuleContext(GdContext.class, 0);
        }

        public Subtask_defsContext subtask_defs() {
            return getRuleContext(Subtask_defsContext.class, 0);
        }

        public Ordering_defsContext ordering_defs() {
            return getRuleContext(Ordering_defsContext.class, 0);
        }

        public Constraint_defsContext constraint_defs() {
            return getRuleContext(Constraint_defsContext.class, 0);
        }

        public Method_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_method_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterMethod_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitMethod_def(this);
        }
    }

    public final Method_defContext method_def() throws RecognitionException {
        Method_defContext _localctx = new Method_defContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_method_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(163);
                match(T__0);
                setState(164);
                match(T__12);
                setState(165);
                method_symbol();
                setState(166);
                match(T__9);
                setState(167);
                match(T__0);
                setState(168);
                typed_var_list();
                setState(169);
                match(T__3);
                setState(170);
                match(T__8);
                setState(171);
                match(T__0);
                setState(172);
                task_symbol();
                setState(176);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VAR_NAME) {
                    {
                        {
                            setState(173);
                            match(VAR_NAME);
                        }
                    }
                    setState(178);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(179);
                match(T__3);
                setState(182);
                _la = _input.LA(1);
                if (_la == T__10) {
                    {
                        setState(180);
                        match(T__10);
                        setState(181);
                        gd();
                    }
                }

                setState(186);
                _la = _input.LA(1);
                if (_la == T__13) {
                    {
                        setState(184);
                        match(T__13);
                        setState(185);
                        subtask_defs();
                    }
                }

                setState(190);
                _la = _input.LA(1);
                if (_la == T__14) {
                    {
                        setState(188);
                        match(T__14);
                        setState(189);
                        ordering_defs();
                    }
                }

                setState(194);
                _la = _input.LA(1);
                if (_la == T__15) {
                    {
                        setState(192);
                        match(T__15);
                        setState(193);
                        constraint_defs();
                    }
                }

                setState(196);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Method_symbolContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Method_symbolContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_method_symbol;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterMethod_symbol(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitMethod_symbol(this);
        }
    }

    public final Method_symbolContext method_symbol() throws RecognitionException {
        Method_symbolContext _localctx = new Method_symbolContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_method_symbol);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(198);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Subtask_defsContext extends ParserRuleContext {
        public List<Subtask_defContext> subtask_def() {
            return getRuleContexts(Subtask_defContext.class);
        }

        public Subtask_defContext subtask_def(int i) {
            return getRuleContext(Subtask_defContext.class, i);
        }

        public Subtask_defsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subtask_defs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterSubtask_defs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitSubtask_defs(this);
        }
    }

    public final Subtask_defsContext subtask_defs() throws RecognitionException {
        Subtask_defsContext _localctx = new Subtask_defsContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_subtask_defs);
        int _la;
        try {
            setState(210);
            switch (getInterpreter().adaptivePredict(_input, 19, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(200);
                    subtask_def();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(201);
                    match(T__0);
                    setState(202);
                    match(T__16);
                    setState(204);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(203);
                                subtask_def();
                            }
                        }
                        setState(206);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(208);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Subtask_defContext extends ParserRuleContext {
        public Subtask_idContext subtask_id() {
            return getRuleContext(Subtask_idContext.class, 0);
        }

        public Task_symbolContext task_symbol() {
            return getRuleContext(Task_symbolContext.class, 0);
        }

        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public Subtask_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subtask_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterSubtask_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitSubtask_def(this);
        }
    }

    public final Subtask_defContext subtask_def() throws RecognitionException {
        Subtask_defContext _localctx = new Subtask_defContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_subtask_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(212);
                match(T__0);
                setState(213);
                subtask_id();
                setState(214);
                match(T__0);
                setState(215);
                task_symbol();
                setState(217);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(216);
                            match(VAR_NAME);
                        }
                    }
                    setState(219);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == VAR_NAME);
                setState(221);
                match(T__3);
                setState(222);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Subtask_idContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Subtask_idContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subtask_id;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterSubtask_id(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitSubtask_id(this);
        }
    }

    public final Subtask_idContext subtask_id() throws RecognitionException {
        Subtask_idContext _localctx = new Subtask_idContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_subtask_id);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(224);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Ordering_defsContext extends ParserRuleContext {
        public List<Ordering_defContext> ordering_def() {
            return getRuleContexts(Ordering_defContext.class);
        }

        public Ordering_defContext ordering_def(int i) {
            return getRuleContext(Ordering_defContext.class, i);
        }

        public Ordering_defsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ordering_defs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterOrdering_defs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitOrdering_defs(this);
        }
    }

    public final Ordering_defsContext ordering_defs() throws RecognitionException {
        Ordering_defsContext _localctx = new Ordering_defsContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_ordering_defs);
        int _la;
        try {
            setState(236);
            switch (getInterpreter().adaptivePredict(_input, 22, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(226);
                    ordering_def();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(227);
                    match(T__0);
                    setState(228);
                    match(T__16);
                    setState(230);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(229);
                                ordering_def();
                            }
                        }
                        setState(232);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(234);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Ordering_defContext extends ParserRuleContext {
        public List<Subtask_idContext> subtask_id() {
            return getRuleContexts(Subtask_idContext.class);
        }

        public Subtask_idContext subtask_id(int i) {
            return getRuleContext(Subtask_idContext.class, i);
        }

        public Ordering_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ordering_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterOrdering_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitOrdering_def(this);
        }
    }

    public final Ordering_defContext ordering_def() throws RecognitionException {
        Ordering_defContext _localctx = new Ordering_defContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_ordering_def);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(238);
                match(T__0);
                setState(239);
                subtask_id();
                setState(240);
                match(T__17);
                setState(241);
                subtask_id();
                setState(242);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Constraint_defsContext extends ParserRuleContext {
        public List<Constraint_defContext> constraint_def() {
            return getRuleContexts(Constraint_defContext.class);
        }

        public Constraint_defContext constraint_def(int i) {
            return getRuleContext(Constraint_defContext.class, i);
        }

        public Constraint_defsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_constraint_defs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterConstraint_defs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitConstraint_defs(this);
        }
    }

    public final Constraint_defsContext constraint_defs() throws RecognitionException {
        Constraint_defsContext _localctx = new Constraint_defsContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_constraint_defs);
        int _la;
        try {
            setState(254);
            switch (getInterpreter().adaptivePredict(_input, 24, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(244);
                    constraint_def();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(245);
                    match(T__0);
                    setState(246);
                    match(T__16);
                    setState(248);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(247);
                                constraint_def();
                            }
                        }
                        setState(250);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(252);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Constraint_defContext extends ParserRuleContext {
        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public Constraint_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_constraint_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterConstraint_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitConstraint_def(this);
        }
    }

    public final Constraint_defContext constraint_def() throws RecognitionException {
        Constraint_defContext _localctx = new Constraint_defContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_constraint_def);
        try {
            setState(269);
            switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(256);
                    match(T__0);
                    setState(257);
                    match(T__18);
                    setState(258);
                    match(T__0);
                    setState(259);
                    match(T__19);
                    setState(260);
                    match(VAR_NAME);
                    setState(261);
                    match(VAR_NAME);
                    setState(262);
                    match(T__3);
                    setState(263);
                    match(T__3);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(264);
                    match(T__0);
                    setState(265);
                    match(T__19);
                    setState(266);
                    match(VAR_NAME);
                    setState(267);
                    match(VAR_NAME);
                    setState(268);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Action_defContext extends ParserRuleContext {
        public Action_symbolContext action_symbol() {
            return getRuleContext(Action_symbolContext.class, 0);
        }

        public Typed_var_listContext typed_var_list() {
            return getRuleContext(Typed_var_listContext.class, 0);
        }

        public GdContext gd() {
            return getRuleContext(GdContext.class, 0);
        }

        public Effect_bodyContext effect_body() {
            return getRuleContext(Effect_bodyContext.class, 0);
        }

        public Action_defContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_action_def;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterAction_def(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitAction_def(this);
        }
    }

    public final Action_defContext action_def() throws RecognitionException {
        Action_defContext _localctx = new Action_defContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_action_def);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(271);
                match(T__0);
                setState(272);
                match(T__20);
                setState(273);
                action_symbol();
                setState(274);
                match(T__9);
                setState(275);
                match(T__0);
                setState(276);
                typed_var_list();
                setState(277);
                match(T__3);
                setState(280);
                _la = _input.LA(1);
                if (_la == T__10) {
                    {
                        setState(278);
                        match(T__10);
                        setState(279);
                        gd();
                    }
                }

                setState(284);
                _la = _input.LA(1);
                if (_la == T__11) {
                    {
                        setState(282);
                        match(T__11);
                        setState(283);
                        effect_body();
                    }
                }

                setState(286);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Action_symbolContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Action_symbolContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_action_symbol;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterAction_symbol(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitAction_symbol(this);
        }
    }

    public final Action_symbolContext action_symbol() throws RecognitionException {
        Action_symbolContext _localctx = new Action_symbolContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_action_symbol);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(288);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class GdContext extends ParserRuleContext {
        public Atomic_formularContext atomic_formular() {
            return getRuleContext(Atomic_formularContext.class, 0);
        }

        public List<GdContext> gd() {
            return getRuleContexts(GdContext.class);
        }

        public GdContext gd(int i) {
            return getRuleContext(GdContext.class, i);
        }

        public GdContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_gd;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterGd(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitGd(this);
        }
    }

    public final GdContext gd() throws RecognitionException {
        GdContext _localctx = new GdContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_gd);
        int _la;
        try {
            setState(307);
            switch (getInterpreter().adaptivePredict(_input, 29, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(290);
                    match(T__0);
                    setState(291);
                    match(T__3);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(292);
                    atomic_formular();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(293);
                    match(T__0);
                    setState(294);
                    _la = _input.LA(1);
                    if (!(_la == T__16 || _la == T__21)) {
                        _errHandler.recoverInline(this);
                    }
                    consume();
                    setState(296);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(295);
                                gd();
                            }
                        }
                        setState(298);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(300);
                    match(T__3);
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(302);
                    match(T__0);
                    setState(303);
                    match(T__18);
                    setState(304);
                    gd();
                    setState(305);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Effect_bodyContext extends ParserRuleContext {
        public List<C_effectContext> c_effect() {
            return getRuleContexts(C_effectContext.class);
        }

        public C_effectContext c_effect(int i) {
            return getRuleContext(C_effectContext.class, i);
        }

        public Effect_bodyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect_body;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterEffect_body(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitEffect_body(this);
        }
    }

    public final Effect_bodyContext effect_body() throws RecognitionException {
        Effect_bodyContext _localctx = new Effect_bodyContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_effect_body);
        int _la;
        try {
            setState(321);
            switch (getInterpreter().adaptivePredict(_input, 31, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(309);
                    match(T__0);
                    setState(310);
                    match(T__3);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(311);
                    c_effect();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(312);
                    match(T__0);
                    setState(313);
                    match(T__16);
                    setState(315);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(314);
                                c_effect();
                            }
                        }
                        setState(317);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(319);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class C_effectContext extends ParserRuleContext {
        public P_effectContext p_effect() {
            return getRuleContext(P_effectContext.class, 0);
        }

        public Effect_bodyContext effect_body() {
            return getRuleContext(Effect_bodyContext.class, 0);
        }

        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public GdContext gd() {
            return getRuleContext(GdContext.class, 0);
        }

        public Cond_effectContext cond_effect() {
            return getRuleContext(Cond_effectContext.class, 0);
        }

        public C_effectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_c_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterC_effect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitC_effect(this);
        }
    }

    public final C_effectContext c_effect() throws RecognitionException {
        C_effectContext _localctx = new C_effectContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_c_effect);
        int _la;
        try {
            setState(343);
            switch (getInterpreter().adaptivePredict(_input, 33, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(323);
                    p_effect();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(324);
                    match(T__0);
                    setState(325);
                    match(T__22);
                    setState(326);
                    match(T__0);
                    setState(330);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == VAR_NAME) {
                        {
                            {
                                setState(327);
                                match(VAR_NAME);
                            }
                        }
                        setState(332);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(333);
                    match(T__3);
                    setState(334);
                    effect_body();
                    setState(335);
                    match(T__3);
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(337);
                    match(T__0);
                    setState(338);
                    match(T__23);
                    setState(339);
                    gd();
                    setState(340);
                    cond_effect();
                    setState(341);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class P_effectContext extends ParserRuleContext {
        public Atomic_formularContext atomic_formular() {
            return getRuleContext(Atomic_formularContext.class, 0);
        }

        public P_effectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_p_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterP_effect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitP_effect(this);
        }
    }

    public final P_effectContext p_effect() throws RecognitionException {
        P_effectContext _localctx = new P_effectContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_p_effect);
        try {
            setState(351);
            switch (getInterpreter().adaptivePredict(_input, 34, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(345);
                    match(T__0);
                    setState(346);
                    match(T__18);
                    setState(347);
                    atomic_formular();
                    setState(348);
                    match(T__3);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(350);
                    atomic_formular();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Cond_effectContext extends ParserRuleContext {
        public List<P_effectContext> p_effect() {
            return getRuleContexts(P_effectContext.class);
        }

        public P_effectContext p_effect(int i) {
            return getRuleContext(P_effectContext.class, i);
        }

        public Cond_effectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cond_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterCond_effect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitCond_effect(this);
        }
    }

    public final Cond_effectContext cond_effect() throws RecognitionException {
        Cond_effectContext _localctx = new Cond_effectContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_cond_effect);
        int _la;
        try {
            setState(363);
            switch (getInterpreter().adaptivePredict(_input, 36, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(353);
                    p_effect();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(354);
                    match(T__0);
                    setState(355);
                    match(T__16);
                    setState(357);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(356);
                                p_effect();
                            }
                        }
                        setState(359);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__0);
                    setState(361);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Atomic_formularContext extends ParserRuleContext {
        public PredicateContext predicate() {
            return getRuleContext(PredicateContext.class, 0);
        }

        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public Atomic_formularContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_atomic_formular;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterAtomic_formular(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitAtomic_formular(this);
        }
    }

    public final Atomic_formularContext atomic_formular() throws RecognitionException {
        Atomic_formularContext _localctx = new Atomic_formularContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_atomic_formular);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(365);
                match(T__0);
                setState(366);
                predicate();
                setState(370);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VAR_NAME) {
                    {
                        {
                            setState(367);
                            match(VAR_NAME);
                        }
                    }
                    setState(372);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(373);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class PredicateContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public PredicateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_predicate;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterPredicate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitPredicate(this);
        }
    }

    public final PredicateContext predicate() throws RecognitionException {
        PredicateContext _localctx = new PredicateContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_predicate);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(375);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Typed_var_listContext extends ParserRuleContext {
        public List<Typed_varsContext> typed_vars() {
            return getRuleContexts(Typed_varsContext.class);
        }

        public Typed_varsContext typed_vars(int i) {
            return getRuleContext(Typed_varsContext.class, i);
        }

        public Typed_var_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typed_var_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterTyped_var_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitTyped_var_list(this);
        }
    }

    public final Typed_var_listContext typed_var_list() throws RecognitionException {
        Typed_var_listContext _localctx = new Typed_var_listContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_typed_var_list);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(380);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VAR_NAME) {
                    {
                        {
                            setState(377);
                            typed_vars();
                        }
                    }
                    setState(382);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Typed_varsContext extends ParserRuleContext {
        public Var_typeContext var_type() {
            return getRuleContext(Var_typeContext.class, 0);
        }

        public List<TerminalNode> VAR_NAME() {
            return getTokens(hddlParser.VAR_NAME);
        }

        public TerminalNode VAR_NAME(int i) {
            return getToken(hddlParser.VAR_NAME, i);
        }

        public Typed_varsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typed_vars;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterTyped_vars(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitTyped_vars(this);
        }
    }

    public final Typed_varsContext typed_vars() throws RecognitionException {
        Typed_varsContext _localctx = new Typed_varsContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_typed_vars);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(384);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(383);
                            match(VAR_NAME);
                        }
                    }
                    setState(386);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == VAR_NAME);
                setState(388);
                match(T__6);
                setState(389);
                var_type();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class Var_typeContext extends ParserRuleContext {
        public TerminalNode NAME() {
            return getToken(hddlParser.NAME, 0);
        }

        public Var_typeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_var_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterVar_type(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitVar_type(this);
        }
    }

    public final Var_typeContext var_type() throws RecognitionException {
        Var_typeContext _localctx = new Var_typeContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_var_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(391);
                match(NAME);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ProblemContext extends ParserRuleContext {
        public ProblemContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_problem;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).enterProblem(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof hddlListener) ((hddlListener) listener).exitProblem(this);
        }
    }

    public final ProblemContext problem() throws RecognitionException {
        ProblemContext _localctx = new ProblemContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_problem);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(393);
                match(T__24);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\37\u018e\4\2\t\2" +
                    "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\3\2\3\2\5\2E\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3N\n\3\3\3\5\3Q\n\3" +
                    "\3\3\5\3T\n\3\3\3\7\3W\n\3\f\3\16\3Z\13\3\3\3\7\3]\n\3\f\3\16\3`\13\3" +
                    "\3\3\7\3c\n\3\f\3\16\3f\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\6" +
                    "\6r\n\6\r\6\16\6s\3\7\3\7\3\7\6\7y\n\7\r\7\16\7z\3\7\3\7\6\7\177\n\7\r" +
                    "\7\16\7\u0080\3\7\3\7\3\b\3\b\3\b\6\b\u0088\n\b\r\b\16\b\u0089\3\b\3\b" +
                    "\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u009c\n\n" +
                    "\3\n\3\n\5\n\u00a0\n\n\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3" +
                    "\f\3\f\3\f\3\f\7\f\u00b1\n\f\f\f\16\f\u00b4\13\f\3\f\3\f\3\f\5\f\u00b9" +
                    "\n\f\3\f\3\f\5\f\u00bd\n\f\3\f\3\f\5\f\u00c1\n\f\3\f\3\f\5\f\u00c5\n\f" +
                    "\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\6\16\u00cf\n\16\r\16\16\16\u00d0" +
                    "\3\16\3\16\5\16\u00d5\n\16\3\17\3\17\3\17\3\17\3\17\6\17\u00dc\n\17\r" +
                    "\17\16\17\u00dd\3\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\6\21\u00e9" +
                    "\n\21\r\21\16\21\u00ea\3\21\3\21\5\21\u00ef\n\21\3\22\3\22\3\22\3\22\3" +
                    "\22\3\22\3\23\3\23\3\23\3\23\6\23\u00fb\n\23\r\23\16\23\u00fc\3\23\3\23" +
                    "\5\23\u0101\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24" +
                    "\3\24\3\24\5\24\u0110\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25" +
                    "\5\25\u011b\n\25\3\25\3\25\5\25\u011f\n\25\3\25\3\25\3\26\3\26\3\27\3" +
                    "\27\3\27\3\27\3\27\3\27\6\27\u012b\n\27\r\27\16\27\u012c\3\27\3\27\3\27" +
                    "\3\27\3\27\3\27\3\27\5\27\u0136\n\27\3\30\3\30\3\30\3\30\3\30\3\30\6\30" +
                    "\u013e\n\30\r\30\16\30\u013f\3\30\3\30\5\30\u0144\n\30\3\31\3\31\3\31" +
                    "\3\31\3\31\7\31\u014b\n\31\f\31\16\31\u014e\13\31\3\31\3\31\3\31\3\31" +
                    "\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u015a\n\31\3\32\3\32\3\32\3\32\3\32" +
                    "\3\32\5\32\u0162\n\32\3\33\3\33\3\33\3\33\6\33\u0168\n\33\r\33\16\33\u0169" +
                    "\3\33\3\33\5\33\u016e\n\33\3\34\3\34\3\34\7\34\u0173\n\34\f\34\16\34\u0176" +
                    "\13\34\3\34\3\34\3\35\3\35\3\36\7\36\u017d\n\36\f\36\16\36\u0180\13\36" +
                    "\3\37\6\37\u0183\n\37\r\37\16\37\u0184\3\37\3\37\3\37\3 \3 \3!\3!\3!\2" +
                    "\2\"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@" +
                    "\2\3\4\2\23\23\30\30\u0199\2D\3\2\2\2\4F\3\2\2\2\6i\3\2\2\2\bk\3\2\2\2" +
                    "\nq\3\2\2\2\fu\3\2\2\2\16\u0084\3\2\2\2\20\u008d\3\2\2\2\22\u0092\3\2" +
                    "\2\2\24\u00a3\3\2\2\2\26\u00a5\3\2\2\2\30\u00c8\3\2\2\2\32\u00d4\3\2\2" +
                    "\2\34\u00d6\3\2\2\2\36\u00e2\3\2\2\2 \u00ee\3\2\2\2\"\u00f0\3\2\2\2$\u0100" +
                    "\3\2\2\2&\u010f\3\2\2\2(\u0111\3\2\2\2*\u0122\3\2\2\2,\u0135\3\2\2\2." +
                    "\u0143\3\2\2\2\60\u0159\3\2\2\2\62\u0161\3\2\2\2\64\u016d\3\2\2\2\66\u016f" +
                    "\3\2\2\28\u0179\3\2\2\2:\u017e\3\2\2\2<\u0182\3\2\2\2>\u0189\3\2\2\2@" +
                    "\u018b\3\2\2\2BE\5\4\3\2CE\5@!\2DB\3\2\2\2DC\3\2\2\2E\3\3\2\2\2FG\7\3" +
                    "\2\2GH\7\4\2\2HI\7\3\2\2IJ\7\5\2\2JK\5\6\4\2KM\7\6\2\2LN\5\b\5\2ML\3\2" +
                    "\2\2MN\3\2\2\2NP\3\2\2\2OQ\5\f\7\2PO\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RT\5\16" +
                    "\b\2SR\3\2\2\2ST\3\2\2\2TX\3\2\2\2UW\5\22\n\2VU\3\2\2\2WZ\3\2\2\2XV\3" +
                    "\2\2\2XY\3\2\2\2Y^\3\2\2\2ZX\3\2\2\2[]\5\26\f\2\\[\3\2\2\2]`\3\2\2\2^" +
                    "\\\3\2\2\2^_\3\2\2\2_d\3\2\2\2`^\3\2\2\2ac\5(\25\2ba\3\2\2\2cf\3\2\2\2" +
                    "db\3\2\2\2de\3\2\2\2eg\3\2\2\2fd\3\2\2\2gh\7\6\2\2h\5\3\2\2\2ij\7\36\2" +
                    "\2j\7\3\2\2\2kl\7\3\2\2lm\7\7\2\2mn\5\n\6\2no\7\6\2\2o\t\3\2\2\2pr\7\34" +
                    "\2\2qp\3\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2t\13\3\2\2\2uv\7\3\2\2v~\7" +
                    "\b\2\2wy\7\36\2\2xw\3\2\2\2yz\3\2\2\2zx\3\2\2\2z{\3\2\2\2{|\3\2\2\2|}" +
                    "\7\t\2\2}\177\5> \2~x\3\2\2\2\177\u0080\3\2\2\2\u0080~\3\2\2\2\u0080\u0081" +
                    "\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\7\6\2\2\u0083\r\3\2\2\2\u0084" +
                    "\u0085\7\3\2\2\u0085\u0087\7\n\2\2\u0086\u0088\5\20\t\2\u0087\u0086\3" +
                    "\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a" +
                    "\u008b\3\2\2\2\u008b\u008c\7\6\2\2\u008c\17\3\2\2\2\u008d\u008e\7\3\2" +
                    "\2\u008e\u008f\58\35\2\u008f\u0090\5:\36\2\u0090\u0091\7\6\2\2\u0091\21" +
                    "\3\2\2\2\u0092\u0093\7\3\2\2\u0093\u0094\7\13\2\2\u0094\u0095\5\24\13" +
                    "\2\u0095\u0096\7\f\2\2\u0096\u0097\7\3\2\2\u0097\u0098\5:\36\2\u0098\u009b" +
                    "\7\6\2\2\u0099\u009a\7\r\2\2\u009a\u009c\5,\27\2\u009b\u0099\3\2\2\2\u009b" +
                    "\u009c\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009e\7\16\2\2\u009e\u00a0\5" +
                    ".\30\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1" +
                    "\u00a2\7\6\2\2\u00a2\23\3\2\2\2\u00a3\u00a4\7\36\2\2\u00a4\25\3\2\2\2" +
                    "\u00a5\u00a6\7\3\2\2\u00a6\u00a7\7\17\2\2\u00a7\u00a8\5\30\r\2\u00a8\u00a9" +
                    "\7\f\2\2\u00a9\u00aa\7\3\2\2\u00aa\u00ab\5:\36\2\u00ab\u00ac\7\6\2\2\u00ac" +
                    "\u00ad\7\13\2\2\u00ad\u00ae\7\3\2\2\u00ae\u00b2\5\24\13\2\u00af\u00b1" +
                    "\7\35\2\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2" +
                    "\u00b2\u00b3\3\2\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b8" +
                    "\7\6\2\2\u00b6\u00b7\7\r\2\2\u00b7\u00b9\5,\27\2\u00b8\u00b6\3\2\2\2\u00b8" +
                    "\u00b9\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00bb\7\20\2\2\u00bb\u00bd\5" +
                    "\32\16\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be" +
                    "\u00bf\7\21\2\2\u00bf\u00c1\5 \21\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3" +
                    "\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c3\7\22\2\2\u00c3\u00c5\5$\23\2\u00c4" +
                    "\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\7\6" +
                    "\2\2\u00c7\27\3\2\2\2\u00c8\u00c9\7\36\2\2\u00c9\31\3\2\2\2\u00ca\u00d5" +
                    "\5\34\17\2\u00cb\u00cc\7\3\2\2\u00cc\u00ce\7\23\2\2\u00cd\u00cf\5\34\17" +
                    "\2\u00ce\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1" +
                    "\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\7\6\2\2\u00d3\u00d5\3\2\2\2\u00d4" +
                    "\u00ca\3\2\2\2\u00d4\u00cb\3\2\2\2\u00d5\33\3\2\2\2\u00d6\u00d7\7\3\2" +
                    "\2\u00d7\u00d8\5\36\20\2\u00d8\u00d9\7\3\2\2\u00d9\u00db\5\24\13\2\u00da" +
                    "\u00dc\7\35\2\2\u00db\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00db\3" +
                    "\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00e0\7\6\2\2\u00e0" +
                    "\u00e1\7\6\2\2\u00e1\35\3\2\2\2\u00e2\u00e3\7\36\2\2\u00e3\37\3\2\2\2" +
                    "\u00e4\u00ef\5\"\22\2\u00e5\u00e6\7\3\2\2\u00e6\u00e8\7\23\2\2\u00e7\u00e9" +
                    "\5\"\22\2\u00e8\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00e8\3\2\2\2" +
                    "\u00ea\u00eb\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed\7\6\2\2\u00ed\u00ef" +
                    "\3\2\2\2\u00ee\u00e4\3\2\2\2\u00ee\u00e5\3\2\2\2\u00ef!\3\2\2\2\u00f0" +
                    "\u00f1\7\3\2\2\u00f1\u00f2\5\36\20\2\u00f2\u00f3\7\24\2\2\u00f3\u00f4" +
                    "\5\36\20\2\u00f4\u00f5\7\6\2\2\u00f5#\3\2\2\2\u00f6\u0101\5&\24\2\u00f7" +
                    "\u00f8\7\3\2\2\u00f8\u00fa\7\23\2\2\u00f9\u00fb\5&\24\2\u00fa\u00f9\3" +
                    "\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd" +
                    "\u00fe\3\2\2\2\u00fe\u00ff\7\6\2\2\u00ff\u0101\3\2\2\2\u0100\u00f6\3\2" +
                    "\2\2\u0100\u00f7\3\2\2\2\u0101%\3\2\2\2\u0102\u0103\7\3\2\2\u0103\u0104" +
                    "\7\25\2\2\u0104\u0105\7\3\2\2\u0105\u0106\7\26\2\2\u0106\u0107\7\35\2" +
                    "\2\u0107\u0108\7\35\2\2\u0108\u0109\7\6\2\2\u0109\u0110\7\6\2\2\u010a" +
                    "\u010b\7\3\2\2\u010b\u010c\7\26\2\2\u010c\u010d\7\35\2\2\u010d\u010e\7" +
                    "\35\2\2\u010e\u0110\7\6\2\2\u010f\u0102\3\2\2\2\u010f\u010a\3\2\2\2\u0110" +
                    "\'\3\2\2\2\u0111\u0112\7\3\2\2\u0112\u0113\7\27\2\2\u0113\u0114\5*\26" +
                    "\2\u0114\u0115\7\f\2\2\u0115\u0116\7\3\2\2\u0116\u0117\5:\36\2\u0117\u011a" +
                    "\7\6\2\2\u0118\u0119\7\r\2\2\u0119\u011b\5,\27\2\u011a\u0118\3\2\2\2\u011a" +
                    "\u011b\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011d\7\16\2\2\u011d\u011f\5" +
                    ".\30\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120" +
                    "\u0121\7\6\2\2\u0121)\3\2\2\2\u0122\u0123\7\36\2\2\u0123+\3\2\2\2\u0124" +
                    "\u0125\7\3\2\2\u0125\u0136\7\6\2\2\u0126\u0136\5\66\34\2\u0127\u0128\7" +
                    "\3\2\2\u0128\u012a\t\2\2\2\u0129\u012b\5,\27\2\u012a\u0129\3\2\2\2\u012b" +
                    "\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012e\3\2" +
                    "\2\2\u012e\u012f\7\6\2\2\u012f\u0136\3\2\2\2\u0130\u0131\7\3\2\2\u0131" +
                    "\u0132\7\25\2\2\u0132\u0133\5,\27\2\u0133\u0134\7\6\2\2\u0134\u0136\3" +
                    "\2\2\2\u0135\u0124\3\2\2\2\u0135\u0126\3\2\2\2\u0135\u0127\3\2\2\2\u0135" +
                    "\u0130\3\2\2\2\u0136-\3\2\2\2\u0137\u0138\7\3\2\2\u0138\u0144\7\6\2\2" +
                    "\u0139\u0144\5\60\31\2\u013a\u013b\7\3\2\2\u013b\u013d\7\23\2\2\u013c" +
                    "\u013e\5\60\31\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u013d\3" +
                    "\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0142\7\6\2\2\u0142" +
                    "\u0144\3\2\2\2\u0143\u0137\3\2\2\2\u0143\u0139\3\2\2\2\u0143\u013a\3\2" +
                    "\2\2\u0144/\3\2\2\2\u0145\u015a\5\62\32\2\u0146\u0147\7\3\2\2\u0147\u0148" +
                    "\7\31\2\2\u0148\u014c\7\3\2\2\u0149\u014b\7\35\2\2\u014a\u0149\3\2\2\2" +
                    "\u014b\u014e\3\2\2\2\u014c\u014a\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014f" +
                    "\3\2\2\2\u014e\u014c\3\2\2\2\u014f\u0150\7\6\2\2\u0150\u0151\5.\30\2\u0151" +
                    "\u0152\7\6\2\2\u0152\u015a\3\2\2\2\u0153\u0154\7\3\2\2\u0154\u0155\7\32" +
                    "\2\2\u0155\u0156\5,\27\2\u0156\u0157\5\64\33\2\u0157\u0158\7\6\2\2\u0158" +
                    "\u015a\3\2\2\2\u0159\u0145\3\2\2\2\u0159\u0146\3\2\2\2\u0159\u0153\3\2" +
                    "\2\2\u015a\61\3\2\2\2\u015b\u015c\7\3\2\2\u015c\u015d\7\25\2\2\u015d\u015e" +
                    "\5\66\34\2\u015e\u015f\7\6\2\2\u015f\u0162\3\2\2\2\u0160\u0162\5\66\34" +
                    "\2\u0161\u015b\3\2\2\2\u0161\u0160\3\2\2\2\u0162\63\3\2\2\2\u0163\u016e" +
                    "\5\62\32\2\u0164\u0165\7\3\2\2\u0165\u0167\7\23\2\2\u0166\u0168\5\62\32" +
                    "\2\u0167\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u016a" +
                    "\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016c\7\6\2\2\u016c\u016e\3\2\2\2\u016d" +
                    "\u0163\3\2\2\2\u016d\u0164\3\2\2\2\u016e\65\3\2\2\2\u016f\u0170\7\3\2" +
                    "\2\u0170\u0174\58\35\2\u0171\u0173\7\35\2\2\u0172\u0171\3\2\2\2\u0173" +
                    "\u0176\3\2\2\2\u0174\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0177\3\2" +
                    "\2\2\u0176\u0174\3\2\2\2\u0177\u0178\7\6\2\2\u0178\67\3\2\2\2\u0179\u017a" +
                    "\7\36\2\2\u017a9\3\2\2\2\u017b\u017d\5<\37\2\u017c\u017b\3\2\2\2\u017d" +
                    "\u0180\3\2\2\2\u017e\u017c\3\2\2\2\u017e\u017f\3\2\2\2\u017f;\3\2\2\2" +
                    "\u0180\u017e\3\2\2\2\u0181\u0183\7\35\2\2\u0182\u0181\3\2\2\2\u0183\u0184" +
                    "\3\2\2\2\u0184\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0186\3\2\2\2\u0186" +
                    "\u0187\7\t\2\2\u0187\u0188\5> \2\u0188=\3\2\2\2\u0189\u018a\7\36\2\2\u018a" +
                    "?\3\2\2\2\u018b\u018c\7\33\2\2\u018cA\3\2\2\2*DMPSX^dsz\u0080\u0089\u009b" +
                    "\u009f\u00b2\u00b8\u00bc\u00c0\u00c4\u00d0\u00d4\u00dd\u00ea\u00ee\u00fc" +
                    "\u0100\u010f\u011a\u011e\u012c\u0135\u013f\u0143\u014c\u0159\u0161\u0169" +
                    "\u016d\u0174\u017e\u0184";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}