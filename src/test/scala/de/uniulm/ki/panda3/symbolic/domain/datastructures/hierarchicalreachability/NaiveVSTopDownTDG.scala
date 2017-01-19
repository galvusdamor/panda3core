package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.parser.FileTypeDetector
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class NaiveVSTopDownTDG extends FlatSpec {


  val testInstanes = ("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml",
    "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml", "Smartphone VerySmall") ::
    ("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml",
      "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_Small.xml", "Smartphone Small") ::
    ("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp",
      "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p00-htn.lisp", "HTN Transport 00") :: Nil

  testInstanes foreach { case (domFile, probFile, name) =>
    "Both TDGs" must "yield the same result on the " + name + " domain" in {

      val parsedDomainAndProblem = FileTypeDetector({ _ => () }).parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
      val sortExpanded = ExpandSortHierarchy.transform(parsedDomainAndProblem, ())
      val cwaApplied = ClosedWorldAssumption.transform(sortExpanded, true)
      val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
      val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())
      val (domain, problem) = RemoveNegativePreconditions(flattened, ())

      val initialState = problem.groundedInitialStateOnlyPositive
      val planningGraph = GroundedPlanningGraph(domain, initialState.toSet, GroundedPlanningGraphConfiguration())

      val naiveTDG = NaiveGroundedTaskDecompositionGraph(domain, problem, planningGraph, prunePrimitive = true)
      val topDownTDG = TopDownTaskDecompositionGraph(domain, problem, planningGraph, prunePrimitive = true)
      val twoWayTDG = TwoStepDecompositionGraph(domain, problem, planningGraph, prunePrimitive = true)

      naiveTDG.taskDecompositionGraph
      topDownTDG.taskDecompositionGraph
      twoWayTDG.taskDecompositionGraph

      naiveTDG.reachableGroundMethods
      twoWayTDG.reachableGroundMethods

      assert(naiveTDG.reachableGroundedTasks.size == topDownTDG.reachableGroundedTasks.size)
      assert(naiveTDG.reachableGroundAbstractActions.size == topDownTDG.reachableGroundAbstractActions.size)
      assert(naiveTDG.reachableGroundPrimitiveActions.size == topDownTDG.reachableGroundPrimitiveActions.size)
      assert(naiveTDG.reachableGroundMethods.size == topDownTDG.reachableGroundMethods.size)
      assert(naiveTDG.reachableGroundLiterals.size == topDownTDG.reachableGroundLiterals.size)

      assert(naiveTDG.reachableGroundedTasks.toSet == topDownTDG.reachableGroundedTasks.toSet, "Lifted Tasks not equal")
      assert(naiveTDG.reachableGroundAbstractActions.toSet == topDownTDG.reachableGroundAbstractActions.toSet, "Lifted Abstract Tasks not equal")
      assert(naiveTDG.reachableGroundPrimitiveActions == topDownTDG.reachableGroundPrimitiveActions, "Lifted Primitive Tasks not equal")
      assert(naiveTDG.reachableGroundMethods.toSet === topDownTDG.reachableGroundMethods.toSet, "Lifted Methods not equal")
      assert(naiveTDG.reachableGroundLiterals.toSet === topDownTDG.reachableGroundLiterals.toSet, "Lifted Literals not equal")


      assert(naiveTDG.reachableGroundedTasks.size == twoWayTDG.reachableGroundedTasks.size)
      assert(naiveTDG.reachableGroundAbstractActions.size == twoWayTDG.reachableGroundAbstractActions.size)
      assert(naiveTDG.reachableGroundPrimitiveActions.size == twoWayTDG.reachableGroundPrimitiveActions.size)
      assert(naiveTDG.reachableGroundMethods.size == twoWayTDG.reachableGroundMethods.size)
      assert(naiveTDG.reachableGroundLiterals.size == twoWayTDG.reachableGroundLiterals.size)

      assert(naiveTDG.reachableGroundedTasks.toSet == twoWayTDG.reachableGroundedTasks.toSet, "Ground Tasks not equal")
      assert(naiveTDG.reachableGroundAbstractActions.toSet == twoWayTDG.reachableGroundAbstractActions.toSet, "Ground Abstract Tasks not equal")
      assert(naiveTDG.reachableGroundPrimitiveActions == twoWayTDG.reachableGroundPrimitiveActions, "Ground Primitive Tasks not equal")
      val x = naiveTDG.reachableGroundMethods filterNot twoWayTDG.reachableGroundMethods.toSet
      val y = twoWayTDG.reachableGroundMethods filterNot naiveTDG.reachableGroundMethods.toSet
      assert(naiveTDG.reachableGroundMethods.toSet === twoWayTDG.reachableGroundMethods.toSet, "Ground Methods not equal")
      assert(naiveTDG.reachableGroundLiterals.toSet === twoWayTDG.reachableGroundLiterals.toSet, "Ground Literals not equal")
    }
  }
}
