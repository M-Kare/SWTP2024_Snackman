package de.hsrm.mi.swt.snackman.messaging.MessageLoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import de.hsrm.mi.swt.snackman.services.MapService;

@Service
public class MessageLoop {
    
    @Autowired
    private LobbyManagerService lobbyService;

    @Autowired
    private MapService mapService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    Logger log = LoggerFactory.getLogger(MessageLoop.class);

    private Map<String, List<Square>> changedSquares = new HashMap<>();
    // private Map<String, List<KollisionEvent>> kollisions;

    private Map<String, List<Chicken>> changedChicken = new HashMap<>();

    @Scheduled(fixedRate=50)
    public void messageLoop(){
        List<Lobby> lobbys = lobbyService.getAllLobbies();
        if(lobbys.isEmpty()){
            return;
        }
        for (Lobby lobby : lobbys){
            if(!lobby.isGameStarted()){
                continue;
            }
            List<Message> messages = new ArrayList<>();
            List<Square> squareQueue = changedSquares.get(lobby.getLobbyId());
            changedSquares.remove(lobby.getLobbyId());
            List<Chicken> chickenQueue = changedChicken.get(lobby.getLobbyId());
            changedChicken.remove(lobby.getLobbyId());
            for(String client : lobby.getClientMobMap().keySet()){
                Mob mob = lobby.getClientMobMap().get(client);

                switch (mob) {
                    case SnackMan snackMan -> {
                        messages.add(new Message<>(EventEnum.MobUpdate, new MobUpdateMessage(snackMan.getPosition(),
                        snackMan.getQuat(), snackMan.getRadius(), snackMan.getSpeed(), client, snackMan.getSprintTimeLeft(),
                                snackMan.isSprinting(), snackMan.isInCooldown(), snackMan.getCurrentCalories(), snackMan.getCurrentCalories() >= GameConfig.MAX_KALORIEN ? GameConfig.MAX_KALORIEN_MESSAGE : null
                        )));
                    }
                    default -> {
                        messages.add(new Message<>(EventEnum.MobUpdate, new MobUpdateMessage(mob.getPosition(),
                                mob.getQuat(), mob.getRadius(), mob.getSpeed(), client, 0,
                                false, false, 0, null)));
                    }
                }
            }
            if(squareQueue != null){
                for(Square square : squareQueue){
                    messages.add(new Message<>(EventEnum.SquareUpdate, new SquareUpdateMessage(square)));
                }
            }
            if(chickenQueue != null){
                for(Chicken chicken : chickenQueue){
                    messages.add(new Message<>(EventEnum.ChickenUpdate, ChickenUpdateMessage.fromChicken(chicken)));
                }
            }
            //TODO: Kollision Messages

            if(messages.isEmpty()){
                return;
            }
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getLobbyId() + "/update", messages);
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lobby.getTimeSinceLastSnackSpawn()) > GameConfig.TIME_FOR_SNACKS_TO_RESPAWN) {
                mapService.respawnSnacks(lobbyService.getGameMapByLobbyId(lobby.getLobbyId()));
                lobby.setTimeSinceLastSnackSpawn(System.currentTimeMillis());
            }
        }
    }

    public void addSquareToQueue(Square square, String lobbyId){
        if(changedSquares.containsKey(lobbyId)){
            changedSquares.get(lobbyId).add(square);
        } else {
            List<Square> temp = new ArrayList<>();
            temp.add(square);
            changedSquares.put(lobbyId, temp);
        }
    }

    public void addChickenToQueue(Chicken chicken, String lobbyId) {
        if(changedChicken.containsKey(lobbyId)){
            changedChicken.get(lobbyId).add(chicken);
        } else {
            List<Chicken> temp = new ArrayList<>();
            temp.add(chicken);
            changedChicken.put(lobbyId, temp);
        }
    }
}
