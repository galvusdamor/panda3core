package de.uniulm.ki.util

import java.lang.management.ManagementFactory

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class TimeCapsule extends DataCapsule {

  import TimeCapsule._

  private val currentAccumulatedTime: scala.collection.mutable.Map[String, Long] = new mutable.HashMap[String, Long]().withDefaultValue(0)

  private val currentStarts: scala.collection.mutable.Map[String, Long] = new mutable.HashMap[String, Long]()
  private val currentThread: scala.collection.mutable.Map[String, Long] = new mutable.HashMap[String, Long]()


  def start(activity: String): Unit =
    this.synchronized {
                        assert(!(currentStarts contains activity), "Tried to start measuring\"" + activity + "\", whose measurement is already running")
                        startOrLetRun(activity)
                      }


  def startOrLetRun(activity: String): Unit =
    this.synchronized {
                        val threadID: Long = Thread.currentThread().getId

                        if (!(currentStarts contains activity)) {
                          currentStarts.put(activity, getCPUTimeOfCurrentThread()) // convert to milliseconds
                          currentThread.put(activity, threadID)
                        } else
                          assert(threadID == currentThread(activity), "Tried to let-run a time measurement from another thread")
                      }

  def stop(activity: String): Unit =
    this.synchronized {
                        assert(currentStarts contains activity, "Tried to stop measuring\"" + activity + "\", whose measurement hasn't started")
                        val threadID: Long = Thread.currentThread().getId
                        assert(threadID == currentThread(activity), "Tried to stop time measurement from another thread")

                        val threadCPUTime = getCPUTimeOfCurrentThread()
                        assert(threadCPUTime != -1, "Tried to stop an already dead thread.")

                        val lastTime = currentAccumulatedTime(activity)
                        val newTime = threadCPUTime - currentStarts(activity) + lastTime
                        currentStarts remove activity
                        currentThread remove activity

                        currentAccumulatedTime.put(activity, newTime)
                      }

  def stopOrIgnore(activity: String): Unit = this.synchronized { if (currentStarts contains activity) stop(activity) }

  def switchTimerToCurrentThreadOrIgnore(activity: String, timeout: Option[Long] = None): Unit =
    this.synchronized { if (currentStarts contains activity) switchTimerToCurrentThread(activity, timeout) }

  def switchTimerToCurrentThread(activity: String, timeout: Option[Long] = None): Unit =
    this.synchronized {
                        assert(currentStarts contains activity, "Tried to transfer measuring\"" + activity + "\", whose measurement " + "hasn't started")

                        // it can be possible that the other thread has died out ... if we don't have
                        // a timeout throw an assertion
                        val currentCPUTimeOfOldHostThread = getCPUTimeOfThread(currentThread(activity))

                        val timeHasPast = if (currentCPUTimeOfOldHostThread == -1) {
                          // thread has died out
                          assert(timeout.isDefined)
                          timeout.get
                        } else currentCPUTimeOfOldHostThread - currentStarts(activity)

                        // use memoised thread ID to switch
                        val lastTime = currentAccumulatedTime(activity)
                        val newTime = timeHasPast + lastTime

                        currentStarts remove activity
                        currentThread remove activity

                        currentAccumulatedTime.put(activity, newTime)


                        // restart timer
                        start(activity)
                      }

  def getCurrentElapsedTimeInThread(activity: String): Long =
    this.synchronized {
                        if (currentStarts contains activity) {
                          getCPUTimeOfThread(currentThread(activity)) - currentStarts(activity) + currentAccumulatedTime(activity)
                        } else currentAccumulatedTime(activity)
                      }

  def set(activity: String, time: Long): Unit = this.synchronized { currentAccumulatedTime.put(activity, time) }

  def addTo(activity: String, time: Long): Unit = this.synchronized {
                                                                      val currentSum = currentAccumulatedTime(activity)
                                                                      currentAccumulatedTime.put(activity, time + currentSum)
                                                                    }

  override def dataMap(): Map[String, String] = this.synchronized { currentAccumulatedTime.toMap map { case (a, b) => a -> b.toString } }
}

object TimeCapsule {
  private val threadTimer = ManagementFactory.getThreadMXBean

  def getCPUTimeOfCurrentThread(): Long = threadTimer.getCurrentThreadCpuTime / 1000000

  def getCPUTimeOfThread(id: Long): Long = threadTimer.getThreadCpuTime(id) / 1000000
}