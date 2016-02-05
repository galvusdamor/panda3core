package de.uniulm.ki.panda3.efficient.domain

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

  // contains for each task an array containing all decomposition methods that can be applied to that task
  val taskToPossibleMethods: Map[Int, Array[EfficientDecompositionMethod]] = (tasks.indices map { i => i -> (decompositionMethods.toSeq filter { _.abstractTask == i }).toArray }).toMap
}