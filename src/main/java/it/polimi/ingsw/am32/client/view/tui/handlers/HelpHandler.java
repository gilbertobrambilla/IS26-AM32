package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

/**
 * The quick help guide display is handled.
 */
public class HelpHandler implements InputHandler {

    /**
     * The help command is executed.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // help message is retrieved from the dictionary and logged
        String helpMsg = GameDictionary.getInstance().translate("HELP_MESSAGE");
        LogManager.getInstance().log(LogLevel.INFO, helpMsg);
    }

    /**
     * The command syntax is returned.
     *
     * @return the command syntax
     */
    @Override
    public String getSyntax() {
        // syntax string is returned
        return "help";
    }

    /**
     * The command description is returned.
     *
     * @return the command description
     */
    @Override
    public String getDescription() {
        // description string is returned
        return "shows game rules summary";
    }
}
