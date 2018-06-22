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

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
abstract class SOGPartialNoPath extends SOGEncoding {

  protected val before: ((Seq[Int], Seq[Int])) => String =
    memoise[(Seq[Int], Seq[Int]), String]({ case (pathA: Seq[Int], pathB: Seq[Int]) => assert(pathA != pathB); "before_" + pathA.mkString(";") + "_" + pathB.mkString(";") })

  override lazy val noAbstractsFormula: Seq[Clause] = primitivePaths flatMap { case (p, ts) => ts filter { _.isAbstract } map { t => Clause((pathAction(p.length - 1, p, t), false)) } }


  val enforceTotalOrder: Boolean = false

  val initVertex : (Seq[Int], Set[Task])                = (-1 :: Nil, Set(initialPlan.init.schema))
  val goalVertex : (Seq[Int], Set[Task])                = (-2 :: Nil, Set(initialPlan.goal.schema))
  val extendedSOG: DirectedGraph[(Seq[Int], Set[Task])] = SimpleDirectedGraph(sog.vertices :+ initVertex :+ goalVertex,
                                                                              sog.edgeList ++ (sog.vertices flatMap { v => (initVertex, v) :: (v, goalVertex) :: Nil
                                                                              }) :+ (initVertex, goalVertex))

  val pathsWithInitAndGoal: Array[Seq[Int]]         = extendedSOG.vertices map { _._1 } toArray
  val onlyPathSOG         : DirectedGraph[Seq[Int]] = extendedSOG map { _._1 }

  lazy val transitiveOrderClauses: Array[Clause] = {
    val clauses = new ArrayBuffer[Clause]
    var i = 0
    while (i < pathsWithInitAndGoal.length) {
      var k = 0
      while (k < pathsWithInitAndGoal.length) {
        if (i != k && !(onlyPathSOG.reachable(pathsWithInitAndGoal(i)) contains pathsWithInitAndGoal(k))) {
          if (enforceTotalOrder) clauses append Clause(Array(before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)), before(pathsWithInitAndGoal(k), pathsWithInitAndGoal(i))))

          var j = 0
          while (j < pathsWithInitAndGoal.length) {
            if (j != i && j != k) {
              // test whether one antecedant is already true
              // note: both cannot be true, else i->k would already hold in the SOG
              if (onlyPathSOG.reachable(pathsWithInitAndGoal(i)) contains pathsWithInitAndGoal(j))
                clauses append impliesRightAndSingle(before(pathsWithInitAndGoal(j), pathsWithInitAndGoal(k)) :: Nil,
                                                     before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)))
              else if (onlyPathSOG.reachable(pathsWithInitAndGoal(j)) contains pathsWithInitAndGoal(k))
                clauses append impliesRightAndSingle(before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j)) :: Nil,
                                                     before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)))
              else
                clauses append impliesRightAndSingle(before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j)) :: before(pathsWithInitAndGoal(j), pathsWithInitAndGoal(k)) :: Nil,
                                                     before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)))
            }
            j += 1
          }
        }
        k += 1
      }
      i += 1
    }

    //orderMustBeConsistent
    pathsWithInitAndGoal foreach { i => pathsWithInitAndGoal filter { _ != i } foreach { j => clauses append impliesNot(before(i, j), before(j, i)) } }

    //sogOrderMustBeRespected
    //extendedSOG.vertices foreach { case p@(i, _) => extendedSOG.reachable(p).-(p) map { _._1 } foreach { j => clauses append Clause(before(i, j)) } }

    clauses.toArray
  }
}
