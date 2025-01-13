<template>
  <div class="end-screen">
    <h1 class="result-title">{{ gameResult }}</h1>
    <p class="end-reason">{{ gameReason }}</p>
    <p class="end-reason">Ihr habt {{ formatedPlayedTime }} Minuten lang gespielt</p>
    <p class="end-reason">SnackMan hat {{ kcalCollected }} Kalorien gesammelt!</p>
    <MainMenuButton class="menu-button" @click="backToMainMenu">Zurück zum Hauptmenü
    </MainMenuButton>
    <MainMenuButton class="menu-button" @click="showCreateNewLeaderboardEntryForm"
                    v-if="!alreadyEntered && lobbydata.currentPlayer.role == 'SNACKMAN' && winningRole == 'SNACKMAN'">Create new leaderboard entry
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

      <MapButton class="map-exportieren-button" @click="downloadMap">Map exportieren</MapButton>
      <div v-if="feedbackMessage" :class="['feedback-message', feedbackClass]">
        {{ feedbackMessage }}
      </div>
  </div>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue'
import MapButton from '@/components/MapButton.vue';
import {computed, ref, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import CreateNewLeaderboardEntryForm from "@/components/CreateNewLeaderboardEntryForm.vue"
import Leaderboard from "@/components/Leaderboard.vue";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";

const route = useRoute()
const router = useRouter()
const showCreateNewLeaderboardEntry = ref(false)
const showLeaderboardPopUp = ref(false)
const alreadyEntered = ref(false)
const {lobbydata} = useLobbiesStore()

// Read player role and game result from query parameters
const lobbyId = route.query.lobby || '-';
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

// Compute the game reason dynamically or display '-' if no data is available
const gameReason = computed(() => {
  if (winningRole === 'SNACKMAN') {
    return 'SnackMan hat das Kalorienziel erreicht!'
  } else if (winningRole === 'GHOST' && kcalCollected < 0) {
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


const feedbackMessage = ref('');
const feedbackClass = ref('');
    
const downloadMap = async () => {
  try{
      const response = await fetch(`/api/download?lobbyId=${lobbyId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        },
      });

      if(!response.ok) throw new Error('Failed to download map.');

      const blob = await response.blob();
      const link = document.createElement('a');   // Create an anchor element to trigger the download
      const url = URL.createObjectURL(blob);
      link.href = url;
      link.download = 'SnackManMap.txt';
      document.body.appendChild(link);            // Append the link to the DOM
      link.click();                               // Simulate a click to start the download        
      document.body.removeChild(link);            // Remove the link after the download

      // Revoke the object URL to free up memory
      URL.revokeObjectURL(url);
      
      // Success feedback
      feedbackMessage.value = 'Map saved';
      feedbackClass.value = 'success';    
  } catch(error: any){
      // Failure feedback
      feedbackMessage.value = 'Map not saved';
      feedbackClass.value = 'error';
  }
  // Clear feedback after 3 seconds
  setTimeout(() => {
    feedbackMessage.value = '';
    feedbackClass.value = '';
  }, 3000);
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

.map-exportieren-button {
  font-size: 1.5rem;
  margin-top: 4rem;
}

.feedback-message {
  font-size: 2rem;
  margin-top: 4rem;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 5px;
  text-align: center;
  animation: fadeIn 0.5s;
}

.feedback-message.success {
  color: #fff;
  background-color: #50C878;
}

.feedback-message.error {
  color: #fff;
  background-color: #C70039;
}

/* Fade-in animation */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
