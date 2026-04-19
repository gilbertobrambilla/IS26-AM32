package it.polimi.ingsw.am32.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.dao.DatabaseConnection;
import it.polimi.ingsw.am32.network.rmi.RMIServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

public class ServerMain {

    private static int rmiPort = 1099;
    private static final String SERVER_NAME = "MesosServer";

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                rmiPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("[SERVER] Porta non valida inserita negli argomenti. Uso il default: " + rmiPort);
            }
        }

        if (args.length > 1) {
            System.setProperty("java.rmi.server.hostname", args[1]);
            System.out.println("[SERVER] RMI Hostname forzato a: " + args[1]);
        }

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

            System.out.println("[SERVER] Starting RMI Registry on port " + rmiPort + "...");
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind(SERVER_NAME, rmiServer);

            System.out.println("[SERVER] === SERVER READY AND LISTENING ===");

        } catch (Exception e) {
            System.err.println("[SERVER] Critical error during Server startup!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}