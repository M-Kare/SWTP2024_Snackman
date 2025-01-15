<template>
  <MenuBackground></MenuBackground>

  <div class="map-list" v-for="map in mapList" :key="map.mapName">
      <div class="map-list-item">
          <input class="map-choose" 
              type="radio" 
              :value="map.mapName" 
              :checked="selectedMap === map.mapName" 
              @change="selectMap(map.mapName)"
          />
          <span>{{ map.mapName }}</span>
      </div>
  </div>

  <div v-if="feedbackMessage" :class="['feedback-message', feedbackClass]">
      {{ feedbackMessage }}
  </div>

  <div id="individual-outer-box-size" class="outer-box">
    <div class="item-row">
      <h1 class="title">{{ lobby?.name || 'Lobby Name' }}</h1>

      <div id="player-count">
        {{ playerCount }} / {{ MAX_PLAYER_COUNT }} Player
      </div>
    </div>

    <div class="inner-box">
      <ul>
        <li v-for="member in members" class="player-list-items">
          <div class="player-name">
            {{ member.playerName }}
          </div>

          <div class="player-character">
            {{ member.role }}
          </div>
        </li>
      </ul>
    </div>
    <div class="item-row">
      <div class="map-list" v-for="map in mapList" :key="map.mapName">
      <div class="map-list-item">
          <input class="map-choose" 
              type="radio" 
              :value="map.mapName" 
              :checked="selectedMap === map.mapName" 
              @change="selectMap(map.mapName)"
          />
          <span>{{ map.mapName }}</span>
      </div>
  </div>
    </div>
    <div class="item-row">
      <div id="button-pair">
        <SmallNavButton
          id="menu-back-button"
          class="small-nav-buttons"
          @click="leaveLobby"
        >
          Leave Lobby
        </SmallNavButton>
        <SmallNavButton 
          id="menu-map-importieren"
          class="small-nav-button"
          @click="triggerFileInput"
        >
          Map Importieren
        </SmallNavButton>
        <input class="input-feld"
            ref="fileInput" 
            type="file" 
            accept=".txt" 
            @change="handleFileImport"
        />
      </div>
      
      <div id="button-pair">
        <SmallNavButton
          id="copyToClip"
          class="small-nav-buttons"
          @click="copyToClip()"
        >
          Copy Link
        </SmallNavButton>
        <SmallNavButton
          id="start-game-button"
          class="small-nav-buttons"
          @click="startGame"
        >
          Start Game
        </SmallNavButton>
      </div>
    </div>
  </div>

  <div v-if="darkenBackground" id="darken-background"></div>

  <PopUp
    v-if="errorBox"
    class="popup-box"
    @click="backToLobbyListView()"
    @hidePopUp="hidePopUp"
  >
    <p class="info-heading">- {{ infoHeading }} -</p>
    <p class="info-text">{{ infoText }}</p>
  </PopUp>

  <PopUp v-if="showPopUp" class="popup-box" @hidePopUp="hidePopUp">
    <p class="info-heading">{{ infoHeading }}</p>
    <p class="info-text">{{ infoText }}</p>
  </PopUp>


  <div v-show="showInfo" id="infoBox">{{ infoText }}</div>
</template>

<script lang="ts" setup>
import MenuBackground from '@/components/MenuBackground.vue'
import SmallNavButton from '@/components/SmallNavButton.vue'
import PopUp from '@/components/PopUp.vue'

import {useRoute, useRouter} from 'vue-router'
import {computed, onMounted, ref, watchEffect} from 'vue'
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore'
import type {IPlayerClientDTD} from '@/stores/Lobby/IPlayerClientDTD'
import type {ILobbyDTD} from '@/stores/Lobby/ILobbyDTD'

const router = useRouter()
const route = useRoute()
const lobbiesStore = useLobbiesStore()

const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;
const lobbyId = route.params.lobbyId as string;

const lobbyUrl = route.params.lobbyId
let lobbyLoaded = false
const lobby = computed(() =>
  lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyUrl),
)
const adminClientId = lobby.value?.adminClient.playerId;
const members = computed(
  () => lobby.value?.members || ([] as Array<IPlayerClientDTD>),
)
const playerCount = computed(() => members.value.length)

