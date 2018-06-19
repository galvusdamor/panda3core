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

package de.uniulm.ki.panda3.symbolic.parser

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class XMLParserTest extends FlatSpec {


  "Parsing hon-hierarchical Files " must "be possible without error" in {
    val dom: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"))

    assert(dom.constants.isEmpty)
    assert(dom.sorts.size == 34)
    assert(dom.predicates.size == 12)
    assert(dom.tasks.size == 16)

    // checking one sort
    val cableAudioAndVideoSortOpt = dom.sorts find { _.name == "Cable_Audio_and_Video" }
    assert(cableAudioAndVideoSortOpt.isDefined)
    val cableAudioAndVideoSort = cableAudioAndVideoSortOpt.get

    assert(cableAudioAndVideoSort.elements.isEmpty)
    assert(cableAudioAndVideoSort.subSorts.size == 2)
    assert(cableAudioAndVideoSort.subSorts forall { s => s.name == "Cable_HDMI" || s.name == "Cable_Scart_CinchVideo_CinchStereo" })
  }

  "Parsing hierarchical Files " must "be possible without error" in {
    val dom: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"))

    assert(dom.constants.isEmpty)
    assert(dom.sorts.size == 40)
    assert(dom.predicates.size == 63)
    assert(dom.tasks.size == 138)

    assert((dom.tasks count { _.isPrimitive }) == 88)
    assert((dom.tasks count { !_.isPrimitive }) == 50)

    // checking some tasks
    // a grounded one
    val task1Opt = dom.tasks find { _.name == "press_People.Smallplus_AlreadyPressed" }
    assert(task1Opt.isDefined)
    val generalTypeTask1 = task1Opt.get
    assert(generalTypeTask1.isInstanceOf[ReducedTask])
    val task1 = generalTypeTask1.asInstanceOf[ReducedTask]
    assert(task1.parameters.isEmpty)
    assert(task1.isPrimitive)
    assert(task1.parameterConstraints.isEmpty)
    assert(task1.precondition.conjuncts.size == 3)
    assert(task1.effect.conjuncts.size == 2)

    assert(task1.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People" && isPositive && parameter.isEmpty })
    assert(task1.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.ReadyToAddFavourite" && isPositive && parameter.isEmpty })
    assert(task1.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Menu" && !isPositive && parameter.isEmpty })
    assert(task1.effect.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.ReadyToAddFavourite" && isPositive && parameter.isEmpty })
    assert(task1.effect.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.FavouriteSelected" && !isPositive && parameter.isEmpty })

    // a lifted one
    val task2Opt = dom.tasks find { _.name == "select_Contacts.ContactForContactable" }
    assert(task2Opt.isDefined)
    val generalTypeTask2 = task2Opt.get
    assert(generalTypeTask2.isInstanceOf[ReducedTask])
    val task2 = generalTypeTask2.asInstanceOf[ReducedTask]
    assert(task2.parameters.size == 2)
    assert(task2.isPrimitive)
    assert(task2.parameterConstraints.isEmpty)
    assert(task2.precondition.conjuncts.size == 4)
    assert(task2.effect.conjuncts.size == 2)


    val variable1Opt = task2.parameters find { v => v.name == "newObj_variable78" && v.sort.name == "Contact" && dom.sorts.contains(v.sort) }
    assert(variable1Opt.isDefined)
    val variable1 = variable1Opt.get
    val variable2Opt = task2.parameters find { v => v.name == "newObj_variable79" && v.sort.name == "Contactable" && dom.sorts.contains(v.sort) }
    assert(variable2Opt.isDefined)
    val variable2 = variable2Opt.get

    assert(task2.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts" && isPositive && parameter.isEmpty })
    assert(task2.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.ConfigureContact" && !isPositive && parameter.isEmpty })
    assert(task2.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.Details" && !isPositive && parameter.isEmpty })
    assert(task2.precondition.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "associated_Contact" && isPositive && parameter.size == 2 &&
      parameter.head == variable1 && parameter(1) == variable2
    })

    assert(task2.effect.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.Details" && isPositive && parameter.isEmpty })
    assert(task2.effect.conjuncts exists { case Literal(predicate, isPositive, parameter) => predicate.name == "selected" && isPositive && parameter.size == 1 && parameter.head ==
      variable1 })


    // test the methods
    assert(dom.decompositionMethods.size == 95)

    val testTaskSchema = (dom.tasks find { _.name == "enterMode_Tasks" }).get
    val methodsForTestTaskSchema = dom.decompositionMethods.filter { _.abstractTask == testTaskSchema }
    assert(methodsForTestTaskSchema.size == 2)

    assert(methodsForTestTaskSchema exists { m => m.subPlan.planSteps.size == 4 && m.subPlan.causalLinks.size == 1 && m.subPlan.orderingConstraints.originalOrderingConstraints.size == 6 })
    // including init and goal
    assert(methodsForTestTaskSchema exists { m => m.subPlan.planSteps.size == 5 && m.subPlan.causalLinks.size == 2 && m.subPlan.orderingConstraints.originalOrderingConstraints.size == 9 })
    // including init and goal
  }


  it must "be possible without error (2)" in {
    val dom: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/fitnessDomain.xml"))

  }


  "Parsing Problem files" must "be possible for flat domains" in {
    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"))
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"), domAlone)
  }


  it must "be possible for hierarchical domains" in {
    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"))
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"), domAlone)
  }
}
