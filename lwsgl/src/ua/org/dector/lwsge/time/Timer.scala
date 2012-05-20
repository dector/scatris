package ua.org.dector.lwsge.time

/**
 * @author dector (dector9@gmail.com)
 */

class Timer(initValue: Long = 0L, startNow: Boolean = false) {
    private var value = initValue
    private var _started = startNow
    private var _paused = false

    private var lastDiffTime = if (startNow) TimerManager.getCurrentTime else 0L

    def started = _started
    def paused = _paused
    private def started_= (newValue: Boolean) {_started = newValue}
    private def paused_= (newValue: Boolean) {_paused = newValue}

    def += (addValue: Long) {value += addValue}
    def -= (subValue: Long) {value -= subValue}

    def start() {
        if (! started || paused) {
            if (! paused) value = 0

            started = true
            paused = false
            lastDiffTime = TimerManager.getCurrentTime
        }
    }

    def time = {
        if (started && !paused) updateValue()

        value
    }

    def pause() {
        if (started) {
            updateValue()
            paused = true
        }
    }

    def stop() {
        if (started) {
            updateValue()
            started = false
        }
    }

    def restart() {
        if (started) started = false

        value = 0
        start()
    }

    private def updateValue() {
        val currTime = TimerManager.getCurrentTime
        value += currTime - lastDiffTime

        lastDiffTime = currTime
    }
}
