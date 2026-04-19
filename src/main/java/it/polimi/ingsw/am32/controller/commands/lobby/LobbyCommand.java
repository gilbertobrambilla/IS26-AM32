package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

import java.io.Serializable;

public interface LobbyCommand extends Serializable {
    void execute(LobbyController lobby, VirtualView view, String sessionNickname);
}
