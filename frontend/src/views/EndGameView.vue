<template>
    <div class="start-page">
      <div class="background"></div>
      <div class="overlay"></div>
      <h1 class="title">Game End!</h1>
      <MainMenuButton class="map-exportieren-button" @click="exportMap">Map exportieren</MainMenuButton>
    </div>
  </template>

  <script setup lang="ts">
    import MainMenuButton from '@/components/MainMenuButton.vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const exportMap = () => {
        //router.push({name: 'GameStart'});
    }

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

  .map-exportieren-button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
  }
  </style>