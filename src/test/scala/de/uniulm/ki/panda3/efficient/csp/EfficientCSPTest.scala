package de.uniulm.ki.panda3.efficient.csp

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class EfficientCSPTest extends FlatSpec {

  val domain  = EfficientDomain(Array(Array(),Array(),Array()),sortsOfConstant = Array(Array(0),Array(0),Array(0,1),Array(1)),Array())

  "Equvalence Inference" must "be possible" in {
    val csp = new EfficientCSP(domain,potentiallyConsistent = true).copy(Array(0,0,0))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE,0,1))
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(1))
    assert(csp.getRepresentativeVariable(0) != csp.getRepresentativeVariable(2))
    assert(csp.getRepresentativeVariable(1) != csp.getRepresentativeVariable(2))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE,1,2))
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(1))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(2))
    assert(csp.getRepresentativeVariable(1) == csp.getRepresentativeVariable(2))
  }

  it must "lead to value assignment if only one value is possible" in {
    val csp = new EfficientCSP(domain,potentiallyConsistent = true).copy(Array(0,1))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE,0,1))
    assert(!csp.isRepresentativeAVariable(0))
    assert(!csp.isRepresentativeAVariable(1))
    assert(csp.getRepresentativeConstant(0) == 2)
    assert(csp.getRepresentativeConstant(1) == 2)
  }
}
