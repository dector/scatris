package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

class GameField(val width: Int, val height: Int) {
    private val elements = Array.ofDim[Boolean](width, height)

    private def isPointCorrect(x: Int, y: Int): Boolean = {
        0 <= x && x < width && 0 <= y && y < height
    }

    def apply(x: Int, y: Int): Boolean = {
        if (isPointCorrect(x, y)) elements(x)(y)
        else if (0 <= x && x < width) false
        else true
    }

    def update(x: Int, y: Int, value: Boolean) {
        if (isPointCorrect(x, y)) elements(x)(y) = value
    }

    def append(el: Element, elX: Int, elY: Int) {
        for ((x, y) <- el.blocks) this(elX + x, elY + y) = true
    }

    def clear() {
        for (i <- 0 until width)
            for (j <- 0 until height)
                elements(i)(j) = false;
    }
}
