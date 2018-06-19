// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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

  def writeDotToFile(dotString: String, file: String): Unit = writeDotToFileWithType(dotString, file, "pdf")

  def writeDotToFilePNG(dotString: String, file: String): Unit = writeDotToFileWithType(dotString, file, "png")


  private def writeDotToFileWithType(dotString: String, file: String, plotType: String): Unit = {
    val tempFile = File.createTempFile("__panda_dot_print", "dot")

    de.uniulm.ki.util.writeStringToFile(dotString, tempFile)

    try {
      System.getProperty("os.name") match {
        case osname if osname.toLowerCase startsWith "windows" =>
          ("cmd.exe /c dot -T" + plotType + " " + tempFile.getAbsolutePath + " -o " + file + " -Nfontname=\"Monospace\"") !!
        case _                                                 => // Linux and all the others
          ("dot -T" + plotType + " " + tempFile.getAbsolutePath + " -o " + file + " -Nfontname=\"Monospace\"") !!
      }
    } catch {
      case _: Throwable =>
    }
  }
}

trait DotPrintable[OptionType] {
  /** The DOT representation of the object */
  val dotString: String

  /** The DOT representation of the object with options */
  def dotString(options: OptionType): String
}
