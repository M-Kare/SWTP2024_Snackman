import type { LeaderboardEntryDTD } from "@/stores/Leaderboard/LeaderboardDTD";
import {EventType} from "@/stores/messaging/IMessageDTD";

export interface IFrontendLeaderboardMessageEvent {
  eventType: EventType,
  leaderboardEntries: LeaderboardEntryDTD[],
}

export interface IFrontendLeaderboardEntryMessageEvent {
  eventType: EventType,
  leaderboardEntry: LeaderboardEntryDTD,
}
