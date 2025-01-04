package de.hsrm.mi.swt.snackman.entities.leaderboard;

import java.util.ArrayList;
import java.util.List;

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
