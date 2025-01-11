<template>
  <button class="button-mainmenu" @click="handleClick">
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';

defineProps({
  type: {
    type: String,
    default: 'button',
  },
});

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click');
};
</script>

<style scoped>
.button-mainmenu {
  display: inline-flex;
  padding: 2rem 3.75rem;
  justify-content: center;
  align-items: center;
  gap: 0.625rem;

  border-radius: 0.5rem;
  border: 6px solid var(--black);
  background: var(--white);
  box-shadow: 10px 10px 0px 0px var(--black);

  font-size: 3.5rem; /* h3 font-size */
  font-family: var(--font-main);
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.button-mainmenu:hover {
  background: var(--primary);
  color: var(--black);
}
</style>