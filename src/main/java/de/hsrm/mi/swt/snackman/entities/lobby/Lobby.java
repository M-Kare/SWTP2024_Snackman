package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a lobby where players can gather to play a game together.
 */
public class Lobby {
      private String uuid;
      private String name;
      private PlayerClient adminClient;
      private boolean isGameStarted;
      private List<PlayerClient> members;

      public Lobby(String name, PlayerClient adminClient, boolean isGameStarted) {
            this.uuid = UUID.randomUUID().toString();
            this.name = name;
            this.adminClient = adminClient;
            this.isGameStarted = isGameStarted;
            this.members = new ArrayList<>();
            this.members.add(adminClient);
      }

      public String getUuid() {
            return uuid;
      }

      public String getName() {
            return name;
      }

      public String getAdminClientId() {
            return adminClient.getPlayerId();
      }

      public boolean isGameStarted() {
            return isGameStarted;
      }

      public List<PlayerClient> getMembers() {
            return members;
      }

      public void setGameStarted(boolean isGameStarted) {
            this.isGameStarted = isGameStarted;
      }

      

}
