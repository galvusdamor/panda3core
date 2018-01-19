package de.uniulm.ki.panda3.progression.sasp.mergeAndShrink

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.util.{DirectedGraphDotOptions, Dot2PdfCompiler}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object StratificationPlotter {

  def plotStratification(d : Domain) = {
    Dot2PdfCompiler.writeDotToFile(d.taskSchemaTransitionGraph.condensation.dotString(DirectedGraphDotOptions(),{t =>
      //i += 1

      (t map {ta =>
        val i = ProgressionNetwork.taskToIndex.get(ta)
        i
      }).mkString(",")
      //i + ""
    }),"decomp_hierarchy.pdf")

  }
}
