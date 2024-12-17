import type { IPlayerClientDTD } from "./IPlayerClientDTD"

export interface ILobbyDTD{
    uuid: string
    name: string
    adminClient: IPlayerClientDTD
    gameStarted: boolean
    members: Array<IPlayerClientDTD>
}