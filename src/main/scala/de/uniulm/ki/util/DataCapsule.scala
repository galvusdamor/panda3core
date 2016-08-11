package de.uniulm.ki.util

import de.uniulm.ki.panda3.symbolic.PrettyPrintable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DataCapsule extends PrettyPrintable {
  def integralDataMap(): Map[String, Long]

  def floatingDataMap(): Map[String, Double]

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = {
    val builder = new StringBuilder()

    (floatingDataMap groupBy { _._1.split(":").head }).toSeq sortBy { _._1 } map { case (g, r) => (g.substring(3), r) } foreach { case (group, inner) =>
      builder append ("============ " + group + " ============\n")
      val reducedNamesWithPrefix = inner map { case (info, value) => info.substring(group.length + 4) -> value } toSeq
      val reducedNames = reducedNamesWithPrefix.sortBy({ _._1 }) map { case (info, value) => info.substring(3) -> value }
      val maxLen = reducedNames.map { _._1.length } max

      val castedIfPossibleMap: Map[String, Any] = reducedNames map { case (a, b: Double) => if (b.round == b) (a, b.toLong) else (a, b); case x => x } toMap

      castedIfPossibleMap foreach {
        case (info, value: Double) => builder append String.format("%-" + maxLen + "s = %f\n", info.asInstanceOf[Object], value.asInstanceOf[Object])
        case (info, value: Long)   => builder append String.format("%-" + maxLen + "s = %d\n", info.asInstanceOf[Object], value.asInstanceOf[Object])
      }
    }

    builder.toString()
  }

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a detailed information about the object */
  override def longInfo: String = shortInfo


  def csvString(): String = {
    (floatingDataMap groupBy { _._1.split(":").head }).toSeq sortBy { _._1 } map { case (g, r) => (g.substring(3), r) } flatMap { case (group, inner) =>
      val reducedNamesWithPrefix = inner map { case (info, value) => info.substring(group.length + 4) -> value } toSeq

      reducedNamesWithPrefix.sortBy({ _._1 }) map { case (info, value) => value }
    } mkString ","

  }
}
