package de.uniulm.ki.util

import de.uniulm.ki.panda3.symbolic.PrettyPrintable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DataCapsule extends PrettyPrintable {

  import scala.util.control.Exception.allCatch

  def dataMap(): Map[String, String]

  def integralDataMap() : Map[String,Int] = dataMap() map {case (a,b) => a -> (allCatch opt b.toInt)} collect {case (a, Some(b)) => a -> b}

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = {
    val builder = new StringBuilder()

    (dataMap groupBy { _._1.split(":").head }).toSeq sortBy { _._1 } map { case (g, r) => (g.substring(3), r) } foreach { case (group, inner) =>
      builder append ("============ " + group + " ============\n")
      val reducedNamesWithPrefix = inner map { case (info, value) => info.substring(group.length + 4) -> value } toSeq
      val reducedNames = reducedNamesWithPrefix.sortBy({ _._1 }) map { case (info, value) => info.substring(3) -> value }
      val maxLen = reducedNames.map { _._1.length } max

      reducedNames foreach { case (info, value) => builder append String.format("%-" + maxLen + "s = %s\n", info.asInstanceOf[Object], value.asInstanceOf[Object]) }
    }

    builder.toString()
  }

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a detailed information about the object */
  override def longInfo: String = shortInfo


  def csvString(): String = {
    (dataMap groupBy { _._1.split(":").head }).toSeq sortBy { _._1 } map { case (g, r) => (g.substring(3), r) } flatMap { case (group, inner) =>
      val reducedNamesWithPrefix = inner map { case (info, value) => info.substring(group.length + 4) -> value } toSeq

      reducedNamesWithPrefix.sortBy({ _._1 }) map { case (info, value) => value }
    } mkString ","
  }

  def keyValueListString(): String = dataMap map { case (k, v) =>
    DataCapsule.toOutputString(k) + "=" + DataCapsule.toOutputString(v.toString)
  } mkString ("" + DataCapsule.SEPARATOR)
}

object DataCapsule {
  val SEPARATOR = ';'
  val DELIMITER = '\"'

  def toOutputString(s: String): String = DELIMITER + s + DELIMITER
}
