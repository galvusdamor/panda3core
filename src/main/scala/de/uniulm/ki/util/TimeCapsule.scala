// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
                        //System.err.println("===================================STARTING " + activity)
                        //System.err.println(Thread.currentThread().getStackTrace().map(_.toString).mkString("\n"))
                        assert(!(currentStarts contains activity), "Tried to start measuring\"" + activity + "\", whose measurement is already running")
                        startOrLetRun(activity)
                      }


  def startOrLetRun(activity: String): Unit =
    this.synchronized {
                        val threadID: Long = Thread.currentThread().getId

                        if (!(currentStarts contains activity)) {
                          currentStarts.put(activity, getCPUTimeOfCurrentThread()) // convert to milliseconds
                          currentThread.put(activity, threadID)
                          //System.err.println("===================================CONTINUING " + activity)

                        } else
                          assert(threadID == currentThread(activity), "Tried to let-run a time measurement from another thread")
                      }

  def stop(activity: String): Unit =
    this.synchronized {
                        //System.err.println("===================================STOPPING " + activity)
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

sealed trait TimeTakingMode

object WallTime extends TimeTakingMode

object ThreadCPUTime extends TimeTakingMode

object TimeCapsule {
  var timeTakingMode : TimeTakingMode = WallTime

  private val threadTimer = ManagementFactory.getThreadMXBean

  def getCPUTimeOfCurrentThread(): Long = timeTakingMode match {
    case ThreadCPUTime => threadTimer.getCurrentThreadCpuTime / 1000000
    case WallTime => System.currentTimeMillis()
  }

  def getCPUTimeOfThread(id: Long): Long =  timeTakingMode match {
    case ThreadCPUTime => threadTimer.getThreadCpuTime(id) / 1000000
    case WallTime => System.currentTimeMillis()
  }
}
