package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.sat.verify._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLMattmüllerEncoding(lTLFormula: LTLFormula, id: String) extends AdditionalSATConstraint with AdditionalEdgesInDisablingGraph {

  def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = linearEncoding match {
    case e: ExistsStep => Nil
    case _             => assert(false, "Mattmüller-Encoding is only compatible with existsstep"); Nil
  }

  override def additionalEdges(encoding: ExistsStep)(
    predicateToAdding: Map[Predicate, Array[encoding.IntTask]], predicateToDeleting: Map[Predicate, Array[encoding.IntTask]],
    predicateToNeeding: Map[Predicate, Array[encoding.IntTask]]): Seq[(encoding.IntTask, encoding.IntTask)] = {
    // first get relevant predicates
    val relevantPredicates = lTLFormula.allPredicates
    // TODO: so something with actions

    val affectedActions: Array[encoding.IntTask] = relevantPredicates flatMap { p => predicateToAdding.getOrElse(p,Array()) ++ predicateToDeleting.getOrElse(p,Array()) } toArray

    val edgesFromStatePredicates = affectedActions flatMap { a1 =>
      affectedActions collect { case a2 if a1 != a2 && a1.hasMoreEffectsRelativeToPredicates(a2, relevantPredicates) => (a1, a2)
      }
    }

    edgesFromStatePredicates
  }
}