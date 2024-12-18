package de.hsrm.mi.swt.snackman.controller.Lobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

/**
 * The PlayerClientController handles HTTP request relalted to managing player client.
 * It provides endpoints for creating new player client.
 */
@Controller
@RequestMapping("/api/playerclients")
public class PlayerClientController {

    @Autowired
    private LobbyManagerService lobbyManagerService;

    private final Logger logger = LoggerFactory.getLogger(PlayerClientController.class);

    /**
     * Create a new player client with a name
     * 
     * @param name  the player's name
     * @return  the newly created {@link PlayerClient} object
     */
    @PostMapping("/create")
    public ResponseEntity<PlayerClient> createPlayerClient(@RequestParam("name") String name){
        if(name == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PlayerClient newPlayerClient = lobbyManagerService.createNewClient(name);
        logger.info("Creating new player with name: {} and playerUuid: {}", newPlayerClient.getPlayerName(), newPlayerClient.getPlayerId());
        
        return ResponseEntity.ok(newPlayerClient);
    }

}
