package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

@Controller
public class PlayerStompController {

    @Autowired
    private LobbyManagerService lobbyService;

    @MessageMapping("/topic/lobbies/{lobbyId}/player/update")
    public void spreadPlayerUpdate(@DestinationVariable String lobbyId, PlayerToBackendDTO player) {
        Lobby currentLobby = lobbyService.findLobbyByLobbyId(lobbyId);
        var playerMob = switch (currentLobby.getClientMobMap().get(player.playerId())) {
            case SnackMan snackman -> updateSnackman(player, snackman);
            case Ghost ghost -> ghost;
            default ->
                    throw new IllegalStateException("Unexpected value: " + currentLobby.getClientMobMap().get(player.playerId()));
        };
        playerMob.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
        playerMob.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());
    }

    private SnackMan updateSnackman(PlayerToBackendDTO player, SnackMan snackman) {
        if (player.jump()) {
            if (player.doubleJump()) {
                snackman.doubleJump();
            } else {
                snackman.jump();
            }
        }
        snackman.updateJumpPosition(player.delta());
        snackman.setSprinting(player.sprinting());

        return snackman;
    }
}
