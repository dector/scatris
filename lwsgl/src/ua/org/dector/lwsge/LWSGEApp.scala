package ua.org.dector.lwsge

import console.LWSGEConsole
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import org.newdawn.slick.Color

import LWSGEConstants._
import graphics._
import common.Config
import time.TimerManager
import java.awt.Canvas
import org.lwjgl.input.Keyboard

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

    private var _consoleOpened = false

    def consoleOpened = _consoleOpened
    private def consoleOpened_= (value: Boolean) { _consoleOpened = value }

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

    private def systemDraw() {
        if (Config.bool(DRAW_FPS)) {
            val timePassed = TimerManager(FPS_TIMER_ID).time
            fps = (1000 / timePassed).toInt

            beginTextDrawing()
                drawText(Config.i(DRAW_FPS_X), Config.i(DRAW_FPS_Y),
                    "FPS: " + fps.toString, Color.white)
            endTextDrawing()
        }

        if (Config.bool(CONSOLE_OPENED))
            LWSGEConsole.render()
    }

    private def systemLoad() {
        Config(DRAW_FPS_Y) = Config.i(DISPLAY_HEIGHT) -
                GraphicsToolkit.MEDIUM_FONT.getLineHeight - 10
        TimerManager.createTimer(FPS_TIMER_ID).start()
    }

    private def init() {
        // Load config
        Config(DRAW_FPS)            = false
        Config(DISPLAY_WIDTH)       = 640
        Config(DISPLAY_HEIGHT)      = 480
        Config(DISPLAY_SYNC_RATE)   = 60
        Config(DRAW_FPS_X)          = 10
        Config(DRAW_FPS_Y)          = 0

        Config(CONSOLE_ENABLED)     = true
        Config(CONSOLE_OPENED)      = false
        Config(CONSOLE_WIDTH)       = Config.i(DISPLAY_WIDTH)
        Config(CONSOLE_HEIGHT)      = (Config.i(DISPLAY_HEIGHT) / 2).toInt

        Config(CONSOLE_INPUT_SIDE_MARGIN)       = 10
        Config(CONSOLE_INPUT_UP_DOWN_MARGIN)    = 10
        Config(CONSOLE_INPUT_WIDTH)             = Config.i(CONSOLE_WIDTH) -
                2 * Config.i(CONSOLE_INPUT_SIDE_MARGIN)
        Config(CONSOLE_INPUT_HEIGHT)            = 25

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
        // Which var needed?
        Config(CONSOLE_OPENED) = true
        consoleOpened = true
    }

    def closeConsole() {
        Config(CONSOLE_OPENED) = false
        consoleOpened = false
    }
}
