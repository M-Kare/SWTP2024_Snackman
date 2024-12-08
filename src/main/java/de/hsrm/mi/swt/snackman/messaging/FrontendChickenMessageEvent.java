package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;

public record FrontendChickenMessageEvent(EventType eventType, ChangeType changeType, Chicken chicken) {

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public Chicken chicken() {
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
}
