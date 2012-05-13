package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

abstract class Element(val width: Int, val height: Int) {
    def blocks: Array[(Int, Int)]
}

class Stick extends Element(1, 4) {
    def blocks = Array((1, 1), (1, 2), (1, 3), (1, 4))
}
