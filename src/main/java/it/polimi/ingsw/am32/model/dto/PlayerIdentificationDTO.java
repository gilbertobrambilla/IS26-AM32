package it.polimi.ingsw.am32.model.dto;

import it.polimi.ingsw.am32.model.enums.TotemColor;

public class PlayerIdentificationDTO {
    private final String nickname;
    private final TotemColor totem;

    public PlayerIdentificationDTO(String nickname, TotemColor totem) {
        this.nickname = nickname;
        this.totem = totem;
    }

    public String getNickname() {
        return nickname;
    }

    public TotemColor getTotem() {
        return totem;
    }
}
