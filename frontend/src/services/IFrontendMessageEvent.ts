import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import type {IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import type {IGhostDTD} from "@/stores/Ghost/IGhostDTD";
import type {IScriptGhostDTD} from "@/stores/Ghost/IScriptGhostDTD";

type EventType = 'SNACK' | 'CHICKEN' | 'GHOST' | 'SCRIPT_GHOST'
type ChangeType = 'CREATE' | 'UPDATE' | 'DELETE'

export interface IFrontendMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  square: ISquareDTD
}

export interface IFrontendChickenMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  chicken: IChickenDTD,
}

export interface IFrontendGhostMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  ghost: IGhostDTD,
}

export interface IFrontendScriptGhostMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  scriptGhost: IScriptGhostDTD,
}
