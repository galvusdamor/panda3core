package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicUnionFindTest extends FlatSpec {

  val union : SymbolicUnionFind = new SymbolicUnionFind
  val v1 = Variable(1, "v1", someSort)
  val v2 = Variable(2, "v2", someSort)
  val v3 = Variable(3, "v3", someSort)
  val v4 = Variable(4, "v4", someSort)
  val v5 = Variable(5, "v5", someSort)

  def someSort: Sort = Sort("someSort", Vector())

  "Variables" must "be insertable and must be unequal by default" in {

    union.addVariable(v1)
    union.addVariable(v2)
    union.addVariable(v3)
    union.addVariable(v4)


    assert(union.getRepresentative(v1) == Left(v1))
    assert(union.getRepresentative(v2) == Left(v2))
    assert(union.getRepresentative(v3) == Left(v3))
    assert(union.getRepresentative(v4) == Left(v4))

    assert(union.getRepresentative(v1) != union.getRepresentative(v2))
    assert(union.getRepresentative(v2) != union.getRepresentative(v3))
    assert(union.getRepresentative(v3) != union.getRepresentative(v4))
  }

  it must "be possible to make them equal" in {
    assert(union.assertEqual(v1, Left(v2)))
    assert(union.getRepresentative(v1) == union.getRepresentative(v2))
  }

  it must "infer transitive equality" in {
    assert(union.assertEqual(v2, Left(v3)))
    assert(union.getRepresentative(v2) == union.getRepresentative(v3))
    assert(union.getRepresentative(v1) == union.getRepresentative(v3))
  }

  "Constants" must "be usable" in {
    assert(union.assertEqual(v4, Right(Constant("a"))))

    assert(union.getRepresentative(v4) == Right(Constant("a")))

    union.addVariable(v5)
    assert(union.assertEqual(v4, Left(v5)))
    assert(union.getRepresentative(v5) == (union.getRepresentative(v4)))
  }

  it must "be uneqal" in {
    assert(union.assertEqual(v4, Right(Constant("a"))))
    assert(union.getRepresentative(v4) == Right(Constant("a")))


    assert(union.assertEqual(v2, Right(Constant("b"))))
    assert(union.getRepresentative(v1) == Right(Constant("b")))
    assert(union.getRepresentative(v2) == Right(Constant("b")))
    assert(union.getRepresentative(v3) == Right(Constant("b")))
  }

  "Equal Constants" must "be equal" in {
    assert(union.assertEqual(v1, Right(Constant("b"))))
  }

  "Unequal Constants" must "be equal" in {
    assert(!union.assertEqual(v3, Right(Constant("a"))))
  }

  "UnionFinds" must "be clonable" in {
    val newUnion = new SymbolicUnionFind
    newUnion.cloneFrom(union)

    // check whether it was the same ...
    assert(newUnion.getRepresentative(v2) == newUnion.getRepresentative(v3))
    assert(newUnion.getRepresentative(v1) == newUnion.getRepresentative(v3))

    assert(newUnion.getRepresentative(v5) == newUnion.getRepresentative(v4))

    assert(newUnion.getRepresentative(v1) != newUnion.getRepresentative(v4))

    assert(newUnion.getRepresentative(v1) == Right(Constant("b")))
    assert(newUnion.getRepresentative(v2) == Right(Constant("b")))
    assert(newUnion.getRepresentative(v3) == Right(Constant("b")))
    assert(newUnion.getRepresentative(v4) == Right(Constant("a")))
    assert(newUnion.getRepresentative(v5) == Right(Constant("a")))

  }
}