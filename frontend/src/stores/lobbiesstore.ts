import { defineStore } from 'pinia';
import { reactive } from 'vue';
import { Client } from '@stomp/stompjs';
import type { IPlayerClientDTD } from './IPlayerClientDTD';
import type { ILobbyDTD } from './ILobbyDTD';

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = 'topic/lobbies'

export const useLobbiesStore = defineStore("lobbiesstore", () =>{
    const lobbydata = reactive({
        lobbies: [] as Array<ILobbyDTD>,
        currentPlayer: {
            playerId: '',
            playerName: '',
            role: '',
        } as IPlayerClientDTD //PlayerClient for each window, for check the sync
    })

    let stompclient: Client | null = null

    // Each Window have only one Admin Client, for create new lobby
    // then join in another lobby, they become the normal player
    // For Test all Players have the same name 'Player Test'
    async function createPlayer(name: string){
        const newPlayerClient: IPlayerClientDTD = {
            playerId: '',
            playerName: name,
            role: '',
        }

        try{
            const url = `/api/playerclients/create?name=${name}`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newPlayerClient)
            })

            if(response.ok){
                const newPlayer = await response.json()
                lobbydata.currentPlayer = newPlayer
            } else {
                const errorText = await response.text()
                console.error('Failed to create a new player client:', errorText)
            }

            return newPlayerClient

        } catch (error: any){
            console.error('Error: ', error)
            
        }
        
    }

    async function updateLobbies(): Promise<void>{
        try{
            const url = `/api/lobbies`
            const response = await fetch(url)

            if(!response.ok) throw new Error(response.statusText)

            const data = await response.json()
            lobbydata.lobbies = data

            startLobbyLiveUpdate()

        } catch (error: any){
            console.error('Error:', error)
        }
    }

    async function startLobbyLiveUpdate(){
        if(stompclient && stompclient.active){
            console.log('STOMP-Client activated.')
            return
        }

        stompclient = new Client({ brokerURL: wsurl })

        stompclient.onConnect = (frame) => {
            stompclient!.subscribe(DEST, async (message) => {
                const eventobject = JSON.parse(message.body)

                if(eventobject.eventType == 'LOBBY'){
                    console.log('Received lobby update:', eventobject)
                    await updateLobbies()
                }
            })
        }

        stompclient.onStompError = (frame) => {
            console.error('Full STOMP Error Frame: ', frame)
        }

        stompclient.activate()
    }

    async function createLobby(lobbyName: string, adminClient: IPlayerClientDTD): Promise<ILobbyDTD | null> {
        const newLobby: ILobbyDTD = {
            uuid: '',
            name: lobbyName,
            adminClient: adminClient,
            isGameStarted: false, 
            members: [adminClient]
        }

        try{
            const url = `/api/lobbies/create?name=` + lobbyName + `&creatorUuid=` + adminClient.playerId
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newLobby)
            })

            if(response.ok){
                const lobby: ILobbyDTD = await response.json()
                lobbydata.lobbies.push(lobby)
                await updateLobbies()
                return lobby
            } else {
                console.error('Failed to create lobby. Status:', response.status)
                return null
            }

        } catch (error: any){
            console.error('Error:', error)
            return null
        }
    }

    async function joinLobby(lobbyId: string, playerId: string): Promise<ILobbyDTD | null> {
        try{
            const currentLobby = await fetchLobbyById(lobbyId);
            if (currentLobby && currentLobby.members.length >= 4) {
                console.error('Lobby is full. Cannot join.');
                alert(`Lobby "${currentLobby.name}" is full!`);
                return null;
            }

            const url = `/api/lobbies/` + lobbyId + `/join?playerId=` + playerId
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            })

            if(response.status == 409){
                console.error("Game has already started. Cannot joint the lobby.")
                return null
            }

            if(response.ok){
                const lobby: ILobbyDTD = await response.json()
                console.log('lobbiesStore joinLobby successful')
                await updateLobbies()
                return lobby
            } else {
                console.error('Failed to join lobby. Status:', response.status)
                return null
            }

        } catch (error: any){
            console.error('Error:', error)
            return null
        }
    }

    async function fetchLobbyById(lobbyId: String): Promise<ILobbyDTD | null> {
        try{
            const url = `/api/lobbies/` + lobbyId
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
            })

            if(!response.ok){
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            const lobby: ILobbyDTD = await response.json()
            console.log('Fetched Lobby: ', lobby)
            return lobby
        } catch (error: any){
            console.error('Error:', error)
            return null
        } 
    }

    return{
        lobbydata,
        createPlayer,
        updateLobbies,
        startLobbyLiveUpdate,
        createLobby,
        joinLobby,
        fetchLobbyById,
    }
})