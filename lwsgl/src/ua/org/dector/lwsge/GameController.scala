package ua.org.dector.lwsge

/**
 * @author dector (dector9@gmail.com)
 */

// TODO: Delete it?
object GameController {
    private var app: LWSGEApp = null

    def setApp(controlledApp: LWSGEApp) { app = controlledApp }

    def executeCommand(command: GameCommand) { command.action() }

    def exit() { app.exit() }
}
