package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object TwoTaskPerMethod extends DecompositionMethodTransformer[Unit] {
  private var globalATCounter: Int = 0

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, noOption: Unit, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) = {
    assert(!originalDomain.isHybrid) // this does not work
    assert(originalDomain.isGround)
    // compute for all methods
    val methodsAndTasks = (methods :+ topMethod) map { case m@SimpleDecompositionMethod(at, plan, name) =>
      if (plan.planStepsWithoutInitGoal.size <= 2) (m :: Nil, Nil) else {
        val orderingDecomposition = plan.orderingConstraints.fullGraph.decomposition

        orderingDecomposition match {
          case None    =>
            //println("Method without deordering")
            (m :: Nil, Nil)
          case Some(dec) =>
            //println("Method with deordering")
            // generate methods recursively from deordering

            def dfs(dec: GraphDecomposition[PlanStep], givenAbstract: Option[Task]): (Task, Seq[DecompositionMethod], Seq[Task]) = dec match {
              case ElementaryDecomposition(planStep)       => (planStep.schema, Nil, Nil) // no additional methods and tasks needed
              case n: NonElementaryDecomposition[PlanStep] =>
                val seq = n.subelements
                assert(seq.length == 2) // important // TODO should change decomposition

                val left = seq(0)
                val right = seq(1)

                val (leftTask, leftMethods, leftTasks) = dfs(left, None)
                val (rightTask, rightMethods, rightTasks) = dfs(right, None)

                // create new abstract task
                val newAt = ReducedTask("transformationName_" + name + "_" + globalATCounter, isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil))
                val newPlan = n match {
                  case SequentialDecomposition(_) => Plan.sequentialPlan(leftTask :: rightTask :: Nil)
                  case ParallelDecomposition(_)   => Plan.parallelPlan(leftTask :: rightTask :: Nil)
                }

                val (newMethod, newTasks) = givenAbstract match {
                  case Some(t) => (SimpleDecompositionMethod(t, newPlan, name + globalATCounter), Nil)
                  case None    => (SimpleDecompositionMethod(newAt, newPlan, name + globalATCounter), newAt :: Nil)
                }
                globalATCounter += 1

                (newMethod.abstractTask, leftMethods ++ rightMethods :+ newMethod, leftTasks ++ rightTasks ++ newTasks)
            }

            val (_,newMethods,newTasks) = dfs(dec,Some(m.abstractTask))

            (newMethods,newTasks)
        }

      }
    }

    (methodsAndTasks.flatMap(_._1), methodsAndTasks.flatMap(_._2))
  }


  override protected val transformationName: String = "twoTaskPerMethod"
}
