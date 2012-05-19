package ua.org.dector.lwsge

/**
 * @author dector (dector9@gmail.com)
 */

object GameController {
    private var app: LWSGEApp = null

    def setApp(controlledApp: LWSGEApp) { app = controlledApp }

    def executeCommand(command: GameCommand) { command.action() }

    def exit() { app.exit() }
}
