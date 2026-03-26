package it.polimi.ingsw.am32.dto.setup;

public class CharacterCardDTO {
    private final String id;
    private final int era;
    private final int basePrestige;
    private final int minPlayers;
    private final String characterType;
    private final String characterParams;

    public CharacterCardDTO(String id, int era, int basePrestige, int minPlayers, String characterType, String characterParams) {
        this.id = id;
        this.era = era;
        this.basePrestige = basePrestige;
        this.minPlayers = minPlayers;
        this.characterType = characterType;
        this.characterParams = characterParams;
    }

    public String getId() {
        return this.id;
    }

    public int getEra() {
        return this.era;
    }

    public int getBasePrestige() {
        return this.basePrestige;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public String getCharacterType() {
        return this.characterType;
    }

    public String getCharacterParams() {
        return this.characterParams;
    }
}
