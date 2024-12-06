export interface IChickenDTD {
    thickness: ChickenThickness;
}

export interface IChicken {
  thickness: ChickenThickness;
  meshId: number
}

export enum ChickenThickness {
    THIN = 1, SLIGHTLY_THICK = 1.25, MEDIUM = 1.5, HEAVY = 1.75, VERY_HEAVY = 2
}
