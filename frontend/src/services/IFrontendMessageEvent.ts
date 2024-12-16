import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import type {IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import type {IGhostDTD} from "@/stores/Ghost/IGhostDTD";

type EventType = 'SNACK' | 'CHICKEN'
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
