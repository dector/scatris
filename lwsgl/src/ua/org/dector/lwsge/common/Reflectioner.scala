package ua.org.dector.lwsge.common

import ua.org.dector.lwsge.console.LWSGEConsole

/**
 * @author dector (dector9@gmail.com)
 */

// Class created to hide reflection highlight errors

object Reflectioner {
    def setNewConfigValue(lArg: String, rArg: String) {
        if (Config contains lArg) {
            val oldValue = Config(lArg)
            val argClass = oldValue.getClass

            try {
                val newValue =
                    if (argClass == classOf[java.lang.Integer])
                        rArg.toInt
                    else if (argClass == classOf[java.lang.Boolean])
                        rArg.toBoolean
                    else if (argClass == classOf[java.lang.Float])
                        rArg.toFloat
                    else if (argClass == classOf[java.lang.Long])
                        rArg.toLong
                    else if (argClass == classOf[java.lang.String])
                        rArg
                    else { LWSGEConsole.println("Unknown value type " + argClass); null }

                if (newValue != null) Config(lArg) = newValue
            } catch {
                case _: NumberFormatException =>
                    LWSGEConsole.println("\"" + rArg + "\" is not number")
            }

        }
    }
}
