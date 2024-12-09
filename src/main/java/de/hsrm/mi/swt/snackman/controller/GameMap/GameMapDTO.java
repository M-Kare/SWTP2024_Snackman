package de.hsrm.mi.swt.snackman.controller.GameMap;

import de.hsrm.mi.swt.snackman.controller.Square.SquareDTO;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GameMapDTO(int DEFAULT_SQUARE_SIDE_LENGTH, int DEFAULT_WALL_HEIGHT, List<SquareDTO> gameMap) {

    public static GameMapDTO fromGameMap(GameMap gameMap) {
        List<SquareDTO> squareDTOs = Stream.of(gameMap.getGameMap())
                .flatMap(Stream::of)
                .map(SquareDTO::fromSquare)
                .collect(Collectors.toList());

        return new GameMapDTO(gameMap.getDEFAULT_SQUARE_SIDE_LENGTH(), gameMap.getDEFAULT_WALL_HEIGHT(), squareDTOs);
    }

}
