package ua.org.dector.lwsgl

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}

/**
 * @author dector (dector9@gmail.com)
 */

abstract class LWSGLApp(val name: String) {
    private val DEFAULT_DISPLAY_WIDTH = 640
    private val DEFAULT_DISPLAY_HEIGHT = 480
    private val DEFAULT_DISPLAY_SYNC_RATE = 60

    val displayWidth = DEFAULT_DISPLAY_WIDTH
    val displayHeight = DEFAULT_DISPLAY_HEIGHT

    val title = name
    val syncRate = DEFAULT_DISPLAY_SYNC_RATE

    private var done = false

    def getDisplayMode: DisplayMode = {
        new DisplayMode(displayWidth, displayHeight)
    }

    def initDisplay() {
        Display.setDisplayMode(getDisplayMode)
        Display.setTitle(title)
        Display.create()
    }

    def initOGL() {
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0, displayWidth, displayHeight, 0, -1, 1)
        glMatrixMode(GL_MODELVIEW)
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

    def render() {}

    def execute() {
        initDisplay()
        initOGL()

        while (!(done || Display.isCloseRequested)) {
            clear()
            detectInput()
            render()
            updateDisplay()
        }

        exitHook()
    }

    def main(args: Array[String]) {
        execute()
    }
}
