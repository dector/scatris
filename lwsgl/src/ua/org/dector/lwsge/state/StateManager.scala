package ua.org.dector.lwsge.state

import collection.mutable.HashMap

/**
 * @author dector (dector9@gmail.com)
 */

object StateManager {
    var _currentState: GameState = null

    def currentState = _currentState
    def currentState_= (newState: GameState) {
        if (newState != null) {
            _currentState = newState

            println("New game state: " + currentState)

            currentState.activate()
        }
    }

    private val states = HashMap.empty[String, (GameState, GameState)]

    def addState(gameState: GameState) {
        addState(gameState, null)
    }

    def addState(gameState: GameState, nextGameState: GameState) {
        states(gameState.name) = (gameState, nextGameState)
    }

    def nextState() {
        val nextState = states(currentState.name)._2
        if (nextState != null)
            currentState = nextState
    }
}
