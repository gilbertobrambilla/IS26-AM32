package it.polimi.ingsw.am32.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.network.rmi.RMIServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    private static final int RMI_PORT = 1099;
    private static final String SERVER_NAME = "MesosServer";

    public static void main(String[] args) {
        try {
            System.out.println("[SERVER] Inizializzazione in corso...");

            LobbyController lobbyController = new LobbyController();

            RMIServerImpl rmiServer = new RMIServerImpl(lobbyController);

            System.out.println("[SERVER] Avvio del Registry RMI sulla porta " + RMI_PORT + "...");
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            registry.rebind(SERVER_NAME, rmiServer);

            System.out.println("[SERVER] === SERVER PRONTO ED IN ASCOLTO ===");

        } catch (Exception e) {
            System.err.println("[SERVER] Errore critico durante l'avvio del Server!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}