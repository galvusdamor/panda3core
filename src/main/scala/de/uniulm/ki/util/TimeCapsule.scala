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
    startOrLetRun(activity)
  }


  def startOrLetRun(activity: String): Unit = if (!(currentStarts contains activity)) currentStarts.put(activity, System.currentTimeMillis())

  def stop(activity: String): Unit = {
    assert(currentStarts contains activity, "Tried to stop measuring\"" + activity + "\", whose measurement hasn't started")

    val lastTime = currentAccumulatedTime(activity)
    val newTime = System.currentTimeMillis() - currentStarts(activity) + lastTime
    currentStarts remove activity

    currentAccumulatedTime.put(activity, newTime)
  }

  /**
    * returns an immutable copy of the internally accumulated time
    */
  def timeMap: Map[String, Long] = currentAccumulatedTime.toMap
}
