<template>
  <MenuBackground></MenuBackground>

  <h1 class="title">Choose Your Character</h1>
  <div class="outer-box">
    <div class="inner-box">
      <div class="character-grid">
        <div
          v-for="button in buttons"
          :key="button.id"
          @click="selectCharacter(button)"
          class="character-item"
          :class="{ 'selected': button.selected }"
          :style="button.selected ? { opacity: 0.3, cursor: 'not-allowed' } : {}"
        >
          <div class="image-container"
               :style="button.selected ? { pointerEvents: 'none' } : {}">
            <img :src="button.image" :alt="button.name" class="character-image">
          </div>
          <p class="character-name">{{ button.name }}</p>
        </div>
      </div>
    </div>

    <SmallNavButton
      v-if="isPlayerAdmin"
      id="start-game-button"
      class="small-nav-buttons"
      @click="startGame"
    >
      Start Game
    </SmallNavButton>

    <PopUp v-if="showPopUp" class="popup-box" @hidePopUp="hidePopUp">
      <p class="info-heading">Can't start the game</p>
      <p class="info-text">{{ infoText }}</p>
    </PopUp>

    <PopUp v-if="showRolePopup" class="popup-box" @hidePopUp="hidePopUp">
      <p class="info-heading">Can't start the game</p>
      <p class="info-text">{{ infoText }}</p>
    </PopUp>

    <div v-if="darkenBackground" id="darken-background"></div>

  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref, watchEffect} from 'vue';
import MenuBackground from '@/components/MenuBackground.vue';
import SmallNavButton from '@/components/SmallNavButton.vue';
import {useRoute, useRouter} from "vue-router";
import PopUp from "@/components/PopUp.vue";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";
import type{Button} from "@/stores/Lobby/lobbiesstore"

const route = useRoute()
const router = useRouter()
const lobbyId = route.params.lobbyId as string
const lobbiesStore = useLobbiesStore()
const usedCustomMap = ref(false);
const buttons = lobbiesStore.buttons

const darkenBackground = ref(false)
const showPopUp = ref(false)
const showRolePopup = ref(false)
const infoText = ref()
const hidePopUp = () => {
  showPopUp.value = false
}
const lobbyUrl = route.params.lobbyId
const infoHeading = ref()

/**
 * Starts the game if the player is the admin and there are enough members in the lobby.
 * If the player is not the admin or there are not enough members, a popup will be shown.
 *
 * @async
 * @function startGame
 * @throws {Error} If the player or lobby is not found.
 */

const selectedCharacter = ref<Button | null>(null);

const isPlayerAdmin = computed(() => {
  const currentPlayer = lobbiesStore.lobbydata.currentPlayer;
  const lobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyId);
  return currentPlayer && lobby && currentPlayer.playerId === lobby.adminClient.playerId;
})


const selectCharacter = async (button: Button) => {

  // already choosen Role
  if (button.selected) {
    infoText.value = "Dieser Charakter wurde bereits gewählt!";
    showPopUp.value = true;
    darkenBackground.value = true;
    return;
  }
  // sende LobbyId, PlayerId,Rolle, ButtonId, Selected, ButtonId
  const payload = {
    lobbyId: lobbyId,
    playerId: lobbiesStore.lobbydata.currentPlayer.playerId,
    role: button.name.toUpperCase(), // "SNACKMAN" oder "GHOST"
    buttonId: button.id,
    selected: button.selected,
  };
  try {
    // bekommt statuscode Zurück 200 Ok , 409 Conflict , 400 Badrequest
    const response = await fetch('/api/lobbies/lobby/choose/role', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      selectedCharacter.value = button;
      darkenBackground.value = true;
    } else if (response.status === 409) {
      infoText.value = "This Character already selected!";
      showPopUp.value = true;
      darkenBackground.value = true;
    } else {
      infoText.value = "Error while selecting character!";
      showPopUp.value = true;
      darkenBackground.value = true;
    }

  } catch (error) {
    console.error("Error selecting character:", error);
    infoText.value = "Failed to connect to the server!";
    showPopUp.value = true;
    darkenBackground.value = true;
  }
}
onMounted(async () => {
  await lobbiesStore.fetchLobbyById(lobbyId)

  await lobbiesStore.startLobbyLiveUpdate()
})

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
  let snackmanCounter:number = 0
  let memberCounter :number = 0
  const lobby = lobbiesStore.lobbydata.lobbies.find(lobby => lobby.lobbyId === lobbyId)

  if (!playerId || !lobby) {
    console.error('Player or Lobby not found')
    return
  }
  if (playerId === lobby.adminClient.playerId) {
    const status = await changeUsedMapStatus(lobby.lobbyId, usedCustomMap.value);
    if (status !== "done") {
      showPopUp.value = true;
      darkenBackground.value = true;
      infoHeading.value = "Map Status Error";
      infoText.value = "Failed to update the map status.";
      return;
    }
    lobby.members.forEach(member => {
      if (member.role === 'UNDEFINED') {
        showPopUp.value= true
        darkenBackground.value = true
        infoText.value = 'Every Player has to choose a Role!'
        return
      }
      else if(member.role === 'SNACKMAN'){
        snackmanCounter ++
        memberCounter++
      }
      else if(member.role ==='GHOST'){
        memberCounter ++
      }
    })

    if ( snackmanCounter === 1 && memberCounter === lobby.members.length){
      await lobbiesStore.startGame(lobby.lobbyId)
      buttons.forEach(button => button.selected = false)
      buttons.forEach(button => button.selectedBy = '')
      selectedCharacter.value = null

      await router.push({
        name: 'GameView',
        query: {
          role: lobbiesStore.lobbydata.currentPlayer.role ,
          lobbyId: lobbiesStore.lobbydata.currentPlayer.joinedLobbyId,
        },
      })
    }
    else {
      showPopUp.value = true
      darkenBackground.value = true
      infoText.value = 'There must be exactly one SnackMan and all players must have a role!'
    }
  } else {
    showPopUp.value = true
    darkenBackground.value = true
    infoText.value = 'Only Admin can start the game!'
  }
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
      lobby => lobby.lobbyId === lobbyUrl,
    )
    if (updatedLobby &&  updatedLobby.gameStarted) {

      const currentPlayerId = lobbiesStore.lobbydata.currentPlayer.playerId
      const currentPlayerInUpdatedLobby = updatedLobby.members.find( member => member.playerId == currentPlayerId)

      if (currentPlayerInUpdatedLobby) {
        buttons.forEach(button => button.selected = false)
        buttons.forEach(button => button.selectedBy = '')
        selectedCharacter.value = null
        router.push({
          name: 'GameView',
          query: {
            role: lobbiesStore.lobbydata.currentPlayer.role ,
            lobbyId: lobbiesStore.lobbydata.currentPlayer.joinedLobbyId,
          },
        })
      }
    }
  }
})
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

.character-grid {
  display: grid;
  grid-template-columns: repeat(2, 5fr);
  grid-template-rows: repeat(2, 18vw);
  grid-gap: 15px;
}

#confirm-button {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
}

#confirm-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.character-item {
  background: rgba(255, 255, 255, 70%);
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.character-item.selected {
  opacity: 0.3;
  cursor: not-allowed;
}

.character-item:hover {
  transform: scale(1.05);
}

.image-container {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.character-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.character-name {
  font-weight: bold;
  color: #000000;
}

.overlay {
  position: absolute; /* Overlay über dem Bild platzieren */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* Dunkler halbtransparenter Hintergrund */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}


</style>
