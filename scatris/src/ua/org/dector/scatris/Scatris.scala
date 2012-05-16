package ua.org.dector.scatris

import ua.org.dector.lwsgl.{GraphicsToolkit, LWSGLApp}
import ua.org.dector.lwsgl.graphics._
import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{Texture, TextureLoader}
import org.newdawn.slick.util.ResourceLoader
import org.lwjgl.input.Keyboard
import util.Random
import collection.mutable.ArrayBuffer


/**
 * @author dector (dector9@gmail.com)
 */

object GameState extends Enumeration {
    type GameState = Value

    val Splash, Running, Paused, GameOver = Value
}

import GameState._

object Scatris extends LWSGLApp("Scatris") {
    private val FIELD_X_BLOCKS_NUM = 10
    private val FIELD_Y_BLOCKS_NUM = 20

    private val BLOCK_COLOR = Color.lightGray

    private val BLOCK_MARGING = 2
    private val BIG_BLOCK_SIZE = 20
    private val SMALL_BLOCK_SIZE = 10
    private val BLOCKS_DIFF_PLACE = ((BIG_BLOCK_SIZE - SMALL_BLOCK_SIZE) / 2).toInt

    private val FIELD_X_PADDING = 5
    private val FIELD_Y_PADDING = 10
    private val FIELD_WIDTH = FIELD_X_BLOCKS_NUM * (BIG_BLOCK_SIZE + BLOCK_MARGING) +
                                2*FIELD_X_PADDING
    private val FIELD_HEIGHT = FIELD_Y_BLOCKS_NUM * (BIG_BLOCK_SIZE + BLOCK_MARGING) +
                                2*FIELD_Y_PADDING

    private val FIELD_X_START = ((displayWidth - FIELD_WIDTH)/2).toInt
    private val FIELD_Y_START = ((displayHeight - FIELD_HEIGHT)/2).toInt

    private val NEXT_ELEMENT_SHOW_WIDTH = 3 * (BIG_BLOCK_SIZE + BLOCK_MARGING)
    private val NEXT_ELEMENT_SHOW_HEIGHT = 4 * (BIG_BLOCK_SIZE + BLOCK_MARGING)
    private val NEXT_ELEMENT_SHOW_OFFSET_X = 20
    private val NEXT_ELEMENT_SHOW_OFFSET_Y = 40 + NEXT_ELEMENT_SHOW_HEIGHT
    private val NEXT_ELEMENT_SHOW_X_IN_BLOCKS = ((FIELD_WIDTH +
            NEXT_ELEMENT_SHOW_OFFSET_X) / BIG_BLOCK_SIZE).toInt
    private val NEXT_ELEMENT_SHOW_Y_IN_BLOCKS = ((FIELD_HEIGHT -
            NEXT_ELEMENT_SHOW_OFFSET_Y) / BIG_BLOCK_SIZE).toInt

    private val STARTING_TICK_TIME = 500
    private val FAST_FALLING_TICK_TIME = 50
    private val SPEEDUP_FALLING = 0.25f

    private val SCORE_PER_CLEARED_LINE = 10
    private val SCORE_PER_FALLING_LINE = 10 // per falling line down

    private val SPLASH_IMAGE_FORMAT = "PNG"
    private val SPLASH_IMAGE_FILE = "scatris.png"
    private var SPLASH_IMAGE: Texture = null
    private var SPLASH_IMAGE_X = 0
    private var SPLASH_IMAGE_Y = 0

    private val PRESS_SPACE_TO_START_MSG = "Press <Space> to start"
    private var PRESS_SPACE_TO_START_MSG_X = 0
    private var PRESS_SPACE_TO_START_MSG_Y = 0


    private val field = new GameField(FIELD_X_BLOCKS_NUM, FIELD_Y_BLOCKS_NUM)

    private val elementsPool =
        Array(new Stick, new Block, new RZip, new LZip, new G, new Seven, new T)
    private var nextElement = getNextFallingElement

    private var currElement = getNextFallingElement
    private var currElementX = getStartFallingX
    private var currElementY = getStartFallingY
    
    private var lastTime = getCurrentTime
    private var tickTime = STARTING_TICK_TIME

    private var gameState = Splash

    private var score = 0
    private var lines = 0

    private def updateLastTime() {lastTime = getCurrentTime}

    // Make it DRY - how in scala?
    private def resetGame() {
        field.clear()
        generateNextFallingElement()
        updateLastTime()
        tickTime = STARTING_TICK_TIME
        gameState = Running // Oh really make it dry??? Not Splash now, yep?

        score = 0
        lines = 0
    }

    private def play() { gameState = Running; resetGame() }

    private def setGameOverState() { gameState = GameOver }

    // Game logic procedures

