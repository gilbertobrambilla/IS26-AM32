package it.polimi.ingsw.am32.network.socket.client;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.MessageDispatcher;
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.socket.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServerConnection implements ServerNode {
    private final String host;
    private final int port;
    private final MessageDispatcher messageDispatcher;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Object outLock = new Object();
    private boolean connected;

    public SocketServerConnection(String host, int port, MessageDispatcher messageDispatcher) {
        this.host = host;
        this.port = port;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void connect(String nickname) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            startListenerThread();

            sendCommand(new SocketConnectMessage(nickname));

        } catch (IOException e) {
            throw new NetworkException("Unable to connect to socket server", e);
        }
    }

    private void startListenerThread() {
        Thread listener = new Thread(() -> {
            while (connected) {
                try {
                    ServerToClientMessage message = (ServerToClientMessage) in.readObject();
                    message.execute(this);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("[SOCKET CLIENT] Connection with server lost.");
                    connected = false;
                    break;
                }
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    public void processMessageDTO(MessageDTO dto) {
        dto.accept(messageDispatcher);
    }

    public void processPing() {
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) {
        sendCommand(new SocketLobbyMessage(command));
    }

    @Override
    public void sendGameCommand(GameCommand command, String nickname) {
        sendCommand(new SocketGameMessage(command));
    }

    private void sendCommand(ClientToServerMessage message) {
        synchronized (outLock) {
            try {
                if (connected) {
                    out.writeObject(message);
                    out.flush();
                    out.reset();
                }
            } catch (IOException e) {
                connected = false;
                throw new NetworkException("Socket write error", e);
            }
        }
    }

    @Override
    public void ping() {
        sendCommand(new SocketPingMessage());
    }

    @Override
    public void disconnect() {
        System.out.println("[SOCKET CLIENT] Disconnecting and cleaning up resources...");
        this.connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing client socket: " + e.getMessage());
        }
    }
}
