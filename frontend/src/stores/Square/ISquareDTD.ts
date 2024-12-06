import type {ISnack, ISnackDTD} from "@/stores/Snack/ISnackDTD";
import {MapObjectType} from "@/stores/IGameMapDTD";
import type {IChicken, IChickenDTD} from "@/stores/Chicken/IChickenDTD";

export interface ISquareDTD {
  id: number,
  indexX: number,
  indexZ: number,
  type: MapObjectType,
  snack: ISnackDTD,
  chickens: IChickenDTD[]
}

export interface ISquare {
  id: number,
  indexX: number,
  indexZ: number,
  type: MapObjectType,
  snack: ISnack,
  chickens: IChicken[]
}


