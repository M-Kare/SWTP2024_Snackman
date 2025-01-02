package de.hsrm.mi.swt.snackman.entities.leaderboard;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class LeaderboardEntry {
    private String name;
    private LocalTime duration;
    private LocalDate releaseDate;

    public LeaderboardEntry(String name, LocalTime duration, LocalDate releaseDate) {
        this.name = name;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

}
