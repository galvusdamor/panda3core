package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp.{CSP, Equals, SymbolicCSP, Variable}
import org.scalatest.FlatSpec

import scala.collection.immutable.HashSet

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class LiteralTest extends FlatSpec {

  val sort1 : Sort = Sort("sort1", Vector() :+ Constant("a") :+ Constant("c") :+ Constant("c"))
  val sort2 : Sort = Sort("sort2", Vector() :+ Constant("x") :+ Constant("y") :+ Constant("z"))

  val p1 : Predicate = Predicate("p1", sort1 :: sort1 :: Nil)
  val p2 : Predicate = Predicate("p2", sort1 :: sort2 :: Nil)
  val p3 : Predicate = Predicate("p3", sort1 :: sort1 :: sort1 :: sort1 :: Nil)

  val v1 : Variable = Variable("v1", sort1);
  val v2 : Variable = Variable("v2", sort1);
  val v3 : Variable = Variable("v3", sort1);
  val v4 : Variable = Variable("v4", sort1);
  val v5 : Variable = Variable("v5", sort2);


  "Checking equality" must "be possible" in {
    var csp : CSP = SymbolicCSP(HashSet(v1, v2, v3, v4, v5), Nil)

    var l1 : Literal = Literal(p1, false, v1 :: v2 :: Nil)
    var l2 : Literal = Literal(p1, true, v1 :: v1 :: Nil)
    var l3 : Literal = Literal(p2, false, v1 :: v5 :: Nil)

    assert((l1 =?= l1)(csp))
    assert((l2 =?= l2)(csp))
    assert((l3 =?= l3)(csp))
    assert(!(l1 =?= l2)(csp))
    assert(!(l1 =?= l3)(csp))
  }


  "Checking equality" must "be possible using constraings" in {
    var csp : CSP = SymbolicCSP(HashSet(v1, v2, v3, v4), Nil).addConstraint(Equals(v1, Left(v2)))

    var l1 : Literal = Literal(p1, false, v1 :: v2 :: Nil)
    var l2 : Literal = Literal(p1, false, v1 :: v1 :: Nil)
    var l3 : Literal = Literal(p1, false, v1 :: v3 :: Nil)

    assert((l1 =?= l2)(csp))
    assert(!(l1 =?= l3)(csp))

    csp = csp.addConstraint(Equals(v1, Right(Constant("a")))).addConstraint(Equals(v3, Right(Constant("a"))))

    assert((l1 =?= l2)(csp))
    assert((l1 =?= l3)(csp))
  }


  "Unification" must "be possible" in {

  }

}