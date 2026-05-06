package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import it.polimi.ingsw.am32.client.view.tui.TuiUI; // Import is updated to the new TUI implementation.
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.network.rmi.RMIClientImpl;
import it.polimi.ingsw.am32.network.rmi.RMIServer;
import it.polimi.ingsw.am32.network.rmi.RMIServerAdapter;
import javafx.application.Application;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main entry point for the client application.
 */
public class ClientMain {
    private static final String SERVER_NAME = "MesosServer";

    /**
     * Application is launched.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        // rmi timeout properties are configured
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "5000");

        if (args.length == 0) {
            // usage instructions are printed
            System.err.println("Usage: java -jar client.jar [--tui|--gui] [--rmi|--socket] [--ip <address>] [--port <number>]"); // tui flag is updated
            System.exit(2);
        }

        // configuration variables are declared
        boolean useTui = true; // flag is renamed to reflect the new ui
        boolean useRmi = true;
        String serverIp = "127.0.0.1";
        int serverPort = 1099;

        // command line arguments are parsed
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--gui":
                    // gui mode is selected
                    useTui = false; // tui is disabled
                    break;
                case "--tui":
                    // tui mode is explicitly selected
                    useTui = true;
                    break;
                case "--socket":
                    useRmi = false;
                    break;
                case "--ip":
                    if (i + 1 < args.length) {
                        serverIp = args[++i];
                    }
                    break;
                case "--port":
                    if (i + 1 < args.length) {
                        try {
                            serverPort = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("[WARNING] invalid port format. default port will be used.");
                        }
                    }
                    break;
            }
        }

        System.out.println("=================================");
        System.out.println("        WELCOME TO MESOS         ");
        System.out.println("=================================");
        System.out.println("Connecting to " + serverIp + ":" + serverPort + " via " + (useRmi ? "RMI" : "Socket") + "...");

        VirtualServer virtualServer = null;
        if (useRmi) {
            virtualServer = connectViaRMI(serverIp, serverPort);
        } else {
            // note: placeholder for the socket implementation
            System.err.println("[FATAL ERROR] Socket implementation is not yet available.");
            System.exit(2);
        }

        if (virtualServer == null) {
            System.err.println("[FATAL ERROR] Cannot reach the server at the specified address.");
            System.exit(3);
        }

        // mvc components are wired
        ClientController clientController = new ClientController(virtualServer);
        virtualServer.setClientController(clientController);

        if (useTui) {
            // tui ui is instantiated and started
            TuiUI tui = new TuiUI(clientController); // constructor takes the controller as before
            tui.start(); // main loop is entered
        } else {
            // GUI framework is launched
            System.out.println("[SYSTEM] Starting JavaFX GUI...");

            // pass the instantiated controller to the static context of our JavaFX App
            GuiApp.setController(clientController);

            // launch the JavaFX Application thread
            Application.launch(GuiApp.class, args);
        }
    }

    /**
     * Rmi connection to the server is established.
     *
     * @param ip the server ip.
     * @param port the server port.
     * @return the virtual server instance, or null if connection fails.
     */
    private static VirtualServer connectViaRMI(String ip, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            RMIServer serverStub = (RMIServer) registry.lookup(SERVER_NAME);
            RMIClientImpl rmiClient = new RMIClientImpl();

            ServerNode serverNode = new RMIServerAdapter(serverStub, rmiClient);
            VirtualServer virtualServer = VirtualServer.create(serverNode, () -> {
                System.err.println("\n[FATAL ERROR] Server connection lost");
                System.exit(3);
            });

            rmiClient.setVirtualServer(virtualServer);
            java.rmi.server.UnicastRemoteObject.exportObject(rmiClient, 0);

            return virtualServer;
        } catch (Exception e) {
            return null;
        }
    }
}
