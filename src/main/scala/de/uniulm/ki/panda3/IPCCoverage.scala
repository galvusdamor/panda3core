package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.efficient.heuristic.AlwaysZeroHeuristic
import de.uniulm.ki.panda3.symbolic.search.SearchState


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object IPCCoverage {
  def main(args: Array[String]) {

    //val ipc7Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7").listFiles() filterNot {_.getName contains "openstack"} map { d => ("IPC7", d) }
    //val ipc6Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6").listFiles() filterNot {_.getName contains "openstack"} map { d => ("IPC6", d) }
    val ipc7Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7").listFiles() map { d => ("IPC7", d) }
    val ipc6Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6").listFiles() map { d => ("IPC6", d) }
    val ipc5Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC5").listFiles() map { d => ("IPC5", d) }
    val ipc4Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4").listFiles() map { d => ("IPC4", d) }
    val ipc3Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3").listFiles() map { d => ("IPC3", d) }
    val ipc2Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC2").listFiles() map { d => ("IPC2", d) }
    val ipc1Domains = new File("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC1").listFiles() map { d => ("IPC1", d) }
    val allICPs = "IPC1" :: "IPC2" :: "IPC3" :: "IPC4" :: "IPC5" :: "IPC6" :: "IPC7" :: Nil
    //val ipc7Domains = new File("IPC7").listFiles() map { d => ("IPC7", d) }
    //val ipc6Domains = new File("IPC6").listFiles() map { d => ("IPC6", d) }


    val results = ipc1Domains ++ ipc2Domains ++ ipc3Domains ++ ipc4Domains ++ ipc5Domains ++ ipc6Domains ++ ipc7Domains map { case (ipc, d) => {
      val domainDir = d.listFiles() find { _.getName == "domain" }
      assert(domainDir.isDefined)
      val problemDir = d.listFiles() find { _.getName == "problems" }
      assert(problemDir.isDefined)

      val domainFiles = domainDir.get.listFiles()
      val problemFiles = problemDir.get.listFiles()

      assert(domainFiles.length == 1 || domainFiles.length == problemFiles.length)

      val usableDomainFiles = if (domainFiles.length == 1) List.fill(problemFiles.length)(domainFiles.head) else domainFiles.toList

      val solvedInstances = problemFiles.sortBy(_.getName) zip usableDomainFiles.sortBy(_.getName) map { case (problemFile, domainFile) =>
        println(ipc + " " + d.getPath + " " + problemFile.getName + " & " + domainFile.getName)
        val domInputStream = new FileInputStream(domainFile)
        val probInputStream = new FileInputStream(problemFile)

        val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                                 ParsingConfiguration(OldPDDLType),
                                                 PreprocessingConfiguration(false, true, None, false, false, false, None, false, false),
                                                 SearchConfiguration(Some(0), Some(0), AStarActionsType, Some(NumberOfFlaws), LCFR),
                                                 //SearchConfiguration(None, Some(60), efficientSearch = true, AStarType, Some(NumberOfFlaws), true),
                                                 //Some (TDGMinimumModification)
                                                 PostprocessingConfiguration(/*verifySolution = false,*/ Set(ProcessingTimings,
                                                                                                             SearchStatus, SearchResult,
                                                                                                             SearchStatistics,
                                                                                                             //SearchSpace,
                                                                                                             SolutionInternalString,
                                                                                                             SolutionDotString)))


        val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

        println("Panda says: " + results(SearchStatus))
        if (results(SearchStatus) == SearchState.SOLUTION) 1 else 0
      } sum

      (ipc + d.getName, solvedInstances, problemFiles.length)
    }
    }


    println(results map { case (dom, solved, total) => dom + ": " + solved + "/" + total } mkString "\n")


    // per IPC
    allICPs foreach { ipc =>
      val ipcResults = results filter { _._1 contains ipc }
      val (totalSolved, totalProblems) = ipcResults.foldLeft((0, 0)) { case ((gs, gt), (_, s, t)) => (s + gs, t + gt) }

      println(ipc + ": " + totalSolved + "/" + totalProblems)
    }

    val (totalSolved, totalProblems) = results.foldLeft((0, 0)) { case ((gs, gt), (_, s, t)) => (s + gs, t + gt) }
    println("GRAND TOTAL: " + totalSolved + "/" + totalProblems)
  }
}
