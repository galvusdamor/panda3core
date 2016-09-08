package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.domain.datastructures.EfficientTaskSchemaTransitionGraph
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral

import scala.collection.{mutable, BitSet}
import scala.collection.mutable.ArrayBuffer

/**
  *
  * Assumptions:
  *
  * - Sorts are numbered 0..sz(subSortsForSort)-1
  * - Constants are numbered
  * - Predicates are numbered 0..sz(predicates)-1 and the contents of that array are the predicates parameters (the number of their sorts)
  * - Tasks are numbered 0..sz(tasks)-1
  * - the list of tasks _must_ include all task schemes for init and goal tasks throughout the domain
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientDomain(var subSortsForSort: Array[Array[Int]] = Array(),
                           var sortsOfConstant: Array[Array[Int]] = Array(),
                           var predicates: Array[Array[Int]] = Array(),
                           var tasks: Array[EfficientTask] = Array(),
                           var decompositionMethods: Array[EfficientDecompositionMethod] = Array()) {


  var constantsOfSort: Array[Array[Int]] = Array()
  recomputeConstantsOfSort()


  def recomputeConstantsOfSort(): Unit = {
    constantsOfSort = new Array[Array[Int]](subSortsForSort.length)
    val sortsOfConstantZipped = sortsOfConstant.zipWithIndex

    var i = 0
    while (i < constantsOfSort.length) {
      constantsOfSort(i) = (sortsOfConstantZipped filter { _._1.contains(i) }).map({ _._2 }).array
      i = i + 1
    }
  }

  lazy val insertableTasks: Array[EfficientTask] = tasks filter { _.allowedToInsert }

  /** the ith index of the array contains all possible tasks that produce predicate i. The first list in the pair the positive ones, the second the negative ones.
    * The inner pairs each contain the index of the task and the index of the possible producer
    */
  lazy val possibleProducerTasksOf: Array[(Array[(Int, Int)], Array[(Int, Int)])] = {
    val predicateToTaskMap = tasks.zipWithIndex flatMap { case (task, taskIndex) => task.effect.zipWithIndex map { x => ((task, taskIndex), x) } } groupBy { _._2._1.predicate }
    val entryMap: Map[Int, (Array[(Int, Int)], Array[(Int, Int)])] = predicateToTaskMap map { case (a, b) =>
      val parted = b.partition(_._2._1.isPositive)

      def toArray(list: Seq[((EfficientTask, Int), (EfficientLiteral, Int))]): Array[(Int, Int)] = list map { case ((_, i), (_, j)) => (i, j) } toArray

      a ->(toArray(parted._1), toArray(parted._2))
    } toMap

    predicates.indices map { predicate => entryMap.getOrElse(predicate, (Array[(Int, Int)](), Array[(Int, Int)]())) } toArray
  }


  /*(predicates.indices map { predicate =>
    val positive: Array[(Int, Int)] = tasks.zipWithIndex flatMap { case (task, taskIndex) =>
      if (task.allowedToInsert) task.effect.zipWithIndex collect {
        case (literal, literalIndex) if literal.isPositive && literal.predicate == predicate => (taskIndex, literalIndex)
      }
      else Nil
    }

    val negative: Array[(Int, Int)] = tasks.zipWithIndex flatMap { case (task, taskIndex) =>
      if (task.allowedToInsert) task.effect.zipWithIndex collect {
        case (literal, literalIndex) if !literal.isPositive && literal.predicate == predicate => (taskIndex, literalIndex)
      } else Nil
    }
    (positive, negative)
  }).toArray
}*/


  private def literalsToPredicateBitSet(literalList: Array[EfficientLiteral]): BitSet = {
    val bitset = mutable.BitSet()
    literalList foreach {
      l => bitset add l.predicate
    }
    bitset
  }

  lazy val taskToEffectPredicates: Array[(BitSet, BitSet)] = tasks map {
    task =>
      val (positiveEffects, negativeEffects) = task.effect partition {
        _.isPositive
      }
      (literalsToPredicateBitSet(positiveEffects), literalsToPredicateBitSet(negativeEffects))
  }

  lazy val taskToPreconditionPredicates: Array[(BitSet, BitSet)] = tasks map {
    task =>
      val (positivePreconditions, negativePreconditions) = task.precondition partition {
        _.isPositive
      }
      (literalsToPredicateBitSet(positivePreconditions), literalsToPredicateBitSet(negativePreconditions))
  }

  lazy val tasksPreconditionCanBeSupportedBy: Array[Array[mutable.BitSet]] = tasks.zipWithIndex map {
    case (thisTask, taskID) =>
      thisTask.precondition map {
        case EfficientLiteral(predicate, isPositive, _) =>
          val bitset = mutable.BitSet()

          (if (isPositive) possibleProducerTasksOf(predicate)._1 else possibleProducerTasksOf(predicate)._2) foreach {
            case (t, _) => bitset add t
          }

          /*tasks.indices filter { other =>
            val otherPSEffects = taskToEffectPredicates(other)
            if (isPositive) otherPSEffects._1 contains predicate else otherPSEffects._2 contains predicate
          } foreach bitset.add*/

          bitset
      } toArray
  } toArray

  /** contains for each task an array containing all decomposition methods that can be applied to that task */
  lazy val taskToPossibleMethods: Array[Array[(EfficientDecompositionMethod, Int)]] = {
    val methodMap = decompositionMethods.zipWithIndex groupBy { _._1.abstractTask }

    tasks.indices map { i => methodMap.getOrElse(i, Array()) } toArray
  }

  lazy val taskSchemaTransitionGraph: EfficientTaskSchemaTransitionGraph = EfficientTaskSchemaTransitionGraph(this)


  /** This applied literal encoding (i.e. +l is 2*l and -l is 2*l+1) */
  lazy val methodCanSupportLiteral: Array[mutable.BitSet] = {
    val startT = System.currentTimeMillis()
    val x = decompositionMethods.zipWithIndex map {
      case (method, mi) =>
        if (mi % 100 == 0)
          println("METHOD " + mi + "/" + decompositionMethods.length)

        val containedTasks = method.subPlan.planStepTasks.drop(2)

        //val reachableTasks = containedTasks flatMap taskSchemaTransitionGraph.reachable

        val bitset = new mutable.BitSet()

        containedTasks foreach {
          task =>
            tasks(task).effect foreach {
              case EfficientLiteral(pred, isPositive, _) => bitset add (2 * pred + (if (isPositive) 0 else 1))
            }

            taskSchemaTransitionGraph.taskCanSupportByDecomposition(task) foreach {
              case (pred, isPositive) => bitset add (2 * pred + (if (isPositive) 0 else 1))
            }
        }
        bitset
    }

    println("TIME " + (System.currentTimeMillis() - startT))

    x
  }


  lazy val taskAddEffectsPerPredicate: Array[Array[Array[(EfficientLiteral, Int)]]] = tasks map {
    task =>
      val effectMap = task.effect.zipWithIndex filter {
        _._1.isPositive
      } groupBy {
        _._1.predicate
      }
      predicates.indices map {
        effectMap.getOrElse(_, Array())
      } toArray
  } toArray

  lazy val taskDelEffectsPerPredicate: Array[Array[Array[(EfficientLiteral, Int)]]] = tasks map {
    task =>
      val effectMap = task.effect.zipWithIndex filter {
        _._1.isNegative
      } groupBy {
        _._1.predicate
      }
      predicates.indices map {
        effectMap.getOrElse(_, Array())
      } toArray
  } toArray


}