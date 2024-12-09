import { defineStore } from 'pinia';
import { reactive } from 'vue';
import { Client } from '@stomp/stompjs';
import type { IPlayerClientDTD } from './IPlayerClientDTD';
import type { ILobbyDTD } from './ILobbyDTD';


const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = 'topic/lobbies'

export const useLobbiesStore = defineStore("lobbiesstore", () =>{
    const lobbydata = reactive({
        ok: false, 
        lobbies: [] as Array<ILobbyDTD>,
        currentPlayer: {
            playerId: '',
            playerName: ''
        } as IPlayerClientDTD //PlayerClient for each window, for check the sync
    })

    let stompclient: Client | null = null

    // Each Window have only one Admin Client, for create new lobby, then join in another lobby, they become the normal player
    async function createAdminPlayer(){
        const sessionNumber = Math.floor(Math.random() * 100000).toString()
        const playerClient: IPlayerClientDTD = {
            playerId: sessionNumber,
            playerName: 'Player Test',
        }

        lobbydata.currentPlayer.playerId = playerClient.playerId;
        lobbydata.currentPlayer.playerName = playerClient.playerName;

        console.log()
        return playerClient
    }

    async function updateLobbies(): Promise<void>{
        try{
            const url = `/api/lobbies`
            const response = await fetch(url)

            if(!response.ok) throw new Error(response.statusText)

            const data = await response.json()
            lobbydata.lobbies = data
            lobbydata.ok = true

            startLobbyLiveUpdate()

        } catch (error: any){
            console.error('Error:', error)
            lobbydata.ok = false
        }
    }

    async function startLobbyLiveUpdate(){
        if(stompclient && stompclient.active){
            console.log('STOMP-Client activated.')
            return
        }

        stompclient = new Client({ brokerURL: wsurl })

        stompclient.onConnect = (frame) => {
            stompclient!.subscribe(DEST, (message) => {
                const eventobject = JSON.parse(message.body)

                if(eventobject.eventType == 'LOBBY'){
                    updateLobbies()
                    console.log('Received lobby update:', eventobject);
                }
            })
        }

        stompclient.onStompError = (frame) => {
            console.error('Full STOMP Error Frame: ', frame)
        }

        stompclient.activate()
    }

    async function createLobby(lobbyName: string, adminClient: IPlayerClientDTD) {
        // if (!lobbydata.currentPlayer){
        //     console.error('Current player is not set. Cannot create lobby.')
        //     return
        // }

        // const adminClient = lobbydata.currentPlayer

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
                const lobby = await response.json()
                lobbydata.lobbies.push(lobby)
                lobbydata.ok = true
                
            } else {
                const errorText = await response.text()
                console.error('Failed to create lobby:', errorText)
                lobbydata.ok = false
            }

        } catch (error: any){
            console.error('Error:', error)
            lobbydata.ok = false
        }
    }



    return{
        lobbydata,
        updateLobbies,
        startLobbyLiveUpdate,
        createLobby,
        createAdminPlayer,
    }
})