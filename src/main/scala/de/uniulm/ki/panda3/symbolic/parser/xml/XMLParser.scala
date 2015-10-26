package de.uniulm.ki.panda3.symbolic.parser.xml

import java.io.FileInputStream
import javax.xml.bind.{JAXBContext, JAXBElement, Unmarshaller}
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.sax.SAXSource

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate, Sort}
import de.uniulm.ki.panda3.symbolic.parser.Parser
import de.uniulm.ki.panda3.symbolic.parser.xml.problem.Problem
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.panda3.symbolic.plan.{Plan, SymbolicPlan, element}
import org.xml.sax.XMLReader

import scala.collection._
import scala.xml.InputSource

/**
 * This is a parser for the old XML format of PANDA 1 and 2.
 *
 * @author Kadir Dede (kadir.dede@uni-ulm.de)
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object XMLParser extends Parser {

  override def parseDomain(filename: String): Domain = {

    val context: JAXBContext = JAXBContext.newInstance(classOf[XMLDomain])
    val marshaller: Unmarshaller = context.createUnmarshaller()
    val spf: SAXParserFactory = SAXParserFactory.newInstance()
    spf.setXIncludeAware(true)
    spf.setNamespaceAware(true)
    spf.setValidating(true)
    // Not required for JAXB/XInclude
    val xr: XMLReader = spf.newSAXParser().getXMLReader
    val source: SAXSource = new SAXSource(xr, new InputSource(new FileInputStream(filename)))
    val dom: XMLDomain = marshaller.unmarshal(source).asInstanceOf[XMLDomain]



    val constantMap: mutable.Map[String, Seq[logic.Constant]] = mutable.Map()
    /**
     * @param input the whole sequence of constants from XMLDomain
     * @return the whole sequence of constants used in Domain
     *         also: generates a map of type (String -> Seq[Constant])
     */
    def createConstantSeq(input: Seq[ConstantDeclaration]): Seq[logic.Constant] = input map { const =>
      val sort = const.getSort.asInstanceOf[SortDeclaration].getName
      if (constantMap contains sort) {
        val buffer = Seq(logic.Constant(const.getName)) ++ constantMap(sort)
        constantMap -= sort
        constantMap += (sort -> buffer)
      } else {
        constantMap += (sort -> Seq(logic.Constant(const.getName)))
      }
      logic.Constant(const.getName)
    }
    val constantSeq = createConstantSeq(JavaConversions.asScalaBuffer(dom.getConstantDeclaration))
    val xmlConstantToScalaConstant: Map[ConstantDeclaration, logic.Constant] = (JavaConversions.asScalaBuffer(dom.getConstantDeclaration) zip constantSeq).toMap
    val stringToXMLConstant: Map[String, ConstantDeclaration] = (JavaConversions.asScalaBuffer(dom.getConstantDeclaration) map { decl => (decl.getName, decl) }).toMap
    // needed for Domain
    val generatedSorts: mutable.Map[SortDeclaration, Sort] = mutable.HashMap()
    /**
     * @param input a single SortDeclaration
     * @return the proper Object of type Sort
     *         also: generates sortSeq and doneSort by adding already constructed sorts
     */
    def createSortSeq(input: SortDeclaration): Sort = if (generatedSorts.contains(input)) generatedSorts(input)
    else {
      val subSorts: Seq[SubSort] = JavaConversions.asScalaBuffer(input.getSubSort)
      val subSortSeq: Seq[Sort] = for (x <- subSorts) yield createSortSeq(x.getSort.asInstanceOf[SortDeclaration])

      val newSort = Sort(input.getName, constantMap.getOrElse(input.getName, Nil), subSortSeq)
      generatedSorts += (input -> newSort)

      newSort
    }
    val sortSeq: Seq[Sort] = for (x <- JavaConversions.asScalaBuffer(dom.getSortDeclaration)) yield createSortSeq(x)
    val xmlSortsToScalaSorts: Map[SortDeclaration, Sort] = generatedSorts.toMap

    // predicates
    val predicates: Seq[Predicate] = JavaConversions.asScalaBuffer(dom.getRelationDeclaration) map { relation =>
      Predicate(relation.getName, JavaConversions.asScalaBuffer(relation.getArgumentSort) map { argument => xmlSortsToScalaSorts(argument.getSort.asInstanceOf[SortDeclaration]) })
    }
    val xmlPredicateToScalaPredicate: Map[RelationDeclaration, Predicate] = (JavaConversions.asScalaBuffer(dom.getRelationDeclaration) zip predicates).toMap

    // task schemata
    val tasks: Seq[Task] = JavaConversions.asScalaBuffer(dom.getTaskSchemaDeclaration) map { taskSchema =>

      // split the content
      val splitContent = JavaConversions.asScalaBuffer(taskSchema.getContent) filter {!_.isInstanceOf[JAXBElement[Any]]} partition {_.isInstanceOf[VariableDeclaration]}
      assert(splitContent._2.size == 2)
      val xmlVariableDeclarations = splitContent._1 map {_.asInstanceOf[VariableDeclaration]}
      val variables = xmlVariableDeclarations.zipWithIndex map { case (variableDeclaration, idx) => logic.Variable(idx, variableDeclaration.getName,
        xmlSortsToScalaSorts(variableDeclaration.getSort.asInstanceOf[SortDeclaration]))
      }
      val varDeclToVariable: Map[VariableDeclaration, logic.Variable] = (xmlVariableDeclarations zip variables).toMap


      val preconditions = extractLiteralList(splitContent._2.head, varDeclToVariable, xmlConstantToScalaConstant, xmlPredicateToScalaPredicate, xmlSortsToScalaSorts)
      val effects = extractLiteralList(splitContent._2(1), varDeclToVariable, xmlConstantToScalaConstant, xmlPredicateToScalaPredicate, xmlSortsToScalaSorts)
      val variableConstraints = preconditions._2 ++ effects._2
      Task(taskSchema.getName, taskSchema.getType == "primitive", variables ++ (variableConstraints map { case Equal(vari, _) => vari }), variableConstraints, preconditions._1, effects._1)
    }
    val xmlTaskToScalaTask: Map[TaskSchemaDeclaration, Task] = (JavaConversions.asScalaBuffer(dom.getTaskSchemaDeclaration) zip tasks).toMap

    // decomposition methods
    val decompositionMethods: Seq[DecompositionMethod] = JavaConversions.asScalaBuffer(dom.getMethodDeclaration) map { xmlMethod =>

      val abstractTaskSchema: Task = xmlTaskToScalaTask(xmlMethod.getTaskSchema.asInstanceOf[TaskSchemaDeclaration])

      val variables: mutable.Map[VariableDeclaration, logic.Variable] = mutable.Map()
      // add variables of the method, aka the variables of the parameter actions
      // TODO: here we COMPLETELY ignore the content of the XML file and presume to know it better
      JavaConversions.asScalaBuffer(xmlMethod.getVariableDeclaration).zipWithIndex foreach { case (vardecl, index) => variables.put(vardecl, abstractTaskSchema.parameters(index)) }
      // add variables we have added due to constants in the abstract tasks declaration
      val additionalParameterList = Range(variables.size, abstractTaskSchema.parameters.size) map { i =>
        val variableDeclaration = new VariableDeclaration()
        variables.put(variableDeclaration, abstractTaskSchema.parameters(i))
        variableDeclaration
      }


      val abstractTaskParameterVariables = (JavaConversions.asScalaBuffer(xmlMethod.getVariableDeclaration) ++ additionalParameterList) map variables

      val init: PlanStep = PlanStep(0, Task("method_init", isPrimitive = true, abstractTaskSchema.parameters, Nil, Nil, abstractTaskSchema.preconditions), abstractTaskParameterVariables)
      val goal: PlanStep = PlanStep(1, Task("method_goal", isPrimitive = true, abstractTaskSchema.parameters, Nil, abstractTaskSchema.effects, Nil), abstractTaskParameterVariables)


      DecompositionMethod(abstractTaskSchema,
        buildPlanFrom(xmlMethod, init, goal, variables, xmlConstantToScalaConstant, xmlPredicateToScalaPredicate, xmlSortsToScalaSorts, xmlTaskToScalaTask, abstractTaskSchema
          .parameterConstraints))
    }

    // already done:
    // Sequence of Constants (constantSeq)
    // Sequence of Sorts     (sortSeq)
    // Sequence of Predicates
    // Sequence of Tasks
    // Sequence of Decomposition Methods
    // still to do:
    // Sequence of Decomposition Axioms

    Domain(sortSeq, predicates, tasks, decompositionMethods, Nil)
  }


  private def getAnyFromFormula(not: {
    def getAnd(): And
    def getAtomic(): Atomic
    def getExists(): Exists
    def getForall(): Forall
    def getImply(): Imply
    def getNot(): Not
    def getOr(): Or}): Any = ((not.getAnd :: not.getAtomic :: not.getExists :: not.getForall :: not.getImply :: not.getNot :: not.getOr :: Nil) find {_ != null}).get


  def extractLiteralList(xmlStruct: Any, xmlVariableToScalaVariable: Map[VariableDeclaration, logic.Variable], xmlConstantToScalaConstant: Map[ConstantDeclaration, logic.Constant],
                         xmlPredicateToScalaPredicate: Map[RelationDeclaration, Predicate], xmlSortsToScalaSorts: Map[SortDeclaration, Sort]): (Seq[Literal], Seq[VariableConstraint]) = {

    // gather the variable constraints
    var variableConstraints: Seq[VariableConstraint] = Nil

    def extract(xmlStruct: Any, positive: Boolean): Seq[Literal] = {
      xmlStruct match {
        case and: And       =>
          if (positive) {
            JavaConversions.asScalaBuffer(and.getAtomicOrNotOrAnd) flatMap {
              extract(_, positive)
            }
          } else {
            assert(assertion = false) // TODO: we can't handle this case yet
            Nil
          }
        case or: Or         =>
          if (!positive) {
            JavaConversions.asScalaBuffer(or.getAtomicOrNotOrAnd) flatMap {
              extract(_, positive)
            }
          } else {
            assert(assertion = false) // TODO: we can't handle this case yet
            Nil
          }
        case not: Not       => extract(getAnyFromFormula(not), !positive)
        case atomic: Atomic =>
          val parameterVariables: scala.Seq[logic.Variable] = JavaConversions.asScalaBuffer(atomic.getVariableOrConstant) map { case variable: Variable => xmlVariableToScalaVariable(
            variable.getName.asInstanceOf[VariableDeclaration])
          case constant: Constant                                                                                                                       =>
            val constDeclaration: ConstantDeclaration = constant.getName.asInstanceOf[ConstantDeclaration]
            val newVariable = logic.Variable(xmlVariableToScalaVariable.size + variableConstraints.size, "ConstantVariable" + constant.hashCode(),
              xmlSortsToScalaSorts(constDeclaration.getSort.asInstanceOf[SortDeclaration]))
            variableConstraints = variableConstraints :+ Equal(newVariable, xmlConstantToScalaConstant(constDeclaration))
            newVariable
          }
          Literal(xmlPredicateToScalaPredicate(atomic.getRelation.asInstanceOf[RelationDeclaration]), positive, parameterVariables) :: Nil
        case _              => Nil
      }
      // TODO: handle existential quantifier
    }
    // run the extraction
    val literals = extract(xmlStruct, positive = true)

    (literals, variableConstraints)
  }


  def buildPlanFrom(planDef: {def getTaskNode(): java.util.List[TaskNode]
    def getCausalLink(): java.util.List[CausalLink]
    def getOrderingConstraint(): java.util.List[OrderingConstraint]
    def getValueRestrictionOrSortRestriction(): java.util.List[java.lang.Object]}, init: PlanStep, goal: PlanStep,
                    predefinedXmlVariableToScalaVariable: Map[VariableDeclaration, logic.Variable], xmlConstantToScalaConstant: Map[ConstantDeclaration, logic.Constant],
                    xmlPredicateToScalaPredicate: Map[RelationDeclaration, Predicate], xmlSortsToScalaSorts: Map[SortDeclaration, Sort],
                    xmlTaskToScalaTask: Map[TaskSchemaDeclaration, Task], inheritedVariableConstraints: Seq[VariableConstraint]): Plan = {

    val xmlVariableToScalaVariable: mutable.Map[VariableDeclaration, logic.Variable] = mutable.Map(predefinedXmlVariableToScalaVariable.toSeq: _*)

    val planSteps: Seq[PlanStep] = (JavaConversions.asScalaBuffer(planDef.getTaskNode).zipWithIndex map { case (taskNode, index) =>
      // create variables
      val arguments: Seq[logic.Variable] = JavaConversions.asScalaBuffer(taskNode.getVariableDeclaration) map { variableDecl =>
        val variable: logic.Variable = logic.Variable(xmlVariableToScalaVariable.size, variableDecl.getName, xmlSortsToScalaSorts(variableDecl.getSort.asInstanceOf[SortDeclaration]))
        xmlVariableToScalaVariable.put(variableDecl, variable)
        variable
      }
      // add arguments for the parameters not occuring in the XML (i.e. variables representing constants)
      val taskSchemaOfThePlanStep = xmlTaskToScalaTask(taskNode.getTaskSchema.asInstanceOf[TaskSchemaDeclaration])
      val otherArguments: Seq[logic.Variable] = Range(taskNode.getVariableDeclaration.size(), taskSchemaOfThePlanStep.parameters.size) map { i =>
        val schemaParameter = taskSchemaOfThePlanStep.parameters(i)
        val variable = logic.Variable(xmlVariableToScalaVariable.size, schemaParameter.name, schemaParameter.sort)
        xmlVariableToScalaVariable.put(new VariableDeclaration(), variable)
        variable
      }

      // build the planstep
      PlanStep(index + 2, taskSchemaOfThePlanStep, arguments ++ otherArguments)
    }) :+ init :+ goal
    val xmlTaskNodesToScalaPlanSteps: Map[TaskNode, PlanStep] = (JavaConversions.asScalaBuffer(planDef.getTaskNode) zip planSteps).toMap

    // generate the causal links contained in the method
    val causalLinks: Seq[(element.CausalLink, Seq[VariableConstraint])] = JavaConversions.asScalaBuffer(planDef.getCausalLink) map { xmlLink =>
      val producer = xmlTaskNodesToScalaPlanSteps(xmlLink.getProducer.asInstanceOf[TaskNode])
      val consumer = xmlTaskNodesToScalaPlanSteps(xmlLink.getConsumer.asInstanceOf[TaskNode])
      val literalAndConstraints = extractLiteralList(getAnyFromFormula(xmlLink), xmlVariableToScalaVariable, xmlConstantToScalaConstant, xmlPredicateToScalaPredicate, xmlSortsToScalaSorts)
      assert(literalAndConstraints._1.size == 1)
      // TODO: might it be necessary to add the equality constraint for the CL here?!
      (element.CausalLink(producer, consumer, literalAndConstraints._1.head), literalAndConstraints._2)
    }


    val variablesIntroducedByCausalLinks: Set[logic.Variable] = (causalLinks flatMap {_._2 flatMap {_.getVariables}}).toSet
    val valueRestrictions = JavaConversions.asScalaBuffer(planDef.getValueRestrictionOrSortRestriction()) map {
      case valueRestriction: ValueRestriction =>
        val v1 = xmlVariableToScalaVariable(valueRestriction.getVariableN.asInstanceOf[VariableDeclaration])
        val voc = if (valueRestriction.getVariable != null) xmlVariableToScalaVariable(valueRestriction.getVariable.getName.asInstanceOf[VariableDeclaration])
        else
          xmlConstantToScalaConstant(valueRestriction.getConstant.getName.asInstanceOf[ConstantDeclaration])

        valueRestriction.getType match {
          case "eq"  => Equal(v1, voc)
          case "neq" => NotEqual(v1, voc)
        }
      case sortRestriction: SortRestriction   =>
        val v = xmlVariableToScalaVariable(sortRestriction.getVariable.asInstanceOf[VariableDeclaration])
        val sort = xmlSortsToScalaSorts(sortRestriction.getSort.asInstanceOf[SortDeclaration])

        sortRestriction.getType match {
          case "eq"  => OfSort(v, sort)
          case "neq" => NotOfSort(v, sort)
        }
    }
    val csp: CSP = SymbolicCSP((variablesIntroducedByCausalLinks ++ (xmlVariableToScalaVariable map {_._2})).toSet, (causalLinks flatMap {_._2}) ++ valueRestrictions ++
      inheritedVariableConstraints)

    // get the order induced by the causal links and the explicitly mentioned order
    val orderingConstraints: Seq[element.OrderingConstraint] = ((causalLinks map { cl => element.OrderingConstraint(cl._1.producer, cl._1.consumer) }) ++
      (JavaConversions.asScalaBuffer(planDef.getOrderingConstraint) map { oc => element.OrderingConstraint(xmlTaskNodesToScalaPlanSteps(oc.getPredecessor.asInstanceOf[TaskNode]),
        xmlTaskNodesToScalaPlanSteps(oc.getSuccessor.asInstanceOf[TaskNode]))
      }) ++ element.OrderingConstraint.allBetween(init, goal, planSteps filterNot { ps => ps == init || ps == goal }: _*)).toSet.toSeq

    val taskOrdering: TaskOrdering = SymbolicTaskOrdering(orderingConstraints, planSteps)

    SymbolicPlan(planSteps, causalLinks map {_._1}, taskOrdering, csp, init, goal)
  }

  def parseProblem(filename: String, inputDomain: Domain): (Domain, Plan) = {

    val context: JAXBContext = JAXBContext.newInstance(classOf[Problem])
    val marshaller: Unmarshaller = context.createUnmarshaller()
    val spf: SAXParserFactory = SAXParserFactory.newInstance()
    spf.setXIncludeAware(true)
    spf.setNamespaceAware(true)
    spf.setValidating(true)
    // Not required for JAXB/XInclude
    val xr: XMLReader = spf.newSAXParser().getXMLReader
    val source: SAXSource = new SAXSource(xr, new InputSource(new FileInputStream(filename)))
    val problem: Problem = marshaller.unmarshal(source).asInstanceOf[Problem]

    // add constants to domain
    val problemConstants: Seq[(Sort, logic.Constant)] = JavaConversions.asScalaBuffer(problem.getConstantDeclaration) map { constantDeclaration =>
      (inputDomain.sorts.find {_.name == constantDeclaration.getSort}.get, logic.Constant(constantDeclaration.getName))
    }

    val domain = inputDomain.addConstantsToDomain(problemConstants)


    val nameToVariablesForConstants: Map[String, logic.Variable] =
      (domain.constants.zipWithIndex map { case (c, id) => (c.name, logic.Variable(id, "var_" + c.name, Sort("const_sort", c :: Nil, Nil))) }).toMap
    val variablesForConstants: Seq[logic.Variable] = nameToVariablesForConstants.values.toSeq
    val variableConstraintsForConstants: Seq[VariableConstraint] = domain.constants map { c => Equal(nameToVariablesForConstants(c.name), c) }


    // build init
    val initTask: Task = Task("init", isPrimitive = true, variablesForConstants, variableConstraintsForConstants, Nil, JavaConversions.asScalaBuffer(problem.getInitialState.getFact)
      map {factToLiteral(_, domain, nameToVariablesForConstants, isPositive = true)})

    val init: PlanStep = PlanStep(0, initTask, variablesForConstants)



    // build goal, this is some kind of a formula
    val goalLiterals = if (problem.getGoals == null) Nil
    else JavaConversions.asScalaBuffer(problem.getGoals.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference) flatMap { goalLiteral =>
      extractProblemLiterals(goalLiteral, domain, nameToVariablesForConstants, Map())
    }
    val goalTask: Task = Task("goal", isPrimitive = true, variablesForConstants, variableConstraintsForConstants, goalLiterals, Nil)
    val goal: PlanStep = PlanStep(1, goalTask, variablesForConstants)

    // variables declared in the plan steps
    val variableNameToVariable: mutable.Map[String, logic.Variable] = mutable.Map()

    val initialTN = problem.getInitialTaskNetwork

    val xmlPlanStepToScalaPlanStep: mutable.Map[String, PlanStep] = mutable.Map()

    val planSteps: Seq[PlanStep] = if (initialTN == null) Nil
    else JavaConversions.asScalaBuffer(initialTN.getTaskNode).zipWithIndex map { case (node, id) =>
      // determine the correct task schema
      val schema: Task = (domain.tasks find {_.name == node.getTaskSchema}).get
      // build arguments
      val arguments: Seq[logic.Variable] = JavaConversions.asScalaBuffer(node.getVariableDeclaration) map { vc =>
        val variable = logic.Variable(nameToVariablesForConstants.size + variableNameToVariable.size, vc.getName, (domain.sorts find {_.name == vc.getSort}).get)
        variableNameToVariable.put(vc.getName, variable)
        variable
      }

      val otherArguments: Seq[logic.Variable] = Range(node.getVariableDeclaration.size(), schema.parameters.size) map { i =>
        val schemaParameter = schema.parameters(i)
        val variable = logic.Variable(variableNameToVariable.size, schemaParameter.name, schemaParameter.sort)
        variableNameToVariable.put("ConstantVariable" + i + "_" + id + 2, variable)
        variable
      }

      val ps = PlanStep(id + 2, schema, arguments ++ otherArguments)
      xmlPlanStepToScalaPlanStep.put(node.getName, ps)
      ps
    }

    // generate the causal links contained in the method
    val causalLinks: Seq[element.CausalLink] = if (initialTN == null) Nil
    else JavaConversions.asScalaBuffer(initialTN.getCausalLink) map { xmlLink =>
      val producer = xmlPlanStepToScalaPlanStep(xmlLink.getProducer.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.TaskNode].getName)
      val consumer = xmlPlanStepToScalaPlanStep(xmlLink.getConsumer.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.TaskNode].getName)

      val linkLiterals = JavaConversions.asScalaBuffer(xmlLink.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists) flatMap {
        extractProblemLiterals(_, domain, nameToVariablesForConstants, variableNameToVariable)
      }
      element.CausalLink(producer, consumer, linkLiterals.head)
    }

    // generate ordering constraints
    val orderingConstraint = if (initialTN == null) Nil
    else (JavaConversions.asScalaBuffer(initialTN.getOrderingConstraint) map { oc =>
      element.OrderingConstraint(
        xmlPlanStepToScalaPlanStep(oc.getPredecessor.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.TaskNode].getName),
        xmlPlanStepToScalaPlanStep(oc.getSuccessor.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.TaskNode].getName))
    }) ++ (causalLinks map { cl => element.OrderingConstraint(cl.producer, cl.consumer) }) ++ element.OrderingConstraint.allBetween(init, goal, planSteps: _*)

    // generate the CSP
    val additionalVariableConstraints: Seq[VariableConstraint] = if (initialTN == null) Nil
    else JavaConversions.asScalaBuffer(initialTN.getValueRestrictionOrSortRestriction) map {
      case valueRestriction: de.uniulm.ki.panda3.symbolic.parser.xml.problem.ValueRestriction =>
        val v1 = variableNameToVariable(valueRestriction.getVariable.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.VariableDeclaration].getName)
        assert(valueRestriction.getVariableOrConstant.size() == 1)
        val voc = valueRestriction.getVariableOrConstant.get(0) match {
          case v2: de.uniulm.ki.panda3.symbolic.parser.xml.problem.VariableDeclaration => variableNameToVariable(v2.getName)
          case v2: de.uniulm.ki.panda3.symbolic.parser.xml.problem.Variable => variableNameToVariable(v2.getName.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem
          .VariableDeclaration].getName)
          case c: de.uniulm.ki.panda3.symbolic.parser.xml.problem.Constant             => nameToVariablesForConstants(c.getName)
        }
        // build the returned restriction
        valueRestriction.getType match {
          case "eq"  => Equal(v1, voc)
          case "neq" => NotEqual(v1, voc)
        }

      case sortRestriction: de.uniulm.ki.panda3.symbolic.parser.xml.problem.SortRestriction =>
        val v = variableNameToVariable(sortRestriction.getVariable.asInstanceOf[de.uniulm.ki.panda3.symbolic.parser.xml.problem.VariableDeclaration].getName)
        val sort = (domain.sorts find {_.name == sortRestriction.getSort}).get

        sortRestriction.getType match {
          case "eq"  => OfSort(v, sort)
          case "neq" => NotOfSort(v, sort)
        }
    }

    val csp: CSP = SymbolicCSP(nameToVariablesForConstants.values.toSet ++ variableNameToVariable.values, variableConstraintsForConstants ++ additionalVariableConstraints)


    val planStepsWithInitAndGoal = planSteps :+ init :+ goal
    (domain, SymbolicPlan(planStepsWithInitAndGoal, causalLinks, SymbolicTaskOrdering(orderingConstraint, planStepsWithInitAndGoal), csp, init, goal))
  }

  def factToLiteral(fact: problem.Fact, dom: Domain, nameToVariablesForConstants: Map[String, logic.Variable], isPositive: Boolean): Literal = {
    val predicate = (dom.predicates find {_.name == fact.getRelation}).get
    val variables = JavaConversions.asScalaBuffer(fact.getConstant) map { c => nameToVariablesForConstants(c.getName) }
    Literal(predicate, isPositive, variables)
  }


  def extractProblemLiterals(element: Any, dom: Domain, nameToVariablesForConstants: Map[String, logic.Variable], variablesNamesToVariables: Map[String, logic.Variable]): Seq[Literal] = {

    def extract(v: Any, positive: Boolean): Seq[Literal] = {
      v match {
        case v: problem.And    =>
          if (positive) {
            JavaConversions.asScalaBuffer(v.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists) flatMap {extract(_, positive)}
          } else {
            assert(assertion = false) // TODO: we can't handle this case yet
            Nil
          }
        case v: problem.Or     =>
          if (!positive) {
            JavaConversions.asScalaBuffer(v.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists) flatMap {extract(_, positive)}
          } else {
            assert(assertion = false) // TODO: we can't handle this case yet
            Nil
          }
        case v: problem.Not    => extract(v.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists, !positive)
        case v: problem.Atomic =>
          val predicate = (dom.predicates find {_.name == v.getRelation}).get
          val arguments = JavaConversions.asScalaBuffer(v.getVariableOrConstant) map {
            case c: problem.Constant => nameToVariablesForConstants(c.getName)
            case v: problem.Variable => variablesNamesToVariables(v.getName.asInstanceOf[problem.VariableDeclaration].getName)
          }

          Literal(predicate, positive, arguments) :: Nil
        case v: problem.Fact   => factToLiteral(v, dom, nameToVariablesForConstants, positive) :: Nil
        case _                 => Nil // TODO: we just ignore everything else
      }
    }

    extract(element, positive = true)

  }

}