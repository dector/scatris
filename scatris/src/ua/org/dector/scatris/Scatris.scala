package ua.org.dector.scatris

import org.newdawn.slick.opengl.{Texture, TextureLoader}
import org.newdawn.slick.util.ResourceLoader
import org.lwjgl.input.Keyboard

import ua.org.dector.lwsge._
import common.Config
import LWSGEConstants._
import ScatrisConstants._
import time.TimerManager
import ua.org.dector.lwsge.graphics._
import util.Random
import collection.mutable.ArrayBuffer
import org.newdawn.slick.Color


/**
 * @author dector (dector9@gmail.com)
 */

object GameState extends Enumeration {
    type GameState = Value

    val Splash, Running, Paused, GameOver = Value
}

import GameState._

object Scatris extends LWSGEApp("Scatris") {
    init()

    private val TICK_TIMER = "Tick Timer"
    private val FADING_TIMER = "Fading Timer"
    private val LEFT_MOVE_TIMER = "Left Move Timer"
    private val RIGHT_MOVE_TIMER = "Right Move Timer"

    private val field = new GameField(Config.i(FIELD_X_BLOCKS_NUM),
        Config.i(FIELD_Y_BLOCKS_NUM))

    private var splashFadingStarted = false
    private var splashFadingFinished = false

    private val elementsPool =
        Array(new Stick, new Block, new RZip, new LZip, new G, new Seven, new T)
    private var nextElement = getNextFallingElement

    private var currElement = getNextFallingElement
    private var currElementX = getStartFallingX
    private var currElementY = getStartFallingY

    private var tickTimeBound = Config.i(STARTING_TICK_TIME)

    private var gameState = Splash

