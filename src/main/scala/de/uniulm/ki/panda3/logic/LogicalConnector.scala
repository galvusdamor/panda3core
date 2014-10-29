package de.uniulm.ki.panda3.logic

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait LogicalConnector extends Formula {

}

case class Not(formula : Formula) extends LogicalConnector {

}

case class And(left : Formula, right : Formula) extends LogicalConnector {

}

case class Or(left : Formula, right : Formula) extends LogicalConnector {

}


case class Implies(left : Formula, right : Formula) extends LogicalConnector {

}

case class Equivalence(left : Formula, right : Formula) extends LogicalConnector {

}

