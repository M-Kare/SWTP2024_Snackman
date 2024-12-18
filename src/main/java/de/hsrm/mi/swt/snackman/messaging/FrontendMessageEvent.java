package de.hsrm.mi.swt.snackman.messaging;


import de.hsrm.mi.swt.snackman.entities.map.Square;

public record FrontendMessageEvent(EventType eventType, ChangeType changeType, String lobbyId, Square square) {

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public Square square() {
        return square;
    }

    @Override
    public String lobbyId() {
        return lobbyId;
    }
}
