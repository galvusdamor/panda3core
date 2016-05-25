package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{RemoveNegativePreconditions, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphTest extends FlatSpec {

  "The grounded planning graph" must "be computable" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


    val groundedInitialState = initialPlan.groundedInitialState filter {
      _.isPositive
    }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))


    assert(planningGraph.graphSize == 3)
    assert(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "d"
    })
  }

  it must "recognise impossible situations" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialState filter {
      _.isPositive
    }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))


    assert(planningGraph.graphSize == 3) // TODO: check whether this is correct manually!
    assert(!(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "d"
    }))
  }

  it must "instantiate actions with parameters not contained in preconditions" in {

    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest03_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest03_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialState filter {
      _.isPositive
    }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))

    assert(planningGraph.reachableGroundPrimitiveActions exists { _.task.name == "v"})
  }

}