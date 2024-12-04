package de.hsrm.mi.swt.snackman.messaging;


import de.hsrm.mi.swt.snackman.entities.map.Square;

public record FrontendMessageEvent(EventType eventType, ChangeType changeType, Square square) {
}
