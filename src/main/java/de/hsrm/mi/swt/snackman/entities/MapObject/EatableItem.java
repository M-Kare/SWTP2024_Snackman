package de.hsrm.mi.swt.snackman.entities.MapObject;

public class EatableItem extends MapObject {

      private int kcal;

      public EatableItem(int kcal, TEXTURE texture) {
          super(texture);
          this.kcal = kcal;
      }

      public int getKcal() {
            return kcal;
      }

      public void setKcal(int kcal) {
            this.kcal = kcal;
      }

      @Override
      public String getSymbol() {
            return "S";
      }
      
}
