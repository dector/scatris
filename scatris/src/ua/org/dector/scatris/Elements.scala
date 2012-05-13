package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

abstract class Element(val width: Int, val height: Int) {
    def blocks: Array[(Int, Int)]
}

class Stick extends Element(1, 4) {
    def blocks = Array((0, 0), (0, 1), (0, 2), (0, 3))
}
