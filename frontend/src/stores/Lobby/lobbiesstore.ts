import { defineStore } from 'pinia';
import { reactive } from 'vue';
import { Client } from '@stomp/stompjs';
import type { ILobbyDTD } from './ILobbyDTD';
import type { IPlayerClientDTD } from './IPlayerClientDTD';

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/lobbies'

export const useLobbiesStore = defineStore('lobbiesstore', () =>{
    let stompclient: Client

    const lobbydata = reactive({
        lobbies: [] as Array<ILobbyDTD>,
        currentPlayer: {
            playerId: '',
            playerName: '',
            role: '',
        } as IPlayerClientDTD //PlayerClient for each window, for check the sync
    })

    // For Test all Players have the same name 'Player Test'
    /**
     * Creates a new player client.
     * @param name The name of the player.
     * @returns The newly created player client object.
     */
    async function createPlayer(name: string){
        const newPlayerClient: IPlayerClientDTD = {
            playerId: '',
            playerName: name,
            role: '',
        }

        try{
            const url = `/api/lobbies/create/player`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(name),
            })

            if(response.ok){
                const newPlayer = await response.json()

                // Only update the role if the backend returns it
                if (newPlayer.role) {
                    newPlayerClient.role = newPlayer.role
                }

                Object.assign(lobbydata.currentPlayer, newPlayer)
            } else {
                console.error(`Failed to create a new player client: ${response.statusText}`)
            }

            return newPlayerClient

        } catch (error: any){
            console.error('Error: ', error)

        }

    }

    /**
     * Fetch Lobby-List from Backend
     */
    async function fetchLobbyList(){
        try{
            const url = `/api/lobbies`
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
            })

            if(response.ok){
                const data = await response.json()
                lobbydata.lobbies = [...data]
            } else {
                console.error(`Failed to create a new player client: ${response.statusText}`)
            }

        } catch (error: any){
            console.error('Error: ', error)

        }
    }

    /**
     * Fetches a specific lobby by its ID.
     * @param lobbyId The ID of the lobby to fetch.
     * @returns The lobby object or null if not found.
     */
    async function fetchLobbyById(lobbyId: string): Promise<ILobbyDTD | null> {
        try{
            const url = `/api/lobbies/lobby/${lobbyId}`
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

    /**
     * Starts the STOMP client for real-time lobby updates.
     */
    async function startLobbyLiveUpdate(){

        stompclient = new Client({ brokerURL: wsurl })

        if(stompclient && stompclient.active){
            console.log('STOMP-Client activated.')
            return
        }

        stompclient.onConnect = (frame) => {
            console.log('STOMP connected:', frame)

            if (stompclient) {
                stompclient.subscribe(DEST, async (message) => {
                    console.log('STOMP Client subscribe')
                    const updatedLobbies = JSON.parse(message.body)
                    lobbydata.lobbies = [...updatedLobbies]
                    console.log('Received lobby update:', updatedLobbies)
                })
            } else {
                console.error('STOMP client is not initialized.')
            }
        }

        stompclient.onWebSocketError = (error) => {
            console.error('WebSocket Error:', error)
        }

        stompclient.onStompError = (frame) => {
            console.error('Full STOMP Error Frame: ', frame)
        }

        stompclient.onDisconnect = () => {
            console.log('Stompclient disconnected.')
        }

        stompclient.activate()
    }

    /**
     * Creates a new lobby with the given name and admin player.
     * @param lobbyName The name of the new lobby.
     * @param adminClient The player who will be the admin of the lobby.
     * @returns The created lobby object or null if failed.
     */
    async function createLobby(name: string, adminClient: IPlayerClientDTD): Promise<ILobbyDTD | null> {
        const creatorUuid = adminClient.playerId

        try{
            const url = `/api/lobbies/create/lobby`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name, creatorUuid }),
            })

            if(response.ok){
                const lobby: ILobbyDTD = await response.json()
                lobbydata.currentPlayer.joinedLobbyId = lobby.lobbyId

                // Admin client should have the SNACKMAN role
                const adminPlayer = lobby.members.find((member) => member.playerId === adminClient.playerId)
                if (adminPlayer) {
                    lobbydata.currentPlayer.role = adminPlayer.role
                }

                lobbydata.lobbies.push(lobby)
                return lobby
            } else {
                console.error(`Failed to create lobby: ${response.statusText}`)
                return null
            }

        } catch (error: any){
            console.error('Error:', error)
            return null
        }
    }

    /**
     * Joins an existing lobby.
     * @param lobbyId The ID of the lobby to join.
     * @param playerId The ID of the player joining the lobby.
     * @returns The updated lobby object or null if failed.
     */
    async function joinLobby(lobbyId: string, playerId: string): Promise<ILobbyDTD | null> {
        try{
            const currentLobby = await fetchLobbyById(lobbyId);
            if (currentLobby && currentLobby.members.length >= 5) {
                console.error('Lobby is full. Cannot join.');
                return null;
            }

            const url = `/api/lobbies/join`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ lobbyId, playerId }),
            })

            if(response.status == 409){
                console.error("Game has already started. Cannot joint the lobby.")
                return null
            }

            if(response.ok){
                const lobby: ILobbyDTD = await response.json()
                lobbydata.currentPlayer.joinedLobbyId = lobby.lobbyId

                // Find the current player in the lobby data and update the role
                const updatedPlayer = lobby.members.find((member) => member.playerId === playerId)
                if (updatedPlayer) {
                    lobbydata.currentPlayer.role = updatedPlayer.role
                }

                return lobby
            } else {
                console.error(`Failed to join lobby: ${response.statusText}`)
                return null
            }

        } catch (error: any){
            console.error('Error:', error)
            return null
        }
    }

    /**
     * Removes a player from an existing lobby.
     * @param lobbyId The ID of the lobby.
     * @param playerId The ID of the player leaving the lobby.
     */
    async function leaveLobby(lobbyId: string, playerId: string): Promise<void> {
        try{
            const url = `/api/lobbies/leave`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ lobbyId, playerId }),
            })

            if (!response.ok) {
                throw new Error(`Failed to leave lobby: ${response.statusText}`)
            }

        } catch (error: any){
            console.error('Error leaving lobby:', error)
        }
    }

    /**
     * Starts the game in the specified lobby.
     * @param lobbyId The ID of the lobby where the game is to be started.
     */
    async function startGame(lobbyId: string): Promise<void> {
        try {
            const url = `/api/lobbies/start`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ lobbyId }),
            })

            if (!response.ok) {
                throw new Error(`Failed to start game: ${response.statusText}`)
            }

            const lobby = lobbydata.lobbies.find(l => l.lobbyId === lobbyId)
            if (lobby) {
                lobby.gameStarted = true
            }
            console.log(`Game started successfully in lobby: ${lobbyId}`)
        } catch (error: any) {
            console.error(`Error starting game in lobby ${lobbyId}:`, error)
            throw new Error('Could not start the game. Please try again.')
        }
    }

    return{
        lobbydata,
        createPlayer,
        fetchLobbyList,
        startLobbyLiveUpdate,
        createLobby,
        joinLobby,
        leaveLobby,
        startGame,
        fetchLobbyById,
    }
})
