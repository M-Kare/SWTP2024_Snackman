<template>
    <div id="form-box">
        <form id="form" @submit.prevent="savePlayerName">
            <label>
                
                Please enter your name:
                <input v-model.trim="playerName" type="text" autofocus>
            </label>
            <p
                id="error-message"
                v-if="errorMessage">

                {{ errorMessage }}
            </p>
        </form>

        <SmallNavButton
            id="save-name-button"
            class="small-nav-button"
            @click="savePlayerName"> 
            
            Save
        </SmallNavButton>
    </div>
</template>

<script setup lang="ts">
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import { ref } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';

    const lobbiesStore = useLobbiesStore();
    const currentPlayer = lobbiesStore.lobbydata.currentPlayer as IPlayerClientDTD;
    
    const playerName = ref('');
    const errorMessage = ref('');

    // TODO needed?
    const emit = defineEmits<(event: 'savePlayerName', value: string) => void>()

    // TODO implement
    /**
     * Saves the name of a player.
     * Validates the admin client and playerName before attempting to save the playerName.
     * Alerts the user if there are any validation errors or if the save fails.
     * On success, hides the popup and shows the main menu.
     * 
     * @async
     * @function savePlayerName
     * @throws {Error} Shows a popup if there is an error while saving the playerName.
     * @returns {void}
     */
    const savePlayerName = async () => {
        const adminClient = currentPlayer;
    
        // if (!adminClient || adminClient.playerId === '' || adminClient.playerName === '') {
        //     alert("Admin client is not valid!");
        //     return;
        // }

        if (!playerName.value.trim()) {
            errorMessage.value = "Playername can't be empty";
            return;
        }

        // TODO implement
        const isDuplicateName = lobbiesStore.lobbydata.lobbies.some(
            (lobby) => lobby.name === playerName.value.trim()
        );

        if (isDuplicateName) {
            errorMessage.value = "Playername already exists! Please choose another name.";
            return;
        }

        // TODO implement
        try{
            const newLobby = await lobbiesStore.createLobby(playerName.value.trim(), adminClient);
            if (newLobby && newLobby.uuid) {
                // cancelLobbyCreation();
            } else {
                throw new Error("Name save returned invalid response.");
            }
        } catch (error: any){
            console.error('Error:', error);
            alert("Error saving PlayerName!");
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
    top: 40%;
    transform: translateX(-50%);
    width: 60%;
    max-width: 600px;
    height: 12rem;
    background: #172D54;
    border-radius: 0.3rem;
}

#form {
    position: inherit;
    top: 10%;
    left: 5%;
    width: 100%;
    font-size: 1.5rem;
    font-weight: bold;
    color: #fff;
}

#form > label {
    display: block;
    margin-bottom: 2rem;
}

#form > label > input {
    font-size: 1.2rem;
    width: 90%;
    height: 2rem;
    padding: 1.2rem;
}


#error-message {
    font-size: 1.1rem;
    font-style: italic;
    margin-top: -1.8rem;
    color: red;
}

#save-name-button {
    bottom: 10%;
    left: 50%;
    transform: translate(-50%);
    font-size: 1.1rem;
    font-weight: bold;
    padding: 0.5rem;
    height: auto;
    width: auto;
}
#save-name-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>