package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;

/**
 * A message used to wrap and send a generic dto via socket is represented.
 */
public class SocketDTOMessage implements ServerToClientMessage {

    private final MessageDTO dto;

    /**
     * A new socket dto message is constructed.
     *
     * @param dto the generic message dto
     */
    public SocketDTOMessage(MessageDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(SocketServerProxy connection) {
        connection.processMessageDTO(dto);
    }
}
