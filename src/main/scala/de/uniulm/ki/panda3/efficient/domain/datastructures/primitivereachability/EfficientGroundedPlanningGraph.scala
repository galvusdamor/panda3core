// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability

import java.{util => jav}

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, HashMap}

case class EGPGConfiguration(serial: Boolean = false,
                             computeMutexes: Boolean = true,
                             disallowedTasks: Array[Int] = Array[Int](),
                             disallowedActions: Array[(Int, Array[Int])] = Array[(Int, Array[Int])]()) {}

/**
  * Implements the efficient variant of the planning graph.
  *
  * Information about the algorithm and inspiration for implementing it can be found here:
  * - A. Blum and M. Furst (1997). Fast planning through planning graph analysis
  * article{blum1997fast,
  * title={Fast planning through planning graph analysis},
  * author={Blum, Avrim L and Furst, Merrick L},
  * journal={Artificial intelligence},
  * volume={90},
  * number={1-2},
  * pages={281--300},
  * year={1997},
  * publisher={Elsevier}
  * }
  *
  * - M. Fox and D.Long (1999). Efficient Implementation of the Plan Graph in STAN
  * article{long1999efficient,
  * title={Efficient implementation of the plan graph in STAN},
  * author={Long, Derek and Fox, Maria},
  * journal={Journal of Artificial Intelligence Research},
  * volume={10},
  * pages={87--115},
  * year={1999}
  * }
  *
  * ## Ranks
  * Ranks despict the layers of the graph. Since an action that was instantiated in one layer is available in all following
  * and a fact that comes true is also true in all following layers we do not need to store actions and facts of each layer seperately.
  * We can simply store them in big arrays and keep track of the first and last index of actions/facts of each layer.
  *
  * ## Spikes
  * The planning graphs actions and facts are arranged in "spike" arrays rather than the typical layered structure.
  * Each spike consists of two arrays. One containing an ID, the other an array of arguments
  * for each node in the graph. For actions it is the task-ID and the arguments array is filled with
  * constants as parameters of the task.
  * The fact spike has an ID array representing the predicate ID and the arguments array is filled with
  * constants as parameters of the predicate.
  *
  * ## Mutexes
  * We keep track of which facts are used as preconditions for an action and which effects an action has by storing indices in the factSpike of facts
  * in BitSets. Each action has three BitSets:
  *  - precs(a): A bit is set if the corresponding fact is a precondition of action a.
  *  - adds(a): A bit is set if the corresponding fact is an add-effect of action a.
  *  - dels(a): A bit is set if the corresponding fact is a delete-effect of action a.
  *
  * Action mutexes are computed by simply checking the unions and intersections of those bitsets.
  * More information about mutex computation with sets can be found in M. Fox and D. Long (1999).
  *
  * ## Performance assumptions 
  * General assumptions about performance in scala.
  * - java.util.BitSets are faster than their scala equivalent.
  * - While-loops are the fastest scala loops.
  * (a nice example can be found in the source code of the sameElements-method)
  * - In general it is faster to manually clear an array rather than creating a new one.
  * - Prepending elements in ArrayBuffers is faster than appending them.
  *
  * ## Tests
  * Test are available in "src/test"
  * General test cases can be found at:
  * - "src/test/scala/de/uniulm/ki/panda3/efficient/domain/datastructures/primitivereachability/GroundedEfficientPlanningGraphTest.scala"
  * Test to compare this implementation to the symbolic one can be found at:
  * - "src/test/scala/de/uniulm/ki/panda3/efficient/domain/datastructures/primitivereachability/GroundedPlanningGraphImplementationComparisionTest.scala"
  *
  * ## TODOs
  * - improve comment coverage
  * - establish a nomenclature, especially for variable names
  * - make documentation javadoc conform
  *
  * @constructor Creates a new planning graph for the given domain, initial state and configuration.
  * @param domain       Domain containing all important information for the graph.
  * @param initialState #Set of grounded literals representing the initial state.
  * @param config       Configuration determining how the graph will be computed.
  */
