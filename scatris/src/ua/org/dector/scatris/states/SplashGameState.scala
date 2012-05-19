package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.graphics._
import org.newdawn.slick.opengl.Texture
import ua.org.dector.lwsge.state.{StateManager, GameState}
import org.lwjgl.input.Keyboard
import ua.org.dector.lwsge.GameController
import ua.org.dector.scatris.ScatrisConstants._

/**
 * @author dector (dector9@gmail.com)
 */

object SplashGameState extends GameState("Splash") {
    private var splashFadingStarted = false
    private var splashFadingFinished = false
    private var alpha = 0f

    private val FADING_TIMER = "Fading Timer"

    def activate() {}

    def preRenderCount() {
        if (! splashFadingStarted) {
            TimerManager.createTimer(FADING_TIMER).start()
            TimerManager(FADING_TIMER) -= Config.i(SPLASH_FADE_TIME_PAUSE)

            splashFadingStarted = true
        } else if (! splashFadingFinished) {
            alpha = TimerManager(FADING_TIMER).time.toFloat / Config.i(SPLASH_FADE_TIME)

            if (alpha > 1) {
                alpha = 1

                TimerManager.destroyTimer(FADING_TIMER)
                splashFadingFinished = true
            }
        }
    }

    def render() {
        // Why it isn't drawing from 0:0 ?
        drawTranspImage(Config.i(SPLASH_IMAGE_X), Config.i(SPLASH_IMAGE_Y) - 32,
            Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureWidth,
            Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureHeight,
        Config(SPLASH_IMAGE).asInstanceOf[Texture], alpha)

        beginTextDrawing()
        drawText(Config.i(PRESS_SPACE_TO_START_MSG_X),
            Config.i(PRESS_SPACE_TO_START_MSG_Y),
            Config.s(PRESS_SPACE_TO_START_MSG))
        endTextDrawing()
    }

    def checkInput() {
        while (Keyboard.next) {
            Keyboard.getEventKey match {
                case Keyboard.KEY_SPACE => StateManager.nextState()
                case Keyboard.KEY_ESCAPE => GameController.exit()
                case _ => {}
            }
        }
    }
}
