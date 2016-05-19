package de.uniulm.ki.panda3

import java.io.FileInputStream

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, SHOPMethodCompiler, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Test {

  def main(args: Array[String]) {
    val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    print("Parsing domain and problem ... ")
    //val domAndInitialPlan = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    val domAndInitialPlan = XMLParser.asParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    print("done\npreprocessing ... ")
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())

    print("transform to efficient representation ... ")

    val wrapper = Wrapping(flattened)
    val initialPlan = wrapper.unwrap(flattened._2)

    // efficient modifications
    val efficientMods = initialPlan.flaws map { f => (f,f.resolver) }
    val symbolicMods = flattened._2.flaws map { f => (f,f.resolvents(flattened._1)) }


    println("ÄÄÄÄÄ")
  }
}
