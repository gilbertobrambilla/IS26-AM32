package it.polimi.ingsw.am32.network.socket.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.server.RemoteClientView;
import it.polimi.ingsw.am32.network.server.NetworkNode;
import it.polimi.ingsw.am32.network.socket.messages.ClientToServerMessage;
import it.polimi.ingsw.am32.network.socket.messages.SocketDTOMessage;
import it.polimi.ingsw.am32.network.socket.messages.SocketPingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A remote client connected via socket is represented by a proxy.
 */
public class SocketClientProxy implements Runnable, ClientNode {

    private final Socket socket;
    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, NetworkNode> activeNodes;
    private final Object outLock = new Object();
    private final AtomicBoolean connected;

    private ObjectOutputStream out;
    private RemoteClientView virtualView;
    private String connectionID;

    /**
     * A new socket client proxy is constructed.
     *
     * @param socket the client socket
     * @param lobbyController the lobby controller instance
     * @param activeNodes the map containing all active network nodes
     */
    public SocketClientProxy(Socket socket, LobbyController lobbyController, ConcurrentHashMap<String, NetworkNode> activeNodes) {
        this.socket = socket;
        this.lobbyController = lobbyController;
        this.activeNodes = activeNodes;
        this.connected = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        try {
            // output stream created
            out = new ObjectOutputStream(socket.getOutputStream());
            // stream flushed to prevent deadlock
            out.flush();
            // input stream created
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (connected.get()) {
                // message read from stream
                ClientToServerMessage message = (ClientToServerMessage) in.readObject();
                // message executed
                message.execute(this);
            }
        } catch (IOException | ClassNotFoundException e) {
            // client disconnection logged
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Disconnection detected for connection: " + connectionID);
        } finally {
            // disconnect handled
            disconnect();
        }
    }

    @Override
    public void sendPingToClient() {
        // ping message sent
        sendMessageToSocket(new SocketPingMessage());
    }

    @Override
    public void sendMessage(MessageDTO message) {
        // dto message wrapped and sent
        sendMessageToSocket(new SocketDTOMessage(message));
    }

    /**
     * Connection process initialized and a connection result notified.
     */
    public void processConnect() {
        // unique connection identifier generated locally
        String currentConnectionID = UUID.randomUUID().toString();
        this.connectionID = currentConnectionID;

        if (this.virtualView != null) {
            // heartbeat of old view stopped
            this.virtualView.stopHeartbeat();
        }

        // virtual view created
        this.virtualView = RemoteClientView.create(this, currentConnectionID, (isNetworkDrop) -> {
            // node removed from active nodes map
            this.activeNodes.remove(currentConnectionID);

            if (isNetworkDrop) {
                if (connected.get()) {
                    // connection state updated
                    connected.set(false);
                    try {
                        // socket closed
                        socket.close();
                    } catch (IOException e) {
                        // socket closure error logged
                        LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Socket closure error: connection already closed by peer");
                    }
                }
                // network drop handled by controller
                lobbyController.processPlayerNetworkDrop(currentConnectionID);
            }
        });

        // virtual view added to active nodes map
        this.activeNodes.put(currentConnectionID, this.virtualView);

        // connection result notified to the client
        this.virtualView.notifyConnectionResult(new ConnectionResultEvent(true, "connection successful", currentConnectionID));
    }

    /**
     * Login request received from the client processed.
     *
     * @param nickname the requested nickname
     */
    public void onLoginRequest(String nickname) {
        // login processed by controller
        lobbyController.processLogin(nickname, this.virtualView);
    }

    /**
     * Lobby command received from the client processed.
     *
     * @param command the lobby command to process
     */
    public void processLobbyCommand(LobbyCommand command) {
        // lobby command forwarded to controller
        lobbyController.processLobbyCommand(command, this.connectionID);
    }

    /**
     * Game command received from the client processed.
     *
     * @param command the game command to process
     */
    public void processGameCommand(GameCommand command) {
        // game command forwarded to controller
        lobbyController.processGameCommand(command, this.connectionID);
    }

    /**
     * Ping received from the client processed.
     */
    public void onPingReceivedFromClient() {
        if (this.virtualView != null) {
            // ping received processed
            this.virtualView.onPingReceivedFromClient();
        }
    }

    /**
     * An object is sent to the client over the socket connection.
     *
     * @param obj the object to be sent
     */
    private void sendMessageToSocket(Object obj) {
        synchronized (outLock) {
            try {
                if (connected.get()) {
                    // object written to stream
                    out.writeObject(obj);
                    // stream flushed
                    out.flush();
                    // stream reset
                    out.reset();
                }
            } catch (IOException e) {
                // network exception thrown
                throw new NetworkException("Socket send error", e);
            }
        }
    }

    /**
     * The connection is disconnected and resources are cleaned up.
     */
    private void disconnect() {
        if (!connected.get()) return;

        if (virtualView != null) {
            // disconnect handled by virtual view
            virtualView.handleDisconnect();
        } else {
            // connection state updated
            connected.set(false);
            try {
                // socket closed
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
