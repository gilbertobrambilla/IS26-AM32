package it.polimi.ingsw.am32.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class GameController {
    private int gameId;
    private GameState gameState;
    private GameLifecycleCallBack lifecycleCallBack;
    private Map<String, PlayerSession> activeClients:
    private BlockingQueue<Command> commandQueue;
    private boolean isSuspended;

    public void GameController(int gameId, GameState gameState, GameLifecycleCallBack callBack){

    }

    public void addPlayerToGame(String nickname, GameView view){

    }

    public void startGame(){

    }

    public void addCommandToQueue(GameCommand cmd){
    }

    public void run(){

    }

    protected void processPlaceTotem(String nickname, String tileId){

    }

    protected void processResolveAction(String nickname, List<String> characters, List<String> buildings){

    }

    protected void processDisconnect(String nickname){

    }

    protected void processReconnect(String nickname, GameVIew newView){

    }

    private void checkTurn(String nickname){

    }

    private void autoAdvancedGameIfNecessary(){

    }

    private void checkSuspendCondition(){

    }

    private void handleGameEnd(){

    }


}