const darkenBackground = ref(false)
const showPopUp = ref(false)
const errorBox = ref(false)
const infoText = ref()
const infoHeading = ref()
const showInfo = ref(false)

const mouseX = ref(0)
const mouseY = ref(0)

const mouseInfoBox = ref(document.getElementById('infoBox'))

const MAX_PLAYER_COUNT = 5

const TIP_TOP_DIST = 30
const TIP_SIDE_DIST = 20

const hidePopUp = () => {
  showPopUp.value = false
  darkenBackground.value = false
}

const mapList = ref<{ mapName: string; fileName: string }[]>([
    { mapName: 'Original Map', fileName: `Maze.txt` },
]);

const usedCustomMap = ref(false);
const selectedMap = ref<string | null>(null);

const selectMap = (mapName: string) => {
    selectedMap.value = mapName;

    if (selectedMap.value === 'Original Map'){
        usedCustomMap.value = false;
    }
    else if (selectedMap.value === 'Snack Man Map') {
        usedCustomMap.value = true;
    } 
};

const feedbackMessage = ref('');
const feedbackClass = ref('');
const toggleState = ref(false);

const updateToggleState = (state: boolean) => {
    toggleState.value = state;
}

const triggerFileInput = () => {
    fileInput.value?.click();
}

const fileInput = ref<HTMLInputElement | null>(null);

/**
 * This function processes the file selected by the user in an input field. 
 * It ensures the file is a `.txt` file.
 * If the file is valid, it triggers an upload to the server.
 * Otherwise, it displays a popup with error information.
 * 
 * @param event - The event triggered by the file input change.
 */
const handleFileImport = (event: Event) => {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
        const file = input.files[0];
        if (file.name.endsWith('.txt')) {
            uploadFileToServer(file, lobbyId);
        } else {
            showPopUp.value = true;
            darkenBackground.value = true;
            infoHeading.value = "File Map is not valid"
            infoText.value = "Please upload file .txt"
        }
    }
}

/**
 * Upload file to server
 * @param file - new custom map in file .txt
 * @param lobbyId - The unique identifier of the lobby.
 */
const uploadFileToServer = async (file: File, lobbyId: string) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('lobbyId', lobbyId);

    try {
        const response = await fetch('/api/upload', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            // Success feedback
            feedbackMessage.value = 'Map saved';
            feedbackClass.value = 'success';

            const mapName = 'Snack Man Map';
            const fileName = `SnackManMap_${lobbyId}.txt`;

            if (mapList.value.length > 1) {
                mapList.value[1] = { mapName, fileName };
            } else {
                mapList.value.push({ mapName, fileName });
            }

            selectMap(mapName);
        } else {
            // Failure feedback
            feedbackMessage.value = 'Map not saved';
            feedbackClass.value = 'error';

            const errorMessage = await response.text();
            showPopUp.value = true;
            darkenBackground.value = true;
            infoHeading.value = "File Map is not valid";
            infoText.value = errorMessage;
        }
    } catch (error) {
        console.error('Error uploading file:', error);
        // Failure feedback
        feedbackMessage.value = 'Map not saved';
        feedbackClass.value = 'error';
    }

    // Clear feedback after 3 seconds
    setTimeout(() => {
        feedbackMessage.value = '';
        feedbackClass.value = '';
    }, 3000);
}

/**
 * Delete uploaded File, when the lobby doesn't exist anymore.
 * @param lobbyId - The unique identifier of the lobby.
 */
const deleteUploadedFile = async (lobbyId: string) => {
    const formData = new FormData();
    formData.append('lobbyId', lobbyId);

    fetch('/api/deleteMap', {
        method: 'DELETE',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            console.error('Error deleting file:', response.text());
        }
    })
    .catch(error => {
        console.error('Error deleting file:', error);
    });
}

