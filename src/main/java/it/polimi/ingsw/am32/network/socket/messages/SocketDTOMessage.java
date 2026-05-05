package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.socket.client.SocketServerConnection;

public class SocketDTOMessage implements ServerToClientMessage {
    private final MessageDTO dto;
    public SocketDTOMessage(MessageDTO dto) { this.dto = dto; }

    @Override
    public void execute(SocketServerConnection connection) {
        connection.processMessageDTO(dto);
    }
}
