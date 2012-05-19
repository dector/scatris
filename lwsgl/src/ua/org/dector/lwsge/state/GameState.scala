package ua.org.dector.lwsge.state

/**
 * @author dector (dector9@gmail.com)
 */

abstract class GameState(val name: String) {
    def added()
    def activate()

    def preRenderCount()
    def render()
    def checkInput()

    override def toString = name
}
