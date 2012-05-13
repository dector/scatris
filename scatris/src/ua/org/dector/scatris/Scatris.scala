package ua.org.dector.scatris

import ua.org.dector.lwsgl.LWSGLApp
import ua.org.dector.lwsgl.graphics._
import org.newdawn.slick.Color
import collection.mutable.Queue
import org.lwjgl.input.Keyboard

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

    private val field = new GameField(FIELD_X_BLOCKS_NUM, FIELD_Y_BLOCKS_NUM)

    private var currElement = getNextFallingElement
    private var currElementX = getStartFallingX
    private var currElementY = getStartFallingY

    // Mock
    for (i <- 0 until field.width)
        field(i, 0) = true
    for (i <- 0 until (field.width, 2))
        field(i, 1) = true
    for (i <- 0 until (field.width, 3))
        field(i, 2) = true

    // Game logic procedures

    private def getStartFallingX: Int = (FIELD_X_BLOCKS_NUM / 2).toInt - 1
    private def getStartFallingY: Int = FIELD_Y_BLOCKS_NUM - 1

    private def getNextFallingElement: Element = {
        new Stick
    }

    private def generateNextFallingElement() {
        currElement = getNextFallingElement
        currElementX = getStartFallingX
        currElementY = getStartFallingY
    }

    private def canMoveCurrElementDown: Boolean =
        ! field(currElementX, currElementY - 1)

    private def canMoveCurrElementLeft: Boolean =
        ! field(currElementX - 1, currElementY)

    private def canMoveCurrElementRight: Boolean =
        ! field(currElementX + 1, currElementY + currElement.width - 1)

    private def moveCurrElementDown() {currElementY -= 1}
    private def moveCurrElementLeft() {currElementX -= 1}
    private def moveCurrElementRight() {currElementX += 1}

    private def tick() {
        if (currElement != null && canMoveCurrElementDown) {
            moveCurrElementDown()
        } else {
            field.append(currElement, currElementX, currElementY)
            generateNextFallingElement()
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

    override def detectInput() {
        if (Keyboard isKeyDown Keyboard.KEY_SPACE) tick()
        if ((Keyboard isKeyDown Keyboard.KEY_LEFT)
                && canMoveCurrElementLeft) moveCurrElementLeft()
        if ((Keyboard isKeyDown Keyboard.KEY_RIGHT)
                && canMoveCurrElementRight) moveCurrElementRight()
    }
}