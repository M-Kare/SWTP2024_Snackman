<template>
    <div class="start-page">
      <div class="background"></div>
      <div class="overlay"></div>
    </div>
  </template>
  
  <script setup lang="ts"></script>
  
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
    z-index: -10;
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: -9;
  }
  </style>