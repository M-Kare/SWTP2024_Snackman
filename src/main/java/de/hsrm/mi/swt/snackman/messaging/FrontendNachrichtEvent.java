package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.controller.Cube.CubeDTO;

public class FrontendNachrichtEvent {
    private EventType eventType;
    private ChangeType changeType;
    private CubeDTO cubeDTO;

    public FrontendNachrichtEvent() {
    }

    public FrontendNachrichtEvent(EventType eventType, ChangeType changeType, CubeDTO cubeDTO) {
        this.eventType = eventType;
        this.changeType = changeType;
        this.cubeDTO = cubeDTO;
    }

    public EventType getEventType() {
        return eventType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public CubeDTO getCubeDTO() {
        return cubeDTO;
    }
}


