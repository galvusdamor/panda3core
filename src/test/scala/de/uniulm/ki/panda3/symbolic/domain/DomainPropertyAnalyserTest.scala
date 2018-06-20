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

package de.uniulm.ki.panda3.symbolic.domain

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.NaiveGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class DomainPropertyAnalyserTest extends FlatSpec {

  def loadDomainAndProblem(domainID: String): DomainPropertyAnalyser = {
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/domainTypeTestProblem.hddl"
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/domainTypeTestDomain" + domainID + ".hddl"
    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    val sortsExpanded = ExpandSortHierarchy(parsedDomainAndProblem, ())
    val cwaApplied = ClosedWorldAssumption(sortsExpanded, (true,Set[String]()))
    val shopCompiled = SHOPMethodCompiler(cwaApplied, ())
    val plainFormula = ToPlainFormulaRepresentation(shopCompiled, ())
    val (domain, plan) = RemoveNegativePreconditions(plainFormula, ())

    val initialState = plan.groundedInitialStateOnlyPositive
    val planningGraph = GroundedPlanningGraph(domain, initialState toSet, GroundedPlanningGraphConfiguration(isSerial = true))
    val tdg = NaiveGroundedTaskDecompositionGraph(domain, plan, planningGraph, prunePrimitive = true, println)

    DomainPropertyAnalyser(domain, tdg)
  }

  // ACYCLIC
  "Acyclicity" must "be recognised" in {
    assert(loadDomainAndProblem("03").isAcyclic)
    assert(loadDomainAndProblem("04").isAcyclic)
    assert(loadDomainAndProblem("07").isAcyclic)
  }

  it must "be correct if not present" in {
    assert(!loadDomainAndProblem("01").isAcyclic)
    assert(!loadDomainAndProblem("02").isAcyclic)
    assert(!loadDomainAndProblem("05").isAcyclic)
    assert(!loadDomainAndProblem("06").isAcyclic)
  }

  // MOSTLY ACYCLIC
  "Mostly acyclicity" must "be recognised" in {
    assert(loadDomainAndProblem("03").isMostlyAcyclic)
    assert(loadDomainAndProblem("04").isMostlyAcyclic)
    assert(loadDomainAndProblem("05").isMostlyAcyclic)
    assert(loadDomainAndProblem("07").isMostlyAcyclic)
  }

  it must "be correct if not present" in {
    assert(!loadDomainAndProblem("01").isMostlyAcyclic)
    assert(!loadDomainAndProblem("02").isMostlyAcyclic)
    assert(!loadDomainAndProblem("06").isMostlyAcyclic)
  }

  /// REGULAR
  "Regularity" must "be recognised" in {
    assert(loadDomainAndProblem("02").isRegular)
    assert(loadDomainAndProblem("03").isRegular)
    assert(loadDomainAndProblem("05").isRegular)
  }

  it must "be correct if not present" in {
    assert(!loadDomainAndProblem("01").isRegular)
    assert(!loadDomainAndProblem("04").isRegular)
    assert(!loadDomainAndProblem("06").isRegular)
    assert(!loadDomainAndProblem("07").isRegular)
  }

  // TAIL RECURSION
  "Tail recusion" must "be recognised" in {
    assert(loadDomainAndProblem("01").isTailRecursive)
    assert(loadDomainAndProblem("02").isTailRecursive)
    assert(loadDomainAndProblem("03").isTailRecursive)
    assert(loadDomainAndProblem("04").isTailRecursive)
    assert(loadDomainAndProblem("05").isTailRecursive)
    assert(loadDomainAndProblem("07").isTailRecursive)
  }

  it must "be correct if not present" in {
    assert(!loadDomainAndProblem("06").isTailRecursive)
  }
}
