package de.uniulm.ki.panda3.symbolic.search

import de.uniulm.ki.panda3.symbolic.compiler.ClosedWorldAssumption
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
 * This is a very simple DFS planner
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object DFS {

  def main(args: Array[String]) {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/AssemblyTask_domain.xml")
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/AssemblyTask_problem.xml", domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val paredProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, paredProblem, ())

    initTime = System.currentTimeMillis()
    println(search(cwaApplied._1, cwaApplied._2))
  }

  var nodes: Int = 0
  var initTime: Long = 0
  var d: Int = 0
  var crap: Int = 0

  def search(domain: Domain, plan: Plan): Option[Plan] = if (plan.flaws.size == 0) Some(plan)
  else {
    nodes = nodes + 1
    d = d + 1
    if (nodes % 10 == 0) println(nodes * 1000.0 / (System.currentTimeMillis() - initTime) + " " + d + s" $crap/$nodes")
    //println()
    //println("CALL with Plan:")
    //println("---------------")
    //println(plan.shortInfo)
    var then = System.currentTimeMillis()

    val flaws = plan.flaws
    val resolvers = flaws map {_.resolvents(domain)}

    var now = System.currentTimeMillis()
    //println("Time to calculate resolvers: " + (now - then))

    //println()
    //println("Flaws " + ((resolvers zip flaws) map {case (rs,f) => rs.size + " " + f.shortInfo}).mkString("\n"))

    if (resolvers exists {_.size == 0}) {d = d - 1; crap = crap + 1; None}
    else {
      val selectedResolvers: Seq[Modification] = (resolvers filterNot {_.size == 0}).head
      //println(selectedResolvers.size)

      val ret = selectedResolvers.foldLeft[Option[Plan]](None)({
        case (Some(p), _) => Some(p)
        case (None, res)  =>
          then = System.currentTimeMillis()
          val nextPlan = plan.modify(res)
          now = System.currentTimeMillis()
          //println("Time to apply modification: " + (now - then))
          search(domain, nextPlan)
      })
      d = d - 1

      ret
    }
  }
}
