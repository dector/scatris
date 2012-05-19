package ua.org.dector.scatris

import ua.org.dector.lwsge.graphics._
import ua.org.dector.lwsge.common.Config
import org.newdawn.slick.Color
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.GraphicsToolkit

/**
 * @author dector (dector9@gmail.com)
 */

object Drawer {
    private def drawFieldBorder() {
        drawRect(Config.i(FIELD_X_START), Config.i(FIELD_Y_START),
            Config.i(FIELD_WIDTH), Config.i(FIELD_HEIGHT),
            Config(BLOCK_COLOR).asInstanceOf[Color])
    }

    private def drawBlock(xNum: Int, yNum: Int) {
        drawBlock(xNum, yNum, false)
    }

    private def drawBlock(xNum: Int, yNum: Int, phantom: Boolean) {
        val color =
            if (phantom)
                Config(PHANTOM_BLOCK_COLOR).asInstanceOf[Color]
            else
                Config(BLOCK_COLOR).asInstanceOf[Color]

        drawBlock(xNum, yNum, color)
    }

    private def drawBlock(xNum: Int, yNum: Int, color: Color) {
        val x = xNum * (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                Config.i(FIELD_X_START) + Config.i(FIELD_X_PADDING)
        val y = yNum * (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                Config.i(FIELD_Y_START) + Config.i(FIELD_Y_PADDING)

        drawRect(x, y, Config.i(BIG_BLOCK_SIZE), Config.i(BIG_BLOCK_SIZE), color)
        fillRect(x + Config.i(BLOCKS_DIFF_PLACE), y + Config.i(BLOCKS_DIFF_PLACE),
            Config.i(SMALL_BLOCK_SIZE), Config.i(SMALL_BLOCK_SIZE), color)
    }

    // ---------------------------------------------------------------------------

    private def drawField() {
        drawFieldBorder()

        for (x <- 0 until GameCore.field.width) {
            for (y <- 0 until GameCore.field.height) {
                if (GameCore.field(x, y))
                    drawBlock(x, y)
            }
        }
    }

    private def drawStat() {
        val statX = Config.i(FIELD_X_START) +
                Config.i(NEXT_ELEMENT_SHOW_X_IN_BLOCKS) * (Config.i(BIG_BLOCK_SIZE) +
                        Config.i(BLOCK_MARGING))
        val statY = Config.i(FIELD_Y_START) + Config.i(NEXT_ELEMENT_SHOW_Y_IN_BLOCKS) *
                (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) -
                4 * GraphicsToolkit.MEDIUM_FONT.getLineHeight

        val statText2 = "Score: " + GameCore.score
        val statText3 = "Lines: " + GameCore.lines

        beginTextDrawing()
        drawText(statX, statY + (1.5f *
                GraphicsToolkit.MEDIUM_FONT.getLineHeight).toInt, statText2)
        drawText(statX, statY, statText3)
        endTextDrawing()
    }

    private def drawFallingElement() {
        drawElement(GameCore.currElement, GameCore.currElementX, GameCore.currElementY)
    }

    private def drawElement(el: Element, elX: Int, elY: Int) {
        drawElement(el, elX, elY, false)
    }

    private def drawElement(el: Element, elX: Int, elY: Int, phantom: Boolean) {
        if (el != null) {
            var drawElX, drawElY = 0
            for ((x, y) <- el.blocks) {
                drawElX = elX + x
                drawElY = elY + y

                if (0 <= drawElX && drawElX < Config.i(FIELD_X_BLOCKS_NUM)
                        && 0 <= drawElY && drawElY < Config.i(FIELD_Y_BLOCKS_NUM))
                    drawBlock(drawElX, drawElY, phantom)
            }
        }
    }

    private def drawNextElement() {
        var elX, elY = 0
        for ((x, y) <- GameCore.nextElement.blocks) {
            elX = Config.i(NEXT_ELEMENT_SHOW_X_IN_BLOCKS) + x
            elY = Config.i(NEXT_ELEMENT_SHOW_Y_IN_BLOCKS) + y

            drawBlock(elX, elY)
        }
    }

    private def drawPhantom() {
        if (Config.bool(DRAW_PHANTOM)) {
            drawElement(GameCore.currElement, GameCore.currElementX, GameCore.phantomY, true)
        }
    }

    // ---------------------------------------------------------------------------

    def drawGame() {
        drawField()
        drawPhantom()
        drawFallingElement()
        drawNextElement()
        drawStat()
    }
}
