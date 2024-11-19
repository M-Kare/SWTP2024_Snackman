package de.hsrm.mi.swt.snackman.controller.PlayerMovementTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PlayerMovementExampleController {

  @Autowired
    private SimpMessagingTemplate messagingTemplate;

  //Erhalte Messages von /topic/cube/update
    @MessageMapping("/topic/cube/player")
    public void spreadCubeUpdate(PlayerDTO player) {

        // Sende das Event explizit an das Ziel "/topic/cube"
        messagingTemplate.convertAndSend("/topic/cube", new PlayerDTO(0.0,2.0,0.0,0.0,0.0,0.0));
    }

}
