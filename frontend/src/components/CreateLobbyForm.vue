<template>
  <div class="overlay"></div>
  <div id="form-box">
    <h1 id="title">New Lobby</h1>

    <form id="form" @submit.prevent="createLobby">
      <label>Enter Name: </label>
      <input v-model.trim="lobbyName" placeholder="Lobbyname" type="text" />
      <p v-if="errorMessage" id="error-message">
        {{ errorMessage }}
      </p>
    </form>

    <SmallNavButton
      id="cancel-lobby-creation-button"
      class="small-nav-buttons"
      @click="cancelLobbyCreation"
    >
      Cancel
    </SmallNavButton>
    <SmallNavButton
      id="create-lobby-button"
      class="small-nav-buttons"
      @click="createLobby"
    >
      Create Lobby
    </SmallNavButton>
  </div>
</template>

<script setup lang="ts">
import SmallNavButton from '@/components/SmallNavButton.vue'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore'
import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD'

const router = useRouter()
const lobbiesStore = useLobbiesStore()
const currentPlayer = lobbiesStore.lobbydata.currentPlayer as IPlayerClientDTD

const lobbyName = ref('')
const errorMessage = ref('')

const emit = defineEmits<{
  (event: 'cancelLobbyCreation', value: boolean): void
  (event: 'createLobby', value: string): void
}>()

/**
 * Emits an event to cancel the lobby creation process.
 *
 * @function cancelLobbyCreation
 * @returns {void}
 */
const cancelLobbyCreation = () => {
  errorMessage.value = ''
  emit('cancelLobbyCreation', false)
}

/**
 * Creates a new lobby with the specified name and admin client.
 * Validates the admin client and lobby name before attempting to create the lobby.
 * Alerts the user if there are any validation errors or if the lobby creation fails.
 * On success, redirects to the newly created lobby view.
 *
 * @async
 * @function createLobby
 * @throws {Error} Throws an alert if the admin client is invalid or the lobby name is empty or already taken.
 * @throws {Error} Throws an alert if there is an error creating the lobby.
 * @returns {void}
 */
const createLobby = async () => {
  const adminClient = currentPlayer

  if (
    !adminClient ||
    adminClient.playerId === '' ||
    adminClient.playerName === ''
  ) {
    alert('Admin client is not valid!')
    return
  }

  if (!lobbyName.value.trim()) {
    errorMessage.value = "Lobby name can't be empty"
    return
  }

  const isDuplicateName = lobbiesStore.lobbydata.lobbies.some(
    lobby => lobby.name === lobbyName.value.trim(),
  )

  if (isDuplicateName) {
    errorMessage.value =
      'Lobby name already exists! Please choose another name.'
    return
  }

  try {
    const newLobby = await lobbiesStore.createLobby(
      lobbyName.value.trim(),
      adminClient,
    )
    if (newLobby && newLobby.lobbyId) {
      cancelLobbyCreation()
      router.push({ name: 'LobbyView', params: { lobbyId: newLobby.lobbyId } })
    } else {
      throw new Error('Lobby creation returned invalid response.')
    }
  } catch (error: any) {
    console.error('Error:', error)
    alert('Error create Lobby!')
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  z-index: 1;
}

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

input::placeholder {
  color: var(--secondary-text-color);
  font-weight: bold;
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

  background-image: url('@/assets/background-design-lobbies.png');
  background-size: cover;
  background-position: center;

  border: var(--background-for-text-color) solid 4px;
  border-radius: 0.5rem;
  box-shadow: 10px 8px 0 var(--background-for-text-color);
}

#form-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #000000dd;
  border-radius: 0.5rem;
}

#form {
  position: inherit;
  top: 35%;
  left: 5%;
  width: 100%;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--background-for-text-color);
}

#form > input {
  font-size: 1.2rem;
  width: 90%;
  height: 2rem;
  margin-top: 0.2rem;
  margin-bottom: 2rem;
  padding: 1.2rem;
}

#error-message {
  font-size: 1.1rem;
  font-style: italic;
  margin-top: -1.8rem;
  color: red;
}

.small-nav-buttons {
  bottom: 7%;
  font-weight: bold;
}

#cancel-lobby-creation-button {
  left: 5%;
}

#create-lobby-button {
  right: 5%;
}

#cancel-lobby-creation-button:hover,
#create-lobby-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>
