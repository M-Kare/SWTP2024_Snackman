package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackManDTO;

@Controller
public class PlayerMovementController {

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  private final SnackMan snackman = new SnackMan(0, 0);

  // Erhalte Messages von /topic/player/update
  @MessageMapping("/topic/player/update")
  public void spreadPlayerUpdate(SnackManDTO player) {
    // Validation here

    snackman.move(player.posX(), player.posY(), player.posZ());
    snackman.setDirY(player.dirY());
    messagingTemplate.convertAndSend("/topic/player", SnackManDTO.toSnackManDTO(snackman));
  }

}
