package de.uniulm.ki.util.grammarIntersection;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

/**
 * Created by dh on 22.02.17.
 */
public class PostscheKorrespondenz {
    public static void main(String[] args) throws Exception {
        String path = "/home/dh/Schreibtisch/pkp/";
        String name = "pkp5";
        String g1_path = path + name + ".txt";

        PKP pkp = new PKP(g1_path);
        pkp.createGrammar();
        Grammar g1 = pkp.grammar1();
        Grammar g2 = pkp.grammar2();

        Tuple2<Domain, Plan> prob = GrammarIntersection.grammerInterProb(g1, g2);
        FileHandler.writeHDDLToFiles(prob, path + name + "-dom.lisp", path + name + "-prob.lisp");
    }
}
