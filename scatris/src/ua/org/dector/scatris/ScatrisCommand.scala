package ua.org.dector.scatris

/**
 * @author dector (dector9@gmail.com)
 */

object ScatrisCommand extends Enumeration {
    type ScatrisCommand = Value

    val FallFast                    = Value
    val moveCurrElementLeftByTimer  = Value
    val moveCurrElementRightByTimer = Value
    val rotateCurrElementRight      = Value
    val dropCurrElementDown         = Value
    val resetGame                   = Value
    val togglePause                 = Value

}
