package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

public interface ServerConnection {
    void ping();
    void connect(String nickname);
    void sendLobbyCommand(LobbyCommand command, String nickname);
    void sendGameCommand(GameCommand command, String nickname);
}