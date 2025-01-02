package de.hsrm.mi.swt.snackman.entities.leaderboard;

import de.hsrm.mi.swt.snackman.services.LeaderboardService;
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
        this.releaseDate = LocalDate.parse(releaseDate);
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

    public String getEntryAsFileLine(){
        return this.name + LeaderboardService.CSV_LINE_SPLITTER + this.duration.toString() + LeaderboardService.CSV_LINE_SPLITTER + this.releaseDate.toString() + "\n";
    }
}
