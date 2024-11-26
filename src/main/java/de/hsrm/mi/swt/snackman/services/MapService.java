package de.hsrm.mi.swt.snackman.services;

import java.util.ArrayList;
import java.util.List;

import de.hsrm.mi.swt.snackman.entities.MapObject.Egg;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Direction;

public class MapService {

      private Square[][] maze;

      public MapService(Square[][] maze) {
            this.maze = maze;
      }

      /**
       * @todo implement set egg into map
       * @param indexX
       * @param indexZ
       * @param newEgg
       */
      public static void layEgg(int indexX, int indexZ, Egg newEgg) {

      }

      /**
       * @todo implement richtiges square zurückgeben
       * 
       * Gives back the new square-position of the chicken
       * @param currentChickenPosition the current position of the chicken
       * @param direction in which the chicken decided to go
       * @return the square which is laying in the direction of the currentPosition
       */
      public Square getNewPosition(Square currentChickenPosition, Direction direction) {
            if (direction == Direction.NORTH)
                  return maze[0][0];
            else if (direction == Direction.NORTH)
                  return maze[0][0];
            else if (direction == Direction.NORTH)
                  return maze[0][0];
            else
                  return maze[0][0];
      }

      public Square[][] getMaze() {
            return maze;
      }

      /**
       * @todo implement richtiges square holen
       * 
       * @param currentPosition the square the chicken is standing on top of
       * @return a list of 8 square which are around the current square
       */
      public List<String> getSquaresVisibleForChicken(Square currentPosition) {
            List<String> squares = new ArrayList<>();
            squares.add(this.maze[0][0].getDominantType());
            return squares;
      }

      /**
       * @param currentPosition for which all snacks have been eaten
       */
      public void deleteConsumedSnacks(Square currentPosition) {
            currentPosition.deleteAllSnacks();
      }

      

}
