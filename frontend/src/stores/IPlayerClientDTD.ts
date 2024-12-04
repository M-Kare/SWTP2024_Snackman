export enum Role{
    SNACKMAN = "SNACKMAN",
    GHOST =  "GHOST"
}

export interface IPlayerClientDTD{
    playerId: string
    playerName: string
    role: Role
}