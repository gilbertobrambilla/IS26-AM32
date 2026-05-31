package it.polimi.ingsw.am32.network.socket.client;

import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.socket.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A remote server connected via socket is represented by a proxy.
 */
public class SocketServerProxy implements ServerNode {

    private final String host;
    private final int port;
    private final Object outLock = new Object();
    private final AtomicBoolean connected;

    private VirtualServer virtualServer;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * A new socket server proxy is constructed.
     *
     * @param host the server host ip
     * @param port the server port
     */
    public SocketServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = new AtomicBoolean(false);
    }

    @Override
    public void connect() {
        try {
            if (!connected.get() || socket == null || socket.isClosed()) {
                // socket established
                socket = new Socket(host, port);
                // output stream created
                out = new ObjectOutputStream(socket.getOutputStream());
                // stream flushed to prevent deadlock
                out.flush();
                // input stream created
                in = new ObjectInputStream(socket.getInputStream());
                // connection state updated
                connected.set(true);

                // listener thread started
                startListenerThread();
            }

            // connect command sent
            sendCommand(new SocketConnectMessage());
        } catch (IOException e) {
            // network exception thrown
            throw new NetworkException("unable to connect to socket server", e);
        }
    }

    @Override
    public void login(String nickname) {
        // login command sent
        sendCommand(new SocketLoginMessage(nickname));
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        // lobby command sent
        sendCommand(new SocketLobbyMessage(command));
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        // game command sent
        sendCommand(new SocketGameMessage(command));
    }

    @Override
    public void sendPingToServer() {
        // ping message sent
        sendCommand(new SocketPingMessage());
    }

    @Override
    public void disconnect() {
        // disconnection and resource cleanup handled silently
        // connection state updated
        this.connected.set(false);
        try {
            if (socket != null && !socket.isClosed()) {
                // socket closed
                socket.close();
            }
        } catch (IOException e) {
            // socket closure error logged
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Socket closure error: failed to close connection to server cleanly.");
        }
    }

    /**
     * The virtual server reference is set.
     *
     * @param virtualServer the virtual server to set
     */
    public void setVirtualServer(VirtualServer virtualServer) {
        // virtual server reference updated
        this.virtualServer = virtualServer;
    }

    /**
     * Ping received from the server processed.
     */
    public void onPingReceivedFromServer() {
        if (this.virtualServer != null) {
            // ping received processed
            this.virtualServer.onPingReceivedFromServer();
        }
    }

    /**
     * Received dto processed by forwarding it to the virtual server.
     *
     * @param dto the received message dto
     */
    public void processMessageDTO(MessageDTO dto) {
        if (virtualServer != null) {
            // message forwarded to virtual server
            virtualServer.receiveMessage(dto);
        }
    }

    /**
     * Listener thread started to continuously read messages from the socket.
     */
    private void startListenerThread() {
        Thread listener = new Thread(() -> {
            while (connected.get()) {
                try {
                    // message read from stream
                    ServerToClientMessage message = (ServerToClientMessage) in.readObject();
                    // message executed
                    message.execute(this);
                } catch (IOException | ClassNotFoundException e) {
                    // connection with server loss caught silently
                    // connection state updated
                    connected.set(false);
                    if (virtualServer != null) {
                        virtualServer.handleServerDisconnect();
                    }
                    break;
                }
            }
        });
        // thread set as daemon
        listener.setDaemon(true);
        // thread started
        listener.start();
    }

    /**
     * A command is sent to the server over the socket connection.
     *
     * @param message the message to be sent
     */
    private void sendCommand(ClientToServerMessage message) {
        synchronized (outLock) {
            try {
                if (connected.get()) {
                    // message written to stream
                    out.writeObject(message);
                    // stream flushed
                    out.flush();
                    // stream reset
                    out.reset();
                }
            } catch (IOException e) {
                // connection state updated
                connected.set(false);
                // network exception thrown
                throw new NetworkException("Socket write error", e);
            }
        }
    }
}
