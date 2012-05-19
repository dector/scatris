package ua.org.dector.scatris

import org.newdawn.slick.opengl.{Texture, TextureLoader}
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.Color

import states._
import util.Random
import collection.mutable.ArrayBuffer

import ua.org.dector.lwsge._
import common.Config
import LWSGEConstants._
import time.TimerManager
import state.StateManager
import ScatrisConstants._

/**
 * @author dector (dector9@gmail.com)
 */

object Scatris extends LWSGEApp("Scatris") {
    val TICK_TIMER = "Tick Timer"
    val LEFT_MOVE_TIMER = "Left Move Timer"
    val RIGHT_MOVE_TIMER = "Right Move Timer"

    // Define it after internal constants
    init()

    val field = new GameField(Config.i(FIELD_X_BLOCKS_NUM),
        Config.i(FIELD_Y_BLOCKS_NUM))

    var tickTimeBound = Config.i(STARTING_TICK_TIME)

    private val elementsPool =
        Array(new Stick, new Block, new RZip, new LZip, new G, new Seven, new T)
    var nextElement = getNextFallingElement

    var currElement = getNextFallingElement
    var currElementX = getStartFallingX
    var currElementY = getStartFallingY

    var score = 0
    var lines = 0

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

    def reset() {
        field.clear()
        generateNextFallingElement()

        tickTimeBound = Config.i(STARTING_TICK_TIME)

        score = 0
        lines = 0
    }

    // TODO: Move them to Game Mechanic class
    // Game logic procedures

    private def getStartFallingX: Int = Config.i(FIELD_X_BLOCKS_NUM) / 2 - 1
    private def getStartFallingY: Int = Config.i(FIELD_Y_BLOCKS_NUM) - 1

    private def getNextFallingElement: Element = {
        elementsPool(Random nextInt elementsPool.size)
    }

    private def generateNextFallingElement() {
        currElement = nextElement
        nextElement = getNextFallingElement

        while (currElement eq nextElement) nextElement = getNextFallingElement

        currElementX = getStartFallingX
        currElementY = getStartFallingY
    }

    private def canMoveCurrElementDown: Boolean = {
        var canMove = true
        var i = 0

        val bottomBlocksY = currElement.bottomBlocksY

        while (canMove && i < currElement.width) {
            if (field(currElementX + i, currElementY + bottomBlocksY(i) - 1)) canMove = false
            i += 1
        }

        canMove
    }

    def canMoveCurrElementLeft: Boolean = {
        var canMove = true
        var i = 0

        val leftBlocksX = currElement.leftBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + leftBlocksX(i) - 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    def canMoveCurrElementRight: Boolean = {
        var canMove = true
        var i = 0

        val rightBlocksX = currElement.rightBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + rightBlocksX(i) + 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    def canRotateCurrElementRight: Boolean = {
        var canRotateRight = true

        rotateCurrElementRight()
        for ((x, y) <- currElement.blocks)
            if (field(currElementX + x, currElementY + y))
                canRotateRight = false
        rotateCurrElementLeft()

        canRotateRight
    }

    def rotateCurrElementLeft() {
        currElementX -= currElement.offsetX
        currElementY -= currElement.offsetY
        currElement.setPreviousRotation()
        currElementX += currElement.offsetX
        currElementY += currElement.offsetY
    }

    def rotateCurrElementRight() {
        currElementX -= currElement.offsetX
        currElementY -= currElement.offsetY
        currElement.setNextRotation()
        currElementX += currElement.offsetX
        currElementY += currElement.offsetY
    }

    def moveCurrElementDown() {currElementY -= 1}
    def moveCurrElementLeft() {currElementX -= 1}
    def moveCurrElementRight() {currElementX += 1}

    def dropCurrElementDown() {
        while (canMoveCurrElementDown) moveCurrElementDown()
        processFallenElement()
    }

    def fallDownFast() {
        if (TimerManager(TICK_TIMER).time >=
                tickTimeBound * Config.f(FAST_FALLING_TICK_COEF)) {
            Scatris.tick()
            TimerManager(TICK_TIMER).restart()
        }
    }

    def moveCurrElementLeftByTimer() {
        if (TimerManager(LEFT_MOVE_TIMER).time >=
                Config.i(LEFT_MOVE_TIME_BOUND)) {
            Scatris.moveCurrElementLeft()
            TimerManager(LEFT_MOVE_TIMER).restart()
        }
    }

    def moveCurrElementRightByTimer() {
        if (TimerManager(RIGHT_MOVE_TIMER).time >=
                Config.i(RIGHT_MOVE_TIME_BOUND)) {
            Scatris.moveCurrElementRight()
            TimerManager(RIGHT_MOVE_TIMER).restart()
        }
    }

    private def checkAndDeleteFullLines() {
        val linesToDrop = new ArrayBuffer[Int]

        var x = 0
        var isFull = true

        for (line <- 0 until field.height) {
            isFull = true
            x = 0

            while (isFull && x < field.width) {
                if (! field(x, line)) isFull = false
                else x += 1
            }

            if (isFull) linesToDrop += line
        }

        for (line <- linesToDrop) {
            for (x <- 0 until field.width) {
                field(x, line) = false
            }
        }

//        println("Drop Lines: " + linesToDrop)

        if (! linesToDrop.isEmpty) {
            lines += linesToDrop.size
            score += linesToDrop.size * Config.i(SCORE_PER_CLEARED_LINE)

            if (lines % Config.i(SPEEDUP_LINE_NUM) == 0) {
                tickTimeBound = (tickTimeBound * Config.f(SPEEDUP_FALLING_COEF)).toInt
                score *= Config.i(SCORE_SPEEDUP_COEF)
            }

            val linesToMove = new ArrayBuffer[(Int, Int)]

            var isEmpty = true
            var dropsCount = 0

            for (line <- 0 until field.height) {
                isEmpty = true
                x = 0

                while (isEmpty && x < field.width) {
                    if (field(x, line)) isEmpty = false
                    else x += 1
                }

                if (isEmpty) {
                    dropsCount += 1
                    linesToMove += ((line, dropsCount))
                }
            }

            var i = 0
            var lastMove = linesToMove(i)
            var nextMove = linesToMove(i+1)
            i += 1

            for (line <- 0 until field.height) {
                if (line > lastMove._1) {
                    for (x <- 0 until field.width) {
                        field(x, line - lastMove._2) = field(x, line)
                        field(x, line) = false

                    }
                }
                if (line == nextMove._1) {
                    lastMove = nextMove
                    i += 1

                    if (i < linesToMove.size) {
                        nextMove = linesToMove(i)
                    } else {
                        nextMove = (field.height, 0)
                    }
                }
            }
        }
    }

    private def processFallenElement() {
        field.append(currElement, currElementX - currElement.offsetX,
            currElementY - currElement.offsetY)

        if (field(getStartFallingX, getStartFallingY)) {
            StateManager.currentState = GameOverGameState
        } else {
            generateNextFallingElement()

            checkAndDeleteFullLines()
        }
    }

    def tick() {
        if (currElement != null && canMoveCurrElementDown)
            moveCurrElementDown()
        else
            processFallenElement()
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