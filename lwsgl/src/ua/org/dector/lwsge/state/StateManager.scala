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

            currentState.activate()
        }
    }

    //                                 name     state       nextState
    private val states = HashMap.empty[String, (GameState, GameState)]

    def addState(gameState: GameState) {
        addState(gameState, null)
    }

    def addState(gameState: GameState, nextGameState: GameState) {
        states(gameState.name) = (gameState, nextGameState)
        gameState.added()
    }

    def setState(stateName: String) {
        if (states.contains(stateName)) {
            currentState = states(stateName)._1
        }
    }

    def removeState(gameState: GameState) {
        states remove gameState.name
    }

    def nextState() {
        val nextState = states(currentState.name)._2
        if (nextState != null)
            currentState = nextState
    }
}
