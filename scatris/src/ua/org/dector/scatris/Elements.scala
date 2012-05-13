package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

abstract class Element(val width: Int, val height: Int) {
    def blocks: Array[(Int, Int)]
    def bottomBlocksY: Array[Int]
    def leftBlocksX: Array[Int]
    def rightBlocksX: Array[Int]

    def apply(x: Int, y: Int): Boolean = blocks.contains((x, y))
}

class Stick extends Element(1, 4) {
    def blocks = Array((0, 0), (0, 1), (0, 2), (0, 3))
    def bottomBlocksY = Array(0)
    def leftBlocksX = Array(0, 0, 0, 0)
    def rightBlocksX = Array(0, 0, 0, 0)
}

class Block extends Element(2, 2) {
    def blocks = Array((0, 0), (1, 0), (0, 1), (1, 1))
    def bottomBlocksY = Array(0, 0)
    def leftBlocksX = Array(0, 0)
    def rightBlocksX = Array(1, 1)
}

class RZip extends Element(2, 3) {
    def blocks = Array((1, 0), (0, 1), (1, 1), (0, 2))
    def bottomBlocksY = Array(1, 0)
    def leftBlocksX = Array(1, 0, 0)
    def rightBlocksX = Array(1, 1, 0)
}

class LZip extends Element(2, 3) {
    def blocks = Array((0, 0), (0, 1), (1, 1), (1, 2))
    def bottomBlocksY = Array(0, 1)
    def leftBlocksX = Array(0, 0, 1)
    def rightBlocksX = Array(0, 1, 1)
}

class G extends Element(2, 3) {
    def blocks = Array((0, 0), (0, 1), (0, 2), (1, 2))
    def bottomBlocksY = Array(0, 2)
    def leftBlocksX = Array(0, 0, 0)
    def rightBlocksX = Array(0, 0, 1)
}
