package de.uniulm.ki.panda3.csp

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
  val sort1sub2 : Sort = Sort("sort1sub1", Vector() :+ Constant("b"))
  val sort2 : Sort = Sort("sort2", Vector() :+ Constant("x") :+ Constant("y") :+ Constant("z"))
  val sort2sub1 : Sort = Sort("sort1sub1", Vector() :+ Constant("x"))
  val sort2sub2 : Sort = Sort("sort1sub1", Vector() :+ Constant("y") :+ Constant("z"))

  val v1 = Variable("v1", sort1)
  val v2 = Variable("v2", sort1)
  val v3 = Variable("v3", sort2)
  val v4 = Variable("v4", sort2)
  val v5 = Variable("v5", sort2)
  val v6 = Variable("v6", sort2)
  val v7 = Variable("v7", sort2)

  "CSP" must "support equal constraints on variables" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Equals(v1, Left(v2)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
  }

  it must "support equal constraints on variables and constants and infer transitivity" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Equals(v1, Left(v2)) :: Equals(v1, Right(Constant("a"))) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v2) == Right(Constant("a")))
  }

  it must "support absolute inference along unequal constraints" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2), NotEquals(v1, Left(v2)) :: Equals(v1, Right(Constant("a"))) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) != csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v1) == Right(Constant("a")))
    assert(csp1.getRepresentative(v2) == Right(Constant("b")))
  }

  it must "support partial inference along unequal constraints" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEquals(v3, Left(v4)) :: Equals(v3, Right(Constant("x"))) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v3) != csp1.getRepresentative(v4))
    assert(csp1.getRepresentative(v3) == Right(Constant("x")))
    assert(csp1.getRepresentative(v4) == Left(v4))
    assert(csp1.reducedDomainOf(v4).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v4).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v4).exists(x => x == Constant("z")))
  }

  it must "support detect unsolvable situations" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEquals(v3, Left(v4)) :: Equals(v3, Right(Constant("x"))) :: Equals(v4, Right(Constant("x"))) :: Nil)
    assert(csp1.areCompatible(v3, v4) == Some(false))
    assert(csp1.isSolvable == Some(false))

    val csp2 : SymbolicCSP = SymbolicCSP(Set(v1), Equals(v1, Right(Constant("a"))) :: OfSort(v1, sort1sub2) :: Nil)
    assert(csp2.isSolvable == Some(false))

    val csp3 : SymbolicCSP = SymbolicCSP(Set(v1, v2), Equals(v1, Right(Constant("a"))) :: Equals(v2, Right(Constant("b"))) :: Equals(v1, Left(v2)) :: Nil)
    assert(csp3.isSolvable == Some(false))
  }

  it must "support detect unsolvable situations in advanced" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4, v5), Equals(v3, Left(v4)) :: Equals(v3, Right(Constant("x"))) :: Equals(v5, Right(Constant("y"))) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v4) == Right(Constant("x")))
    assert(csp1.getRepresentative(v5) == Right(Constant("y")))
    assert(csp1.areCompatible(v4, v5) == Some(false))
  }

  it must "support OfSort and infer" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), OfSort(v1, sort1sub1) :: NotEquals(v2, Left(v1)) :: OfSort(v3, sort2sub2) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(Constant("a")))
    assert(csp1.getRepresentative(v2) == Right(Constant("b")))
    assert(csp1.reducedDomainOf(v3).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("z")))

    val csp2 : SymbolicCSP = SymbolicCSP(Set(v1), Equals(v1, Right(Constant("a"))) :: OfSort(v1, sort1sub1) :: Nil)
    assert(csp2.isSolvable != Some(false))
    assert(csp2.getRepresentative(v1) == Right(Constant("a")))
  }

  it must "support NotOfSort and infer" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), NotOfSort(v1, sort1sub1) :: NotEquals(v2, Left(v1)) :: NotOfSort(v3, sort2sub1) :: NotOfSort(v4, sort2sub2) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(Constant("b")))
    assert(csp1.getRepresentative(v2) == Right(Constant("a")))
    assert(csp1.reducedDomainOf(v3).forall(x => x == Constant("y") || x == Constant("z")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("y")))
    assert(csp1.reducedDomainOf(v3).exists(x => x == Constant("z")))
    assert(csp1.reducedDomainOf(v2).size == 1)
    assert(csp1.reducedDomainOf(v2).exists(_ == Constant("a")))
    assert(csp1.getRepresentative(v4) == Right(Constant("x")))
  }

  it must "allow adding constraints while keeping internal data structures" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1), Nil)
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


    val csp6 = csp5.addConstraint(Equals(v5, Left(v6)))
    assert(csp6.isSolvable != Some(false))
    assert(csp6.getRepresentative(v5) == csp6.getRepresentative(v6))

    val csp7 = csp6.addConstraint(Equals(v1, Right(Constant("a")))).addConstraint(NotEquals(v1, Left(v2)))
    assert(csp7.isSolvable == Some(false))

    val csp8 = csp6.addConstraint(NotEquals(v5, Left(v1)))
    assert(csp8.isSolvable != Some(false))

    val csp9 = csp8.addConstraint(Equals(v6, Left(v7))).addConstraint(Equals(v5, Left(v7)))
    assert(csp9.isSolvable != Some(false))
    assert(csp9.getRepresentative(v5) == csp9.getRepresentative(v7))
  }

  it must "detect early aborts" in {
    val csp1 = SymbolicCSP(Set(), Nil).addConstraint(Equals(v1, Right(Constant("a")))).addConstraint(Equals(v1, Right(Constant("b"))))
    assert(csp1.reducedDomainOf(v1).size == 0)
  }

  "Finding solutions" must "work" in {
    val csp1 : CSP = SymbolicCSP(Set(), Nil).addConstraint(NotEquals(v1, Left(v2)))

    assert(csp1.isSolvable != Some(false))
    csp1.solution match {
      case None => assert(false) // it must have a solution
      case Some(solution : Map[Variable, Constant]) => {
        assert(solution(v1) != solution(v2))
        assert(solution(v1) == Constant("a") || solution(v1) == Constant("b"))
        assert(solution(v2) == Constant("a") || solution(v2) == Constant("b"))
      }
    }
  }

  "Unsolvable CSP" must "not have a solution" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEquals(v3, Left(v4)) :: Equals(v3, Right(Constant("x"))) :: Equals(v4, Right(Constant("x"))) :: Nil)
    assert(csp1.solution == None)
  }

}