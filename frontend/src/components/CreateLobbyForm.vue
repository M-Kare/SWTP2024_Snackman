<template>
    <div id="create-lobby-form">
        <h1 id="title"> New Lobby </h1>

        <!-- Input-Form -->
        <form id="form">
            <p>Name</p>
            <input type="text">
        </form>

        <SmallNavButton
            id="cancel-lobby-creation-button"
            class="small-nav-buttons"
            @click="cancelLobbyCreation">
            
            Cancel
        </SmallNavButton>
        <SmallNavButton
            id="create-lobby-button"
            class="small-nav-buttons"
            @click="createLobby"> 
            
            Create Lobby
        </SmallNavButton>
    </div>
</template>

<script setup lang="ts">
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const emit = defineEmits<{
        (event: 'cancelLobbyCreation', value: boolean): void;
    }>()

    const cancelLobbyCreation = () => {
        emit('cancelLobbyCreation', false);
    }

    const createLobby = () => { 
        router.push({name: 'Lobby'});
    }

</script>

<style scoped>
#title {
    position: absolute;
    top: 1rem;
    left: 50%;
    transform: translateX(-50%);
    font-size: 2rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
}

#form {

}

#create-lobby-form {
    z-index: 2;
    position: fixed;
    left: 50%;
    top: 15%;
    transform: translateX(-50%);
    width: 60%;
    height: 40%;
    background: rgb(202, 83, 83);
    border-radius: 0.3rem;
}

.small-nav-buttons {
    font-size: 0.8rem;
    font-weight: bold;
    padding: 0.5rem;
    height: 2rem;
}

#cancel-lobby-creation-button {
    left: 5%;
}

#create-lobby-button {
    right: 5%;
    
}
#cancel-lobby-creation-button:hover, #create-lobby-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>