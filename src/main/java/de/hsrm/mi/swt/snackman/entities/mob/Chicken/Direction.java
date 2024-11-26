package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

public enum Direction {
      NORTH("0"), EAST("1"), SOUTH("2"), WEST("3");

      private final String indexOfList;

      private Direction(String indexOfList) {
            this.indexOfList = indexOfList;
      }

      public static Direction getDirection(String indexOfList){
            if(indexOfList == "0")
                  return Direction.NORTH;
            else if (indexOfList == "1")
                  return Direction.EAST;
            else if (indexOfList == "2")
                  return Direction.SOUTH;
            else
                  return Direction.WEST;
      }
}
