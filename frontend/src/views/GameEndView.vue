<template>
  <div class="end-screen">
      <h1 class="result-title">{{ gameResult }}</h1>
      <p class="end-reason">{{ gameReason }}</p>
      <MainMenuButton class="menu-button" id="main-menu-button" @click="backToMainMenu">Zurück zum Hauptmenü</MainMenuButton>
  </div>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const gameResult = ref("WIN | LOSE");
const gameReason = ref("Placeholder Reason");

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