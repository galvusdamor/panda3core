package de.uniulm.ki.panda3.parser

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.logic.Literal
import org.scalatest.FlatSpec

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class XMLParserTest extends FlatSpec {


  "Parsing hon-hierarchical Files " must "be possible without error" in {
    val dom: Domain = XMLParser.parseFromFile("src/test/resources/de/uniulm/ki/panda3/parser/AssemblyTask_domain.xml")

    assert(dom.constants.size == 0)
    assert(dom.sorts.size == 34)
    assert(dom.predicates.size == 12)
    assert(dom.tasks.size == 16)

    // checking one sort
    val cableAudioAndVideoSortOpt = dom.sorts find {_.name == "Cable_Audio_and_Video"}
    assert(cableAudioAndVideoSortOpt.isDefined)
    val cableAudioAndVideoSort = cableAudioAndVideoSortOpt.get

    assert(cableAudioAndVideoSort.elements.size == 0)
    assert(cableAudioAndVideoSort.subSorts.size == 2)
    assert(cableAudioAndVideoSort.subSorts forall { s => s.name == "Cable_HDMI" || s.name == "Cable_Scart_CinchVideo_CinchStereo" })
  }

  "Parsing hierarchical Files " must "be possible without error" in {
    val dom: Domain = XMLParser.parseFromFile("src/test/resources/de/uniulm/ki/panda3/parser/SmartPhone-HierarchicalNoAxioms.xml")

    assert(dom.constants.size == 0)
    assert(dom.sorts.size == 40)
    assert(dom.predicates.size == 63)
    assert(dom.tasks.size == 138)

    assert((dom.tasks count {_.isPrimitive}) == 88)
    assert((dom.tasks count {!_.isPrimitive}) == 50)

    // checking some tasks
    // a grounded one
    val task1Opt = dom.tasks find {_.name == "press_People.Smallplus_AlreadyPressed"}
    assert(task1Opt.isDefined)
    val task1 = task1Opt.get
    assert(task1.parameters.size == 0)
    assert(task1.isPrimitive)
    assert(task1.parameterConstraints.size == 0)
    assert(task1.preconditions.size == 3)
    assert(task1.effects.size == 2)

    assert(task1.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People" && isPositive && parameter.size == 0 })
    assert(task1.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.ReadyToAddFavourite" && isPositive && parameter.size == 0 })
    assert(task1.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Menu" && !isPositive && parameter.size == 0 })
    assert(task1.effects exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.ReadyToAddFavourite" && isPositive && parameter.size == 0 })
    assert(task1.effects exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_People.FavouriteSelected" && !isPositive && parameter.size == 0 })

    // a lifted one
    val task2Opt = dom.tasks find {_.name == "select_Contacts.ContactForContactable"}
    assert(task2Opt.isDefined)
    val task2 = task2Opt.get
    assert(task2.parameters.size == 2)
    assert(task2.isPrimitive)
    assert(task2.parameterConstraints.size == 0)
    assert(task2.preconditions.size == 4)
    assert(task2.effects.size == 2)


    val variable1Opt = task2.parameters find { v => v.name == "newObj_variable78" && v.sort.name == "Contact" && dom.sorts.contains(v.sort) }
    assert(variable1Opt.isDefined)
    val variable1 = variable1Opt.get
    val variable2Opt = task2.parameters find { v => v.name == "newObj_variable79" && v.sort.name == "Contactable" && dom.sorts.contains(v.sort) }
    assert(variable2Opt.isDefined)
    val variable2 = variable2Opt.get

    assert(task2.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts" && isPositive && parameter.size == 0 })
    assert(task2.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.ConfigureContact" && !isPositive && parameter.size == 0 })
    assert(task2.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.Details" && !isPositive && parameter.size == 0 })
    assert(task2.preconditions exists { case Literal(predicate, isPositive, parameter) => predicate.name == "associated_Contact" && isPositive && parameter.size == 2 &&
      parameter(0) == variable1 && parameter(1) == variable2
    })

    assert(task2.effects exists { case Literal(predicate, isPositive, parameter) => predicate.name == "inMode_Contacts.Details" && isPositive && parameter.size == 0 })
    assert(task2.effects exists { case Literal(predicate, isPositive, parameter) => predicate.name == "selected" && isPositive && parameter.size == 1 && parameter.head == variable1 })


    // test the methods
    assert(dom.decompositionMethods.size == 95)

    val testTaskSchema = (dom.tasks find {_.name == "enterMode_Tasks"}).get
    val methodsForTestTaskSchema = dom.decompositionMethods.filter {_.abstractTask == testTaskSchema}
    assert(methodsForTestTaskSchema.size == 2)

    assert(methodsForTestTaskSchema exists { m => m.subPlan.planSteps.size == 4 && m.subPlan.causalLinks.size == 1 && m.subPlan.orderingConstraints.originalOrderingConstraints.size == 6 })
    // including init and goal
    assert(methodsForTestTaskSchema exists { m => m.subPlan.planSteps.size == 5 && m.subPlan.causalLinks.size == 2 && m.subPlan.orderingConstraints.originalOrderingConstraints.size == 9 })
    // including init and goal
  }
}