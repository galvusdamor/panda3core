package de.uniulm.ki.panda3.symbolic.parser.hddl;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.writer.hpddl.HPDDLWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import scala.Tuple2;

import java.io.*;

/**
 * Created by dhoeller on 14.04.15.
 */
public class TestHddlVisitorParser {
    public static void main(String[] strs) {
        FileReader inDomain = null;
        FileReader inProblem = null;
        try {
            //String domainFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/testdomain.pddl";
            //String problemFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/testproblem.pddl";
            //String domainFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/monroe-d.lisp";
            //String problemFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/monroe-p1.lisp";
            //String domainFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/domain.lisp";
            //String problemFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/problem2.lisp";
            String domainFileName = "/home/dhoeller/Schreibtisch/temp/test-parser/d.lisp";
            String problemFileName = "/home/dhoeller/Schreibtisch/temp/test-parser/p.lisp";

            inDomain = new FileReader(domainFileName);
            inProblem = new FileReader(problemFileName);

            antlrHDDLLexer lDomain = new antlrHDDLLexer(new ANTLRInputStream(inDomain));
            antlrHDDLLexer lProblem = new antlrHDDLLexer(new ANTLRInputStream(inProblem));

            antlrHDDLParser pDomain = new antlrHDDLParser(new CommonTokenStream(lDomain));
            antlrHDDLParser pProblem = new antlrHDDLParser(new CommonTokenStream(lProblem));

            Tuple2<Domain, Plan> tup = new hddlPanda3Visitor().visitInstance(pDomain.domain(), pProblem.problem());

/*
            HPDDLWriter writer = HPDDLWriter.apply("monroe", "monroe1");

            String tempOut = "/home/dhoeller/Schreibtisch/domain.lisp";
            String tempOut2 = "/home/dhoeller/Schreibtisch/problem.lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempOut));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(tempOut2));
            bw.write(writer.writeDomain(tup._1()));
            bw2.write(writer.writeProblem(tup._1(), tup._2()));
            bw.close();
            bw2.close();*/
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
