package de.hsrm.mi.swt.snackman.controller.Chicken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;

@Configuration
@Controller
public class ChickenController {

    private final Logger logger = LoggerFactory.getLogger(ChickenController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Bean
    Chicken createChicken() {
        return new Chicken();
    }

    // Receive messages from /topic/send/update
    @MessageMapping("/topic/chicken/update")
    public void spreadCubeUpdate(ChickenDTO chicken) {
        logger.info("Message" + chicken.toString() + " send.");

        // Send the event explicitly to the destination /topic/send
        messagingTemplate.convertAndSend("/topic/chicken", chicken);
    }

    @GetMapping("/test")
    public ChickenDTO getMethodName() {
        return ChickenDTO.fromChicken(createChicken());
    }

}
