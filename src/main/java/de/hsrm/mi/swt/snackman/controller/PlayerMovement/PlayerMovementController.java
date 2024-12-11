package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;

@Controller
public class PlayerMovementController {

  @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private SnackMan snackman;

    // Erhalte Messages von /topic/player/update
    @MessageMapping("/topic/player/update")
    public void spreadPlayerUpdate(SnackManFrontendDTO player) {
      //Validation here
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
      //JUMPING

      messagingTemplate.convertAndSend("/topic/player", new SnackManPositionDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ()));
    }
}
