<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">

        <!--This is used just for test, can delete later-->
        <div class="player-info" v-if="currentPlayer">
            <p>Player ID:   {{ currentPlayer.playerId }}</p>
            <p>Player Name: {{ currentPlayer.playerName }}</p>
        </div>
        <!---->

        <SmallNavButton
            id="menu-back-button"
            class="small-nav-buttons"
            @click="backToMainMenu">
            
            Back
        </SmallNavButton>
        <SmallNavButton
            id="show-lobby-creation-button"
            class="small-nav-buttons"
            @click="showCreateLobbyForm">

            Create new Lobby
        </SmallNavButton>

        <div class="inner-box"> <!-- :key for order? -->
            <ul>
                <li
                    v-for="lobby in lobbies" :key="lobby.uuid"
                    class="lobby-list-items"
                    @click="joinLobby(lobby)">

                    <div class="lobby-name">
                        {{ lobby.name }}
                    </div>
                    
                    <div class="playercount">
                        {{ lobby.members.length }} / {{ maxPlayerCount }}
                    </div>
                </li>
            </ul>
        </div>
    </div>

    <div v-if="showNewLobbyForm" id="darken-background"></div>

    <CreateLobbyForm
        v-if="showNewLobbyForm"
        @cancelLobbyCreation = "cancelLobbyCreation">
    </CreateLobbyForm>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import CreateLobbyForm from '@/components/CreateLobbyForm.vue';

    import { useRouter } from 'vue-router';
    import { computed, onMounted, ref, watch } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { ILobbyDTD } from '@/stores/Lobby/ILobbyDTD';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';

    const router = useRouter();
    const lobbiesStore = useLobbiesStore();

    const lobbies = computed(() => lobbiesStore.lobbydata.lobbies);
    const currentPlayer = lobbiesStore.lobbydata.currentPlayer as IPlayerClientDTD;
    
    const maxPlayerCount = 4;

    // lobby value tracking
    // watch(lobbies, (newVal) => {
    //     console.log("Updated lobbies:", newVal);
    // });

    const showNewLobbyForm = ref(false);

    const backToMainMenu = () => {
        router.push({name: "MainMenu"});
    }

    const showCreateLobbyForm = () => {
        showNewLobbyForm.value = true;
    }

    const cancelLobbyCreation = () => {
        showNewLobbyForm.value = false;
    }

    /**
     * Joins a specified lobby if it is not full and the game has not started.
     * Alerts the user if the lobby is full or if the game has already started.
     * On successful join, redirects to the lobby view.
     * 
     * @async
     * @function joinLobby
     * @param {ILobbyDTD} lobby - The lobby object that the player wants to join.
     * @throws {Error} Throws an alert if the lobby is full or the game has already started.
     * @throws {Error} Throws an alert if there is an error joining the lobby.
     */
    const joinLobby = async (lobby: ILobbyDTD) => {
        
        if(lobby.members.length >= maxPlayerCount){
            alert(`Lobby "${lobby.name}" is full! Please select another one.`);
            return;
        }

        if(lobby.gameStarted){
            alert(`Lobby "${lobby.name}" started game! Please select another one.`);
            return;
        }

        try{
            const joinedLobby = await lobbiesStore.joinLobby(lobby.uuid, currentPlayer.playerId);

            if(joinedLobby) {
                console.log('Successfully joined lobby', joinedLobby.name);
                router.push({ name: "LobbyView", params: { lobbyId: lobby.uuid } });
            }
        } catch (error: any){
            console.error('Error:', error);
            alert("Error join Lobby!");
        }
    }

    onMounted(async () => {
        // const savedPlayer = sessionStorage.getItem("currentPlayer");
        // const savedLobbies = sessionStorage.getItem('currentLobby');

        // if (savedPlayer) {
        //     lobbiesStore.lobbydata.currentPlayer = JSON.parse(savedPlayer);
        //     console.log("Restored player data:", savedLobbies);
        // } else {
        //     console.log("No player data found in sessionStorage.");
        // }

        // if (savedLobbies) {
        //     lobbiesStore.lobbydata.lobbies = JSON.parse(savedLobbies);
        //     console.log("Restored lobby data:", savedLobbies);
        // } else {
        //      lobbiesStore.startLobbyLiveUpdate();
        //     console.log("No lobby data found in sessionStorage.");
        // }
        lobbiesStore.fetchLobbyList();
        console.log(lobbies)
        
        if (!lobbiesStore.lobbydata.currentPlayer || lobbiesStore.lobbydata.currentPlayer.playerId === '' || lobbiesStore.lobbydata.currentPlayer.playerName === '') {
            lobbiesStore.createPlayer('Player Test');
        }

        console.log("Current Player:", lobbiesStore.lobbydata.currentPlayer);

        lobbiesStore.startLobbyLiveUpdate();
        console.log("Start Live Lobby Update");
    })

</script>

<style scoped>
:root {
    --button-bottom-spacing: ;
}

.title {
    position: absolute;
    top: 3rem;
    left: 50%;
    transform: translateX(-50%);
    font-size: 3rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
}

.outer-box {
    position: absolute;
    top: 20%;
    left: 50%;
    transform: translateX(-50%);
    width: 70%;
    max-width: 1000px;
    height: 65%;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

.inner-box {
    position: absolute;
    top: 5%;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    max-height: 80%;
    background: rgba(255, 255, 255, 70%);
    border-radius: 0.3rem;
    overflow-y: scroll;
}

.inner-box > ul {
    list-style: none;
    left: 50%;
    transform: translateX(-50%);
    margin: 0;
    padding: 0;
    vertical-align: middle;
}

.lobby-list-items {
    display: flex;
    justify-content: space-between;
    border: 0.5px solid black;
    border-radius: 0.2rem;
    font-size: 1.2rem;
    padding: 0.5rem;
    margin: 1rem;
}

.lobby-list-items:hover {
    background-color: rgba(0, 0, 0, 0.5);
}

.small-nav-buttons {
    bottom: 3%;
    font-weight: bold;
}

#darken-background {
    z-index: 1;
    position: fixed;
    top: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 50%);

    transition: background 0.3s ease;
}

#menu-back-button {
    left: 5%;
}

#show-lobby-creation-button {
    right: 5%;
}

#menu-back-button:hover, #show-lobby-creation-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>