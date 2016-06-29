package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream
import java.io.File

import de.uniulm.ki.panda3.symbolic.compiler.{RemoveNegativePreconditions, Grounding, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Domain}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.EverythingIsHiearchicallyReachable
import de.uniulm.ki.panda3.symbolic.logic.{And, Variable, Literal}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.scalatest.FlatSpec

import sys.process._
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraphCompareWithCppImplementation extends FlatSpec {

  def writeDomainAndProblemToSimpleFormat(domain: Domain, problem: Plan): String = {
    domain.predicates foreach { p => assert(p.argumentSorts.isEmpty) }

    val builder = new StringBuilder

    // write initial state
    val (positiveInit, negativeInit) = domain.predicates partition { p => problem.groundedInitialState exists { gp => gp.predicate == p && gp.isPositive } }
    positiveInit.zipWithIndex foreach { case (p, i) => builder.append((if (i != 0) "," else "") + p.name) }
    builder.append(";")
    negativeInit.zipWithIndex foreach { case (p, i) => builder.append((if (i != 0) "," else "") + p.name) }
    builder.append("\n")

    def addLiteralList(literals: Seq[Literal]): Unit = literals.zipWithIndex foreach { case (l, i) => builder.append((if (i != 0) "," else "") + l.predicate.name) }

    // goal state
    val (positiveGoal, negativeGoal) = problem.goal.substitutedPreconditions partition { _.isPositive }
    addLiteralList(positiveGoal)
    builder.append(";")
    addLiteralList(negativeGoal)
    builder.append("\n")

    // actions
    domain.tasks foreach { case ReducedTask(name, true, Nil, _, precondition, effect) =>
      builder.append(name + ";")

      // precon
      precondition.conjuncts foreach { l => assert(l.isPositive) }
      addLiteralList(precondition.conjuncts)
      builder.append(";;")
      // effects
      val (positiveEffects, negativeEffects) = effect.conjuncts partition { _.isPositive }
      addLiteralList(positiveEffects)
      builder.append(";")
      addLiteralList(negativeEffects)

      builder.append("\n")
    }

    builder.toString()
  }

  //
  "01" :: "02" :: "03" :: "04" :: "05" ::"06" :: Nil foreach { problemID =>
  //"03" :: Nil foreach { problemID =>
    "The grounded planning graph" must "produce the same result as Gregor's C++ implementation in TC " + problemID in {
      val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest" + problemID + "_domain.hddl"
      val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest" + problemID + "_problem.hddl"

      val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
      // we assume that the domain is grounded

      // cwa
      val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, info = false)
      val plain = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
      val negPre = RemoveNegativePreconditions.transform(plain, ())
      val (domain, initialPlan) = Grounding.transform(negPre, EverythingIsHiearchicallyReachable(negPre._1, negPre._2))

      val domainString = writeDomainAndProblemToSimpleFormat(domain, initialPlan)
      writeStringToFile(domainString, new File("__probleminput"))

      // compile the program
      "g++ src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planninggraph.cpp" !!

      //"cat __probleminput" #| "./a.out" !
      val cppPlanningGraphOutput: Seq[Seq[Int]] = ("cat __probleminput" #| "./a.out" !!) split "\n" map { _ split " " map { _.toInt } toSeq } toSeq

      println(cppPlanningGraphOutput)


      val groundedInitialState = negPre._2.groundedInitialState filter { _.isPositive }
      val planningGraph = new GroundedPlanningGraph(negPre._1, groundedInitialState.toSet, true, false, Left(Nil))
      //assert(planningGraph.layerWithMutexes.size == cppPlanningGraphOutput.size)
      planningGraph.layerWithMutexes zip cppPlanningGraphOutput foreach { case ((a, b, c, d), cppRes) =>
        val newB = (b map { case (a, b) => if (a.task.name != b.task.name) {
          if (a.task.name < b.task.name)(a, b) else (b,a)
        }
        else {
          val argCompare = (a.arguments zip b.arguments).foldLeft[Option[Boolean]](None) {
                                                                                           case (Some(x), _)   => Some(x)
                                                                                           case (None, (x, y)) => if (x == y) None else if (x.name < y.name) Some(true) else Some(false)
                                                                                         }
          if (argCompare getOrElse true) (a, b) else (b, a)
        }
        }).toSeq.distinct
        println(a.size + " " + newB.size + " " + c.size + " " + d.size + " vs " + (cppRes mkString " "))
        // println(a map {_.task.name} mkString " ")
        val as = a.size
        val bs = newB.size
        val cs = c.size
        val ds = d.size
        assert(as == cppRes(0))
        //println(newB map { case (a, b) =>
        //  a.task.name + ((a.arguments map { _.name }).mkString("(", ",", ")")) + "!" + b.task.name + ((b.arguments map { _.name }).mkString("(", ",", ")"))
        //} mkString " ")
        assert(bs == cppRes(1))
        assert(cs == cppRes(2))
        assert(ds == cppRes(3))
      }
      //assert(false)

      //"rm __probleminput a.out" !!
    }
  }

}
