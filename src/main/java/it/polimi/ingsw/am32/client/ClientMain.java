package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.network.rmi.RMIClientImpl;
import it.polimi.ingsw.am32.network.rmi.RMIServer;
import it.polimi.ingsw.am32.network.rmi.RMIServerAdapter;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientMain {

    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "MesosServer";

    public static void main(String[] args) {
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "5000");

        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println("        WELCOME TO MESOS         ");
        System.out.println("=================================");

        System.out.print("Enter server IP (e.g. localhost or 127.0.0.1): ");
        String serverIp = scanner.nextLine();

        // 1. Setup Rete
        VirtualServer virtualServer = connectToServer(serverIp);
        if (virtualServer == null) {
            System.exit(1);
        }

        // 2. Setup Controller Logico
        ClientController clientController = new ClientController(virtualServer);
        virtualServer.setClientController(clientController);

        // 3. Avvio TUI
        runTUI(scanner, virtualServer);
    }

    private static VirtualServer connectToServer(String ip) {
        try {
            System.out.println("[CLIENT] Searching for server at " + ip + "...");
            Registry registry = LocateRegistry.getRegistry(ip, RMI_PORT);

            RMIServer serverStub = (RMIServer) registry.lookup(SERVER_NAME);
            RMIClientImpl rmiClient = new RMIClientImpl();
            RMIServerAdapter serverNode = new RMIServerAdapter(serverStub, rmiClient);

            VirtualServer virtualServer = new VirtualServer(serverNode, () -> {
                System.err.println("\n[FATAL ERROR] Connection to server lost unexpectedly!");
                System.exit(1);
            });

            rmiClient.setVirtualServer(virtualServer);
            System.out.println("[CLIENT] Connection established successfully!\n");

            return virtualServer;
        } catch (Exception e) {
            System.err.println("[CLIENT] Unable to connect to server. Please check the IP and ensure the server is running.");
            e.printStackTrace();
            return null;
        }
    }

    private static void runTUI(Scanner scanner, VirtualServer virtualServer) {
        System.out.print("Choose your Nickname to enter the Lobby: ");
        String nickname = scanner.nextLine();

        virtualServer.connect(nickname);

        System.out.println("\n[CLIENT] You are in the Lobby! (Type 'quit' to exit)");

        boolean isRunning = true;
        while (isRunning) {
            String userInput = scanner.nextLine();

            if ("quit".equalsIgnoreCase(userInput)) {
                System.out.println("[CLIENT] Closing application...");
                virtualServer.handleServerDisconnect();
                isRunning = false;
            } else {
                System.out.println("[CLIENT] Unknown command: " + userInput);
            }
        }
        scanner.close();
    }
}