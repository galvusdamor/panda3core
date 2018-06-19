// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
            String domainFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problem-from-ridder-paper/domain.lisp";
            String problemFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problem-from-ridder-paper/prob-missing-road.lisp";
            //String domainFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/monroe-d.lisp";
            //String problemFileName = System.getProperty("user.dir") + "/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/monroe-p1.lisp";
            //String domainFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/domain.lisp";
            //String problemFileName = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/hddl-finalize/problem2.lisp";
            //String domainFileName = "/home/dh/Schreibtisch/test-ron/domain-block.hpddl";
            //String problemFileName = "/home/dh/Schreibtisch/test-ron/pfile_005.pddl";

            inDomain = new FileReader(domainFileName);
            inProblem = new FileReader(problemFileName);

            antlrHDDLLexer lDomain = new antlrHDDLLexer(new ANTLRInputStream(inDomain));
            antlrHDDLLexer lProblem = new antlrHDDLLexer(new ANTLRInputStream(inProblem));

            antlrHDDLParser pDomain = new antlrHDDLParser(new CommonTokenStream(lDomain));
            antlrHDDLParser pProblem = new antlrHDDLParser(new CommonTokenStream(lProblem));

            pDomain.removeErrorListeners();
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
