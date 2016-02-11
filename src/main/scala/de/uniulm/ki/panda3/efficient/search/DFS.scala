package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream

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
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val flattened = ToPlainFormulaRepresentation.transform(cwaApplied._1, cwaApplied._2, ())

    // wrap everything into the efficient Datastructures
    val wrapper = Wrapping(flattened._1, flattened._2)

    val initialPlan = wrapper.unwrap(flattened._2)

    println(initialPlan.flaws.length)

    dfs(initialPlan, 0)
  }


  def dfs(plan: EfficientPlan, depth: Int): Option[EfficientPlan] = if (plan.flaws.length == 0) Some(plan)
  else {
    println(depth)
    val flaws = plan.flaws
    val modifications = new Array[Array[EfficientModification]](flaws.length)
    var flawnum = 0
    var smallFlaw = 0
    var smallFlawNumMod = 0x3f3f3f3f
    while (flawnum < flaws.length) {
      modifications(flawnum) = flaws(flawnum).resolver
      if (modifications(flawnum).length < smallFlawNumMod) {
        smallFlawNumMod = modifications(flawnum).length
        smallFlaw = flawnum
      }
      flawnum += 1
    }

    if (smallFlawNumMod == 0) None
    else {
      var result: Option[EfficientPlan] = None
      var modNum = 0
      while (modNum < smallFlawNumMod && result.isEmpty) {
        // apply modification
        val newPlan = plan.modify(modifications(smallFlaw)(modNum))
        result = dfs(newPlan, depth + 1)
      }
      result
    }
  }


}