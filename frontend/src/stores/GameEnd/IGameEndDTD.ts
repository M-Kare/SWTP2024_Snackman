export interface IGameEndDTD {
  role: ROLE
  timePlayed: number;
  kcalCollected: number
}

export enum ROLE {
  SNACKMAN= "SNACKMAN", GHOST="GHOST"
}
