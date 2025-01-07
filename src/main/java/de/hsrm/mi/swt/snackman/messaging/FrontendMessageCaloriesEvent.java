package de.hsrm.mi.swt.snackman.messaging;


public record FrontendMessageCaloriesEvent(int calories, String message ) {

//TODO add to message loop
 /*   @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }


  */
    @Override
    public int calories() {
        return calories;
    }

    @Override
    public String message() {
        return message;
    }
}
