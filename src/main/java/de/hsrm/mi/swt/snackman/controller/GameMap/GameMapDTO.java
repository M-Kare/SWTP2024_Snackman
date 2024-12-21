package de.hsrm.mi.swt.snackman.controller.GameMap;

import de.hsrm.mi.swt.snackman.controller.Square.ChickenDTO;
import de.hsrm.mi.swt.snackman.controller.Square.SquareDTO;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GameMapDTO(int DEFAULT_SQUARE_SIDE_LENGTH, int DEFAULT_WALL_HEIGHT, List<SquareDTO> gameMap, List<ChickenDTO> chickens , List<GhostDTO> ghosts) {

    private final static Logger log = LoggerFactory.getLogger(GameMapDTO.class);

    public static GameMapDTO fromGameMap(GameMap gameMap) {
        List<SquareDTO> squareDTOs = Stream.of(gameMap.getGameMap())
                .flatMap(Stream::of)
                .map(SquareDTO::fromSquare)
                .collect(Collectors.toList());

        List<ChickenDTO> chickenDTOs = Stream.of(gameMap.getGameMap())
                .flatMap(Stream::of)
                .flatMap(square -> square.getMobs().stream()
                        .filter(mob -> mob instanceof Chicken)
                        .map(chicken -> ChickenDTO.fromChicken((Chicken) chicken)))
                .collect(Collectors.toList());

        List<GhostDTO> ghostDTOS = Stream.of(gameMap.getGameMap())
                .flatMap(Stream::of)
                .flatMap(square -> square.getMobs().stream()
                        .filter(mob -> mob instanceof Ghost)
                        .map(ghost -> GhostDTO.fromGhost((Ghost) ghost)))
                .toList();


        System.out.println( "Hier ist die Liste der GHOOOOOOSSSTTTTSSS"+ ghostDTOS);
        return new GameMapDTO(gameMap.getDEFAULT_SQUARE_SIDE_LENGTH(), gameMap.getDEFAULT_WALL_HEIGHT(), squareDTOs, chickenDTOs, ghostDTOS);
    }

}
