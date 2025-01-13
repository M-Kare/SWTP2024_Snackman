import type {LeaderboardEntryDTD} from "@/stores/Leaderboard/LeaderboardDTD";

type EventType = 'SNACK' | 'CHICKEN' | 'CALORIES' | 'LEADERBOARD'
type ChangeType = 'CREATE' | 'UPDATE' | 'DELETE'

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
