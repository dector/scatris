package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.GameController
import ua.org.dector.lwsge.LWSGEConstants._
import ua.org.dector.lwsge.state.{StateManager, GameState}
import ua.org.dector.scatris.{Drawer, GameCore}
import ua.org.dector.lwsge.common.Config
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.console.LWSGEConsole

/**
 * @author dector (dector9@gmail.com)
 */

object RunningGameState extends GameState("Running") {
    def added() {
        TimerManager.createTimer(GameCore.TICK_TIMER)
        TimerManager.createTimer(GameCore.LEFT_MOVE_TIMER)
        TimerManager.createTimer(GameCore.RIGHT_MOVE_TIMER)
    }

    def activate() {
        TimerManager(GameCore.TICK_TIMER).start()
        TimerManager(GameCore.LEFT_MOVE_TIMER).start()
        TimerManager(GameCore.RIGHT_MOVE_TIMER).start()
    }


    def preRenderCount() {
        if (TimerManager(GameCore.TICK_TIMER).time >= GameCore.tickTimeBound) {
            TimerManager(GameCore.TICK_TIMER).restart()
            GameCore.tick()
        }
    }

    def render() {
        Drawer.drawGame()
    }

    def checkInput() {
        if (Config.bool(CONSOLE_OPENED)) {
            LWSGEConsole.checkInput()
        } else {
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                GameCore.fallDownFast()

            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
                GameCore.tryMoveCurrElementLeftByTimer()
            else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
                GameCore.tryMoveCurrElementRightByTimer()

            while (Keyboard.next && Keyboard.getEventKeyState) {
                Keyboard.getEventKey match {
                    case Keyboard.KEY_UP =>
                        GameCore.tryRotateCurrElementRight()
                    case Keyboard.KEY_SPACE =>
                        GameCore.dropCurrElementDown()
                    case Keyboard.KEY_ESCAPE =>
                        GameController.exit()
                    case Keyboard.KEY_R =>
                        if (Keyboard.getEventKeyState)
                            StateManager.currentState = ResetGameState
                    case Keyboard.KEY_P =>
                        StateManager.currentState = PausedGameState
                    case Keyboard.KEY_F2 =>
                        Config(DRAW_PHANTOM) = !Config.bool(DRAW_PHANTOM)
                    case Keyboard.KEY_GRAVE =>
                        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                            GameController.trySwitchConsole()
                    case _ => {}
                }
            }
        }
    }


}
