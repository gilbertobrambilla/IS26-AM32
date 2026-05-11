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
 * Proxy representing the remote server connected via socket.
 */
public class SocketServerProxy implements ServerNode {
    private final String host;
    private final int port;
    private VirtualServer virtualServer;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Object outLock = new Object();
    private final AtomicBoolean connected;

    /**
     * Constructs a new socket server proxy.
     *
     * @param host The server host ip.
     * @param port The server port.
     */
    public SocketServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = new AtomicBoolean(false);
    }

    /**
     * Sets the virtual server reference.
     *
     * @param virtualServer The virtual server to set.
     */
    public void setVirtualServer(VirtualServer virtualServer) {
        // virtual server reference is updated
        this.virtualServer = virtualServer;
    }

    @Override
    public void connect(String nickname) {
        try {
            // socket is established
            socket = new Socket(host, port);
            // output stream is created
            out = new ObjectOutputStream(socket.getOutputStream());
            // stream is flushed to prevent deadlock
            out.flush();
            // input stream is created
            in = new ObjectInputStream(socket.getInputStream());
            // connection state is updated
            connected.set(true);

            // listener thread is started
            startListenerThread();

            // connect command is sent
            sendCommand(new SocketConnectMessage(nickname));
        } catch (IOException e) {
            // network exception is thrown
            throw new NetworkException("Unable to connect to socket server", e);
        }
    }

    private void startListenerThread() {
        Thread listener = new Thread(() -> {
            while (connected.get()) {
                try {
                    // message is read from stream
                    ServerToClientMessage message = (ServerToClientMessage) in.readObject();
                    // message is executed
                    message.execute(this);
                } catch (IOException | ClassNotFoundException e) {
                    // connection with server loss is caught silently
                    // connection state is updated
                    connected.set(false);
                    if (virtualServer != null) {
                        virtualServer.handleServerDisconnect();
                    }
                    break;
                }
            }
        });
        // thread is set as daemon
        listener.setDaemon(true);
        // thread is started
        listener.start();
    }

    /**
     * Processes a received message dto by forwarding it to the virtual server.
     *
     * @param dto The received message dto.
     */
    public void processMessageDTO(MessageDTO dto) {
        if (virtualServer != null) {
            // message is forwarded to virtual server
            virtualServer.receiveMessage(dto);
        }
    }

    /**
     * Processes a ping request from the server.
     */
    public void processPing() {
        if (this.virtualServer != null) {
            this.virtualServer.notifyPingReceived();
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        // lobby command is sent
        sendCommand(new SocketLobbyMessage(command));
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        // game command is sent
        sendCommand(new SocketGameMessage(command));
    }

    private void sendCommand(ClientToServerMessage message) {
        synchronized (outLock) {
            try {
                if (connected.get()) {
                    // message is written to stream
                    out.writeObject(message);
                    // stream is flushed
                    out.flush();
                    // stream is reset
                    out.reset();
                }
            } catch (IOException e) {
                // connection state is updated
                connected.set(false);
                // network exception is thrown
                throw new NetworkException("Socket write error", e);
            }
        }
    }

    @Override
    public void ping() {
        // ping message is sent
        sendCommand(new SocketPingMessage());
    }

    @Override
    public void disconnect() {
        // disconnection and resource cleanup are handled silently
        // connection state is updated
        this.connected.set(false);
        try {
            if (socket != null && !socket.isClosed()) {
                // socket is closed
                socket.close();
            }
        } catch (IOException e) {
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Socket closure error: failed to close connection to server cleanly.");
        }
    }
}
