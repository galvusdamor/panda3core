package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.efficient.csp.EfficientUnionFind
import de.uniulm.ki.panda3.symbolic.compiler.{ClosedWorldAssumption, ExpandSortHierarchy, RemoveNegativePreconditions, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.logic.{Constant, GroundLiteral}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object EquivalentObjectsInference {


  def computeEquivalentObjects(facts: Seq[GroundLiteral], constants: Seq[Constant]): Array[Array[Constant]] = {

    def areEquivalent(a: Constant, b: Constant): Boolean = {
      val aFacts = facts filter { _.parameter contains a }
      val bFacts = facts filter { _.parameter contains b }

      if (aFacts.length == bFacts.length) {
        // split by predicate
        val aGroupedFacts = aFacts groupBy { _.predicate }
        val bGroupedFacts = bFacts groupBy { _.predicate }

        if (aGroupedFacts.keySet == bGroupedFacts.keySet) {
          aGroupedFacts.keySet forall { pred =>
            val aPredFacts = aGroupedFacts(pred)
            val bPredFacts = bGroupedFacts(pred)

            if (aPredFacts.length == bPredFacts.length) {
              val aPositions = aPredFacts flatMap { _.parameter.zipWithIndex collect { case (x, i) if x == a => i } } toSet
              val bPositions = bPredFacts flatMap { _.parameter.zipWithIndex collect { case (x, i) if x == b => i } } toSet

              if (aPositions == bPositions) {
                val aAllowedAtB = bPositions forall { i => pred.argumentSorts(i).elements contains a }
                val bAllowedAtA = aPositions forall { i => pred.argumentSorts(i).elements contains b }

                if (aAllowedAtB && bAllowedAtA) {
                  def replaceArgs(args: Seq[Constant]) = args map { x => if (x == a) b else if (x == b) a else x }
                  val bReplaced = bPredFacts map { case GroundLiteral(_, true, args) => GroundLiteral(pred, isPositive = true, replaceArgs(args)) }

                  aPredFacts.toSet == bReplaced.toSet
                } else false
              } else false
            } else false
          }
        } else false
      } else false
    }

    val uf = EfficientUnionFind().addVariables(constants.length)

    for (a <- constants.indices; b <- Range(a + 1, constants.length)) if (uf.getRepresentative(a) != uf.getRepresentative(b)) {
      if (areEquivalent(constants(a), constants(b))) {
        uf.assertEqual(a, b)
        //println("Equiv " + a + " " + b)
      }
    }


    // build equivalence classes
    (constants.zipWithIndex groupBy { uf getRepresentative _._2 } values) map { _ map { _._1 } toArray } toArray
  }


  def main(args: Array[String]) {
    val domFile = "../lifted_planner/data/transport/p01-domain.pddl"
    val probFile = "../lifted_planner/data/transport/p01.pddl"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/domain/domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/problems/p01.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Depots/domain/Depots-Mod.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Depots/problems/pfile5-mod"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problem-from-ridder-paper/domain.lisp"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problem-from-ridder-paper/prob.lisp"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/SATELLITE/domain/DOMAIN.PDDL"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/SATELLITE/problems/P20_PFILE20.PDDL"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/domain/zenotravelStrips.pddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/problems/pfile1"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p01-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p03.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

    //val domFile = args(0)
    //val probFile = args(1)

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    // we assume that the domain is grounded

    // cwa
    val expanded = ExpandSortHierarchy.transform(parsedDomainAndProblem, ())
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(expanded, true)
    val plainFormula = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val (domain, initialPlan) = RemoveNegativePreconditions.transform(plainFormula, ())


    val groundedInitialState = initialPlan.groundedInitialStateOnlyPositive.toSet
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState, GroundedPlanningGraphConfiguration(computeMutexes = false, buckets = true))

    println("PG done")
    val finalLiterals = planningGraph.reachableGroundLiterals map { _.copy(isPositive = true) } distinct

    println(finalLiterals map { _.longInfo } mkString "\n")
    println(finalLiterals.size)
    println(planningGraph.graphSize)

    val equivalenceClasses = computeEquivalentObjects(finalLiterals, domain.constants)
    //val equivalenceClasses = computeEquivalentObjects(initialPlan.groundedInitialStateOnlyPositive, domain.constants)

    println(equivalenceClasses map { _ mkString "," } mkString "\n")
  }
}