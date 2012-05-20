package ua.org.dector.lwsge

import console.LWSGEConsole
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import org.newdawn.slick.Color

import LWSGEConstants._
import graphics._
import common.Config
import time.TimerManager
import java.awt.{Graphics, Canvas}

/**
 * Root application class. Extend it to create new game
 *
 * @author dector (dector9@gmail.com)
 */

abstract class LWSGEApp(val name: String) {
    init()

    var displayParent: Canvas = null

    // Private vals
    val title = name
    val clearColor = Color.black

    // Repeat main loop while !done
    private var done = false

    // FPS
    var _fps = 0
    def fps = _fps
    private def fps_= (fpsValue: Int) {_fps = fpsValue}

    private val FPS_TIMER_ID = "FPS Timer"

    // Methods

    def getDisplayMode: DisplayMode = {
        new DisplayMode(Config.i(DISPLAY_WIDTH),
            Config.i(DISPLAY_HEIGHT))
    }

    def initDisplay() {
        Display.setDisplayMode(getDisplayMode)
        Display.setTitle(title)
        if (displayParent != null)
            Display.setParent(displayParent)
        Display.create()
    }

    def initOGL() {
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LIGHTING)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0, Config.i(DISPLAY_WIDTH),
            0, Config.i(DISPLAY_HEIGHT), -1, 1)
        glMatrixMode(GL_MODELVIEW)

        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
    }

    def loadResources() {}

    def updateDisplay() {
        Display.update()
        Display.sync(Config.i(DISPLAY_SYNC_RATE))
    }

    def exitHook() {
        Display.destroy()
        System.exit(0)
    }

    def clear() {
        glClear(GL_COLOR_BUFFER_BIT)
    }

    def checkInput() {}
    def preRenderCount() {}
    def render() {}
    def renderText() {}

    private def systemCount() {
        if (Config.bool(FPS_DRAW)) {
            val timePassed = TimerManager(FPS_TIMER_ID).time
            TimerManager(FPS_TIMER_ID).restart()
            fps = (1000 / timePassed).toInt
        }

        if (Config.bool(CONSOLE_ENABLED) && Config.bool(CONSOLE_MOVING)) {
            if (Config.bool(CONSOLE_ANIMATION)) {
                val animTimer = LWSGEConsole.animationTimer

                var timePart = animTimer.time.toFloat / Config.i(CONSOLE_ANIMATION_TIME)
                if (timePart >= 1) {
                    timePart = 1

                    Config(CONSOLE_MOVING) = false
                    Config(CONSOLE_OPENED) = Config.bool(CONSOLE_OPENING)
                    animTimer.stop()
                }

                Config(CONSOLE_ANIMATION_PART) =
                        if (Config.bool(CONSOLE_OPENING)) timePart else 1 - timePart
            } else {
                Config(CONSOLE_MOVING) = false
                Config(CONSOLE_OPENED) = Config.bool(CONSOLE_OPENING)

                Config(CONSOLE_ANIMATION_PART) =
                        if (Config.bool(CONSOLE_OPENING)) 1f
                        else 0f
            }
        }
    }

    private def systemDraw() {
        if (Config.bool(CONSOLE_OPENED) || Config.bool(CONSOLE_MOVING))
            LWSGEConsole.render()

        if (Config.bool(FPS_DRAW)) {
            beginTextDrawing()
            drawText(Config.i(FPS_DRAW_X), Config.i(FPS_DRAW_Y),
                "FPS: " + fps.toString, Color.white)
            endTextDrawing()
        }
    }

    private def systemLoad() {
        Config(FPS_DRAW_Y) = Config.i(DISPLAY_HEIGHT) -
                GraphicsToolkit.MEDIUM_FONT.getLineHeight - 10
        TimerManager.createTimer(FPS_TIMER_ID).start()

        Config(CONSOLE_LINES_NUM)           = ((Config.i(CONSOLE_HEIGHT) -
                Config.i(CONSOLE_INPUT_HEIGHT)).toFloat / (
                GraphicsToolkit.CONSOLE_FONT.getLineHeight +
                        Config.i(CONSOLE_LINES_PADDING))).toInt
    }

    private def init() {
        // Load config
        Config(FPS_DRAW)            = false
        Config(DISPLAY_WIDTH)       = 640
        Config(DISPLAY_HEIGHT)      = 480
        Config(DISPLAY_SYNC_RATE)   = 60
        Config(FPS_DRAW_X)          = 10
        Config(FPS_DRAW_Y)          = 0

        Config(CONSOLE_ENABLED)     = true
        Config(CONSOLE_ANIMATION)   = true
        Config(CONSOLE_ANIMATION_TIME)  = 300
        Config(CONSOLE_ANIMATION_PART)  = 1f

        Config(CONSOLE_OPENED)      = false
        Config(CONSOLE_OPENING)     = false
        Config(CONSOLE_MOVING)      = false
        Config(CONSOLE_WIDTH)       = Config.i(DISPLAY_WIDTH)
        Config(CONSOLE_HEIGHT)      = (Config.i(DISPLAY_HEIGHT) / 2).toInt

        Config(CONSOLE_PADDING_LEFT)        = 10
        Config(CONSOLE_PADDING_RIGHT)       = 10
        Config(CONSOLE_PADDING_TOP)         = 10
        Config(CONSOLE_PADDING_BOTTOM)      = 10

        Config(CONSOLE_LINES_PADDING)       = 3

        Config(CONSOLE_INPUT_WIDTH)         = Config.i(CONSOLE_WIDTH) -
                2 * Config.i(CONSOLE_PADDING_LEFT)
        Config(CONSOLE_INPUT_HEIGHT)        = 25

        Config(CONSOLE_INPUT_CURSOR)        = "|"
        Config(CONSOLE_RETURN_END)          = true

        Config(CONSOLE_DRAW_COLOR)  = Color.white
        Config(CONSOLE_BACK_COLOR)  = {
            val color = new Color(Color.black)
            color.a = 0.7f
            color
        }

        GameController.setApp(this)
    }

    /**
     * Main loop
     */
    def execute() {
        initDisplay()
        initOGL()
        systemLoad()
        loadResources()

        while (!(done || Display.isCloseRequested)) {
            clear()
            checkInput()
            preRenderCount()
            systemCount()
            render()
            systemDraw()
            updateDisplay()
        }

        exitHook()
    }

    def main(args: Array[String]) {
        execute()
    }

    def exit() {
        done = true
    }

    def openConsole() {
        Config(CONSOLE_OPENING) = true
        Config(CONSOLE_MOVING) = true
        if (Config.bool(CONSOLE_ANIMATION)) LWSGEConsole.animationTimer.start()
    }

    def closeConsole() {
        Config(CONSOLE_OPENING) = false
        Config(CONSOLE_MOVING) = true
        if (Config.bool(CONSOLE_ANIMATION)) LWSGEConsole.animationTimer.start()
    }
}
