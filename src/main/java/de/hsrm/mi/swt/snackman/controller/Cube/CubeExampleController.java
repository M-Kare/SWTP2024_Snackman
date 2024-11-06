package de.hsrm.mi.swt.snackman.controller.Cube;

import de.hsrm.mi.swt.snackman.messaging.ChangeType;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendNachrichtEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CubeExampleController {

    private final Logger logger = LoggerFactory.getLogger(CubeExampleController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //Erhalte Messages von /topic/cube/update
    @MessageMapping("/topic/cube/update")
    public void spreadCubeUpdate(CubeDTO cube) {
        logger.info("Message" + cube.toString() + " send.");

        // Erstelle das FrontendNachrichtEvent mit dem Cube-Objekt
        FrontendNachrichtEvent event = new FrontendNachrichtEvent(EventType.CUBE, ChangeType.UPDATE, cube);

        // Sende das Event explizit an das Ziel "/topic/cube"
        messagingTemplate.convertAndSend("/topic/cube", event);
    }
}