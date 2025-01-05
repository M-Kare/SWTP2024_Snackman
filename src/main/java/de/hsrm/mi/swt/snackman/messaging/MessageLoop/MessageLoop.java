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

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

@Service
public class MessageLoop {
    
    @Autowired
    private LobbyManagerService lobbyService;

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
            for(String client : lobby.getClientMobMap().keySet()){
                Mob mob = lobby.getClientMobMap().get(client);
                messages.add(new Message<>(EventEnum.MobUpdate, new MobUpdateMessage(mob.getPosition(), mob.getQuat(), mob.getRadius(), mob.getSpeed(), client)));
            }
            if(changedSquares.get(lobby.getLobbyId()) != null){
                for(Square square : changedSquares.get(lobby.getLobbyId())){
                    messages.add(new Message<>(EventEnum.SquareUpdate, new SquareUpdateMessage(square)));
                }
                changedSquares.remove(lobby.getLobbyId());
            }
            if(changedChicken.get(lobby.getLobbyId()) != null){
                for(Chicken chicken : changedChicken.get(lobby.getLobbyId())){
                    messages.add(new Message<>(EventEnum.ChickenUpdate, ChickenUpdateMessage.fromChicken(chicken)));
                }
                changedChicken.remove(lobby.getLobbyId());
            }
            //TODO: Kollision Messages

            if(messages.isEmpty()){
                return;
            }
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getLobbyId() + "/update", messages);
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
