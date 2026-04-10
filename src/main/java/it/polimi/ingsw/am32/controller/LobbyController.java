package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.GameResult;

import java.util.concurrent.ConcurrentMap;

public class LobbyController {
    private ConcurrentMap<Integer, GameController> activeGames;
    private ConcurrentMap<String, VirualView> connectedPlayers;
    private ConcurrentMap<String, GameController> playerGameMap;
    private int nextGameId;
    private GameResultRepository dbRepository;

    public void setConnectedPlayers(String nickname, VirtualView view){

    }

    public void createGame(String nickname, int numOfPlayers){

    }

    public void joinGame(String nickname, int gameId){

    }

    public void handlePlayerNetworkDrop(String nickname){

    }

    public void endGame(int GameId, GameResult result){

    }

    private void broadcastLobbyState(){

    }

}
