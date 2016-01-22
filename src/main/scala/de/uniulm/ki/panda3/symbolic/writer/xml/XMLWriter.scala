package de.uniulm.ki.panda3.symbolic.writer.xml

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.xml.bind.{JAXBContext, Marshaller}

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Formula, Literal, Predicate, Sort}
import de.uniulm.ki.panda3.symbolic.parser.xml._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.writer.Writer
import de.uniulm.ki.panda3.symbolic.{logic, plan}

import scala.collection.mutable

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class XMLWriter(domainName: String, problemName: String) extends Writer {

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


  /**
    * Takes a domain and writes and produces a string representation thereof.
    * This will not write any constant into the domain string
    */
  override def writeDomain(dom: Domain): String = {
    val xmldomain: XMLDomain = new ObjectFactory().createDomain()
    xmldomain.setType("pure-hierarchical")
    xmldomain.setName(domainName)

    // 1. Step build the sorts (if the sort graph contains a circle we cannot translate)
    val sortToSortDecl: Map[Sort, SortDeclaration] = dom.sortGraph.topologicalOrdering.get.reverse.foldLeft(Map[Sort, SortDeclaration]())({ case (map, s) =>
      val ns = new SortDeclaration
      ns.setName(s.name)
      ns.setType("concrete")

      s.subSorts map map map { subs => val ssDecl = new SubSort
        ssDecl.setSort(subs)
        ns.getSubSort.add(ssDecl)
      }

      map.+((s, ns))
                                                                                                                                          })
    sortToSortDecl.values foreach { xmldomain.getSortDeclaration.add }


    // 2. Step add the constants
    val constantsToConstDecl: Map[logic.Constant, ConstantDeclaration] = (dom.constants map { c =>
      val sort = dom.getSortOfConstant(c).get // unsafe, but if not possible, writing will not be possible
    val cd: ConstantDeclaration = new ConstantDeclaration
      cd.setName(c.name)
      cd.setSort(sortToSortDecl(sort))
      (c, cd)
    }).toMap
    constantsToConstDecl.values foreach { xmldomain.getConstantDeclaration.add }

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
    predicatesToXMLPredicates.values foreach { xmldomain.getRelationDeclaration.add }

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
    tasksToTaskDeclarations.values foreach { xmldomain.getTaskSchemaDeclaration.add }


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
      val temp = method.subPlan.planStepWithoutInitGoal map { ps =>
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
      planStepsToTaskNodes.values foreach methodDecl.getTaskNode.add
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
      method.subPlan.orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(method.subPlan.init, method.subPlan.goal) } map {
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

  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  override def writeProblem(dom: Domain, plan: Plan): String = ???
}