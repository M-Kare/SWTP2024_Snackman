package de.hsrm.mi.swt.snackman.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontendMessageService {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(FrontendMessageService.class);

    public FrontendMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(FrontendMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, square {}", ev.eventType(), ev.changeType(),
                ev.square().toString());

        messagingTemplate.convertAndSend("/topic/square", ev);
    }

    public void sendChickenEvent(FrontendChickenMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, chicken {}", ev.eventType(), ev.changeType(), ev.chicken());

        messagingTemplate.convertAndSend("/topic/chicken", ev);
    }

    public void sendScriptGhostEvent(FrontendScriptGhostMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, scriptGhost {}", ev.eventType(), ev.changeType(), ev.scriptGhost());

        messagingTemplate.convertAndSend("/topic/scriptGhost", ev);
    }

    public void sendGhostEvent(FrontendGhostMessageEvent ev){
        log.info("Send Event: eventType {}, changeTyp {}, ghost {}", ev.eventType(), ev.changeType(), ev.ghost().toString());

        messagingTemplate.convertAndSend("/topic/ghost" , ev);
    }
}
