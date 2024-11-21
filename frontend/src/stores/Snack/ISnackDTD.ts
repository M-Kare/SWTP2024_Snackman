export interface ISnackDTD {
  height: number,
  position: Position,
  snackType: SnackType
}

export interface Position{
  x: number,
  z: number
}

export enum SnackType{
  CHERRY = 'CHERRY', STRAWBERRY = 'STRAWBERRY', ORANGE = 'ORANGE'
}
