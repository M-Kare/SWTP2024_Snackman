package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;


public record FrontedLobbyRoleUpdateEvent(Lobby lobby, String characterId) {

    @Override
    public Lobby lobby() {
        return lobby;
    }

    @Override
    public String characterId() {
        return characterId;
    }

    @Override
    public String toString() {
        return "FrontendLobbyRoleUpdateEvent{" +
                "lobby=" + lobby +
                ", characterId='" + characterId + '\'' +
                '}';
    }
}