/**
 * Sends a request to update the used map status for a specific lobby.
 *
 * @param {string} lobbyId - The unique identifier of the lobby.
 * @param {boolean} usedCustomMap - Indicates whether a custom map is used (true) or not (false). 
*/
const changeUsedMapStatus = async (lobbyId: string, usedCustomMap: boolean): Promise<string> => {
    try {
        const response = await fetch('/api/change-used-map-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ lobbyId, usedCustomMap })
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error changing the used map status:', errorText);
            throw new Error(`Failed to change map status: ${errorText}`);
        }

        return "done";
    } catch (error) {
        console.error('Error changing the used map status:', error);
        throw error;
    }
}

watchEffect(() => {
  if (lobbiesStore.lobbydata && lobbiesStore.lobbydata.lobbies) {
    const updatedLobby = lobbiesStore.lobbydata.lobbies.find(
      l => l.lobbyId === lobbyUrl,
    )
    if (updatedLobby) {
      lobbyLoaded = true
      if (updatedLobby.gameStarted) {
        router.push({
          name: 'GameView',
          query: {role: lobbiesStore.lobbydata.currentPlayer.role},
        })
      }
    } else if (lobbyLoaded) {
      deleteUploadedFile(lobbyId);
      router.push({name: 'LobbyListView'})
    }
  }
})

onMounted(async () => {
  await lobbiesStore.fetchLobbyList()

  if (!lobby.value) {
    infoHeading.value = 'Lobby does not exist'
    infoText.value = 'Please choose or create another one!'
    errorBox.value = true
  }
  await lobbiesStore.startLobbyLiveUpdate()
  if (
    !lobbiesStore.lobbydata.currentPlayer ||
    lobbiesStore.lobbydata.currentPlayer.playerId === '' ||
    lobbiesStore.lobbydata.currentPlayer.playerName === ''
  ) {
    if (lobby.value!.members.length >= MAX_PLAYER_COUNT) {
      infoHeading.value = 'Lobby full'
      infoText.value = 'Please choose or create another one!'
      errorBox.value = true
      darkenBackground.value = true
    } else {
      await lobbiesStore.createPlayer('Mr. Late')
      await joinLobby(lobby.value!)
    }
  }
})

const joinLobby = async (lobby: ILobbyDTD) => {
  // if(lobby.members.length >= 4){
  //     showPopUp.value = true;
  //     darkenBackground.value = true;
  //     return;
  // }

  try {
    const joinedLobby = await lobbiesStore.joinLobby(
      lobby.lobbyId,
      lobbiesStore.lobbydata.currentPlayer.playerId,
    )

    if (joinedLobby) {
      router.push({name: 'LobbyView', params: {lobbyId: lobby.lobbyId}})
    }
  } catch (error: any) {
    console.error('Error:', error)
  }
}

function backToLobbyListView() {
  router.push({name: 'LobbyListView'})
}

/**
 * Leaves the current lobby. If the player is the admin, it will remove other members from the lobby first.
 * After leaving the lobby, the user is redirected to the Lobby List View.
 *
 * @async
 * @function leaveLobby
 * @throws {Error} If the player or lobby is not found.
 */
const leaveLobby = async () => {
  const playerId = lobbiesStore.lobbydata.currentPlayer.playerId
  if (!playerId || !lobby.value) {
    console.error('Player or Lobby not found')
    return
  }

  if (playerId === lobby.value.adminClient.playerId) {
    for (const member of lobby.value.members) {
      if (member.playerId !== playerId) {
        await lobbiesStore.leaveLobby(lobby.value.lobbyId, member.playerId)
      }
    }
    // If Admin-Player leave Lobby, delete the uploaded map
    deleteUploadedFile(lobbyId);
  }

  await lobbiesStore.leaveLobby(lobby.value.lobbyId, playerId)
  router.push({name: 'LobbyListView'})
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
  const playerId = lobbiesStore.lobbydata.currentPlayer.playerId

  if (!playerId || !lobby.value) {
    console.error('Player or Lobby not found')
    return
  }

  if (lobby.value.members.length < 2) {
    showPopUp.value = true
    darkenBackground.value = true
    infoText.value = 'Not enough players to start the game!'
    return
  }

  if(playerId === lobby.value.adminClient.playerId){
    const status = await changeUsedMapStatus(lobby.value.lobbyId, usedCustomMap.value);
    if (status !== "done") {
      showPopUp.value = true;
      darkenBackground.value = true;
      infoHeading.value = "Map Status Error";
      infoText.value = "Failed to update the map status.";
      return; 
    }

    await lobbiesStore.startGame(lobby.value.lobbyId);
  } else {
    showPopUp.value = true;
    darkenBackground.value = true;
    infoHeading.value = "Can't start the game"
    infoText.value = "Only SnackMan can start the game!"
    return
  }
}

