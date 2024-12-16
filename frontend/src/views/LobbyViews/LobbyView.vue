<template>
    <MenuBackground></MenuBackground>

    <h1 class="title"> {{ lobby?.name || 'Lobby Name' }} </h1>
    <div class="outer-box">
        <SmallNavButton
            id="menu-back-button"
            class="small-nav-buttons"
            @click="leaveLobby">
            
            Leave Lobby
        </SmallNavButton>
        <SmallNavButton
            id="start-game-button"
            class="small-nav-buttons"
            @click="startGame">

            Start Game
        </SmallNavButton>

        <div id="player-count">
                {{ playerCount }} / {{ maxPlayerCount }} Players
        </div>

        <div class="inner-box">
            <ul>
                <li
                    v-for="member in members" :key="member.playerId"
                    class="player-list-items">

                    <div class="player-name">
                        {{ member.playerName }}
                    </div>

                    <div class="player-name">
                        {{ member.playerId }}
                    </div>

                    <div class="player-character">
                        {{ member.role }}
                    </div>

                </li>
            </ul>

        </div>
    </div>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';

    import { useRoute, useRouter } from 'vue-router';
    import { computed, onMounted, ref } from 'vue';
    import type { ILobbyDTD } from '@/stores/Lobby/ILobbyDTD';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';

    const router = useRouter();
    const route = useRoute();
    const lobbiesStore = useLobbiesStore();

    const lobby = ref<ILobbyDTD | null>(null);
    const members = computed(() => lobby.value?.members || [] as Array<IPlayerClientDTD>);
    const playerCount = computed(() => members.value.length);
    const maxPlayerCount = ref(4);
    
    onMounted(async () => {
        const lobbyId = route.params.lobbyId as string;
        if (!lobbyId) {
            console.error('Lobby ID is missing!');
            return;
        }

        // const savedLobby = sessionStorage.getItem(`lobby_${lobbyId}`);
        // if (savedLobby) {
        //     lobby.value = JSON.parse(savedLobby);
        // } else {
        //     lobby.value = await lobbiesStore.fetchLobbyById(lobbyId);
        //     sessionStorage.setItem(`lobby_${lobbyId}`, JSON.stringify(lobby.value));
        // }

        lobby.value = await lobbiesStore.fetchLobbyById(lobbyId);

        if(!lobby.value){
            alert('Lobby not found or failed to load.');
            router.push({name: "LobbyList"});
        }

        lobbiesStore.startLobbyLiveUpdate();

        // Update lobby data reactively if STOMP updates arrive
        lobbiesStore.$subscribe((mutation, state) => {
            const updatedLobby = state.lobbydata.lobbies.find(l => l.uuid === lobbyId);
        
            // If Lobby doesn't exit, come back to LobbyListView-Seite
            if (!updatedLobby) {
                router.push({ name: 'LobbyList' });
                return;
            }

            lobby.value = updatedLobby;

            if (updatedLobby.isGameStarted){
                router.push({ name: 'GameView' });
            }
        });
    })

    const leaveLobby = async () => {
        const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;
        if (!playerId || !lobby.value) {
            console.error('Player or Lobby not found');
            return;
        }

        if(playerId === lobby.value.adminClient.playerId){
            for (const member of lobby.value.members) {
                if (member.playerId !== playerId) {
                    await lobbiesStore.leaveLobby(lobby.value.uuid, member.playerId);
                }
            }
        } 

        await lobbiesStore.leaveLobby(lobby.value.uuid, playerId);
        router.push({ name: 'LobbyList' });
    }

    const startGame = async () => {
        const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;

        if (!playerId ||!lobby.value) {
            console.error('Player or Lobby not found');
            return;
        }

        if(lobby.value.members.length < 2){
            alert('Have not enough members to start game!');
        }

        if(playerId === lobby.value.adminClient.playerId){
            await lobbiesStore.startGame(lobby.value.uuid);
            lobby.value.members.forEach(() => {
                router.push({ name: "GameStart" });
            });
        } else {
            alert('Just Admin Snackman can start the game!');
        }
    }

</script>

<style scoped>
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
    top: 30%;
    left: 50%;
    transform: translateX(-50%);
    width: 70vw;
    max-width: 1000px;
    height: 30rem;
    max-height: 45rem;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

#player-count {
    top: 1%;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
    font-size: 1.8rem;
    font-weight: bold;
    color: #000000;
    padding: 1rem;
}

.inner-box {
    position: relative;
    margin-top: 1vh;
    margin-bottom: 1vh;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    height: auto;
    background: rgba(255, 255, 255, 70%);
    border-radius: 0.3rem;
    color: #000000;
}

.inner-box > ul {
    list-style: none;
    display: flex;
    flex-direction: column;
    left: 50%;
    transform: translateX(-50%);
    margin: 0;
    padding: 0;
    width: 100%;
}

.player-list-items {
    display: flex;
    justify-content: space-between;
    border: 0.5px solid black;
    border-radius: 0.2rem;
    font-size: 1.2rem;
    padding: 0.5rem;
    margin: 0.6rem;
}

.small-nav-buttons {
    bottom: 3%;
    font-weight: bold;
}

#menu-back-button {
    left: 5%;
}

#start-game-button {
    right: 5%;
}

#menu-back-button:hover, #start-game-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}

</style>