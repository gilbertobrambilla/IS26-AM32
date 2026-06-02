package it.polimi.ingsw.am32.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.dao.DatabaseConnection;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.rmi.server.RMIServerDispatcher;
import it.polimi.ingsw.am32.network.socket.server.SocketServer;
import it.polimi.ingsw.am32.network.utils.NetworkUtils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The server application entry point is represented.
 */
public class ServerMain {
    private static final int rmiPort = 1111;
    private static final int socketPort = 2222;
    private static final String SERVER_NAME = "MesosServer";

    /**
     * The server application is launched.
     */
    public static void main() {
        // scanner is instantiated
        Scanner scanner = new Scanner(System.in);

        // available ips are retrieved
        Map<String, String> availableIps = NetworkUtils.getAvailableIpv4Addresses();
        List<String> ipList = new ArrayList<>(availableIps.keySet());

        // prompt is printed
        System.out.println("Available network interfaces for hosting the server:");

        // options are printed
        for (int i = 0; i < ipList.size(); i++) {
            String ip = ipList.get(i);
            String name = availableIps.get(ip);
            System.out.println((i + 1) + ": " + ip + " (" + name + ")");
        }

        int customOption = ipList.size() + 1;
        System.out.println(customOption + ": Custom IP (Manual entry)");

        // selected ip is evaluated
        String selectedIp = "";
        while (selectedIp.isEmpty()) {
            System.out.print("Select an option: ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= ipList.size()) {
                    // ip from list is selected
                    selectedIp = ipList.get(choice - 1);
                } else if (choice == customOption) {
                    // custom ip is requested
                    System.out.print("Enter the IP address manually: ");
                    selectedIp = scanner.nextLine().trim();
                } else {
                    // invalid input is notified
                    System.out.println("Invalid selection");
                }
            } catch (NumberFormatException e) {
                // invalid input is notified
                System.out.println("Invalid selection");
            }
        }

        // rmi host property is set
        System.setProperty("java.rmi.server.hostname", selectedIp);

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
                LogManager.getInstance().log(LogLevel.ERROR, "[DATABASE] FATAL ERROR: Cannot reach the Database!", dbException);
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
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] === SERVER READY AND LISTENING ON IP: " + selectedIp + " ===");

        } catch (Exception e) {
            // critical startup error is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Critical error during Server startup!", e);
            System.exit(1);
        }
    }
}
