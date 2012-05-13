package ua.org.dector.scatris

import ua.org.dector.lwsgl.LWSGLApp
import ua.org.dector.lwsgl.graphics._
import org.newdawn.slick.Color

/**
 * @author dector (dector9@gmail.com)
 */

object Scatris extends LWSGLApp("Scatris") {
    private val FIELD_WIDTH = 20
    private val FIELD_HEIGHT = 10

    private val BLOCK_COLOR = Color.lightGray

    private val BLOCK_PADDING = 2
    private val BIG_BLOCK_SIZE = 20
    private val SMALL_BLOCK_SIZE = 10
    private val BLOCKS_DIFF_PLACE = ((BIG_BLOCK_SIZE - SMALL_BLOCK_SIZE) / 2).toInt

    private val field = new GameField(FIELD_WIDTH, FIELD_HEIGHT)

    // Mock
    for (i <- 0 until field.width)
        field.elements(i)(0) = true
    for (i <- 0 until (field.width, 2))
        field.elements(i)(1) = true
    for (i <- 0 until (field.width, 3))
        field.elements(i)(1) = true

    private def drawBlock(xNum: Int, yNum: Int) {
        val x = xNum * (BIG_BLOCK_SIZE + BLOCK_PADDING) + BLOCK_PADDING
        val y = yNum * (BIG_BLOCK_SIZE + BLOCK_PADDING) + BLOCK_PADDING

        drawRect(x, y, BIG_BLOCK_SIZE, BIG_BLOCK_SIZE, BLOCK_COLOR)
        fillRect(x + BLOCKS_DIFF_PLACE, y + BLOCKS_DIFF_PLACE,
            SMALL_BLOCK_SIZE, SMALL_BLOCK_SIZE, BLOCK_COLOR)
    }

    override def render {
        for (x <- 0 until field.height) {
            for (y <- 0 until field.width) {
                drawBlock(x, y)
            }
        }
    }
}
