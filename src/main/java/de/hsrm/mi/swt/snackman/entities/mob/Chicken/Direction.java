package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

/**
 * The direction the chicken could move into
 * North: move up in maze into direction of x
 * South: move down in maze into direction of z
 */
public enum Direction {
      NORTH("0"), EAST("1"), SOUTH("2"), WEST("3");

      private final String indexOfList;

      private Direction(String indexOfList) {
            this.indexOfList = indexOfList;
      }

      /**
       * Return the Direction as an index used in chicken movement python script
       * @param indexOfList index of the list where the chicken want to move to
       * @return the direction the chicken want to moves to
       */
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
