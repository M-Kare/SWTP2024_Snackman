<template>
  <div class="menu-background">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.menu-background {
  background: url('@/assets/background-design.png') no-repeat center center;
  background-size: cover;
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}
</style>