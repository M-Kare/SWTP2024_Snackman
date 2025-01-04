package de.hsrm.mi.swt.snackman.controller.leaderboard;

import de.hsrm.mi.swt.snackman.entities.leaderboard.Leaderboard;

import java.util.List;

public record LeaderboardDTO(List<LeaderboardEntryDTO> leaderboardEntries) {
    public static LeaderboardDTO fromLeaderboardDTO(Leaderboard leaderboard){
        return new LeaderboardDTO(leaderboard.getLeaderboard().stream()
                .map(LeaderboardEntryDTO::fromLeaderboardEntry)
                .toList());
    }
    public static Leaderboard toLeaderboard(LeaderboardDTO leaderboardDTO){
        return new Leaderboard(leaderboardDTO.leaderboardEntries().stream().map(LeaderboardEntryDTO::toLeaderboardEntry).toList());
    }
}
