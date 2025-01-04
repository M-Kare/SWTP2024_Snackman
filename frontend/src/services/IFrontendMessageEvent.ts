import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import type {IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import type {LeaderboardEntryDTD} from "@/stores/Leaderboard/LeaderboardDTD";

type EventType = 'SNACK' | 'CHICKEN' | 'CALORIES' | 'LEADERBOARD'
type ChangeType = 'CREATE' | 'UPDATE' | 'DELETE'

export interface IFrontendMessageEvent {
  eventType: EventType,
  changeType: ChangeType,
  square: ISquareDTD
}

export interface IFrontendChickenMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  chicken: IChickenDTD,
}

export interface IFrontendCaloriesMessageEvent {
  eventType: EventType,
  changeType: ChangeType,
  calories: number,
  message?: string
}

export interface IFrontendLeaderboardMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  leaderboardEntries: LeaderboardEntryDTD[],
}

export interface IFrontendLeaderboardEntryMessageEvent{
  eventType: EventType,
  changeType: ChangeType,
  leaderboardEntry: LeaderboardEntryDTD,
}
