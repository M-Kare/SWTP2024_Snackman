import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

export interface IMazeDTD {
  default_WALL_HEIGHT: number,
  default_SQUARE_SIDE_LENGTH: number,
  gameMap: Array<ISquareDTD>
}

export enum MapObjectType{
  WALL = 'WALL', FLOOR = 'FLOOR'
}
