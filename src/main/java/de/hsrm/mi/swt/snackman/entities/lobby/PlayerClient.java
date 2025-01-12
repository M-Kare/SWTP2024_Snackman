package de.hsrm.mi.swt.snackman.entities.lobby;


/**
 * Represents a client or player in the lobby system. 
 */
public class PlayerClient {
      private String playerId;
      private String playerName;
      private ROLE role;
      
      // TODO is not called anywhere, currently removable
      public PlayerClient(String playerId, String playerName, ROLE role) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.role = role;
      }

      public PlayerClient(String playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
      }

      public String getPlayerId() {
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
