package de.uniulm.ki.util

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class TimeCapsule {

  private val currentAccumulatedTime: scala.collection.mutable.Map[String, Long] = new mutable.HashMap[String, Long]().withDefaultValue(0)

  private val currentStarts: scala.collection.mutable.Map[String, Long] = new mutable.HashMap[String, Long]()


  def start(activity: String): Unit = {
    assert(!(currentStarts contains activity), "Tried to start measuring\"" + activity + "\", whose measurement is already running")
    currentStarts.put(activity, System.currentTimeMillis())
  }

  def stop(activity: String): Unit = {
    assert(currentStarts contains activity, "Tried to stop measuring\"" + activity + "\", whose measurement hasn't started")

    val lastTime = currentAccumulatedTime(activity)
    val newTime = System.currentTimeMillis() - lastTime
    currentAccumulatedTime.put(activity, newTime)
  }

  /**
    * returns an imutable copy of the internally accumulated time
    */
  def timeMap: Map[String, Long] = currentAccumulatedTime.toMap
}
