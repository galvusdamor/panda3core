package UUBenchmarksets.provableHard.problemGenerators.postCorrespondenceProblem;

import UUBenchmarksets.provableHard.problemGenerators.cfGrammarIntersection.CfGrammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dh on 22.02.17.
 */
public class PostCorrespondenceProblem {
    List<String> x = new ArrayList<>();
    List<String> y = new ArrayList<>();
    private CfGrammar grammar1;
    private CfGrammar grammar2;

    public PostCorrespondenceProblem(String path) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(path));
        while (br.ready()) {
            String l = br.readLine();
            String[] split = l.split("-");
            assert (split.length == 2);
            x.add(split[0].trim());
            y.add(split[1].trim());
        }
    }

    public void createGrammar() throws Exception {
        this.grammar1 = makeGrammar(x);
        this.grammar2 = makeGrammar(y);
    }

    public CfGrammar makeGrammar(List<String> seq) {
        Set<String> characters = new HashSet<>();

        List<String> terminal = new ArrayList<>();
        List<String> nonterminal = new ArrayList<>();
        List<String> rulesLeft = new ArrayList<>();
        List<List<String>> rulesRight = new ArrayList<>();
        String start = "S";

        nonterminal.add("S");
        for (int i = 0; i < seq.size(); i++) {
            terminal.add("t" + (i + 1));
            String someX = seq.get(i);
            List<String> right = new ArrayList<>();
            right.add("t" + (i + 1));
            right.add(start);
            String[] split = someX.split(" ");
            for (int j = split.length - 1; j >= 0; j--) {
                String s = split[j];
                characters.add(s);
                right.add(s);
            }
            rulesLeft.add(start);
            rulesRight.add(right);
        }
        for (int i = 0; i < seq.size(); i++) {
            terminal.add("t" + (i + 1));
            String someX = seq.get(i);
            List<String> right = new ArrayList<>();
            right.add("t" + (i + 1));
            // the start is missing here
            String[] split = someX.split(" ");
            for (int j = split.length - 1; j >= 0; j--) {
                String s = split[j];
                characters.add(s);
                right.add(s);
            }
            rulesLeft.add(start);
            rulesRight.add(right);
        }
        for (String c : characters) {
            terminal.add(c);
        }
        return new CfGrammar(terminal, nonterminal, rulesLeft, rulesRight, start);
    }

    public CfGrammar grammar1() {
        return grammar1;
    }

    public CfGrammar grammar2() {
        return grammar2;
    }
}
