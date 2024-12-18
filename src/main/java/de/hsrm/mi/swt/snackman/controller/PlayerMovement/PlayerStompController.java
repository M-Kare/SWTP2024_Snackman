package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

@Controller
public class PlayerStompController {

  @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MapService mapService;

    // Erhalte Messages von /topic/player/update
    //TODO: In MapService HashMap von <UUID, Mob> und dann entsprechende Figur holen zum verschicken
    @MessageMapping("topic/player/update")
    public void spreadPlayerUpdate(PlayerToBackendDTO player) {
      SnackMan snackman = mapService.getSnackMan(player.uuid());
      snackman.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
      snackman.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());

        //JUMPING
        if (player.jump()) {
            if (player.doubleJump()) {
                snackman.doubleJump();
            } else {
                snackman.jump();
            }
        }
        snackman.updateJumpPosition(player.delta());
      messagingTemplate.convertAndSend("/topic/player", new PlayerToFrontendDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getRotationQuaternion().x, snackman.getRotationQuaternion().y, snackman.getRotationQuaternion().z, snackman.getRotationQuaternion().w, snackman.getRadius(), snackman.getSpeed(), player.uuid()));
    }
  }
