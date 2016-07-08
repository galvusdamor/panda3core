package de.uniulm.ki.util

import java.io.File

import sys.process._

/**
  * Compiles a given dot string into a pdf
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Dot2PdfCompiler {

  def writeDotToFile[X](dotObject: DotPrintable[X], file: String): Unit = writeDotToFile(dotObject.dotString, file)

  def writeDotToFile(dotString: String, file: String): Unit = System.getProperty("os.name") match {
    case "NEVER" => //"Linux"
      // println(dotString)
      val prepString = dotString.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t").replaceAll("'", "\\'")
      ("echo -e " + prepString + "") #| ("dot -Tpdf -o " + file) !!

    case _ =>
      val tempFile = File.createTempFile("__panda_dot_print", "dot")

      de.uniulm.ki.util.writeStringToFile(dotString, tempFile)

      ("cmd.exe /c dot -Tpdf " + tempFile.getAbsolutePath + " -o " + file) !!

  }
}

trait DotPrintable[OptionType] {
  /** The DOT representation of the object */
  val dotString: String

  /** The DOT representation of the object with options */
  def dotString(options: OptionType): String
}