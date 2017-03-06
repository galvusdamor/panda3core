package de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability

import java.{util => jav}

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain

import scala.collection.mutable.ArrayBuffer

/**
  * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de).
  * TODOS:
  *  - TODO: Check how fast Array.fill is.
  *  - TODO: Refactor multi-dimensional array iteration.
  *  - TODO: Longs instead of Ints might be necessary in some instances.
  *  - TODO: ArrayBuffers vs Stacks or Queues.
  */
class EfficientGroundedPlanningGraph(domain: EfficientDomain, initialState: Array[(Int, Array[Int])]) {

  /**
    * Maximum number of actions.
   */
  private val maxActions = computeMaxActions()

  /**
    * Maximum number of facts.
    */
  private val maxFacts = computeMaxFacts()

  /**
    * Array containing the taskID of each action.
    */
  val actionSpikeIDs: Array[Int] = new Array[Int](maxActions)

  /**
    * Array containing the arguments of each action.
    */
  val actionSpikeArgs: Array[Array[Int]] = new Array[Array[Int]](maxActions)

  /**
    * ArrayBuffer containing the first index into the actionSpikes for each rank.
    */
  val actionRankFirst: ArrayBuffer[Int] = new ArrayBuffer[Int]()

  /**
    * ArrayBuffer containing the last index into the actionSpikes for each rank.
    */
  val actionRankLast: ArrayBuffer[Int] = new ArrayBuffer[Int]()

  /**
    * Array containing the predicateID of each fact.
    */
  val factSpikeIDs: Array[Int] = new Array[Int](maxFacts)

  /**
    * Array containing the arguments of each fact.
    */
  val factSpikeArgs: Array[Array[Int]] = new Array[Array[Int]](maxFacts)

  /**
    * ArrayBuffer containing the first index into the factSpikes for each rank.
    */
  val factRankFirst: ArrayBuffer[Int] = new ArrayBuffer[Int]()

  /**
    * ArrayBuffer containing the last index into the factSpikes for each rank.
    */
  val factRankLast: ArrayBuffer[Int] = new ArrayBuffer[Int]()

  /**
    *
    */
  val factMutexes: Array[jav.BitSet] = Array.fill[jav.BitSet](maxFacts)(new jav.BitSet())

  /**
    *
    */
  val permanentActionMutexes: Array[jav.BitSet] = Array.fill[jav.BitSet](maxActions)(new jav.BitSet())

  /**
    *
    */
  val temporaryActionMutexes: Array[jav.BitSet] = Array.fill[jav.BitSet](maxActions)(new jav.BitSet())

  build()

  //noinspection ScalaStyle
  private def build(): Unit = {

    /**
      * Array containing the number of unmet preconditions for every task.
      * A precondition is unmet if no fact exists in the factSpike containing the precondition's predicate.
      */
    val unmetPreconditions: Array[Int] = new Array[Int](domain.tasks.length)

    /**
      * Array containing ArrayBuffers for every predicate of the domain.
      * The indices of the Array represent the index of the predicate in the domain.predicates-array.
      * Every predicate therefore has one ArrayBuffer containing the indices of every task that has the specific predicate as
      * a precondition. The index of a task is his position in the domain.tasks-array.
      */
    val predicateTaskMap: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())

    /**
      * A java BitSet acting as existence test for facts.
      * If a specific bit is set to true, the corresponding fact exists in the factSpike.
      * How facts correspond to  bits is explained in the computeBit(predicate, args)-method.
      */
    val factStorage: jav.BitSet = new jav.BitSet()

