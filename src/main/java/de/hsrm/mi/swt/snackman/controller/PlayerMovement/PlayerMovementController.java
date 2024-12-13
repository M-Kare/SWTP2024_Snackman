package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

@Controller
public class PlayerMovementController {

  @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MapService mapService;

    // Erhalte Messages von /topic/player/update
    @MessageMapping("/topic/player/update")
    public void spreadPlayerUpdate(SnackManFrontendDTO player) {
      SnackMan snackman = mapService.getSnackMan(player.uuid());
      snackman.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
      snackman.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());
      messagingTemplate.convertAndSend("/topic/player", new SnackManPositionDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), player.uuid()));
    }

}
