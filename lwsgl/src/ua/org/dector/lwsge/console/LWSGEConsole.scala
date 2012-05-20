package ua.org.dector.lwsge.console

import ua.org.dector.lwsge.graphics._
import ua.org.dector.lwsge.LWSGEConstants._
import org.newdawn.slick.Color
import ua.org.dector.lwsge.time.TimerManager
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.{GraphicsToolkit, GameController}
import ua.org.dector.lwsge.state.StateManager
import collection.mutable.ArrayBuffer
import ua.org.dector.lwsge.common.{Reflectioner, Config}

/**
 * @author dector (dector9@gmail.com)
 */

object LWSGEConsole {
    private val COMMAND_EXIT    = "exit"
//    private val COMMAND_PAUSE   = "pause"
    private val COMMAND_SKIP    = "skip"
    private val COMMAND_SET     = "set"
    private val COMMAND_GET     = "get"

    private val CONSOLE_ANIMATION_TIMER = "Console Animation Timer"
    val animationTimer = TimerManager.createTimer(CONSOLE_ANIMATION_TIMER)

    private val linesStorage = new ArrayBuffer[String]
    private val commandsStorage = new ArrayBuffer[String]
    private var currCommandIndex = commandsStorage.length

    private val inputString = new StringBuilder

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
                case Keyboard.KEY_UP =>
                    if (0 < currCommandIndex ) currCommandIndex -= 1
                case Keyboard.KEY_DOWN =>
                    if (currCommandIndex < commandsStorage.size) currCommandIndex += 1

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
        val linesCount = linesStorage.size
        beginTextDrawing()
            // Draw all lines
            while (lineNum <= Config.i(CONSOLE_LINES_NUM) &&
                    lineNum <= linesCount) {
                drawText(textX, textY + lineNum * (lineHeight + linePadding),
                    linesStorage(linesCount - lineNum),
                    font = GraphicsToolkit.CONSOLE_FONT)
                lineNum += 1
            }

            drawText(textX, textY, inputtedString.toString + Config.s(CONSOLE_INPUT_CURSOR),
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
        val str = inputtedString

        inputString.clear()
        appendInput(str)
    }

    def println(s: String) {
        addLine(s)
    }

    private def inputtedString: String = {
        if (currCommandIndex == commandsStorage.size)
            inputString.toString
        else
            commandsStorage(currCommandIndex)
    }

    private def addLine(s: String) {
        linesStorage += s
    }

    private def appendInput(s: String) {
        commandsStorage += s
        currCommandIndex = commandsStorage.length

        addLine("> " + s)

        checkCommand(s)
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
                case COMMAND_EXIT => { addLine("Exiting ..."); GameController.exit() }
                    // Make it general in game!!
//                case COMMAND_PAUSE => { StateManager.setState("Paused") }
                case COMMAND_SKIP => { StateManager.nextState() }
                case COMMAND_SET => {
                    try {
                        val args = s.substring(commandArgsIndex, s.length)
                        val splitterIndex = args.indexOf("=")

                        val lArg = args.substring(0, splitterIndex).trim()
                        val rArg = args.substring(splitterIndex + 1, args.length()).trim()

                        Reflectioner.setNewConfigValue(lArg, rArg)
                    } catch {
                        case _: IndexOutOfBoundsException => addLine("Wrong command usage")
                    }
                }
                case COMMAND_GET => {
                    try {
                        val arg = s.substring(commandArgsIndex, s.length).trim()

                        if (Config.contains(arg))
                            addLine(Config(arg).toString)
                        else
                            addLine(arg + " not found")
                    } catch {
                        case _: IndexOutOfBoundsException => addLine("Wrong command usage")
                    }
                }
                case _ => { addLine("Unknown command \"" + s + "\"") }
            }
        }

    }
}
