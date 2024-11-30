import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

export interface IGameMapDTD {
  default_WALL_HEIGHT: number,
  default_SQUARE_SIDE_LENGTH: number,
  gameMap: ISquareDTD[][]
}

export enum MapObjectType{
  WALL = 'WALL', FLOOR = 'FLOOR'
}
