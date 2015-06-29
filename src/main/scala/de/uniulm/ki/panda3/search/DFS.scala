package de.uniulm.ki.panda3.search

import de.uniulm.ki.panda3.compiler.ClosedWorldAssumption
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.parser.XMLParser
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.modification.Modification

/**
 * This is a very simple DFS planner
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object DFS {

  def main(args: Array[String]) {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/parser/AssemblyTask_domain.xml")
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/parser/AssemblyTask_problem.xml", domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val paredProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, paredProblem, ())

    println(search(cwaApplied._1, cwaApplied._2))
  }

  def search(domain: Domain, plan: Plan): Option[Plan] = if (plan.flaws.size == 0) Some(plan)
  else {
    //println()
    println("CALL with Plan:")
    //println("---------------")
    //println(plan.shortInfo)
    var then = System.currentTimeMillis()

    val flaws = plan.flaws
    val resolvers = flaws map {_.resolvents(domain)}

    var now = System.currentTimeMillis()
    println("Time to calculate resolvers: " + (now - then))

    //println()
    //println("Flaws " + ((resolvers zip flaws) map {case (rs,f) => rs.size + " " + f.shortInfo}).mkString("\n"))

    if (resolvers exists {_.size == 0}) None
    else {
      val selectedResolvers: Seq[Modification] = (resolvers filterNot {_.size == 0}).head
      //println(selectedResolvers.size)

      selectedResolvers.foldLeft[Option[Plan]](None)({
        case (Some(p), _) => Some(p)
        case (None, res)  =>
          then = System.currentTimeMillis()
          val nextPlan = plan.modify(res)
          now = System.currentTimeMillis()
          println("Time to apply modification: " + (now - then))
          search(domain, nextPlan)
      })
    }
  }
}
