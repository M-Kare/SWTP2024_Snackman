<template>
    <button class="map-button" @click="handleClick">
      <slot></slot>
    </button>
</template>
  
<script setup lang="ts">
  const handleClick = () => {
  };

</script>
  
<style scoped>
.map-button {
  position: relative;
  padding: 1.75rem 4rem;
  border: none;
  border-radius: 0.3125rem;
  background: #fff;
  color: #000;
  font-size: 2rem;
  font-weight: bold;
  cursor: pointer;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.map-button:hover {
  box-shadow: 0px 0px 40px 5px rgba(255, 255, 255, 0.7);
}
</style>