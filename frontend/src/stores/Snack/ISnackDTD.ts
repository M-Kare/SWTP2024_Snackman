export interface ISnackDTD {
  position: Position,
  snackType: SnackType
}

interface Position{
  x: number,
  y: number,
  z: number
}

enum SnackType{
  CHERRY, STRAWBERRY, ORANGE
}
