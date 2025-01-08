<template>
  <div class="end-screen">
    <h1 class="result-title">{{ gameResult || '–' }}</h1>
    <p class="end-reason">{{ gameReason }}</p>
    <MainMenuButton class="menu-button" @click="backToMainMenu">Zurück zum Hauptmenü
    </MainMenuButton>
    <MainMenuButton class="menu-button" @click="showLeaderboard">Leaderboard</MainMenuButton>
    <MainMenuButton class="menu-button" @click="showCreateNewLeaderboardEntryForm">Create new leaderboard entry
    </MainMenuButton>

    <Leaderboard
      v-if="showLeaderboardPopUp"
      :show="showLeaderboardPopUp"
      @close="hideLeaderboard"
    />

    <CreateNewLeaderboardEntryForm
      v-if="showCreateNewLeaderboardEntry"
      @cancelNewLeaderboardEntryCreation="cancelNewLeaderboardEntryCreation"
      @entryCreated="hideCreateNewLeaderboardEntryForm">
    </CreateNewLeaderboardEntryForm>
  </div>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue'
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import CreateNewLeaderboardEntryForm from "@/components/CreateNewLeaderboardEntryForm.vue"
import Leaderboard from "@/components/Leaderboard.vue";

const route = useRoute()
const router = useRouter()
const showCreateNewLeaderboardEntry = ref(false)
const showLeaderboardPopUp = ref(false)

// Read player role and game result from query parameters (undefined if not provided)
const gameResult = route.query.result || '-'
const playerRole = route.query.role || '-'

// Compute the game reason dynamically or display '-' if no data is available
const gameReason = computed(() => {

  if (gameResult === 'Gewonnen') {
    return playerRole === 'SNACKMAN'
      ? 'SnackMan hat 3000kcal erreicht!'
      : 'Die Geister haben SnackMan auf negative Kalorien gebracht!'
  } else {
    return playerRole === 'SNACKMAN'
      ? 'Die Geister haben SnackMan auf negative Kalorien gebracht!'
      : 'SnackMan hat 3000kcal erreicht!'
  }

})

const showLeaderboard = () => {
  showLeaderboardPopUp.value = true
}

const hideLeaderboard = () => {
  showLeaderboardPopUp.value = false
}

const cancelNewLeaderboardEntryCreation = () => {
  showCreateNewLeaderboardEntry.value = false
}

const showCreateNewLeaderboardEntryForm = () => {
  showCreateNewLeaderboardEntry.value = true
}

const hideCreateNewLeaderboardEntryForm = () => {
  showCreateNewLeaderboardEntry.value = false
}

const backToMainMenu = () => {
  router.push({name: 'MainMenu'})
}
</script>

<style scoped>
.end-screen {
  text-align: center;
  color: #fff;
  background-color: black;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.result-title {
  font-size: 8rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.end-reason {
  font-size: 2rem;
  margin-bottom: 4rem;
}

.menu-button {
  font-size: 1.5rem;
  margin-top: 4rem;
}
</style>
