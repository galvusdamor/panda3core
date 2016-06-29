package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{ClosedWorldAssumption, RemoveNegativePreconditions, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask
import de.uniulm.ki.panda3.symbolic.logic.Constant
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
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


    assert(planningGraph.graphSize == 4)
    assert(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "a"
    })
    assert(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "b"
    })
    assert(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "c"
    })
    assert(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "d"
    })
    assert(planningGraph.layerWithMutexes.last._2.size == 20)
    assert(planningGraph.layerWithMutexes.last._4.size == 4)
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

    assert(planningGraph.graphSize == 2) // TODO: check whether this is correct manually!
    assert(!(planningGraph.reachableGroundLiterals exists {
      _.predicate.name == "d"
    }))
    assert(planningGraph.layerWithMutexes.last._4 exists { case (gl1, gl2) => (gl1.predicate.name == "+a") && (gl2.predicate.name == "+c") })
    assert(planningGraph.layerWithMutexes.last._4 exists { case (gl1, gl2) => (gl1.predicate.name == "+c") && (gl2.predicate.name == "+e") })
    assert(planningGraph.layerWithMutexes.last._4 exists { case (gl1, gl2) => (gl1.predicate.name == "+c") && (gl2.predicate.name == "+b") })

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

    assert(planningGraph.reachableGroundPrimitiveActions exists { _.task.name == "v" })
  }

  it must "handle sorts without objects" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest04_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest04_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))

    assert(planningGraph.graphSize == 2)
  }

  it must "handle negative preconditions correctly" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest05_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest05_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))

    assert(planningGraph.graphSize == 1)
  }

  it must "not instantiate forbidden tasks" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val forbiddenLiftedTasks: Seq[ReducedTask] = (domain.tasks filter { case t: ReducedTask => t.name == "Y" }).asInstanceOf[Seq[ReducedTask]]
    val forbiddenTasks = Right(forbiddenLiftedTasks)
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, forbiddenTasks)

    assert(planningGraph.graphSize == 2)
    assert(!(planningGraph.layerWithMutexes.last._1 exists { groundTask => groundTask.task.name == "Y" }))

    val forbiddenGroundTasks: Seq[GroundTask] = Seq(GroundTask(forbiddenLiftedTasks.head, Seq.empty[Constant]))
    val forbiddenTasks2 = Left(forbiddenGroundTasks)
    val planningGraph2 = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, forbiddenTasks2)

    assert(!(planningGraph2.layerWithMutexes.last._1 contains forbiddenGroundTasks.head))
  }
  it must "not compute mutexes if required" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, false, false, Left(Nil))

    assert(planningGraph.reachableGroundLiterals exists { _.predicate.name == "a" })
    assert(planningGraph.reachableGroundLiterals exists { _.predicate.name == "b" })
    assert(planningGraph.reachableGroundLiterals exists { _.predicate.name == "c" })
    assert(planningGraph.reachableGroundLiterals exists { _.predicate.name == "d" })
    assert(planningGraph.layerWithMutexes.last._2.isEmpty)
    assert(planningGraph.layerWithMutexes.last._4.isEmpty)
  }

  it must "handle constants in actions correctly" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest06_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest06_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


    val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))

    assert(planningGraph.graphSize == 1)
    assert(planningGraph.reachableGroundPrimitiveActions.isEmpty)
  }
}