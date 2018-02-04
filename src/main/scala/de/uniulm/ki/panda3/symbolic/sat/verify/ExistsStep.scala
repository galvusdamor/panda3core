package de.uniulm.ki.panda3.symbolic.sat.verify

import java.util

import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.{DirectedGraph, Dot2PdfCompiler, SimpleDirectedGraph, TimeCapsule}

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class ExistsStep(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int) extends LinearPrimitivePlanEncoding {
  override lazy val offsetToK = 0

  override lazy val overrideK = None

  override lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  override val numberOfChildrenClauses = 0 // none

  override val expansionPossible = Math.pow(2, domain.predicates.length) > taskSequenceLength

  override val decompositionFormula = Nil

  override val givenActionsFormula = Nil

  override val noAbstractsFormula = Nil

  println("Computing invariante [Rintanen]")

  lazy val invariants: Seq[((Predicate, Boolean), (Predicate, Boolean))] = {
    val pMap: Map[Predicate, Int] = domain.predicates.zipWithIndex map { case (p, i) => (p, i + 1) } toMap
    val mapP: Map[Int, Predicate] = pMap map { _.swap }

    case class IntTask(task: Task) {
      val pre: util.BitSet = task.posPreconditionAsPredicate.map(pMap).foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })
      val add: util.BitSet = task.addEffectsAsPredicate.map(pMap).foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })
      val del: util.BitSet = task.delEffectsAsPredicate.map(pMap).foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })

      val changed: util.BitSet = {
        val r = new util.BitSet()
        r.or(add)
        r.or(del)
        r
      }

      val deletesPosP: Array[Boolean] = domain.predicates.indices map { i => del get (i + 1) } toArray
      val deletesNegP: Array[Boolean] = domain.predicates.indices map { i => add get (i + 1) } toArray
    }

    val tasks: Seq[IntTask] = domain.primitiveTasks map { IntTask }


    val v0: Seq[(Int, Int)] =
      domain.predicates flatMap { p1 =>
        val l1 = (p1, initialPlan.init.schema.addEffectsAsPredicateSet contains p1)
        domain.predicates collect { case p2 if p1 != p2 => (l1, (p2, true)) :: (l1, (p2, false)) :: Nil } flatten
      } map { case ((ap, ab), (bp, bb)) if ap < bp => ((ap, ab), (bp, bb)); case ((ap, ab), (bp, bb)) => ((bp, bb), (ap, ab)) } map { case ((ap, ab), (bp, bb)) =>
        (pMap(ap) * (if (ab) 1 else -1), pMap(bp) * (if (bb) 1 else -1))
      }

    println("candicates build")

    def filter(invar: Seq[(Int, Int)], task: IntTask): Seq[(Int, Int)] = {
      val t01 = System.currentTimeMillis()
      val (posInferredPreconditions, negInferredPreconditions) = {
        val pbs = new util.BitSet()
        val nbs = new util.BitSet()

        invar foreach {
          case (a, b) if task.pre.get(Math.abs(a)) && a < 0 => if (b > 0) pbs.set(b) else nbs.set(-b)
          case (a, b) if task.pre.get(Math.abs(b)) && b < 0 => if (a > 0) pbs.set(a) else nbs.set(-a)
          case _                                            =>
        }

        (pbs, nbs)
      }

      val t02 = System.currentTimeMillis()
      //println("Inferred " + inferredPreconditions.size + " " + (t02 - t01))

      val ensuresPosP: Array[Boolean] = domain.predicates.indices map { i =>
        val ap = i + 1
        (task.add get ap) || (!task.del.get(ap) && (task.pre.get(ap) || posInferredPreconditions.get(ap)))
      } toArray

      val ensuresNegP: Array[Boolean] = domain.predicates.indices map { i =>
        val ap = i + 1
        (task.del get ap) || (!task.add.get(ap) && negInferredPreconditions.get(ap))
      } toArray
      var nc= 0

      val yy = invar filter {
        case (a, b) if !(task.changed.get(Math.abs(a)) || task.changed.get(Math.abs(b))) => nc+=1; true

        case (a, b) =>
          val ap = Math.abs(a)
          val bp = Math.abs(b)
          val ab = a > 0
          val bb = b > 0

          // build lookup
          //val check = invar.groupBy(_._1._1)

          val deletesA = if (ab) task.deletesPosP(ap - 1) else task.deletesNegP(ap - 1)
          val deletesB = if (bb) task.deletesPosP(bp - 1) else task.deletesNegP(bp - 1)
          val ensuresA = if (ab) ensuresPosP(ap - 1) else ensuresNegP(ap - 1)
          val ensuresB = if (bb) ensuresPosP(bp - 1) else ensuresNegP(bp - 1)

          (!deletesA || ensuresB) && (!deletesB || ensuresA)
      }
      println(invar.size + " " + nc)
      val t03 = System.currentTimeMillis()
      //println(t03 - t02)
      yy
    }

    //println(domain.predicates.sorted mkString "\n")

    def reduce(invar: Seq[((Int, Int))]): Seq[((Int, Int))] = {
      val r = tasks.zipWithIndex.foldLeft(invar)({ case (v, (t, i)) =>
        val x = filter(v, t)
        //println(t.name)
        //if (v.size == 1) println(v.head._1._1.name + " " + v.head._1._2 + " v " + v.head._2._1.name + " " + v.head._2._2)
        if (i % 100 == 0) {
          println("Size " + x.size + " at " + i + "/" + domain.primitiveTasks.size)
        }
        x
                                                 })
      if (r.size == invar.size) r else reduce(r)
    }

    val time001 = System.currentTimeMillis()
    val res = reduce(v0) map { case (a, b) =>
      ((mapP(Math.abs(a)), a > 0), (mapP(Math.abs(b)), b > 0))
    }
    val time002 = System.currentTimeMillis()
    println("Invariant time: " + (time002 - time001) / 1000.0)
    res
  }

  println("Number of invariants: " + invariants.size)
  //println(invariants map { case (a, b) => (if (!a._2) "-" else "") + a._1.name + " v " + (if (!b._2) "-" else "") + b._1.name } mkString ("\n"))


  println("Computing disabling graph")
  val time1                               = System.currentTimeMillis()
  val disablingGraph: DirectedGraph[Task] = {

    def applicable(task1: Task, task2: Task): Boolean = true

    def affects(task1: Task, task2: Task): Boolean = task1.delEffectsAsPredicate exists task2.posPreconditionAsPredicateSet.contains

    // compute affection
    val predicateToDeleting: Map[Predicate, Seq[Task]] =
      domain.primitiveTasks flatMap { t => t.delEffectsAsPredicate map { e => (t, e) } } groupBy (_._2) map { case (p, as) => p -> as.map(_._1) }

    val predicateToNeeding: Map[Predicate, Seq[Task]] =
      domain.primitiveTasks flatMap { t => t.posPreconditionAsPredicate map { e => (t, e) } } groupBy (_._2) map { case (p, as) => p -> as.map(_._1) }

    val edgesWithDuplicats: Seq[(Task, Task)] = predicateToDeleting.toSeq flatMap { case (p, as) => predicateToNeeding.getOrElse(p, Nil) flatMap { n => as map { d => (d, n) } } }

    val edges: Seq[(Task, Task)] = (edgesWithDuplicats groupBy { _._1 } toSeq) flatMap { case (t1, t2) => (t2 map { _._2 } distinct) collect { case t if t != t1 => (t1, t) } }
    // 7585838
    SimpleDirectedGraph(domain.primitiveTasks,
                        edges
                        //domain.primitiveTasks.flatMap { a => domain.primitiveTasks collect { case b if a != b && applicable(a, b) && affects(a, b) => (a, b) } }
                       )
  }
  val time2                               = System.currentTimeMillis()
  println("EDGELIST " + disablingGraph.edgeList.length + " of " + disablingGraph.vertices.size * (disablingGraph.vertices.size - 1) + " in " + (time2 - time1) / 1000.0)
  //println(disablingGraph.edgeList map { case (a, b) => a.name + " " + b.name } mkString ("\n"))
  val scc   = disablingGraph.stronglyConnectedComponents
  val time3 = System.currentTimeMillis()
  println(((scc map { _.size } groupBy { x => x }).toSeq.sortBy(_._1) map { case (k, s) => k + ":" + s.size } mkString (", ")) + " in " + (time3 - time2) / 1000.0)

  //Dot2PdfCompiler.writeDotToFile(disablingGraph, "disablingGraph.pdf")

  System exit 0


  override lazy val stateTransitionFormula: Seq[Clause] = stateTransitionFormulaOfLength(taskSequenceLength) ++
    Range(0, taskSequenceLength).flatMap(position => {
      //atMostOneOf(domain.primitiveTasks map { action(K - 1, position, _) })
      Nil
    })

  override lazy val goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  println("Exists-Step, plan length: " + taskSequenceLength)
}
