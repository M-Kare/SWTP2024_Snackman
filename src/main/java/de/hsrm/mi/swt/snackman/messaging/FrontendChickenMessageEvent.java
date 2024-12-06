package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.entities.map.Square;

public record FrontendChickenMessageEvent(EventType eventType, ChangeType changeType, Square oldSquare, Square newSquare) {


    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public Square newSquare() {
        return newSquare;
    }

    @Override
    public Square oldSquare() {
        return oldSquare;
    }
}
