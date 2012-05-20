package ua.org.dector.lwsge.console

import ua.org.dector.lwsge.graphics._
import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.LWSGEConstants._
import org.newdawn.slick.Color
import ua.org.dector.lwsge.time.TimerManager
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.{GraphicsToolkit, GameController}

/**
 * @author dector (dector9@gmail.com)
 */

object LWSGEConsole {
    private val COMMAND_EXIT = "exit"

    private val CONSOLE_ANIMATION_TIMER = "Console Animation Timer"
    val animationTimer = TimerManager.createTimer(CONSOLE_ANIMATION_TIMER)

    private val inputString = new StringBuilder

    // TEMP
    private val data = new StringBuilder

    def checkInput() {
        while (Keyboard.next && Keyboard.getEventKeyState) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_GRAVE =>
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                        GameController.trySwitchConsole()
                case Keyboard.KEY_RETURN =>
                    flushInput()
                case Keyboard.KEY_BACK =>
                    removeLast()
                case _ => {
                    val char = Keyboard.getEventCharacter
                    addToInput(char)
                }
            }
        }
    }

    def render() {
        val consoleHeight = (Config.f(CONSOLE_ANIMATION_PART)
                * Config.i(CONSOLE_HEIGHT) - 1).toInt

        val startX = 1
        val startY = Config.i(DISPLAY_HEIGHT) - consoleHeight - 1

        val textX = startX + Config.i(CONSOLE_PADDING_LEFT)
        val textY = startY + Config.i(CONSOLE_PADDING_BOTTOM)

        val lineHeight = GraphicsToolkit.CONSOLE_FONT.getLineHeight
        val linePadding = Config.i(CONSOLE_LINES_PADDING)

        val drawColor = Config(CONSOLE_DRAW_COLOR).asInstanceOf[Color]
        val backColor = Config(CONSOLE_BACK_COLOR).asInstanceOf[Color]

        // Draw console

        fillRect(startX, startY, Config.i(CONSOLE_WIDTH) - 1, consoleHeight, backColor)
        drawRect(startX, startY, Config.i(CONSOLE_WIDTH) - 1, consoleHeight, drawColor)

        // Draw input

        var lineNum = 1
        beginTextDrawing()
            // Draw all lines!
            drawText(textX, textY + lineNum * (lineHeight + linePadding), data.toString,
                font = GraphicsToolkit.CONSOLE_FONT)

            drawText(textX, textY, inputString.toString + Config.s(CONSOLE_INPUT_CURSOR),
                font = GraphicsToolkit.CONSOLE_FONT)
        endTextDrawing()
    }

    def addToInput(c: Char) {
        inputString append c
    }

    def removeLast() {
        if (inputString.length > 0) {
            inputString.deleteCharAt(inputString.length - 1)
        }
    }

    def flushInput() {
        val str = inputString.toString

        addString(str)

        checkCommand(str)
        inputString.clear()
    }

    private def addString(s: String) {
        // TEMP
        data.clear()
        data append "User> " append s
    }

    private def checkCommand(inS: String) {
        val s = inS.trim()

        if (s.startsWith("\\")) {
            val firstSpaceIndex = s.indexOf(" ")
            val commandArgsIndex =
                if (firstSpaceIndex > 0)
                    firstSpaceIndex
                else
                    s.length()

            s.substring(1, commandArgsIndex) match {
                case COMMAND_EXIT => { addString("Exiting ..."); GameController.exit() }
                case _ => { addString("Unknown command \"" + s + "\"") }
            }
        }

    }
}
