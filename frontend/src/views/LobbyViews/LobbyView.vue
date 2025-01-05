<template>
    <MenuBackground></MenuBackground>

    <h1 class="title"> {{ lobby?.name || 'Lobby Name' }} </h1>

    <div v-if="playerId === adminClientId" class="custom-map">
      <span>Custom Map</span>
      <CustomMapButton :toggleState="toggleState" @updateToggleState="updateToggleState"></CustomMapButton>
    </div>
    
    <div v-if="toggleState" class="map-importiren">
      <MapButton @click="triggerFileInput">Map Importieren</MapButton>
      <input class="input-feld"
          ref="fileInput" 
          type="file" 
          accept=".txt" 
          @change="handleFileImport"
      />
    </div>

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
                    v-for="member in members"
                    class="player-list-items">

                    <div class="player-name">
                        {{ member.playerName }}
                    </div>

                    <div class="player-character">
                        {{ member.role }}
                    </div>

                </li>
            </ul>

        </div>
    </div>

    <div v-if="darkenBackground" id="darken-background"></div>

    <PopUp class="popup-box"
        v-if="showPopUp"
        @hidePopUp="hidePopUp">

        <p class="info-heading"> {{ infoHeading }}  </p>
        <p class="info-text"> {{ infoText }} </p>
    </PopUp>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import PopUp from '@/components/PopUp.vue';
    import CustomMapButton from '@/components/CustomMapButton.vue';
    import MapButton from '@/components/MapButton.vue';

    import { useRoute, useRouter } from 'vue-router';
    import { computed, onMounted, ref, watchEffect } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';

    const router = useRouter();
    const route = useRoute();
    const lobbiesStore = useLobbiesStore();

    const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;
    const lobby = computed(() => lobbiesStore.lobbydata.lobbies.find(l => l.uuid === route.params.lobbyId));
    const adminClientId = lobby.value?.adminClient.playerId;
    const members = computed(() => lobby.value?.members || [] as Array<IPlayerClientDTD>);
    const playerCount = computed(() => members.value.length);
    const maxPlayerCount = ref(4);

    const darkenBackground = ref(false);
    const showPopUp = ref(false);
    const infoHeading = ref();
    const infoText = ref();

    const hidePopUp = () => {
        showPopUp.value = false;
        darkenBackground.value = false;
    }

    const toggleState = ref(false);

    const updateToggleState = (state: boolean) => {
        toggleState.value = state;
    }

    const triggerFileInput = () => {
        fileInput.value?.click();
    }

    const fileInput = ref<HTMLInputElement | null>(null);

    const handleFileImport = (event: Event) => {
        const input = event.target as HTMLInputElement;

        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            if (file.name.endsWith('.txt')) {
                const reader = new FileReader();
                reader.onload = () => {
                    const fileContent = reader.result as string;
                    const validPattern = /^[SGCo#\s]*$/;

                    if (validPattern.test(fileContent)){
                        console.log('File Content:', fileContent);
                    } else {
                        showPopUp.value = true;
                        darkenBackground.value = true;
                        infoHeading.value = "- Map Data is not valid -"
                    }
                };
                reader.readAsText(file);
            } else {
                alert('Please select a valid .txt file.');
            }
        }
    }

    watchEffect(() => {
        if (lobbiesStore.lobbydata) {
            const lobbyId = route.params.lobbyId as string;
            
            const updatedLobby = lobbiesStore.lobbydata.lobbies.find(l => l.uuid === lobbyId);
            console.log("Updated Lobby in Lobby-View", updatedLobby)

            if (updatedLobby) {
                console.log("Gamestarted in Lobby-View", updatedLobby.gameStarted) 
                if (updatedLobby.gameStarted){
                    console.log('Game has started! Redirecting to GameView...');
                    router.push({ name: 'GameView' });
                }
            }else {
                router.push({ name: 'LobbyListView' });
            }
        }
    });

    onMounted(async () => {
        if(!lobby.value){
            alert('Lobby not found or failed to load.');
            router.push({name: 'LobbyListView'});
        }

        lobbiesStore.startLobbyLiveUpdate();

    })

    /**
     * Leaves the current lobby. If the player is the admin, it will remove other members from the lobby first.
     * After leaving the lobby, the user is redirected to the Lobby List View.
     * 
     * @async
     * @function leaveLobby
     * @throws {Error} If the player or lobby is not found.
     */
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
        router.push({ name: 'LobbyListView' });
    }

    /**
     * Starts the game if the player is the admin and there are enough members in the lobby.
     * If the player is not the admin or there are not enough members, a popup will be shown.
     * 
     * @async
     * @function startGame
     * @throws {Error} If the player or lobby is not found.
     */
    const startGame = async () => {
        const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;

        if (!playerId ||!lobby.value) {
            console.error('Player or Lobby not found');
            return;
        }

        if(lobby.value.members.length < 2){
            showPopUp.value = true;
            darkenBackground.value = true;
            infoText.value = "Not enough players to start the game!"
        }

        if(playerId === lobby.value.adminClient.playerId){
            await lobbiesStore.startGame(lobby.value.uuid);
        } else {
            showPopUp.value = true;
            darkenBackground.value = true;
            infoHeading.value = "- Can't start the game -"
            infoText.value = "Only SnackMan can start the game!"
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

.info-heading {
    font-size: 3rem;
    font-weight: bold;
}

.info-text {
    font-size: 1.8rem;
    padding: 1.2rem;
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

#darken-background {
    z-index: 1;
    position: fixed;
    top: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 50%);

    transition: background 0.3s ease;
}

.custom-map{
  position: absolute;
  right: 20px;
  top: 30px;
  display: flex;
  gap: 25px;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  color: #fff;
  z-index: 3;
}

.map-importiren{
  position: absolute;
  right: 20px;
  top: 150px;
  z-index: 3;
}

.input-feld{
  display: none;
}
</style>