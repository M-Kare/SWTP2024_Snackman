<template>
    <div id="popup-box">
        <slot id="info-text">Info-Text</slot>
    </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
#popup-box {
    z-index: 2;
    position: absolute;
    left: 50%;
    top: 25%;
    transform: translateX(-50%);
    width: 60%;
    max-width: 600px;
    height: auto;
    background:rgb(204, 90, 90);
    border-radius: 0.3rem;
}
</style>