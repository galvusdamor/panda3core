package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait LTLFormulaEncoding[NodeType, EdgeToType] extends AdditionalSATConstraint {

  def id: String

  def automaton: LTLAutomaton[NodeType, EdgeToType]

  val automataStatesToIndices: Map[NodeType, Int] = automaton.vertices.zipWithIndex.toMap

  //println(automataStatesToIndices map {case (f,i) => i + " " + f.longInfo} mkString "\n")

  protected def state(formula: NodeType, position: Int) = id + "_auto_state_" + automataStatesToIndices(formula) + "_" + position

  protected def noPresent(position: Int) = id + "_auto_Present" + position


  def maintainStateAtNoPresent(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {
    linearEncoding.linearPlan.zipWithIndex flatMap { case (taskMap, position) =>
      val setNoPresent: Seq[Clause] = (linearEncoding.notImplies(taskMap.values.toSeq, noPresent(position)) :: Nil) ++
        (taskMap.values map { a => linearEncoding.impliesNot(noPresent(position), a) })

      val noTaskRule: Seq[Clause] = automaton.vertices map { s => linearEncoding.impliesRightAndSingle(state(s, position) :: noPresent(position) :: Nil, state(s, position + 1)) }

      setNoPresent ++ noTaskRule
    }
  }
}