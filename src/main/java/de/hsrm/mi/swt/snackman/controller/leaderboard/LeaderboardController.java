package de.hsrm.mi.swt.snackman.controller.leaderboard;

import de.hsrm.mi.swt.snackman.entities.leaderboard.LeaderboardEntry;
import de.hsrm.mi.swt.snackman.services.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboardAsDTO());
    }

    @PostMapping("/new/entry")
    public ResponseEntity<Void> createNewLeaderboardEntry(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        String duration = requestBody.get("duration");
        String releaseDate = requestBody.get("releaseDate");

        LeaderboardEntry newEntry = new LeaderboardEntry(name, releaseDate, duration);
        this.leaderboardService.addLeaderboardEntry(newEntry);

        return ResponseEntity.ok().build();
    }
}
