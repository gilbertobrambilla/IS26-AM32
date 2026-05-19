package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import it.polimi.ingsw.am32.client.view.tui.TuiUI;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.utils.NetworkUtils;
import javafx.application.Application;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main entry point for the client application.
 */
public class ClientMain {

    /**
     * Application is launched.
     */
    public static void main() {
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

        // available ips are retrieved
        Map<String, String> availableIps = NetworkUtils.getAvailableIpv4Addresses();
        List<String> ipList = new ArrayList<>(availableIps.keySet());

        // prompt is printed
        System.out.println("Select your local IP address:");

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
                    System.out.print("Enter your local IP address manually: ");
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
                    LogManager.getInstance().log(LogLevel.WARNING, "[SYSTEM] Server is unreachable. Please try again.");
                }
            } catch (Exception e) {
                LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Invalid IP address or network error. Please try again.");
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
            System.out.println();
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Starting TUI...");
            TuiUI tui = new TuiUI(currentIp);
            tui.start();
        } else {
            // gui framework is launched
            System.out.println();
            LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Starting JavaFX GUI...");
            // pass the server address to the static context of our javafx app
            GuiApp.setServerAddress(currentIp);
            // launch the javafx application thread
            Application.launch(GuiApp.class);
        }
    }
}
