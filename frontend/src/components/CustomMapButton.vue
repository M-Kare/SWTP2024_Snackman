<template>
    <div class="outside-toggle-switch" @click="toggle" :class="{checked: modelValue}">
        <div class="inside-toggle-switch" :class="{checked: modelValue}">
        </div>
    </div>
</template>

<script setup lang="ts">
    const props = defineProps({
        modelValue: {
            required: true,
            type: Boolean
        }
    })

    const emit = defineEmits(['update:modelValue'])

    const toggle = () => {
        emit('update:modelValue', !props.modelValue)
    }
</script>

<style>
.outside-toggle-switch {
    width: 7rem;
    height: 3.5rem;
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
    width: 3rem;
    height: 3rem;
    background-color: white;
    border-radius: 50%;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease-in-out;
}

.inside-toggle-switch.checked {
    transform: translateX(3.5rem);
}
</style>