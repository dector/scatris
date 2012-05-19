package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import ua.org.dector.scatris.Scatris
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.common.Config
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.GameController
import ua.org.dector.lwsge.state.{StateManager, GameState}

/**
 * @author dector (dector9@gmail.com)
 */

object RunningGameState extends GameState("Running") {
    def activate() {
        TimerManager.createTimer(Scatris.TICK_TIMER).start()
        TimerManager.createTimer(Scatris.LEFT_MOVE_TIMER).start()
        TimerManager.createTimer(Scatris.RIGHT_MOVE_TIMER).start()

        // TODO: Remove
        Scatris.play()
    }


    def preRenderCount() {
        if (TimerManager(Scatris.TICK_TIMER).time >= Scatris.tickTimeBound) {
            TimerManager(Scatris.TICK_TIMER).restart()
            Scatris.tick()
        }
    }

    def render() {}

    def checkInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            fallDownFast()
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Scatris.canMoveCurrElementLeft)
            moveCurrElementLeftByTimer()
        else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Scatris.canMoveCurrElementRight)
            moveCurrElementRightByTimer()

        while (Keyboard.next && Keyboard.getEventKeyState) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_UP =>
                    if (Scatris.canRotateCurrElementRight) Scatris.rotateCurrElementRight()
                case Keyboard.KEY_SPACE =>
                    Scatris.dropCurrElementDown()
                case Keyboard.KEY_ESCAPE =>
                    GameController.exit()
                case Keyboard.KEY_R =>
                    if (Keyboard.getEventKeyState) StateManager.currentState = ResetGameState
                case Keyboard.KEY_P =>
                    Scatris.togglePause()
                case _ => {}
            }
        }
    }

    private def fallDownFast() {
        if (TimerManager(Scatris.TICK_TIMER).time >= Config.f(FAST_FALLING_TICK_TIME)) {
            Scatris.tick()
            TimerManager(Scatris.TICK_TIMER).restart()
        }
    }

    private def moveCurrElementLeftByTimer() {
        if (TimerManager(Scatris.LEFT_MOVE_TIMER).time >=
                Config.i(LEFT_MOVE_TIME_BOUND)) {
            Scatris.moveCurrElementLeft()
            TimerManager(Scatris.LEFT_MOVE_TIMER).restart()
        }
    }

    private def moveCurrElementRightByTimer() {
        if (TimerManager(Scatris.RIGHT_MOVE_TIMER).time >=
                Config.i(RIGHT_MOVE_TIME_BOUND)) {
            Scatris.moveCurrElementRight()
            TimerManager(Scatris.RIGHT_MOVE_TIMER).restart()
        }
    }
}
