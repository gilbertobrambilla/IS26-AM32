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
 * Proxy representing a remote client connected via socket.
 */
public class SocketClientProxy implements Runnable, ClientNode {
    private final Socket socket;
    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, NetworkNode> activeNodes;

    private ObjectOutputStream out;
    private final Object outLock = new Object();

    private RemoteClientView virtualView;
    private String nickname;
    private String connectionID;
    private final AtomicBoolean connected;

    /**
     * Constructs a new socket client proxy.
     *
     * @param socket The client socket.
     * @param lobbyController The lobby controller instance.
     * @param activeNodes The map containing all active network nodes.
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
            // output stream is created
            out = new ObjectOutputStream(socket.getOutputStream());
            // stream is flushed to prevent deadlock
            out.flush();
            // input stream is created
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (connected.get()) {
                // message is read from stream
                ClientToServerMessage message = (ClientToServerMessage) in.readObject();
                // message is executed
                message.execute(this);
            }
        } catch (IOException | ClassNotFoundException e) {
            // client disconnection is logged
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Disconnection detected for: " + nickname);
        } finally {
            // disconnect is handled
            disconnect();
        }
    }

    /**
     * Processes a connection request from the client and establishes the virtual view.
     *
     * @param nickname The nickname of the connecting player.
     */
    public void processConnect(String nickname) {
        this.nickname = nickname;
        // unique connection identifier is generated locally
        String currentConnectionID = UUID.randomUUID().toString();
        this.connectionID = currentConnectionID;

        if (this.virtualView != null) {
            // heartbeat of old view is stopped
            this.virtualView.stopHeartbeat();
        }

        // virtual view is created with the connection id
        this.virtualView = RemoteClientView.create(nickname, this, currentConnectionID, (isNetworkDrop) -> {
            // node is removed from active nodes map
            this.activeNodes.remove(currentConnectionID);

            if (connected.get()) {
                // connection state is updated
                connected.set(false);
                try {
                    // socket is closed
                    socket.close();
                } // Inside SocketClientProxy.java
                catch (IOException e) {
                    LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Socket closure error for client " + nickname + ": connection already closed by peer.");
                }
            }
            if (isNetworkDrop) {
                // network drop is handled by controller using the connection id
                lobbyController.handlePlayerNetworkDrop(nickname, currentConnectionID);
            }
        });
        // virtual view is added to active nodes map
        this.activeNodes.put(currentConnectionID, this.virtualView);

        // player connection is delegated to controller
        ConnectionResultEvent result = lobbyController.connectPlayer(nickname, this.virtualView);

        // connection result is sent to client
        sendMessage(result);

        if (!result.isSuccess()) {
            // node is removed from active nodes
            this.activeNodes.remove(currentConnectionID);
            // heartbeat is stopped
            this.virtualView.stopHeartbeat();
            // virtual view is cleared for future retry
            this.virtualView = null;
        }
    }

    /**
     * Processes a lobby command received from the client.
     *
     * @param command The lobby command to process.
     */
    public void processLobbyCommand(LobbyCommand command) {
        // lobby command is forwarded to controller
        lobbyController.handleLobbyCommand(command, this.nickname);
    }

    /**
     * Processes a game command received from the client.
     *
     * @param command The game command to process.
     */
    public void processGameCommand(GameCommand command) {
        // game command is forwarded to controller
        lobbyController.handleGameCommand(command, this.nickname);
    }

    /**
     * Processes a ping message received from the client.
     */
    public void processPing() {
        if (this.virtualView != null) {
            // ping received is notified
            this.virtualView.notifyPingReceived();
        }
    }

    @Override
    public void sendMessage(MessageDTO message) {
        // dto message is wrapped and sent
        sendMessageToSocket(new SocketDTOMessage(message));
    }

    @Override
    public void ping() {
        // ping message is sent
        sendMessageToSocket(new SocketPingMessage());
    }

    private void sendMessageToSocket(Object obj) {
        synchronized (outLock) {
            try {
                if (connected.get()) {
                    // object is written to stream
                    out.writeObject(obj);
                    // stream is flushed
                    out.flush();
                    // stream is reset
                    out.reset();
                }
            } catch (IOException e) {
                // network exception is thrown
                throw new NetworkException("Socket send error", e);
            }
        }
    }

    private void disconnect() {
        if (!connected.get()) return;

        if (virtualView != null) {
            // disconnect is handled by virtual view
            virtualView.handleDisconnect();
        } else {
            // connection state is updated
            connected.set(false);
            try {
                // socket is closed
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
