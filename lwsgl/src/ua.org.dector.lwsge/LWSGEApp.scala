package ua.org.dector.lwsge

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import graphics._
import org.newdawn.slick.{Graphics, Color}
import org.newdawn.slick.opengl.renderer.Renderer
import time.TimerManager

/**
 * @author dector (dector9@gmail.com)
 */

abstract class LWSGEApp(val name: String) {
    private val DEFAULT_DISPLAY_WIDTH = 640
    private val DEFAULT_DISPLAY_HEIGHT = 480
    private val DEFAULT_DISPLAY_SYNC_RATE = 60

    val displayWidth = DEFAULT_DISPLAY_WIDTH
    val displayHeight = DEFAULT_DISPLAY_HEIGHT

    val clearColor = Color.black

    val title = name
    val syncRate = DEFAULT_DISPLAY_SYNC_RATE

    private var done = false
    var drawFps = false

    var _fps = 0
    def fps = _fps
    private def fps_= (fpsValue: Int) {_fps = fpsValue}

    private val FPS_TIMER_ID = "FPS Timer"

    private val FPS_DRAWING_X = 10
    private var FPS_DRAWING_Y = 0

    def getDisplayMode: DisplayMode = {
        new DisplayMode(displayWidth, displayHeight)
    }

    def initDisplay() {
        Display.setDisplayMode(getDisplayMode)
        Display.setTitle(title)
        Display.create()
    }

    def initOGL() {
//        glEnable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LIGHTING)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

//        glViewport(0, 0, displayWidth, displayHeight)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0, displayWidth, 0, displayHeight, -1, 1)
        glMatrixMode(GL_MODELVIEW)

        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
    }

    def loadResources() {}

    def updateDisplay() {
        Display.update()
        Display.sync(syncRate)
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
        if (drawFps) {
            val timePassed = TimerManager(FPS_TIMER_ID).time
            fps = (1000 / timePassed).toInt

            beginTextDrawing()
                drawText(FPS_DRAWING_X, FPS_DRAWING_Y, "FPS: " + fps.toString, Color.white)
            endTextDrawing()
        }
    }

    private def systemLoad() {
        FPS_DRAWING_Y = displayHeight - GraphicsToolkit.MEDIUM_FONT.getLineHeight - 10
        TimerManager.createTimer(FPS_TIMER_ID).start()
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
