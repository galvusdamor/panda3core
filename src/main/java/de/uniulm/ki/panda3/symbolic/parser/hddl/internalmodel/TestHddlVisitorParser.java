package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlLexer;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlParser;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.writer.hpddl.HPDDLWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

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
            String domainFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/domain.lisp";
            String problemFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/problem.lisp";

            inDomain = new FileReader(domainFileName);
            inProblem = new FileReader(problemFileName);

            hddlLexer lDomain = new hddlLexer(new ANTLRInputStream(inDomain));
            hddlLexer lProblem = new hddlLexer(new ANTLRInputStream(inProblem));

            hddlParser pDomain = new hddlParser(new CommonTokenStream(lDomain));
            hddlParser pProblem = new hddlParser(new CommonTokenStream(lProblem));

            Tuple2<Domain, Plan> tup = new hddlPanda3Visitor().visitInstance(pDomain.domain(), pProblem.problem());


            HPDDLWriter writer = HPDDLWriter.apply("monroe", "monroe1");

            String tempOut = "/home/dhoeller/Schreibtisch/PANDAout.lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempOut));
            bw.write(writer.writeDomain(tup._1()));
            bw.write("\n\n\n---------------------------------------------------------------------\n\n\n");
            bw.write(writer.writeProblem(tup._1(), tup._2()));
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
