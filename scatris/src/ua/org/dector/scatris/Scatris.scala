package ua.org.dector.scatris

import states._
import ua.org.dector.lwsge.common.Config._
import org.newdawn.slick.Color
import ua.org.dector.lwsge.common.Config
import ua.org.dector.lwsge.state.StateManager
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.opengl.{Texture, TextureLoader}
import ua.org.dector.lwsge.{GraphicsToolkit, LWSGEApp}
import ua.org.dector.scatris.ScatrisConstants._
import ua.org.dector.lwsge.LWSGEConstants._

/**
 * @author dector (dector9@gmail.com)
 */

object Scatris extends LWSGEApp("GameCore") {
    init()

    private def init() {
        Config(FIELD_X_BLOCKS_NUM)      = 10
        Config(FIELD_Y_BLOCKS_NUM)      = 20

        Config(BIG_BLOCK_SIZE)          = 20
        Config(SMALL_BLOCK_SIZE)        = 10
        Config(BLOCK_MARGING)           = 2

        Config(FIELD_X_PADDING)         = 5
        Config(FIELD_Y_PADDING)         = 10

        Config(BLOCK_COLOR)             = Color.lightGray

        Config(BLOCKS_DIFF_PLACE)       = ((Config.i(BIG_BLOCK_SIZE) -
                Config.i(SMALL_BLOCK_SIZE)) / 2).toInt

        Config(FIELD_WIDTH)             = Config.i(FIELD_X_BLOCKS_NUM) *
                (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                2 * Config.i(FIELD_X_PADDING)
        Config(FIELD_HEIGHT)            = Config.i(FIELD_Y_BLOCKS_NUM) *
                (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                2 * Config.i(FIELD_Y_PADDING)

        Config(FIELD_X_START)           = ((Config.i(DISPLAY_WIDTH) -
                Config.i(FIELD_WIDTH))/2).toInt
        Config(FIELD_Y_START)           = ((Config.i(DISPLAY_HEIGHT) -
                Config.i(FIELD_HEIGHT))/2).toInt

        Config(NEXT_ELEMENT_SHOW_WIDTH)         = 3 *
                (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING))
        Config(NEXT_ELEMENT_SHOW_HEIGHT)        = 4 *
                (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING))
        Config(NEXT_ELEMENT_SHOW_OFFSET_X)      = 20
        Config(NEXT_ELEMENT_SHOW_OFFSET_Y)      = 40 +
                Config.i(NEXT_ELEMENT_SHOW_HEIGHT)
        Config(NEXT_ELEMENT_SHOW_X_IN_BLOCKS)   = ((Config.i(FIELD_WIDTH) +
                Config.i(NEXT_ELEMENT_SHOW_OFFSET_X)) / Config.i(BIG_BLOCK_SIZE))
        Config(NEXT_ELEMENT_SHOW_Y_IN_BLOCKS)   = ((Config.i(FIELD_HEIGHT) -
                Config.i(NEXT_ELEMENT_SHOW_OFFSET_Y)) / Config.i(BIG_BLOCK_SIZE))

        Config(STARTING_TICK_TIME)      = 500
        Config(FAST_FALLING_TICK_COEF)  = 0.12f

        Config(SCORE_PER_CLEARED_LINE)  = 10
        Config(SCORE_SPEEDUP_COEF)      = 2
        Config(SCORE_PER_FALLING_LINE)  = 10 // per falling line down
        Config(SPEEDUP_LINE_NUM)        = 10
        Config(SPEEDUP_FALLING_COEF)    = 0.85f

        Config(SPLASH_IMAGE_FORMAT)     = "PNG"
        Config(SPLASH_IMAGE_FILE)       = "scatris.png"

        Config(SPLASH_FADE_TIME)        = 2000
        Config(SPLASH_FADE_TIME_PAUSE)  = 500

        Config(PRESS_SPACE_TO_START_MSG)        = "Press <Space> to start"

        Config(LEFT_MOVE_TIME_BOUND)    = 80
        Config(RIGHT_MOVE_TIME_BOUND)   = 80

        StateManager.addState(SplashGameState, RunningGameState)
        StateManager.addState(RunningGameState)
        StateManager.addState(ResetGameState, RunningGameState)
        StateManager.addState(PausedGameState)
        StateManager.addState(GameOverGameState)

        StateManager.currentState = SplashGameState
    }

    override def preRenderCount {
        StateManager.currentState.preRenderCount()
    }

    override def render {
        StateManager.currentState.render()
    }

    override def checkInput {
        StateManager.currentState.checkInput()
    }

    override def loadResources() {
        Config(SPLASH_IMAGE) = TextureLoader.getTexture(SPLASH_IMAGE_FORMAT,
            ResourceLoader.getResourceAsStream(Config.s(SPLASH_IMAGE_FILE)))

        Config(SPLASH_IMAGE_X) = ((Config.i(DISPLAY_WIDTH) -
                Config(SPLASH_IMAGE).asInstanceOf[Texture].getImageWidth) / 2).toInt
        Config(SPLASH_IMAGE_Y) = ((Config.i(DISPLAY_HEIGHT) -
                Config(SPLASH_IMAGE).asInstanceOf[Texture].getImageHeight) / 2).toInt

        Config(PRESS_SPACE_TO_START_MSG_X) = ((Config.i(DISPLAY_WIDTH) -
                GraphicsToolkit.MEDIUM_FONT.getWidth(
                    Config.s(PRESS_SPACE_TO_START_MSG))) /2).toInt
        Config(PRESS_SPACE_TO_START_MSG_Y) = 2 * GraphicsToolkit.MEDIUM_FONT.getLineHeight
    }
}
