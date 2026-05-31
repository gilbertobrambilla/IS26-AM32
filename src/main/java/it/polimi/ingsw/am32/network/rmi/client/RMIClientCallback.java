package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.client.VirtualServer;

import java.rmi.RemoteException;

/**
 * Implementation of the rmi client remote interface.
 */
public class RMIClientCallback implements RMIClient {

    private VirtualServer virtualServer;
    private RMIServerProxy serverProxy;

    /**
     * Constructs a new rmi client implementation.
     */
    public RMIClientCallback() {
    }

    @Override
    public void onPingReceivedFromServer() throws RemoteException {
        if (this.virtualServer != null) {
            // ping received processed by virtual server
            this.virtualServer.onPingReceivedFromServer();
        }
    }

    @Override
    public void receiveMessage(MessageDTO message) throws RemoteException {
        if (message instanceof ConnectionResultEvent) {
            ConnectionResultEvent result = (ConnectionResultEvent) message;

            if (result.isSuccess()) {
                if (this.serverProxy != null) {
                    // connection id passed to proxy
                    this.serverProxy.setConnectionID(result.getConnectionID());
                } else {
                    // severe error logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] RMI callback received but server proxy is not initialized!");
                }
            }
        }

        if (virtualServer != null) {
            // message forwarded to the virtual server
            virtualServer.receiveMessage(message);
        }
    }

    /**
     * Sets the virtual server instance.
     *
     * @param virtualServer the virtual server to be set
     */
    public void setVirtualServer(VirtualServer virtualServer) {
        // virtual server reference updated
        this.virtualServer = virtualServer;
    }

    /**
     * Sets the server proxy instance.
     *
     * @param serverProxy the server proxy to be set
     */
    public void setServerProxy(RMIServerProxy serverProxy) {
        // server proxy reference updated
        this.serverProxy = serverProxy;
    }
}
