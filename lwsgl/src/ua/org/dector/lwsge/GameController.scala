package ua.org.dector.lwsge

import common.Config
import LWSGEConstants._

/**
 * @author dector (dector9@gmail.com)
 */

// TODO: Delete it?
object GameController {
    private var app: LWSGEApp = null

    def setApp(controlledApp: LWSGEApp) { app = controlledApp }

    def executeCommand(command: GameCommand) { command.action() }

    def trySwitchConsole() {
        if (Config.bool(CONSOLE_ENABLED)) {
            if (Config.bool(CONSOLE_OPENED))
                app.closeConsole()
            else
                app.openConsole()
        }
    }

    def exit() { app.exit() }
}
