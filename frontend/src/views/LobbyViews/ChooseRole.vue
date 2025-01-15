<template>
  <MenuBackground></MenuBackground>

  <h1 class="title">Choose Your Character</h1>
  <div class="outer-box">
    <div class="inner-box">
      <div class="character-grid">
        <div
          v-for="character in characters"
          :key="character.id"
          @click="selectCharacter(character)"
          class="character-item"
          :class="{ 'selected': selectedCharacter === character }">
          <div class="image-container">
            <img :src="character.image" :alt="character.name" class="character-image">
          </div>
          <p class="character-name">{{ character.name }}</p>
        </div>
      </div>
    </div>


    <SmallNavButton
      id="confirm-button"
      class="small-nav-buttons"
      @click="startGame"
      :disabled="!selectedCharacter"
    >
      Finish Character Selection
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
import {onMounted, reactive, ref} from 'vue';
import MenuBackground from '@/components/MenuBackground.vue';
import SmallNavButton from '@/components/SmallNavButton.vue';
import {useRoute, useRouter} from "vue-router";
import PopUp from "@/components/PopUp.vue";
import type {ILobbyDTD} from "@/stores/Lobby/ILobbyDTD";
import type {IPlayerClientDTD} from "@/stores/Lobby/IPlayerClientDTD";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";

// TODO Finish ersetzten durch Start

const route = useRoute();
const lobbyId = route.params.lobbyId as string;



const lobbiesStore = useLobbiesStore()
const router = useRouter();

const darkenBackground = ref(false)

const showPopUp = ref(false)
const showRolePopup = ref(false)
const infoText = ref()

const hidePopUp = () => {
  showPopUp.value = false

}

interface Character {
  id: number,
  name: string,
  image: string
}

const characters = ref<Character[]>([      // TODO add as many as there are players
  {id: 1, name: 'Snackman', image: '/packman.png'},
  {id: 2, name: 'Ghost', image: '/ghost.jpg'},
  {id: 3, name: 'Ghost', image: '/ghost.jpg'},
  {id: 4, name: 'Ghost', image: '/ghost.jpg'},
  {id: 5, name: 'Ghost', image: '/ghost.jpg'}
]);


/**
 * Starts the game if the player is the admin and there are enough members in the lobby.
 * If the player is not the admin or there are not enough members, a popup will be shown.
 *
 * @async
 * @function startGame
 * @throws {Error} If the player or lobby is not found.
 */

const selectedCharacter = ref<Character | null>(null);    // TODO make responsive

const selectCharacter = async (character: Character) => {    // TODO API Call to backend to see if you can pick this character
  console.log("Trying to select this character", character)


  const payload = {
    lobbyId: lobbyId,
    playerId: lobbiesStore.lobbydata.currentPlayer.playerId,
    role: character.name.toUpperCase() // "SNACKMAN" oder "GHOST"
  };
  console.log("Lobbie Bei Charakter : ", lobbiesStore.lobbydata.lobbies.find(lobby => lobby.lobbyId === lobbyId))

  try {
    const response = await fetch('/api/lobbies/lobby/choose/role', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    //  Backend Rollen werden beim Createn und Joinen auf Undefined gesetzt

    if (response.ok) {
      selectedCharacter.value = character;
      selectedCharacter.value = character;
      console.log("Character selected successfully");
    } else if (response.status === 409) {
      console.log("Value " , selectedCharacter.value ,"Response " , response )
      infoText.value = "Dieser Charakter wurde bereits gewählt!";
      showPopUp.value = true;
      darkenBackground.value = true;
    } else {
      infoText.value = "Fehler bei der Charakterauswahl!";
      showPopUp.value = true;
      darkenBackground.value = true;
    }
  } catch (error) {
    console.error("Error selecting character:", error);
    infoText.value = "Verbindung zum Server fehlgeschlagen!";
    showPopUp.value = true;
    darkenBackground.value = true;
  }


  onMounted(async () => {
    await lobbiesStore.fetchLobbyById(lobbyId)

    await lobbiesStore.startLobbyLiveUpdate()
  })


};


/**
 * Route to gameview when characters have been selected
 */
const startGame = async () => {      // TODO enable this when the snackman character has been chosen + currently not routing to gameview correctly
  /*const lobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbiesStore.lobbydata.currentPlayer.lobbyId);
  const playerId = lobbiesStore.lobbydata.currentPlayer.playerId

  if (!lobby) {
    return
  }

  if (playerId === lobby.adminClient.playerId) {
    await lobbiesStore.chooseRoleFinish(lobby.lobbyId)

    // router.push({name:'ChooseRole',  params: {lobbyId: lobby.lobbyId}})
  } else {
    showPopUp.value = true
    darkenBackground.value = true
    infoText.value = 'The role selection can only be initiated by the host.!'


  } */

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


</style>
