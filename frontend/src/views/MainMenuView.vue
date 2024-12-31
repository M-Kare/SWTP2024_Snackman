<template>
    <div class="start-page">
      <div class="background"></div>
      <div class="overlay"></div>
      <h1 class="title">Snackman</h1>
      <div class="custom-map">
        <span>Custom Map</span>
        <CustomMapButton v-model="toggleState"></CustomMapButton>
      </div>
      <MapButton v-if="toggleState" class="map-importiren-button">Map Importieren</MapButton>
      <MainMenuButton class="start-button" @click="startGame">Spiel starten</MainMenuButton>
    </div>
  </template>

  <script setup lang="ts">
    import CustomMapButton from '@/components/CustomMapButton.vue';
    import MainMenuButton from '@/components/MainMenuButton.vue';
    import MapButton from '@/components/MapButton.vue';
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const startGame = () => {
        router.push({name: 'GameStart'});
    }

    const toggleState= ref(false);
  </script>

  <style scoped>
  .start-page {
    width: 100vw;
    height: 100vh;
    position: relative;
    overflow: hidden;
  }

  .background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('@/assets/main_menu-bg.jpg');
    background-size: cover;
    background-position: center;
    z-index: 1;
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 2;
  }

  .title {
    position: absolute;
    top: 50px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 10rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
    z-index: 3;
  }

  .custom-map{
    position: absolute;
    right: 20px;
    top: 30px;
    display: flex;
    gap: 25px;
    justify-content: center;
    align-items: center;
    font-size: 3rem;
    color: #fff;
    z-index: 3;
  }

  .map-importiren-button{
    position: absolute;
    right: 20px;
    top: 30px;
    z-index: 3;
  }

  .start-button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
  }
  </style>
