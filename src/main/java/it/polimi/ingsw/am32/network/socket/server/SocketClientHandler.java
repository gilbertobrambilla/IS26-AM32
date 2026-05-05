package it.polimi.ingsw.am32.network.socket.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.server.VirtualViewImpl;
import it.polimi.ingsw.am32.network.socket.messages.ClientToServerMessage;
import it.polimi.ingsw.am32.network.socket.messages.SocketDTOMessage;
import it.polimi.ingsw.am32.network.socket.messages.SocketPingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientHandler implements Runnable, ClientNode {

    private final Socket socket;
    private final LobbyController lobbyController;
    private ObjectOutputStream out;
    private final Object outLock = new Object();

    private VirtualViewImpl virtualView;
    private String nickname;
    private boolean connected;

    public SocketClientHandler(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (connected) {
                ClientToServerMessage message = (ClientToServerMessage) in.readObject();
                message.execute(this);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[SOCKET HANDLER] Disconnection detected for: " + nickname);
        } finally {
            disconnect();
        }
    }

    public void processConnect(String nickname) {
        this.nickname = nickname;
        if (this.virtualView == null) {
            this.virtualView = VirtualViewImpl.create(nickname, this, (isNetworkDrop) -> {
                if (connected) {
                    connected = false;
                    try { socket.close(); } catch (IOException ignored) {}
                }
                if (isNetworkDrop) {
                    lobbyController.handlePlayerNetworkDrop(nickname);
                }
            });
        }
        lobbyController.connectPlayer(nickname, this.virtualView);
    }

    public void processLobbyCommand(LobbyCommand command) {
        lobbyController.handleLobbyCommand(command, this.nickname);
    }

    public void processGameCommand(GameCommand command) {
        lobbyController.handleGameCommand(command, this.nickname);
    }

    public void processPing() {
    }

    @Override
    public void sendMessage(MessageDTO message) {
        sendMessageToSocket(new SocketDTOMessage(message));
    }

    @Override
    public void ping() {
        sendMessageToSocket(new SocketPingMessage());
    }

    private void sendMessageToSocket(Object obj) {
        synchronized (outLock) {
            try {
                if (connected) {
                    out.writeObject(obj);
                    out.flush();
                    out.reset();
                }
            } catch (IOException e) {
                throw new NetworkException("Socket send error", e);
            }
        }
    }

    private void disconnect() {
        if (!connected) return;

        if (virtualView != null) {
            virtualView.handleDisconnect();
        } else {
            connected = false;
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
