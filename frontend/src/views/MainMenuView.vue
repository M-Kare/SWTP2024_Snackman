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
import {ref} from 'vue'


const router = useRouter()
const lobbiesStore = useLobbiesStore()

const showLobbies = () => {
  router.push({ name: 'LobbyListView' })
}

const showLeaderboard = () => {
  router.push({name: 'Leaderboard'})
}

const startSingleplayer = () => {
  lobbiesStore.lobbydata.currentPlayer.role = 'SNACKMAN'
  router.push({
    name: 'GameView',
    query: { role: 'SNACKMAN' },
  })
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
