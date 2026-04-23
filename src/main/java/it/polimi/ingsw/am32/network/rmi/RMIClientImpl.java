package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.VirtualServer;

import java.rmi.RemoteException;

/**
 * Implementation of the rmi client remote interface.
 */
public class RMIClientImpl implements RMIClient {

    private VirtualServer virtualServer;

    /**
     * Constructs a new rmi client implementation.
     */
    public RMIClientImpl() {
    }

    /**
     * Sets the virtual server instance.
     *
     * @param virtualServer The virtual server to be set.
     */
    public void setVirtualServer(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
    }

    @Override
    public void ping() throws RemoteException {
        // liveness check is received
    }

    @Override
    public void receiveMessage(MessageDTO message) throws RemoteException {
        if (virtualServer != null) {
            // message is forwarded to the virtual server
            virtualServer.receiveMessage(message);
        }
    }
}
