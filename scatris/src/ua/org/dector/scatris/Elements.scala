package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

object ElementRotation extends Enumeration {
    type ElementRotation = Value

    val DEG0, DEG90, DEG180, DEG270 = Value
}

import ElementRotation._

abstract class Element(private val _width: Int, private val _height: Int) {
    var rotation = DEG0

    def width = {
        rotation match {
            case (DEG0 | DEG180) => _width
            case (DEG90 | DEG270) => _height
        }
    }

    def height = {
        rotation match {
            case (DEG0 | DEG180) => _height
            case (DEG90 | DEG270) => _width
        }
    }

    def blocks: Array[(Int, Int)]
    def bottomBlocksY: Array[Int]
    def leftBlocksX: Array[Int]
    def rightBlocksX: Array[Int]

    def setNextRotation() {
        rotation match {
            case DEG0   => rotation = DEG90
            case DEG90  => rotation = DEG180
            case DEG180 => rotation = DEG270
            case DEG270 => rotation = DEG0
        }
    }

    def setPreviousRotation() {
        rotation match {
            case DEG0   => rotation = DEG270
            case DEG90  => rotation = DEG0
            case DEG180 => rotation = DEG90
            case DEG270 => rotation = DEG180
        }
    }

    def apply(x: Int, y: Int): Boolean = blocks.contains((x, y))
}


class Stick extends Element(1, 4) {
    def blocks = {
        rotation match {
            case (DEG0 | DEG180) => Array((0, 0), (0, 1), (0, 2), (0, 3))
            case (DEG90 | DEG270) => Array((0, 0), (1, 0), (2, 0), (3, 0))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case (DEG0 | DEG180) => Array(0)
            case (DEG90 | DEG270) => Array(0, 0, 0, 0)
        }
    }

    def leftBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(0, 0, 0, 0)
            case (DEG90 | DEG270) => Array(0)
        }
    }

    def rightBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(0, 0, 0, 0)
            case (DEG90 | DEG270) => Array(3)
        }
    }
}

class Block extends Element(2, 2) {
    def blocks = Array((0, 0), (1, 0), (0, 1), (1, 1))
    def bottomBlocksY = Array(0, 0)
    def leftBlocksX = Array(0, 0)
    def rightBlocksX = Array(1, 1)
}

class RZip extends Element(2, 3) {
    def blocks = {
        rotation match {
            case (DEG0 | DEG180) => Array((1, 0), (0, 1), (1, 1), (0, 2))
            case (DEG90 | DEG270) => Array((0, 0), (1, 0), (1, 1), (2, 1))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case (DEG0 | DEG180) => Array(1, 0)
            case (DEG90 | DEG270) => Array(0, 0, 1)
        }
    }

    def leftBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(1, 0, 0)
            case (DEG90 | DEG270) => Array(0, 1)
        }
    }

    def rightBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(1, 1, 0)
            case (DEG90 | DEG270) => Array(1, 2)
        }
    }
}

class LZip extends Element(2, 3) {
    def blocks = {
        rotation match {
            case (DEG0 | DEG180) => Array((0, 0), (0, 1), (1, 1), (1, 2))
            case (DEG90 | DEG270) => Array((0, 1), (1, 1), (1, 0), (2, 0))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case (DEG0 | DEG180) => Array(0, 1)
            case (DEG90 | DEG270) => Array(1, 0, 0)
        }
    }

    def leftBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(0, 0, 1)
            case (DEG90 | DEG270) => Array(1, 0)
        }
    }

    def rightBlocksX = {
        rotation match {
            case (DEG0 | DEG180) => Array(0, 1, 1)
            case (DEG90 | DEG270) => Array(2, 1)
        }
    }
}

class G extends Element(2, 3) {
    def blocks = {
        rotation match {
            case DEG0   => Array((0, 0), (0, 1), (0, 2), (1, 2))
            case DEG90  => Array((0, 1), (1, 1), (2, 1), (2, 0))
            case DEG180 => Array((0, 0), (1, 0), (1, 1), (1, 2))
            case DEG270 => Array((0, 1), (1, 0), (2, 0), (0, 0))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case DEG0   => Array(0, 2)
            case DEG90  => Array(1 ,1 ,0)
            case DEG180 => Array(0, 0)
            case DEG270 => Array(0, 0, 0)
        }
    }

    def leftBlocksX = {
        rotation match {
            case DEG0   => Array(0, 0, 0)
            case DEG90  => Array(2, 0)
            case DEG180 => Array(0, 1, 1)
            case DEG270 => Array(0, 0)
        }
    }

    def rightBlocksX = {
        rotation match {
            case DEG0   => Array(0, 0, 1)
            case DEG90  => Array(2, 2)
            case DEG180 => Array(1, 1, 1)
            case DEG270 => Array(2, 0)
        }
    }
}

class Seven extends Element(2, 3) {
    def blocks = {
        rotation match {
            case DEG0   => Array((1, 0), (1, 1), (1, 2), (0, 2))
            case DEG90  => Array((0, 0), (1, 0), (2, 0), (2, 1))
            case DEG180 => Array((1, 0), (0, 0), (0, 1), (0, 2))
            case DEG270 => Array((0, 0), (1, 1), (2, 1), (0, 1))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case DEG0   => Array(2, 0)
            case DEG90  => Array(0 ,0 ,0)
            case DEG180 => Array(0, 0)
            case DEG270 => Array(0, 1, 1)
        }
    }

    def leftBlocksX = {
        rotation match {
            case DEG0   => Array(1, 1, 0)
            case DEG90  => Array(0, 2)
            case DEG180 => Array(0, 0, 0)
            case DEG270 => Array(0, 0)
        }
    }

    def rightBlocksX = {
        rotation match {
            case DEG0   => Array(1, 1, 1)
            case DEG90  => Array(2, 2)
            case DEG180 => Array(1, 0, 0)
            case DEG270 => Array(0, 2)
        }
    }
}

class T extends Element(3, 2) {
    def blocks = {
        rotation match {
            case DEG0   => Array((1, 0), (0, 1), (1, 1), (2, 1))
            case DEG90  => Array((1, 0), (0, 1), (1, 1), (1, 2))
            case DEG180 => Array((0, 0), (1, 0), (2, 0), (1, 1))
            case DEG270 => Array((0, 0), (0, 1), (1, 1), (0, 2))
        }

    }

    def bottomBlocksY = {
        rotation match {
            case DEG0   => Array(1, 0, 1)
            case DEG90  => Array(1 ,0)
            case DEG180 => Array(0, 0, 0)
            case DEG270 => Array(0, 1)
        }
    }

    def leftBlocksX = {
        rotation match {
            case DEG0   => Array(1, 0)
            case DEG90  => Array(1, 0, 1)
            case DEG180 => Array(0, 1)
            case DEG270 => Array(0, 0, 0)
        }
    }

    def rightBlocksX = {
        rotation match {
            case DEG0   => Array(1, 2)
            case DEG90  => Array(1, 1, 1)
            case DEG180 => Array(2, 1)
            case DEG270 => Array(0, 1, 0)
        }
    }
}
