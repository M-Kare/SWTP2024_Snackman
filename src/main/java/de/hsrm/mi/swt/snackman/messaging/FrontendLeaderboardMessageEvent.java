package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.controller.leaderboard.LeaderboardEntryDTO;
import java.util.List;

public record FrontendLeaderboardMessageEvent(EventType eventType, ChangeType changeType, List<LeaderboardEntryDTO> leaderboardEntries) {

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public ChangeType changeType() {
        return changeType;
    }

    @Override
    public List<LeaderboardEntryDTO> leaderboardEntries() {
        return leaderboardEntries;
    }
}
