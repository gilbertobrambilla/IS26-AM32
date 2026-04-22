package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.rmi.RemoteException;

/**
 * Adapter class mapping the client node interface to the rmi client stub.
 */
public class RMIClientAdapter implements ClientNode {

    private final RMIClient clientStub;

    /**
     * Constructs a new rmi client adapter.
     *
     * @param clientStub The rmi client stub.
     */
    public RMIClientAdapter(RMIClient clientStub) {
        this.clientStub = clientStub;
    }

    @Override
    public void ping() {
        try {
            // ping is sent through the stub
            clientStub.ping();
        } catch (RemoteException e) {
            // network exception is thrown
            throw new NetworkException("rmi network error while pinging", e);
        }
    }

    @Override
    public void sendMessage(MessageDTO message) {
        try {
            // message is pushed through the stub
            clientStub.receiveMessage(message);
        } catch (RemoteException e) {
            // network exception is thrown
            throw new NetworkException("rmi network error while sending message", e);
        }
    }
}
