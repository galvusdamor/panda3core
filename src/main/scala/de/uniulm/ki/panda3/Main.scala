package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.efficient.search.HeuristicSearch
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util._


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Main {
  def main(args: Array[String]) {
    println("This is Panda3")

    /*if (args.length != 3) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output file. If the file extension is .dot a dot file will be" +
                " written, else a pdf.")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    val outputPDF = args(2)*/

    //val domFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //outputPDF = "/home/dhoeller/Schreibtisch/test.pdf"
    val outputPDF = "/home/gregor/test.pdf"
    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_Small.xml"
    //val domFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem-test-split1.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(XMLParserType),
                                             PreprocessingConfiguration(true, true, true, true, false, true),
                                             SearchConfiguration(None, true, AStarType, Some(TDGMinimumModification), true),
                                             PostprocessingConfiguration(Set(ProcessingTimings,
                                                                             SearchStatus, SearchResult,
                                                                             SearchStatistics,
                                                                             //SearchSpace,
                                                                             SolutionInternalString,
                                                                             SolutionDotString)))

    System.in.read()

    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

    println("Panda says: " + results(SearchStatus))
    printInformationByCategory(results(SearchStatistics))
    printInformationByCategory(results(ProcessingTimings))

    if (results(SearchStatus) == SearchState.SOLUTION) {

      // write output
      if (outputPDF.endsWith("dot")) {
        writeStringToFile(results(SolutionDotString).get, new File(outputPDF))
      } else {
        Dot2PdfCompiler.writeDotToFile(results(SolutionDotString).get, outputPDF)
      }
    }
    // check the tree
    def dfs(searchNode: SearchNode): Unit = if (!searchNode.dirty) {
      searchNode.modifications.length // force computation (and check of assertions)
      searchNode.children foreach { x => dfs(x._1) }
    }

    if (searchConfig.postprocessingConfiguration.resultsToProduce contains SearchSpace) dfs(results(SearchSpace))
  }


  def printInformationByCategory(information: Map[String, _]): Unit = {
    (information groupBy { _._1.split(":").head }).toSeq sortBy { _._1 } map { case (g, r) => (g.substring(3), r) } foreach { case (group, inner) =>
      println("============ " + group + " ============")
      val reducedNamesWithPrefix = inner map { case (info, value) => info.substring(group.length + 4) -> value } toSeq
      val reducedNames = reducedNamesWithPrefix.sortBy({ _._1 }) map { case (info, value) => info.substring(3) -> value }
      val maxLen = reducedNames.map { _._1.length } max

      reducedNames foreach { case (info, value) =>
        printf("%-" + maxLen + "s = %d\n", info, value)
      }

    }
  }

}