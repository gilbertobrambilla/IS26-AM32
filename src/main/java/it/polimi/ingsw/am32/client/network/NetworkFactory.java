package it.polimi.ingsw.am32.client.network;

import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.network.rmi.client.RMIClientCallback;
import it.polimi.ingsw.am32.network.rmi.server.RMIServer;
import it.polimi.ingsw.am32.network.rmi.client.RMIServerProxy;
import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Factory class responsible for creating network connections.
 */
public class NetworkFactory {
    private static final String SERVER_NAME = "MesosServer";
    private static final int RMI_PORT = 1111;
    private static final int SOCKET_PORT = 2222;

    /**
     * Rmi connection to the server is established.
     *
     * @param ip the server ip
     * @param onFatalError the callback to be executed on connection loss
     * @return the virtual server instance, or null if the connection fails
     */
    public static VirtualServer createRmiConnection(String ip, Runnable onFatalError) {
        // fast fail socket check is performed
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, RMI_PORT), 2000);
        } catch (Exception e) {
            return null;
        }

        try {
            // registry and stubs are configured
            Registry registry = LocateRegistry.getRegistry(ip, RMI_PORT);
            RMIServer serverStub = (RMIServer) registry.lookup(SERVER_NAME);

            // callback is instantiated
            RMIClientCallback rmiClient = new RMIClientCallback();

            // proxy is instantiated
            RMIServerProxy serverProxy = new RMIServerProxy(serverStub, rmiClient);

            // virtual server is created
            VirtualServer virtualServer = VirtualServer.create(serverProxy, onFatalError);

            // virtual server is injected into callback
            rmiClient.setVirtualServer(virtualServer);

            // server proxy is injected into callback
            rmiClient.setServerProxy(serverProxy);

            // callback is exported after all dependencies are set
            UnicastRemoteObject.exportObject(rmiClient, 0);

            return virtualServer;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Socket connection to the server is established.
     *
     * @param ip the server ip
     * @param onFatalError the callback to be executed on connection loss
     * @return the virtual server instance, or null if the connection fails
     */
    public static VirtualServer createSocketConnection(String ip, Runnable onFatalError) {
        try {
            // socket server connection is created
            SocketServerProxy socketConnection = new SocketServerProxy(ip, SOCKET_PORT);

            // virtual server is created
            VirtualServer virtualServer = VirtualServer.create(socketConnection, onFatalError);

            // virtual server is injected into socket connection
            socketConnection.setVirtualServer(virtualServer);

            return virtualServer;
        } catch (Exception e) {
            return null;
        }
    }
}
