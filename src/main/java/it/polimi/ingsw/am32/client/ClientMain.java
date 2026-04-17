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
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println("       BENVENUTO IN MESOS        ");
        System.out.println("=================================");

        System.out.print("Inserisci l'IP del server (es. localhost o 127.0.0.1): ");
        String serverIp = scanner.nextLine();

        try {
            System.out.println("[CLIENT] Ricerca del server all'indirizzo " + serverIp + "...");
            Registry registry = LocateRegistry.getRegistry(serverIp, RMI_PORT);

            RMIServer serverStub = (RMIServer) registry.lookup(SERVER_NAME);

            RMIClientImpl rmiClient = new RMIClientImpl();

            RMIServerAdapter serverNode = new RMIServerAdapter(serverStub, rmiClient);

            VirtualServer virtualServer = new VirtualServer(serverNode, () -> {
                System.err.println("\n[ERRORE FATALE] Connessione al Server persa in modo anomalo!");
                System.exit(1);
            });

            rmiClient.setVirtualServer(virtualServer);

            System.out.println("[CLIENT] Connessione stabilita con successo!\n");

            System.out.print("Scegli il tuo Nickname per entrare nella Lobby: ");
            String nickname = scanner.nextLine();

            virtualServer.connect(nickname);

            while (true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.err.println("[CLIENT] Impossibile connettersi al server. Verifica l'IP e che il server sia acceso.");
            e.printStackTrace();
        }
    }
}