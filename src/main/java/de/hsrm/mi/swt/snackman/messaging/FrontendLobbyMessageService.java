package de.hsrm.mi.swt.snackman.messaging;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontendLobbyMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(FrontendLobbyMessageService.class);

    //TODO LobbyId to messages
    public FrontendLobbyMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(FrontendMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, square {} to lobby {}", ev.eventType(), ev.changeType(),
                ev.square().toString(), ev.lobbyId());

        messagingTemplate.convertAndSend("/topic/lobby/" + ev.lobbyId() + "/square", ev);
    }

    public void sendChickenEvent(FrontendChickenMessageEvent ev) {
        log.info("Send Event: eventType {}, changeTyp {}, chicken {} to lobby {}", ev.eventType(), ev.changeType(),
                ev.chicken().toString(), ev.lobbyId());

        messagingTemplate.convertAndSend("/topic/lobby/" + ev.lobbyId() + "/chicken/", ev);
    }
}
