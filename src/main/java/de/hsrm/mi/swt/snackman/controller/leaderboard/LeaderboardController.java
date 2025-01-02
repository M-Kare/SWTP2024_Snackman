package de.hsrm.mi.swt.snackman.controller.leaderboard;

import de.hsrm.mi.swt.snackman.services.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LeaderboardController {

    private final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);
    @Autowired
    private LeaderboardService leaderboardService;


}
