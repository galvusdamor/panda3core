package de.uniulm.ki.panda3.efficient.domain

import scala.collection.mutable.ArrayBuffer

/**
  *
  * Assumptions:
  *
  * - Sorts are numbered 0..sz(subSortsForSort)-1
  * - Constants are numbered
  * - Predicates are numbered 0..sz(predicates)-1 and the contents of that array are the predicates parameters
  * - Tasks are numbered 0..sz(tasks)-1
  * - the list of tasks _must_ include all task schemes for init and goal tasks throughout the domain
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class EfficientDomain(var subSortsForSort: Array[Array[Int]] = Array(),
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
  lazy val possibleProducerTasksOf: Array[(Array[(Int, Int)], Array[(Int, Int)])] = (predicates.indices map { predicate =>
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

  /** contains for each task an array containing all decomposition methods that can be applied to that task */
  lazy val taskToPossibleMethods: Map[Int, Array[(EfficientDecompositionMethod, Int)]] =
    (tasks.indices map { i => i -> (decompositionMethods.zipWithIndex filter { _._1.abstractTask == i }) }).toMap
}