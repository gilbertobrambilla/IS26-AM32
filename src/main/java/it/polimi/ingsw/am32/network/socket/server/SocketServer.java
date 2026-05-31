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
    private final ConcurrentHashMap<String, NetworkNode> activeNodes;
    private final ExecutorService threadPool;

    private ServerSocket serverSocket;

    /**
     * New socket server instance constructed.
     *
     * @param port the port to listen on
     * @param lobbyController the controller managing the lobby logic
     */
    public SocketServer(int port, LobbyController lobbyController) {
        this.port = port;
        this.lobbyController = lobbyController;
        this.running = new AtomicBoolean(true);
        // map for active nodes initialized
        this.activeNodes = new ConcurrentHashMap<>();
        // cached thread pool created
        this.threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            // server socket created
            serverSocket = new ServerSocket(port);

            while (running.get()) {
                // client connection accepted
                Socket clientSocket = serverSocket.accept();
                // client connection logged
                LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Connection accepted from: " + clientSocket.getInetAddress());

                // client proxy instantiated
                SocketClientProxy proxy = new SocketClientProxy(clientSocket, lobbyController, activeNodes);
                // proxy task submitted to thread pool
                threadPool.submit(proxy);
            }
        } catch (IOException e) {
            if (running.get()) {
                // critical server error logged
                LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Critical socket server error.", e);
            } else {
                // server shutdown logged
                LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Server shut down gracefully.");
            }
        }
    }

    /**
     * The server is stopped and the socket is closed.
     */
    public void stopServer() {
        // running flag updated
        this.running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                // server socket closed
                serverSocket.close();
            }
            // thread pool shut down
            threadPool.shutdownNow();
        } catch (IOException e) {
            // server socket closure failure is logged
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] Server socket could not be closed.", e);
        }
    }
}
