package ua.org.dector.lwsge

import org.lwjgl.opengl.GL11._
import org.newdawn.slick.{Color, Font}
import GraphicsToolkit._
import org.newdawn.slick.opengl.Texture

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
        glColor4f(color.r, color.g, color.b, color.a)
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
        glColor4f(color.r, color.g, color.b, color.a)
        glBegin(GL_LINE_LOOP)
            glVertex2i(xd, yd + height)
            glVertex2i(xd + width, yd + height)
            glVertex2i(xd + width, yd)
            glVertex2i(xd, yd)
        glEnd()
    }

    def drawLine(x0: Int, y0: Int, x1: Int, y1: Int, color: Color) {
        glColor4f(color.r, color.g, color.b, color.a)
        glBegin(GL_LINE)
            glVertex2i(x0, y0)
            glVertex2i(x1, y1)
        glEnd()
    }

    def beginTextDrawing() { glEnable(GL_TEXTURE_2D) }
    def endTextDrawing() { glDisable(GL_TEXTURE_2D) }

    def drawText(x: Int, y: Int, text: String, color: Color = DEFAULT_FOREGROUND_COLOR,
                 font: Font = MEDIUM_FONT) {
        font.drawString(x, y, text, color)
    }

    def drawTranspImage(x: Int, y: Int, width: Int, height: Int, image: Texture,
                             alpha: Float) {
        glColor4f(1, 1, 1, alpha)
        drawImage(x, y, width, height, image)
    }

    def drawImage(x: Int, y: Int, width: Int, height: Int, image: Texture) {
        glEnable(GL_TEXTURE_2D)
        image.bind()

        glBegin(GL_QUADS)
            glTexCoord2f(0, 0)
            glVertex2i(x, y + height)
            glTexCoord2f(1, 0)
            glVertex2i(x + width, y + height)
            glTexCoord2f(1, 1)
            glVertex2i(x + width, y)
            glTexCoord2f(0, 1)
            glVertex2i(x, y)
        glEnd()

        glDisable(GL_TEXTURE_2D)
    }
}