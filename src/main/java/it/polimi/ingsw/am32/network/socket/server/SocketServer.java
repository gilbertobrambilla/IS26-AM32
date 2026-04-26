package it.polimi.ingsw.am32.network.socket.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {
    private final int port;
    private final LobbyController lobbyController;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public SocketServer(int port, LobbyController lobbyController) {
        this.port = port;
        this.lobbyController = lobbyController;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SOCKET SERVER] Listening on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SOCKET SERVER] Connection accepted from: " + clientSocket.getInetAddress());

                SocketClientHandler handler = new SocketClientHandler(clientSocket, lobbyController);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("[SOCKET SERVER] Critical error: " + e.getMessage());
            } else {
                System.out.println("[SOCKET SERVER] Server shut down gracefully.");
            }
        }
    }

    public void stopServer() {
        this.running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing server socket: " + e.getMessage());
        }
    }
}
