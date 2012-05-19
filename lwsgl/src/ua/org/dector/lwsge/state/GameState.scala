package ua.org.dector.lwsge.state

/**
 * @author dector (dector9@gmail.com)
 */

abstract class GameState(val name: String) {
    def activate()
    def render()
    def checkInput()
}