case class EfficientGroundedPlanningGraphImplementation(domain: EfficientDomain,
                                                        initialState: Array[(Int, Array[Int])],
                                                        config: EGPGConfiguration = EGPGConfiguration()) {

  /**
    * Maximum number of facts that could be true in the domain.
    */
  private val maxFacts = computeMaxFacts()
  /**
    * Maximum number of actions that could be instantiated in the domain.
    */
  private val maxActions = computeMaxActions()
  /**
    * Array containing the taskIndex of each action.
    * The taskIndex is the index of the task in the domain.tasks-array.
    *
    */
  val actionSpikeIDs: Array[Int] = Array.fill[Int](maxActions)(-1)
  /**
    * Array containing the arguments of each action.
    * The arguments persist of an array of constants while each constant is an Int.
    * The Ints are Indices of the sort's constant-array.
    * The sort of each argument is determined by the task.
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
  val factSpikeIDs: Array[Int] = Array.fill[Int](maxFacts)(-1)

  /*
   * ################
   * #              #
   * #  Public API  #
   * #              #
   * ################
   *
   */
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
    * ArrayBuffer containing fact mutexes for each layer.
    * Each layer has an Array containing BitSets for every fact in that layer.
    * A bit is set in the BitSet if the corresponding fact (represented by its factSpike-Index) is mutex with the fact
    * the BitSet belongs to.
    * factMutexes(i)(j).get(k) => true | means that in the i-th layer facts j and k are mutex.
    */
  val factMutexes: ArrayBuffer[Array[jav.BitSet]] = new ArrayBuffer[Array[jav.BitSet]]()
  /**
    * ArrayBuffer containing fact mutexes for each layer.
    * Each layer has an Array containing ArrayBuffers for every fact in that layer.
    * The Ints in an ArrayBuffer mean that their corresponding fact in that layer is mutex with the fact
    * the ArrayBuffer belongs to.
    * factMutexesAB(i)(j).contains(k) => true | means that in the i-th layer facts j and k are mutex.
    */
  val factMutexesAB: ArrayBuffer[Array[ArrayBuffer[Int]]] = new ArrayBuffer[Array[ArrayBuffer[Int]]]()
  /**
    * ArrayBuffer containing permanent action mutexes for each layer.
    * Every layer has an Array containing BitSets for each action of the layer.
    * A bit is set in the BitSet if the corresponding action (represented by its actionSpike-Index) is mutex
    * with the action the BitSet belongs to.
    * permanentActionMutexes(i)(j).get(k) => true | means that in the i-th layer actions j and k are permanently mutex.
    */
  val permanentActionMutexes: ArrayBuffer[Array[jav.BitSet]] = new ArrayBuffer[Array[jav.BitSet]]()
  /**
    * ArrayBuffer containing temporary action mutexes for each layer.
    * Every layer has an Array containing ArrayBuffers for each action of the layer.
    * The Ints in an ArrayBuffer mean that their corresponding action in that layer is mutex with the action
    * the ArrayBuffer belongs to.
    * temporaryActionMutexes(i)(j).contains(k) => true | means that in the i-th layer actions j and k are temporally mutex.
    */
  val temporaryActionMutexes: ArrayBuffer[Array[jav.BitSet]] = new ArrayBuffer[Array[jav.BitSet]]()
  /**
    * ArrayBuffer containing temporary action mutexes for each layer.
    * Each layer has an Array containing ArrayBuffers for every action in that layer.
    * The Ints in an ArrayBuffer mean that their corresponding action in that layer is mutex with the action
    * the ArrayBuffer belongs to.
    * temporaryActionMutexesAB(i)(j).contains(k) => true | means that in the i-th layer actions j and k are temporally mutex.
    */
  val temporaryActionMutexesAB: ArrayBuffer[Array[ArrayBuffer[Int]]] = new ArrayBuffer[Array[ArrayBuffer[Int]]]()
  /**
    * Array containing the layer of first appearance for each fact.
    * facts are represented by their index in the factSpike, the value represents the layer a fact first becomes true.
    * firstAppearanceOfFacts(i) => k | means that fact i becomes true for the first time in the k-th layer.
    */
  val firstAppearanceOfFacts: Array[Int] = Array.fill[Int](maxFacts)(-1)
  /**
    * Array containing all actions (represented by their indices in the actionSpike) for every fact that achieved the fact
    * in the layer they first became true.
    * firstAchieversOfFacts(i).contains(k) => true | means that action k is one of the actions that achieved fact i in its
    * first layer of appearance.
    */
  val firstAchieversOfFacts: Array[ArrayBuffer[Int]] = new Array[ArrayBuffer[Int]](maxFacts)
  /**
    * Array containing the add-effects for every action in the actionSpike.
    * Each action has a BitSet and if a bit is set the action has the fact that is represented by the bit as an add-effect.
    * adds(i).get(k) => true | means that fact k is an add-effect of action i.
    */
  val adds = new Array[jav.BitSet](maxActions)
  /**
    * Array containing the delete-effects for every action in the actionSpike.
    * Each action has a BitSet and if a bit is set the action has the fact that is represented by the bit as an delete-effect.
    * dels(i).get(k) => true | means that fact k is a delete-effect of action i.
    */
  val dels = new Array[jav.BitSet](maxActions)
  /**
    * Array containing the preconditions for every action in the actionSpike.
    * Each action has a BitSet and if a bit is set the fact that is represented by the bit is a precondition of the action.
    * precs(i).get(k) => true | means that fact k is a precondition of action i.
    */
  val precs = new Array[jav.BitSet](maxActions)
  /**
    * Array containing the preconditions for every action in the actionSpike.
    * Each action has an ArrayBuffer containing Ints which are precondition of the action.
    * precs(i).contains(k) => true | means that fact k is a precondition of action i.
    */
  val precsAB = new Array[ArrayBuffer[Int]](maxActions)

  /**
    * Number of layers in the graph.
    */
  var graphSize: Int = 0

  /**
    * Array storing an ArrayBuffer for each predicate of the domain.
    * Every ArrayBuffer contains the indices (in the factSpike) of every fact that contains the specific predicate.
    */
  private val predicateFactMap: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())

  def getIndexOfFact(predicate: Int, args: Array[Int]): Int = {
    var index = -1
    val factsToConsider = predicateFactMap(predicate)
    var factIterator = 0
    val numOfFacts = factsToConsider.length
    while(factIterator < numOfFacts && !factSpikeArgs(factsToConsider(factIterator)).sameElements(args)) factIterator += 1
    if(factIterator < numOfFacts) {
      index = factIterator
    }
    index
  }


  /*
   * ###############################
   * #                             #
   * #  Private Methods/Variables  #
   * #                             #
   * ###############################
   */

  /**
    * Method that actually builds the planning graph.
    */
  //noinspection ScalaStyle
  private def build(): Unit = {

    var counter = 0

    /**
      * Array containing the number of unmet preconditions for every task.
      * A precondition is unmet if no fact exists in the factSpike containing the precondition's predicate.
      * unmetPreconditions(i) => k | means that task i has k unmet preconditions currently.
      */
    val unmetPreconditions: Array[Int] = Array.fill[Int](domain.tasks.length)(-1)

    /**
      * Array containing ArrayBuffers for every predicate of the domain.
      * The indices of the array represent the index of the predicate in the domain.predicates-array.
      * Every predicate therefore has one ArrayBuffer containing the indices of every task that has the specific predicate as
      * a precondition. The index of a task is his position in the domain.tasks-array.
      */
    val predicateTaskMap: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())


    /**
      * First fact of a mutex-pair that got deleted.
      */
    val deletedFactMutexFirst = new ArrayBuffer[Int]()

    /**
      * Second fact of a mutex-pair that got deleted.
      */
    val deletedFactMutexSecond = new ArrayBuffer[Int]()

    val producers = Array.fill[jav.BitSet](maxFacts)(new jav.BitSet())

    val producersAB = Array.fill[ArrayBuffer[Int]](maxFacts)(new ArrayBuffer[Int]())

    val consumersAB = Array.fill[ArrayBuffer[Int]](maxFacts)(new ArrayBuffer[Int]())

    val updatedFacts = new ArrayBuffer[Int]()

    val disAllowedActions = if (config.disallowedActions.isEmpty) Array.empty[ArrayBuffer[Array[Int]]] else Array.fill[ArrayBuffer[Array[Int]]](domain.tasks.length)(new ArrayBuffer[Array[Int]]())

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
      * predicateConstantFactMap(predicate)(argIndex)(constant) -> ArrayBuffer
      */
    val predicateConstantFactMap: Array[Array[HashMap[Int, ArrayBuffer[Int]]]] = new Array[Array[HashMap[Int, ArrayBuffer[Int]]]](domain.predicates.length)


    /**
      * index into array is the sort, key of map is constant, value is index in the constantsOfSortsArray.
      * TODO: Find the purpose behind this data structure and why we would need it.
      */
    val constantToIndexMap: Array[mutable.Map[Int, Int]] = Array.fill[mutable.Map[Int, Int]](domain.constantsOfSort.length)(mutable.Map())

    val taskActionsABs = Array.fill[ArrayBuffer[Int]](domain.tasks.length)(new ArrayBuffer[Int]())

    /**
      * Index is predicate, ArrayBuffer contains index into actionSpike.
      */
    val delBackLogActionIndex: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())

    /**
      * Index is predicate, ArrayBuffer contains index into effect-array.
      */
    val delBackLogEffectIndex: Array[ArrayBuffer[Array[Int]]] = Array.fill[ArrayBuffer[Array[Int]]](domain.predicates.length)(new ArrayBuffer[Array[Int]]())

    /**
      * Initialises the data-structures needed to build the graph
      * and instantiate tasks without any preconditions.
      * Also sets up the initial state of the problem.
      */
    def init(): Unit = {
      /*
       * Iterate all tasks of them domain in order to setup the unmetPreconditions array and the predicateTaskMap
       */
      var taskIndex = 0
      while (taskIndex < domain.tasks.length) {
        var allowed = true
        var disTaskIndex = 0
        while (disTaskIndex < config.disallowedTasks.length) {
          if (taskIndex == config.disallowedTasks(disTaskIndex)) {
            allowed = false
          }
          disTaskIndex += 1
        }
        val task = domain.tasks(taskIndex)
        if (task.isPrimitive) {
          if (!task.initOrGoalTask && allowed) {
            unmetPreconditions(taskIndex) = task.precondition.length
            var predicateIndex = 0
            while (predicateIndex < task.precondition.length) {
              predicateTaskMap(task.precondition(predicateIndex).predicate) += taskIndex
              predicateIndex += 1
            }
          }
        }
        taskIndex += 1
      }

      /*
       * Iterate all sorts in order to setup the constantToIndexMap.
       */
      var sIndex = 0
      while (sIndex < domain.constantsOfSort.length) {
        var cIndex = 0
        while (cIndex < domain.constantsOfSort(sIndex).length) {
          val constant = domain.constantsOfSort(sIndex)(cIndex)
          constantToIndexMap(sIndex) += ((constant, cIndex))
          cIndex += 1
        }
        sIndex += 1
      }
      /*
       * Transform disAllowedActions in to a better data-structure.
       */
      var dAIndex = 0
      while (dAIndex < config.disallowedActions.length) {
        var dAction = config.disallowedActions(dAIndex)
        disAllowedActions(dAction._1).+=:(dAction._2)
        dAIndex += 1
      }
      /*
       * Setup the initial state.
       */
      var stateIndex = 0
      while (stateIndex < initialState.length) {
        val fact = initialState(stateIndex)
        addFact(fact._1, fact._2)
        stateIndex += 1
      }
      factMutexes += Array.fill[jav.BitSet](factIndex + 1)(new jav.BitSet())
      factMutexesAB += Array.fill[ArrayBuffer[Int]](factIndex + 1)(new ArrayBuffer[Int]())
      permanentActionMutexes += Array.fill[jav.BitSet](actionIndex + 1)(new jav.BitSet())
      temporaryActionMutexes += Array.fill[jav.BitSet](actionIndex + 1)(new jav.BitSet())
      temporaryActionMutexesAB += Array.fill[ArrayBuffer[Int]](actionIndex + 1)(new ArrayBuffer[Int]())
    }

    def doesActionExist(taskID: Int, args: Array[Int]): Int = {
      var actionExists = -1
      val actions = taskActionsABs(taskID)
      var actionIterator = 0
      while (actionIterator < actions.length) {
        var same = true
        val actionIndex = actions(actionIterator)
        val arguments = actionSpikeArgs(actionIndex)
        var argIterator = 0
        while (argIterator < args.length && args(argIterator) == arguments(argIterator)) argIterator += 1
        if (argIterator != args.length) same = false
        if (same) {
          actionExists = actionIndex
          actionIterator = actions.length
        }
        actionIterator += 1
      }
      actionExists
    }

    def doesFactExists(predicate: Int, args: Array[Int]): Int = {
      var factExists = -1
      val facts = predicateFactMap(predicate)
      var factIterator = 0
      while (factIterator < facts.length) {
        var same = true
        val factIndex = facts(factIterator)
        val arguments = factSpikeArgs(factIndex)
        var argIterator = 0
        while (argIterator < args.length) {
          val arg1 = args(argIterator)
          val arg2 = arguments(argIterator)
          if (arg1 != arg2) {
            same = false
            argIterator = args.length
          }
          argIterator += 1
        }
        if (same) {
          factExists = factIndex
          factIterator = facts.length
        }
        factIterator += 1
      }
      factExists
    }

    /**
      * Adds a fact to the graph, if it doesn't already exists.
      *
      * @param predicate Number of the predicate in the domain.predicates-array.
      * @param args      Array of Ints, each an index identifying the constant in the respective domain.sortsOfConstant-arrays.
      * @return true if the fact was added, false if not.
      */
    def addFact(predicate: Int, args: Array[Int]): Int = {
      val exists = doesFactExists(predicate, args)
      if (exists != -1) {
        exists
      } else {
        factSpikeIDs(factIndex) = predicate
        factSpikeArgs(factIndex) = args
        predicateFactMap(predicate).+=:(factIndex)
        if (predicateFactMap(predicate).length == 1) {
          predicateConstantFactMap(predicate) = Array.fill[HashMap[Int, ArrayBuffer[Int]]](args.length)(new mutable.HashMap[Int, ArrayBuffer[Int]]())
          val tasks = predicateTaskMap(predicate)
          var taskIterator = 0
          val numOfTasks = tasks.length
          while (taskIterator < numOfTasks) {
            val taskID = tasks(taskIterator)
            unmetPreconditions(taskID) -= 1
            taskIterator += 1
          }
        }
        var argIterator = 0
        val numOfArgs = args.length
        while (argIterator < numOfArgs) {
          val constant = args(argIterator)
          val map = predicateConstantFactMap(predicate)(argIterator)
          if (!map.contains(constant)) {
            map.put(constant, new ArrayBuffer[Int]())
          }
          val buffer = map(constant)
          buffer += factIndex
          argIterator += 1
        }
        /*
         * Check backlog for potential dels to set.
         */
        val actions = delBackLogActionIndex(predicate)
        val effects = delBackLogEffectIndex(predicate)
        val numOfIterations = actions.length
        var iterator = 0
        while (iterator < numOfIterations) {

          /*
           * Compare arguments for same constants.
           */
          val action = actions(iterator)
          val effectArgs = effects(iterator)

          val numOfArgs = args.length
          var argIterator = 0
          while (argIterator < numOfArgs && args(argIterator) == effectArgs(argIterator)) argIterator += 1
          if (argIterator == numOfArgs) {
            delBackLogActionIndex(predicate).remove(iterator)
            delBackLogEffectIndex(predicate).remove(iterator)
            dels(action).set(factIndex)
          }
          iterator += 1
        }
        factIndex += 1
        factIndex - 1
      }
    }

    def addActions(taskID: Int, facts: Array[Int]): Unit = {

      val task = domain.tasks(taskID)


      /**
        * Preconditions of the task which need to be met.
        */
      val preconditions = task.precondition

      /**
        * Every given fact has one ArrayBuffer[Int] which is filled with the indices of the precondition-array that the given fact can fulfill.
        */
      val candidates = Array.fill[ArrayBuffer[Int]](facts.length)(new ArrayBuffer[Int]())

      val usableFacts = new Array[ArrayBuffer[Int]](preconditions.length)

      /**
        * Facts that are used to fulfill preconditions. A fact is represented by an Int as the position of the fact in the factSpike. A fact at the
        * n-th position in the array fulfills the n-th precondition in the precondition-array of the task.
        */
      val usedFacts: Array[Int] = Array.fill[Int](preconditions.length)(-1)
      val assignment = Array.fill[Int](task.parameterSorts.length)(-1)
      val assignmentCounter = Array.fill(task.parameterSorts.length)(0)
      val singleFact = facts.length == 1

      def isMutexFree(index: Int): Boolean = {
        var mutexFree = config.computeMutexes
        if (mutexFree) {
          val fact = usedFacts(index)
          var uFIndex = 0
          val mutexes = factMutexes.last(fact)
          while (uFIndex < usedFacts.length && mutexFree) {
            if (uFIndex != index && usedFacts(uFIndex) >= 0) {
              if (mutexes.get(usedFacts(uFIndex))) {
                mutexFree = false
              }
            }
            uFIndex += 1
          }
          mutexFree
        } else {
          true
        }
      }

      /**
        * checks if the given fact has the correct constant sorts for a precondition.
        *
        * @param fIndex
        * @param precIndex
        * @return
        */
      def areSortsValid(fIndex: Int, precIndex: Int): Boolean = {
        var valid = true
        val precondition = task.precondition(precIndex)
        var argIterator = 0
        val numOfArgs = precondition.parameterVariables.length
        while (argIterator < numOfArgs) {
          val constant = factSpikeArgs(fIndex)(argIterator)
          // TODO: Is it necessary to check the argSort? I think not!
          //val argSort = domain.predicates(predicate)(argIterator)
          val parameterSort = task.parameterSorts(precondition.parameterVariables(argIterator))
          // TODO: Do we really have to use the constantToIndexMap?
          if (!constantToIndexMap(parameterSort).contains(constant)) {
            valid = false
          }
          argIterator += 1
        }
        valid
      }

      /**
        * Finds candidates for the facts-parameter of the addActions-method.
        * A candidate is an Int representing the index of a precondition that fact can fulfill.
        */
      def setup(): Unit = {
        var preconditionIndex = 0
        while (preconditionIndex < preconditions.length) {
          val precondition = preconditions(preconditionIndex)
          val predicate = precondition.predicate
          var factIterator = 0
          val numOfFacts = facts.length
          while (factIterator < numOfFacts) {
            val fact = facts(factIterator)
            if (predicate == factSpikeIDs(fact) && areSortsValid(fact, preconditionIndex)) {
              candidates(factIterator) += preconditionIndex
            }
            factIterator += 1
          }
          usableFacts(preconditionIndex) = predicateFactMap(predicate)
          preconditionIndex += 1
        }
      }


      def iteratePrecCombinations(index: Int): Unit = {

        // check if the precondition is already used by the original facts
        if (usedFacts(index) == -1) {
          var uFIndex = 0
          // iterate usable facts
          while (uFIndex < usableFacts(index).length) {
            var canBeUsed = true

            val fact = usableFacts(index)(uFIndex)
            if (singleFact && fact < facts(0)) {
              canBeUsed = false
            }
            if (canBeUsed) {
              usedFacts(index) = fact
              if (setAssignment(fact, index) && firstAppearanceOfFacts(fact) >= 0 && isMutexFree(index)) {
                if (index == usedFacts.length - 1) {
                  addAction(taskID, usedFacts, assignment)
                } else {
                  iteratePrecCombinations(index + 1)
                }
                unsetAssignment(fact, index)
              }
            }
            uFIndex += 1
          }
        } else {
          if (index == usedFacts.length - 1) {
            addAction(taskID, usedFacts, assignment)
          } else {
            iteratePrecCombinations(index + 1)
          }
        }
      }

      def setAssignment(factIndex: Int, preconditionIndex: Int): Boolean = {
        var success = true
        val precondition = preconditions(preconditionIndex)
        val args = factSpikeArgs(factIndex)
        var argIterator = 0
        val numOfArgs = args.length
        while (argIterator < numOfArgs) {
          val constant = args(argIterator)
          val variable = precondition.parameterVariables(argIterator)
          if ((assignment(variable) == -1 || assignment(variable) == constant) && areSortsValid(factIndex, preconditionIndex)) {
            assignment(variable) = constant
            assignmentCounter(variable) += 1
          } else {
            success = false
            var reverseIterator = argIterator
            while (reverseIterator >= 0) {
              val variable = precondition.parameterVariables(reverseIterator)
              assignmentCounter(variable) -= 1
              if (assignmentCounter(variable) == 0) {
                assignment(variable) = -1
              }
              reverseIterator -= 1
            }
            argIterator = numOfArgs
          }
          argIterator += 1
        }
        success
      }

      def unsetAssignment(factIndex: Int, preconditionIndex: Int): Unit = {
        val precondition = preconditions(preconditionIndex)
        val args = factSpikeArgs(factIndex)
        var argIterator = 0
        val numOfArgs = args.length
        while (argIterator < numOfArgs) {
          val variable = precondition.parameterVariables(argIterator)
          assignmentCounter(variable) -= 1
          if (assignmentCounter(variable) == 0) {
            assignment(variable) = -1
          }
          argIterator += 1
        }
      }


      def iterateCandidatesCombinations(index: Int): Unit = {
        var candidateIterator = 0
        val numOfCandidatesForIndex = candidates(index).length
        while (candidateIterator < numOfCandidatesForIndex) {
          val potentialIndex = candidates(index)(candidateIterator)
          if (usedFacts(potentialIndex) == -1) {
            if (setAssignment(facts(index), potentialIndex)) {
              usedFacts(potentialIndex) = facts(index)
              if (index == candidates.length - 1) {
                iteratePrecCombinations(0)
              } else {
                iterateCandidatesCombinations(index + 1)
              }
              usedFacts(potentialIndex) = -1
              unsetAssignment(facts(index), potentialIndex)
            }
          }
          candidateIterator += 1
        }
      }

      setup()
      iterateCandidatesCombinations(0)
    }

    def addAction(taskIndex: Int, preconditions: Array[Int], assignment: Array[Int]): Unit = {


      val task = domain.tasks(taskIndex)

      def getLastIndex(): Int = {
        var lastIndex = assignment.length - 1
        while (lastIndex >= 0 && assignment(lastIndex) != -1) lastIndex -= 1
        lastIndex
      }

      val lastIndex = if (assignment.length > 0) getLastIndex() else 0

      def isAllowed: Boolean = {
        var disallowedIterator = 0
        val disallowedArgs = if (disAllowedActions.isEmpty) ArrayBuffer.empty[Array[Int]] else disAllowedActions(taskIndex)
        val numOfDisallowedArgs = disallowedArgs.length
        while (disallowedIterator < numOfDisallowedArgs) {
          if (assignment.sameElements(disallowedArgs(disallowedIterator))) {
            return false
          }
          disallowedIterator += 1
        }
        true
      }

      def instantiateAction(finalAssignment: Array[Int]): Unit = {
        def areConstraintsFulfilled(): Boolean = {
          var fulfilled = true
          val constraints = task.constraints
          val numOfConstraints = constraints.length
          var constraintIterator = 0
          while (constraintIterator < numOfConstraints && fulfilled) {
            val constraint = constraints(constraintIterator)
            val variable = constraint.variable
            val constant = constraint.other
            if (constraint.constraintType == EfficientVariableConstraint.EQUALCONSTANT) {
              fulfilled = finalAssignment(variable) == constant
            }
            if (constraint.constraintType == EfficientVariableConstraint.UNEQUALCONSTANT) {
              fulfilled = finalAssignment(variable) != constant
            }
            constraintIterator += 1
          }
          fulfilled
        }

        if (areConstraintsFulfilled()) {
          taskActionsABs(taskIndex) += actionIndex
          actionSpikeIDs(actionIndex) = taskIndex
          actionSpikeArgs(actionIndex) = finalAssignment.clone()
          precs(actionIndex) = new jav.BitSet()
          precsAB(actionIndex) = new ArrayBuffer[Int]()
          adds(actionIndex) = new jav.BitSet()
          dels(actionIndex) = new jav.BitSet()
          // set precs
          var precIndex = 0
          while (precIndex < preconditions.length) {
            val prec = preconditions(precIndex)
            precs(actionIndex).set(prec)
            precsAB(actionIndex) += prec
            consumersAB(prec) += actionIndex
            precIndex += 1
          }
          actionIndex += 1
        }
      }

      def iterateParameterCombinations(index: Int): Unit = {
        if (assignment(index) == -1) {
          val sort = task.parameterSorts(index)
          var sIndex = 0
          while (sIndex < domain.constantsOfSort(sort).length) {
            val constant = domain.constantsOfSort(sort)(sIndex)
            assignment(index) = constant
            if (index == lastIndex) {
              if (isAllowed && doesActionExist(taskIndex, assignment) == -1) {
                instantiateAction(assignment)
              }
            } else {
              iterateParameterCombinations(index + 1)
            }
            assignment(index) = -1
            sIndex += 1
          }
        } else {
          if (index == lastIndex) {
            if (isAllowed && doesActionExist(taskIndex, assignment) == -1) {
              instantiateAction(assignment)
            }
          } else {
            iterateParameterCombinations(index + 1)
          }
        }
      }

      var assignmentIterator = 0
      while (assignmentIterator < assignment.length && assignment(assignmentIterator) != -1) assignmentIterator += 1

      if (assignmentIterator != assignment.length && assignment.length > 0) {
        iterateParameterCombinations(0)
      } else {
        if (isAllowed && doesActionExist(taskIndex, assignment) == -1) {
          instantiateAction(assignment)
        }
      }

    }

    def instantiateActionsFromNewFacts(): Unit = {
      var fIndex = oldFactIndex
      while (fIndex < factIndex) {
        var taskIterator = 0
        val tasks = predicateTaskMap(factSpikeIDs(fIndex))
        while (taskIterator < tasks.length) {
          val taskID = tasks(taskIterator)

          if (unmetPreconditions(taskID) == 0) {
            addActions(taskID, Array[Int](fIndex))
          }
          taskIterator += 1
        }
        fIndex += 1
      }
    }

    def instantiateActionsFromDeletedMutexes(): Unit = {
      var mutexIndex = 0
      val nuOfDelMutexes = deletedFactMutexFirst.length
      while (mutexIndex < nuOfDelMutexes) {
        val fact1Index = deletedFactMutexFirst(mutexIndex)
        val fact2Index = deletedFactMutexSecond(mutexIndex)
        val fact1 = factSpikeIDs(fact1Index)
        val fact2 = factSpikeIDs(fact2Index)
        val tasks1 = predicateTaskMap(fact1)
        val tasks2 = predicateTaskMap(fact2)
        var tIndex1 = 0
        var tIndex2 = 0
        while ((tIndex1 < tasks1.length) && (tIndex2 < tasks2.length)) {
          val task1 = tasks1(tIndex1)
          val task2 = tasks2(tIndex2)
          if (task1 == task2) {
            val task = tasks1(tIndex1)
            val facts = Array(fact1Index, fact2Index)
            if (unmetPreconditions(task) == 0) {
              addActions(task, facts)
            }
            tIndex1 += 1
            tIndex2 += 1
          } else {
            if (tasks1(tIndex1) > tasks2(tIndex2)) {
              tIndex2 += 1
            } else {
              tIndex1 += 1
            }
          }
        }
        mutexIndex += 1
      }
    }

    def addFacts(): Unit = {
      oldFactIndex = factIndex
      var actionIterator = oldActionIndex
      while (actionIterator < actionIndex) {
        val taskID = actionSpikeIDs(actionIterator)
        if (taskID < domain.tasks.length) {
          val task = domain.tasks(taskID)
          var effectIterator = 0
          while (effectIterator < task.effect.length) {
            val effect = task.effect(effectIterator)
            val args = new Array[Int](effect.parameterVariables.length)
            var argIterator = 0
            while (argIterator < args.length) {
              args(argIterator) = actionSpikeArgs(actionIterator)(effect.parameterVariables(argIterator))
              argIterator += 1
            }
            if (effect.isPositive) {
              val fIndex = addFact(effect.predicate, args)
              updatedFacts.+=:(fIndex)
              producers(fIndex).set(actionIterator)
              producersAB(fIndex).+=:(actionIterator)
              adds(actionIterator).set(fIndex)
            } else {
              val fIndex = doesFactExists(effect.predicate, args)
              if (fIndex >= 0) {
                dels(actionIterator).set(fIndex)
              } else {
                delBackLogActionIndex(effect.predicate) += actionIterator
                delBackLogEffectIndex(effect.predicate) += args
              }
            }
            effectIterator += 1
          }
        }
        actionIterator += 1
      }
    }

    def addNOOPs(): Unit = {
      var factIterator = oldFactIndex
      while (factIterator < factIndex) {
        createNOOP(factIterator)
        factIterator += 1
      }
    }

    def createNOOP(factIndex: Int): Unit = {
      val taskID = factSpikeIDs(factIndex)
      actionSpikeIDs(actionIndex) = domain.tasks.length + taskID + 1
      actionSpikeArgs(actionIndex) = factSpikeArgs(factIndex)
      val add = new jav.BitSet()
      val del = new jav.BitSet()
      val prec = new jav.BitSet()
      val precAB = new ArrayBuffer[Int]()
      add.set(factIndex)
      precAB += factIndex
      prec.set(factIndex)
      adds(actionIndex) = add
      dels(actionIndex) = del
      precs(actionIndex) = prec
      precsAB(actionIndex) = precAB
      producers(factIndex).set(actionIndex)
      producersAB(factIndex) += actionIndex
      updatedFacts += factIndex
      actionIndex += 1
    }

    /**
      * For new each fact that was added in the current graph layer its first appearance in the graph is stored and all actions that achieve it
      * are stored aswell.
      */
    def setFirstAppearance(): Unit = {
      var index = oldFactIndex
      while (index < factIndex) {
        firstAppearanceOfFacts(index) = factRankFirst.length + 1
        firstAchieversOfFacts(index) = producersAB(index).clone()
        index += 1
      }
    }

    /**
      * Computes fact and action mutexes of the current rank.
      * An exact description how mutex computation works can be found in M. Fox and D. Long (1999).
      *
      */
    def computeMutexes(): Unit = {
      val newFactMutexes = Array.fill[jav.BitSet](factIndex)(new jav.BitSet())
      val newFactMutexesAB = Array.fill[ArrayBuffer[Int]](factIndex)(new ArrayBuffer[Int]())
      val newPermanentActionMutexes = Array.fill[jav.BitSet](actionIndex)(new jav.BitSet())
      val newTemporaryActionMutexes = Array.fill[jav.BitSet](actionIndex)(new jav.BitSet())
      val newTemporaryActionMutexesAB = Array.fill[ArrayBuffer[Int]](actionIndex)(new ArrayBuffer[Int]())

      setupMutexes()
      addActionMutexes()
      deleteActionMutexes()
      addFactMutexes()
      deleteFactMutexes()

      factMutexes += newFactMutexes
      factMutexesAB += newFactMutexesAB
      permanentActionMutexes += newPermanentActionMutexes
      temporaryActionMutexes += newTemporaryActionMutexes
      temporaryActionMutexesAB += newTemporaryActionMutexesAB

      updatedFacts.clear()

      def addActionMutexes(): Unit = {
        var aIndex1 = oldActionIndex
        while (aIndex1 < actionIndex) {
          var aIndex2 = 0
          while (aIndex2 < aIndex1) {
            val mutex = areActionsMutex(aIndex1, aIndex2)
            if (mutex == 1) {
              newTemporaryActionMutexes(aIndex2).set(aIndex1)
              newTemporaryActionMutexes(aIndex1).set(aIndex2)
              newTemporaryActionMutexesAB(aIndex1).+:(aIndex2)
              newTemporaryActionMutexesAB(aIndex2).+:(aIndex1)
            }
            if (mutex == 2) {
              newPermanentActionMutexes(aIndex2).set(aIndex1)
              newPermanentActionMutexes(aIndex1).set(aIndex2)
            }
            aIndex2 += 1
          }
          aIndex1 += 1
        }
      }

      def setupMutexes(): Unit = {
        var fIndex = 0
        while (fIndex < oldFactIndex) {
          newFactMutexes(fIndex) = factMutexes.last(fIndex).clone().asInstanceOf[jav.BitSet]
          newFactMutexesAB(fIndex) = factMutexesAB.last(fIndex).clone()
          fIndex += 1
        }
        var aIndex = 0
        while (aIndex < oldActionIndex) {
          newPermanentActionMutexes(aIndex) = permanentActionMutexes.last(aIndex).clone().asInstanceOf[jav.BitSet]
          newTemporaryActionMutexes(aIndex) = temporaryActionMutexes.last(aIndex).clone().asInstanceOf[jav.BitSet]
          newTemporaryActionMutexesAB(aIndex) = temporaryActionMutexesAB.last(aIndex).clone()
          aIndex += 1
        }
      }

      def areFactsMutex(fact1: Int, fact2: Int): Boolean = {
        val producers1 = producersAB(fact1)
        val producers2 = producersAB(fact2)
        val actionMutexes = new jav.BitSet()
        var producerIndex = 0
        while (producerIndex < producers1.length) {
          val mutexes = new jav.BitSet()
          mutexes.or(newPermanentActionMutexes(producers1(producerIndex)))
          mutexes.or(newTemporaryActionMutexes(producers1(producerIndex)))
          if (actionMutexes.isEmpty) actionMutexes.or(mutexes) else actionMutexes.and(mutexes)
          producerIndex += 1
        }
        var mutex = true
        var pIndex = 0
        while (pIndex < producers2.length) {
          if (!actionMutexes.get(producers2(pIndex))) {
            mutex = false
            pIndex = producers2.length
          }
          pIndex += 1
        }
        mutex
      }

      def areActionsMutex(action1: Int, action2: Int): Int = {
        val numOfTasks = domain.tasks.length
        val task1 = actionSpikeIDs(action1)
        val task2 = actionSpikeIDs(action2)
        if (config.serial && task1 < numOfTasks && task2 < numOfTasks) {
          return 2
        }
        if (areActionsPermanentMutex(action1, action2)) {
          return 2
        }
        if (areActionsTemporaryMutex(action1, action2)) {
          return 1
        }
        0
      }

      def areActionsPermanentMutex(action1: Int, action2: Int): Boolean = {
        val ap1 = new jav.BitSet()
        ap1.or(adds(action1))
        ap1.or(precs(action1))
        if (ap1.intersects(dels(action2))) {
          true
        } else {
          // adds and precs of action2
          val ap2 = new jav.BitSet()
          ap2.or(adds(action2))
          ap2.or(precs(action2))
          if (ap2.intersects(dels(action1))) {
            true
          } else {
            false
          }
        }
      }

      def areActionsTemporaryMutex(action1: Int, action2: Int): Boolean = {
        val precs1 = precsAB(action1)
        val precs2 = precsAB(action2)
        var prec1Index = 0
        while (prec1Index < precs1.length) {
          var prec2Index = 0
          val prec1 = precs1(prec1Index)
          while (prec2Index < precs2.length) {
            val prec2 = precs2(prec2Index)
            if (factMutexes.last(prec2).get(prec1)) {
              return true
            }
            prec2Index += 1
          }
          prec1Index += 1
        }
        false
      }

      def deleteActionMutexes(): Unit = {
        var delFIndex = 0
        while (delFIndex < deletedFactMutexFirst.length) {
          val fact1 = deletedFactMutexFirst(delFIndex)
          val fact2 = deletedFactMutexSecond(delFIndex)
          val consumers1 = consumersAB(fact1)
          val consumers2 = consumersAB(fact2)
          var consumer1Index = 0
          while (consumer1Index < consumers1.length) {
            val action1 = consumers1(consumer1Index)
            val mutexes = newTemporaryActionMutexes(action1)
            var consumer2Index = 0
            while (consumer2Index < consumers2.length) {
              val action2 = consumers2(consumer2Index)
              if (mutexes.get(action2)) {
                if (!areActionsTemporaryMutex(action1, action2)) {
                  deleteTemporaryActionMutex(action1, action2)
                }
              }
              consumer2Index += 1
            }
            consumer1Index += 1
          }
          delFIndex += 1
        }

        def deleteTemporaryActionMutex(action1: Int, action2: Int): Unit = {
          newTemporaryActionMutexes(action1).set(action2, false)
          newTemporaryActionMutexes(action2).set(action1, false)
        }
      }

      def addFactMutexes(): Unit = {
        var fIndex1 = factIndex - 1
        while (fIndex1 >= oldFactIndex) {
          var fIndex2 = fIndex1 - 1
          while (fIndex2 >= 0) {
            if (areFactsMutex(fIndex2, fIndex1)) {
              newFactMutexes(fIndex2).set(fIndex1)
              newFactMutexes(fIndex1).set(fIndex2)
              newFactMutexesAB(fIndex2) += (fIndex1)
              newFactMutexesAB(fIndex1) += (fIndex2)
            }
            fIndex2 -= 1
          }
          fIndex1 -= 1
        }
      }

      def deleteFactMutex(fact1: Int, fact2: Int): Unit = {
        val fact1Mutexes = newFactMutexesAB(fact1)
        val fact2Mutexes = newFactMutexesAB(fact2)
        if (fact1Mutexes.contains(fact2)) {
          newFactMutexes(fact1).set(fact2, false)
          newFactMutexes(fact2).set(fact1, false)
          fact1Mutexes.remove(fact1Mutexes.indexOf(fact2))
          fact2Mutexes.remove(fact2Mutexes.indexOf(fact1))
          deletedFactMutexFirst += fact1
          deletedFactMutexSecond += fact2
        }
      }

      def deleteFactMutexes(): Unit = {
        val uFacts = updatedFacts.distinct
        deletedFactMutexFirst.clear()
        deletedFactMutexSecond.clear()
        var fIndex = 0
        while (fIndex < uFacts.length) {
          val fact1 = uFacts(fIndex)
          if (fact1 < oldFactIndex) {
            val mutexes = factMutexesAB.last(fact1)
            var mIndex = 0
            while (mIndex < mutexes.length) {
              val fact2 = mutexes(mIndex)
              if (!areFactsMutex(fact1, fact2)) {
                deleteFactMutex(fact1, fact2)
              }
              mIndex += 1
            }
          }
          fIndex += 1
        }
      }

    }

    var running = true
    while (running) {
      if (counter == 0) {
        init()
      } else {
        if (counter == 1) {
          var taskIndex = 0
          while (taskIndex < domain.tasks.length) {
            val task = domain.tasks(taskIndex)
            // instantiate 0-precs-tasks
            if (task.precondition.length == 0 && !task.initOrGoalTask) {
              addAction(taskIndex, new Array[Int](0), Array.fill[Int](task.parameterSorts.length)(-1))
            }
            taskIndex += 1
          }

        }

        if (config.computeMutexes) {
          addNOOPs()
        }
        // add new actions based on new facts.
        instantiateActionsFromNewFacts()
        // add new actions based on deleted mutexes.

        if (config.computeMutexes) {
          instantiateActionsFromDeletedMutexes()
        }
        addFacts()
        if (config.computeMutexes) {
          computeMutexes()
        }
      }
      setFirstAppearance()

      actionRankFirst += oldActionIndex
      actionRankLast += actionIndex
      factRankFirst += oldFactIndex
      factRankLast += factIndex
      // check if the building process of the planning graph is done.
      if ((oldFactIndex == factIndex) && deletedFactMutexFirst.isEmpty) {
        running = false
        graphSize = counter + 1
      }
      oldActionIndex = actionIndex
      counter += 1
    }

  }

  /**
    * Computes the maximum number of actions (including NO-OPs) that can theoretically be instantiated in the domain.
    * If this number exceeds Integer.MAX_VALUE it will return Integer.MAX_VALUE instead.
    *
    * @return The maximum number of actions.
    */
  private def computeMaxActions(): Int = {
    var max = false
    var maxActions = 0
    var taskIndex = 0
    while (taskIndex < domain.tasks.length) {
      val args = domain.tasks(taskIndex).parameterSorts
      var sortIndex = 0
      while (sortIndex < args.length) {
        maxActions += domain.constantsOfSort(args(sortIndex)).length
        if (maxActions < 0) {
          max = true
        }
        sortIndex += 1
      }
      if (args.length == 0) {
        maxActions += 1
        if (maxActions < 0) {
          max = true
        }
      }
      taskIndex += 1
    }
    if (max) {
      Integer.MAX_VALUE
    } else {
      if (config.computeMutexes) {
        if (maxFacts == Integer.MAX_VALUE) {
          Integer.MAX_VALUE
        } else {
          maxActions + maxFacts + 1
        }
      } else {
        maxActions
      }
    }
  }

  /**
    * Computes the maximum number of facts that theoretically can become true in the domain.
    * If this number exceeds Integer.MAX_VALUE it will return Integer.MAX_VALUE instead.
    *
    * @return The maximum number of facts.
    */
  private def computeMaxFacts(): Int = {
    var max = false
    var maxFacts = 0
    var predicateIndex = 0
    while (predicateIndex < domain.predicates.length) {
      val args = domain.predicates(predicateIndex)
      var sortIndex = 0
      while (sortIndex < args.length) {
        maxFacts += domain.constantsOfSort(args(sortIndex)).length
        if (maxFacts < 0) {
          max = true
        }
        sortIndex += 1
      }
      predicateIndex += 1
      if (args.length == 0) {
        maxFacts += 1
        if (maxFacts < 0) {
          max = true
        }
      }
    }
    if (max) {
      Integer.MAX_VALUE
    } else {
      maxFacts
    }
  }

  build()
}
