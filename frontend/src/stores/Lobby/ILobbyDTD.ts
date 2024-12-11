import type { IPlayerClientDTD } from "./IPlayerClientDTD"

export interface ILobbyDTD{
    uuid: string
    name: string
    adminClient: IPlayerClientDTD
    isGameStarted: boolean
    members: Array<IPlayerClientDTD>
}