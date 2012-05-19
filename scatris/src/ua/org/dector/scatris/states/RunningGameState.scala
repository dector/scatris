package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.GameController
import ua.org.dector.lwsge.state.{StateManager, GameState}
import ua.org.dector.scatris.{Drawer, Scatris}

/**
 * @author dector (dector9@gmail.com)
 */

object RunningGameState extends GameState("Running") {
    def added() {
        TimerManager.createTimer(Scatris.TICK_TIMER)
        TimerManager.createTimer(Scatris.LEFT_MOVE_TIMER)
        TimerManager.createTimer(Scatris.RIGHT_MOVE_TIMER)
    }

    def activate() {
        TimerManager(Scatris.TICK_TIMER).start()
        TimerManager(Scatris.LEFT_MOVE_TIMER).start()
        TimerManager(Scatris.RIGHT_MOVE_TIMER).start()
    }


    def preRenderCount() {
        if (TimerManager(Scatris.TICK_TIMER).time >= Scatris.tickTimeBound) {
            TimerManager(Scatris.TICK_TIMER).restart()
            Scatris.tick()
        }
    }

    def render() {
        Drawer.drawGame()
    }

    def checkInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            Scatris.fallDownFast()
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Scatris.canMoveCurrElementLeft)
            Scatris.moveCurrElementLeftByTimer()
        else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Scatris.canMoveCurrElementRight)
            Scatris.moveCurrElementRightByTimer()

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
                    StateManager.currentState = PausedGameState
                case _ => {}
            }
        }
    }


}
