package de.hsrm.mi.swt.snackman.entities.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Square {

      private final int DEFAULT_SIDE_LENGTH = 1;
      private int indexX;
      private int indexZ;
      private List<MapObject> contents;

      /**
       * initializes the square with the
       * 
       * @param indexX x of the square in the map
       * @param indexZ y of the square in the map
       */
      public Square(int indexX, int indexZ) {
            this.indexX = indexX;
            this.indexZ = indexZ;
            contents = new ArrayList<>();
      }

      public void addMapObject(MapObject mapObject) {
            this.contents.add(mapObject);
      }

      public boolean hasSnack() {
            return this.contents.stream().anyMatch(mapObject -> mapObject.getType().equals("S"));
      }

      public int getDEFAULT_SIDE_LENGTH() {
            return DEFAULT_SIDE_LENGTH;
      }

      public int getIndexX() {
            return indexX;
      }

      public int getIndexZ() {
            return indexZ;
      }

      /**
       * 
       * @return the dominant type of MapObject
       */
      public String getDominantType() {
            if (this.contents.stream().anyMatch(mapObject -> mapObject.getType().equals("W")))
                  return "W";
            else if (this.contents.stream().anyMatch(mapObject -> mapObject.getType().equals("G")))
                  return "G";
            else if (this.contents.stream().anyMatch(mapObject -> mapObject.getType().equals("S")))
                  return "S";
            else
                  return "L";
      }

      public int getKcal() {
            int kcal = 0;
            for (MapObject mapObject : this.contents) {
                  if (mapObject instanceof EatableItem) {
                        kcal += ((EatableItem) mapObject).getKcal();
                  }
            }
            return kcal;
      }

      public void deleteAllSnacks() {
            List<EatableItem> snacksToRemove = contents.stream()
                        .filter(mapObject -> mapObject instanceof EatableItem)
                        .map(mapObject -> (EatableItem) mapObject)
                        .collect(Collectors.toList());

            contents.removeAll(snacksToRemove);
      }

}
