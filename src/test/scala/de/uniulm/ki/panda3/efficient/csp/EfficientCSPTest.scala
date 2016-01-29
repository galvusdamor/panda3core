package de.uniulm.ki.panda3.efficient.csp

import java.io.{File, FileInputStream}
import java.util.Scanner

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import org.scalatest.FlatSpec

import scala.io.Source

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class EfficientCSPTest extends FlatSpec {

  val domain = EfficientDomain(Array(Array(), Array(), Array(), Array()), sortsOfConstant = Array(Array(0, 2), Array(0, 2), Array(0, 1), Array(1, 3)), Array(), Array(), Array())


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
    assert(csp.getVariableUnequalTo(2).isEmpty)
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

  val sudokuDomain = EfficientDomain(Array(Array()), Array(Array(0), Array(0), Array(0), Array(0), Array(0), Array(0), Array(0), Array(0), Array(0)), Array(), Array(), Array())

  def sudokuFToI(x: Int, y: Int): Int = 9 * x + y

  val sudokuCSP: EfficientCSP = {
    var buildCSP = new EfficientCSP(sudokuDomain, potentiallyConsistent = true)
    var i = 0
    while (i < 9 * 9) {
      buildCSP = buildCSP.addVariables(Array(0))
      i = i + 1
    }
    // add the constraints
    for (x <- Range(0, 9); y1 <- Range(0, 9); y2 <- Range(y1 + 1, 9)) {
      buildCSP.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, sudokuFToI(x, y1), sudokuFToI(x, y2)))
      buildCSP.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, sudokuFToI(y1, x), sudokuFToI(y2, x)))
    }
    for (fx <- Range(0, 3); fy <- Range(0, 3); ix1 <- Range(0, 3); ix2 <- Range(0, 3); iy1 <- Range(0, 3); iy2 <- Range(0, 3)) {
      if (ix1 != ix2 || iy1 != iy2)
        buildCSP.addConstraint(VariableConstraint(VariableConstraint.UNEQUALVARIABLE, sudokuFToI(fx * 3 + ix1, fy * 3 + iy1), sudokuFToI(fx * 3 + ix2, fy * 3 + iy2)))
    }

    buildCSP
  }

  "Edge Consistency for Sudokus" must "be correct" in {
    val kakoDir = new File("src/test/resources/de/uniulm/ki/panda3/efficient/csp/sudokuEdgeConsistency")
    assert(kakoDir.isDirectory)
    for (file <- kakoDir.listFiles() filter { _.getName.endsWith("in") }) {
      val copyCSP = sudokuCSP.copy()
      val sc: Scanner = new Scanner(new FileInputStream(file))
      for (x <- Range(0, 9)) {
        val line = sc.next()
        for (y <- Range(0, 9)) {
          if (line.charAt(y) != '0')
            copyCSP.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, sudokuFToI(x, y), line.charAt(y) - '1'))
          assert(copyCSP.isCSPInternallyConsistent())
        }
      }
      assert(copyCSP.potentiallyConsistent)
      // produce the output string
      val sb = new StringBuilder
      for (x <- Range(0, 9)) {
        for (y <- Range(0, 9)) {
          sb.append("{")
          val possibleValues = copyCSP.getRemainingDomain(sudokuFToI(x, y)).toArray.sortWith(_ < _)
          var i = 0
          while (i < possibleValues.length) {
            if (i != 0) sb.append(",")
            sb.append(possibleValues(i) + 1)
            i = i + 1
          }
          sb.append("}")
        }
        sb.append("\n")
      }
      val cspOutput: String = sb.toString()
      // get the correct string
      val inPath = file.getAbsolutePath
      val correctString: String = Source.fromFile(inPath.substring(0, inPath.length - 2) + "ans").mkString
      assert(correctString == cspOutput)
    }
  }


  def isSudokuSolution(efficientCSP: EfficientCSP): Boolean = (for (x <- Range(0, 9); y <- Range(0, 9)) yield (x, y)) forall { case (x, y) => !efficientCSP
    .isRepresentativeAVariable(sudokuFToI(x, y))
  }

  def solveSudoku(csp: EfficientCSP): Option[EfficientCSP] = if (!csp.potentiallyConsistent) None
  else if (isSudokuSolution(csp)) Some(csp)
  else {
    // min deg heuristic
    val minDeg: Int = ((for (x <- Range(0, 9); y <- Range(0, 9)) yield (x, y)) map { case (x, y) => (csp.getRemainingDomain(sudokuFToI(x, y)).size, sudokuFToI(x, y)) } filter {
      _._1 != 1
    }).min._2

    val i = csp.getRemainingDomain(minDeg).iterator
    var solution: Option[EfficientCSP] = None
    while (i.hasNext && solution.isEmpty) {
      val value = i.next()
      val newCSP = csp.copy()
      newCSP.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, minDeg, value))
      solution = solveSudoku(newCSP)
    }
    solution
  }


  "Sudokus" must "must be solved correctly" in {
    val kakoDir = new File("src/test/resources/de/uniulm/ki/panda3/efficient/csp/sudokuSolution")
    assert(kakoDir.isDirectory)
    for (file <- kakoDir.listFiles() filter { _.getName.endsWith("in") }) {
      val copyCSP = sudokuCSP.copy()
      val sc: Scanner = new Scanner(new FileInputStream(file))
      for (x <- Range(0, 9)) {
        val line = sc.next()
        for (y <- Range(0, 9)) {
          if (line.charAt(y) != '0')
            copyCSP.addConstraint(VariableConstraint(VariableConstraint.EQUALCONSTANT, sudokuFToI(x, y), line.charAt(y) - '1'))
          assert(copyCSP.isCSPInternallyConsistent())
        }
      }
      assert(copyCSP.potentiallyConsistent)
      val solutionOption = solveSudoku(copyCSP)
      assert(solutionOption.isDefined)
      val solution = solutionOption.get

      // produce the output string
      val sb = new StringBuilder
      for (x <- Range(0, 9)) {
        for (y <- Range(0, 9)) {
          val possibleValues = solution.getRemainingDomain(sudokuFToI(x, y)).toArray
          assert(possibleValues.length == 1)
          sb.append(possibleValues.head + 1)
        }
        sb.append("\n")
      }
      val cspOutput: String = sb.toString()
      // get the correct string
      val inPath = file.getAbsolutePath
      val correctString: String = Source.fromFile(inPath.substring(0, inPath.length - 2) + "ans").mkString
      assert(correctString == cspOutput)
    }
  }
}


































