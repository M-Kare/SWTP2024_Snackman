<template>
    <button class="small-nav-button">
        <slot>Nav</slot> <!-- slot = default, will be overwritten -->
    </button>
</template>

<script setup lang="ts">

</script>

<style scoped>
.small-nav-button {
    position: inherit;
    font-size: 1.2rem;
    width: auto;
    padding: 1rem;
    height: auto;
    text-align: center;
    cursor: pointer;
    border: none;
    border-radius: 0.3rem;
}
</style>