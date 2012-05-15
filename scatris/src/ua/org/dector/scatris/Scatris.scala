package ua.org.dector.scatris

import ua.org.dector.lwsgl.LWSGLApp
import ua.org.dector.lwsgl.graphics._
import org.newdawn.slick.Color
import org.lwjgl.input.Keyboard
import util.Random
import collection.mutable.ArrayBuffer

/**
 * @author dector (dector9@gmail.com)
 */

object Scatris extends LWSGLApp("Scatris") {
    private val FIELD_X_BLOCKS_NUM = 10
    private val FIELD_Y_BLOCKS_NUM = 20

    private val BLOCK_COLOR = Color.lightGray

    private val BLOCK_MARGING = 2
    private val BIG_BLOCK_SIZE = 20
    private val SMALL_BLOCK_SIZE = 10
    private val BLOCKS_DIFF_PLACE = ((BIG_BLOCK_SIZE - SMALL_BLOCK_SIZE) / 2).toInt

    private val FIELD_X_PADDING = 5
    private val FIELD_Y_PADDING = 10
    private val FIELD_WIDTH = FIELD_X_BLOCKS_NUM * (BIG_BLOCK_SIZE + BLOCK_MARGING) +
                                2*FIELD_X_PADDING
    private val FIELD_HEIGHT = FIELD_Y_BLOCKS_NUM * (BIG_BLOCK_SIZE + BLOCK_MARGING) +
                                2*FIELD_Y_PADDING

    private val FIELD_X_START = ((displayWidth - FIELD_WIDTH)/2).toInt
    private val FIELD_Y_START = ((displayHeight - FIELD_HEIGHT)/2).toInt

    private val STARTING_TICK_TIME = 500

    private val field = new GameField(FIELD_X_BLOCKS_NUM, FIELD_Y_BLOCKS_NUM)

    private val elementsPool =
        Array(new Stick, new Block, new RZip, new LZip, new G, new Seven, new T)
    private var currElement = getNextFallingElement
    private var currElementX = getStartFallingX
    private var currElementY = getStartFallingY
    
    private var lastTime = getCurrentTime
    private var tickTime = STARTING_TICK_TIME

    private def updateLastTime() {lastTime = getCurrentTime}
    
    private def resetGame() {
        field.clear()
        generateNextFallingElement()
        updateLastTime()
        tickTime = STARTING_TICK_TIME
    }

    // Game logic procedures

    private def getStartFallingX: Int = (FIELD_X_BLOCKS_NUM / 2).toInt - 1
    private def getStartFallingY: Int = FIELD_Y_BLOCKS_NUM - 1

    private def getNextFallingElement: Element = {
        elementsPool(Random nextInt elementsPool.size)
    }

    private def generateNextFallingElement() {
        currElement = getNextFallingElement
        currElementX = getStartFallingX
        currElementY = getStartFallingY
    }

    private def canMoveCurrElementDown: Boolean = {
        var canMove = true
        var i = 0

        val bottomBlocksY = currElement.bottomBlocksY

        while (canMove && i < currElement.width) {
            if (field(currElementX + i, currElementY + bottomBlocksY(i) - 1)) canMove = false
            i += 1
        }

        canMove
    }

