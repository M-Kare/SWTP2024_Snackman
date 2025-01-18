<template>
  <MenuBackground>
    <div v-if="darkenBackground" id="darken-background"></div>

    <PlayerNameForm
      v-if="showPlayerNameForm && !playerNameSaved"
      @hidePlayerNameForm="hidePlayerNameForm"
    >
    </PlayerNameForm>

    <div class="button-container">
      <MainMenuButton @click="startSingleplayer">Singleplayer</MainMenuButton>
      <MainMenuButton @click="showLobbies">Multiplayer</MainMenuButton>
      <MainMenuButton @click="showLeaderboard">Leaderboard</MainMenuButton>
    </div>
  </MenuBackground>
</template>

<script setup lang="ts">
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore'
import MainMenuButton from '@/components/MainMenuButton.vue'
import MenuBackground from '@/components/MenuBackground.vue'
import PlayerNameForm from '@/components/PlayerNameForm.vue';
import {useRouter} from 'vue-router'
import {onMounted, ref} from 'vue';
import type {ILobbyDTD} from "@/stores/Lobby/ILobbyDTD";

const router = useRouter()
const lobbiesStore = useLobbiesStore()

const playerNameSaved = lobbiesStore.lobbydata.currentPlayer.playerName;
const darkenBackground = ref(false);
const showPlayerNameForm = ref(false);

const hidePlayerNameForm = () => {
  showPlayerNameForm.value = false;
  darkenBackground.value = false;
}

const showLobbies = () => {
  router.push({ name: 'LobbyListView' })
}

const showLeaderboard = () => {
  router.push({name: 'Leaderboard'})
}

const startSingleplayer = async () => {
  await lobbiesStore.createPlayer("Single Player")
  const player = lobbiesStore.lobbydata.currentPlayer
  const lobby = await lobbiesStore.startSingleplayerGame(player) as ILobbyDTD

  if (!player.playerId || !lobby) {
    console.error('Player or Lobby not found')
    return
  }

  if (player.playerId === lobby.adminClient.playerId) {
    await router.push({
      name: 'GameView',
      query: {
        role: lobbiesStore.lobbydata.currentPlayer.role,
        lobbyId: lobbiesStore.lobbydata.currentPlayer.joinedLobbyId,
      },
    })
  }
}

onMounted(() => {
  if (!playerNameSaved) {
    darkenBackground.value = true;
    showPlayerNameForm.value = true;
  }
})
</script>

<style scoped>
.button-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.button-container > * {
  width: 100%;
  text-align: center;
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
