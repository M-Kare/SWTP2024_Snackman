package de.hsrm.mi.swt.snackman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

@Controller
@RequestMapping("/api/playerclients")
public class PlayerClientController {

    @Autowired
    private LobbyManagerService lobbyManagerService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final Logger logger = LoggerFactory.getLogger(PlayerClientController.class);


    public ResponseEntity<PlayerClient> createPlayerClient(@RequestParam("name") String name){
        if(name == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        logger.info("Creating new player with name: {}", name);
        
        return null;
    }
}
