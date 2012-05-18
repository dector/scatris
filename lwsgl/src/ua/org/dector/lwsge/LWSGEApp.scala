package ua.org.dector.lwsge

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import org.newdawn.slick.Color

import LWSGEConstants._
import graphics._
import common.Config
import time.TimerManager

/**
 * Root application class. Extend it to create new game
 *
 * @author dector (dector9@gmail.com)
 */

abstract class LWSGEApp(val name: String) {
    init()

    // Private vals
    val title = name
    val clearColor = Color.black

    // Repeat main loop
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

    def detectInput() {}
    def preRenderCount() {}
    def render() {}
    def renderText() {}

    def drawDebug() {
        if (Config.bool(DRAW_FPS)) {
            val timePassed = TimerManager(FPS_TIMER_ID).time
            fps = (1000 / timePassed).toInt

            beginTextDrawing()
                drawText(Config.i(DRAW_FPS_X), Config.i(DRAW_FPS_Y),
                    "FPS: " + fps.toString, Color.white)
            endTextDrawing()
        }
    }

    private def systemLoad() {
        Config(DRAW_FPS_Y) = Config.i(DISPLAY_HEIGHT) -
                GraphicsToolkit.MEDIUM_FONT.getLineHeight - 10
        TimerManager.createTimer(FPS_TIMER_ID).start()
    }

    private def init() {
        // Load config
        Config(DRAW_FPS)             = false
        Config(DISPLAY_WIDTH)        = 640
        Config(DISPLAY_HEIGHT)       = 480
        Config(DISPLAY_SYNC_RATE)    = 60
        Config(DRAW_FPS_X)           = 10
        Config(DRAW_FPS_Y)           = 0
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
            detectInput()
            preRenderCount()
            drawDebug()
            render()
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
}
