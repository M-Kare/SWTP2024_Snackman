import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

type EventType = 'SNACK'
type ChangeType = 'CREATE' | 'UPDATE' | 'DELETE'

export interface IFrontendMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  square: ISquareDTD
}
