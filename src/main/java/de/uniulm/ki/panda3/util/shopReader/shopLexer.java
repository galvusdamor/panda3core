// Generated from /home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/util/shop/shop.g4 by ANTLR 4.5.3
package de.uniulm.ki.panda3.util.shopReader;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class shopLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, VAR_NAME=11, NAME=12, COMMENT=13, WS=14, NUMBER=15;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "VAR_NAME", "NAME", "COMMENT", "WS", "NUMBER"
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


	public shopLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "shop.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\21\u008d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3"+
		"\f\3\r\3\r\7\rn\n\r\f\r\16\rq\13\r\3\16\3\16\7\16u\n\16\f\16\16\16x\13"+
		"\16\3\16\3\16\5\16|\n\16\3\16\3\16\3\17\6\17\u0081\n\17\r\17\16\17\u0082"+
		"\3\17\3\17\3\20\3\20\7\20\u0089\n\20\f\20\16\20\u008c\13\20\2\2\21\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"\3\2\7\4\2C\\c|\7\2//\62;C\\aac|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\3\2"+
		"\62;\u0091\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3!\3\2\2"+
		"\2\5,\3\2\2\2\7.\3\2\2\2\t\60\3\2\2\2\139\3\2\2\2\r?\3\2\2\2\17J\3\2\2"+
		"\2\21N\3\2\2\2\23Z\3\2\2\2\25f\3\2\2\2\27h\3\2\2\2\31k\3\2\2\2\33r\3\2"+
		"\2\2\35\u0080\3\2\2\2\37\u0086\3\2\2\2!\"\7*\2\2\"#\7f\2\2#$\7g\2\2$%"+
		"\7h\2\2%&\7f\2\2&\'\7q\2\2\'(\7o\2\2()\7c\2\2)*\7k\2\2*+\7p\2\2+\4\3\2"+
		"\2\2,-\7*\2\2-\6\3\2\2\2./\7+\2\2/\b\3\2\2\2\60\61\7*\2\2\61\62\7<\2\2"+
		"\62\63\7o\2\2\63\64\7g\2\2\64\65\7v\2\2\65\66\7j\2\2\66\67\7q\2\2\678"+
		"\7f\2\28\n\3\2\2\29:\7<\2\2:;\7v\2\2;<\7c\2\2<=\7u\2\2=>\7m\2\2>\f\3\2"+
		"\2\2?@\7*\2\2@A\7<\2\2AB\7q\2\2BC\7r\2\2CD\7g\2\2DE\7t\2\2EF\7c\2\2FG"+
		"\7v\2\2GH\7q\2\2HI\7t\2\2I\16\3\2\2\2JK\7p\2\2KL\7q\2\2LM\7v\2\2M\20\3"+
		"\2\2\2NO\7*\2\2OP\7f\2\2PQ\7g\2\2QR\7h\2\2RS\7r\2\2ST\7t\2\2TU\7q\2\2"+
		"UV\7d\2\2VW\7n\2\2WX\7g\2\2XY\7o\2\2Y\22\3\2\2\2Z[\7*\2\2[\\\7<\2\2\\"+
		"]\7w\2\2]^\7p\2\2^_\7q\2\2_`\7t\2\2`a\7f\2\2ab\7g\2\2bc\7t\2\2cd\7g\2"+
		"\2de\7f\2\2e\24\3\2\2\2fg\7#\2\2g\26\3\2\2\2hi\7A\2\2ij\5\31\r\2j\30\3"+
		"\2\2\2ko\t\2\2\2ln\t\3\2\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2p\32"+
		"\3\2\2\2qo\3\2\2\2rv\7=\2\2su\n\4\2\2ts\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw"+
		"\3\2\2\2wy\3\2\2\2xv\3\2\2\2y{\t\4\2\2z|\t\4\2\2{z\3\2\2\2{|\3\2\2\2|"+
		"}\3\2\2\2}~\b\16\2\2~\34\3\2\2\2\177\u0081\t\5\2\2\u0080\177\3\2\2\2\u0081"+
		"\u0082\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2"+
		"\2\2\u0084\u0085\b\17\2\2\u0085\36\3\2\2\2\u0086\u008a\t\6\2\2\u0087\u0089"+
		"\t\6\2\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008a"+
		"\u008b\3\2\2\2\u008b \3\2\2\2\u008c\u008a\3\2\2\2\b\2ov{\u0082\u008a\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}