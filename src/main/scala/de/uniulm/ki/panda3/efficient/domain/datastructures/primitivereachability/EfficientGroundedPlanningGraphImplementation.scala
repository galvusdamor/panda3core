package de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability

import java.{util => jav}

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

case class EFGPGConfiguration(serial: Boolean = false,
                              computeMutexes: Boolean = true,
                              disallowedTasks: Array[Int] = Array[Int](),
                              disallowedActions: Array[(Int, Array[Int])] = Array[(Int, Array[Int])]()) {}

/**
  * Implements the efficient variant of the planning graph.
  * Basic information about the algorithm and inspiration for implementing it can be found here:
  * - A. Blum and M. Furst (1997). Fast planning through planning graph analysis
  * - M. Fox and D.Long (1999). Efficient Implementation of the Plan Graph in STAN
  *
  * General assumptions about performance in scala.
  * - java.util.BitSets are faster than their scala equivalent.
  * - While-loops are the fastest scala loops.
  * - In general it is faster to manually clear an array rather than creating a new one.
  *
  *
  *
  *
  *
  * @constructor         Creates a new planning graph for the given domain, initial state and configuration.
  * @param domain        Domain containing all important information for the graph.
  * @param initialState  #Set of grounded literals representing the initial state.
  * @param config        Configuration determining how the graph will be computed.
  */
