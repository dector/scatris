package ua.org.dector.lwsgl

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}
import graphics._
import org.newdawn.slick.Color

/**
 * @author dector (dector9@gmail.com)
 */

abstract class LWSGLApp(val name: String) {
    private val DEFAULT_DISPLAY_WIDTH = 640
    private val DEFAULT_DISPLAY_HEIGHT = 480
    private val DEFAULT_DISPLAY_SYNC_RATE = 60

    val displayWidth = DEFAULT_DISPLAY_WIDTH
    val displayHeight = DEFAULT_DISPLAY_HEIGHT

    val clearColor = Color.black

    val title = name
    val syncRate = DEFAULT_DISPLAY_SYNC_RATE

    private var done = false
    var drawFps = true

    var _fps = 0
    def fps = _fps
    private def fps_= (fpsValue: Int) {_fps = fpsValue}

    var fpsTime = getCurrentTime
    val FPS_DRAWING_X = displayHeight - 20
    val FPS_DRAWING_Y = displayWidth + 5

    def getCurrentTime: Long = System.currentTimeMillis

    def getDisplayMode: DisplayMode = {
        new DisplayMode(displayWidth, displayHeight)
    }

    def initDisplay() {
        Display.setDisplayMode(getDisplayMode)
        Display.setTitle(title)
        Display.create()
    }

    def initOGL() {
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//        glEnable(GL_TEXTURE_2D)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0, displayWidth, 0, displayHeight, 1, -1)
        glMatrixMode(GL_MODELVIEW)

        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
    }

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

    def drawDebug() {
        if (drawFps) {
            val currentTime = getCurrentTime
            fps = (1000 / (currentTime - fpsTime)).toInt
            fpsTime = currentTime

//            drawText(FPS_DRAWING_X, FPS_DRAWING_Y, "FPS: " + fps.toString)
//            drawText(100, 100, "FPS: " + fps.toString, Color.red)
        }
    }

    def execute() {
        initDisplay()
        initOGL()

        while (!(done || Display.isCloseRequested)) {
            clear()
            detectInput()
            preRenderCount()
            render()
            drawDebug()
            updateDisplay()
        }

        exitHook()
    }

    def main(args: Array[String]) {
        execute()
    }
}
