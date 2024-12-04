import type { IPlayerClientDTD } from "./IPlayerClientDTD"

export interface ILobbyDTD{
    uuid: String
    name: String
    adminClient: IPlayerClientDTD
    isGameStarted: boolean
    members: IPlayerClientDTD[]
}