case class EfficientGroundedPlanningGraphImplementation(domain: EfficientDomain,
                                          initialState: Array[(Int, Array[Int])],
                                          config: EFGPGConfiguration = EFGPGConfiguration()) {


  /**
    * Maximum number of facts that could be true in the domain.
    */
  private val maxFacts = computeMaxFacts()

  /**
    * Maximum number of actions that could be instantiated in the domain.
    */
  private val maxActions = computeMaxActions()

  /*
   * ################
   * #              #
   * #  Public API  #
   * #              #
   * ################
   *
   */

  var graphSize: Int = 0

  /**
    * Array containing the taskIndex of each action.
    * The taskIndex is the index of the task in the domain.tasks-array.
    *
    */
  val actionSpikeIDs: Array[Int] = new Array[Int](maxActions)

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
  val firstAppearanceOfFacts: Array[Int] = new Array[Int](maxFacts)

  /**
    * Array containing all actions (represented by their indices in the actionSpike) for every fact that achieved the fact
    * in the layer they first became true.
    * firstAchieversOfFacts(i).contains(k) => true | means that action k is one of the actions that achieved fact i in its
    *                                                first layer of appearance.
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
      * A BitSet acting as existence test for facts.
      * If a specific bit is set to true, the corresponding fact exists in the factSpike.
      * How facts correspond to bits is explained in the computeFactBit-method.
      */
    val factVault: jav.BitSet = new jav.BitSet()

    /**
      * A Map to find the factSpike-index of a fact given its corresponding bit.
      * factVaultMap.get(i) => k | means that k is the index of a fact with bit i in the factSpike.
      */
    val factVaultMap = new HashMap[Int, Int]()

    /**
      * A BitSet acting as an existence test for actions.
      * If a specific bit is set, the corresponding action exists in the actionSpike.
      * How actions correspond to bits is explained in the computeActionBit-method.
      */
    val actionVault: jav.BitSet = new jav.BitSet()

    /**
      * Array storing an ArrayBuffer for each predicate of the domain.
      * Every ArrayBuffer contains the indices (in the factSpike) of every fact that contains the specific predicate.
      */
    val predicateFactMap: Array[ArrayBuffer[Int]] = Array.fill[ArrayBuffer[Int]](domain.predicates.length)(new ArrayBuffer[Int]())

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

    val disAllowedActions = Array.fill[ArrayBuffer[Array[Int]]](domain.tasks.length)(new ArrayBuffer[Array[Int]]())

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
      * Initialises the data-structures needed to build the graph
      * and instantiate tasks without any preconditions.
      * Also sets up the initial state of the problem.
      */
    def init(): Unit = {
      //Fill the unmetPreconditions array and the predicateTaskMap.
      var taskIndex = 0
      while (taskIndex < domain.tasks.length) {
        var allowed = true
        var disTaskIndex = 0
        while(disTaskIndex < config.disallowedTasks.length) {
          if(taskIndex == config.disallowedTasks(disTaskIndex)) {
            allowed = false
          }
          disTaskIndex += 1
        }
        val task = domain.tasks(taskIndex)
        if(!task.initOrGoalTask && allowed) {
          unmetPreconditions(taskIndex) = task.precondition.length
          var predicateIndex = 0
          while (predicateIndex < task.precondition.length) {
              predicateTaskMap(task.precondition(predicateIndex).predicate) += taskIndex
              predicateIndex += 1
          }
        }
        taskIndex += 1
      }
      var dAIndex = 0
      while(dAIndex < config.disallowedActions.length) {
        var dAction = config.disallowedActions(dAIndex)
        disAllowedActions(dAction._1) += dAction._2
        dAIndex += 1
      }
      //Setup the initialState.
      var stateIndex = 0
      while (stateIndex < initialState.length) {
        val fact = initialState(stateIndex)
        addFact(fact._1, fact._2)
        stateIndex += 1
      }
      setFirstAppearance()
      factMutexes += Array.fill[jav.BitSet](factIndex+1)(new jav.BitSet())
      factMutexesAB += Array.fill[ArrayBuffer[Int]](factIndex+1)(new ArrayBuffer[Int]())
      permanentActionMutexes += Array.fill[jav.BitSet](actionIndex+1)(new jav.BitSet())
      temporaryActionMutexes += Array.fill[jav.BitSet](actionIndex+1)(new jav.BitSet())
      temporaryActionMutexesAB += Array.fill[ArrayBuffer[Int]](actionIndex+1)(new ArrayBuffer[Int]())

    }

    /**
      * Adds a fact to the graph, if it doesn't already exists.
      *
      * @param predicate Number of the predicate in the domain.predicates-array.
      * @param args      Array of Ints, each an index identifying the constant in the respective domain.sortsOfConstant-arrays.
      * @return true if the fact was added, false if not.
      */
    def addFact(predicate: Int, args: Array[Int]): Int = {
      val bit = computeFactBit(predicate, args)
      if (!factVault.get(bit)) {
        factSpikeIDs(factIndex) = predicate
        factSpikeArgs(factIndex) = args
        predicateFactMap(predicate) += factIndex
        if(predicateFactMap(predicate).length == 1) {
          var index = 0
          val tasks = predicateTaskMap(predicate)
          while (index < tasks.length) {
            val task = tasks(index)
            unmetPreconditions(task) -= 1
            index += 1
          }
        }
        factVault.set(bit)
        factVaultMap.update(bit, factIndex)
        factIndex += 1
        factIndex-1
      } else {
        factVaultMap.getOrElse(bit, -1)
      }
    }

    def addActions(taskID: Int, facts: Array[Int]): Unit = {

      val task = domain.tasks(taskID)

      /**
        * Preconditions of the task which need to be met.
        */
      val preconditions = task.precondition

      /**
        * Every given fact has one ArrayBuffer[Int] which is filled with the indices of the precondition-array that the given fact can meet.
        */
      val candidates = Array.fill[ArrayBuffer[Int]](facts.length)(new ArrayBuffer[Int]())

      /**
        * Facts used to met the preconditions. A fact is represented by an Int as the position of the fact in the factSpike. A fact at the
        * n-th position in the array fulfills the n-th precondition in the precondition-array of the task.
        */
      val usedFacts = new Array[Int](preconditions.length)

      val assignment = Array.fill[Int](task.parameterSorts.length)(-1)

      val usableFacts = new Array[Array[Int]](preconditions.length)

      /**
        * Finds candidates for the facts-parameter of the addActions-method.
        * A candidate is an Int representing the index of a precondition that fact can fulfill.
        */
      def setup(): Unit = {
        var preconditionIndex = 0
        while (preconditionIndex < preconditions.length) {
          val predicate = preconditions(preconditionIndex).predicate
          var fIndex = 0
          while (fIndex < facts.length) {
            val fact = facts(fIndex)
            if (predicate == factSpikeIDs(fact)) {
              candidates(fIndex) += preconditionIndex
            }
            fIndex += 1
          }

          usableFacts(preconditionIndex) = predicateFactMap(predicate).toArray
          preconditionIndex += 1
        }
      }

      setup()

      /**
        * ##############################################################
        * #                                                            #
        * # WARNING: This is where the instantiation magic happens.    #
        * #          Make changes only if you know what you are doing. #
        * #                                                            #
        * ##############################################################
        */

      val cCounter = new Array[Int](candidates.length)
      val pCounter = new Array[Int](preconditions.length)
      var cPointer = 0
      var pPointer = 0
      var runningC = true
      while (runningC) {
        if (candidatesAreValid()) {
          /*
           * if the candidates are valid......
           */
          var assignmentIsValid = true
          val usedPosition = new jav.BitSet()
          var aIndex = 0
          while (aIndex < assignment.length) {
            assignment(aIndex) = -1
            aIndex += 1
          }
          var cIndex = 0
          while (cIndex < cCounter.length) {
            val pos = candidates(cIndex)(cCounter(cIndex))
            usedPosition.set(pos)
            usedFacts(pos) = facts(cIndex)
            cIndex += 1
          }
          var pointerPos = 0
          var pos = 0
          while (pos < preconditions.length) {
            if (usedPosition.get(pos)) {
              pos += 1
            } else {
              pointerPos = pos
              pos = preconditions.length
            }
          }
          var runningP = true
          pPointer = pointerPos
          while (runningP) {

            // set used facts.
            var precIndex = 0
            while (precIndex < preconditions.length) {
              if (!usedPosition.get(precIndex)) {
                val predicate = preconditions(precIndex).predicate
                usedFacts(precIndex) = usableFacts(precIndex)(pCounter(precIndex))
              }
              precIndex += 1
            }

            // check assignment.
            var uFIndex = 0
            while (uFIndex < usedFacts.length) {
              val fact = usedFacts(uFIndex)
              val prec = preconditions(uFIndex)
              var argIndex = 0
              val args = factSpikeArgs(fact)
              while (argIndex < args.length) {
                val assignPos = prec.parameterVariables(argIndex)
                val assignVal = assignment(assignPos)
                if (assignVal == -1 || assignVal == args(argIndex)) {
                  assignment(assignPos) = args(argIndex)
                } else {
                  assignmentIsValid = false
                  argIndex = args.length
                  uFIndex = usedFacts.length
                }
                argIndex += 1
              }
              uFIndex += 1
            }

            // add actions if everything is okay.
            if (isMutexFree && assignmentIsValid) {
              addAction(taskID, usedFacts, assignment)
            }

            pCounter(pPointer) += 1
            while (runningP && pCounter(pPointer) >= usableFacts(pPointer).length) {
              pCounter(pPointer) = 0
              pPointer += 1
              while (usedPosition.get(pPointer)) {
                pPointer += 1
              }
              if (pPointer >= pCounter.length) {
                runningP = false
              } else {
                pCounter(pPointer) += 1
              }
            }
            pPointer = pointerPos
          }

        }
        // iterate combinations of candidates
        cCounter(cPointer) += 1
        while (runningC && cCounter(cPointer) >= candidates(cPointer).length) {
          cCounter(cPointer) = 0
          cPointer += 1
          if (cPointer >= cCounter.length) {
            runningC = false
          } else {
            cCounter(cPointer) += 1
          }
        }
        cPointer = 0
      }

      /**
        * Checks if the current candidates are valid. They are valid as long as they are not used to fulfill the same precondition.
        *
        * @return Returns true if candidates are valid. Otherwise false.
        */
      def candidatesAreValid(): Boolean = {
        var valid = true
        var c1index = 0
        while (c1index < (candidates.length - 1)) {
          val c1 = candidates(c1index)(cCounter(c1index))
          var c2index = c1index + 1
          while (c2index < candidates.length) {
            val c2 = candidates(c2index)(cCounter(c2index))
            if (c1 == c2) {
              c1index = candidates.length
              c2index = candidates.length
              valid = false
            }
            c2index += 1
          }
          c1index += 1
        }
        valid
      }

      def isMutexFree: Boolean = {
        var f1Index = 0
        while (f1Index < usedFacts.length - 1) {
          val fact1 = usedFacts(f1Index)
          val mutexes = factMutexes.last(fact1)
          var f2Index = f1Index + 1
          while (f2Index < usedFacts.length) {
            val fact2 = usedFacts(f2Index)
            if (mutexes.get(fact2)) {
              return false
            }
            f2Index += 1
          }
          f1Index += 1
        }
        true
      }

    }

    def addAction(taskIndex: Int, preconditions: Array[Int], assignment: Array[Int]): Unit = {

      def instantiateAction(finalAssignment: Array[Int]): Unit = {
        actionSpikeIDs(actionIndex) = taskIndex
        actionSpikeArgs(actionIndex) = finalAssignment
        precs(actionIndex) = new jav.BitSet()
        precsAB(actionIndex) = new ArrayBuffer[Int]()
        adds(actionIndex) = new jav.BitSet()
        dels(actionIndex) = new jav.BitSet()
        // set precs
        var precIndex = 0
        while (precIndex < preconditions.length) {
          val prec = preconditions(precIndex)
          precs(actionIndex).set(computeFactBit(factSpikeIDs(prec), factSpikeArgs(prec)))
          precsAB(actionIndex).+=:(prec)
          consumersAB(prec).+=:(actionIndex)
          precIndex += 1
        }
        var effIndex = 0
        val task = domain.tasks(taskIndex)
        while (effIndex < task.effect.length) {
          val args = new Array[Int](task.effect(effIndex).parameterVariables.length)
          var argIndex = 0
          while (argIndex < args.length) {
            args(argIndex) = finalAssignment(task.effect(effIndex).parameterVariables(argIndex))
            argIndex += 1
          }
          val bit = computeFactBit(task.effect(effIndex).predicate, args)
          if (task.effect(effIndex).isPositive) {
            adds(actionIndex).set(bit)
          } else {
            dels(actionIndex).set(bit)
          }
          effIndex += 1
        }
        val bit = computeActionBit(taskIndex, assignment)
        actionVault.set(bit)
        actionIndex += 1
      }

      val unassignedAB = new ArrayBuffer[Int]()
      val unassignedBS = new jav.BitSet()
      var assignmentIndex = 0
      while (assignmentIndex < assignment.length) {
        if (assignment(assignmentIndex) == -1) {
          unassignedAB += assignmentIndex
          unassignedBS.set(assignmentIndex)
        }
        assignmentIndex += 1
      }
      if (unassignedAB.nonEmpty) {
        val paramSorts = domain.tasks(taskIndex).parameterSorts
        val counter = Array.fill[Int](unassignedAB.length)(0)
        var pointer = unassignedAB(0)
        var running = true
        while(running) {
          var uIndex = 0
          while(uIndex < unassignedAB.length) {
            val index = unassignedAB(uIndex)
            assignment(index) = domain.sortsOfConstant(paramSorts(index))(counter(index))
            uIndex += 1
          }
          val bit = computeActionBit(taskIndex, assignment)
          if (!actionVault.get(bit) && isAllowed) {
            instantiateAction(assignment)
          }
          counter(pointer) += 1
          while(running && counter(pointer) >= domain.sortsOfConstant(paramSorts(pointer)).length) {
            counter(pointer) = 0
            pointer += 1
            while(!unassignedBS.get(pointer) && pointer < counter.length) {
              pointer += 1
            }
            if(pointer >= counter.length) {
              running = false
            } else {
              counter(pointer) += 1
            }
          }
          pointer = unassignedAB(0)
        }
      } else {
        val bit = computeActionBit(taskIndex, assignment)
        if (!actionVault.get(bit) && isAllowed) {
          instantiateAction(assignment)
        }
      }

      def isAllowed: Boolean = {
        var dIndex = 0
        val disallowedArgs = disAllowedActions(taskIndex)
        while(dIndex < disallowedArgs.length) {
          if (assignment.sameElements(disallowedArgs(dIndex))) {
            return false
          }
          dIndex += 1
        }
        true
      }
    }

    def instantiateActionsFromNewFacts(): Unit = {
      var fIndex = oldFactIndex
      while (fIndex < factIndex) {
        var taskIterator = 0
        val tasks = predicateTaskMap(factSpikeIDs(fIndex))
        while (taskIterator < tasks.length) {
          val taskID = tasks(taskIterator)

          if(unmetPreconditions(taskID) == 0 && !domain.tasks(taskID).initOrGoalTask) {
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
            if(unmetPreconditions(task) == 0) {
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
            if(effect.isPositive) {
              val args = new Array[Int](effect.parameterVariables.length)
              var argIterator = 0
              while (argIterator < args.length) {
                args(argIterator) = actionSpikeArgs(actionIterator)(effect.parameterVariables(argIterator))
                argIterator += 1
              }
              val fIndex = addFact(effect.predicate, args)
              updatedFacts.+=:(fIndex)
              producers(fIndex).set(actionIterator)
              producersAB(fIndex).+=:(actionIterator)
            }
            effectIterator += 1
          }
        }
        actionIterator += 1
      }
    }

    def addNOOPs(): Unit = {
      var index = factRankFirst.last
      while (index < factIndex) {
        createNOOP(index)
        index += 1
      }
    }

    def createNOOP(factIndex: Int): Unit = {
      val taskID = factSpikeIDs(factIndex)
      actionSpikeIDs(actionIndex) = domain.tasks.length+taskID+1
      val add = new jav.BitSet()
      val del = new jav.BitSet()
      val prec = new jav.BitSet()
      val precAB = new ArrayBuffer[Int]()
      val bit = computeFactBit(factSpikeIDs(factIndex), factSpikeArgs(factIndex))
      add.set(bit)
      precAB.+=:(factIndex)
      prec.set(bit)
      adds(actionIndex) = add
      dels(actionIndex) = del
      precs(actionIndex) = prec
      precsAB(actionIndex) = precAB
      producers(factIndex).set(actionIndex)
      producersAB(factIndex).+=:(actionIndex)
      updatedFacts.+=:(factIndex)
      actionIndex += 1
    }

    def setFirstAppearance(): Unit = {
      var index = oldFactIndex
      while(index < factIndex) {
        firstAppearanceOfFacts(index) = factRankFirst.length+1
        firstAchieversOfFacts(index) = producersAB(index).clone()
        index += 1
      }
    }

    def computeMutexes(): Unit = {
      val newFactMutexes = Array.fill[jav.BitSet](factIndex + 1)(new jav.BitSet())
      val newFactMutexesAB = Array.fill[ArrayBuffer[Int]](factIndex + 1)(new ArrayBuffer[Int]())
      val newPermanentActionMutexes = Array.fill[jav.BitSet](actionIndex + 1)(new jav.BitSet())
      val newTemporaryActionMutexes = Array.fill[jav.BitSet](actionIndex + 1)(new jav.BitSet())
      val newTemporaryActionMutexesAB = Array.fill[ArrayBuffer[Int]](actionIndex + 1)(new ArrayBuffer[Int]())

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
        while(fIndex < oldFactIndex) {
          newFactMutexes(fIndex) = factMutexes.last(fIndex).clone().asInstanceOf[jav.BitSet]
          newFactMutexesAB(fIndex) = factMutexesAB.last(fIndex).clone()
          fIndex += 1
        }
        var aIndex = 0
        while(aIndex < oldActionIndex) {
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
        actionMutexes.or(newPermanentActionMutexes(producers1(0)))
        actionMutexes.or(newTemporaryActionMutexes(producers1(0)))
        var producerIndex = 0
        while (producerIndex < producers1.length) {
          val mutexes = new jav.BitSet()
          mutexes.or(newPermanentActionMutexes(producers1(producerIndex)))
          mutexes.or(newTemporaryActionMutexes(producers1(producerIndex)))
          actionMutexes.and(mutexes)
          producerIndex += 1
        }
        var mutex = true
        var pIndex = 0
        while(pIndex < producers2.length) {
          if(!actionMutexes.get(producers2(pIndex))) {
            mutex = false
            pIndex = producers2.length
          }
          pIndex += 1
        }
        mutex
      }

      def areActionsMutex(action1: Int, action2: Int): Int = {
        if(config.serial) {
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
        while(delFIndex < deletedFactMutexFirst.length) {
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
        var fIndex1 = factIndex-1
        while (fIndex1 >= oldFactIndex) {
          var fIndex2 = fIndex1 - 1
          while (fIndex2 >= 0) {
            if (areFactsMutex(fIndex2, fIndex1)) {
              newFactMutexes(fIndex2).set(fIndex1)
              newFactMutexes(fIndex1).set(fIndex2)
              newFactMutexesAB(fIndex2).+=:(fIndex1)
              newFactMutexesAB(fIndex1).+=:(fIndex2)
            }
            fIndex2 -= 1
          }
          fIndex1 -= 1
        }
      }

      def deleteFactMutex(fact1: Int, fact2: Int): Unit = {
        val fact1Mutexes = newFactMutexesAB(fact1)
        val fact2Mutexes = newFactMutexesAB(fact2)
        if(fact1Mutexes.contains(fact2)) {
          newFactMutexes(fact1).set(fact2, false)
          newFactMutexes(fact2).set(fact1, false)
          fact1Mutexes.remove(fact1Mutexes.indexOf(fact2))
          fact2Mutexes.remove(fact2Mutexes.indexOf(fact1))
          deletedFactMutexFirst.+=:(fact1)
          deletedFactMutexSecond.+=:(fact2)
        }
      }

      def deleteFactMutexes(): Unit = {
        val uFacts = updatedFacts.distinct
        deletedFactMutexFirst.clear()
        deletedFactMutexSecond.clear()
        var fIndex = 0
        while(fIndex < uFacts.length) {
          val fact1 = uFacts(fIndex)
          if(fact1 < oldFactIndex) {
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
      if(counter == 0) {
        init()
      } else {
        if (counter == 1) {
          var taskIndex = 0
          while (taskIndex < domain.tasks.length) {
            val task = domain.tasks(taskIndex)
            // instantiate 0-precs-tasks
            if (task.precondition.length == 0 && !task.initOrGoalTask) {
              addAction(taskIndex, new Array[Int](0), Array.fill[Int](domain.tasks(taskIndex).parameterSorts.length)(-1))
            }
            taskIndex += 1
          }
        }

        updatedFacts.clear()
        addNOOPs()
        // add new actions based on new facts.
        instantiateActionsFromNewFacts()
        // add new actions based on deleted mutexes.
        instantiateActionsFromDeletedMutexes()
        // add facts from newly instantiated actions.
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
    * Computes the maximum number of actions that can theoretically be instantiated in the domain.
    *
    * @return The maximum number of actions.
    */
  private def computeMaxActions(): Int = {
    var maxActions = 0
    var taskIndex = 0
    while (taskIndex < domain.tasks.length) {
      val args = domain.tasks(taskIndex).parameterSorts
      var sortIndex = 0
      while (sortIndex < args.length) {
        maxActions += domain.sortsOfConstant(args(sortIndex)).length
        sortIndex += 1
      }
      if (args.length == 0) {
        maxActions += 1
      }
      taskIndex += 1
    }
    maxActions + maxFacts
  }

  /**
    * Computes the maximum number of facts that theoretically can become true in the domain.
    *
    * @return The maximum number of facts.
    */
  private def computeMaxFacts(): Int = {
    var maxFacts = 0
    var predicateIndex = 0
    while (predicateIndex < domain.predicates.length) {
      val args = domain.predicates(predicateIndex)
      var sortIndex = 0
      while (sortIndex < args.length) {
        maxFacts += domain.constantsOfSort(args(sortIndex)).length
        sortIndex += 1
      }
      predicateIndex += 1
      if (args.length == 0) {
        maxFacts += 1
      }
    }
    maxFacts
  }

  /**
    * Computes the bit of a given fact.
    * The bit is computed like this:
    * - n is the number of predicates in the domain (domain.predicates.length).
    * - p is the number of the predicate the given fact contains (the index in the domain.predicates-array).
    * - b is the sum of every constant in the args-array, and each predicate is multiplied
    * with the number of combinations of the previous predicates in the array.
    * The resulting bit is b * n + p.
    *
    * @param predicate Predicate of the fact, represented by the index in the domain.predicates-array.
    * @param args      Arguments of the fact, represented by an array of Ints each a constant of a sort.
    * @return Returns the bit corresponding to the given fact.
    */
  private def computeFactBit(predicate: Int, args: Array[Int]): Int = {
    var bit = 0
    var predicateIndex = 0
    var acc = 1
    while (predicateIndex < domain.predicates(predicate).length) {
      bit += args(predicateIndex) * acc
      acc *= domain.sortsOfConstant(domain.predicates(predicate)(predicateIndex)).length
      predicateIndex += 1
    }
    bit * domain.predicates.length + predicate
  }

  private def computeActionBit(task: Int, args: Array[Int]): Int = {
    var bit = 0
    var argIndex = 0
    var acc = 1
    while (argIndex < args.length) {
      bit += args(argIndex) * acc
      acc *= domain.sortsOfConstant(domain.tasks(task).parameterSorts(argIndex)).length
      argIndex += 1
    }
    task
  }

  build()
}