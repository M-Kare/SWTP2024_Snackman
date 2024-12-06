export interface IChickenDTD {
  id: number,
  thickness: ChickenThickness;
}

export interface IChicken {
  id: number,
  thickness: ChickenThickness;
  meshId: number
}

export enum ChickenThickness {
  THIN = 1, SLIGHTLY_THICK = 1.25, MEDIUM = 1.5, HEAVY = 1.75, VERY_HEAVY = 2
}
