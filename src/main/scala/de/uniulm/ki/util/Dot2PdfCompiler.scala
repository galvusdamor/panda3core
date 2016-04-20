package de.uniulm.ki.util

import sys.process._

/**
  * Compiles a given dot string into a pdf
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Dot2PdfCompiler {

  def writeDotToFile(dotObject: DotPrintable, file: String): Unit = writeDotToFile(dotObject.dotString, file)

  def writeDotToFile(dotString: String, file: String): Unit = {
    val prepString = dotString.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t").replaceAll("'", "\\'")
    ("echo -e " + prepString + "") #| ("dot -Tpdf -o " + file) !!
  }
}

trait DotPrintable {
  /** The DOT representation of the object */
  val dotString: String
}