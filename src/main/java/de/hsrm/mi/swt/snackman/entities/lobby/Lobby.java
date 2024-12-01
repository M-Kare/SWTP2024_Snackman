package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {
      private String uuid;
      private String name;
      private String adminClientId;
      private boolean isGameStarted;
      private List<Client> members;

      public Lobby(String name, String adminId, boolean isGameStarted) {
            this.uuid = UUID.randomUUID().toString();
            this.name = name;
            this.adminClientId = adminId;
            this.isGameStarted = isGameStarted;
            this.members = new ArrayList<>();
      }

      public String getUuid() {
            return uuid;
      }

      public String getName() {
            return name;
      }

      public String getAdminClientId() {
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
