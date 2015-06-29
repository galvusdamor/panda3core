package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.csp._
import org.scalatest.FlatSpec

import scala.collection.immutable.HashSet

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class LiteralTest extends FlatSpec {

  val sort1: Sort = Sort("sort1", Vector() :+ Constant("a") :+ Constant("b") :+ Constant("c"), Nil)
  val sort2: Sort = Sort("sort2", Vector() :+ Constant("x") :+ Constant("y") :+ Constant("z"), Nil)

  val p1 : Predicate = Predicate("p1", sort1 :: sort1 :: Nil)
  val p2 : Predicate = Predicate("p2", sort1 :: sort2 :: Nil)
  val p3 : Predicate = Predicate("p3", sort1 :: sort1 :: sort1 :: sort1 :: Nil)
  val p4: Predicate = Predicate("p4", sort1 :: Nil)


  val v1: Variable = Variable(1, "v1", sort1)
  val v2: Variable = Variable(2, "v2", sort1)
  val v3: Variable = Variable(3, "v3", sort1)
  val v4: Variable = Variable(4, "v4", sort1)
  val v5: Variable = Variable(5, "v5", sort2)


  "Checking equality" must "be possible" in {
    val csp : SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4, v5), Nil)

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l2: Literal = Literal(p1, isPositive = true, v1 :: v1 :: Nil)
    val l3: Literal = Literal(p2, isPositive = false, v1 :: v5 :: Nil)

    assert((l1 =?= l1)(csp))
    assert((l2 =?= l2)(csp))
    assert((l3 =?= l3)(csp))
    assert(!(l1 =?= l2)(csp))
    assert(!(l1 =?= l3)(csp))
  }


  "Checking equality" must "be possible using constraints" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(Equal(v1, v2))

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l2: Literal = Literal(p1, isPositive = false, v1 :: v1 :: Nil)
    val l3: Literal = Literal(p1, isPositive = false, v1 :: v3 :: Nil)

    assert((l1 =?= l2)(csp))
    assert(!(l1 =?= l3)(csp))

    val csp2 = csp.addConstraint(Equal(v1, Constant("a"))).addConstraint(Equal(v3, Constant("a")))

    assert((l1 =?= l2)(csp2))
    assert((l1 =?= l3)(csp2))
  }


  "Unification" must "be possible" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(Equal(v1, v2))

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l3: Literal = Literal(p1, isPositive = false, v1 :: v3 :: Nil)

    // compute the most general unifier
    val mgu = (l1 #?# l3)(csp)

    assert(mgu match { case Some(x) => true; case None => false})

    val unifier: Seq[VariableConstraint] = mgu match {case Some(x) => x}
    // exactly one unification is necessary
    assert(unifier.size == 1)
    // and this is "v2 equals v3"
    assert(unifier.head == Equal(v2, v3))

    // after unification, the expressions must be equal
    val equalCsp = csp.addConstraints(unifier)
    assert((l1 =?= l3)(equalCsp))
  }


  "Unification of literals with only a single argument" must "be possible" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2), Nil)

    val l1: Literal = Literal(p4, isPositive = false, v1 :: Nil)
    val l2: Literal = Literal(p4, isPositive = false, v2 :: Nil)

    // compute the most general unifier
    val mgu = (l1 #?# l2)(csp)

    assert(mgu match { case Some(x) => true; case None => false})

    val unifier: Seq[VariableConstraint] = mgu match {case Some(x) => x}
    // exactly one unification is necessary
    assert(unifier.size == 1)
    // and this is "v2 equals v3"
    assert(unifier.head == Equal(v1, v2) || unifier.head == Equal(v2, v1))

    // after unification, the expressions must be equal
    val equalCsp = csp.addConstraints(unifier)
    assert((l1 =?= l2)(equalCsp))
  }

  "Unification" must "be impossible for non-equal predicates" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(Equal(v1, v2))

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l3: Literal = Literal(p2, isPositive = false, v1 :: v5 :: Nil)

    // compute the most general unifier
    val mgu = (l1 #?# l3)(csp)

    assert(mgu == None)
  }


  "Unification" must "be impossible of the CSP says so" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(NotEqual(v2, v3))

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l3: Literal = Literal(p1, isPositive = false, v1 :: v3 :: Nil)

    // compute the most general unifier
    val mgu = (l1 #?# l3)(csp)

    assert(mgu == None)
  }

  "Differentiation" must "be possible for two Literals" in {
    val csp: SymbolicCSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(NotEqual(v2, v3))

    val l1: Literal = Literal(p1, isPositive = false, v1 :: v2 :: Nil)
    val l2: Literal = Literal(p1, isPositive = false, v1 :: v3 :: Nil)
    val l3: Literal = Literal(p1, isPositive = true, v3 :: v4 :: Nil)

    val diff1 = (l1 !?! l2)(csp)
    assert(diff1.size == 1)
    assert(diff1.head == NotEqual(v2, v3) || diff1.head == NotEqual(v3, v2))

    val diff2 = (l1 !?! l3)(csp)
    assert(diff2.size == 0)

    val diff3 = (l1 !?! l3.negate)(csp)
    assert(diff3.size == 2)
    assert(diff3 exists { p => p == NotEqual(v1, v3) || p == NotEqual(v3, v1)})
    assert(diff3 exists { p => p == NotEqual(v2, v4) || p == NotEqual(v4, v2)})
  }
}