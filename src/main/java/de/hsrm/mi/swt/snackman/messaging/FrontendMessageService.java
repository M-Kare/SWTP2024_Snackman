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
        log.info("Send Event: eventType {}, changeTyp {}, chicken {}", ev.eventType(), ev.changeType(), ev.chicken().toString());

        messagingTemplate.convertAndSend("/topic/chicken", ev);
    }
}
