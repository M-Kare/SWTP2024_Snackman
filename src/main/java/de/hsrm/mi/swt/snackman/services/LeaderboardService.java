package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.leaderboard.LeaderboardEntry;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing the leaderboard
 * This class is responsible for loading and providing access to the leaderboard data
 */
@Service
public class LeaderboardService {
    Logger log = LoggerFactory.getLogger(MapService.class);
    private FrontendMessageService frontendMessageService;
    private String filePath;
    private List<LeaderboardEntry> leaderboard = new ArrayList<>();

    @Autowired
    public LeaderboardService(FrontendMessageService frontendMessageService) {
        this.frontendMessageService = frontendMessageService;
        this.filePath = "leaderboard.txt";
    }
}
