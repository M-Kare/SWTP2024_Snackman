package de.hsrm.mi.swt.snackman.entities.leaderboard;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private String name;
    private LocalTime duration;
    private LocalDate releaseDate;

    public LeaderboardEntry(String name, LocalTime duration, LocalDate releaseDate) {
        this.name = name;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    public LeaderboardEntry(String name, String duration, String releaseDate) {
        this.name = name;
        this.duration = LocalTime.parse(duration);
        this.releaseDate = LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    @Override
    public int compareTo(@NotNull LeaderboardEntry o) {
        int durationComparison = this.duration.compareTo(o.duration);
        if (durationComparison != 0) {
            return durationComparison;
        }
        int releaseComparison = this.releaseDate.compareTo(o.releaseDate);
        if (releaseComparison != 0) {
            return releaseComparison;
        }
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
