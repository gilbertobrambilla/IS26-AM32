package it.polimi.ingsw.am32.controller;

public interface GameCommand {
    public void execute(LobbyController lobby, LobbyView view, String sessionNickname);

}