    private var score = 0
    private var lines = 0

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
    }

    // Make it DRY - how in scala?
    private def resetGame() {
        field.clear()
        generateNextFallingElement()

        TimerManager(TICK_TIMER).restart()
        TimerManager(LEFT_MOVE_TIMER).restart()
        TimerManager(RIGHT_MOVE_TIMER).restart()

        tickTimeBound = Config.i(STARTING_TICK_TIME)
        gameState = Running // Oh really make it dry??? Not Splash now, yep?

        score = 0
        lines = 0
    }

    private def play() {
        gameState = Running
        TimerManager.createTimer(TICK_TIMER)
        TimerManager.createTimer(LEFT_MOVE_TIMER)
        TimerManager.createTimer(RIGHT_MOVE_TIMER)

        resetGame()
    }

    private def setGameOverState() { gameState = GameOver }

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

    private def canMoveCurrElementLeft: Boolean = {
        var canMove = true
        var i = 0

        val leftBlocksX = currElement.leftBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + leftBlocksX(i) - 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    private def canMoveCurrElementRight: Boolean = {
        var canMove = true
        var i = 0

        val rightBlocksX = currElement.rightBlocksX

        while (canMove && i < currElement.height) {
            if (field(currElementX + rightBlocksX(i) + 1, currElementY + i)) canMove = false
            i += 1
        }

        canMove
    }

    private def canRotateCurrElementRight: Boolean = {
        var canRotateRight = true

        rotateCurrElementRight()
        for ((x, y) <- currElement.blocks)
            if (field(currElementX + x, currElementY + y))
                canRotateRight = false
        rotateCurrElementLeft()

        canRotateRight
    }

    private def rotateCurrElementLeft() {
        currElementX -= currElement.offsetX
        currElementY -= currElement.offsetY
        currElement.setPreviousRotation()
        currElementX += currElement.offsetX
        currElementY += currElement.offsetY
    }

    private def rotateCurrElementRight() {
        currElementX -= currElement.offsetX
        currElementY -= currElement.offsetY
        currElement.setNextRotation()
        currElementX += currElement.offsetX
        currElementY += currElement.offsetY
    }

    private def moveCurrElementDown() {currElementY -= 1}
    private def moveCurrElementLeft() {currElementX -= 1}
    private def moveCurrElementRight() {currElementX += 1}

    private def dropCurrElementDown() {
        while (canMoveCurrElementDown) moveCurrElementDown()
        processElementFall()
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

    private def processElementFall() {
        field.append(currElement, currElementX - currElement.offsetX,
            currElementY - currElement.offsetY)

        if (field(getStartFallingX, getStartFallingY)) {
            setGameOverState()
        } else {
            generateNextFallingElement()

            checkAndDeleteFullLines()
        }
    }

    private def tick() {
        if (currElement != null && canMoveCurrElementDown)
            moveCurrElementDown()
        else
            processElementFall()
    }

    private def fallFast() {
        if (TimerManager(TICK_TIMER).time >=
                tickTimeBound * Config.f(FAST_FALLING_TICK_COEF)) {
            tick()
            TimerManager(TICK_TIMER).restart()
        }
    }

    private def moveCurrElementLeftByTimer() {
        if (TimerManager(LEFT_MOVE_TIMER).time >=
                Config.i(LEFT_MOVE_TIME_BOUND)) {
            moveCurrElementLeft()
            TimerManager(LEFT_MOVE_TIMER).restart()
        }
    }

    private def moveCurrElementRightByTimer() {
        if (TimerManager(RIGHT_MOVE_TIMER).time >=
                Config.i(RIGHT_MOVE_TIME_BOUND)) {
            moveCurrElementRight()
            TimerManager(RIGHT_MOVE_TIMER).restart()
        }
    }

    // Draw procedures

    private def drawFieldBorder() {
        drawRect(Config.i(FIELD_X_START), Config.i(FIELD_Y_START),
            Config.i(FIELD_WIDTH), Config.i(FIELD_HEIGHT),
            Config(BLOCK_COLOR).asInstanceOf[Color])
    }

    private def drawBlock(xNum: Int, yNum: Int) {
        val x = xNum * (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                Config.i(FIELD_X_START) + Config.i(FIELD_X_PADDING)
        val y = yNum * (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) +
                Config.i(FIELD_Y_START) + Config.i(FIELD_Y_PADDING)

        drawRect(x, y, Config.i(BIG_BLOCK_SIZE), Config.i(BIG_BLOCK_SIZE),
            Config(BLOCK_COLOR).asInstanceOf[Color])
        fillRect(x + Config.i(BLOCKS_DIFF_PLACE), y + Config.i(BLOCKS_DIFF_PLACE),
            Config.i(SMALL_BLOCK_SIZE), Config.i(SMALL_BLOCK_SIZE),
            Config(BLOCK_COLOR).asInstanceOf[Color])
    }

    override def preRenderCount {
        if (gameState == Running && TimerManager(TICK_TIMER).time >= tickTimeBound) {
            TimerManager(TICK_TIMER).restart()
            tick()
        }
    }

    override def render {
        if (gameState == Running || gameState == Paused || gameState == GameOver) {
            // Draw field
            drawFieldBorder()

            for (x <- 0 until field.width) {
                for (y <- 0 until field.height) {
                    if (field(x, y))
                        drawBlock(x, y)
                }
            }

            // Draw statistics
            val statX = Config.i(FIELD_X_START) +
                    Config.i(NEXT_ELEMENT_SHOW_X_IN_BLOCKS) * (Config.i(BIG_BLOCK_SIZE) +
                    Config.i(BLOCK_MARGING))
            val statY = Config.i(FIELD_Y_START) + Config.i(NEXT_ELEMENT_SHOW_Y_IN_BLOCKS) *
                    (Config.i(BIG_BLOCK_SIZE) + Config.i(BLOCK_MARGING)) -
                    4 * GraphicsToolkit.MEDIUM_FONT.getLineHeight

            val statText2 = "Score: " + score
            val statText3 = "Lines: " + lines
            //            val statText1 = "Level: "

            beginTextDrawing()
                drawText(statX, statY + (1.5f *
                        GraphicsToolkit.MEDIUM_FONT.getLineHeight).toInt, statText2)
                drawText(statX, statY, statText3)
            endTextDrawing()


            // Draw falling element
            if (currElement != null) {
                var elX, elY = 0
                for ((x, y) <- currElement.blocks) {
                    elX = currElementX + x
                    elY = currElementY + y

                    if (0 <= elX && elX < Config.i(FIELD_X_BLOCKS_NUM)
                            && 0 <= elY && elY < Config.i(FIELD_Y_BLOCKS_NUM))
                        drawBlock(elX, elY)
                }
            }

            // Draw next element
            var elX, elY = 0
            for ((x, y) <- nextElement.blocks) {
                elX = Config.i(NEXT_ELEMENT_SHOW_X_IN_BLOCKS) + x
                elY = Config.i(NEXT_ELEMENT_SHOW_Y_IN_BLOCKS) + y

                drawBlock(elX, elY)
            }

            // Draw score
//            println("Score: " + score + " Lines: " + lines)
        }

        if (gameState == GameOver) {
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
        } else if (gameState == Splash) {
            if (! splashFadingStarted) {
                TimerManager.createTimer(FADING_TIMER).start()
                TimerManager(FADING_TIMER) -= Config.i(SPLASH_FADE_TIME_PAUSE)

                splashFadingStarted = true
            } else {
                var alpha = 1f

                if (! splashFadingFinished) {
                    alpha = TimerManager(FADING_TIMER).time.toFloat / Config.i(SPLASH_FADE_TIME)
                    if (alpha > 1) {
                        alpha = 1
                        splashFadingFinished = true
                        TimerManager.destroyTimer(FADING_TIMER)
                    }
                }

                // Why it isn't drawing from 0:0 ?
                drawTranspImage(Config.i(SPLASH_IMAGE_X), Config.i(SPLASH_IMAGE_Y) - 32,
                    Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureWidth,
                    Config(SPLASH_IMAGE).asInstanceOf[Texture].getTextureHeight,
                    Config(SPLASH_IMAGE).asInstanceOf[Texture], alpha)
            }


            beginTextDrawing()
                drawText(Config.i(PRESS_SPACE_TO_START_MSG_X),
                    Config.i(PRESS_SPACE_TO_START_MSG_Y),
                    Config.s(PRESS_SPACE_TO_START_MSG))
            endTextDrawing()
        } else if (gameState == Paused) {
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
    }

    // Input procedures

    override def detectInput {
        gameState match {
            case Running => {
                if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                    fallFast()
                if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && canMoveCurrElementLeft)
                    moveCurrElementLeftByTimer()
                else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && canMoveCurrElementRight)
                    moveCurrElementRightByTimer()

                while (Keyboard.next && Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_UP =>
                            if (canRotateCurrElementRight) rotateCurrElementRight()
//                        case Keyboard.KEY_LEFT =>
//                            if (Keyboard.getEventKeyState && canMoveCurrElementLeft)
//                                moveCurrElementLeft()
//                        case Keyboard.KEY_RIGHT =>
//                            if (Keyboard.getEventKeyState && canMoveCurrElementRight)
//                                moveCurrElementRight()
                        case Keyboard.KEY_SPACE =>
                            dropCurrElementDown()
                        case Keyboard.KEY_ESCAPE =>
                            exit()
                        case Keyboard.KEY_R =>
                            if (Keyboard.getEventKeyState) resetGame()
                        case Keyboard.KEY_P =>
                            togglePause()
                        case _ => {}
                    }
                }
            }
            case Paused => {
                while (Keyboard.next && Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_ESCAPE =>
                            exit()
                        case Keyboard.KEY_P =>
                            togglePause()
                        case _ => {}
                    }
                }
            }
            case GameOver => {
                while (Keyboard.next && Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_R =>
                            if (Keyboard.getEventKeyState) resetGame()
                        case Keyboard.KEY_ESCAPE =>
                            exit()
                        case _ => {}
                    }
                }
            }
            case Splash => {
                while (Keyboard.next) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_SPACE =>
                            play()
                        case Keyboard.KEY_ESCAPE =>
                            exit()
                        case _ => {}
                    }
                }
            }
        }
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

    private def togglePause() {
        if (gameState == Running) {
            gameState = Paused

            TimerManager(TICK_TIMER).pause()
        } else if (gameState == Paused) {
            gameState = Running

            TimerManager(TICK_TIMER).start()
        }
    }
}