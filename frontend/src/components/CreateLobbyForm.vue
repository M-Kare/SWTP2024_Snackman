<template>
    <div id="form-box">
        <h1 id="title"> New Lobby </h1>

        <form id="form" @submit.prevent="createLobby">
            <label>Enter Name: </label>
            <input v-model.trim="lobbyName" type="text">
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
    import { computed, onMounted, ref } from 'vue';
    import { useLobbiesStore } from '@/stores/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/IPlayerClientDTD';

    const router = useRouter();
    const lobbiesStore = useLobbiesStore();
    const currentPlayer = lobbiesStore.lobbydata.currentPlayer as IPlayerClientDTD;
    
    const lobbyName = ref('');

    const emit = defineEmits<{
        (event: 'cancelLobbyCreation', value: boolean): void;
        (event: 'createLobby', value: string): void;
    }>()

    const cancelLobbyCreation = () => {
        emit('cancelLobbyCreation', false);
    }

    const createLobby = async () => {
        const adminClient = currentPlayer;
        adminClient.playerId = currentPlayer.playerId;
        adminClient.playerName = currentPlayer.playerName;
        adminClient.role = currentPlayer.role;

        if (!adminClient || adminClient.playerId === '' || adminClient.playerName === '') {
            alert("Admin client is not valid!");
            return;
        }

        if (!lobbyName.value.trim()) {
            alert("Lobby name cannot be empty!");
            return;
        }

        const isDuplicateName = lobbiesStore.lobbydata.lobbies.some(
            (lobby) => lobby.name === lobbyName.value.trim()
        );

        if (isDuplicateName) {
            alert("Lobby name already exists! Please choose another name.");
            return;
        }

        try{
            const newLobby = await lobbiesStore.createLobby(lobbyName.value.trim(), adminClient);
            if (newLobby && newLobby.uuid) {
                alert("Lobby created successfully!");
                await lobbiesStore.updateLobbies();
                cancelLobbyCreation();
                router.push({ name: "Lobby", params: { lobbyId: newLobby.uuid } });
            } else {
                throw new Error("Lobby creation returned invalid response.");
            }
        } catch (error: any){
            console.error('Error:', error);
            alert("Error create Lobby!");
        }
    }

</script>

<style scoped>
#title {
    position: absolute;
    top: 1rem;
    left: 50%;
    transform: translateX(-50%);
    font-size: 2.3rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
}

#form-box {
    z-index: 2;
    position: absolute;
    left: 50%;
    top: 25%;
    transform: translateX(-50%);
    width: 60%;
    max-width: 600px;
    height: 20rem;
    background: #172D54;
    border-radius: 0.3rem;
}

#form {
    position: inherit;
    top: 35%;
    left: 5%;
    width: 100%;
    font-size: 1.5rem;
    font-weight: bold;
    color: #fff;
}

#form > input {
    font-size: 1.2rem;
    width: 90%;
    height: 2rem;
    margin-top: 0.2rem;
    margin-bottom: 2rem;
    padding: 1.2rem;
}

.small-nav-buttons {
    bottom: 7%;
    font-size: 0.9rem;
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