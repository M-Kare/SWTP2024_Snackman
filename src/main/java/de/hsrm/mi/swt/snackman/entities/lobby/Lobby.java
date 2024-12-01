package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {
      private UUID uuid;
      private String name;
      private UUID adminClientId;
      private boolean isGameStarted;
      private List<Client> members;

      public Lobby(String name, UUID adminId, boolean isGameStarted) {
            this.uuid = UUID.randomUUID();
            this.name = name;
            this.adminClientId = adminId;
            this.isGameStarted = isGameStarted;
            this.members = new ArrayList<>();
      }

      public UUID getUuid() {
            return uuid;
      }

      public String getName() {
            return name;
      }

      public UUID getAdminClientId() {
            return adminClientId;
      }

      public boolean isGameStarted() {
            return isGameStarted;
      }

      public List<Client> getMembers() {
            return members;
      }

      public void setGameStarted(boolean isGameStarted) {
            this.isGameStarted = isGameStarted;
      }

      

}
