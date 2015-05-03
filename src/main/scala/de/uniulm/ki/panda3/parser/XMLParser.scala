package de.uniulm.ki.panda3.parser


import java.io.FileInputStream
import javax.xml.bind.{JAXBContext, Unmarshaller}
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.sax.SAXSource

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.parser.xml._
import de.uniulm.ki.panda3.logic.{Constant, Sort}
import org.xml.sax.XMLReader

import scala.collection.JavaConversions
import scala.xml.InputSource
import scala.collection._

/**
 *
 *
 * @author Kadir Dede (kadir.dede@uni-ulm.de)
 */

class XMLParser extends Parser {

  @Override
  override def parseFromFile(filename: String): Domain = {

    val context: JAXBContext = JAXBContext.newInstance(Domain.getClass)
    val marshaller: Unmarshaller = context.createUnmarshaller()
    val spf: SAXParserFactory = SAXParserFactory.newInstance()
    spf.setXIncludeAware(true)
    spf.setNamespaceAware(true)
    spf.setValidating(true); // Not required for JAXB/XInclude
    val xr: XMLReader = spf.newSAXParser().getXMLReader()
    val source: SAXSource = new SAXSource(xr, new InputSource(new FileInputStream(filename)))
    val dom: XMLDomain = marshaller.unmarshal(source).asInstanceOf[XMLDomain]



    val constantMap : mutable.Map[String, Seq[Constant]] = mutable.Map()
    val doneSort : mutable.Set[String] = mutable.Set()

    /**
     *
     * @param input the whole sequence of constants from XMLDomain
     * @return the whole sequence of constants used in Domain
     *         also: generates a map of type (String -> Seq[Constant])
     */
    def createConstantSeq(input : Seq[ConstantDeclaration]) : Seq[Constant] = input map {
      const =>
        val sort = const.getSort.asInstanceOf[SortDeclaration].getName
        if (constantMap contains sort) {
          val buffer = Seq(Constant(const.getName)) ++ constantMap(sort)
          constantMap -= sort
          constantMap += (sort -> buffer)
        } else {
          constantMap += (sort -> Seq(Constant(const.getName)))
        }
        Constant(const.getName)
    }
    val constantSeq = createConstantSeq(JavaConversions.asScalaBuffer(dom.getConstantDeclaration))
    // needed for Domain

    // from here: implementation not tested yet!


    var sortSeq : Seq[Sort] = Nil
    // deeded for Domain
    /**
     *
     * @param input a single SortDeclaration
     * @return the proper Object of type Sort
     *         also: generates sortSeq and doneSort by adding already constructed sorts
     */
    def createSortSeq(input : SortDeclaration) : Sort = {
      val subSorts : Seq[SubSort] = JavaConversions.asScalaBuffer(input.getSubSort)
      var subSortSeq : Seq[Sort] = Nil
      for (x <- subSorts) {
        subSortSeq ++= Seq(createSortSeq(x.getSort.asInstanceOf[SortDeclaration]))
      }
      val res = Sort(input.getName, constantMap(input.getName), subSortSeq)
      if (!(doneSort contains res.name)) {
        sortSeq ++= Seq(res)
        doneSort += res.name
      }
      res
    }
    /**
     *
     * @param input the whole sequence of sorts from XMLDomain calling createSortSeq if not already constructed
     */
    def checkSorts(input : Seq[SortDeclaration]) = {
      for(x <- input)
        if(doneSort contains x.getName) createSortSeq(x)
    }
    checkSorts(JavaConversions.asScalaBuffer(dom.getSortDeclaration))


    // already done:
    // Sequence of Constants (constantSeq)
    // Sequence of Sorts     (sortSeq)
    // still to do:
    // Sequence of Predicates
    // Sequence of Tasks
    // Sequence of Decomposition Methods
    // Sequence of Decomposition Axioms

    ???

  }

}
