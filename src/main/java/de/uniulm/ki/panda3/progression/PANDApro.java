package de.uniulm.ki.panda3.progression;

import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance;
import de.uniulm.ki.panda3.progression.strips.planningInstance;
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
 * - Feature (nice-to-have): One should implement enforced-hill-climbing
 * - Feature (nice-to-have): One should implement helpful actions
 * - Feature (necessary): One need to implement different strategies to include methods in action layers:
 * -- Implement a first version that simulates Ron's adl-representation one-to-one as a base for further relaxations
 */
public class PANDApro {
    public static void main(String[] str) throws Exception {
        System.out.println("This is PANDApro - An hierarchical planning system that plans via heuristic progression search.");
        //String domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl";
        //String problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl";
        //String domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p20-domain.pddl";
        //String problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p20.pddl";

        // STRIPS
        //String domainFile = "/home/dhoeller/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/domain/domain.pddl";
        //String problemFile = "/home/dhoeller/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/problems/p01.pddl";

        // HTN
        String domainFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp";
        String problemFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p00-htn.lisp";
        //String domainFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-domain-2.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-problem-2.lisp";

        //String domainFile = "/home/dhoeller/Schreibtisch/englert-test/testDomain1.pddl";
        //String problemFile ="/home/dhoeller/Schreibtisch/englert-test/testProblem1.pddl";


        Tuple2<Domain, Plan> instance = FileHandler.loadHDDLFromFile(domainFile, problemFile);

        instance = ExpandSortHierarchy.transform(instance._1(), instance._2());
        instance = ClosedWorldAssumption.transform(instance._1(), instance._2());
        instance = ToPlainFormulaRepresentation.transform(instance._1(), instance._2());
        instance = RemoveNegativePreconditions.transform(instance._1(), instance._2());

        boolean isHtn = instance._1().abstractTasks().size() > 0;

        if (isHtn) {
            htnPlanningInstance pi = new htnPlanningInstance();
            pi.plan(instance._1(), instance._2());
        } else {
            planningInstance pi = new planningInstance();
            pi.plan(instance._1(), instance._2());
        }
    }
}
