package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{ClosedWorldAssumption, RemoveNegativePreconditions, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.logic.Constant
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphTest extends FlatSpec {

  false :: true :: Nil foreach { useBuckets =>
    ("The grounded planning graph" + (if (useBuckets) " with buckets")) must "be computable" in {
      println("===============")
      println("TEST 1")
      println("===============")
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


      val groundedInitialState = initialPlan.groundedInitialState filter {
        _.isPositive
      }
      val planningGraph = GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      planningGraph.graphSize

      assert(planningGraph.graphSize == 5)
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
      //TODO: compute the sizes by hand
      //assert(planningGraph.layerWithMutexes.last._2.size == 20)
      //assert(planningGraph.layerWithMutexes.last._4.size == 4)
    }

    it must "recognise impossible situations" in {
      println("===============")
      println("TEST 2")
      println("===============")
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


      val groundedInitialState = initialPlan.groundedInitialState filter {
        _.isPositive
      }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      assert(planningGraph.graphSize == 3) // TODO: check whether this is correct manually!
      assert(!(planningGraph.reachableGroundLiterals exists {
        _.predicate.name == "d"
      }))
      println("mutexes: ")
      planningGraph.layerWithMutexes.last._4 foreach { case (gl1, gl2) => println(gl1.predicate.name + ", " + gl2.predicate.name) }

    }

    it must "instantiate actions with parameters not contained in preconditions" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest03_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest03_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


      val groundedInitialState = initialPlan.groundedInitialState filter {
        _.isPositive
      }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      assert(planningGraph.reachableGroundPrimitiveActions exists { _.task.name == "v" })
    }

    it must "handle sorts without objects" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest04_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest04_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


      val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      assert(planningGraph.graphSize == 3)
    }

    it must "handle negative preconditions correctly" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest05_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest05_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


      val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      assert(planningGraph.graphSize == 2)
    }

    it must "not instantiate forbidden tasks" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


      val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
      val forbiddenLiftedTasks: Seq[ReducedTask] = (domain.tasks filter { case t: ReducedTask => t.name == "Y" }).asInstanceOf[Seq[ReducedTask]]
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets,
                                                                                                                           forbiddenLiftedTasks = forbiddenLiftedTasks toSet))

      assert(planningGraph.graphSize == 3)
      assert(!(planningGraph.layerWithMutexes.last._1 exists { groundTask => groundTask.task.name == "Y" }))

      val forbiddenGroundTasks: Seq[GroundTask] = Seq(GroundTask(forbiddenLiftedTasks.head, Seq.empty[Constant]))
      val planningGraph2 = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets,
                                                                                                                            forbiddenGroundedTasks = forbiddenGroundTasks toSet))

      assert(!(planningGraph2.layerWithMutexes.last._1 contains forbiddenGroundTasks.head))
    }

    it must "not compute mutexes if required" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


      val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets, computeMutexes = false))

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
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, true)
      val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())


      val groundedInitialState = initialPlan.groundedInitialState filter { _.isPositive }
      val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))

      assert(planningGraph.graphSize == 2)
      assert(planningGraph.reachableGroundPrimitiveActions.isEmpty)
    }

    it must "produce the same result as the grounded forward search when running without mutexes" in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p03-domain.pddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p03.pddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, info = true)
      val plain = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val (domain, problem) = RemoveNegativePreconditions.transform(plain, ())


      val groundedInitialState = (problem.groundedInitialState filter { _.isPositive }) toSet
      val forwardSearch = GroundedForwardSearchReachabilityAnalysis(domain, groundedInitialState)()
      val planningGraph = GroundedPlanningGraph(domain, groundedInitialState, GroundedPlanningGraphConfiguration(buckets = useBuckets, computeMutexes = false))

      // check whether there are no mutexes
      planningGraph.layerWithMutexes foreach { case (_, a, _, b) =>
        assert(a.isEmpty) // no action mutexes
        assert(b.isEmpty) // no state mutexes
      }


      val forwardReacheableActions = forwardSearch.reachableGroundPrimitiveActions.size
      val planningGraphReacheableActions = planningGraph.reachableGroundPrimitiveActions.size

      val forwardButNotPG = forwardSearch.reachableGroundPrimitiveActions filterNot planningGraph.reachableGroundPrimitiveActions.contains
      val pgButNotForward = planningGraph.reachableGroundPrimitiveActions filterNot forwardSearch.reachableGroundPrimitiveActions.contains

      //println(forwardButNotPG map { g => g.task.name + (g.arguments map { _.name }).mkString("(", ",", ")") } mkString "\n")

      //println(forwardButNotPG.size)
      //println(pgButNotForward.size)

      val pgLastState = planningGraph.reachableGroundLiterals filter { _.isPositive } toSet
      val forwardSearchLastState = forwardSearch.reachableGroundLiterals filter { _.isPositive } toSet

      // check whole graph
      forwardSearch.layer zip planningGraph.layer.drop(1) foreach { case ((fActions, fState), (pgActions, pgState)) =>

        val fActionCount = fActions.size
        val pgActionCount = pgActions.size
        val fStateCount = fState count { _.isPositive }
        val pgStateCount = pgState.size

        println(fActionCount + " vs " + pgActionCount)
        println(fStateCount + " vs " + pgStateCount)

        //println("\n\n\n\nSTATE")
        //println(pgState map {literal => literal.predicate.name + (literal.parameter map { _.name }).mkString("(", ",", ")")} mkString "\n")

        //println("\n\n\n\nDifferentActions")
        //println(fActions diff pgActions map { g => g.task.name + (g.arguments map { _.name }).mkString("(", ",", ")") } mkString "\n")

        assert(fActionCount == pgActionCount)
        assert(fStateCount == pgStateCount)
      }

      // check executability
      forwardButNotPG foreach { groundTask => assert(groundTask.substitutedPreconditionsSet subsetOf pgLastState) }
      pgButNotForward foreach { groundTask => assert(groundTask.substitutedPreconditionsSet subsetOf forwardSearchLastState) }




      assert(pgLastState.size == forwardSearchLastState.size)
      assert(forwardReacheableActions == planningGraphReacheableActions)

    }
  }
}