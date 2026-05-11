package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;
import java.io.Serializable;

public interface ClientToServerMessage extends Serializable {
    void execute(SocketClientProxy handler);
}
