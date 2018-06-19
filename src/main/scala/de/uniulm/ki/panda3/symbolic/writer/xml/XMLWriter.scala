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

package de.uniulm.ki.panda3.symbolic.writer.xml

import java.io.{FileInputStream, ByteArrayOutputStream}
import java.nio.charset.Charset
import javax.xml.bind.{JAXBContext, Marshaller}

import de.uniulm.ki.panda3.symbolic.compiler.ExpandSortHierarchy
import de.uniulm.ki.panda3.symbolic.csp.{NotEqual, Equal}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.writer.Writer
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Formula, Literal, Predicate, Sort}
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.{logic, plan}
import de.uniulm.ki.panda3.symbolic
import de.uniulm.ki.panda3.symbolic._

import scala.collection.mutable


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// we have to piggy-back onto the structure of the generated XML file
// scalastyle:off structural.type
case class XMLWriter(domainName: String, problemName: String) extends Writer {
  /**
    * Takes a domain and writes and produces a string representation thereof.
    * This will not write any constant into the domain string
    */
  override def writeDomain(dom: Domain): String = XMLWriterDomain.writeDomain(dom, domainName)

  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  override def writeProblem(dom: Domain, plan: Plan): String = XMLWriterProblem.writeProblem(dom, plan, domainName, problemName)
}

// herein the actual functionality is contained. This was done to keep the namespaces of domain and problem apart

private object XMLWriterDomain {

  import de.uniulm.ki.panda3.symbolic.parser.xml._

  private def toVariable(variableDeclaration: VariableDeclaration): Variable = {
    val variable = new Variable
    variable.setName(variableDeclaration)
    variable
  }

  private def toConstant(constantDeclaration: ConstantDeclaration): Constant = {
    val constant = new Constant
    constant.setName(constantDeclaration)
    constant
  }


  private def setInnerValue(expression: {def setAnd(v: And)
    def setAtomic(v: Atomic)
    def setExists(v: Exists)
    def setForall(v: Forall)
    def setImply(v: Imply)
    def setNot(v: Not)
    def setOr(v: Or)}, value: Any): Unit = value match {
    case a: And    => expression.setAnd(a)
    case a: Atomic => expression.setAtomic(a)
    case a: Exists => expression.setExists(a)
    case a: Forall => expression.setForall(a)
    case a: Imply  => expression.setImply(a)
    case a: Not    => expression.setNot(a)
    case a: Or     => expression.setOr(a)
  }

  // TODO: this does not take the CSP inside the task into account, i.e., constants in preconditions and effects might not show
  private def formulaToAny(formula: Formula, predicatesToXMLPredicates: Map[Predicate, RelationDeclaration], varToVarDecl: Map[logic.Variable, VariableDeclaration],
                           sortToSortDecl: Map[Sort, SortDeclaration]): AnyRef =
    formula match {
      case l: Literal =>
        if (l.isNegative) {
          // handle negation
          val not = new Not
          not.setAtomic(formulaToAny(l.negate, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)
                          .asInstanceOf[Atomic]) // this is necessary due to the crappy definition of the old XML format
          not
        } else {
          val atom = new Atomic
          atom.setRelation(predicatesToXMLPredicates(l.predicate))
          l.parameterVariables foreach { v =>
            atom.getVariableOrConstant.add(toVariable(varToVarDecl(v)))
          }
          atom
        }

      case logic.Identity() => noSupport("identity cannot be written")
      case logic.Not(inner) => val not = new Not
        setInnerValue(not, formulaToAny(inner, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl))
        not
      case logic.And(conj)  => val and = new And
        conj foreach { conjunct => and.getAtomicOrNotOrAnd.add(formulaToAny(conjunct, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)) }
        and
      case logic.Or(disj)   => val or = new Or
        disj foreach { conjunct => or.getAtomicOrNotOrAnd.add(formulaToAny(conjunct, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)) }
        or
      // TODO: I don't know whether this correct at all
      case logic.Implies(left, right)     => val imply = new Imply
        imply.getContent.add(formulaToAny(left, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl))
        imply.getContent.add(formulaToAny(right, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl))
        imply
      case logic.Equivalence(left, right) => formulaToAny(logic.And(logic.Implies(left, right) :: logic.Implies(right, left) :: Nil), predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)

      case logic.Exists(v, form) => val exists = new Exists
        val varDecl = new VariableDeclaration
        varDecl.setName(v.name)
        varDecl.setSort(sortToSortDecl(v.sort))
        exists.setVariableDeclaration(varDecl)
        setInnerValue(exists, formulaToAny(form, predicatesToXMLPredicates, varToVarDecl + (v -> varDecl), sortToSortDecl))
        exists
      case logic.Forall(v, form) => val forall = new Forall
        val varDecl = new VariableDeclaration
        varDecl.setName(v.name)
        varDecl.setSort(sortToSortDecl(v.sort))
        forall.setVariableDeclaration(varDecl)
        setInnerValue(forall, formulaToAny(form, predicatesToXMLPredicates, varToVarDecl + (v -> varDecl), sortToSortDecl))
        forall

    }