    private def getStartFallingX: Int = (FIELD_X_BLOCKS_NUM / 2).toInt - 1
    private def getStartFallingY: Int = FIELD_Y_BLOCKS_NUM - 1

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
            score += linesToDrop.size * SCORE_PER_CLEARED_LINE

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

//            println("Move Lines: " + linesToMove)

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

//                    score += lastMove._2 * SCORE_PER_FALLING_LINE
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
        if (getCurrentTime - lastTime >= FAST_FALLING_TICK_TIME) {
            updateLastTime()
            tick()
        }
    }

    // Draw procedures

    private def drawFieldBorder() {
        drawRect(FIELD_X_START, FIELD_Y_START, FIELD_WIDTH, FIELD_HEIGHT, BLOCK_COLOR)
    }

    private def drawBlock(xNum: Int, yNum: Int) {
        val x = xNum * (BIG_BLOCK_SIZE + BLOCK_MARGING) + FIELD_X_START + FIELD_X_PADDING
        val y = yNum * (BIG_BLOCK_SIZE + BLOCK_MARGING) + FIELD_Y_START + FIELD_Y_PADDING

        drawRect(x, y, BIG_BLOCK_SIZE, BIG_BLOCK_SIZE, BLOCK_COLOR)
        fillRect(x + BLOCKS_DIFF_PLACE, y + BLOCKS_DIFF_PLACE,
            SMALL_BLOCK_SIZE, SMALL_BLOCK_SIZE, BLOCK_COLOR)
    }

    override def preRenderCount {
        if (gameState == Running && getCurrentTime - lastTime >= tickTime) {
            updateLastTime()
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
        }

        if (gameState == Running || gameState == Paused) {
            // Draw falling element
            if (currElement != null) {
                var elX, elY = 0
                for ((x, y) <- currElement.blocks) {
                    elX = currElementX + x
                    elY = currElementY + y

                    if (0 <= elX && elX < FIELD_X_BLOCKS_NUM
                            && 0 <= elY && elY < FIELD_Y_BLOCKS_NUM) drawBlock(elX, elY)
                }
            }

            // Draw next element
            var elX, elY = 0
            for ((x, y) <- nextElement.blocks) {
                elX = NEXT_ELEMENT_SHOW_X_IN_BLOCKS + x
                elY = NEXT_ELEMENT_SHOW_Y_IN_BLOCKS + y

                drawBlock(elX, elY)
            }

            // Draw score
//            println("Score: " + score + " Lines: " + lines)
        } else if (gameState == GameOver) {
            // Draw "Game Over!" notification
            // Mock
            val rectWidth = 200
            val rectHeight = 150

            val rectX = ((displayWidth - rectWidth) / 2).toInt
            val rectY = ((displayHeight - rectHeight) / 2).toInt
            fillRect(rectX, rectY, rectWidth, rectHeight, Color.white)
            fillRect(rectX + 5, rectY + 5, rectWidth - 10, rectHeight - 10, Color.lightGray)
        } else if (gameState == Splash) {
            // Why it isn't drawing from 0:0 ?
//            drawImage(SPLASH_IMAGE_X, SPLASH_IMAGE_Y - 32, SPLASH_IMAGE.getTextureWidth,
//                SPLASH_IMAGE.getTextureHeight, SPLASH_IMAGE)
            beginTextDrawing()
                drawText(PRESS_SPACE_TO_START_MSG_X, PRESS_SPACE_TO_START_MSG_Y,
                    PRESS_SPACE_TO_START_MSG)
            endTextDrawing()
        }
    }

    // Input procedures

    override def detectInput {
        gameState match {
            case Running => {
                if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                    fallFast()

                while (Keyboard.next && Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_UP =>
                            if (canRotateCurrElementRight) rotateCurrElementRight()
                        case Keyboard.KEY_LEFT =>
                            if (Keyboard.getEventKeyState&& canMoveCurrElementLeft)
                                moveCurrElementLeft()
                        case Keyboard.KEY_RIGHT =>
                            if (Keyboard.getEventKeyState && canMoveCurrElementRight)
                                moveCurrElementRight()
                        case Keyboard.KEY_SPACE =>
                            dropCurrElementDown()
                        case Keyboard.KEY_R =>
                            if (Keyboard.getEventKeyState) resetGame()
                        case _ => {}
                    }
                }
            }
            case Paused => {}
            case GameOver => {
                while (Keyboard.next && Keyboard.getEventKeyState) {
                    Keyboard.getEventKey match {
                        case Keyboard.KEY_R =>
                            if (Keyboard.getEventKeyState) resetGame()
                        case _ => {}
                    }
                }
            }
            case Splash => {
                while (Keyboard.next) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                        play()
                }
            }
        }
    }

    override def loadResources() {
        SPLASH_IMAGE = TextureLoader.getTexture(SPLASH_IMAGE_FORMAT,
            ResourceLoader.getResourceAsStream(SPLASH_IMAGE_FILE))

        SPLASH_IMAGE_X = ((displayWidth - SPLASH_IMAGE.getImageWidth) / 2).toInt
        SPLASH_IMAGE_Y = ((displayHeight - SPLASH_IMAGE.getImageHeight) / 2).toInt

        PRESS_SPACE_TO_START_MSG_X =
                ((displayWidth - GraphicsToolkit.DEFAULT_FONT.getWidth(PRESS_SPACE_TO_START_MSG))
                        / 2).toInt
        PRESS_SPACE_TO_START_MSG_Y = 2 * GraphicsToolkit.DEFAULT_FONT.getLineHeight
    }
}