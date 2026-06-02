package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

/**
 * The quick help guide display is handled.
 */
public class HelpHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // help message is retrieved from the dictionary and logged
        String helpMsg = GameDictionary.getInstance().translate("HELP_MESSAGE");
        LogManager.getInstance().log(LogLevel.INFO, helpMsg);
    }

    @Override
    public String getSyntax() {
        // syntax string is returned
        return "help";
    }

    @Override
    public String getDescription() {
        // description string is returned
        return "shows game rules summary";
    }
}
