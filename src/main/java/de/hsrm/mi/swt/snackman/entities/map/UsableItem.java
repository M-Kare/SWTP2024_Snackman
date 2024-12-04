package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;

public class UsableItem extends MapObject {

      /**
       * Constructs a new MapObject with the specified texture
       *
       * @param texture texture to be applied to the object
       */
      public UsableItem(TEXTURE texture) {
            super(texture);
      }

      public String getSymbol() {
            return "L";
      }
      
}
