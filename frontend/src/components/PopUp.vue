<template>
    <div id="popup-box">
        <slot> PopUp-Message </slot>
        <SmallNavButton
            id="ok-button"
            @click="$emit('hidePopUp')">

            OK
        </SmallNavButton>
    </div>
</template>

<script setup lang="ts">
import SmallNavButton from '@/components/SmallNavButton.vue';
</script>

<style scoped>
#popup-box {
    z-index: 10;
    position: absolute;
    top: 25%;
    left: 50%;
    transform: translateX(-50%);
    width: 60%;
    max-width: 600px;
    height: 16rem;
    background:rgb(204, 90, 90);
    border-radius: 0.3rem;
    text-align: center;
    padding: 1rem;
    color: #000000;
}

#ok-button {
    left: 50%;
    transform: translateX(-50%);
    font-size: 1.2rem;
    margin-top: 1rem;
    padding: 0.7rem;
}

#ok-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>