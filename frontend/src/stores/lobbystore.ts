import { defineStore } from 'pinia';
import type { IPlayerClientDTD } from './IPlayerClientDTD';
import { reactive } from 'vue';
import type { ILobbyDTD } from './ILobbyDTD';

export const useLobbyStore = defineStore("lobbystore", () =>{
    const lobbydata = reactive({
        ok: false, 
        lobbylist: [] as Array<ILobbyDTD>
    })
})