package de.uniulm.ki.panda3.plan.csp

import de.uniulm.ki.panda3.csp._
import de.uniulm.ki.panda3.logic.{Constant, Sort}
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicCSPTest extends FlatSpec {

  val sort1 : Sort = Sort("sort1", Vector() :+ Constant("a") :+ Constant("b"))
  val sort1sub1 : Sort = Sort("sort1sub1", Vector() :+ Constant("a"))
  val sort2 : Sort = Sort("sort2", Vector() :+ Constant("x") :+ Constant("y") :+ Constant("z"))
  val sort2sub1 : Sort = Sort("sort1sub1", Vector() :+ Constant("x"))
  val sort2sub2 : Sort = Sort("sort1sub1", Vector() :+ Constant("y") :+ Constant("z"))

  val v1 = Variable("v1", sort1)
  val v2 = Variable("v2", sort1)
  val v3 = Variable("v3", sort2)
  val v4 = Variable("v4", sort2)
  val v5 = Variable("v5", sort2)

  "CSP" must "support equal constraints on variables" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Set(Equals(v1, Left(v2))))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
  }

  it must "support equal constraints on variables and constants and infer transitivity" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Set(Equals(v1, Left(v2)), Equals(v1, Right(Constant("a")))))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v2) == Right(Constant("a")))
  }

  it must "support absolute inference along unequal constraints" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Set(NotEquals(v1, Left(v2)), Equals(v1, Right(Constant("a")))))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) != csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v1) == Right(Constant("a")))
    assert(csp1.getRepresentative(v2) == Right(Constant("b")))
  }

  it must "support partial inference along unequal constraints" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4), Set(NotEquals(v3, Left(v4)), Equals(v3, Right(Constant("x")))))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v3) != csp1.getRepresentative(v4))
    assert(csp1.getRepresentative(v3) == Right(Constant("x")))
    assert(csp1.getRepresentative(v4) == Left(v4))
    assert(csp1.reducedDomainOf(v4).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v4).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v4).exists(x => x == Constant("z")))
  }

  it must "support detect unsolvable situations" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4), Set(NotEquals(v3, Left(v4)), Equals(v3, Right(Constant("x"))), Equals(v4, Right(Constant("x")))))
    assert(csp1.isSolvable == Some(false))
  }

  it must "support detect unsolvable situations in advanced" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4, v5), Set(Equals(v3, Left(v4)), Equals(v3, Right(Constant("x"))), Equals(v5, Right(Constant("y")))))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v4) == Right(Constant("x")))
    assert(csp1.getRepresentative(v5) == Right(Constant("y")))
    assert(csp1.areCompatible(v4, v5) == Some(false))
  }

  it must "support OfSort and infer" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), Set(OfSort(v1, sort1sub1), NotEquals(v2, Left(v1)), OfSort(v3, sort2sub2)))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(Constant("a")))
    assert(csp1.getRepresentative(v2) == Right(Constant("b")))
    assert(csp1.reducedDomainOf(v3).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("z")))
  }

  it must "support NotOfSort and infer" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), Set(NotOfSort(v1, sort1sub1), NotEquals(v2, Left(v1)), NotOfSort(v3, sort2sub1), NotOfSort(v4, sort2sub2)))
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(Constant("b")))
    assert(csp1.getRepresentative(v2) == Right(Constant("a")))
    assert(csp1.reducedDomainOf(v3).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("z")))
    assert(csp1.getRepresentative(v4) == Right(Constant("x")))
  }

  it must "allow adding constraints while keeping internal data structures" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1), Set())
    assert(csp1.isSolvable != Some(false))

    val csp2 : CSP = csp1.addConstraint(Equals(v1, Left(v2)))
    assert(csp2.isSolvable != Some(false))
    assert(csp2.getRepresentative(v1) == csp2.getRepresentative(v2))
    assert(csp2.reducedDomainOf(v1).forall(x => x == Constant("a") || x == Constant("b")))
    assert(csp2.reducedDomainOf(v1).exists(x => x == Constant("a")))
    assert(csp2.reducedDomainOf(v1).exists(x => x == Constant("b")))

    val csp3 = csp2.addConstraint(NotEquals(v4, Left(v5)))
    assert(csp3.isSolvable != Some(false))
    assert(csp3.getRepresentative(v1) == csp3.getRepresentative(v2))
    assert(csp3.reducedDomainOf(v1).forall(x => x == Constant("a") || x == Constant("b")))
    assert(csp3.reducedDomainOf(v1).exists(x => x == Constant("a")))
    assert(csp3.reducedDomainOf(v1).exists(x => x == Constant("b")))
    assert(csp3.getRepresentative(v4) != csp3.getRepresentative(v5))
    assert(csp3.reducedDomainOf(v4).forall(x => x == Constant("x") || x == Constant("y") || x == Constant("z")))
    assert(csp3.reducedDomainOf(v4).exists(x => x == Constant("x")))
    assert(csp3.reducedDomainOf(v4).exists(x => x == Constant("y")))
    assert(csp3.reducedDomainOf(v4).exists(x => x == Constant("z")))


    val csp4 = csp3.addConstraint(NotOfSort(v4, Sort("temp", Vector() :+ Constant("x"))))
    assert(csp4.isSolvable != Some(false))
    assert(csp4.reducedDomainOf(v4).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp4.reducedDomainOf(v4).exists(x => x == Constant("y")))
    assert(csp4.reducedDomainOf(v4).exists(x => x == Constant("z")))

    val csp5 = csp4.addConstraint(Equals(v5, Right(Constant("y"))))
    assert(csp5.isSolvable != Some(false))
    assert(csp5.getRepresentative(v5) == Right(Constant("y")))
    assert(csp5.getRepresentative(v4) == Right(Constant("z")))
  }

}