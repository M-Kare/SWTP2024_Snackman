package de.hsrm.mi.swt.snackman.entities.map;

public class EatableItem extends MapObject {

      private int kcal;

      @Override
      public String getType() {
            return "S";
      }

      public int getKcal() {
            return kcal;
      }
      
}
