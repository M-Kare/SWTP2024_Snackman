<template>
  <div class="end-screen">
      <h1 class="result-title">{{ gameResult || '–' }}</h1>
      <p class="end-reason">{{ gameReason }}</p>
      <MainMenuButton class="menu-button" id="main-menu-button" @click="backToMainMenu">Zurück zum Hauptmenü</MainMenuButton>
      <MapButton class="map-exportieren-button" @click="downloadMap">Map exportieren</MapButton>
      <div v-if="feedbackMessage" :class="['feedback-message', feedbackClass]">
        {{ feedbackMessage }}
      </div>
  </div>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue';
import MapButton from '@/components/MapButton.vue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

// Read player role and game result from query parameters (undefined if not provided)
const gameResult = route.query.result || '-';
const playerRole = route.query.role || '-';

// Compute the game reason dynamically or display '-' if no data is available
const gameReason = computed(() => {

  if (gameResult === 'Gewonnen') {
    return playerRole === 'SNACKMAN'
      ? 'SnackMan hat das Kalorienziel erreicht!'
      : 'Die Geister haben SnackMan auf negative Kalorien gebracht!';
  } else {
    return playerRole === 'SNACKMAN'
      ? 'Die Geister haben SnackMan auf negative Kalorien gebracht!'
      : 'SnackMan hat das Kalorienziel erreicht!';
  }

});

const backToMainMenu = () => {
    router.push({ name: 'MainMenu' });
};

const feedbackMessage = ref('');
const feedbackClass = ref('');
    
const downloadMap = async () => {
  try{
      const response = await fetch(`/api/download`);

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