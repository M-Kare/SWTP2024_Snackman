package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.controller.Square.ChickenDTO;

public record FrontendChickenMessageEvent(EventType eventType, ChangeType changeType, String lobbyId,
                                          ChickenDTO chicken) {

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public ChickenDTO chicken() {
        return chicken;
    }

    @Override
    public String toString() {
        return "FrontendChickenMessageEvent{" +
                "eventType=" + eventType +
                ", changeType=" + changeType +
                ", chicken=" + chicken +
                '}';
    }

    @Override
    public String lobbyId() {
        return lobbyId;
    }
}
