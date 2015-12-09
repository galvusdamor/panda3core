package de.uniulm.ki.panda3.efficient.csp

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class EfficientCSPTest extends FlatSpec {

  val domain = EfficientDomain(Array(Array(), Array(), Array(),Array()), sortsOfConstant = Array(Array(0,2), Array(0,2), Array(0, 1), Array(1,3)), Array())


  def assignSingleVariableToValue(): EfficientCSP = {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeVariable(0) == 0)
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 0))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeConstant(0) == 0)
    csp
  }

  "Assigning Constants to variables" must "be possible" in {
    val csp = assignSingleVariableToValue()
  }

  "Assigning Constants to variables" must "be only be possible with one value" in {
    val csp = assignSingleVariableToValue()
    // try assign the same value
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 0))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeConstant(0) == 0)
    // assign another value
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(!csp.potentiallyConsistent)
  }

  "Equvalence Inference" must "be possible" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 0, 0))


    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(1))
    assert(csp.getRepresentativeVariable(0) != csp.getRepresentativeVariable(2))
    assert(csp.getRepresentativeVariable(1) != csp.getRepresentativeVariable(2))


    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE, 1, 2))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(1))
    assert(csp.getRepresentativeVariable(0) == csp.getRepresentativeVariable(2))
    assert(csp.getRepresentativeVariable(1) == csp.getRepresentativeVariable(2))
  }

  it must "lead to value assignment if only one value is possible" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 1))


    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(!csp.isRepresentativeAVariable(1))
    assert(csp.getRepresentativeConstant(0) == 2)
    assert(csp.getRepresentativeConstant(1) == 2)
  }

  it must "correctly propagate variables that were set to constants" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 0))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 2))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(!csp.isRepresentativeAVariable(1))
    assert(csp.getRepresentativeConstant(0) == 2)
    assert(csp.getRepresentativeConstant(1) == 2)
  }
  "Unequality" must "be stored correctly" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 0, 0))
    csp.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeVariable(0) == 0)
    assert(csp.getRepresentativeVariable(0) == 0)
    assert(csp.getRepresentativeVariable(0) == 0)
    assert(csp.getVariableUnequalTo(0).size == 1)
    assert(csp.getVariableUnequalTo(0).contains(1))
    assert(csp.getVariableUnequalTo(1).size == 1)
    assert(csp.getVariableUnequalTo(1).contains(0))
    assert(csp.getVariableUnequalTo(2).size == 0)
  }

  it must "lead to the removal of possible values" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 0))

    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 1))
    csp.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, 1, 0))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeConstant(0) == 1)
    assert(csp.isRepresentativeAVariable(1))
    assert(csp.getRepresentativeVariable(1) == 1)
    assert(csp.getRemainingDomain(1).contains(0))
    assert(!csp.getRemainingDomain(1).contains(1))
    assert(csp.getRemainingDomain(1).contains(2))
  }

  it must "be correctly be propagated through equality constants" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0, 0, 0))

    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, 0, 2))
    csp.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, 1, 2))
    csp.addConstraint(VariableConstraint(VariableConstraint.EQUALVARIABLE, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(!csp.isRepresentativeAVariable(1))
    assert(csp.isRepresentativeAVariable(2))
    assert(csp.getRepresentativeConstant(0) == 2)
    assert(csp.getRepresentativeConstant(1) == 2)
    assert(csp.getRepresentativeVariable(2) == 2)
    assert(csp.getRemainingDomain(2).contains(0))
    assert(csp.getRemainingDomain(2).contains(1))
    assert(!csp.getRemainingDomain(2).contains(2))
  }

  "OfSort Constraints" must "be handled correctly" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.OFSORT, 0, 2))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeVariable(0) == 0)
    assert(csp.getRemainingDomain(0).size == 2)
    assert(csp.getRemainingDomain(0).contains(0))
    assert(csp.getRemainingDomain(0).contains(1))
  }

  it must "lead to a variable be set to a constant if appropriate" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.OFSORT, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeConstant(0) == 2)
  }

  it must "lead to an non-solvable CSP" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.OFSORT, 0, 3))
    assert(csp.isCSPInternallyConsistent())
    assert(!csp.potentiallyConsistent)
  }

  "NotOfSort Constraints" must "be handled correctly" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.NOTOFSORT, 0, 1))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeVariable(0) == 0)
    assert(csp.getRemainingDomain(0).size == 2)
    assert(csp.getRemainingDomain(0).contains(0))
    assert(csp.getRemainingDomain(0).contains(1))
  }

  it must "lead to a variable be set to a constant if appropriate" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.NOTOFSORT, 0, 2))
    assert(csp.isCSPInternallyConsistent())
    assert(csp.potentiallyConsistent)
    assert(!csp.isRepresentativeAVariable(0))
    assert(csp.getRepresentativeConstant(0) == 2)
  }

  it must "lead to an non-solvable CSP" in {
    val csp = new EfficientCSP(domain, potentiallyConsistent = true).addVariables(Array(0))
    csp.addConstraint(VariableConstraint(VariableConstraint.NOTOFSORT, 0, 0))
    assert(csp.isCSPInternallyConsistent())
    assert(!csp.potentiallyConsistent)
  }
}