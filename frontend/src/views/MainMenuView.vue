<template>
  <MenuBackground>
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
import {useRouter} from 'vue-router'
import type {ILobbyDTD} from "@/stores/Lobby/ILobbyDTD";

const router = useRouter()
const lobbiesStore = useLobbiesStore()

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
      query: {role: lobbiesStore.lobbydata.currentPlayer.role},
    })
  }
}
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
</style>
