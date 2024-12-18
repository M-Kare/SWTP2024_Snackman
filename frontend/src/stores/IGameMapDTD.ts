import type {ISquare, ISquareDTD} from "@/stores/Square/ISquareDTD";
import type {IChicken, IChickenDTD} from "@/stores/Chicken/IChickenDTD";

export interface IGameMapDTD {
  DEFAULT_WALL_HEIGHT: number,
  DEFAULT_SQUARE_SIDE_LENGTH: number,
  gameMap: Array<ISquareDTD>,
  chickens: IChickenDTD[]
}

export enum MapObjectType {
  WALL = 'WALL', FLOOR = 'FLOOR'
}

export interface IGameMap {
  DEFAULT_WALL_HEIGHT: number,
  DEFAULT_SQUARE_SIDE_LENGTH: number,
  gameMap: Map<number, ISquare>
  chickens: IChicken[]
}
