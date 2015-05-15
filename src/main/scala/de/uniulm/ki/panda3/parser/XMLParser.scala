package de.uniulm.ki.panda3.parser


import java.io.FileInputStream
import javax.xml.bind.{JAXBContext, JAXBElement, Unmarshaller}
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.sax.SAXSource

import de.uniulm.ki.panda3.csp.{Equal, Variable, VariableConstraint}
import de.uniulm.ki.panda3.domain.{Domain, Task}
import de.uniulm.ki.panda3.logic.{Constant, Literal, Predicate, Sort}
import de.uniulm.ki.panda3.parser.xml._
import org.xml.sax.XMLReader

import scala.collection.{JavaConversions, _}
import scala.xml.InputSource

/**
 *
 *
 * @author Kadir Dede (kadir.dede@uni-ulm.de)
 */
object XMLParser extends Parser {

  @Override
  override def parseFromFile(filename: String): Domain = {

    val context: JAXBContext = JAXBContext.newInstance(classOf[XMLDomain])
    val marshaller: Unmarshaller = context.createUnmarshaller()
    val spf: SAXParserFactory = SAXParserFactory.newInstance()
    spf.setXIncludeAware(true)
    spf.setNamespaceAware(true)
    spf.setValidating(true)
    // Not required for JAXB/XInclude
    val xr: XMLReader = spf.newSAXParser().getXMLReader()
    val source: SAXSource = new SAXSource(xr, new InputSource(new FileInputStream(filename)))
    val dom: XMLDomain = marshaller.unmarshal(source).asInstanceOf[XMLDomain]



    val constantMap: mutable.Map[String, Seq[Constant]] = mutable.Map()
    /**
     * @param input the whole sequence of constants from XMLDomain
     * @return the whole sequence of constants used in Domain
     *         also: generates a map of type (String -> Seq[Constant])
     */
    def createConstantSeq(input: Seq[ConstantDeclaration]): Seq[Constant] = input map { const =>
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
    val xmlConstantToScalaConstant: Map[ConstantDeclaration, Constant] = (JavaConversions.asScalaBuffer(dom.getConstantDeclaration) zip constantSeq).toMap
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


    val predicates: Seq[Predicate] = JavaConversions.asScalaBuffer(dom.getRelationDeclaration) map { relation =>
      Predicate(relation.getName, JavaConversions.asScalaBuffer(relation.getArgumentSort) map { argument => xmlSortsToScalaSorts(argument.getSort.asInstanceOf[SortDeclaration]) })
    }
    val xmlPredicateToScalaPredicate: Map[RelationDeclaration, Predicate] = (JavaConversions.asScalaBuffer(dom.getRelationDeclaration) zip predicates).toMap

    val tasks: Seq[Task] = JavaConversions.asScalaBuffer(dom.getTaskSchemaDeclaration) map { taskSchema =>

      // split the content
      val splitContent = JavaConversions.asScalaBuffer(taskSchema.getContent) filter {!_.isInstanceOf[JAXBElement[Any]]} partition {_.isInstanceOf[VariableDeclaration]}
      assert(splitContent._2.size == 2)
      val xmlVariableDeclarations = splitContent._1 map {_.asInstanceOf[VariableDeclaration]}
      val variables = (xmlVariableDeclarations.zipWithIndex) map { case (variableDeclaration, idx) => Variable(idx, variableDeclaration.getName, xmlSortsToScalaSorts(
        variableDeclaration.getSort.asInstanceOf[SortDeclaration]))
      }
      val varDeclToVariable: Map[VariableDeclaration, Variable] = (xmlVariableDeclarations zip variables).toMap

      var variableConstraints: Seq[VariableConstraint] = Nil

      def toLiteralList(xmlStruct: Any, positive: Boolean): Seq[Literal] = {
        if (xmlStruct.isInstanceOf[And]) {
          if (positive) {
            JavaConversions.asScalaBuffer(xmlStruct.asInstanceOf[And].getAtomicOrNotOrAnd) flatMap {toLiteralList(_, positive)}
          } else {
            assert(false)
            Nil
          }
        } else if (xmlStruct.isInstanceOf[Or]) {
          if (!positive) {
            JavaConversions.asScalaBuffer(xmlStruct.asInstanceOf[And].getAtomicOrNotOrAnd) flatMap {toLiteralList(_, positive)}
          } else {
            assert(false)
            Nil
          }
        } else if (xmlStruct.isInstanceOf[Not])
          toLiteralList(getAnyFromNot(xmlStruct.asInstanceOf[Not]), !positive)
        else if (xmlStruct.isInstanceOf[Atomic]) {
          val atomic: Atomic = xmlStruct.asInstanceOf[Atomic]
          val parameterVariables: Seq[Variable] = JavaConversions.asScalaBuffer(atomic.getVariableOrConstant) map { case variable: xml.Variable => varDeclToVariable(
            variable.getName.asInstanceOf[VariableDeclaration])
          case constant: xml.Constant =>
            val constDeclaration: ConstantDeclaration = constant.getName.asInstanceOf[ConstantDeclaration]
            val newVariable = Variable(variables.size + variableConstraints.size, "ConstantVariable" + constant.getName,
                                       xmlSortsToScalaSorts(constDeclaration.getSort.asInstanceOf[SortDeclaration]))
            variableConstraints = variableConstraints :+ Equal(newVariable, xmlConstantToScalaConstant(constDeclaration))
            newVariable
          }
          Literal(xmlPredicateToScalaPredicate(atomic.getRelation.asInstanceOf[RelationDeclaration]), positive, parameterVariables) :: Nil
        } else Nil
        // TODO: handle exisistential quantifier
      }

      val preconditions = toLiteralList(splitContent._2(0), positive = true)
      val effects = toLiteralList(splitContent._2(1), positive = true)
      // TODO: add constant variables
      Task(taskSchema.getName, taskSchema.getType == "primitive", variables ++ (variableConstraints map { case Equal(vari, _) => vari }), variableConstraints, preconditions, effects)
    }


    // already done:
    // Sequence of Constants (constantSeq)
    // Sequence of Sorts     (sortSeq)
    // Sequence of Predicates
    // Sequence of Tasks
    // still to do:
    // Sequence of Decomposition Methods
    // Sequence of Decomposition Axioms

    Domain(sortSeq, constantSeq, predicates, Nil, Nil, Nil)
  }


  private def getAnyFromNot(not: Not): Any = ((not.getAnd :: not.getAtomic :: not.getExists :: not.getForall :: not.getImply :: not.getNot :: not.getOr :: Nil) find {_ != null}).get


}