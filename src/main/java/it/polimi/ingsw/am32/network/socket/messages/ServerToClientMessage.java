package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import java.io.Serializable;

public interface ServerToClientMessage extends Serializable {
    void execute(SocketServerProxy connection);
}
