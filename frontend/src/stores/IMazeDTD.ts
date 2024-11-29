export interface IMazeDTD {
  'default-side-length': number
  map: {
    x: number
    z: number
    type: MapObjectType
  }[]
  height: number
}

export enum MapObjectType{
  WALL = 'WALL', FLOOR = 'FLOOR', SNACK = 'SNACK'
}
