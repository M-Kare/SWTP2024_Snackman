export interface ISnackDTD {
  id: number,
  position: Position,
  snackType: SnackType
}

interface Position{
  x: number,
  y: number,
  z: number
}

export enum SnackType{
  CHERRY = 'CHERRY', STRAWBERRY = 'STRAWBERRY', ORANGE = 'ORANGE'
}
