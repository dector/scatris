package ua.org.dector.lwsge.console

import ua.org.dector.lwsge.graphics._
import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.LWSGEConstants._
import org.newdawn.slick.Color
import ua.org.dector.lwsge.time.TimerManager

/**
 * @author dector (dector9@gmail.com)
 */

object LWSGEConsole {
    private val CONSOLE_ANIMATION_TIMER = "Console Animation Timer"
    val animationTimer = TimerManager.createTimer(CONSOLE_ANIMATION_TIMER)

    def render() {
        val consoleHeight = (Config.f(CONSOLE_ANIMATION_PART)
                * Config.i(CONSOLE_HEIGHT) - 1).toInt

        val startX = 1
        val startY = Config.i(DISPLAY_HEIGHT) - consoleHeight - 1

        val drawColor = Config(CONSOLE_DRAW_COLOR).asInstanceOf[Color]
        val backColor = Config(CONSOLE_BACK_COLOR).asInstanceOf[Color]

        // Draw console

        fillRect(startX, startY, Config.i(CONSOLE_WIDTH) - 1, consoleHeight, backColor)
        drawRect(startX, startY, Config.i(CONSOLE_WIDTH) - 1, consoleHeight, drawColor)

//        val lineY = startY + Config.i(CONSOLE_INPUT_HEIGHT) +
//                2 * Config.i(CONSOLE_INPUT_UP_DOWN_MARGIN) - 1
//        drawLine(startX, lineY, startX + Config.i(CONSOLE_WIDTH), lineY, drawColor)

//        // Draw input
//
//        drawRect(startX + Config.i(CONSOLE_INPUT_SIDE_MARGIN),
//            startY + Config.i(CONSOLE_INPUT_UP_DOWN_MARGIN),
//            Config.i(CONSOLE_INPUT_WIDTH) - 1, Config.i(CONSOLE_INPUT_HEIGHT) - 1,
//            drawColor)
    }
}
