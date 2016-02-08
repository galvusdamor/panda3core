package de.uniulm.ki.panda3.efficient

import java.io.FileInputStream

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption, ExpandSortHierarchy}
import de.uniulm.ki.panda3.symbolic.csp.SymbolicCSP
import de.uniulm.ki.panda3.symbolic.domain.{Domain, HasExampleProblem4}
import de.uniulm.ki.panda3.symbolic.logic.{Variable, Sort}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import de.uniulm.ki.panda3.symbolic.plan.{SymbolicPlan, Plan}
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, CausalLink, PlanStep}
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

  var effucientPlanExample4AdHocSort: EfficientPlan = null

  "unwrapping a plan containing an ad hoc sort" must "not crash" in {
    val adHocSort = Sort("Ad hoc sort", constantSort1(1) :: Nil, Nil)

    val adHocVariable = Variable(1, "instance_variable_" + 1 + "_sort1", adHocSort)
    val adHocPsAbstract2 = PlanStep(2, abstractTask2, adHocVariable :: Nil, None, None)

    val adHocCausalLinkInit2Abstract2P1 = CausalLink(psInit2, adHocPsAbstract2, psInit2.substitutedEffects.head)
    val adHocCausalLinkInit2Abstract2P2 = CausalLink(psInit2, adHocPsAbstract2, psInit2.substitutedEffects(1))

    // create a plan  init| -> a1 -> |goal (with one causal link)
    val adHocPlanSteps = psInit2 :: psGoal2 :: adHocPsAbstract2 :: Nil
    val adHocPlan2WithTwoLinks = SymbolicPlan(adHocPlanSteps, adHocCausalLinkInit2Abstract2P1 :: adHocCausalLinkInit2Abstract2P2 :: Nil,
                                         SymbolicTaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, adHocPsAbstract2), adHocPlanSteps),
                                         SymbolicCSP(Set(instance_variableSort1(1),adHocVariable), Nil), psInit2, psGoal2)


    effucientPlanExample4AdHocSort = wrapperExample4.unwrap(adHocPlan2WithTwoLinks)

  }

}