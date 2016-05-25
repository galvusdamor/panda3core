package de.uniulm.ki.panda3.symbolic.compiler

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, HasExampleProblem3}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class RemoveNegativePreconditionsTest extends FlatSpec with HasExampleProblem3 {

  "Removing negative preconditions" must "work correctly" in {
    val (domain, plan) = RemoveNegativePreconditions.transform(domain3, plan1WithBothCausalLinks, ())

    assert(domain.tasks.length == domain3.tasks.length)

    domain3.tasks foreach { originalTask =>
      val newTask: Task = (domain.tasks find { _.name == originalTask.name }).get
      assert(newTask.isInstanceOf[ReducedTask])
      assert(originalTask.isInstanceOf[ReducedTask])
      val reducedTask = newTask.asInstanceOf[ReducedTask]
      val reducedOriginalTask = originalTask.asInstanceOf[ReducedTask]
      assert(reducedTask.precondition.conjuncts.length == reducedOriginalTask.precondition.conjuncts.length)
      assert(reducedTask.effect.conjuncts.length == 2 * reducedOriginalTask.effect.conjuncts.length)
    }
  }


  it must "produce the correct result" in {
    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    // we assume that the domain is grounded

    // cwa
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())

    assert(domain.tasks.length == 3)

    val taskX = domain.tasks find { _.name == "X" } get
    val taskY = domain.tasks find { _.name == "Y" } get
    val taskZ = domain.tasks find { _.name == "Z" } get

    assert(taskX.isInstanceOf[ReducedTask])
    assert(taskY.isInstanceOf[ReducedTask])
    assert(taskY.isInstanceOf[ReducedTask])

    val reducedTaskX = taskX.asInstanceOf[ReducedTask]
    val reducedTaskY = taskY.asInstanceOf[ReducedTask]
    val reducedTaskZ = taskZ.asInstanceOf[ReducedTask]

    // check out task X
    assert(reducedTaskX.precondition.conjuncts.length == 2)
    assert(reducedTaskX.precondition.conjuncts forall { _.isPositive })
    assert(reducedTaskX.precondition.conjuncts exists { _.predicate.name == "+a" })
    assert(reducedTaskX.precondition.conjuncts exists { _.predicate.name == "-e" })
    assert(reducedTaskX.effect.conjuncts.length == 4)
    val (reducedTaskXPositiveEffects, reducedTaskXNegativeEffects) = reducedTaskX.effect.conjuncts partition { _.isPositive }
    assert(reducedTaskXPositiveEffects.length == 2)
    assert(reducedTaskXPositiveEffects exists { _.predicate.name == "+b" })
    assert(reducedTaskXPositiveEffects exists { _.predicate.name == "+e" })
    assert(reducedTaskXNegativeEffects.length == 2)
    assert(reducedTaskXNegativeEffects exists { _.predicate.name == "-b" })
    assert(reducedTaskXNegativeEffects exists { _.predicate.name == "-e" })

    // check out task Y
    assert(reducedTaskY.precondition.conjuncts.length == 2)
    assert(reducedTaskY.precondition.conjuncts forall { _.isPositive })
    assert(reducedTaskY.precondition.conjuncts exists { _.predicate.name == "+a" })
    assert(reducedTaskY.precondition.conjuncts exists { _.predicate.name == "-e" })
    assert(reducedTaskY.effect.conjuncts.length == 4)
    val (reducedTaskYPositiveEffects, reducedTaskYNegativeEffects) = reducedTaskY.effect.conjuncts partition { _.isPositive }
    assert(reducedTaskYPositiveEffects.length == 2)
    assert(reducedTaskYPositiveEffects exists { _.predicate.name == "+c" })
    assert(reducedTaskYPositiveEffects exists { _.predicate.name == "-a" })
    assert(reducedTaskYNegativeEffects.length == 2)
    assert(reducedTaskYNegativeEffects exists { _.predicate.name == "-c" })
    assert(reducedTaskYNegativeEffects exists { _.predicate.name == "+a" })

    // check out task X
    assert(reducedTaskZ.precondition.conjuncts.length == 2)
    assert(reducedTaskZ.precondition.conjuncts forall { _.isPositive })
    assert(reducedTaskZ.precondition.conjuncts exists { _.predicate.name == "+b" })
    assert(reducedTaskZ.precondition.conjuncts exists { _.predicate.name == "+c" })
    assert(reducedTaskZ.effect.conjuncts.length == 2)
    val (reducedTaskZPositiveEffects, reducedTaskZNegativeEffects) = reducedTaskZ.effect.conjuncts partition { _.isPositive }
    assert(reducedTaskZPositiveEffects.length == 1)
    assert(reducedTaskZPositiveEffects exists { _.predicate.name == "+d" })
    assert(reducedTaskZNegativeEffects.length == 1)
    assert(reducedTaskZNegativeEffects exists { _.predicate.name == "-d" })
  }
}