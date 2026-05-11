package it.polimi.ingsw.am32.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.dao.DatabaseConnection;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.rmi.server.RMIServerDispatcher;
import it.polimi.ingsw.am32.network.socket.server.SocketServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

/**
 * Main entry point for the server application.
 */
public class ServerMain {
    private static final int rmiPort = 1111;
    private static final int socketPort = 2222;
    private static final String SERVER_NAME = "MesosServer";

    /**
     * Server application is launched.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            // rmi host property is set
            System.setProperty("java.rmi.server.hostname", args[0]);
            // host configuration is logged
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] RMI Hostname forced to: " + args[0]);
        }

        // rmi timeouts are configured
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "5000");

        try {
            // initialization start is logged
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Initializing...");
            LogManager.getInstance().log(LogLevel.INFO, "[DATABASE] Checking connection...");

            try (Connection testConn = DatabaseConnection.getInstance().getConnection()) {
                if (testConn != null && !testConn.isClosed()) {
                    // database connection success is logged
                    LogManager.getInstance().log(LogLevel.INFO, "[DATABASE] Connection successfully established!");
                }
            } catch (Exception dbException) {
                // database connection failure is logged
                LogManager.getInstance().log(LogLevel.ERROR, "[DATABASE] FATAL ERROR: Cannot reach the Database!");
                dbException.printStackTrace();
                System.exit(1);
            }

            // lobby controller is instantiated
            LobbyController lobbyController = new LobbyController();

            // rmi server is instantiated and registered
            RMIServerDispatcher rmiServer = new RMIServerDispatcher(lobbyController);

            // rmi startup is logged with listening status
            LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Starting RMI Registry and listening on port " + rmiPort + "...");
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind(SERVER_NAME, rmiServer);

            // socket startup is logged with listening status synchronously
            LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Starting Socket Server and listening on port " + socketPort + "...");
            // socket server is instantiated and started
            SocketServer socketServer = new SocketServer(socketPort, lobbyController);
            socketServer.start();

            // shutdown hook is added for clean exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // shutdown sequence is logged
                LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Shutting down services...");
                // resources are cleaned up
                socketServer.stopServer();
            }));

            // final ready state is guaranteed to be logged last
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] === SERVER READY AND LISTENING ===");

        } catch (Exception e) {
            // critical startup error is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Critical error during Server startup!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
