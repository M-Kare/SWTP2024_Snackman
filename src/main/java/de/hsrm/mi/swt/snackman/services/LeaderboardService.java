package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.leaderboard.LeaderboardEntry;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing the leaderboard
 * This class is responsible for loading and providing access to the leaderboard data
 */
@Service
public class LeaderboardService {
    Logger log = LoggerFactory.getLogger(MapService.class);
    private FrontendMessageService frontendMessageService;
    private final String filePath;
    private final List<LeaderboardEntry> leaderboard = new ArrayList<>();
    private final String CSV_LINE_SPLITTER = ";";

    @Autowired
    public LeaderboardService(FrontendMessageService frontendMessageService) {
        this.frontendMessageService = frontendMessageService;
        this.filePath = "leaderboard.txt";
        List<String> lines = readInLeaderboard();
        fillLeaderboard(lines);
        log.info("Leaderboard loaded: {}", leaderboard);
    }

    private List<String> readInLeaderboard() {
        List<String> lines = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the " + filePath + " file.", e);
        }

        if (lines.isEmpty()) {
            throw new RuntimeException(filePath + " is empty.");
        }
        return lines;
    }

    private void fillLeaderboard(List<String> lines) {
        for (String line : lines) {
            String[] parts = line.split(CSV_LINE_SPLITTER);
            if (parts.length != 3) throw new RuntimeException("Invalid CSV line: " + line + " at " + filePath + " file.");
            addLeaderboardEntry(new LeaderboardEntry(parts[0], parts[1], parts[2]));
        }
    }

    private void addLeaderboardEntry(LeaderboardEntry entry) {
        this.leaderboard.add(entry);
        Collections.sort(this.leaderboard);
    }
}
