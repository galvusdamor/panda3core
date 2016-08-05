package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{Grounding, RemoveNegativePreconditions, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.EverythingIsHiearchicallyReachable
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphCompareWithOld extends FlatSpec {

  def runComparisonWithDomain(domainFile: String, problemFile: String, useBuckets: Boolean): Unit = {

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, info = true)
    val plain = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val negPre = RemoveNegativePreconditions.transform(plain, ())
    val (domain, initialPlan) = Grounding.transform(negPre, EverythingIsHiearchicallyReachable(negPre._1, negPre._2))

    val groundedInitialState = negPre._2.groundedInitialState filter { _.isPositive }
    val currentPlanningGraph = GroundedPlanningGraph(negPre._1, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))
    val oldPlanningGraph = OldGroundedPlanningGraph(negPre._1, groundedInitialState.toSet, computeMutexes = true, isSerial = false)

    println("WAITING")
    Thread.sleep(10000)
    //System.in.read()
    println("GO")
    val startTime = System.currentTimeMillis()
    val currentLayer = currentPlanningGraph.layerWithMutexes.drop(1)
    val intermediateTime = System.currentTimeMillis()
    val oldLayer = oldPlanningGraph.layerWithMutexes
    val endTime = System.currentTimeMillis()
    println("It took " + (intermediateTime - startTime) + " ms for the current and " + (endTime - intermediateTime) + " ms for the new.")

     currentLayer zip oldLayer foreach {
      case ((newActions, newActionMutexes, newState, newStateMutexes), (oldActions, oldActionMutexes, oldState, oldStateMutexes)) =>
        assert(newActions == oldActions)
        assert(newActionMutexes == oldActionMutexes)
        assert(newState == oldState)
        assert(newStateMutexes == oldStateMutexes)
    }
  }

  false /*:: true*/ :: Nil foreach { useBuckets =>
    /*
    "01" :: "02" :: "03" :: "04" :: "05" :: "06" :: Nil foreach { problemID =>
      //"03" :: Nil foreach { problemID =>
      "The grounded planning graph" + (if (useBuckets) " with buckets" else "") must "produce the same result as the old implementation implementation in TC " + problemID in {
        val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest" + problemID + "_domain.hddl"
        val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest" + problemID + "_problem.hddl"
        runComparisonWithDomain(domainFile, problemFile, useBuckets)
      }
    }

    "01" :: "02" :: "03" :: "04" :: Nil foreach { id =>
      it must "produce the same result in PEGSOL " + id in {
        val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p" + id + "-domain.pddl"
        val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p" + id + ".pddl"
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }

    "1" :: "2" :: "3" :: "4" :: Nil foreach { id =>
      it must "produce the same result in SATELITE " + id in {
        val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/domain/stripsSat.pddl"
        val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/problems/pfile" + id
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }

    "P01_NET1_B6_G2.PDDL" :: "P02_NET1_B6_G4.PDDL" :: "P03_NET1_B8_G3.PDDL" :: */"P04_NET1_B8_G5.PDDL" :: Nil foreach { id =>
    ("Foo" + useBuckets) must "produce the same result in PIPESWORLD " + id in {
      //it must "produce the same result in PIPESWORLD " + id in {
        val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PIPESWORLD/domain/DOMAIN.PDDL"
        val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PIPESWORLD/problems/" + id
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }
  }
}