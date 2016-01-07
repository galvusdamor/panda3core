package de.uniulm.ki.panda3.symbolic.search

//import scala.pickling.Defaults._, scala.pickling.json._
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.symbolic.compiler.ClosedWorldAssumption
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
 * This is a very simple DFS planner
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object DFS {

  def main(args: Array[String]) {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml")
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml", domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())

    val x = startSearch(cwaApplied._1, cwaApplied._2, None)

    x._2.acquire()
    println("100 Nodes generated")
    //val json = x._1.pickle
    //println(json.toString)
  }


  /**
   * This functions starts the (asynchronious) search for a solution to a given planning problem.
   * It returns a pointer to the search space and a semaphore signaling that new search nodes have been inserted into the tree.
   *
   * The semaphore will not be released for every search node, but after a couple 100 ones.
   */
  def startSearch(domain: Domain, initialPlan: Plan, nodeLimit : Option[Int]): (SearchNode, Semaphore, Unit => Unit) = {
    val semaphore: Semaphore = new Semaphore(0)
    val node: SearchNode = new SearchNode(initialPlan, null, -1)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false

    def search(domain: Domain, node: SearchNode): Option[Plan] = if (node.plan.flaws.size == 0) Some(node.plan)
    else if ((nodeLimit.isDefined && nodes > nodeLimit.get) || abort) None
    else {
      nodes = nodes + 1
      d = d + 1
      if (nodes % 10 == 0) println(nodes * 1000.0 / (System.currentTimeMillis() - initTime) + " " + d + s" $crap/$nodes")
      if (nodes % 100 == 0) semaphore.release()

      val flaws = node.plan.flaws
      node.modifications = flaws map {_.resolvents(domain)}

      // check whether we are at a dead end in the search space
      if (node.modifications exists {_.size == 0}) {d = d - 1; crap = crap + 1; node.dirty = false; None}
      else {
        val selectedResolvers: Seq[Modification] = (node.modifications filterNot {_.size == 0}).head
        // set the selected flaw
        node.selectedFlaw = node.modifications indexOf selectedResolvers

        // create all children
        node.children = selectedResolvers map { m => new SearchNode(node.plan.modify(m), node, -1) }
        node.dirty = false

        // perform the search
        val ret = node.children.foldLeft[Option[Plan]](None)({
          case (Some(p), _) => Some(p)
          case (None, res)  => search(domain, res)
        })
        d = d - 1

        ret
      }
    }

    new Thread(new Runnable {
      override def run(): Unit = search(domain, node)
    }).start()

    (node, semaphore, {_ => abort = true})
  }
}