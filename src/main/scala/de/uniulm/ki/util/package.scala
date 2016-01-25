package de.uniulm.ki

import java.io.{PrintWriter, File}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object util {
  def writeStringToFile(s: String, file: File): Unit = {
    Some(new PrintWriter(file)).foreach { p => p.write(s); p.close() }
  }
}
