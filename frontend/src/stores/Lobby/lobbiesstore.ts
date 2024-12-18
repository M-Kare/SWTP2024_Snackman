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

    // Function to set player
    function setPlayer(player: IPlayerClientDTD) {
        lobbydata.currentPlayer = { ...player }; // Cập nhật toàn bộ thông tin của player
        console.log('Player has been set:', player);
    }

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
                Object.assign(lobbydata.currentPlayer, newPlayer)
            } else {
                console.error(`Failed to create a new player client: ${response.statusText}`)
            }

            return newPlayerClient

        } catch (error: any){
            console.error('Error: ', error)

        }

    }

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
    async function createLobby(lobbyName: string, adminClient: IPlayerClientDTD): Promise<ILobbyDTD | null> {
        const newLobby: ILobbyDTD = {
            lobbyId: '',
            name: lobbyName,
            adminClient: adminClient,
            gameStarted: false,
            members: [adminClient]
        }

        try{
            const url = `/api/lobbies/create?name=${lobbyName}&creatorUuid=${adminClient.playerId}`
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
            if (currentLobby && currentLobby.members.length >= 4) {
                console.error('Lobby is full. Cannot join.');
                alert(`Lobby "${currentLobby.name}" is full!`);
                return null;
            }

            const url = `/api/lobbies/${lobbyId}/join?playerId=${playerId}`
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
            const url = `/api/lobbies/${lobbyId}/leave?playerId=${playerId}`
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const url = `/api/lobbies/${lobbyId}/start`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Failed to start game: ${response.statusText}`);
            }

            console.log(`Game started successfully in lobby: ${lobbyId}`);
        } catch (error: any) {
            console.error(`Error starting game in lobby ${lobbyId}:`, error);
            throw new Error('Could not start the game. Please try again.');
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

    return{
        lobbydata,
        setPlayer,
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
