package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Task, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.{GreedyNumberOfAbstractChildrenOptimiser, OptimalBranchAndBoundOptimiser}
import de.uniulm.ki.util.{DirectedGraphDotOptions, Dot2PdfCompiler, SimpleDirectedGraph, DirectedGraph}

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait SOGEncoding  extends PathBasedEncoding[SOG, NonExpandedSOG] with LinearPrimitivePlanEncoding {

  protected final val useImplicationForbiddenness = false

  assert(initialPlan.planStepsWithoutInitGoal.length == 1, "This formula is only correct if the initial plan has been replaced by an artificial top task")

  // this is only needed in the tree encoding
  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: Seq[Task]): Seq[Clause] = Nil


  override lazy val noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  override lazy val goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  //TODO: is this correct?
  override val numberOfChildrenClauses: Int = 0

  // TODO: we don't support this (yet), maybe N. Dönhoff will do it
  override def givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: Seq[Int]): SOG = SOG(SimpleDirectedGraph((path, possibleTasks) :: Nil, Nil))


  def minimiseAbstractTaskOccurencesMetric(g: DirectedGraph[Int], mapping: Seq[Map[PlanStep, Int]]): Int = if (g.vertices.isEmpty) 0
  else {
    val abstractTasks = g.vertices map { _ => new mutable.HashSet[Task]() } toArray

    mapping foreach { m => m filter { _._1.schema.isAbstract } foreach { case (ps, i) => abstractTasks(i) add ps.schema } }

    abstractTasks map { _.size } sum
  }

  def minimiseChildrenWithAbstractTasks(g: DirectedGraph[Int], mapping: Seq[Map[PlanStep, Int]]): Int = if (g.vertices.isEmpty) 0
  else {
    val abstractTasks = g.vertices map { _ => new mutable.HashSet[Task]() } toArray

    mapping foreach { m => m filter { _._1.schema.isAbstract } foreach { case (ps, i) => abstractTasks(i) add ps.schema } }

    (abstractTasks count { _.nonEmpty }) * 1000 + g.vertices.length
  }


  protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod], possiblePrimitives: Seq[Task]):
  (Array[Array[Int]], Array[Int], Array[Set[Task]], NonExpandedSOG) = {
    val methodTaskGraphs = (possibleMethods map { _.subPlan.orderingConstraints.fullGraph }) ++ (
      possiblePrimitives map { t => SimpleDirectedGraph(PlanStep(-1, t, Nil) :: Nil, Nil) })

    // TODO we are currently mapping plansteps, maybe we should prefer plansteps with identical tasks to be mapped together
    print("MINI " + possibleMethods.length + " " + possiblePrimitives.length + " ... ")
    val lb = methodTaskGraphs map { _.vertices count { _.schema.isAbstract } } max
    val optimiser =
      //OptimalBranchAndBoundOptimiser(minimiseChildrenWithAbstractTasks, lowerBound = lb) //, minimiseAbstractTaskOccurencesMetric)
      GreedyNumberOfAbstractChildrenOptimiser

    val g = optimiser.minimalSOG(methodTaskGraphs)
    //val met = minimiseChildrenWithAbstractTasks(g._1,g._2)

    //val check = OptimalBranchAndBoundOptimiser(minimiseChildrenWithAbstractTasks, lowerBound = lb).minimalSOG(methodTaskGraphs)
    //val metOp = minimiseChildrenWithAbstractTasks(check._1,check._2)

    println("done")
    //println("OP " + met + " of " + metOp)
    val minimalSuperGraph = g._1
    val planStepToIndexMappings: Seq[Map[PlanStep, Int]] = g._2

    val (methodMappings, primitiveMappings) = planStepToIndexMappings.splitAt(possibleMethods.length)

    val childrenIndicesToPossibleTasks = minimalSuperGraph.vertices map { _ => new mutable.HashSet[Task]() }

    val tasksPerMethodToChildrenMapping = methodMappings.zipWithIndex map { case (mapping, methodIndex) =>
      val methodPlanSteps = possibleMethods(methodIndex).subPlan.planStepsWithoutInitGoal
      (methodPlanSteps map { ps =>
        childrenIndicesToPossibleTasks(mapping(ps)) add ps.schema
        mapping(ps)
      }).toArray
    } toArray

    val childrenForPrimitives = primitiveMappings.zipWithIndex map { case (mapping, primitiveIndex) =>
      assert(mapping.size == 1)
      childrenIndicesToPossibleTasks(mapping.head._2) add mapping.head._1.schema
      mapping.head._2
    } toArray

    //println("\n\nGraph minisation")
    //println(childrenIndicesToPossibleTasks map {s => s map {t => t.name + " " + t.isAbstract} mkString " "} mkString "\n")

    val maxVertex = minimalSuperGraph.vertices.max
    assert(minimalSuperGraph.vertices.length - 1 == maxVertex, "SOG has " + minimalSuperGraph.vertices.length + " vertices, but maximum vertex is " + maxVertex)

    (tasksPerMethodToChildrenMapping, childrenForPrimitives, childrenIndicesToPossibleTasks map { _.toSet } toArray, NonExpandedSOG(minimalSuperGraph))
  }

  protected def combinePayloads(childrenPayload: Seq[SOG], intermediate: NonExpandedSOG): SOG = {
    val vertices = childrenPayload flatMap { _.ordering.vertices }
    val internalEdges = childrenPayload flatMap { _.ordering.edgeList }

    val connectingEdges = intermediate.ordering.edgeList flatMap { case (from, to) =>
      val fromVertices = childrenPayload(from).ordering.vertices
      val toVertices = childrenPayload(to).ordering.vertices

      for (x <- fromVertices; y <- toVertices) yield (x, y)
    }

    SOG(SimpleDirectedGraph(vertices, internalEdges ++ connectingEdges))
  }
}

case class NonExpandedSOG(ordering: DirectedGraph[Int])

case class SOG(ordering: DirectedGraph[(Seq[Int], Set[Task])]) {}