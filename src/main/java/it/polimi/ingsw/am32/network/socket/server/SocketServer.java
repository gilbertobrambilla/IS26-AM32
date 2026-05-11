package it.polimi.ingsw.am32.network.socket.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.server.NetworkNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Socket server that listens for incoming connections and manages active nodes.
 */
public class SocketServer extends Thread {
    private final int port;
    private final LobbyController lobbyController;
    private final AtomicBoolean running;
    private ServerSocket serverSocket;
    private final ConcurrentHashMap<String, NetworkNode> activeNodes;
    private final ExecutorService threadPool;

    /**
     * Constructs a new socket server instance.
     *
     * @param port The port to listen on.
     * @param lobbyController The controller managing the lobby logic.
     */
    public SocketServer(int port, LobbyController lobbyController) {
        this.port = port;
        this.lobbyController = lobbyController;
        this.running = new AtomicBoolean(true);
        // map for active nodes is initialized
        this.activeNodes = new ConcurrentHashMap<>();
        // cached thread pool is created
        this.threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            // server socket is created
            serverSocket = new ServerSocket(port);

            while (running.get()) {
                // client connection is accepted
                Socket clientSocket = serverSocket.accept();
                // client connection is logged
                LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Connection accepted from: " + clientSocket.getInetAddress());

                // client proxy is instantiated
                SocketClientProxy proxy = new SocketClientProxy(clientSocket, lobbyController, activeNodes);
                // proxy task is submitted to thread pool
                threadPool.submit(proxy);
            }
        } catch (IOException e) {
            if (running.get()) {
                // critical server error is logged
                LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Critical error: " + e.getMessage());
            } else {
                // server shutdown is logged
                LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Server shut down gracefully.");
            }
        }
    }

    /**
     * Stops the server and closes the socket.
     */
    public void stopServer() {
        // running flag is updated
        this.running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                // server socket is closed
                serverSocket.close();
            }
            // thread pool is shut down
            threadPool.shutdownNow();
        } catch (IOException e) {
            // error is printed
            System.err.println("Error while closing server socket: " + e.getMessage());
        }
    }
}
