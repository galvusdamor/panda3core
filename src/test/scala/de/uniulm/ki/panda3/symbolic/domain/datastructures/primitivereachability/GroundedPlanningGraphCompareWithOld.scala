// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.EverythingIsHiearchicallyReachable
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphCompareWithOld extends FlatSpec {

  def runComparisonWithDomain(domainFile: String, problemFile: String, useBuckets: Boolean): Unit = {

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    val sortsExpanded = ExpandSortHierarchy.transform(parsedDomainAndProblem, ())
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(sortsExpanded, info = true)
    val plain = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val negPre = RemoveNegativePreconditions.transform(plain, ())

    val groundedInitialState = negPre._2.groundedInitialState filter { _.isPositive }
    val currentPlanningGraph = GroundedPlanningGraph(negPre._1, groundedInitialState.toSet, GroundedPlanningGraphConfiguration(buckets = useBuckets))
    val oldPlanningGraph = OldGroundedPlanningGraph(negPre._1, groundedInitialState.toSet, computeMutexes = true, isSerial = false)

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

  false :: true :: Nil foreach { useBuckets =>
    "01" :: "02" :: "03" :: "04" :: "05" :: "06" :: Nil foreach { problemID =>
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
        val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/domain/stripsSat.pddl"
        val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/problems/pfile" + id
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }

    "P01_NET1_B6_G2.PDDL" :: "P02_NET1_B6_G4.PDDL" :: "P03_NET1_B8_G3.PDDL" :: "P04_NET1_B8_G5.PDDL" :: Nil foreach { id =>
      it must "produce the same result in PIPESWORLD " + id in {
        val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PIPESWORLD/domain/DOMAIN.PDDL"
        val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PIPESWORLD/problems/" + id
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }

    "seq-p01-001.pddl" :: "seq-p01-002.pddl" :: "seq-p02-003.pddl" :: "seq-p02-004.pddl" :: Nil foreach { id =>
      it must "produce the same result in STORAGE " + id in {
        val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/floortile/domain/domain.pddl"
        val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/floortile/problems/" + id
        runComparisonWithDomain(domFile, probFile, useBuckets)
      }
    }
  }
}
