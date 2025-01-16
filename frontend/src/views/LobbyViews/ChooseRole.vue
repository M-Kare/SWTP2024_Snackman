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
import {Button} from "@/stores/Lobby/lobbiesstore"


// TODO Finish ersetzten durch Start

const route = useRoute();
const lobbyId = route.params.lobbyId as string;

const lobbiesStore = useLobbiesStore()
const buttons = lobbiesStore.buttons
const router = useRouter();
const darkenBackground = ref(false)
const showPopUp = ref(false)
const showRolePopup = ref(false)
const infoText = ref()
const hidePopUp = () => {
  showPopUp.value = false
}
const lobbyUrl = route.params.lobbyId

const lobby = computed(() =>
  lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyUrl),
)


/**
 * Starts the game if the player is the admin and there are enough members in the lobby.
 * If the player is not the admin or there are not enough members, a popup will be shown.
 *
 * @async
 * @function startGame
 * @throws {Error} If the player or lobby is not found.
 */

const selectedCharacter = ref<Button | null>(null); // TODO make responsive


const selectCharacter = async (button: Button) => {    // TODO API Call to backend to see if you can pick this character
  console.log("Trying to select this character", button)

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
      // schauen welche Buttons bereits selected sind und löschen
      //  lobbiesStore.updateButtonSelection(button.id  , lobbiesStore.lobbydata.currentPlayer.playerId )

      selectedCharacter.value = button;
      darkenBackground.value = true;
      console.log("Character selected successfully");
    } else if (response.status === 409) {
      console.log("Value ", selectedCharacter.value, "Response ", response)
      infoText.value = "Dieser Charakter wurde bereits gewählt!";
      showPopUp.value = true;
      darkenBackground.value = true;
    } else {
      infoText.value = "Fehler beim Charakterauswählen!";
      showPopUp.value = true;
      darkenBackground.value = true;
    }

  } catch (error) {
    console.error("Error selecting character:", error);
    infoText.value = "Verbindung zum Server fehlgeschlagen!";
    showPopUp.value = true;
    darkenBackground.value = true;
  }
}


onMounted(async () => {
  await lobbiesStore.fetchLobbyById(lobbyId)

  await lobbiesStore.startLobbyLiveUpdate()
})
/*
watchEffect(() => {
  const lobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyUrl);
  if (lobby) {
    console.log("Aktuelle Lobby-Daten:", lobby);
  }
});


 */


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
  const lobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyId)

  console.log("Lobby bevore Start ", lobby)

  if (!playerId || !lobby) {
    console.error('Player or Lobby not found')
    return
  }
  if (playerId === lobby.adminClient.playerId) {
    lobby.members.forEach(member => {
      console.log("Überprüfe Mitglied : ", member.role)
      if (member.role === 'UNDEFINED') {
        showPopUp.value= true
        darkenBackground.value = true
        infoText.value = 'Every Player has to choose a Role!'
        return
      }
      else if(member.role === 'SNACKMAN'){
        snackmanCounter ++
        memberCounter++
        // TODO counter hochzählen und dann starten noch mit der bedingung verbinden
      }
      else if(member.role ==='GHOST'){
        memberCounter ++
      }
    })

    console.log("Es sind " , memberCounter, " Spieler und" , snackmanCounter, " Snackman")
    if ( snackmanCounter === 1 && memberCounter === lobby.members.length){
      await lobbiesStore.startGame(lobby.lobbyId)
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


// TODO die neue Lobby aus watch effect nutzen


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
