package de.hsrm.mi.swt.snackman.controller.PlayerMovementTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs.SnackMan;
import de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs.SnackManDTO;

@Controller
public class PlayerMovementExampleController {

  @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final SnackMan snackman = new SnackMan(0, 0);

  //Erhalte Messages von /topic/cube/update
    @MessageMapping("/topic/player/update")
    public void spreadPlayerUpdate(SnackManDTO player) {
      //Validation here
      
      snackman.move(player.posX(), player.posY(), player.posZ());
      snackman.setDirY(player.dirY());
      messagingTemplate.convertAndSend("/topic/player", SnackManDTO.toSnackManDTO(snackman));
    }

}