  def writeDomain(dom: Domain, domainName: String): String = {
    val xmldomain: XMLDomain = new ObjectFactory().createDomain()
    xmldomain.setType("pure-hierarchical")
    xmldomain.setName(domainName)

    // 1. Step build the sorts (if the sort graph contains a circle we cannot translate)
    val sortToSortDecl: Map[Sort, SortDeclaration] = dom.sortGraph.topologicalOrdering.get.reverse.foldLeft(Map[Sort, SortDeclaration]())(
      { case (map, s) =>
        val ns = new SortDeclaration
        ns.setName(s.name)
        ns.setType("concrete")

        s.subSorts map map map { subs => val ssDecl = new SubSort
          ssDecl.setSort(subs)
          ns.getSubSort.add(ssDecl)
        }
        map.+((s, ns))
      })
    sortToSortDecl.toSeq sortBy { _._1.name } map { _._2 } foreach { xmldomain.getSortDeclaration.add }


    // 2. create a function that adds constants if necessary
    val constantsToConstDecl: logic.Constant => ConstantDeclaration = {
      val innerConstantsToConstDecl: mutable.Map[logic.Constant, ConstantDeclaration] = new mutable.HashMap[logic.Constant, ConstantDeclaration]()

      { constant =>
        if (innerConstantsToConstDecl.contains(constant)) {innerConstantsToConstDecl(constant) }
        else {
          // construct
          val sort = dom.getSortOfConstant(constant).get // unsafe, but if not possible, writing will not be possible
          val cd: ConstantDeclaration = new ConstantDeclaration
          cd.setName(constant.name)
          cd.setSort(sortToSortDecl(sort))

          innerConstantsToConstDecl(constant) = cd
          xmldomain.getConstantDeclaration.add(cd)
          cd
        }
      }
    }

    //3. step add the predicates
    val predicatesToXMLPredicates: Map[Predicate, RelationDeclaration] = (dom.predicates map { pred =>
      val relDecl = new RelationDeclaration
      relDecl.setName(pred.name)
      relDecl.setType("flexible") // just assume
      pred.argumentSorts foreach { as => val asD = new ArgumentSort
        asD.setSort(sortToSortDecl(as))
        relDecl.getArgumentSort.add(asD)
      }
      (pred, relDecl)
    }).toMap
    predicatesToXMLPredicates.toSeq sortBy { _._1.name } map { _._2 } foreach { xmldomain.getRelationDeclaration.add }

    // 4. step add tasks
    val tasksToTaskDeclarations: Map[Task, TaskSchemaDeclaration] = (dom.tasks map { t =>
      val tsd = new TaskSchemaDeclaration
      tsd.setName(t.name)
      tsd.setType(if (t.isPrimitive) "primitive" else "complex")
      // add parameter variables
      val varToVarDecl: Map[logic.Variable, VariableDeclaration] = (t.parameters map { v =>
        val varDecl = new VariableDeclaration
        varDecl.setName(v.name)
        varDecl.setSort(sortToSortDecl(v.sort))
        // add to the task
        tsd.getContent.add(varDecl)
        (v, varDecl)
      }).toMap


      // add precondition and effect
      val precond = formulaToAny(t.precondition, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)
      val effect = formulaToAny(t.effect, predicatesToXMLPredicates, varToVarDecl, sortToSortDecl)
      // actually adding it
      tsd.getContent.add(precond)
      tsd.getContent.add(effect)

      (t, tsd)
    }).toMap
    tasksToTaskDeclarations.toSeq.sortBy { _._1.name } map { _._2 } foreach { xmldomain.getTaskSchemaDeclaration.add }


    // 5. step add the decomposition methods
    dom.decompositionMethods.zipWithIndex foreach { case (method, idx) =>
      val methodDecl = new MethodDeclaration
      methodDecl.setName("method" + idx)
      methodDecl.setTaskSchema(tasksToTaskDeclarations(method.abstractTask))

      // parameter variables for the abstract task
      val abstractParametersToVariables: Map[logic.Variable, VariableDeclaration] = (method.abstractTask.parameters map { v =>
        val varDecl = new VariableDeclaration
        varDecl.setName(v.name)
        varDecl.setSort(sortToSortDecl(v.sort))
        methodDecl.getVariableDeclaration.add(varDecl)
        (v, varDecl)
      }).toMap

      var newVariablesCounter = 0
      val taskParametersToVariables: mutable.Map[logic.Variable, VariableDeclaration] = new mutable.HashMap[logic.Variable, VariableDeclaration]()
      // all tasks in the plan
      val temp = method.subPlan.planStepsWithoutInitGoal map { ps =>
        val tasknode = new TaskNode
        tasknode.setTaskSchema(tasksToTaskDeclarations(ps.schema))
        tasknode.setName("method" + idx + "_subtask_" + ps.id)

        // generate a set of completely new variables
        val arguments = ps.arguments map { argument =>
          val varDecl = new VariableDeclaration()
          varDecl.setName(ps.schema.name + "_instance_" + ps.id + "_argument_" + newVariablesCounter)
          newVariablesCounter = newVariablesCounter + 1
          varDecl.setSort(sortToSortDecl(argument.sort))

          // if the variables already occurs, just add the equality constraint
          val potenrialConstraint: Option[ValueRestriction] = if (taskParametersToVariables.contains(argument)) {
            // create a new Variable Constraint
            val valueRestriction = new ValueRestriction
            valueRestriction.setType("eq")
            valueRestriction.setVariable(toVariable(varDecl))
            valueRestriction.setVariableN(taskParametersToVariables(argument))
            Some(valueRestriction)
          } else {
            taskParametersToVariables.put(argument, varDecl)
            None
          }

          ((varDecl, potenrialConstraint), potenrialConstraint)
        }

        // add the newly created variable declarations to the task
        arguments map { _._1._1 } foreach tasknode.getVariableDeclaration.add


        ((ps, tasknode), arguments map { _._2 } collect { case Some(x) => x })
      }


      // check all other variables that occur in the method -> they might be neither a parameter of the abstract task not a parameter of a task in the subnet
      // most notably Daniel's HDDL parser generates such methods ... dammit
      val planUF = SymbolicUnionFind.constructVariableUnionFind(method.subPlan)
      val allRepresentedVariables: Seq[logic.Variable] = (taskParametersToVariables.keys ++ abstractParametersToVariables.keys).toSeq
      method.subPlan.variableConstraints.variables filterNot allRepresentedVariables.contains foreach { v =>
        // try to find a variable that is a parameter and identical to this one.
        val possibleRepresentative = allRepresentedVariables find { planUF.getRepresentative(v) == planUF.getRepresentative(_) }
        if (possibleRepresentative.isDefined) {
          val representativeMap = if (taskParametersToVariables.contains(possibleRepresentative.get)) taskParametersToVariables else abstractParametersToVariables
          taskParametersToVariables(v) = representativeMap(possibleRepresentative.get)
        } else assert(false, "The XML-Writer does not support methods with independent variables. This one contains " + v)
      }




      // add all plan steps
      val planStepsToTaskNodes: Map[PlanStep, TaskNode] = (temp map { _._1 }).toMap
      val necessaryEqualities: Seq[ValueRestriction] = temp flatMap { _._2 }
      planStepsToTaskNodes.toSeq sortBy { _._1.id } map { _._2 } foreach methodDecl.getTaskNode.add
      necessaryEqualities foreach methodDecl.getValueRestrictionOrSortRestriction.add

      val allVariablesToXMLVariable = abstractParametersToVariables ++ taskParametersToVariables

      // add all variable constraints
      method.subPlan.variableConstraints.constraints map {
        case Equal(var1, value)        =>
          val valueRestriction = new ValueRestriction
          valueRestriction.setType("eq")
          valueRestriction.setVariableN(allVariablesToXMLVariable(var1)) // wrapping this in a variable will crash
          value match {
            case v@logic.Variable(_, _, _) => valueRestriction.setVariable(toVariable(allVariablesToXMLVariable(v)))
            case c@logic.Constant(_)       => valueRestriction.setConstant(toConstant(constantsToConstDecl(c)))
          }
          valueRestriction
        case NotEqual(var1, value)     =>
          val valueRestriction = new ValueRestriction
          valueRestriction.setType("neq")
          valueRestriction.setVariableN(allVariablesToXMLVariable(var1)) // wrapping this in a variable will crash
          value match {
            case v@logic.Variable(_, _, _) => valueRestriction.setVariable(toVariable(allVariablesToXMLVariable(v)))
            case c@logic.Constant(_)       => valueRestriction.setConstant(toConstant(constantsToConstDecl(c)))
          }
          valueRestriction
        case OfSort(variable, sort)    =>
          val sortRestriction = new SortRestriction
          sortRestriction.setType("eq")
          sortRestriction.setVariable(allVariablesToXMLVariable(variable)) // wrapping this in a variable will crash
          sortRestriction.setSort(sortToSortDecl(sort))
          sortRestriction
        case NotOfSort(variable, sort) =>
          val sortRestriction = new SortRestriction
          sortRestriction.setType("neq")
          sortRestriction.setVariable(allVariablesToXMLVariable(variable)) // wrapping this in a variable will crash
          sortRestriction.setSort(sortToSortDecl(sort))
          sortRestriction
      } foreach methodDecl.getValueRestrictionOrSortRestriction.add

      // ordering constraints
      method.subPlan.orderingConstraints.originalOrderingConstraints.sortWith({ case (plan.element.OrderingConstraint(prev1, after1), plan.element.OrderingConstraint(prev2, after2)) =>
        if (prev1.id != prev2.id) prev1.id < prev2.id else after1.id < after2.id
                                                                               }) filterNot { _.containsAny(method.subPlan.init, method.subPlan.goal) } map {
        case plan.element.OrderingConstraint(before, after) =>
          val ordering = new OrderingConstraint
          ordering.setPredecessor(planStepsToTaskNodes(before))
          ordering.setSuccessor(planStepsToTaskNodes(after))
          ordering
      } foreach methodDecl.getOrderingConstraint.add

      // causal links
      method.subPlan.causalLinks map { case plan.element.CausalLink(producer, consumer, condition) =>
        val link = new CausalLink
        link.setProducer(planStepsToTaskNodes(producer))
        link.setConsumer(planStepsToTaskNodes(consumer))
        setInnerValue(link, formulaToAny(condition, predicatesToXMLPredicates, allVariablesToXMLVariable, sortToSortDecl))
        link
      } foreach methodDecl.getCausalLink.add

      xmldomain.getMethodDeclaration.add(methodDecl)
    }

    // 6. Decomposition axioms
    assert(dom.decompositionAxioms.isEmpty)

    // 7. step generate the String
    val context: JAXBContext = JAXBContext.newInstance(classOf[XMLDomain])
    val marshaller: Marshaller = context.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

    // write the domain description into a "string"
    val primaryOut: ByteArrayOutputStream = new ByteArrayOutputStream()
    marshaller.marshal(xmldomain, primaryOut)
    new String(primaryOut.toByteArray, Charset.defaultCharset())
  }
}