function copyToClip() {
  navigator.clipboard.writeText(document.URL)
  infoText.value = 'Link copied to clipboard'
  showInfo.value = true
  mouseInfoBox.value = document.getElementById('infoBox')
  moveToMouse(mouseInfoBox.value!)
  setTimeout(() => {
    showInfo.value = false
  }, 1000)
}

window.onmousemove = function (e) {
  mouseX.value = e.clientX
  mouseY.value = e.clientY
  if (showInfo.value) {
    moveToMouse(mouseInfoBox.value!)
  }
}

function moveToMouse(element: HTMLElement) {
  const offset = mouseInfoBox.value!.parentElement!.getBoundingClientRect()
  element.style.top = mouseY.value - offset.top - TIP_TOP_DIST + 'px'
  element.style.left = mouseX.value - offset.left + TIP_SIDE_DIST + 'px'
}
</script>

<style scoped>
.title {
  font-size: 3rem;
  font-weight: bold;
  color: var(--background-for-text-color);
  text-align: left;
}

#individual-outer-box-size {
  width: 70vw;
  max-width: 1000px;
  height: 40rem;
  max-height: 50rem;
}

#infoBox {
  position: absolute;
  border-radius: 0.5rem;
  background: rgba(255, 255, 255, 60%);
  color: #000000;
  padding-left: 0.5rem;
  padding-right: 0.5rem;
}

#player-count {
  font-size: 3rem;
  font-weight: bold;
  color: var(--background-for-text-color);
  text-align: right;
}

.inner-box {
  position: relative;
  margin-top: 1vh;
  margin-bottom: 1vh;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  height: auto;
  border-radius: 0.3rem;
  color: var(--primary-text-color);
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
  min-height: 350px;
}

.player-list-items {
  display: flex;
  justify-content: space-between;
  background: var(--background-for-text-color);
  border: 4px solid var(--primary-text-color);
  border-radius: 0.1rem;
  box-shadow: 4px 3px 0 var(--primary-text-color);
  font-size: 1.2rem;
  padding: 0.5rem 0.8rem;
  margin: 0.4rem 0;
}

.player-list-items:hover {
  cursor: pointer;
}

.info-heading {
  font-size: 3rem;
  font-weight: bold;
}

.info-text {
  font-size: 1.8rem;
  padding: 1.2rem 0;
}

.item-row {
  padding: 2rem 3rem;
  display: flex;
  justify-content: space-between;
}

#button-pair {
  display: flex;
  gap: 20px;
}

#menu-back-button:hover,
#copyToClip:hover,
#start-game-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
.custom-map{
  position: absolute;
  right: 20px;
  top: 30px;
  display: flex;
  gap: 25px;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  color: #fff;
  z-index: 3;
}

.map-importiren{
  position: absolute;
  right: 20px;
  top: 100px;
  z-index: 3;
}

.map-list{
    font-size: 1.5rem;
    margin-top: 30px;
    gap: 25px;
    justify-content: center;
    align-items: center;
}

.map-choose{
    width: 20px;
    height: 20px;
    transform: scale(1.5);
    margin-right: 15px; 
}

.input-feld{
  display: none;
}

.feedback-message {
    margin-top: 30px;
    font-size: 1.5rem;
    font-weight: bold;
    padding: 10px 20px;
    border-radius: 5px;
    text-align: center;
    animation: fadeIn 0.5s;
}

.feedback-message.success {
    color: #fff;
    background-color: #50C878;
}

.feedback-message.error {
    color: #fff;
    background-color: #C70039;
}

/* Fade-in animation */
@keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
}
</style>
