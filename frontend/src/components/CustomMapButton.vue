<template>
    <div class="outside-toggle-switch" @click="toggle" :class="{checked: toggleState}">
        <div class="inside-toggle-switch" :class="{checked: toggleState}">
        </div>
    </div>
</template>

<script setup lang="ts">
    const props = defineProps({
        toggleState: Boolean,
    })

    const emit = defineEmits(['updateToggleState'])

    const toggle = () => {
        emit('updateToggleState', !props.toggleState)
    }
</script>

<style>
.outside-toggle-switch {
    width: 6rem;
    height: 3rem;
    padding: 0.25rem;
    display: flex;
    align-items: center;
    background-color: #d1d5db;
    border-radius: 9999px;
}

.outside-toggle-switch.checked{
    background-color: #003366;
}

.inside-toggle-switch {
    width: 2.5rem;
    height: 2.5rem;
    background-color: white;
    border-radius: 50%;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease-in-out;
}

.inside-toggle-switch.checked {
    transform: translateX(3rem);
}
</style>