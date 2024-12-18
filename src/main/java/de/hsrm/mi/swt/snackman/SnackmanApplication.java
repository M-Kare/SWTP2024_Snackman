package de.hsrm.mi.swt.snackman;

import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnackmanApplication {

    @Autowired
    private LobbyManagerService lobbyManagerService;

	public static void main(String[] args) {
		SpringApplication.run(SnackmanApplication.class, args);
	}
}