    /**
      * Array storing an ArrayBuffer for each predicate of the domain.
      * Every ArrayBuffer contains the indices (in the factSpike) of every fact that contains the specific predicate.
      */
    val predicateFactMap: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())

    val deletedFactMutexFirst = new ArrayBuffer[Int]()

    val deletedFactMutexSecond = new ArrayBuffer[Int]()

    val adds = new Array[jav.BitSet](maxActions)

    val dels = new Array[jav.BitSet](maxActions)

    val precs = new Array[jav.BitSet](maxActions)

    val producers = Array.fill[jav.BitSet](maxFacts)(new jav.BitSet())

    /**
      * Current position of the last action in the actionSpike.
      */
    var actionIndex = 0

    /**
      * Position of the last action in the previous rank.
      */
    var oldActionIndex = 0

    /**
      * Current position of the last fact in the factSpike.
      */
    var factIndex = 0

    /**
      * Position of the last fact in the previous rank.
      */
    var oldFactIndex = 0

    /**
      * Initialises the data-structures needed to build the graph later.
      * Also set ups the initial state of the problem.
      */
    def init(): Unit = {
      //Fill the unmetPreconditions array and the predicateTaskMap.
      var taskIndex = 0
      while(taskIndex < domain.tasks.length) {
        val task = domain.tasks(taskIndex)
        unmetPreconditions(taskIndex) = task.precondition.length
        // add 0-precs-tasks
        if(unmetPreconditions(taskIndex) == 0) {
          addAction(taskIndex, new Array[Int](0), Array.fill[Int](domain.tasks(taskIndex).parameterSorts.length)(-1))
        }
        var predicateIndex = 0
        while(predicateIndex < task.precondition.length) {
          predicateTaskMap(predicateIndex) += taskIndex
          predicateIndex += 1
        }
        taskIndex += 1
      }
      //Setup the initialState.
      var stateIndex = 0
      while(stateIndex < initialState.length) {
        val fact = initialState(stateIndex)
        addFact(fact._1, fact._2)
        stateIndex += 1
      }
    }

    /**
      * Adds a fact to the graph, if it doesn't already exists.
      * @param predicate Number of the predicate in the domain.predicates-array.
      * @param args Array of Ints, each an index identifying the constant in the respective domain.sortsOfConstant-arrays.
      */
    def addFact(predicate: Int, args: Array[Int]): Unit = {

      val bit = computeBit(predicate, args)
      if(!factStorage.get(bit)) {
        factSpikeIDs(factIndex) = predicate
        factSpikeArgs(factIndex) = args
        predicateFactMap(predicate) += factIndex
        factStorage.set(bit)
        factIndex += 1
      }
    }

    def addActions(task: Int, facts: Array[Int]): Unit = {

      /**
        * Preconditions of the task which need to be met.
        */
      val preconditions = domain.tasks(task).precondition

      /**
        * Every given fact has one ArrayBuffer[Int] which is filled with the indices of the precondition-array that the given fact can meet.
        */
      val candidates = Array.fill[ArrayBuffer[Int]](facts.length)(new ArrayBuffer[Int]())

      /**
        * Facts used to met the preconditions. A fact is represented by an Int as the position of the fact in the factSpike. A fact at the
        * n-th position in the array fulfills the n-th precondition in the precondition-array of the task.
        */
      val usedFacts = new Array[Int](preconditions.length)

      val assignment = Array.fill[Int](domain.tasks(task).parameterSorts.length)(-1)

      /**
        * Finds candidates for the facts-parameter of the addActions-method.
        * A candidate is an Int representing the index of a precondition that fact can fulfill.
        *
        * @return An Array of ArrayBuffers, one for each fact filled with candidates.
        */
      def findCandidates(): Unit = {
        var preconditionIndex = 0
        while (preconditionIndex < preconditions.length) {
          var factIndex = 0
          while (factIndex < facts.length) {
            if (preconditions(preconditionIndex).predicate == factSpikeIDs(factIndex)) {
              candidates(factIndex) += preconditionIndex
            }
            factIndex += 1
          }
          preconditionIndex += 1
        }
      }

      findCandidates()

      /**
        * ##############################################################
        * #                                                            #
        * # WARNING: This is where the instantiation magic happens.    #
        * #          Make changes only if you know what you are doing. #
        * #                                                            #
        * ##############################################################
        */
      var outerNumberOfCombinations = 1;
      {
        var candidateIndex = 0
        while (candidateIndex < candidates.length) {
          outerNumberOfCombinations *= candidates(candidateIndex).length
          candidateIndex += 1
        }
      }
      val outerCounter = new Array[Int](candidates.length)
      val usedPositions = new jav.BitSet(preconditions.length)
      var outerIterator = 0
      while(outerIterator < outerNumberOfCombinations) {
        if(hasValidCandidates) {

          // set usedPositions/usedFacts for the candidates.
          var candidateIndex = 0
          while(candidateIndex < candidates.length) {
            usedPositions.set(candidates(candidateIndex)(outerCounter(candidateIndex)))
            usedFacts(candidates(candidateIndex)(outerCounter(candidateIndex))) = facts(candidateIndex)
            candidateIndex += 1
          }

          // compute the number of combinations for the inner iteration.
          var innerNumberOfCombinations = 1
          var preconditionIndex = 0
          while(preconditionIndex < preconditions.length){
            if(!usedPositions.get(preconditionIndex)) {
              innerNumberOfCombinations *= predicateFactMap(preconditions(preconditionIndex).predicate).length
            }
            preconditionIndex += 1
          }
          val innerCounter = new Array[Int](usedFacts.length)
          var innerIterator = 0
          while(innerIterator < innerNumberOfCombinations){
            // set the usedFact-array to the counter-values.
            // set the assignment
            var uFactIndex = 0
            while(uFactIndex < usedFacts.length){
              if(!usedPositions.get(uFactIndex)){
                usedFacts(uFactIndex) = predicateFactMap(preconditions(uFactIndex).predicate)(innerCounter(uFactIndex))
              }
              uFactIndex += 1
            }
            // if the assignment is valid and the preconditions are mutex free add actions.
            if(isMutexFree) {
              addAction(task, usedFacts, assignment)
            }

            // TODO: Add ignore-clause for candidate-positions.
            var counterIndex = innerCounter.length - 1
            while(counterIndex > 0) {
              if((innerCounter(counterIndex) + 1) < predicateFactMap(preconditions(counterIndex).predicate).length){
                innerCounter(counterIndex) += 1
                counterIndex = 0
              }
              innerCounter(counterIndex) = 0
              counterIndex -= 1
            }
            innerIterator += 1
          }
        }

        var counterIndex = outerCounter.length - 1
        while(counterIndex > 0 ) {
          if((outerCounter(counterIndex) + 1) < candidates(counterIndex).length) {
            outerCounter(counterIndex) += 1
            counterIndex = 0
          }
          outerCounter(counterIndex) = 0
          counterIndex -= 1
        }
        outerIterator += 1
      }

      def isMutexFree: Boolean = {
        true
      }

      def hasValidCandidates: Boolean = {
        val tester = candidates(0)(outerCounter(0))
        var valid = true
        var candidateIndex = 1
        while(candidateIndex < candidates.length) {
          if(tester == candidates(candidateIndex)(outerCounter(candidateIndex))) {
            candidateIndex = candidates.length
            valid = false
          }
          candidateIndex += 1
        }
        valid
      }
    }

    def addAction(taskIndex: Int, preconditions: Array[Int], assignment: Array[Int]): Unit = {
      def instantiateAction(finalAssignment: Array[Int]): Unit ={
        actionSpikeIDs(actionIndex) = taskIndex
        actionSpikeArgs(actionIndex) = finalAssignment
        // set precs
        var precIndex = 0
        while(precIndex < preconditions.length) {
          val prec = preconditions(precIndex)
          precs(actionIndex).set(computeBit(factSpikeIDs(prec), factSpikeArgs(prec)))
          precIndex += 1
        }
        var effIndex = 0
        val task = domain.tasks(taskIndex)
        while(effIndex < task.effect.length){
          val args = new Array[Int](task.effect(effIndex).parameterVariables.length)
          var argIndex = 0
          while(argIndex < args.length){
            args(argIndex) = finalAssignment(task.effect(effIndex).parameterVariables(argIndex))
            argIndex += 1
          }
          addFact(task.effect(effIndex).predicate, args)
          val bit = computeBit(task.effect(effIndex).predicate, args)
          if(task.effect(effIndex).isPositive) {
            adds(actionIndex).set(bit)
          } else {
            dels(actionIndex).set(bit)
          }
          effIndex += 1
        }
        actionIndex += 1
      }

      val unassigned = new ArrayBuffer[Int]()
      var assignmentIndex = 0
      while(assignmentIndex < assignment.length) {
        if(assignment(assignmentIndex) == -1) {
          unassigned += assignmentIndex
        }
        assignmentIndex += 1
      }
      if(unassigned.nonEmpty){
        var numberOfCombinations = 1
        var predicateIndex = 0
        while(predicateIndex < unassigned.length) {
          numberOfCombinations *= domain.sortsOfConstant(domain.tasks(taskIndex).parameterSorts(unassigned(predicateIndex))).length
          predicateIndex += 1
        }
        val counter = Array.fill[Int](unassigned.length)(0)
        var iterator = 0
        while(iterator < numberOfCombinations) {
          val updatedAssignment = assignment
          var uIndex = 0
          while(uIndex < unassigned.length) {
            updatedAssignment(unassigned(uIndex)) = domain.sortsOfConstant(domain.tasks(taskIndex).parameterSorts(unassigned(predicateIndex)))(counter(uIndex))
            uIndex += 1
          }
          instantiateAction(updatedAssignment)
          var counterIndex = counter.length - 1
          while(counterIndex > 0 ) {
            if((counter(counterIndex) + 1) < domain.sortsOfConstant(domain.tasks(taskIndex).parameterSorts(unassigned(predicateIndex))).length) {
              counter(counterIndex) += 1
              counterIndex = 0
            }
            counter(counterIndex) = 0
            counterIndex -= 1
          }
          iterator += 1
        }
      } else {
        instantiateAction(assignment)
      }
    }

    init()
    var running = true
    while(running) {
      // add new actions based on new facts.
      var factIterator = oldFactIndex
      while(factIterator < factIndex){
        var taskIndex = 0
        val tasks = predicateTaskMap(factSpikeIDs(factIterator))
        while(taskIndex < tasks.length) {
          addActions(tasks(taskIndex), Array[Int](factIterator))
          taskIndex += 1
        }
        factIterator += 1
      }
      // add new actions based on deleted mutexes.
      var mutexIndex = 0
      val delMutexes = deletedFactMutexFirst.length
      while(mutexIndex < delMutexes) {
        // find the tasks that have preconditions with both facts.
        val tasks1 = predicateTaskMap(deletedFactMutexFirst(mutexIndex))
        val tasks2 = predicateTaskMap(deletedFactMutexSecond(mutexIndex))
        var tIndex1 = 0
        var tIndex2 = 0
        var iterating = true
        while(iterating) {
          if(tasks1(tIndex1) == tasks2(tIndex2)) {
            val task = tasks1(tIndex1)
            val facts = Array(deletedFactMutexFirst(mutexIndex), deletedFactMutexSecond(mutexIndex))
            addActions(task, facts)
            tIndex1 += 1
            tIndex2 += 1
          } else {
            if(tasks1(tIndex1) > tasks2(tIndex2)) {
              tIndex2 += 1
            } else {
              tIndex1 += 1
            }
          }
          if((tIndex1 > tasks1.length) || (tIndex2 > tasks2.length)) {
            iterating = false
          }
        }
        mutexIndex += 1
      }
    }
    // add action mutexes
    var oActionIterator = oldActionIndex
    while(oActionIterator < actionIndex) {
      var iActionIterator = 0
      while(iActionIterator < oActionIterator) {
        //permanent
        val action1 = adds(oActionIterator)
        action1.or(precs(oActionIterator))
        val action2 = adds(iActionIterator)
        action2.or(precs(iActionIterator))
        if(action1.intersects(dels(iActionIterator)) || action2.intersects(dels(oActionIterator))){
          permanentActionMutexes(iActionIterator).set(oActionIterator)
          permanentActionMutexes(oActionIterator).set(iActionIterator)
        } else {
          //temporary
          val action1precs = precs(oActionIterator).toLongArray
          val precmutexes = factMutexes(action1precs(1).toInt)
          var precIndex = 1
          while(precIndex < action1precs.length) {
            precmutexes.or(factMutexes(action1precs(precIndex).toInt))
            precIndex += 1
          }
          if(precmutexes.intersects(precs(iActionIterator))){
            temporaryActionMutexes(iActionIterator).set(oActionIterator)
            temporaryActionMutexes(oActionIterator).set(iActionIterator)
          }
        }
        iActionIterator += 1
      }
      oActionIterator += 1
    }

    // add fact mutexes
    var oFactIterator = factIndex
    while(oFactIterator > oldFactIndex) {
      var iFactIterator = oldFactIndex -1
      while(iFactIterator > 0) {
        val producers1 = producers(oFactIterator).toLongArray
        val allMutexes = new jav.BitSet()
        allMutexes.or(permanentActionMutexes(producers1(0).toInt))
        allMutexes.or(temporaryActionMutexes(producers1(0).toInt))
        var producerIndex = 1
        while(producerIndex < producers1.length) {
          val mutexes = permanentActionMutexes(producers1(producerIndex).toInt)
          mutexes.or(temporaryActionMutexes(producers1(producerIndex).toInt))
          allMutexes.and(mutexes)
          producerIndex += 1
        }
        if(allMutexes.intersects(producers(iFactIterator))) {
          factMutexes(iFactIterator).set(oFactIterator)
          factMutexes(oFactIterator).set(iFactIterator)
        }
        iFactIterator -= 1
      }
      oFactIterator -= 1
    }
    actionRankFirst += oldActionIndex
    actionRankLast += actionIndex
    factRankFirst += oldFactIndex
    factRankLast += factIndex
    // check if next layer should be build
    if((oldFactIndex == factIndex) || deletedFactMutexFirst.isEmpty) {
      running = false
    }
    oldActionIndex = actionIndex
    oldFactIndex = factIndex
  }

  /**
    * Computes the maximum number of actions that can theoretically be instantiated in the domain.
    * @return The maximum number of actions.
    */
  private def computeMaxActions(): Int = {
    var maxActions = 0
    var taskIndex = 0
    while(taskIndex < domain.tasks.length){
      val args = domain.tasks(taskIndex).parameterSorts
      var sortIndex = 0
      while(sortIndex < args.length) {
        maxActions += domain.sortsOfConstant(args(sortIndex)).length
        sortIndex += 1
      }
      taskIndex += 1
    }
    maxActions
  }

  /**
    * Computes the maximum number of facts that can theoretically be true in the domain.
    * @return The maximum number of facts.
    */
  private def computeMaxFacts(): Int = {
    var maxFacts = 0
    var predicateIndex = 0
    while(predicateIndex < domain.predicates.length) {
      val args = domain.predicates(predicateIndex)
      var sortIndex = 0
      while(sortIndex < args.length){
        maxFacts += domain.sortsOfConstant(args(sortIndex)).length
        sortIndex += 1
      }
      predicateIndex += 1
    }
    maxFacts
  }

  /**
    * Computes the bit of a given fact.
    * The bit is computed like this:
    * - n is the number of predicates in the domain (domain.predicates.length).
    * - p is the number of the predicate the given fact contains (the index in the domain.predicates-array).
    * - b is the sum of every predicate in the args-array, and each predicate is multiplied
    *   with the number of combinations of the previous predicates in the array.
    * The resulting bit is b * n + p.
    * @param predicate Predicate of the fact, represented by the index in the domain.predicates-array.
    * @param args Arguments of the fact, represented by an array of Ints each a constant of a sort.
    * @return Returns the bit corresponding to the given fact.
    */
  private def computeBit(predicate: Int, args: Array[Int]): Int = {
    var bit = 0
    var predicateIndex = 0
    var acc = 1
    while(predicateIndex < domain.predicates(predicate).length) {
      bit += args(predicateIndex) * acc
      acc *= domain.sortsOfConstant(domain.predicates(predicate)(predicateIndex)).length
      predicateIndex += 1
    }
    bit * domain.predicates.length + predicate
  }
}
