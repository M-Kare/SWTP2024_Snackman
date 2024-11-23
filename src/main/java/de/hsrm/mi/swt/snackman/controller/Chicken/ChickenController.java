package de.hsrm.mi.swt.snackman.controller.Chicken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;

@Controller
public class ChickenController {

    private final Logger logger = LoggerFactory.getLogger(ChickenController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Bean
    public Chicken createChicken() {
        return new Chicken();
    }

    // Erhalte Messages von /topic/chicken/update
    @MessageMapping("/topic/chicken/update")
    public void spreadCubeUpdate(ChickenDTO chicken) {
        logger.info("Message" + chicken.toString() + " send.");

        // Sende das Event explizit an das Ziel "/topic/chicken"
        messagingTemplate.convertAndSend("/topic/chicken", chicken);
    }

    @GetMapping("/test")
    public String getMethodName() {
        return createChicken().chooseWalkingPath();
    }

}
