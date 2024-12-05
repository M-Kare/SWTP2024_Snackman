<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">
        <SmallNavButton id="menu-back-button" class="nav-buttons" @click="backToMainMenu"> Back </SmallNavButton>
        <SmallNavButton id="create-lobby-button" class="nav-buttons" @click="createLobby"> Create Lobby </SmallNavButton>

        <div class="inner-box"> <!-- :key for order? -->
            <ul>
                <li class="lobby-list-items" v-for="lobby in lobbies" :key="lobby.uuid" @click="showLobbyDetails">
                    <div class="lobby-name">
                        {{ lobby.name }}
                    </div>

                    <div class="lobby-uuid">
                        {{ lobby.uuid }}
                    </div>

                    <!--
                    <div class="playercount">
                        {{ lobby.playerCount }} / {{ maxPlayerCount }}
                    </div>
                    -->
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import { useRouter } from 'vue-router';
    import { computed, ref } from 'vue';
    import { useLobbiesStore } from '@/stores/lobbiesstore';
    import type { ILobbyDTD } from '@/stores/ILobbyDTD';
    import type { IPlayerClientDTD } from '@/stores/IPlayerClientDTD';
import { Player } from '@/components/Player';

    const router = useRouter();
    const lobbiesStore = useLobbiesStore();

    const lobbies = computed(() => lobbiesStore.lobbydata.lobbies)

    const backToMainMenu = () => {
        router.push({name: "MainMenu"});
    }

    const createLobby = async () => {
        const lobbyName = prompt("Enter Lobby Name: ")
        if (lobbyName){
            try{

                //Player-Data just for Test
                const sessionNumber = Math.floor(Math.random() * 100000).toString()
                const adminPlayer: IPlayerClientDTD = {
                    playerId: sessionNumber,
                    playerName: "playerTest",
                }
                
                await lobbiesStore.createLobby(lobbyName, adminPlayer)
                alert("Lobby created successfully!")
            } catch (error: any){
                console.error('Error:', error)
                alert("Error create Lobby!")
            }
        }
    }

    const showLobbyDetails = (lobby: any) => {
        alert(`Lobby Details\nName: ${lobby.name}\nUUID: ${lobby.uuid}`)
    }

    // TODO - Connection to Backend
    const maxPlayerCount = "4";
    // const lobbies = ref([
    //     {lobbyNumber: "Lobby 1", playerCount: "1"},
    //     {lobbyNumber: "Lobby 2", playerCount: "3"},
    //     {lobbyNumber: "Lobby 3", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"},
    //     {lobbyNumber: "Lobby x", playerCount: "4"}
    // ]);



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
    top: 12%;
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
    height: 80%;
    background: rgba(255, 255, 255, 70%);
    border-radius: 0.3rem;
    overflow-y: scroll;
    color: black;
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

#menu-back-button {
    left: 5%;
}
#menu-back-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.2);
}

#create-lobby-button {
    right: 5%;
}

#create-lobby-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>