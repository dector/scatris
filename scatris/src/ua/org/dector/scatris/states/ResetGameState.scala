package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.state.{StateManager, GameState}
import ua.org.dector.scatris.GameCore

/**
 * @author dector (dector9@gmail.com)
 */

object ResetGameState extends GameState("Reset") {
    def added() {}

    def activate() {
        GameCore.reset()

        TimerManager(GameCore.TICK_TIMER).restart()
        TimerManager(GameCore.LEFT_MOVE_TIMER).restart()
        TimerManager(GameCore.RIGHT_MOVE_TIMER).restart()

        StateManager.nextState()
    }


    def preRenderCount() {}
    def render() {}
    def checkInput() {}
}
