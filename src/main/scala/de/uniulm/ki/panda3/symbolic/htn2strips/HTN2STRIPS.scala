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

package de.uniulm.ki.panda3.symbolic.htn2strips

import de.uniulm.ki.panda3.symbolic.compiler.SHOPMethodCompiler
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object HTN2STRIPS {

  def computeProgressionBoundForDepth(domain: Domain, plan: Plan, depth: Int): Int = {
    // initialise
    val initialPBMap = domain.primitiveTasks map { p => p -> (if (p.name.startsWith(SHOPMethodCompiler.SHOP_METHOD_PRECONDITION_PREFIX))0 else 1) } toMap

    val finalPBMap = Range(0, depth).foldRight(initialPBMap)(
      { case (d, pbMap) =>
        println("\n\nLayer " + d)

        val newValues = domain.decompositionMethods collect { case m if m.subPlan.orderingConstraints.graph.vertices.forall(ps => pbMap.contains(ps.schema)) =>
          val maxMethodPB = m.subPlan.orderingConstraints.fullGraph.allIndependentSets map { is =>
            val isValue = is.toSeq map { i => pbMap(i.schema) } sum
            val allOther = m.subPlan.orderingConstraints.graph.vertices filterNot is.contains filter { v => is.exists(i => m.subPlan.orderingConstraints.graph.reachable(i).contains(v)) }

            isValue + allOther.size
          } max

          //println("M " + m.name + " " + m.abstractTask.name + " " + maxMethodPB)

          m.abstractTask -> maxMethodPB
        } groupBy { _._1 } map { case (t, ts) => t -> ts.map(_._2).max }

        //println(newValues.map({case (t,i) => t.name + " " + i}).mkString("\n"))

        pbMap ++ newValues
      })


    finalPBMap(plan.planStepSchemaArray.head)
  }
}
