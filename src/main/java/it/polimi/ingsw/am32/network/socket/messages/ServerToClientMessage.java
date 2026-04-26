package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.client.SocketServerConnection;
import java.io.Serializable;

public interface ServerToClientMessage extends Serializable {
    void execute(SocketServerConnection connection);
}
