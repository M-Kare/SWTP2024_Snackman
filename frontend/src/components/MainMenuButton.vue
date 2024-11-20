<template>
    <button class="main-menu-button" @click="handleClick">
      <slot></slot>
    </button>
  </template>
  
  <script setup lang="ts">
  const handleClick = () => {
    // TODO: onclick Implementierung
  };
  </script>
  
  <style scoped>
  .main-menu-button {
    position: relative;
    z-index: 2;
    padding: 1.5rem 3.125rem;
    border: none;
    border-radius: 0.3125rem;
    background: #fff;
    color: #000;
    font-size: 1.25rem;
    font-weight: bold;
    cursor: pointer;
    transition: box-shadow 0.3s ease, transform 0.3s ease;
  }
  
  .main-menu-button:hover {
    box-shadow: 0px 0px 40px 5px rgba(255, 255, 255, 0.7);
  }
  </style>