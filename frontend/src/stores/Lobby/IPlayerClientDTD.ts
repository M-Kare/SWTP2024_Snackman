import type {ILobbyDTD} from "@/stores/Lobby/ILobbyDTD";

export interface IPlayerClientDTD{
    playerId: string
    playerName: string
    role: string
    joinedLobbyId?: string
}
