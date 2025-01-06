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

    private int lmao = 0;

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
                messages.add(new Message<>(EventEnum.MobUpdate, new MobUpdateMessage(mob.getPosition(), mob.getQuat(), mob.getRadius(), mob.getSpeed(), client)));
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
            lmao++;
            if (lmao > 100) {
                mapService.respawnSnacks(lobbyService.getGameMapByLobbyId(lobby.getLobbyId()));
                lmao = 0;
                System.out.println("The deed has been done");
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
