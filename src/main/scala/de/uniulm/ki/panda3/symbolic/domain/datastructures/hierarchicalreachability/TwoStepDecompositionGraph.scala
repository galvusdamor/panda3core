package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TwoStepDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis, prunePrimitive: Boolean,
                                     omitTopDownStep: Boolean)
  extends TaskDecompositionGraph with WithHierarchyTyping{

  lazy val (abstractTaskGroundings, groundedDecompositionMethods) = {
    initialise()

    // 2. build the groundings in a bottom up fashion
    val taskOrdering = domain.taskSchemaTransitionGraph.condensation.topologicalOrdering.get.reverse :+ Set(groundedTopTask.task)

    val methodsMap = new mutable.HashMap[GroundTask, Set[GroundedDecompositionMethod]]().withDefaultValue(Set())
    // go through the reachable ground tasks and add to data structure
    val possibleGroundInstances = new mutable.HashMap[CartesianGroundTask, Set[GroundTask]]().withDefaultValue(Set())
    groundedReachabilityAnalysis.reachableGroundPrimitiveActions foreach {
      gt =>
        cartTasksMap(gt.task) foreach {
          ct =>
            // only add if the ground action is a valid instantiation of the cartesian action
            if (ct.parameter zip gt.arguments forall {
              case (cs, c) => cs contains c
            }) possibleGroundInstances(ct) = possibleGroundInstances(ct) + gt
        }
    }

    // run the actual grounding procedure
    taskOrdering foreach {
      scc =>
        if (scc.size != 1 || scc.head.isAbstract) {
          //println("\n\n\n\n\nSCC " + (scc map { _.name }))

          def groundNew(cartesianGroundMethod: CartesianGroundMethod): Seq[(Task, GroundTask)] = {
            //println("GROUND A NEW " + cartesianGroundMethod.method.name + " " + (cartesianGroundMethod.subTasks map { gt => gt.shortInfo + " " + possibleGroundInstances(gt).size })
            //  .mkString("  "))
            //println("RESTRICT " + (cartesianGroundMethod.parameter map {case (v,c) => v.name + " -> " + c.size}).mkString(" "))

            var nc = 0

            val newMethods = cartesianGroundMethod.groundWithPossibleTasks(possibleGroundInstances)
            //println("DONE " + newMethods.size)
            var actuallyNew = 0
            val r = newMethods flatMap {
              newMethod =>
                cartTasksMap(cartesianGroundMethod.abstractTask.task) foreach {
                  case possCT =>
                    if (possCT isCompatible newMethod.groundAbstractTask) if (!(possibleGroundInstances(possCT) contains newMethod.groundAbstractTask)) {
                      possibleGroundInstances(possCT) = possibleGroundInstances(possCT) + newMethod.groundAbstractTask
                      //println("ADD TO " + possCT.shortInfo )
                      nc += 1
                    }
                }

                if (!(methodsMap contains newMethod.groundAbstractTask)) {
                  methodsMap(newMethod.groundAbstractTask) = Set(newMethod)
                  //println("START " + (newMethod.variableBinding map { case (v,c) => v.name + "->" + c.name} mkString " "))
                  actuallyNew += 1
                  (newMethod.groundAbstractTask.task, newMethod.groundAbstractTask) :: Nil
                } else if (!(methodsMap(newMethod.groundAbstractTask) contains newMethod)) {
                  methodsMap(newMethod.groundAbstractTask) = methodsMap(newMethod.groundAbstractTask) + newMethod
                  actuallyNew += 1
                  //println("ADD "  + (newMethod.variableBinding map { case (v,c) => v.name + "->" + c.name} mkString " "))
                  Nil
                } else Nil
            }

            //println("Actually " + nc + " N " + actuallyNew)
            r
          }

          var untreatedTaskGroundings: Set[(Task, GroundTask)] =
            scc flatMap { task => cartTasksMap(task) flatMap { cartTask => cartMethodsMap(cartTask) } } flatMap { cartMethod => groundNew(cartMethod) }

          while (untreatedTaskGroundings.nonEmpty) {
            //println("ITERATE")
            val triggers: Set[(Task, Seq[GroundTask])] =
              untreatedTaskGroundings groupBy { _._1 } map { case (task, taskAndGroundTasks) => (task, taskAndGroundTasks map { _._2 } toSeq) } toSet

            untreatedTaskGroundings = triggers flatMap {
              case (task, groundTasks) =>
                //println("ONE TRIGGER " + groundTasks.length)
                cartTasksMap(task) flatMap { cartTask => cartTaskInMethodsMap(cartTask) } flatMap groundNew
            } filter { case (t, _) => scc contains t }
          }
        }
    }

    val taskGroundingMap: Map[Task, Set[GroundTask]] = methodsMap.keys groupBy { _.task } map { case (a, b) => (a, b.toSet) }
    (taskGroundingMap, methodsMap.toMap)
  }
}