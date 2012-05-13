package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

class GameField(val width: Int, val height: Int) {
    val elements = Array.ofDim[Boolean](width, height)

    def append(el: Element) {
        println("Implement me")
    }
}
