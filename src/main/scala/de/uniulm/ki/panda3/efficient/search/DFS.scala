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

package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream
import java.util

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object DFS {

  def main(args: Array[String]) {
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream(domFile))
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem(new FileInputStream(probFile), domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem)
    val flattened = ToPlainFormulaRepresentation.transform(cwaApplied._1, cwaApplied._2, ())

    // wrap everything into the efficient Datastructures
    val wrapper = Wrapping(flattened._1, flattened._2)

    val initialPlan = wrapper.unwrap(flattened._2)

    println(initialPlan.flaws.length)

    System.in.read()

    //dfs(initialPlan, 0)
    initTime = System.currentTimeMillis()
    nodes = 0

    dfs(initialPlan, 0)
  }

  var initTime: Long = System.currentTimeMillis()
  var nodes: Int = 0 // count the nodes

  def dfs(plan: EfficientPlan, depth: Int): Option[EfficientPlan] = if (plan.flaws.length == 0) Some(plan)
  else {

    if (nodes % 500 == 0 && nodes > 0) {
      val nTime = System.currentTimeMillis()
      val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
      //time = nTime
      println("Plans Expanded: " + nodes + " " + nps + " Depth " + depth )
    }
    nodes += 1


    val flaws = plan.flaws
    //val modifications = new Array[Array[EfficientModification]](flaws.length)
    var flawnum = 0
    var smallFlaw = 0
    var smallFlawNumMod = 0x3f3f3f3f
    while (flawnum < flaws.length) {
      //printTime("ToModcall")
      val modnum = flaws(flawnum).estimatedNumberOfResolvers
      //modifications(flawnum) = flaws(flawnum).resolver
      //printTime("Modification")
      if (modnum < smallFlawNumMod) {
        smallFlawNumMod = modnum
        smallFlaw = flawnum
      }
      /*if (modifications(flawnum).length < smallFlawNumMod) {
        smallFlawNumMod = modifications(flawnum).length
        smallFlaw = flawnum
      }*/
      flawnum += 1
    }

    if (smallFlawNumMod == 0) None
    else {
      var result: Option[EfficientPlan] = None
      var modNum = 0
      val resolver = flaws(smallFlaw).resolver
      assert(resolver.length == smallFlawNumMod)
      while (modNum < smallFlawNumMod && result.isEmpty) {
        // apply modification
        val newPlan = plan.modify(resolver(modNum))
        result = dfs(newPlan, depth + 1)
        modNum += 1
      }
      result
    }
  }
}
