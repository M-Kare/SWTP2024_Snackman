import type {ISnackDTD, Position} from "@/stores/Snack/ISnackDTD";

export interface ISquareDTD {
  snacks: Array<ISnackDTD>,
  position: Position,
  sideLength: number
}
