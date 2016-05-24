package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphTest extends FlatSpec {

  "The grounded planning graph" must "be computable" in {
    val domainFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    val problemFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))


    assert(planningGraph.graphSize == 3)
    assert(planningGraph.reachableGroundLiterals exists {_.predicate.name == "d"})
  }

}
