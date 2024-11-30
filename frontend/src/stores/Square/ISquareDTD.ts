import type {ISnackDTD} from "@/stores/Snack/ISnackDTD";
import {MapObjectType} from "@/stores/IMazeDTD";

export interface ISquareDTD {
  id: number,
  indexX: number,
  indexZ: number,
  type: MapObjectType,
  snacks: Array<ISnackDTD>,
}