    private def canMoveCurrElementLeft: Boolean = {
        var canMove = true
        var i = 0

        val leftBlocksX = currElement.leftBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + leftBlocksX(i) - 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    private def canMoveCurrElementRight: Boolean = {
        var canMove = true
        var i = 0

        val rightBlocksX = currElement.rightBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + rightBlocksX(i) + 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    private def canRotateCurrElementRight: Boolean = {
        var canRotateRight = true

        currElement.setNextRotation()
        for ((x, y) <- currElement.blocks)
            if (field(currElementX + x, currElementY + y))
                canRotateRight = false
        currElement.setPreviousRotation()

        canRotateRight
    }

    private def rotateCurrElementLeft() {
        currElement.setPreviousRotation()
    }

    private def rotateCurrElementRight() {
        currElement.setNextRotation()
    }

    private def moveCurrElementDown() {currElementY -= 1}
    private def moveCurrElementLeft() {currElementX -= 1}
    private def moveCurrElementRight() {currElementX += 1}

    private def checkAndDeleteFullLines() {
        val linesToDrop = new ArrayBuffer[Int]

        var x = 0
        var isFull = true

        for (line <- 0 until field.height) {
            isFull = true
            x = 0

            while (isFull && x < field.width) {
                if (! field(x, line)) isFull = false
                else x += 1
            }

            if (isFull) linesToDrop += line
        }

        for (line <- linesToDrop) {
            for (x <- 0 until field.width) {
                field(x, line) = false
            }
        }

        println("Drop Lines: " + linesToDrop)

        if (! linesToDrop.isEmpty) {
            val linesToMove = new ArrayBuffer[(Int, Int)]

            var isEmpty = true
            var dropsCount = 0

            for (line <- 0 until field.height) {
                isEmpty = true
                x = 0

                while (isEmpty && x < field.width) {
                    if (field(x, line)) isEmpty = false
                    else x += 1
                }

                if (isEmpty) {
                    dropsCount += 1
                    linesToMove += ((line, dropsCount))
                }
            }

            println("Move Lines: " + linesToMove)

            var i = 0
            var lastMove = linesToMove(i)
            var nextMove = linesToMove(i+1)
            i += 1

            for (line <- 0 until field.height) {
                if (line > lastMove._1) {
                    for (x <- 0 until field.width) {
                        field(x, line - lastMove._2) = field(x, line)
                        field(x, line) = false
                    }
                }
                if (line == nextMove._1) {
                    lastMove = nextMove
                    i += 1

                    if (i < linesToMove.size) {
                        nextMove = linesToMove(i)
                    } else {
                        nextMove = (field.height, 0)
                    }
                }
            }
        }
    }

    private def tick() {
        if (currElement != null && canMoveCurrElementDown) {
            moveCurrElementDown()
        } else {
            field.append(currElement, currElementX, currElementY)
            generateNextFallingElement()

            checkAndDeleteFullLines()
        }
    }

    // Draw procedures

    private def drawFieldBorder() {
        drawRect(FIELD_X_START, FIELD_Y_START, FIELD_WIDTH, FIELD_HEIGHT, BLOCK_COLOR)
    }

    private def drawBlock(xNum: Int, yNum: Int) {
        val x = xNum * (BIG_BLOCK_SIZE + BLOCK_MARGING) + FIELD_X_START + FIELD_X_PADDING
        val y = yNum * (BIG_BLOCK_SIZE + BLOCK_MARGING) + FIELD_Y_START + FIELD_Y_PADDING

        drawRect(x, y, BIG_BLOCK_SIZE, BIG_BLOCK_SIZE, BLOCK_COLOR)
        fillRect(x + BLOCKS_DIFF_PLACE, y + BLOCKS_DIFF_PLACE,
            SMALL_BLOCK_SIZE, SMALL_BLOCK_SIZE, BLOCK_COLOR)
    }

    override def preRenderCount {
        if (getCurrentTime - lastTime >= tickTime) {
            updateLastTime()
            tick()
        }
    }

    override def render {
        // Draw field
        drawFieldBorder()

        for (x <- 0 until field.width) {
            for (y <- 0 until field.height) {
                if (field(x, y))
                    drawBlock(x, y)
            }
        }

        // Draw falling element
        if (currElement != null) {
            var elX, elY = 0
            for ((x, y) <- currElement.blocks) {
                elX = currElementX + x
                elY = currElementY + y

                if (0 <= elX && elX < FIELD_X_BLOCKS_NUM
                        && 0 <= elY && elY < FIELD_Y_BLOCKS_NUM) drawBlock(elX, elY)
            }
        }
    }

    // Input procedures

    override def detectInput {
        while (Keyboard.next && Keyboard.getEventKeyState) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_DOWN =>
                   tick()
                case Keyboard.KEY_LEFT =>
                    if (Keyboard.getEventKeyState&& canMoveCurrElementLeft)
                        moveCurrElementLeft()
                case Keyboard.KEY_RIGHT =>
                    if (Keyboard.getEventKeyState && canMoveCurrElementRight)
                        moveCurrElementRight()
                case Keyboard.KEY_R =>
                    if (Keyboard.getEventKeyState) resetGame()
                case Keyboard.KEY_UP =>
                    if (canRotateCurrElementRight) rotateCurrElementRight()
                case _ => {}
            }
        }
    }
}