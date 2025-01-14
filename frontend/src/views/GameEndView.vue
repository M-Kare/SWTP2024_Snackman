<template>
  <div class="end-screen">
    <h1 class="result-title">{{ gameResult }}</h1>
    <p class="end-reason">{{ gameReason }}</p>
    <p class="end-reason">Ihr habt {{ formatedPlayedTime }} Minuten lang gespielt</p>
    <p class="end-reason">SnackMan hat {{ kcalCollected }} Kalorien gesammelt!</p>
    <MainMenuButton class="menu-button" @click="backToMainMenu">Zurück zum Hauptmenü
    </MainMenuButton>
    <MainMenuButton v-if="!alreadyEntered && lobbydata.currentPlayer.role == 'SNACKMAN' && winningRole == 'SNACKMAN'"
                    class="menu-button"
                    @click="showCreateNewLeaderboardEntryForm">
      Create new leaderboard entry
    </MainMenuButton>

    <CreateNewLeaderboardEntryForm
      v-if="showCreateNewLeaderboardEntry"
      :playedTime="formatTime(playedTime)"
      @cancelNewLeaderboardEntryCreation="cancelNewLeaderboardEntryCreation"
      @entryCreated="hideCreateNewLeaderboardEntryForm">
    </CreateNewLeaderboardEntryForm>

  </div>
</template>

<script lang="ts" setup>
import MainMenuButton from '@/components/MainMenuButton.vue'
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import CreateNewLeaderboardEntryForm from "@/components/CreateNewLeaderboardEntryForm.vue"
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";

const route = useRoute()
const router = useRouter()
const showCreateNewLeaderboardEntry = ref(false)
const alreadyEntered = ref(false)
const {lobbydata} = useLobbiesStore()

// Read player role and game result from query parameters
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

/**
 * Computes the reason for the game's result based on the winning role and game conditions.
 */
const gameReason = computed(() => {
  if (winningRole === 'SNACKMAN') {
    return 'SnackMan hat das Kalorienziel erreicht!'
  } else if (winningRole === 'GHOST' && kcalCollected < 0) {
    return 'Die Geister haben SnackMan auf negative Kalorien gebracht!'
  }
  return 'Die Zeit ist abgelaufen und SnackMan hat nicht genügend Kalorien gesammelt!'
})

const showLeaderboard = () => {
  if (winningRole && winningRole !== '-') {
    router.push({
      name: 'Leaderboard',
      query: { winningRole: winningRole },
    })
  } else {
    console.debug('no winning role')
    router.push({ name: 'Leaderboard' })
  }
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
  showLeaderboard() // show leaderboard after creating a new entry in it
}

const backToMainMenu = () => {
  router.push({name: 'MainMenu'})
}

/**
 * Formats the time in seconds into a string in the format `MM:SS`.
 * @param {number} seconds - The time in seconds to be formatted.
 * @returns {string} The formatted time string.
 */
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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  box-sizing: border-box;
}

.result-title {
  font-size: clamp(3rem, 8vw, 8rem);
  line-height: 1.2;
  margin-bottom: 1rem;
  font-weight: bold;
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
