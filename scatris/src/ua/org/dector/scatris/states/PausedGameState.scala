package ua.org.dector.scatris.states

import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.LWSGEConstants._
import ua.org.dector.lwsge.graphics._
import org.newdawn.slick.Color
import ua.org.dector.scatris.ScatrisConstants._
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.{GameController, GraphicsToolkit}
import ua.org.dector.scatris.{GameCore, Drawer}
import ua.org.dector.lwsge.state.{StateManager, GameState}
import ua.org.dector.lwsge.time.TimerManager._
import ua.org.dector.lwsge.time.TimerManager

/**
 * @author dector (dector9@gmail.com)
 */

object PausedGameState extends GameState("Paused") {
    def added() {}

    def activate() {
        TimerManager(GameCore.TICK_TIMER).pause()
        TimerManager(GameCore.LEFT_MOVE_TIMER).pause()
        TimerManager(GameCore.RIGHT_MOVE_TIMER).pause()
    }

    def preRenderCount() {}

    def render() {
        Drawer.drawGame()

        // Draw "Pause!" notification
        // Mock
        val text = "Paused"
        val text2 = "Press <P> to continue"
        val textWidth = GraphicsToolkit.MEDIUM_FONT.getWidth(text)
        val textWidth2 = GraphicsToolkit.MEDIUM_FONT.getWidth(text2)
        val textHeight = GraphicsToolkit.MEDIUM_FONT.getLineHeight
        val textX = ((Config.i(DISPLAY_WIDTH) - textWidth) / 2).toInt
        val textX2 = ((Config.i(DISPLAY_WIDTH) - textWidth2) / 2).toInt
        val textY2 = ((Config.i(DISPLAY_HEIGHT) - 2.5f*textHeight) / 2).toInt

        val rectWidth = textWidth2 + 20
        val rectHeight = (2.5f * textHeight + 20).toInt

        val rectX = textX2 - 10
        val rectY = textY2 - 10
        fillRect(rectX, rectY, rectWidth, rectHeight, Color.black)
        drawRect(rectX, rectY, rectWidth, rectHeight, Color.lightGray)

        beginTextDrawing()
        drawText(textX, (textY2 + 1.5f*textHeight).toInt, text)
        drawText(textX2, textY2, text2)
        endTextDrawing()
    }

    def checkInput() {
        while (Keyboard.next && Keyboard.getEventKeyState) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_ESCAPE =>
                    GameController.exit()
                case Keyboard.KEY_P =>
                    StateManager.currentState = RunningGameState
                case _ => {}
            }
        }
    }
}
