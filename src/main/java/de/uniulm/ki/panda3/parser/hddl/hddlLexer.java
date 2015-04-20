// Generated from /home/dhoeller/IdeaProjects/panda3core/src/main/java/de/uniulm/ki/panda3/parser/hddl/hddl.g4 by ANTLR 4.5
package de.uniulm.ki.panda3.parser.hddl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class hddlLexer extends Lexer {
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
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8",
            "T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16",
            "T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24",
            "REQUIRE_NAME", "VAR_NAME", "NAME", "WS"
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


    public hddlLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\37\u00fb\b\1\4\2" +
                    "\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4" +
                    "\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22" +
                    "\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31" +
                    "\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\6" +
                    "\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3" +
                    "\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n" +
                    "\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3" +
                    "\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3" +
                    "\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3" +
                    "\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3" +
                    "\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3" +
                    "\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3" +
                    "\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3" +
                    "\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\33\3\33\3" +
                    "\33\3\34\3\34\3\34\3\35\3\35\7\35\u00f0\n\35\f\35\16\35\u00f3\13\35\3" +
                    "\36\6\36\u00f6\n\36\r\36\16\36\u00f7\3\36\3\36\2\2\37\3\3\5\4\7\5\t\6" +
                    "\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24" +
                    "\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37\3\2\5\4\2C\\c|\7" +
                    "\2//\62;C\\aac|\5\2\13\f\17\17\"\"\u00fb\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3" +
                    "\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2" +
                    "\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35" +
                    "\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)" +
                    "\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2" +
                    "\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2\7" +
                    "F\3\2\2\2\tM\3\2\2\2\13O\3\2\2\2\r]\3\2\2\2\17d\3\2\2\2\21f\3\2\2\2\23" +
                    "r\3\2\2\2\25x\3\2\2\2\27\u0084\3\2\2\2\31\u0092\3\2\2\2\33\u009a\3\2\2" +
                    "\2\35\u00a2\3\2\2\2\37\u00ac\3\2\2\2!\u00b6\3\2\2\2#\u00c3\3\2\2\2%\u00c7" +
                    "\3\2\2\2\'\u00c9\3\2\2\2)\u00cd\3\2\2\2+\u00cf\3\2\2\2-\u00d7\3\2\2\2" +
                    "/\u00da\3\2\2\2\61\u00e1\3\2\2\2\63\u00e6\3\2\2\2\65\u00e7\3\2\2\2\67" +
                    "\u00ea\3\2\2\29\u00ed\3\2\2\2;\u00f5\3\2\2\2=>\7*\2\2>\4\3\2\2\2?@\7f" +
                    "\2\2@A\7g\2\2AB\7h\2\2BC\7k\2\2CD\7p\2\2DE\7g\2\2E\6\3\2\2\2FG\7f\2\2" +
                    "GH\7q\2\2HI\7o\2\2IJ\7c\2\2JK\7k\2\2KL\7p\2\2L\b\3\2\2\2MN\7+\2\2N\n\3" +
                    "\2\2\2OP\7<\2\2PQ\7t\2\2QR\7g\2\2RS\7s\2\2ST\7w\2\2TU\7k\2\2UV\7t\2\2" +
                    "VW\7g\2\2WX\7o\2\2XY\7g\2\2YZ\7p\2\2Z[\7v\2\2[\\\7u\2\2\\\f\3\2\2\2]^" +
                    "\7<\2\2^_\7v\2\2_`\7{\2\2`a\7r\2\2ab\7g\2\2bc\7u\2\2c\16\3\2\2\2de\7/" +
                    "\2\2e\20\3\2\2\2fg\7<\2\2gh\7r\2\2hi\7t\2\2ij\7g\2\2jk\7f\2\2kl\7k\2\2" +
                    "lm\7e\2\2mn\7c\2\2no\7v\2\2op\7g\2\2pq\7u\2\2q\22\3\2\2\2rs\7<\2\2st\7" +
                    "v\2\2tu\7c\2\2uv\7u\2\2vw\7m\2\2w\24\3\2\2\2xy\7<\2\2yz\7r\2\2z{\7c\2" +
                    "\2{|\7t\2\2|}\7c\2\2}~\7o\2\2~\177\7g\2\2\177\u0080\7v\2\2\u0080\u0081" +
                    "\7g\2\2\u0081\u0082\7t\2\2\u0082\u0083\7u\2\2\u0083\26\3\2\2\2\u0084\u0085" +
                    "\7<\2\2\u0085\u0086\7r\2\2\u0086\u0087\7t\2\2\u0087\u0088\7g\2\2\u0088" +
                    "\u0089\7e\2\2\u0089\u008a\7q\2\2\u008a\u008b\7p\2\2\u008b\u008c\7f\2\2" +
                    "\u008c\u008d\7k\2\2\u008d\u008e\7v\2\2\u008e\u008f\7k\2\2\u008f\u0090" +
                    "\7q\2\2\u0090\u0091\7p\2\2\u0091\30\3\2\2\2\u0092\u0093\7<\2\2\u0093\u0094" +
                    "\7g\2\2\u0094\u0095\7h\2\2\u0095\u0096\7h\2\2\u0096\u0097\7g\2\2\u0097" +
                    "\u0098\7e\2\2\u0098\u0099\7v\2\2\u0099\32\3\2\2\2\u009a\u009b\7<\2\2\u009b" +
                    "\u009c\7o\2\2\u009c\u009d\7g\2\2\u009d\u009e\7v\2\2\u009e\u009f\7j\2\2" +
                    "\u009f\u00a0\7q\2\2\u00a0\u00a1\7f\2\2\u00a1\34\3\2\2\2\u00a2\u00a3\7" +
                    "<\2\2\u00a3\u00a4\7u\2\2\u00a4\u00a5\7w\2\2\u00a5\u00a6\7d\2\2\u00a6\u00a7" +
                    "\7v\2\2\u00a7\u00a8\7c\2\2\u00a8\u00a9\7u\2\2\u00a9\u00aa\7m\2\2\u00aa" +
                    "\u00ab\7u\2\2\u00ab\36\3\2\2\2\u00ac\u00ad\7<\2\2\u00ad\u00ae\7q\2\2\u00ae" +
                    "\u00af\7t\2\2\u00af\u00b0\7f\2\2\u00b0\u00b1\7g\2\2\u00b1\u00b2\7t\2\2" +
                    "\u00b2\u00b3\7k\2\2\u00b3\u00b4\7p\2\2\u00b4\u00b5\7i\2\2\u00b5 \3\2\2" +
                    "\2\u00b6\u00b7\7<\2\2\u00b7\u00b8\7e\2\2\u00b8\u00b9\7q\2\2\u00b9\u00ba" +
                    "\7p\2\2\u00ba\u00bb\7u\2\2\u00bb\u00bc\7v\2\2\u00bc\u00bd\7t\2\2\u00bd" +
                    "\u00be\7c\2\2\u00be\u00bf\7k\2\2\u00bf\u00c0\7p\2\2\u00c0\u00c1\7v\2\2" +
                    "\u00c1\u00c2\7u\2\2\u00c2\"\3\2\2\2\u00c3\u00c4\7c\2\2\u00c4\u00c5\7p" +
                    "\2\2\u00c5\u00c6\7f\2\2\u00c6$\3\2\2\2\u00c7\u00c8\7>\2\2\u00c8&\3\2\2" +
                    "\2\u00c9\u00ca\7p\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7v\2\2\u00cc(\3\2" +
                    "\2\2\u00cd\u00ce\7?\2\2\u00ce*\3\2\2\2\u00cf\u00d0\7<\2\2\u00d0\u00d1" +
                    "\7c\2\2\u00d1\u00d2\7e\2\2\u00d2\u00d3\7v\2\2\u00d3\u00d4\7k\2\2\u00d4" +
                    "\u00d5\7q\2\2\u00d5\u00d6\7p\2\2\u00d6,\3\2\2\2\u00d7\u00d8\7q\2\2\u00d8" +
                    "\u00d9\7t\2\2\u00d9.\3\2\2\2\u00da\u00db\7h\2\2\u00db\u00dc\7q\2\2\u00dc" +
                    "\u00dd\7t\2\2\u00dd\u00de\7c\2\2\u00de\u00df\7n\2\2\u00df\u00e0\7n\2\2" +
                    "\u00e0\60\3\2\2\2\u00e1\u00e2\7y\2\2\u00e2\u00e3\7j\2\2\u00e3\u00e4\7" +
                    "g\2\2\u00e4\u00e5\7p\2\2\u00e5\62\3\2\2\2\u00e7\u00e8\7<\2\2\u00e8\u00e9" +
                    "\59\35\2\u00e9\66\3\2\2\2\u00ea\u00eb\7A\2\2\u00eb\u00ec\59\35\2\u00ec" +
                    "8\3\2\2\2\u00ed\u00f1\t\2\2\2\u00ee\u00f0\t\3\2\2\u00ef\u00ee\3\2\2\2" +
                    "\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2:\3" +
                    "\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f6\t\4\2\2\u00f5\u00f4\3\2\2\2\u00f6" +
                    "\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9\3\2" +
                    "\2\2\u00f9\u00fa\b\36\2\2\u00fa<\3\2\2\2\5\2\u00f1\u00f7\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}