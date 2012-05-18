package ua.org.dector.lwsge

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import org.newdawn.slick.Color

import Constants._
import graphics._
import common.Config
import time.TimerManager

/**
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
        new DisplayMode(Config(CONFIG_DISPLAY_WIDTH).toInt,
            Config(CONFIG_DISPLAY_HEIGHT).toInt)
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
        glOrtho(0, Config(CONFIG_DISPLAY_WIDTH).toInt,
            0, Config(CONFIG_DISPLAY_HEIGHT).toInt, -1, 1)
        glMatrixMode(GL_MODELVIEW)

        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
    }

    def loadResources() {}

    def updateDisplay() {
        Display.update()
        Display.sync(Config(CONFIG_DISPLAY_SYNC_RATE).toInt)
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
        if (Config(CONFIG_DRAW_FPS).toBoolean) {
            val timePassed = TimerManager(FPS_TIMER_ID).time
            fps = (1000 / timePassed).toInt

            beginTextDrawing()
                drawText(Config(CONFIG_DRAW_FPS_X).toInt, Config(CONFIG_DRAW_FPS_Y).toInt,
                    "FPS: " + fps.toString, Color.white)
            endTextDrawing()
        }
    }

    private def systemLoad() {
        Config(CONFIG_DRAW_FPS_Y) = Config(CONFIG_DISPLAY_HEIGHT).toInt -
                GraphicsToolkit.MEDIUM_FONT.getLineHeight - 10
        TimerManager.createTimer(FPS_TIMER_ID).start()
    }

    private def init() {
        // Load config
        Config(CONFIG_DRAW_FPS)             = false
        Config(CONFIG_DISPLAY_WIDTH)        = 640
        Config(CONFIG_DISPLAY_HEIGHT)       = 480
        Config(CONFIG_DISPLAY_SYNC_RATE)    = 60
        Config(CONFIG_DRAW_FPS_X)           = 10
        Config(CONFIG_DRAW_FPS_Y)           = 0
    }

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
