package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;

public record FrontendScriptGhostMessageEvent(EventType eventType, ChangeType changeType, ScriptGhost scriptGhost) {

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    public ScriptGhost scriptGhost() {
        return scriptGhost;
    }

    @Override
    public String toString() {
        return "FrontendScriptGhostMessageEvent{" +
                "eventType=" + eventType +
                ", changeType=" + changeType +
                ", scriptGhost=" + scriptGhost +
                '}';
    }
}
