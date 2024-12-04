export enum ROLE{
    SNACKMAN = "SNACKMAN",
    GHOST =  "GHOST"
}

export interface IPlayerClientDTD{
    playerId: String
    playerName: String
    role: ROLE
}