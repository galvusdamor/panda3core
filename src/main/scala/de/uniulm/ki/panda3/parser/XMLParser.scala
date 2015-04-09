package de.uniulm.ki.panda3.parser

import javax.xml.bind.{JAXBContext, Unmarshaller}

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.parser.xml.XMLDomain

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class XMLParser extends Parser {

  override def parseFromFile(filename: String): Domain = {
    val context: JAXBContext = JAXBContext.newInstance(Domain.getClass)
    val marshaller: Unmarshaller = context.createUnmarshaller()


    val dom: XMLDomain = marshaller.unmarshal(new java.io.File(filename)).asInstanceOf[XMLDomain]


    ???
  }
}