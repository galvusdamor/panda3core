package de.uniulm.ki.panda3.symbolic

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object writer {

  def toPDDLIdentifier(id: String): String = {
    val removedSigns = id map { c => if (c == '?') c else if (c >= 'a' && c <= 'z') c else if (c >= 'A' && c <= 'Z') c else if (c >= '0' && c <= '9') c else '_' }
    if (removedSigns.charAt(0) >= '0' && removedSigns.charAt(0) <= '9') "p" + removedSigns else removedSigns
  }
}
