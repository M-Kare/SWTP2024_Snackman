export interface IGhostDTD {
  id: number,
  posX: number,
  posY: number,
  posZ: number
  radius: number,
  speed: number
}

export interface IGhost {
  id: number,
  posX: number,
  posY: number,
  posZ: number,
  radius: number,
  speed: number,
  meshId: number
}
