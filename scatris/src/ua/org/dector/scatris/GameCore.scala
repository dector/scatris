package ua.org.dector.scatris

import org.newdawn.slick.opengl.{Texture, TextureLoader}
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.Color

import states._
import util.Random
import collection.mutable.ArrayBuffer

import ua.org.dector.lwsge._
import common.Config
import time.TimerManager
import state.StateManager
import ScatrisConstants._

/**
 * @author dector (dector9@gmail.com)
 */

object GameCore {
    val TICK_TIMER = "Tick Timer"
    val LEFT_MOVE_TIMER = "Left Move Timer"
    val RIGHT_MOVE_TIMER = "Right Move Timer"

    val field = new GameField(Config.i(FIELD_X_BLOCKS_NUM),
        Config.i(FIELD_Y_BLOCKS_NUM))

    private var _tickTimeBound = Config.i(STARTING_TICK_TIME)
    def tickTimeBound = _tickTimeBound
    private def tickTimeBound_= (value: Int) {_tickTimeBound = value}

    private val elementsPool =
        Array(new Stick, new Block, new RZip, new LZip, new G, new Seven, new T)
    var nextElement = getNextFallingElement

    private var _currElement = getNextFallingElement
    def currElement = _currElement
    private def currElement_= (newElement: Element) {_currElement = newElement}

    private var _currElementX = getStartFallingX
    def currElementX = _currElementX
    private def currElementX_= (newElementX: Int) {_currElementX = newElementX}

    private var _currElementY = getStartFallingY
    def currElementY = _currElementY
    private def currElementY_= (newElementY: Int) {_currElementY = newElementY}

    private var _score = 0
    def score = _score
    private def score_= (value: Int) {_score = value}

    var _lines = 0
    def lines = _lines
    private def lines_= (value: Int) {_lines = value}

    /* *** Getters *** */

    private def getStartFallingX: Int = Config.i(FIELD_X_BLOCKS_NUM) / 2 - 1
    private def getStartFallingY: Int = Config.i(FIELD_Y_BLOCKS_NUM) - 1

    /* *** Next element generator *** */

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

    /* *** Post-fallen processing *** */

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

    /* *** Moving *** */

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

    private def moveCurrElementDown() {currElementY -= 1}
    private def moveCurrElementLeft() {currElementX -= 1}
    private def moveCurrElementRight() {currElementX += 1}

    private def moveCurrElementLeftByTimer() {
        if (TimerManager(LEFT_MOVE_TIMER).time >=
                Config.i(LEFT_MOVE_TIME_BOUND)) {
            GameCore.moveCurrElementLeft()
            TimerManager(LEFT_MOVE_TIMER).restart()
        }
    }

    private def moveCurrElementRightByTimer() {
        if (TimerManager(RIGHT_MOVE_TIMER).time >=
                Config.i(RIGHT_MOVE_TIME_BOUND)) {
            GameCore.moveCurrElementRight()
            TimerManager(RIGHT_MOVE_TIMER).restart()
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

    /* *** Rotating *** */

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

    /* *** Public interface *** */

    def tryMoveCurrElementLeftByTimer() {
        if (GameCore.canMoveCurrElementLeft) moveCurrElementLeftByTimer
    }

    def tryMoveCurrElementRightByTimer() {
        if (GameCore.canMoveCurrElementRight) moveCurrElementRightByTimer
    }

    def tryRotateCurrElementRight() {
        if (canRotateCurrElementRight) rotateCurrElementRight()
    }

    def fallDownFast() {
        if (TimerManager(TICK_TIMER).time >=
                tickTimeBound * Config.f(FAST_FALLING_TICK_COEF)) {
            GameCore.tick()
            TimerManager(TICK_TIMER).restart()
        }
    }

    def dropCurrElementDown() {
        while (canMoveCurrElementDown) moveCurrElementDown()
        processFallenElement()
    }

    def tick() {
        if (currElement != null && canMoveCurrElementDown)
            moveCurrElementDown()
        else
            processFallenElement()
    }

    def reset() {
        field.clear()
        generateNextFallingElement()

        tickTimeBound = Config.i(STARTING_TICK_TIME)

        score = 0
        lines = 0
    }
}