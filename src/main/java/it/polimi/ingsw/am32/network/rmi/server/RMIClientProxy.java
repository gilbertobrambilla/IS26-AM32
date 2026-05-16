package it.polimi.ingsw.am32.network.rmi.server;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.rmi.client.RMIClient;

import java.rmi.RemoteException;

/**
 * Adapter class used to map the client node interface to the rmi client stub.
 */
public class RMIClientProxy implements ClientNode {

    private final RMIClient clientStub;

    /**
     * New rmi client adapter constructed.
     *
     * @param clientStub the rmi client stub
     */
    public RMIClientProxy(RMIClient clientStub) {
        this.clientStub = clientStub;
    }

    @Override
    public void sendPingToClient() {
        try {
            // ping reception triggered through the stub
            clientStub.onPingReceivedFromServer();
        } catch (RemoteException e) {
            // network exception thrown
            throw new NetworkException("rmi network error while pinging", e);
        }
    }

    @Override
    public void sendMessage(MessageDTO message) {
        try {
            // message pushed through the stub
            clientStub.receiveMessage(message);
        } catch (RemoteException e) {
            // network exception thrown
            throw new NetworkException("rmi network error while sending message", e);
        }
    }
}
