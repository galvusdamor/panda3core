package de.uniulm.ki.panda3.progression;

import de.uniulm.ki.panda3.progression.bottomUpGrounder.htnBottomUpGrounder;
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.IRPG;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.symbolicRPG;
import de.uniulm.ki.panda3.progression.strips.planningInstance;
import de.uniulm.ki.panda3.symbolic.compiler.*;
import de.uniulm.ki.panda3.symbolic.compiler.prefix.forallAndExistsPrecCompiler;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

import java.io.IOException;

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
        //String domainFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp";
        //String problemFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p00-htn.lisp";

        String domainFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/v2/domain-htn-ad.lisp";
        String problemFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/v2/p00-htn-ad.lisp";

        //String domainFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-domain-2.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-problem-2.lisp";
        //-String domainFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/hddl-finalize/domain.lisp";

        //String domainFile = "/home/dh/Schreibtisch/test-domain/d-0002-plow-road-7.hddl";
        //String problemFile = "/home/dh/Schreibtisch/test-domain/p-0002-plow-road-7.hddl";


        //-String basedir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/problems/";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/aaai/problems/p-0001-clear-road-wreck.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/deletedObjs-1-to-100/problems/p-0002-plow-road.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/deletedObjs-1-to-100/problems/p-0003-set-up-shelter.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/deletedObjs-1-to-100/problems/p-0004-provide-medical-attention.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/deletedObjs-1-to-100/problems/p-0004-provide-medical-attention.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/deletedObjs-1-to-100/problems/p-0005-clear-road-wreck.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems-exact-obj/only/problems/p-0002-plow-road.lisp";
        //String problemFile = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems-exact-obj/only/problems/p-0003-set-up-shelter.lisp";
        //-String problemFile = basedir+"location/deletedObjs-1-to-100/problems/p-0002-plow-road.lisp";

        //String domainFile = "/home/dhoeller/Schreibtisch/englert-test/testDomain1.pddl";
        //String problemFile ="/home/dhoeller/Schreibtisch/englert-test/testProblem1.pddl";


        Tuple2<Domain, Plan> instance = FileHandler.loadHDDLFromFile(domainFile, problemFile);

        instance = ExpandSortHierarchy.transform(instance._1(), instance._2());
        instance = ClosedWorldAssumption.transform(instance._1(), instance._2());
        instance = ToPlainFormulaRepresentation.transform(instance._1(), instance._2());
        instance = RemoveNegativePreconditions.transform(instance._1(), instance._2());
        //instance = (new forallAndExistsPrecCompiler()).transform(instance, null);
        instance = SHOPMethodCompiler.transform(instance);
        instance = ToPlainFormulaRepresentation.transform(instance._1(), instance._2());

        boolean isHtn = instance._1().abstractTasks().size() > 0;

        if (isHtn) {
            htnPlanningInstance pi = new htnPlanningInstance();
            htnBottomUpGrounder gr = null;
            boolean converged = false;
            IRPG rpg = null;
            try {
                System.out.println("<PRESS KEY>");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!converged) {
                System.out.println("Building relaxed planning graph");
                long time2 = System.currentTimeMillis();
                rpg = new symbolicRPG();
                //rpg = new hierarchyAwareRPG();
                if (gr == null)
                    rpg.build(instance._1(), instance._2());
                else
                    rpg.build(instance._1(), instance._2(), gr.groundingsByTask);
                System.out.println(" (" + (System.currentTimeMillis() - time2) + " ms).");

                System.out.println(" - Graph contains " + rpg.getApplicableActions().size() + " ground actions.");
                System.out.println(" - Graph contains " + rpg.numOfReachableFacts() + " environment facts.");

                gr = new htnBottomUpGrounder(instance._1(), instance._2(), rpg.getApplicableActions());
                converged = !gr.deletedActions;
                if (gr.deletedActions) {
                    System.out.println("Restart grounding ...");
                }
            }

            //pi.plan(instance._2(),gr.methodsByTask,rpg.getApplicableActions(),rpg.getReachableFacts());
        } else {
            planningInstance pi = new planningInstance();
            //pi.plan(instance._1(), instance._2());
        }
    }
}
