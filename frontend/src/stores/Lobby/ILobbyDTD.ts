import type { IPlayerClientDTD } from "./IPlayerClientDTD"

export interface ILobbyDTD{
    lobbyId: string
    name: string
    adminClient: IPlayerClientDTD
    gameStarted: boolean
    members: Array<IPlayerClientDTD>
}
