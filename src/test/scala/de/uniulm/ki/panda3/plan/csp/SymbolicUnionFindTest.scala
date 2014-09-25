package de.uniulm.ki.panda3.plan.csp

import de.uniulm.ki.panda3.csp.{SymbolicUnionFind, Variable}
import de.uniulm.ki.panda3.logic.{Constant, Sort}
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicUnionFindTest extends FlatSpec {

  def someSort : Sort = Sort("someSort", Vector())

  val union : SymbolicUnionFind = new SymbolicUnionFind

  "Variables" must "be insertable and must be unequal by default" in {
    union.addVariable(Variable("v1", someSort))
    union.addVariable(Variable("v2", someSort))
    union.addVariable(Variable("v3", someSort))
    union.addVariable(Variable("v4", someSort))


    assert(union.getRepresentative(Variable("v1", someSort)) == Left(Variable("v1", someSort)))
    assert(union.getRepresentative(Variable("v2", someSort)) == Left(Variable("v2", someSort)))
    assert(union.getRepresentative(Variable("v3", someSort)) == Left(Variable("v3", someSort)))
    assert(union.getRepresentative(Variable("v4", someSort)) == Left(Variable("v4", someSort)))

    assert(union.getRepresentative(Variable("v1", someSort)) != union.getRepresentative(Variable("v2", someSort)))
    assert(union.getRepresentative(Variable("v2", someSort)) != union.getRepresentative(Variable("v3", someSort)))
    assert(union.getRepresentative(Variable("v3", someSort)) != union.getRepresentative(Variable("v4", someSort)))
  }

  it must "be possible to make them equal" in {
    assert(union.assertEqual(Variable("v1", someSort), Left(Variable("v2", someSort))))
    assert(union.getRepresentative(Variable("v1", someSort)) == union.getRepresentative(Variable("v2", someSort)))
  }

  it must "infer transitive equality" in {
    assert(union.assertEqual(Variable("v2", someSort), Left(Variable("v3", someSort))))
    assert(union.getRepresentative(Variable("v2", someSort)) == union.getRepresentative(Variable("v3", someSort)))
    assert(union.getRepresentative(Variable("v1", someSort)) == union.getRepresentative(Variable("v3", someSort)))
  }

  "Constants" must "be usable" in {
    assert(union.assertEqual(Variable("v4", someSort), Right(Constant("a"))))

    assert(union.getRepresentative(Variable("v4", someSort)) == Right(Constant("a")))
  }

  it must "be uneqal" in {
    assert(union.assertEqual(Variable("v4", someSort), Right(Constant("a"))))
    assert(union.getRepresentative(Variable("v4", someSort)) == Right(Constant("a")))


    assert(union.assertEqual(Variable("v2", someSort), Right(Constant("b"))))
    assert(union.getRepresentative(Variable("v2", someSort)) == Right(Constant("b")))
  }

  "Equal Constants" must "be equal" in {
    assert(union.assertEqual(Variable("v1", someSort), Right(Constant("b"))))
  }

  "Unequal Constants" must "be equal" in {
    assert(!union.assertEqual(Variable("v3", someSort), Right(Constant("a"))))
  }
}