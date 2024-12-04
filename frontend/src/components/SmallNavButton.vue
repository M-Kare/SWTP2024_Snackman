<template>
    <button class="small-nav-button">
        <slot>Nav</slot> <!-- slot = default, will be overwritten -->
    </button>
</template>

<script setup lang="ts">
    import { useRouter } from 'vue-router';

    const router = useRouter();

</script>

<style scoped>
.small-nav-button {
    position: inherit;
    bottom: 3%;
    font-size: 1.2rem;
    width: auto;
    padding: 1rem;
    height: 3.5rem;
    text-align: center;
    cursor: pointer;
    border: none;
    border-radius: 0.3rem;
}
</style>