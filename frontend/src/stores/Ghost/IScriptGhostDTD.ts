import {Direction} from "@/stores/Chicken/IChickenDTD";

export interface IScriptGhostDTD {
  id: number,
  scriptGhostPosX: number,
  scriptGhostPosZ: number,
  lookingDirection: Direction
}

export interface IScriptGhost {
  id: number,
  scriptGhostPosX: number,
  scriptGhostPosZ: number,
  lookingDirection: Direction,
  meshId: number
}
