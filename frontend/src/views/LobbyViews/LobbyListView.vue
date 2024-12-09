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
                    @click="enterLobby(lobby.uuid)">

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
    import { computed, onMounted, ref } from 'vue';
    import { useLobbiesStore } from '@/stores/lobbiesstore';

    const router = useRouter();
    const lobbiesStore = useLobbiesStore();

    const lobbies = computed(() => lobbiesStore.lobbydata.lobbies);
    const currentPlayer = lobbiesStore.lobbydata.currentPlayer;
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

    //join in lobby
    const enterLobby = (lobby: any) => {
        router.push({name: "Lobby", params: {lobbyName: lobby.name}});
    }

    interface Lobby {
        lobbyName: string,
        playerCount: number
    }
    const maxPlayerCount = 4;
    // const lobbies = ref<Lobby[]>([
    //     {lobbyName: "Lobby 1", playerCount: 1},
    //     {lobbyName: "Lobby 2", playerCount: 3},
    //     {lobbyName: "Lobby 3", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4},
    //     {lobbyName: "Lobby x", playerCount: 4}
    // ]);

    onMounted(() => {
        if (!lobbiesStore.lobbydata.currentPlayer || lobbiesStore.lobbydata.currentPlayer.playerId === '' || lobbiesStore.lobbydata.currentPlayer.playerName === '') {
            lobbiesStore.createPlayer('Player Test');
        }

        console.log("Current Player:", lobbiesStore.lobbydata.currentPlayer);

        //lobbiesStore.updateLobbies()
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