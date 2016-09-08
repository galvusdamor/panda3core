package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class NaiveVSTopDownTDG extends FlatSpec {

  "Both TDGs" must "yield the same result on the Smartphone domain" in {
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"

    val parsedDomainAndProblem = XMLParser.asParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    val sortExpanded = ExpandSortHierarchy.transform(parsedDomainAndProblem, ())
    val cwaApplied = ClosedWorldAssumption.transform(sortExpanded, true)
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())
    val (domain, problem) = RemoveNegativePreconditions(flattened, ())

    val initialState = problem.groundedInitialStateOnlyPositive
    val planningGraph = GroundedPlanningGraph(domain, initialState.toSet, GroundedPlanningGraphConfiguration())

    val naiveTDG = NaiveGroundedTaskDecompositionGraph(domain, problem, planningGraph, prunePrimitive = true)
    val topDownTDG = TopDownTaskDecompositionGraph(domain, problem, planningGraph, prunePrimitive = true)

    naiveTDG.taskDecompositionGraph
    topDownTDG.taskDecompositionGraph

    assert(naiveTDG.reachableGroundedTasks.size == topDownTDG.reachableGroundedTasks.size)
    assert(naiveTDG.reachableGroundAbstractActions.size == topDownTDG.reachableGroundAbstractActions.size)
    assert(naiveTDG.reachableGroundPrimitiveActions.size == topDownTDG.reachableGroundPrimitiveActions.size)
    assert(naiveTDG.reachableGroundMethods.size == topDownTDG.reachableGroundMethods.size)
    assert(naiveTDG.reachableGroundLiterals.size == topDownTDG.reachableGroundLiterals.size)

    assert(naiveTDG.reachableGroundedTasks.toSet == topDownTDG.reachableGroundedTasks.toSet)
    assert(naiveTDG.reachableGroundAbstractActions.toSet == topDownTDG.reachableGroundAbstractActions.toSet)
    assert(naiveTDG.reachableGroundPrimitiveActions == topDownTDG.reachableGroundPrimitiveActions)
    assert(naiveTDG.reachableGroundMethods.toSet === topDownTDG.reachableGroundMethods.toSet)
    assert(naiveTDG.reachableGroundLiterals.toSet === topDownTDG.reachableGroundLiterals.toSet)
  }
}
