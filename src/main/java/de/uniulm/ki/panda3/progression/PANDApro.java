package de.uniulm.ki.panda3.progression;

import de.uniulm.ki.panda3.progression.proSearch.planningInstance;
import de.uniulm.ki.panda3.symbolic.compiler.ClosedWorldAssumption;
import de.uniulm.ki.panda3.symbolic.compiler.ExpandSortHierarchy;
import de.uniulm.ki.panda3.symbolic.compiler.RemoveNegativePreconditions;
import de.uniulm.ki.panda3.symbolic.compiler.ToPlainFormulaRepresentation;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

/**
 * Created by dhoeller on 28.06.16.
 * <p/>
 * - Bug: Result changes when erasing reconditions
 * - Feature (nice-to-have): One should implement enforced-hill-climbing
 * - Feature (nice-to-have): One should implement helpful actions
 * - Feature (necessary): One need to implement different strategies to include methods in action layers:
 * -- Implement a first version that simulates Ron's adl-representation one-to-one as a base for
 * further relaxations
 */
public class PANDApro {
    public static void main(String[] str) throws Exception {
        //String domainFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-domain-2.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-problem-2.lisp";
        //String domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl";
        //String problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl";
        //String domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p25-domain.pddl";
        //String problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p25.pddl";

        // STRIPS
        // String domainFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/lifted-htn/domain-strips.lisp";
        // String problemFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/lifted-htn/p01-strips.lisp";

        // HTN
        String domainFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/domain-htn.lisp";
        String problemFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/p01-htn.lisp";

        Tuple2<Domain, Plan> instance = FileHandler.loadHDDLFromFile(domainFile, problemFile);

        instance = ExpandSortHierarchy.transform(instance._1(), instance._2());
        instance = ClosedWorldAssumption.transform(instance._1(), instance._2());
        instance = ToPlainFormulaRepresentation.transform(instance._1(), instance._2());
        instance = RemoveNegativePreconditions.transform(instance._1(), instance._2());

        planningInstance pi = new planningInstance();
        pi.plan(instance._1(), instance._2());

    }
}
