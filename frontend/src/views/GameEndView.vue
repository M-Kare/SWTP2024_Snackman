<template>
  <div class="end-screen">
      <h1 class="result-title">{{ gameResult || '–' }}</h1>
      <p class="end-reason">{{ gameReason }}</p>
      <MainMenuButton class="menu-button" id="main-menu-button" @click="backToMainMenu">Zurück zum Hauptmenü</MainMenuButton>
  </div>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue';
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

// Read player role and game result from query parameters (undefined if not provided)
const playerRole = route.query.role;
const gameResult = route.query.result;

// Compute the game reason dynamically or display '-' if no data is available
const gameReason = computed(() => {
  if (!playerRole || !gameResult) {
    return '–';
  }

  if (gameResult === 'Gewonnen') {
    return playerRole === 'SnackMan' ? 'Snackman hat 3000kcal erreicht!' : 'Die Geister haben Snackman auf negative Kalorien gebracht!';
  } else if (gameResult === 'Verloren') {
    return playerRole === 'SnackMan' ? 'Die Geister haben Snackman auf negative Kalorien gebracht!' : 'Snackman hat 3000kcal erreicht!';
  }

  return '–';
});

const backToMainMenu = () => {
    router.push({ name: 'MainMenu' });
};
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