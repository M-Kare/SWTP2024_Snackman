package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

import java.util.Arrays;

@Controller
public class PlayerStompController {

    @Autowired
    private LobbyManagerService lobbyService;

    @MessageMapping("/topic/lobbies/{lobbyId}/player/update")
    public void spreadPlayerUpdate(@DestinationVariable("lobbyId") String lobbyId, PlayerToBackendDTO player) {
        Lobby currentLobby = lobbyService.findLobbyByLobbyId(lobbyId);
        var playerMob = switch (currentLobby.getClientMobMap().get(player.playerId())) {
            case SnackMan snackman -> updateSnackman(player, snackman, currentLobby);
            case Ghost ghost -> updateGhost(currentLobby, ghost);
            default ->
                    throw new IllegalStateException("Unexpected value: " + currentLobby.getClientMobMap().get(player.playerId()));
        };
        playerMob.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
        playerMob.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());
    }

    private Ghost updateGhost (Lobby currentLobby, Ghost ghost){
        SnackMan snackman = getSnackman(currentLobby);
        checkWinningCondition(snackman, currentLobby);

        return ghost;
    }

    public SnackMan getSnackman(Lobby currentLobby) {
        return Arrays.stream(currentLobby.getGameMap().getGameMapSquares())
                .flatMap(Arrays::stream)
                .flatMap(square -> square.getMobs().stream())
                .filter(mob -> mob instanceof SnackMan)
                .map(mob -> (SnackMan) mob)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein Snackman gefunden"));
    }



    private SnackMan updateSnackman(PlayerToBackendDTO player, SnackMan snackman, Lobby currentLobby) {
        if (player.jump()) {
            if (player.doubleJump()) {
                snackman.doubleJump();
            } else {
                snackman.jump();
            }
        }
        snackman.updateJumpPosition(player.delta());
        snackman.setSprinting(player.sprinting());

        checkWinningCondition(snackman, currentLobby);

        return snackman;
    }

    private void checkWinningCondition(SnackMan snackman, Lobby currentLobby) {
        if (snackman.getKcal() < 0) {
            currentLobby.endGame(ROLE.GHOST);
        } else if (snackman.getKcal() >= GameConfig.SNACKMAN_MAX_CALORIES) {
            currentLobby.endGame(ROLE.SNACKMAN);
        }
    }
}
