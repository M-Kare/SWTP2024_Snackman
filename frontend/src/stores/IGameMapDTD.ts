import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

export interface IGameMapDTD {
  DEFAULT_WALL_HEIGHT: number,
  DEFAULT_SQUARE_SIDE_LENGTH: number,
  gameMap: Array<ISquareDTD>
}

export enum MapObjectType {
  WALL = 'WALL', FLOOR = 'FLOOR'
}

export interface IGameMap {
  DEFAULT_WALL_HEIGHT: number,
  DEFAULT_SQUARE_SIDE_LENGTH: number,
  gameMap: Map<number, ISquareDTD>
}
