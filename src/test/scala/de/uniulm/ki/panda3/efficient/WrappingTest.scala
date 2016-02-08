package de.uniulm.ki.panda3.efficient

import java.io.FileInputStream

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption, ExpandSortHierarchy}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, HasExampleProblem4}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
// scalastyle:off null
class WrappingTest extends FlatSpec with HasExampleProblem4 {

  var wrapperExample4             : Wrapping       = null
  var wrapperXMLDomain            : Wrapping       = null
  val hierarchicalDomainAndProblem: (Domain, Plan) = {

    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"))
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"), domAlone)

    val expandedSorts = ExpandSortHierarchy(domAndInitialPlan, ())
    val cwaApplied = ClosedWorldAssumption(expandedSorts, ())
    val reducedFormula = ToPlainFormulaRepresentation(cwaApplied, ())

    reducedFormula
  }

  "Creating a wrapper" must "not crash" in {
    wrapperExample4 = Wrapping(domain4, plan2WithTwoLinks)
    wrapperXMLDomain = Wrapping(hierarchicalDomainAndProblem)
  }

  "Computing the efficient Representation of the domain" must "not crash" in {
    val efficientDomainExample4 = wrapperExample4.efficientDomain
    val efficientDomainXMLDomain = wrapperXMLDomain.efficientDomain
  }

  var efficientInitialPlanExample4 : EfficientPlan = null
  var efficientInitialPlanXMLDomain: EfficientPlan = null

  "Unwrapping a plan" must "not crash" in {
    efficientInitialPlanExample4 = wrapperExample4.unwrap(plan2WithTwoLinks)
    efficientInitialPlanXMLDomain = wrapperXMLDomain.unwrap(hierarchicalDomainAndProblem._2)
  }

  "Wrapping a plan" must "not crash" in {
    val wrappedInitialPlanExample4 = wrapperExample4.wrap(efficientInitialPlanExample4)
    val wrappedInitialPlanXML = wrapperXMLDomain.wrap(efficientInitialPlanXMLDomain)
  }

}