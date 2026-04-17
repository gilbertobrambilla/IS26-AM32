package it.polimi.ingsw.am32.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.dao.DatabaseConnection;
import it.polimi.ingsw.am32.network.rmi.RMIServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

public class ServerMain {

    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "MesosServer";

    public static void main(String[] args) {
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "5000");

        try {
            System.out.println("[SERVER] Initializing...");
            System.out.println("[SERVER] Checking Database connection...");

            try (Connection testConn = DatabaseConnection.getInstance().getConnection()) {
                if (testConn != null && !testConn.isClosed()) {
                    System.out.println("[SERVER] Database connection successfully established!");
                }
            } catch (Exception dbException) {
                System.err.println("[SERVER] FATAL ERROR: Cannot reach the Database!");
                dbException.printStackTrace();
                System.exit(1);
            }

            LobbyController lobbyController = new LobbyController();
            RMIServerImpl rmiServer = new RMIServerImpl(lobbyController);

            System.out.println("[SERVER] Starting RMI Registry on port " + RMI_PORT + "...");
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(SERVER_NAME, rmiServer);

            System.out.println("[SERVER] === SERVER READY AND LISTENING ===");

        } catch (Exception e) {
            System.err.println("[SERVER] Critical error during Server startup!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}