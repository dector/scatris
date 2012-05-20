package ua.org.dector.scatris.states

import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.graphics._
import ua.org.dector.lwsge.LWSGEConstants._
import org.newdawn.slick.Color
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.{GameController, GraphicsToolkit}
import ua.org.dector.lwsge.state.{StateManager, GameState}
import ua.org.dector.scatris.Drawer

/**
 * @author dector (dector9@gmail.com)
 */

object GameOverGameState extends GameState("Game Over") {
    def added() {}
    def activate() {}
    def preRenderCount() {}

    def render() {
        Drawer.drawGame()

        // Draw "Game Over!" notification
        // Mock
        val text = "Game Over"
        val textWidth = GraphicsToolkit.BIG_FONT.getWidth(text)
        val textHeight = GraphicsToolkit.BIG_FONT.getLineHeight
        val textX = ((Config.i(DISPLAY_WIDTH) - textWidth) / 2).toInt
        val textY = ((Config.i(DISPLAY_HEIGHT) - textHeight) / 2).toInt

        val rectWidth = textWidth + 20
        val rectHeight = textHeight + 20

        val rectX = textX - 10
        val rectY = textY - 10
        fillRect(rectX, rectY, rectWidth, rectHeight, Color.black)
        drawRect(rectX, rectY, rectWidth, rectHeight, Color.lightGray)

        beginTextDrawing()
            drawText(textX, textY, text, font = GraphicsToolkit.BIG_FONT)
        endTextDrawing()
    }

    def checkInput() {
        while (Keyboard.next && Keyboard.getEventKeyState) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_R =>
                    if (Keyboard.getEventKeyState)
                        StateManager.currentState = ResetGameState
                case Keyboard.KEY_ESCAPE =>
                    GameController.exit()
                case Keyboard.KEY_GRAVE =>
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                        GameController.trySwitchConsole()
                case _ => {}
            }
        }
    }
}
