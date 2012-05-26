package ua.org.dector.scatris.states

import ua.org.dector.lwsge.time.TimerManager
import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.graphics._
import org.newdawn.slick.opengl.Texture
import ua.org.dector.lwsge.state.{StateManager, GameState}
import org.lwjgl.input.Keyboard
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.{GraphicsToolkit, GameController}
import ua.org.dector.lwsge.LWSGEConstants._
import ua.org.dector.lwsge.console.LWSGEConsole
import ua.org.dector.scatris.{Element, Drawer}
import org.newdawn.slick.Color

/**
 * @author dector (dector9@gmail.com)
 */

object SplashGameState extends GameState("Splash") {
    private var splashFadingStarted = false
    private var splashFadingFinished = false
    private var alpha = 0f

    private val SPLASH_IMAGE_OFFSET_X = 0
    private val SPLASH_IMAGE_OFFSET_Y = 30

    private val FADING_TIMER = "Fading Timer"
    private val LOGO_MOVING_TIMER = "Logo Moving Timer"
    private val LOGO_ROTATING_TIMER = "Logo Rotating Timer"

    def added() {}

    def activate() {
        splashFadingStarted = false
        splashFadingFinished = false

        alpha = 0f
    }

    def preRenderCount() {
        if (! splashFadingStarted) {
            TimerManager.createTimer(FADING_TIMER, -Config.i(SPLASH_FADE_TIME_PAUSE))
            TimerManager.createTimer(LOGO_MOVING_TIMER).start()
            TimerManager.createTimer(LOGO_ROTATING_TIMER)

            splashFadingStarted = true
        } else if (! splashFadingFinished) {
            if (TimerManager(LOGO_MOVING_TIMER).started) {
                var wayPassed = TimerManager(LOGO_MOVING_TIMER).time.toFloat /
                        Config.i(SPLASH_LOGO_MOVING_TIME)

                if (wayPassed > 1) {
                    wayPassed = 1
                    TimerManager(LOGO_MOVING_TIMER).stop()
                    TimerManager(LOGO_ROTATING_TIMER).start()
                }

                Config(SPLASH_LOGO_LEFT_X) = Config.i(SPLASH_LOGO_LEFT_START_X) +
                        ((Config.i(SPLASH_LOGO_MEET_X) -
                                Config.i(SPLASH_LOGO_LEFT_START_X)) * wayPassed).toInt
                Config(SPLASH_LOGO_LEFT_Y) = Config.i(SPLASH_LOGO_LEFT_START_Y) +
                        ((Config.i(SPLASH_LOGO_MEET_Y) -
                                Config.i(SPLASH_LOGO_LEFT_START_Y)) * wayPassed).toInt

                Config(SPLASH_LOGO_RIGHT_X) = Config.i(SPLASH_LOGO_RIGHT_START_X) -
                        ((Config.i(SPLASH_LOGO_RIGHT_START_X) -
                                Config.i(SPLASH_LOGO_MEET_X) - Config.i(BIG_BLOCK_SIZE) -
                                Config.i(BLOCK_MARGIN)) * wayPassed).toInt
                Config(SPLASH_LOGO_RIGHT_Y) = Config.i(SPLASH_LOGO_RIGHT_START_Y) -
                        ((Config.i(SPLASH_LOGO_RIGHT_START_Y) -
                                Config.i(SPLASH_LOGO_MEET_Y))
                                * wayPassed).toInt
            } else if (TimerManager(LOGO_ROTATING_TIMER).started) {
                var partRotated = TimerManager(LOGO_ROTATING_TIMER).time.toFloat /
                        Config.i(SPLASH_LOGO_ROTATING_TIME)

                if (partRotated > 1) {
                    partRotated = 1
                    TimerManager(LOGO_ROTATING_TIMER).stop()
                    TimerManager(FADING_TIMER).start()
                }

                Config(SPLASH_LOGO_ROTATE_ANGLE) = (90 * partRotated).toInt
            } else {
                alpha = TimerManager(FADING_TIMER).time.toFloat / Config.i(SPLASH_FADE_TIME)

                if (alpha > 1) {
                    alpha = 1

                    TimerManager.destroyTimer(FADING_TIMER)
                    splashFadingFinished = true
                }
            }
        }
    }

    def render() {
        Drawer.drawElementAbs(Config.i(SPLASH_LOGO_LEFT_X), Config.i(SPLASH_LOGO_LEFT_Y),
            Config(SPLASH_LOGO_LEFT_ELEMENT).asInstanceOf[Element],
            Config(SPLASH_LOGO_LEFT_COLOR).asInstanceOf[Color],
            Config.i(SPLASH_LOGO_ROTATE_CENTER_X), Config.i(SPLASH_LOGO_ROTATE_CENTER_Y),
            Config.i(SPLASH_LOGO_ROTATE_ANGLE))

        Drawer.drawElementAbs(Config.i(SPLASH_LOGO_RIGHT_X), Config.i(SPLASH_LOGO_RIGHT_Y),
            Config(SPLASH_LOGO_RIGHT_ELEMENT).asInstanceOf[Element],
            Config(SPLASH_LOGO_RIGHT_COLOR).asInstanceOf[Color],
            Config.i(SPLASH_LOGO_ROTATE_CENTER_X), Config.i(SPLASH_LOGO_ROTATE_CENTER_Y),
            Config.i(SPLASH_LOGO_ROTATE_ANGLE))

        drawTranspImage((Config.i(SPLASH_IMAGE_X) +
                SPLASH_IMAGE_OFFSET_X * (1 - alpha)).toInt,
            (Config.i(SPLASH_IMAGE_Y) + SPLASH_IMAGE_OFFSET_Y * (1 - alpha)).toInt,
            Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureWidth,
            Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureHeight,
        Config(SPLASH_IMAGE).asInstanceOf[Texture], alpha)

        beginTextDrawing()
            drawText(Config.i(PRESS_SPACE_TO_START_MSG_X),
                Config.i(PRESS_SPACE_TO_START_MSG_Y),
                Config.s(PRESS_SPACE_TO_START_MSG))
            drawText(Config.i(SPLASH_AUTHOR_MSG_X), Config.i(SPLASH_AUTHOR_MSG_Y),
                Config.s(SPLASH_AUTHOR_MSG), font = GraphicsToolkit.SMALL_FONT)
        endTextDrawing()
    }

    def checkInput() {
        if (Config.bool(CONSOLE_OPENED)) {
            LWSGEConsole.checkInput()
        } else {
            while (Keyboard.next) {
                if (Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_SPACE => StateManager.nextState()
                        case Keyboard.KEY_ESCAPE => GameController.exit()
                        case Keyboard.KEY_GRAVE =>
                            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                                GameController.trySwitchConsole()
                        case _ => {}
                    }
                }
            }
        }
    }
}
