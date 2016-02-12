package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint, EfficientCSP}
import de.uniulm.ki.panda3.efficient.domain.{EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientOpenPrecondition
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class EfficientPlanTest extends FlatSpec {

  /**
    * sorts: 0
    * constants: 0,1,2 -> of sort 0
    * predicates:
    * 0()
    * 1(0)
    *
    * tasks:
    * init::
    * goal  :       : +0()
    * task1 :       : +1(0)
    * task2 : +1(0) :
    * task3 : +1(1) : + 1(0)
    */
  val init   = new EfficientTask(true, Array(), Array(), Array(), Array(), true)
  val goal   = new EfficientTask(true, Array(), Array(), Array(new EfficientLiteral(0, true, Array())), Array(), true)
  val task1  = new EfficientTask(true, Array(0), Array(), Array(), Array(new EfficientLiteral(1, true, Array(0))), true)
  val task2  = new EfficientTask(true, Array(0), Array(), Array(new EfficientLiteral(1, true, Array(0))), Array(), true)
  val task3  = new EfficientTask(false, Array(0, 0), Array(), Array(new EfficientLiteral(1, true, Array(1))), Array(new EfficientLiteral(1, false, Array(0))), true)
  val domain = new EfficientDomain(Array(Array()), Array(Array(0), Array(0), Array(0)), Array(Array(), Array(0), Array(0, 0)), Array(init, goal, task1, task2, task3), Array())

  // the order of tasks is scrambled to test whether we access the correct one
  val csp = new EfficientCSP(domain)().addVariables(Array(0, 0, 0, 0, 0, 0))
  csp.addConstraint(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 3))
  csp.addConstraint(EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, 1, 5))
  val ordering = new EfficientOrdering().addPlanSteps(7)
  Range(2, 7) foreach { i => ordering.addOrderingConstraint(0, i); ordering.addOrderingConstraint(i, 1) }
  ordering.addOrderingConstraint(3, 4)
  ordering.addOrderingConstraint(5, 3)
  val causalLink = EfficientCausalLink(3, 4, 0, 0)
  val plan       = new EfficientPlan(domain, Array(0, 1, 4, 2, 3, 4, 4, 4), Array(Array(), Array(), Array(0, 2), Array(1), Array(3), Array(4, 4), Array(5, 5), Array(5, 5)),
                                     Array(-1, -1, -1, -1, -1, -1, -1, 1), Array(-1, -1, -1, -1, -1, -1, -1, -1), csp, ordering, Array(causalLink))

  "Detecting Open Preconditions" must "yield all of them" in {
    val openPrecondition = plan.openPreconditions

    assert(openPrecondition.length == 4)

    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 1 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 2 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 5 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 6 && prec == 0 })
  }

  "Detecting Abstract Tasks" must "yield all of them" in {
    val abstractTasks = plan.abstractPlanSteps

    assert(abstractTasks.length == 3)
    assert(abstractTasks exists { _.planStep == 2 })
    assert(abstractTasks exists { _.planStep == 5 })
    assert(abstractTasks exists { _.planStep == 6 })
  }

  "Detecting Causal Threats" must "yield all of them" in {
    val causalThreats = plan.causalThreats

    assert(causalThreats.length == 1)
    assert(causalThreats exists { _.causalLink == causalLink })
    assert(causalThreats exists { _.threatingPlanStep == 2 })
    assert(causalThreats exists { _.indexOfThreatingEffect == 0 })
    assert(causalThreats exists { _.mgu.length == 1 })
    assert(causalThreats exists { cl => cl.mgu.head == EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, csp.getRepresentativeVariable(1)) ||
      cl.mgu.head == EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, csp.getRepresentativeVariable(1), 0)
    })
  }

  "Detecting Unbound Variables" must "yield all of them" in {
    val unboundVariables = plan.unboundVariables

    assert(unboundVariables.length == 6)
    assert(unboundVariables.toSet.size == 6)
  }
}