private object XMLWriterProblem {

  import de.uniulm.ki.panda3.symbolic.parser.xml.problem._

  private def toConstant(constant: logic.Constant): Constant = {
    val xmlconstant = new Constant
    xmlconstant.setName(constant.name)
    xmlconstant
  }

  private def toVariable(variableDeclaration: VariableDeclaration): Variable = {
    val variable = new Variable
    variable.setName(variableDeclaration)
    variable
  }


  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  def writeProblem(dom: Domain, plan: Plan, domainName: String, problemName: String): String = {
    val xmlproblem: Problem = new ObjectFactory().createProblem()

    xmlproblem.setDomain(domainName)
    xmlproblem.setName(problemName)
    xmlproblem.setType("pure-hierarchical")


    // 1. step generate all constants
    //determine which constants would have ended up in the domain
    val domainConstants: Seq[logic.Constant] = dom.decompositionMethods flatMap { method => method.subPlan.variableConstraints.constraints collect {
      case Equal(_, c: logic.Constant)    => c
      case NotEqual(_, c: logic.Constant) => c
    }
    }
    val problemConstants = dom.constants filterNot domainConstants.contains

    problemConstants map { c =>
      val constantDeclaration = new ConstantDeclaration
      constantDeclaration.setName(c.name)
      val sortOfConstant = dom.getSortOfConstant(c).get // this might cause an exception, but if so writing is not possible at all
      constantDeclaration.setSort(sortOfConstant.name)

      constantDeclaration
    } map xmlproblem.getConstantDeclaration.add

    // 2. generate the initial state
    val initAndGoalCSP = plan.variableConstraints
    assert(plan.init.schema.isInstanceOf[ReducedTask])
    val initLiterals = plan.init.substitutedEffects filter { _.isPositive } // only positive effects are necessary in the problem file
    val initialState = new InitialState
    initLiterals map { literal =>
      val fact = new Fact
      fact.setRelation(literal.predicate.name)
      literal.parameterVariables foreach { v =>
        initAndGoalCSP.getRepresentative(v) match {
          case constant: logic.Constant => fact.getConstant.add(toConstant(constant))
          case variable: logic.Variable => assert(false, "The initial state must be ground, but I got " + variable)
        }
      }
      fact
    } foreach initialState.getFact.add
    xmlproblem.setInitialState(initialState)


    // 3. generate the initial state
    assert(plan.goal.schema.isInstanceOf[ReducedTask])
    val goalLiterals = plan.goal.substitutedPreconditions filter { _.isPositive } // only positive effects are necessary in the problem file
    val goalState = new Goals
    goalLiterals map { literal =>
      val fact = new Fact
      fact.setRelation(literal.predicate.name)
      literal.parameterVariables foreach { v =>
        initAndGoalCSP.getRepresentative(v) match {
          case c: logic.Constant => fact.getConstant.add(toConstant(c))
          case v: logic.Variable => assert(false, "The initial state must be ground")
        }
      }
      fact
    } foreach goalState.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference.add
    xmlproblem.setGoals(goalState)

    // 4. generate the initial task network
    val initialTaskNetwork = new InitialTaskNetwork
    // 4.1 plan steps
    val variableToXMLVariableDeclaration: mutable.Map[logic.Variable, VariableDeclaration] = new mutable.HashMap[logic.Variable, VariableDeclaration]()
    val planStepsToXMLPlanSteps: mutable.Map[PlanStep, TaskNode] = new mutable.HashMap[PlanStep, TaskNode]()
    plan.planStepsWithoutInitGoal map { ps =>
      val taskNode = new TaskNode
      taskNode.setName("task_" + ps.id + "_" + ps.schema.name)
      taskNode.setTaskSchema(ps.schema.name)

      ps.arguments.zipWithIndex map { case (argument, argumentIndex) =>
        // we have to generate new variables of each argument to keep panda2 happy
        val varDecl = new VariableDeclaration
        varDecl.setName("planstep_" + ps.id + "_argument_" + argumentIndex + "_" + argument.name)
        varDecl.setSort(argument.sort.name)

        if (variableToXMLVariableDeclaration.contains(argument)) {
          val valueRestriction = new ValueRestriction
          valueRestriction.setType("eq")
          valueRestriction.setVariable(variableToXMLVariableDeclaration(argument))
          valueRestriction.getVariableOrConstant.add(toVariable(varDecl))
          // add the equality constraint
          initialTaskNetwork.getValueRestrictionOrSortRestriction add valueRestriction
        } else {
          variableToXMLVariableDeclaration(argument) = varDecl
        }

        varDecl
      } foreach taskNode.getVariableDeclaration.add
      planStepsToXMLPlanSteps(ps) = taskNode
      taskNode
    } foreach initialTaskNetwork.getTaskNode.add
    // 4.1.2 add other variables
    val remainingVariables = plan.variableConstraints.variables filterNot variableToXMLVariableDeclaration.contains
    remainingVariables foreach { variable =>
      val possibleRepresentative = variableToXMLVariableDeclaration.keySet find { plan.variableConstraints.getRepresentative(_) == plan.variableConstraints.getRepresentative(variable) }
      if (possibleRepresentative.isDefined) variableToXMLVariableDeclaration(variable) = variableToXMLVariableDeclaration(possibleRepresentative.get)
    }
    // 4.2 variable constraints
    plan.variableConstraints.constraints.distinct map {
      case e@Equal(variable, value)  => (e, "eq")
      case NotEqual(variable, value) => (Equal(variable, value), "neq")
      case os@OfSort(variable, sort) => (os, "eq")
      case NotOfSort(variable, sort) => (OfSort(variable, sort), "neq")
    } map {
      case (Equal(variable, value), modus) =>
        if (variableToXMLVariableDeclaration.contains(variable)) {
          val valueRestriction = new ValueRestriction
          valueRestriction.setType(modus)
          valueRestriction.setVariable(variableToXMLVariableDeclaration(variable))
          valueRestriction.getVariableOrConstant.add(value match {
                                                       case constant: logic.Constant => toConstant(constant)
                                                       case variable: logic.Variable => toVariable(variableToXMLVariableDeclaration(variable))
                                                     })
          Some(valueRestriction)
        } else None
      case (OfSort(variable, sort), modus) =>
        if (variableToXMLVariableDeclaration.contains(variable)) {
          val sortRestriction = new SortRestriction
          sortRestriction.setType(modus)
          sortRestriction.setVariable(variableToXMLVariableDeclaration(variable))
          sortRestriction.setSort(sort.name)
          Some(sortRestriction)
        } else None
    } collect { case Some(x) => x } foreach initialTaskNetwork.getValueRestrictionOrSortRestriction.add
    // 4.3 ordering constraints
    plan.orderingConstraints.minimalOrderingConstraints() filterNot { _.containsAny(plan.init, plan.goal) } map {
      case symbolic.plan.element.OrderingConstraint(before, after) =>
        val ordering = new OrderingConstraint
        ordering.setPredecessor(planStepsToXMLPlanSteps(before))
        ordering.setSuccessor(planStepsToXMLPlanSteps(after))
        ordering
    } foreach initialTaskNetwork.getOrderingConstraint.add
    // 4.4 causal links
    plan.causalLinks map { case symbolic.plan.element.CausalLink(producer, consumer, condition) =>
      val causalLink = new CausalLink
      causalLink.setProducer(planStepsToXMLPlanSteps(producer))
      causalLink.setConsumer(planStepsToXMLPlanSteps(consumer))
      val linkAtomic = new Atomic
      linkAtomic.setRelation(condition.predicate.name)
      condition.parameterVariables map { parameter => plan.variableConstraints.getRepresentative(parameter) } map {
        case vari: logic.Variable  => toVariable(variableToXMLVariableDeclaration(vari))
        case const: logic.Constant => toConstant(const)
      } foreach linkAtomic.getVariableOrConstant.add


      val linkLiteral = if (condition.isPositive) linkAtomic
      else {
        val not = new Not
        not.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists.add(linkAtomic)
        not
      }
      causalLink.getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists.add(linkLiteral)

      causalLink
    } foreach initialTaskNetwork.getCausalLink.add



    xmlproblem.setInitialTaskNetwork(initialTaskNetwork)


    // 5. step generate the String
    val context: JAXBContext = JAXBContext.newInstance(classOf[Problem])
    val marshaller: Marshaller = context.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

    // write the domain description into a "string"
    val primaryOut: ByteArrayOutputStream = new ByteArrayOutputStream()
    marshaller.marshal(xmlproblem, primaryOut)
    new String(primaryOut.toByteArray, Charset.defaultCharset())
  }
}

object Foo {
  def main(args: Array[String]) {
    val domainFile = if (args.length >= 1) args(0) else "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    val problemFile = if (args.length >= 2) args(1) else "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"

    val readDomain = XMLParser.asParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    val expanded = ExpandSortHierarchy.transform(readDomain._1, readDomain._2, ())

    val xmlWriter = new XMLWriter("monroe", "monroe-1")

    println(xmlWriter.writeProblem(expanded._1, expanded._2))

  }
}
