import {Direction} from "@/stores/Chicken/IChickenDTD";

export interface IScriptGhostDTD {
  id: number,
  ghostPosX: number,
  ghostPosZ: number,
  lookingDirection: Direction
}

export interface IScriptGhost {
  id: number,
  ghostPosX: number,
  ghostPosZ: number,
  lookingDirection: Direction,
  meshId: number
}
