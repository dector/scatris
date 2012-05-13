package ua.org.dector.scatris

import ua.org.dector.lwsgl.LWSGLApp
import ua.org.dector.lwsgl.graphics._
import org.newdawn.slick.Color

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

    // Mock
    for (i <- 0 until field.width)
        field.elements(i)(0) = true
    for (i <- 0 until (field.width, 2))
        field.elements(i)(1) = true
    for (i <- 0 until (field.width, 3))
        field.elements(i)(2) = true

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
        drawFieldBorder()

        for (i <- 0 until field.width) {
            for (j <- 0 until field.height) {
                if (field.elements(i)(j))
                    drawBlock(i, j)
            }
        }
    }
}
