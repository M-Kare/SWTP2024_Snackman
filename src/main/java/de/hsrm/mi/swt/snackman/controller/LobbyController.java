package de.hsrm.mi.swt.snackman.controller;

import java.util.List;

import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.hsrm.mi.swt.snackman.entities.lobby.Client;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

@Controller
@RequestMapping("/api/lobbies")
public class LobbyController {

      @Autowired
      private LobbyManagerService lobbyManagerService;
      @Autowired
      private SimpMessagingTemplate messagingTemplate;
      private final Logger logger = LoggerFactory.getLogger(LobbyController.class);

      @PostMapping("/create")
      public ResponseEntity<Lobby> createLobby(@RequestParam String name, @RequestParam String creatorUuid) {
            Client client = lobbyManagerService.getClient(name, creatorUuid);
            try {
                  Lobby newLobby = lobbyManagerService.createLobby(name, client);
                  messagingTemplate.convertAndSend("/topic/lobbies", lobbyManagerService.getAllLobbies());

                  return ResponseEntity.ok(newLobby);
            } catch (LobbyAlreadyExistsException e) {
                  logger.error("Error occurred: ", e);
                  return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
      }

      @GetMapping
      public ResponseEntity<List<Lobby>> getAllLobbies() {
            return ResponseEntity.ok(lobbyManagerService.getAllLobbies());
      }

      @PostMapping("/{lobbyId}/join")
      public ResponseEntity<Lobby> joinLobby(@PathVariable String lobbyId, @RequestParam String playerId) {
            try {
                  Lobby joiningLobby = lobbyManagerService.joinLobby(lobbyId, playerId);
                  messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, joiningLobby);

                  return ResponseEntity.ok(joiningLobby);
            } catch (GameAlreadyStartedException e) {
                  logger.error("Error occurred: ", e);
                  return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
      }

      @PostMapping("/{lobbyId}/leave")
      public ResponseEntity<Void> leaveLobby(@PathVariable String lobbyId, @RequestParam String playerId) {
            lobbyManagerService.leaveLobby(lobbyId, playerId);
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, lobbyManagerService.findLobbyByUUID(lobbyId));

            return ResponseEntity.ok().build();
      }

      @PostMapping("/{lobbyId}/start")
      public ResponseEntity<Void> startGame(@PathVariable String lobbyId) {
            lobbyManagerService.startGame(lobbyId);
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, lobbyManagerService.findLobbyByUUID(lobbyId));

            return ResponseEntity.ok().build();
      }

}
