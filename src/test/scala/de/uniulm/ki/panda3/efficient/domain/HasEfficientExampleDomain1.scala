package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint, EfficientCSP}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasEfficientExampleDomain1 {
  /**
    * sorts: 0
    * constants: 0,1,2 -> of sort 0
    * predicates:
    * 0()
    * 1(0)
    *
    * tasks:
    * 0: init()     :       :
    * 1: goal()     : +0()  :
    * 2: task1(0)   :       : +1(0)
    * 3: task2(0)   : +1(0) :
    * 4: task3(0,1) : +1(1) : -1(0)
    * 5: task4(0,1) :       : +1(0),+1(1),+0()
    * 6: task5(0)   : +1(0) : +1(0)
    */
  val init   = new EfficientTask(true, Array(), Array(), Array(), Array(), true)
  val goal   = new EfficientTask(true, Array(), Array(), Array(new EfficientLiteral(0, true, Array())), Array(), true)
  val task1  = new EfficientTask(true, Array(0), Array(), Array(), Array(new EfficientLiteral(1, true, Array(0))), true)
  val task2  = new EfficientTask(true, Array(0), Array(), Array(new EfficientLiteral(1, true, Array(0))), Array(), true)
  val task3  = new EfficientTask(false, Array(0, 0), Array(), Array(new EfficientLiteral(1, true, Array(1))), Array(new EfficientLiteral(1, false, Array(0))), true)
  val task4  = new EfficientTask(false, Array(0, 0), Array(), Array(),
                                 Array(new EfficientLiteral(1, true, Array(0)), new EfficientLiteral(1, true, Array(1)), new EfficientLiteral(0, true, Array())), true)
  val task5  = new EfficientTask(true, Array(0), Array(), Array(new EfficientLiteral(1, true, Array(0))), Array(new EfficientLiteral(1, true, Array(0))), false)
  val domain = new EfficientDomain(Array(Array()), Array(Array(0), Array(0), Array(0)), Array(Array(), Array(0), Array(0, 0)), Array(init, goal, task1, task2, task3, task4, task5), Array())


  val efficientPlanTestPlan = {
    // the order of tasks is scrambled to test whether we access the correct one
    val csp = new EfficientCSP(domain)().addVariables(Array(0, 0, 0, 0, 0, 0))
    csp.addConstraint(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 3))
    csp.addConstraint(EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, 1, 5))
    val ordering = new EfficientOrdering().addPlanSteps(7)
    Range(2, 7) foreach { i => ordering.addOrderingConstraint(0, i); ordering.addOrderingConstraint(i, 1) }
    ordering.addOrderingConstraint(3, 4)
    ordering.addOrderingConstraint(5, 3)
    val causalLink = EfficientCausalLink(3, 4, 0, 0)
    EfficientPlan(domain, Array(0, 1, 4, 2, 3, 4, 4, 4), Array(Array(), Array(), Array(0, 2), Array(1), Array(3), Array(4, 4), Array(5, 5), Array(5, 5)),
                  Array(-1, -1, -1, -1, -1, -1, -1, 1), Array(-1, -1, -1, -1, -1, -1, -1, -1), csp, ordering, Array(causalLink))
  }


  /** a plan containing:
    *
    * 0:init()
    * 1:goal()
    * 2:task5(0)
    * 3:task3(0,0)
    *
    *
    * ordering:
    * ...2
    * ./ \
    * 0    1
    * .\ /
    * ..3
    */
  val simpleOpenPreconditionPlan = {
    val csp = new EfficientCSP(domain)().addVariables(Array(0))
    val ordering = new EfficientOrdering().addPlanSteps(4)
    ordering.addOrderingConstraint(0, 2)
    ordering.addOrderingConstraint(0, 3)
    ordering.addOrderingConstraint(3, 1)
    ordering.addOrderingConstraint(2, 1)

    EfficientPlan(domain, Array(0, 1, 6, 4), Array(Array(), Array(), Array(0), Array(0, 0)), Array(-1, -1, -1, -1), Array(-1, -1, -1, -1), csp, ordering, Array())
  }
}
