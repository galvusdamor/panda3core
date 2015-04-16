package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicCSPTest extends FlatSpec {

  val ca = Constant("a")
  val cb = Constant("b")
  val cx = Constant("x")
  val cy = Constant("y")
  val cz = Constant("z")

  val sort1    : Sort = Sort("sort1", Vector() :+ ca :+ cb, None)
  val sort1sub1: Sort = Sort("sort1sub1", Vector() :+ ca, None)
  val sort1sub2: Sort = Sort("sort1sub1", Vector() :+ cb, None)
  val sort2    : Sort = Sort("sort2", Vector() :+ cx :+ cy :+ cz, None)
  val sort2sub1: Sort = Sort("sort1sub1", Vector() :+ cx, None)
  val sort2sub2: Sort = Sort("sort1sub1", Vector() :+ cy :+ cz, None)

  val v1 = Variable(1, "v1", sort1)
  val v2 = Variable(2, "v2", sort1)
  val v3 = Variable(3, "v3", sort2)
  val v4 = Variable(4, "v4", sort2)
  val v5 = Variable(5, "v5", sort2)
  val v6 = Variable(6, "v6", sort2)
  val v7 = Variable(7, "v7", sort2)

  "CSP" must "support equal constraints on variables" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v1, v2), Equal(v1, Left(v2)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
  }

  it must "support equal constraints on variables and constants and infer transitivity" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v1, v2), Equal(v1, Left(v2)) :: Equal(v1, Right(ca)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v2) == Right(ca))
    assert(csp1.areCompatible(Left(v2), Right(ca)) == Some(true))
    assert(csp1.areCompatible(Right(ca), Left(v2)) == Some(true))
  }

  it must "support absolute inference along unequal constraints" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v1, v2), NotEqual(v1, Left(v2)) :: Equal(v1, Right(ca)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) != csp1.getRepresentative(v2))
    assert(csp1.getRepresentative(v1) == Right(ca))
    assert(csp1.getRepresentative(v2) == Right(cb))
  }

  it must "support partial inference along unequal constraints" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEqual(v3, Left(v4)) :: Equal(v3, Right(cx)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v3) != csp1.getRepresentative(v4))
    assert(csp1.getRepresentative(v3) == Right(cx))
    assert(csp1.getRepresentative(v4) == Left(v4))
    assert(csp1.reducedDomainOf(v4).forall(x => x == cy || x == cz))
    assert(csp1.reducedDomainOf(v4).exists(x => x == cy))
    assert(csp1.reducedDomainOf(v4).exists(x => x == cz))
  }

  it must "support detect unsolvable situations" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEqual(v3, Left(v4)) :: Equal(v3, Right(cx)) :: Equal(v4, Right(cx)) :: Nil)
    assert(csp1.areCompatible(v3, v4) == Some(false))
    assert(csp1.areCompatible(Left(v3), Left(v4)) == Some(false))
    assert(csp1.isSolvable == Some(false))

    val csp2: SymbolicCSP = SymbolicCSP(Set(v1), Equal(v1, Right(ca)) :: OfSort(v1, sort1sub2) :: Nil)
    assert(csp2.isSolvable == Some(false))

    val csp3: SymbolicCSP = SymbolicCSP(Set(v1, v2), Equal(v1, Right(ca)) :: Equal(v2, Right(cb)) :: Equal(v1, Left(v2)) :: Nil)
    assert(csp3.isSolvable == Some(false))
  }

  it must "support detect unsolvable situations in advanced" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v3, v4, v5), Equal(v3, Left(v4)) :: Equal(v3, Right(cx)) :: Equal(v5, Right(cy)) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v4) == Right(cx))
    assert(csp1.getRepresentative(v5) == Right(cy))
    assert(csp1.areCompatible(v4, v5) == Some(false))
  }

  it must "support OfSort and infer" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), OfSort(v1, sort1sub1) :: NotEqual(v2, Left(v1)) :: OfSort(v3, sort2sub2) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(ca))
    assert(csp1.getRepresentative(v2) == Right(cb))
    assert(csp1.reducedDomainOf(v3).forall(x => x == cy || x == cz))
    assert(csp1.reducedDomainOf(v3).exists(x => x == cy))
    assert(csp1.reducedDomainOf(v3).exists(x => x == cz))

    val csp2: SymbolicCSP = SymbolicCSP(Set(v1), Equal(v1, Right(ca)) :: OfSort(v1, sort1sub1) :: Nil)
    assert(csp2.isSolvable != Some(false))
    assert(csp2.getRepresentative(v1) == Right(ca))
  }

  it must "support NotOfSort and infer" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v1, v2, v3, v4), NotOfSort(v1, sort1sub1) :: NotEqual(v2, Left(v1)) :: NotOfSort(v3, sort2sub1) :: NotOfSort(v4, sort2sub2) :: Nil)
    assert(csp1.isSolvable != Some(false))
    assert(csp1.getRepresentative(v1) == Right(cb))
    assert(csp1.getRepresentative(v2) == Right(ca))
    assert(csp1.reducedDomainOf(v3).forall(x => x == cy || x == cz))
    assert(csp1.reducedDomainOf(v3).exists(x => x == cy))
    assert(csp1.reducedDomainOf(v3).exists(x => x == cz))
    assert(csp1.reducedDomainOf(v2).size == 1)
    assert(csp1.reducedDomainOf(v2).exists(_ == ca))
    assert(csp1.getRepresentative(v4) == Right(cx))
  }

  it must "allow adding constraints while keeping internal data structures" in {
    val csp1 : SymbolicCSP = SymbolicCSP(Set(v1), Nil)
    assert(csp1.isSolvable != Some(false))

    val csp2: SymbolicCSP = csp1.addConstraint(Equal(v1, Left(v2)))
    assert(csp2.isSolvable != Some(false))
    assert(csp2.getRepresentative(v1) == csp2.getRepresentative(v2))
    assert(csp2.reducedDomainOf(v1).forall(x => x == ca || x == cb))
    assert(csp2.reducedDomainOf(v1).exists(x => x == ca))
    assert(csp2.reducedDomainOf(v1).exists(x => x == cb))

    val csp3 = csp2.addConstraint(NotEqual(v4, Left(v5)))
    assert(csp3.isSolvable != Some(false))
    assert(csp3.getRepresentative(v1) == csp3.getRepresentative(v2))
    assert(csp3.reducedDomainOf(v1).forall(x => x == ca || x == cb))
    assert(csp3.reducedDomainOf(v1).exists(x => x == ca))
    assert(csp3.reducedDomainOf(v1).exists(x => x == cb))
    assert(csp3.getRepresentative(v4) != csp3.getRepresentative(v5))
    assert(csp3.reducedDomainOf(v4).forall(x => x == cx || x == cy || x == cz))
    assert(csp3.reducedDomainOf(v4).exists(x => x == cx))
    assert(csp3.reducedDomainOf(v4).exists(x => x == cy))
    assert(csp3.reducedDomainOf(v4).exists(x => x == cz))


    val csp4 = csp3.addConstraint(NotOfSort(v4, Sort("temp", Vector() :+ cx, None)))
    assert(csp4.isSolvable != Some(false))
    assert(csp4.reducedDomainOf(v4).forall(x => x == cy || x == cz))
    assert(csp4.reducedDomainOf(v4).exists(x => x == cy))
    assert(csp4.reducedDomainOf(v4).exists(x => x == cz))

    val csp5 = csp4.addConstraint(Equal(v5, Right(cy)))
    assert(csp5.isSolvable != Some(false))
    assert(csp5.getRepresentative(v5) == Right(cy))
    assert(csp5.getRepresentative(v4) == Right(cz))


    val csp6 = csp5.addConstraint(Equal(v5, Left(v6)))
    assert(csp6.isSolvable != Some(false))
    assert(csp6.getRepresentative(v5) == csp6.getRepresentative(v6))

    val csp7 = csp6.addConstraint(Equal(v1, Right(ca))).addConstraint(NotEqual(v1, Left(v2)))
    assert(csp7.isSolvable == Some(false))

    val csp8 = csp6.addConstraint(NotEqual(v5, Left(v1)))
    assert(csp8.isSolvable != Some(false))

    val csp9 = csp8.addConstraint(Equal(v6, Left(v7))).addConstraint(Equal(v5, Left(v7)))
    assert(csp9.isSolvable != Some(false))
    assert(csp9.getRepresentative(v5) == csp9.getRepresentative(v7))
  }

  it must "detect early aborts" in {
    val csp1 = SymbolicCSP(Set(), Nil).addConstraint(Equal(v1, Right(ca))).addConstraint(Equal(v1, Right(cb)))
    assert(csp1.reducedDomainOf(v1).size == 0)
  }

  "Finding solutions" must "work" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(), Nil).addConstraint(NotEqual(v1, Left(v2)))

    assert(csp1.isSolvable != Some(false))
    csp1.solution match {
      case None => assert(false) // it must have a solution
      case Some(solution : Map[Variable, Constant]) => {
        assert(solution(v1) != solution(v2))
        assert(solution(v1) == ca || solution(v1) == cb)
        assert(solution(v2) == ca || solution(v2) == cb)
      }
    }
  }

  "Unsolvable CSP" must "not have a solution" in {
    val csp1: SymbolicCSP = SymbolicCSP(Set(v3, v4), NotEqual(v3, Left(v4)) :: Equal(v3, Right(cx)) :: Equal(v4, Right(cx)) :: Nil)
    assert(csp1.solution == None)
  }

  "Checking compatibility" must "be possible" in {
    val csp: SymbolicCSP = SymbolicCSP(Set(v3, v4, v5), NotEqual(v4, cx) :: Nil)

    assert(csp.areCompatible(Left(v3), Left(v4)) == None)
    assert(csp.areCompatible(Left(v3), Right(cx)) == None)
    assert(csp.areCompatible(Right(cx), Left(v3)) == None)
    assert(csp.areCompatible(Left(v4), Right(cx)) == Some(false))
    assert(csp.areCompatible(Right(cx), Left(v4)) == Some(false))

    assert(csp.areCompatible(Right(cx), Right(cx)) == Some(true))
    assert(csp.areCompatible(Right(cx), Right(cy)) == Some(false))
  }

  "Checking equality" must "be possible" in {
    val csp: SymbolicCSP = SymbolicCSP(Set(v3, v4, v5, v6), Equal(v4, cx) :: Equal(v5, v6) :: Nil)

    // V-V
    assert(!csp.equal(v3, v4))
    assert(csp.equal(v5, v6))

    // V-C
    assert(!csp.equal(v3, cx))
    assert(!csp.equal(cx, v3))
    assert(csp.equal(v4, cx))
    assert(csp.equal(cx, v4))

    // C-C
    assert(csp.equal(cx, cx))
    assert(!csp.equal(cx, cy))
  }


  "Unsolvabe CSPs" must "be unsolvable" in {
    val csp = UnsolvableCSP

    assert(csp.reducedDomainOf(v1) == Nil)
    assert(csp.areCompatible(v1, v2) == Some(false))
    assert(csp.areCompatible(v1, v1) == Some(false))
    assert(csp.solution == None)

    v1 :: v2 :: v3 :: v4 :: v5 :: Nil foreach {v => assert(csp.getRepresentative(v) == Left(v))}
  }
}