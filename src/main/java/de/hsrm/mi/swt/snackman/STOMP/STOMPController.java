package de.hsrm.mi.swt.snackman.STOMP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;



@Controller
public class STOMPController {

    Logger logger = LoggerFactory.getLogger(STOMPController.class);

    @MessageMapping("/topic/coordination")
    @SendTo("/topic/coordination")
    public String postman(String msg) {
        logger.info(msg);
        return msg;
    }
}
