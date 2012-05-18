package ua.org.dector.lwsge.time

import collection.mutable.HashMap

/**
 * @author dector (dector9@gmail.com)
 */

object TimerManager {
    val TIMER_NOT_FOUND = -1L

    private val timers = HashMap.empty[String, Timer]

    def getCurrentTime = System.currentTimeMillis

    def createTimer(timerId: String): Timer = {
        val timer = new Timer
        timers(timerId) = timer
        timer
    }

    def createTimer(timerId: String, startTime: Long) {
        timers(timerId) = new Timer(startTime)
    }

    def apply(timerId: String): Timer = {
        if (timers contains timerId)
            timers(timerId)
        else
            throw new TimerNotFoundException
    }

    /*def update(timerId: String, time: Long) {
        if (timers contains timerId)
            timers(timerId) = time
    }*/

    def destroyTimer(timerId: String) {
        timers.remove(timerId)
    }
}
