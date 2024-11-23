import type { ISquareDTD } from "./ISquareDTD";

export interface IChickenDTD {
    thickness: ChickenThickness;
    square: ISquareDTD;
}

export enum ChickenThickness {
    THIN, SLIGHTLY_THICK, MEDIUM, HEAVY, VERY_HEAVY
}