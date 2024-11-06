import type {ICubeDTD} from "@/stores/ICubeDTD";

export interface IFrontendNachrichtEvent {
  cubeDTO: ICubeDTD
  eventType: String
  changeType: String
}
