<template>
  <MenuBackground>
    <div class="button-container">
      <MainMenuButton @click="startSingleplayer">Singleplayer</MainMenuButton>
      <MainMenuButton @click="showLobbies">Multiplayer</MainMenuButton>
      <MainMenuButton @click="showLeaderboard">Leaderboard</MainMenuButton>
    </div>

    <Leaderboard
      v-if="showLeaderboardPopUp"
      :show="showLeaderboardPopUp"
      @close="hideLeaderboard"
    />
  </MenuBackground>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue'
import MenuBackground from '@/components/MenuBackground.vue'
import Leaderboard from '@/components/Leaderboard.vue'
import { useRouter } from 'vue-router'
import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore'
import { ref } from 'vue'

const router = useRouter()
const lobbiesStore = useLobbiesStore()
const showLeaderboardPopUp = ref(false)

const showLobbies = () => {
  router.push({ name: 'LobbyListView' })
}

const showLeaderboard = () => {
  showLeaderboardPopUp.value = true
}

const hideLeaderboard = () => {
  showLeaderboardPopUp.value = false
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
