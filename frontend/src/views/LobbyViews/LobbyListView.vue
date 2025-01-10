<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">

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

        <div class="inner-box">
            <ul>
                <li
                    v-for="lobby in filteredLobbies" :key="lobby.lobbyId"
                    class="lobby-list-items"
                    @click="joinLobby(lobby)">

                    <div class="lobby-name">
                        {{ lobby.name }}
                    </div>

                    <div class="playercount">
                        {{ lobby.members.length }} / {{ MAX_PLAYER_COUNT }}
                    </div>
                </li>
            </ul>
        </div>
    </div>

    <div v-if="darkenBackground" id="darken-background"></div>

    <PopUp class="popup-box"
        v-if="showPopUp"
        @hidePopUp="hidePopUp">

        <p class="info-heading"> - Lobby full -  </p>
        <p class="info-text"> Please choose or create another one! </p>
    </PopUp>

    <CreateLobbyForm
        v-if="showLobbyForm"
        @cancelLobbyCreation="cancelLobbyCreation">
    </CreateLobbyForm>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import CreateLobbyForm from '@/components/CreateLobbyForm.vue';
    import PopUp from '@/components/PopUp.vue';

    import { useRouter } from 'vue-router';
    import { computed, onMounted, ref } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { ILobbyDTD } from '@/stores/Lobby/ILobbyDTD';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';

    const router = useRouter();
    const lobbiesStore = useLobbiesStore();

    const lobbies = computed(() => lobbiesStore.lobbydata.lobbies);
    const currentPlayer = lobbiesStore.lobbydata.currentPlayer as IPlayerClientDTD;

    const MAX_PLAYER_COUNT = 5;

    const filteredLobbies = computed(() => {
        return lobbies.value.filter(lobby => !lobby.gameStarted);
    });

    const darkenBackground = ref(false);
    const showPopUp = ref(false);
    const showLobbyForm = ref(false);

    const hidePopUp = () => {
        showPopUp.value = false;
        darkenBackground.value = false;
    }

    const backToMainMenu = () => {
        router.push({name: "MainMenu"});
    }

    const showCreateLobbyForm = () => {
        showLobbyForm.value = true;
        darkenBackground.value = true;
    }

    const cancelLobbyCreation = () => {
        showLobbyForm.value = false;
        darkenBackground.value = false;
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

        if(lobby.members.length >= MAX_PLAYER_COUNT){
            showPopUp.value = true;
            darkenBackground.value = true;
            return;
        }

        try{
            const joinedLobby = await lobbiesStore.joinLobby(lobby.lobbyId, currentPlayer.playerId);

            if(joinedLobby) {
                console.log('Successfully joined lobby', joinedLobby.name);
                router.push({ name: "LobbyView", params: { lobbyId: lobby.lobbyId } });
            }
        } catch (error: any){
            console.error('Error:', error);
            alert("Error join Lobby!");
        }
    }

    onMounted(async () => {
        await lobbiesStore.fetchLobbyList();
        console.log(lobbies)

        if (!lobbiesStore.lobbydata.currentPlayer || lobbiesStore.lobbydata.currentPlayer.playerId === '' || lobbiesStore.lobbydata.currentPlayer.playerName === '') {
            lobbiesStore.createPlayer("Player Test");
        }

        console.log("Current Player:", lobbiesStore.lobbydata.currentPlayer);

        lobbiesStore.startLobbyLiveUpdate();
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
    color: #000000;
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

#menu-back-button {
    left: 5%;
}

#show-lobby-creation-button {
    right: 5%;
}

#menu-back-button:hover, #show-lobby-creation-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}

.info-heading {
    font-size: 3rem;
    font-weight: bold;
}

.info-text {
    font-size: 1.8rem;
    padding: 1.2rem;
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


</style>
