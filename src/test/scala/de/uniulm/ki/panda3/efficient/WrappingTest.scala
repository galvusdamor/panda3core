package de.uniulm.ki.panda3.efficient

import java.io.FileInputStream

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption, ExpandSortHierarchy}
import de.uniulm.ki.panda3.symbolic.csp.{Equal, CSP}
import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask, Domain, HasExampleProblem4}
import de.uniulm.ki.panda3.symbolic.logic.{Variable, Sort}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}
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
    val cwaApplied = ClosedWorldAssumption(expandedSorts, (true,Set[String]()))
    val reducedFormula = ToPlainFormulaRepresentation(cwaApplied, ())

    reducedFormula
  }

  "Creating a wrapper" must "not crash" in {
    wrapperExample4 = Wrapping(domain4, plan2WithTwoLinks)
    wrapperXMLDomain = Wrapping(hierarchicalDomainAndProblem)
  }

  var efficientDomainExample4: EfficientDomain = null

  "Computing the efficient Representation of the domain" must "not crash" in {
    efficientDomainExample4 = wrapperExample4.efficientDomain
    val efficientDomainXMLDomain = wrapperXMLDomain.efficientDomain
  }

  it must "produce a reasonable domain" in {
    assert(efficientDomainExample4.subSortsForSort.length == 1)
    assert(efficientDomainExample4.subSortsForSort(0).length == 0)
    assert(efficientDomainExample4.sortsOfConstant.length == 4)
    efficientDomainExample4.sortsOfConstant foreach { arr => assert(arr.length == 1); assert(arr(0) == 0) }
    assert(efficientDomainExample4.predicates.length == 2)
    efficientDomainExample4.predicates foreach { arr => assert(arr.length == 1); assert(arr(0) == 0) }


    assert(efficientDomainExample4.tasks.length == 13)
    val expectedTasks: Seq[Task] = (exampleDomain2.tasks :+ initTaskOfPlanOfDecompositionMethod3 :+ goalTaskOfPlanOfDecompositionMethod3) ++ (plan2WithTwoLinks.initAndGoal map { _.schema })
    expectedTasks map { t => (t, wrapperExample4.unwrap(t)) } foreach { case (t, i) =>
      val efficientTask = efficientDomainExample4.tasks(i)
      val symTask = t.asInstanceOf[ReducedTask]
      WrappingChecker.assertEqual(symTask, efficientTask, wrapperExample4)
    }
  }


  var efficientInitialPlanExample4 : EfficientPlan = null
  var efficientInitialPlanXMLDomain: EfficientPlan = null

  "Unwrapping a plan" must "not crash and " in {
    efficientInitialPlanExample4 = wrapperExample4.unwrap(plan2WithTwoLinks)
    efficientInitialPlanXMLDomain = wrapperXMLDomain.unwrap(hierarchicalDomainAndProblem._2)
  }

  it must "lead to the correct plan" in {
    WrappingChecker.assertEqual(plan2WithTwoLinks, efficientInitialPlanExample4, wrapperExample4)
  }


  // TODO add a test after a decomposition method was applied

  "Wrapping a plan" must "not crash" in {
    val wrappedInitialPlanExample4 = wrapperExample4.wrap(efficientInitialPlanExample4)
    val wrappedInitialPlanXML = wrapperXMLDomain.wrap(efficientInitialPlanXMLDomain)
  }

  var effucientPlanExample4AdHocSort: EfficientPlan = null

  "unwrapping a plan containing an ad hoc sort" must "not crash" in {
    val adHocSort = Sort("Ad hoc sort", constantSort1(1) :: Nil, Nil)

    val adHocVariable = Variable(1, "instance_variable_" + 1 + "_sort1", adHocSort)
    val adHocPsAbstract2 = PlanStep(2, abstractTask2, adHocVariable :: Nil)

    val adHocCausalLinkInit2Abstract2P1 = CausalLink(psInit2, adHocPsAbstract2, psInit2.substitutedEffects.head)
    val adHocCausalLinkInit2Abstract2P2 = CausalLink(psInit2, adHocPsAbstract2, psInit2.substitutedEffects(1))

    // create a plan  init| -> a1 -> |goal (with one causal link)
    val adHocPlanSteps = psInit2 :: psGoal2 :: adHocPsAbstract2 :: Nil
    val adHocPlan2WithTwoLinks = Plan(adHocPlanSteps, adHocCausalLinkInit2Abstract2P1 :: adHocCausalLinkInit2Abstract2P2 :: Nil,
                                              TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, adHocPsAbstract2), adHocPlanSteps),
                                      CSP(Set(instance_variableSort1(1), adHocVariable), Equal(adHocVariable, psInit2.arguments.head) :: Nil), psInit2, psGoal2,
                                              AllModifications, AllFlaws, Map(), Map())


    effucientPlanExample4AdHocSort = wrapperExample4.unwrap(adHocPlan2WithTwoLinks)

  }

}