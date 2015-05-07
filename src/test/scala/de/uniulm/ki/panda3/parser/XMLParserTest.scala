package de.uniulm.ki.panda3.parser

import de.uniulm.ki.panda3.domain.Domain
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class XMLParserTest extends FlatSpec {


  "Parsing Files " must "be possible without error" in {
    val dom: Domain = XMLParser.parseFromFile("src/test/resources/de/uniulm/ki/panda3/parser/AssemblyTask_domain.xml")
  }


}
