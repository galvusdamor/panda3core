package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem
import de.uniulm.ki.panda3.symbolic.csp.SymbolicUnionFind
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SASPlusGrounding(domain: Domain, problem: Plan, sasPlusProblem: SasPlusProblem) extends GroundedPrimitiveReachabilityAnalysis {


  override lazy val reachableGroundLiterals: Seq[GroundLiteral] =
    ((reachableGroundPrimitiveActions flatMap { t => t.substitutedPreconditions ++ t.substitutedEffects }) ++ problem.groundedInitialState) distinct

  override lazy val reachableGroundPrimitiveActions: Seq[GroundTask] = groundedTasksToNewGroundTasksMapping.keys.toSeq

  lazy val sasPlusPredicates: Array[Predicate] = sasPlusProblem.values.zipWithIndex flatMap { case (values, varNum) => values map { v => Predicate("var" + varNum + "=" + v, Nil) } }

  lazy val groundedTasksToNewGroundTasksMapping: Map[GroundTask, Task] = {
    val generalActions = sasPlusProblem.getGroundedOperatorSignatures.zipWithIndex map { case (op, i) =>
      val splitted = op split " "
      val operatorName = splitted.head
      val parameter = splitted.tail

      import de.uniulm.ki.panda3.symbolic.writer._
      val taskOption: Option[Task] = domain.primitiveTasks find { t => toPDDLIdentifier(t.name).toLowerCase == operatorName }

      (taskOption, parameter, i)
    } collect { case (Some(t), parameterStrings, taskIndex) =>
      val taskUF = SymbolicUnionFind.constructVariableUnionFind(t)

      // generate the actual parameter list (may contain constants that where omitted when writing the PDDL format)
      val (params, remainingParameter) = t.parameters.foldLeft[(Seq[Constant], Array[String])]((Nil, parameterStrings))(
        {
          case ((paramsSoFar, nextParams), parameter) =>
            taskUF.getRepresentative(parameter) match {
              case v: Variable =>
                import de.uniulm.ki.panda3.symbolic.writer._

                //if (domain.constants.find(p => toPDDLIdentifier(p.name).toLowerCase == nextParams.head).isEmpty)
                //  println("OCH NEE")

                (paramsSoFar.:+(domain.constants.find(p => toPDDLIdentifier(p.name).toLowerCase == nextParams.head).get), nextParams.tail)
              case c: Constant => (paramsSoFar.:+(c), nextParams)
            }
        })
      assert(remainingParameter.length == 0)

      // build new task
      val sasGroundTask = {
        val precondition = sasPlusProblem.precLists(taskIndex) map { prec => Literal(sasPlusPredicates(prec), isPositive = true, Nil) }
        val addEffects = sasPlusProblem.addLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = true, Nil) }
        //val delEffects = sasPlusProblem.delLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = false, Nil) }
        val delEffects = sasPlusProblem.expandedDelLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = false, Nil) }

        ReducedTask(t + params.map { _.name }.mkString("[", ",", "]"), isPrimitive = true, Nil, Nil, Nil, And(precondition), And(addEffects ++ delEffects))
      }

      GroundTask(t, params) -> sasGroundTask
    }

    val newInitTask = {
      val effects = sasPlusProblem.s0List map { eff => Literal(sasPlusPredicates(eff), isPositive = true, Nil) }
      ReducedTask("init", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(effects))
    }

    val newGoalTask = {
      val preconditions = sasPlusProblem.gList map { prec => Literal(sasPlusPredicates(prec), isPositive = true, Nil) }
      ReducedTask("goal", isPrimitive = true, Nil, Nil, Nil, And(preconditions), And(Nil))
    }

    generalActions ++ ((problem.groundedInitialTask, newInitTask) ::(problem.groundedGoalTask, newGoalTask) :: Nil)
  } toMap
}
