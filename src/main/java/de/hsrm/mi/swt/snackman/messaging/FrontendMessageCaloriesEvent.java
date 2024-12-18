package de.hsrm.mi.swt.snackman.messaging;


public record FrontendMessageCaloriesEvent(EventType eventType, ChangeType changeType, int calories, String message ) {


    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public int calories() {
        return calories;
    }

    @Override
    public String message() {
        return message;
    }
}
