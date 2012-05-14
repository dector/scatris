package ua.org.dector.lwsgl

import org.lwjgl.opengl.GL11._
import org.newdawn.slick.{Color, Font}
import GraphicsToolkit._

/**
 * @author dector (dector9@gmail.com)
 */
package object graphics {
    def fillRectCentered(cx: Int, cy: Int, width: Int, height: Int, color: Color) {
        val xd = cx - (width/2).toInt
        val yd = cy - (height/2).toInt

        fillRect(xd, yd, width, height, color)
    }

    def fillRect(xd: Int, yd: Int, width: Int, height: Int, color: Color) {
        glColor3f(color.r, color.g, color.b)
        glBegin(GL_QUADS)
        glVertex2i(xd, yd + height)
        glVertex2i(xd + width, yd + height)
        glVertex2i(xd + width, yd)
        glVertex2i(xd, yd)
        glEnd()
    }

    def drawRectCentered(cx: Int, cy: Int, width: Int, height: Int, color: Color) {
        val xd = cx - (width/2).toInt
        val yd = cy - (height/2).toInt

        drawRect(xd, yd, width, height, color)
    }

    def drawRect(xd: Int, yd: Int, width: Int, height: Int, color: Color) {
        glColor3f(color.r, color.g, color.b)
        glBegin(GL_LINE_LOOP)
        glVertex2i(xd, yd + height)
        glVertex2i(xd + width, yd + height)
        glVertex2i(xd + width, yd)
        glVertex2i(xd, yd)
        glEnd()
    }

    def drawText(x: Int, y: Int, text: String, color: Color = DEFAULT_FOREGROUND_COLOR,
                 font: Font = DEFAULT_FONT) {
        // TODO: Remake text drawing
//        glEnable(GL_BLEND)
        font.drawString(x, y, text, color)
//        glDisable(GL_BLEND)
    }
}