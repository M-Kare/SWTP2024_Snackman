package de.hsrm.mi.swt.snackman.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontendLobbyMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(FrontendLobbyMessageService.class);

    public FrontendLobbyMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(FrontendMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, square {} to lobby {}", ev.eventType(), ev.changeType(),
                ev.square().toString(), ev.lobbyId());

        messagingTemplate.convertAndSend("/topic/lobbies/" + ev.lobbyId() + "/square", ev);
    }

    public void sendChickenEvent(FrontendChickenMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, chicken {} to lobby {}", ev.eventType(), ev.changeType(),
                ev.chicken().toString(), ev.lobbyId());

        messagingTemplate.convertAndSend("/topic/lobbies/" + ev.lobbyId() + "/chicken", ev);
    }
}
