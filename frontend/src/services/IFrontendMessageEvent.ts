
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
