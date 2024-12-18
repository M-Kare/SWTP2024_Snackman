package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;

@Controller
public class PlayerStompController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LobbyManagerService lobbyService;

    // Erhalte Messages von /topic/player/update
    //TODO: In MapService HashMap von <UUID, Mob> und dann entsprechende Figur holen zum verschicken
    @MessageMapping("/topic/lobbies/{lobbyId}/player/update")
    public void spreadPlayerUpdate(@DestinationVariable String lobbyId, PlayerToBackendDTO player) {
        Lobby currentLobby = lobbyService.findLobbyByLobbyId(lobbyId);
        //TODO prüfen ob instanz von Snackman
        var playerMob = switch (currentLobby.getClientMobMap().get(player.playerId())) {
            case SnackMan snackman -> jumpUpdate(player, snackman);
            case Ghost ghost -> ghost;
            default ->
                    throw new IllegalStateException("Unexpected value: " + currentLobby.getClientMobMap().get(lobbyService.findClientByClientId(player.playerId())));
        };
        //SnackMan snackman = mapService.getSnackMan(player.uuid());
        playerMob.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
        playerMob.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());

        //JUMPING

        messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId + "/player",
                new PlayerToFrontendDTO(playerMob.getPosX(),
                playerMob.getPosY(),
                playerMob.getPosZ(), playerMob.getRotationQuaternion().x, playerMob.getRotationQuaternion().y,
                playerMob.getRotationQuaternion().z, playerMob.getRotationQuaternion().w, playerMob.getRadius(),
                playerMob.getSpeed(), player.playerId()));
    }

    private SnackMan jumpUpdate(PlayerToBackendDTO player, SnackMan snackman) {
            if (player.jump()) {
                if (player.doubleJump()) {
                    snackman.doubleJump();
                } else {
                    snackman.jump();
                }
            }
            snackman.updateJumpPosition(player.delta());
        return snackman;
    }
}
