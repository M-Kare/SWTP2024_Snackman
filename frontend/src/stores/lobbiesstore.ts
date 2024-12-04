import { defineStore } from 'pinia';
import type { IPlayerClientDTD } from './IPlayerClientDTD';
import { reactive } from 'vue';
import type { ILobbyDTD } from './ILobbyDTD';

export const useLobbiesStore = defineStore("lobbiesstore", () =>{
    const lobbydata = reactive({
        ok: false, 
        lobbies: [] as Array<ILobbyDTD>
    })

    async function fetchLobbies(){
        try{
            const url = "/api/lobbies"
            const response = await fetch(url)

            if(!response.ok){
                console.error('Error upload lobby list')
                lobbydata.ok = false
                return
            }

            const data = await response.json()
            lobbydata.lobbies = data
            lobbydata.ok = true

        } catch (error: any){
            console.error('Error:', error)
            lobbydata.ok = false
        }
    }

    async function createLobby(lobbyName: string, adminClient: IPlayerClientDTD) {
        const newLobby = {
            name: lobbyName,
            adminClient: adminClient.playerId,
            isGameStarted: false, 
            members: [adminClient]
        }

        try{
            const url = "/api/lobbies/create?name=" + lobbyName + "&createUuid=" + adminClient.playerId
            const response = await fetch(url, {method: 'POST'})

            if(response.ok){
                const lobby = await response.json()
                lobbydata.lobbies.push(lobby)
                lobbydata.ok = true
            } else {
                console.log('Can not create new lobby!')
                lobbydata.ok = false
            }

        } catch (error: any){
            console.error('Error:', error)
            lobbydata.ok = false
        }
    }

    return{
        lobbydata,
        fetchLobbies,
        createLobby
    }
})