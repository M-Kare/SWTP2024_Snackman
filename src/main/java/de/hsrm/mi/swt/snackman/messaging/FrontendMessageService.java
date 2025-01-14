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

    public void sendLeaderboardEvent(FrontendLeaderboardMessageEvent ev){
        log.debug("Send Event: eventType {}, changeTyp {}, leaderboardEntries {}", ev.eventType(), ev.changeType(), ev.leaderboardDTO());

        messagingTemplate.convertAndSend("/topic/leaderboard", ev);
    }

    public void sendLeaderboardEntryEvent(FrontendLeaderboardEntryMessageEvent ev){
        log.debug("Send Event: eventType {}, changeTyp {}, leaderboardEntry {}", ev.eventType(), ev.changeType(), ev.leaderboardEntry());

        messagingTemplate.convertAndSend("/topic/leaderboard", ev);
    }

    public void sendLobbyEvent(FrontendLobbyMessageEvent ev) {
        log.debug("Send Event: lobbies {}", ev.lobbies().toString());

        messagingTemplate.convertAndSend("/topic/lobbies", ev.lobbies());
    }

    public void sendChooseEvent ( FrontendChooseRoleEvent ev){
        log.debug("Send Event: lobby {}", ev.lobby().toString());
        messagingTemplate.convertAndSend("/topic/lobbies/chooseRole", ev.lobby());
    }
}
