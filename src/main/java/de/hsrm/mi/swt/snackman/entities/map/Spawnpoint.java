package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;

public class Spawnpoint extends MapObject {

      /**
       * Constructs a new MapObject with the specified texture
       *
       * @param texture texture to be applied to the object
       */
      public Spawnpoint(TEXTURE texture) {
            super(texture);
      }

      public String getSymbol() {
            return "L";
      }
      
}
