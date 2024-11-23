package de.hsrm.mi.swt.snackman.controller.Chicken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChickenController {

    private final Logger logger = LoggerFactory.getLogger(ChickenController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //Erhalte Messages von /topic/chicken/update
    @MessageMapping("/topic/chicken/update")
    public void spreadCubeUpdate(ChickenDTO chicken) {
        logger.info("Message" + chicken.toString() + " send.");

        // Sende das Event explizit an das Ziel "/topic/chicken"
        messagingTemplate.convertAndSend("/topic/chicken", chicken);
    }
    
}
