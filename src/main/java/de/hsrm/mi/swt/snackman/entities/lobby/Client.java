package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.UUID;

public class Client {
      private UUID playerId;
      private String playerName;
      private ROLE role;
      
      public Client(UUID playerId, String playerName, ROLE role) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.role = role;
      }

      public Client(UUID playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
      }

      public UUID getPlayerId() {
            return playerId;
      }

      public String getPlayerName() {
            return playerName;
      }

      public ROLE getRole() {
            return role;
      }

      public void setRole(ROLE role) {
            this.role = role;
      }

      
      
}
