export interface IChickenDTD {
    thickness: ChickenThickness;
}

export interface IChicken {
  thickness: ChickenThickness;
  meshId: number
}

export enum ChickenThickness {
    THIN, SLIGHTLY_THICK, MEDIUM, HEAVY, VERY_HEAVY
}
