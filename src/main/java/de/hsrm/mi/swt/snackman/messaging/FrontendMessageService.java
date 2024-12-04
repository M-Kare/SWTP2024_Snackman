package de.hsrm.mi.swt.snackman.messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontendMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public FrontendMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(FrontendMessageEvent ev) {
        messagingTemplate.convertAndSend("/topic/square", ev);
    }
}
