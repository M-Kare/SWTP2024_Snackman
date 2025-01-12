package de.hsrm.mi.swt.snackman.entities.leaderboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a leaderboard, which is a collection of {@link LeaderboardEntry} objects.
 * Provides methods to manage and retrieve leaderboard entries.
 */
public class Leaderboard {
    private List<LeaderboardEntry> leaderboard;

    public Leaderboard(List<LeaderboardEntry> list) {
        this();
        this.leaderboard = list;
    }

    public Leaderboard() {
        leaderboard = new ArrayList<LeaderboardEntry>();
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboard;
    }

    public void addEntry(LeaderboardEntry entry) {
        leaderboard.add(entry);
    }
}
