package de.hsrm.mi.swt.snackman;

import de.hsrm.mi.swt.snackman.services.LeaderboardService;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnackmanApplication {

    @Autowired
    private MapService mapService;

	@Autowired
	private LeaderboardService leaderboardService;

	public static void main(String[] args) {
		SpringApplication.run(SnackmanApplication.class, args);
	}
}
