<template>
  <div class="end-screen">
    <h1 class="result-title">{{ gameResult }}</h1>
    <p class="end-reason">{{ gameReason }}</p>
    <p class="end-reason">Ihr habt {{ formatedPlayedTime }} Minuten lang gespielt</p>
    <p class="end-reason">SnackMan hat {{ kcalCollected }} Kalorien gesammelt!</p>
    <MainMenuButton class="menu-button" @click="backToMainMenu">Zurück zum Hauptmenü
    </MainMenuButton>
    <MainMenuButton class="menu-button" @click="showLeaderboard">Leaderboard</MainMenuButton>
    <MainMenuButton class="menu-button" @click="showCreateNewLeaderboardEntryForm"
                    v-if="!alreadyEntered && lobbydata.currentPlayer.role == 'SNACKMAN'">Create new leaderboard entry
    </MainMenuButton>

    <Leaderboard
      v-if="showLeaderboardPopUp"
      :show="showLeaderboardPopUp"
      @close="hideLeaderboard"
    />

    <CreateNewLeaderboardEntryForm
      v-if="showCreateNewLeaderboardEntry"
      :playedTime="formatTime(playedTime)"
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
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";
import type {IPlayerClientDTD} from "@/stores/Lobby/IPlayerClientDTD";

const route = useRoute()
const router = useRouter()
const showCreateNewLeaderboardEntry = ref(false)
const showLeaderboardPopUp = ref(false)
const alreadyEntered = ref(false)
const {lobbydata} = useLobbiesStore()

// Read player role and game result from query parameters (undefined if not provided)
const winningRole = (route.query.winningRole as string) || '-'
const playedTime = (route.query.timePlayed as unknown as number) || 0
const formatedPlayedTime = formatTime(playedTime)
const kcalCollected = (route.query.kcalCollected as unknown as number) || 0
const gameResult = ref()

if (winningRole == "SNACKMAN") {
  gameResult.value = "Der Snackman hat gewonnen."
} else {
  gameResult.value = "Die Geister haben gewonnen."
}

// Compute the game reason dynamically or display '-' if no data is available
const gameReason = computed(() => {
  if (winningRole === 'SNACKMAN') {
    return 'SnackMan hat das Kalorienziel erreicht!'
  } else if (winningRole === "GHOST" && kcalCollected < 0) {
    return 'Die Geister haben SnackMan auf negative Kalorien gebracht!'
  }
  return 'Die Zeit ist abgelaufen und SnackMan hat nicht genügend Kalorien gesammelt!'
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
  alreadyEntered.value = true
}

const backToMainMenu = () => {
  router.push({name: 'MainMenu'})
}

function formatTime(seconds: number): string {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
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
