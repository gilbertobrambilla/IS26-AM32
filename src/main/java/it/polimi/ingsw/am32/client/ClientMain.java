package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.client.controller.ClientController;

import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import it.polimi.ingsw.am32.client.view.tui.TuiUI;
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.network.rmi.client.RMIClientCallback;
import it.polimi.ingsw.am32.network.rmi.server.RMIServer;
import it.polimi.ingsw.am32.network.rmi.client.RMIServerProxy;
import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import javafx.application.Application;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main entry point for the client application.
 */
public class ClientMain {
    private static final String SERVER_NAME = "MesosServer";
    private static final int RMI_PORT = 1111;
    private static final int SOCKET_PORT = 2222;

    /**
     * Application is launched.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        // rmi timeout properties are configured
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "5000");

        // default configuration variables are declared
        String defaultIp = "127.0.0.1";

        // scanner is instantiated
        Scanner scanner = new Scanner(System.in);

        // ascii art is printed
        System.out.println("\033[36m"  + "      ___           ___           ___           ___           ___     " + "\033[0m");
        System.out.println("\033[36m"  + "     /\\__\\         /\\  \\         /\\  \\         /\\  \\         /\\  \\    " + "\033[0m");
        System.out.println("\033[36m"  + "    /::|  |       /::\\  \\       /::\\  \\       /::\\  \\       /::\\  \\   " + "\033[0m");
        System.out.println("\033[36m"  + "   /:|:|  |      /:/\\:\\  \\     /:/\\ \\  \\     /:/\\:\\  \\     /:/\\ \\  \\  " + "\033[0m");
        System.out.println("\033[36m"  + "  /:/|:|__|__   /::\\~\\:\\  \\   _\\:\\~\\ \\  \\   /:/  \\:\\  \\   _\\:\\~\\ \\  \\ " + "\033[0m");
        System.out.println("\033[36m"  + " /:/ |::::\\__\\ /:/\\:\\ \\:\\__\\ /\\ \\:\\ \\ \\__\\ /:/__/ \\:\\__\\ /\\ \\:\\ \\ \\__\\" + "\033[0m");
        System.out.println("\033[36m"  + " \\/__/~~/:/  / \\:\\~\\:\\ \\/__/ \\:\\ \\:\\ \\/__/ \\:\\  \\ /:/  / \\:\\ \\:\\ \\/__/" + "\033[0m");
        System.out.println("\033[36m"  + "       /:/  /   \\:\\ \\:\\__\\    \\:\\ \\:\\__\\    \\:\\  /:/  /   \\:\\ \\:\\__\\  " + "\033[0m");
        System.out.println("\033[36m"  + "      /:/  /     \\:\\ \\/__/     \\:\\/:/  /     \\:\\/:/  /     \\:\\/:/  /  " + "\033[0m");
        System.out.println("\033[36m"  + "     /:/  /       \\:\\__\\        \\::/  /       \\::/  /       \\::/  /   " + "\033[0m");
        System.out.println("\033[36m"  + "     \\/__/         \\/__/         \\/__/         \\/__/         \\/__/    " + "\033[0m\n");

        // application loop is started
        while (true) {
            // ip is requested
            System.out.print("\nEnter server IP [" + defaultIp + "] (press Enter to use default): ");
            String ipInput = scanner.nextLine().trim();
            String currentIp = ipInput.isEmpty() ? defaultIp : ipInput;

            // ui is requested
            boolean useTui;
            while (true) {
                System.out.println("\nChoose user interface:");
                System.out.println("1: TUI");
                System.out.println("2: GUI");
                System.out.print("Select: ");
                String uiInput = scanner.nextLine().trim();

                // input is validated
                if (uiInput.equals("1")) {
                    useTui = true;
                    break;
                } else if (uiInput.equals("2")) {
                    useTui = false;
                    break;
                } else {
                    System.out.println("Invalid selection");
                }
            }

            // protocol is requested
            boolean useRmi;
            while (true) {
                System.out.println("\nChoose connection protocol:");
                System.out.println("1: RMI");
                System.out.println("2: Socket");
                System.out.print("Select: ");
                String netInput = scanner.nextLine().trim();

                // input is validated
                if (netInput.equals("1")) {
                    useRmi = true;
                    break;
                } else if (netInput.equals("2")) {
                    useRmi = false;
                    break;
                } else {
                    System.out.println("Invalid selection");
                }
            }

            // port is automatically assigned
            int currentPort = useRmi ? RMI_PORT : SOCKET_PORT;

            System.out.println("\nConnecting to " + currentIp + ":" + currentPort + " via " + (useRmi ? "RMI" : "Socket") + "...");

            // controller reference is declared using atomic reference
            final AtomicReference<ClientController> controllerRef = new AtomicReference<>();

            VirtualServer virtualServer;
            if (useRmi) {
                // rmi connection is established
                virtualServer = connectViaRMI(currentIp, currentPort, controllerRef);
            } else {
                // socket connection is established
                virtualServer = connectViaSocket(currentIp, currentPort, controllerRef);
            }

            if (virtualServer == null) {
                System.err.println("Cannot reach the server at the specified address");
                continue;
            }

            // mvc components are wired
            ClientController clientController = new ClientController(virtualServer);
            virtualServer.setClientController(clientController);
            // controller is injected into the atomic reference
            controllerRef.set(clientController);

            if (useTui) {
                // tui ui is instantiated and started
                TuiUI tui = new TuiUI(clientController);
                tui.start();

                // exit reason is evaluated
                if (tui.getExitReason() == TuiUI.ExitReason.USER_QUIT) {
                    // resources are cleaned up
                    clientController.shutdown();
                    // application loop is broken
                    break;
                } else {
                    // resources are cleaned up before reconnecting
                    clientController.shutdown();
                }
            } else {
                // gui framework is launched
                System.out.println("\nStarting GUI...");

                // GUI framework is launched
                System.out.println("[SYSTEM] Starting JavaFX GUI...");

                // pass the instantiated controller to the static context of our JavaFX App
                GuiApp.setController(clientController);

                // pass the server address to the static context of our JavaFX App
                GuiApp.setServerAddress(currentIp);

                // launch the JavaFX Application thread
                Application.launch(GuiApp.class, args);

                break;
            }

            // ui closed message is printed
            System.out.println("\nReturning to setup...");
        }

        // scanner is closed
        scanner.close();
    }

    /**
     * Rmi connection to the server is established.
     *
     * @param ip The server ip.
     * @param port The server port.
     * @param controllerRef The atomic reference to the client controller for callbacks.
     * @return The virtual server instance, or null if connection fails.
     */
    private static VirtualServer connectViaRMI(String ip, int port, AtomicReference<ClientController> controllerRef) {
        // fast fail socket check is performed
        try (Socket socket = new Socket()) {
            // connection is attempted
            socket.connect(new InetSocketAddress(ip, port), 2000);
        } catch (Exception e) {
            // failure is returned immediately
            return null;
        }

        try {
            // registry and stubs are configured
            Registry registry = LocateRegistry.getRegistry(ip, port);
            RMIServer serverStub = (RMIServer) registry.lookup(SERVER_NAME);
            RMIClientCallback rmiClient = new RMIClientCallback();
            ServerNode serverNode = new RMIServerProxy(serverStub, rmiClient);

            // virtual server is created
            VirtualServer virtualServer = VirtualServer.create(serverNode, () -> {
                // disconnection is notified
                if (controllerRef.get() != null) {
                    controllerRef.get().onFatalError("Server connection lost");
                }
            });

            // rmi client is exported
            rmiClient.setVirtualServer(virtualServer);
            UnicastRemoteObject.exportObject(rmiClient, 0);

            return virtualServer;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Socket connection to the server is established.
     *
     * @param ip The server ip.
     * @param port The server port.
     * @param controllerRef The atomic reference to the client controller for callbacks.
     * @return The virtual server instance, or null if connection fails.
     */
    private static VirtualServer connectViaSocket(String ip, int port, AtomicReference<ClientController> controllerRef) {
        try {
            // socket server connection is created
            SocketServerProxy socketConnection = new SocketServerProxy(ip, port);

            // virtual server is created
            VirtualServer virtualServer = VirtualServer.create(socketConnection, () -> {
                // disconnection is notified
                if (controllerRef.get() != null) {
                    controllerRef.get().onFatalError("Server connection lost");
                }
            });

            // virtual server is injected into socket connection
            socketConnection.setVirtualServer(virtualServer);

            return virtualServer;
        } catch (Exception e) {
            // failure is returned immediately
            return null;
        }
    }
}
