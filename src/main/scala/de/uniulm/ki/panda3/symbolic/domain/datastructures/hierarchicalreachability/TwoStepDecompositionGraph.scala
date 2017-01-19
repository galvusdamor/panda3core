package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.logic.{Sort, Variable, Constant}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{PlanStep, GroundTask}

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TwoStepDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis, prunePrimitive: Boolean)
  extends TaskDecompositionGraph {


  private case class CartesianGroundMethod(method: DecompositionMethod, parameter: Map[Variable, Set[Constant]]) {
    lazy val subTasks  : Seq[CartesianGroundTask]           = subTaskMap.values.toSeq
    lazy val subTaskMap: Map[PlanStep, CartesianGroundTask] = method.subPlan.planStepsWithoutInitGoal map { case ps@PlanStep(_, schema: ReducedTask, arguments) =>
      ps -> CartesianGroundTask(schema, arguments map parameter)
    } toMap

    lazy val subCartesianToPlanSteps: Map[CartesianGroundTask, Seq[PlanStep]] = subTaskMap.toSeq groupBy { _._2 } map { case (cart, seq) => cart -> seq.map(_._1) }

    lazy val abstractTask: CartesianGroundTask = CartesianGroundTask(method.abstractTask, method.abstractTask.parameters map parameter)

    private val constraintsPerVariable: Map[Variable, Seq[VariableConstraint]] = method.subPlan.variableConstraints.variables.toSeq map { v =>
      v -> (method.subPlan.variableConstraints.constraints filter { _.getVariables contains v })
    } toMap

    private def areParametersAllowed(newBindings: Seq[(Variable, Constant)], instantiation: Map[Variable, Constant]): Boolean =
      newBindings flatMap { case (v, _) => constraintsPerVariable(v) } forall {
        case Equal(var1, var2: Variable)     => if (instantiation.contains(var1) && instantiation.contains(var2)) instantiation(var1) == instantiation(var2) else true
        case Equal(vari, const: Constant)    => instantiation(vari) == const
        case NotEqual(var1, var2: Variable)  => if (instantiation.contains(var1) && instantiation.contains(var2)) instantiation(var1) != instantiation(var2) else true
        case NotEqual(vari, const: Constant) => instantiation(vari) != const
        case OfSort(vari, sort)              => sort.elements contains instantiation(vari)
        case NotOfSort(vari, sort)           => !(sort.elements contains instantiation(vari))
      }

    // : Seq[(Variable, Seq[Constant])]
    private val (nonBindableVariables, boundByEquality) = {
      val planUF = SymbolicUnionFind.constructVariableUnionFind(method.subPlan)
      val planArguments = method.subPlan.planStepsWithoutInitGoal flatMap { _.arguments } toSet

      val nonBound = parameter.keySet -- planArguments
      val potentialEquality = nonBound map { v => (v, planArguments.find(arg => planUF(v) == planUF(arg))) }

      val (bindByEquality, nonBindable) = potentialEquality partition { case (_, Some(_)) => true; case _ => false }

      (nonBindable map { case (v, _) => (v, parameter(v).toSeq) } toSeq, bindByEquality map { case (v1, Some(v2)) => (v1, v2) })
    }

    def groundWithPossibleTasks(possibleTasks: mutable.Map[CartesianGroundTask, Set[GroundTask]]): Seq[GroundedDecompositionMethod] = {
      // treat plansteps in increasing order of groundings
      val planStepsSortedByDifficulty = method.subPlan.planStepsWithoutInitGoal sortBy { ps => possibleTasks(subTaskMap(ps)).size }


      val (_, possibleTasksMapsSeq) = planStepsSortedByDifficulty.foldLeft[(Set[Variable], Seq[(PlanStep, Seq[Variable], Map[Seq[Constant], Seq[GroundTask]])])]((Set(), Nil))(
        { case ((boundVariables, mapsSoFar), nextPlanStep) =>
          val commonVariables = nextPlanStep.arguments.zipWithIndex filter { case (v, _) => boundVariables contains v }
          val commonVariablesIndex = commonVariables map { _._2 }
          val groundingsPerInstantiation =
            possibleTasks(subTaskMap(nextPlanStep)) groupBy { groundTask => commonVariablesIndex map groundTask.argumentArray } map { case (a, b) => (a, b.toSeq) } withDefaultValue Nil

          val nextEntry = (nextPlanStep, commonVariables map { _._1 }, groundingsPerInstantiation)

          (boundVariables ++ nextPlanStep.arguments, mapsSoFar :+ nextEntry)
        })


      val possibleTasksMaps: Array[(PlanStep, Seq[Variable], Map[Seq[Constant], Seq[GroundTask]])] = possibleTasksMapsSeq.toArray


      // recursively match the tasks
      def matchRecursively(position: Int, variableBinding: Map[Variable, Constant]): Seq[GroundedDecompositionMethod] = {
        //println("RECURSION")
        if (position == possibleTasksMaps.length) {
          //println("NON BIND " + nonBindableVariables.size + (nonBindableVariables map { _._2.size }))
          val equalityBindable = variableBinding ++ (boundByEquality map { case (v, eqV) => (v, variableBinding(eqV)) })

          // instantiate all variables that do not occur in the methods subplan (usually some rough arguments of the abtract task)
          Sort.allPossibleInstantiationsWithVariables(nonBindableVariables) map { b => (b, b ++ equalityBindable toMap) } collect {
            case (binding, fullBinding) if !(binding exists { case (nv, c) => if (equalityBindable.contains(nv)) c != equalityBindable(nv) else false }) &&
              method.areParametersAllowed(fullBinding) => GroundedDecompositionMethod(method, fullBinding)
          } filter { _.isCorrentlyInheriting }
        } else {
          val (nextPlanStep, commonVariables, groundAccessMap) = possibleTasksMaps(position)
          val commonVariablesValues = commonVariables map variableBinding

          val possibleGroundings: Seq[GroundTask] = groundAccessMap(commonVariablesValues)

          //println("POSS groundings " + possibleGroundings.size)

          possibleGroundings flatMap { groundTask =>
            val newVariableBindingList = nextPlanStep.arguments zip groundTask.arguments
            val newVariableBinding = newVariableBindingList.toMap

            if (nextPlanStep.arguments exists { nv => if (newVariableBinding.contains(nv) && variableBinding.contains(nv)) newVariableBinding(nv) != variableBinding(nv) else false })
              Nil
            else {
              val newBinding = variableBinding ++ newVariableBinding
              // inefficient, actually we only have to check the constraints pertaining to newly added variables
              if (areParametersAllowed(newVariableBindingList, newBinding)) matchRecursively(position + 1, newBinding) else Nil
            }
          } toSeq
        }
      }

      val res = matchRecursively(0, Map())


      //println("RESULT " + res.length)
      res
    }

  }

  private case class CartesianGroundTask(task: Task, parameter: Seq[Set[Constant]]) extends PrettyPrintable {
    val argumentMap: Map[Variable, Seq[Constant]] = task.parameters zip (parameter map { _.toSeq }) toMap

    override def shortInfo: String = task.name + (parameter map { l => l.map(_.name).mkString("(", ",", ")") }).mkString(";")

    override def mediumInfo: String = shortInfo

    override def longInfo: String = mediumInfo

    def isCompatible(groundTask: GroundTask): Boolean = this.parameter zip groundTask.arguments forall { case (allowed, param) => allowed contains param }
  }

  lazy val (abstractTaskGroundings, groundedDecompositionMethods) = {
    // 1. propagate possible parameter values in a top down fashion

    val cartTasksMap = new mutable.HashMap[Task, Set[CartesianGroundTask]]().withDefaultValue(Set())
    val cartMethodsMap = new mutable.HashMap[CartesianGroundTask, Set[CartesianGroundMethod]]().withDefaultValue(Set())
    val cartTaskInMethodsMap = new mutable.HashMap[CartesianGroundTask, Set[CartesianGroundMethod]]().withDefaultValue(Set())

    def dfs(currentGroundTask: CartesianGroundTask): Unit = if (!(cartTasksMap(currentGroundTask.task) contains currentGroundTask)) {
      // add the ground task to its map
      cartTasksMap(currentGroundTask.task) = cartTasksMap(currentGroundTask.task) + currentGroundTask
      //println("TASK " + currentGroundTask.task.name)
      // if the task is abstract, we have to ground it
      // we have a partial variable binding from the abstract task
      val possibleMethods: Seq[(SimpleDecompositionMethod, CartesianGroundMethod)] =
        (domain.methodsForAbstractTasks.getOrElse(currentGroundTask.task, Nil) ++ (if (topMethod.abstractTask == currentGroundTask.task) topMethod :: Nil else Nil)) map {
          case simpleMethod: SimpleDecompositionMethod =>
            //println("Method " + simpleMethod.name)
            val setParameter: mutable.Map[Variable, Set[Constant]] = new mutable.HashMap()
            // fill map
            simpleMethod.subPlan.variableConstraints.variables foreach {
              v =>
                val cspPossibleValues = simpleMethod.subPlan.variableConstraints.reducedDomainOf(v)
                if (currentGroundTask.argumentMap contains v) cspPossibleValues intersect currentGroundTask.argumentMap(v) else cspPossibleValues

                setParameter(v) = cspPossibleValues.toSet
            }
            // propagate equality
            var changed = true
            while (changed)
              changed = simpleMethod.subPlan.variableConstraints.constraints collect {
                case Equal(var1: Variable, var2: Variable) =>
                  val newDomain = setParameter(var1) intersect setParameter(var2)
                  val innerChanged = newDomain != setParameter(var1) || newDomain != setParameter(var2)

                  setParameter(var1) = newDomain
                  setParameter(var2) = newDomain

                  innerChanged
              } exists { x => x }

            if (setParameter exists { _._2.isEmpty }) None else Some((simpleMethod, CartesianGroundMethod(simpleMethod, setParameter.toMap)))
          case _                                       => noSupport(NONSIMPLEMETHOD)
        } collect {case Some(x) => x}

      // add the new methods to the map
      val flattenedPossibleMethods = possibleMethods map { _._2 }
      cartMethodsMap(currentGroundTask) = cartMethodsMap(currentGroundTask) ++ flattenedPossibleMethods
      flattenedPossibleMethods foreach {
        cartMethod => cartMethod.subTasks foreach {
          subTask => cartTaskInMethodsMap(subTask) = cartTaskInMethodsMap(subTask) + cartMethod
        }
      }


      // perform recursion
      flattenedPossibleMethods flatMap { _.subTasks } foreach dfs
    }

    // start the cartesian process from the artificial grounding top task
    val cartesianTop = CartesianGroundTask(groundedTopTask.task.asInstanceOf[ReducedTask], groundedTopTask.arguments map { x => Set(x) })
    dfs(cartesianTop)


    // 2. build the groundings in a bottom up fashion
    val taskOrdering = domain.taskSchemaTransitionGraph.condensation.topologicalOrdering.get.reverse :+ Set(groundedTopTask.task)

    val methodsMap = new mutable.HashMap[GroundTask, Set[GroundedDecompositionMethod]]().withDefaultValue(Set())
    // go through the reachable ground tasks and add to data structure
    val possibleGroundInstances = new mutable.HashMap[CartesianGroundTask, Set[GroundTask]]().withDefaultValue(Set())
    groundedReachabilityAnalysis.reachableGroundPrimitiveActions foreach {
      gt => cartTasksMap(gt.task) foreach {
        ct =>
          // only add if the ground action is a valid instantiation of the cartesian action
          if (ct.parameter zip gt.arguments forall {
            case (cs, c) => cs contains c
          }) possibleGroundInstances(ct) = possibleGroundInstances(ct) + gt
      }
    }

    // run the actual grounding procedure
    taskOrdering foreach {
      scc => if (scc.size != 1 || scc.head.isAbstract) {
        //println("\n\n\n\n\nSCC " + (scc map { _.name }))

        def groundNew(cartesianGroundMethod: CartesianGroundMethod): Seq[(Task, GroundTask)] = {
          //println("GROUND A NEW " + cartesianGroundMethod.method.name + " " + (cartesianGroundMethod.subTasks map { gt => gt.shortInfo + " " + possibleGroundInstances(gt).size })
          //  .mkString("  "))

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