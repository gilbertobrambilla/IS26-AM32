package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import it.polimi.ingsw.am32.client.view.tui.TuiUI;
import javafx.application.Application;

import java.net.InetAddress;
import java.util.Scanner;

/**
 * Main entry point for the client application.
 */
public class ClientMain {

    /**
     * Application is launched.
     *
     * @param args the command line arguments
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

        // ip is requested and validated
        String currentIp;
        while (true) {
            System.out.print("\nEnter server IP [" + defaultIp + "] (press Enter to use default): ");
            String ipInput = scanner.nextLine().trim();
            currentIp = ipInput.isEmpty() ? defaultIp : ipInput;

            try {
                // host reachability is checked
                if (InetAddress.getByName(currentIp).isReachable(2500)) {
                    break;
                } else {
                    System.out.println("[SYSTEM] Server is unreachable. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("[SYSTEM] Invalid IP address or network error. Please try again.");
            }
        }

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

        if (useTui) {
            // tui ui is instantiated and started
            System.out.println("\n[SYSTEM] Starting TUI...");
            TuiUI tui = new TuiUI(currentIp);
            tui.start();
        } else {
            // gui framework is launched
            System.out.println("\n[SYSTEM] Starting JavaFX GUI...");
            // pass the server address to the static context of our javafx app
            GuiApp.setServerAddress(currentIp);
            // launch the javafx application thread
            Application.launch(GuiApp.class, args);
        }
    }
}
