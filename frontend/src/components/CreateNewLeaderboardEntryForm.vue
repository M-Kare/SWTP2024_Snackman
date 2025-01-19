<template>
  <div class="overlay"></div>
  <div id="form-box">
    <h1 id="title"> {{ $t('newLeaderBoardEntry.title') }} </h1>

    <form id="form" @submit.prevent="createNewLeaderboardEntry">
      <label> {{ $t('newLeaderBoardEntry.label') }} </label>
      <input v-model.trim="yourName" type="text">
      <p
          id="error-message"
          v-if="errorMessage">
        {{ errorMessage }}
      </p>
      <p>{{ $t('newLeaderBoardEntry.playedTime', { time: playedTime }) }}</p>
    </form>

    <SmallNavButton
        id="cancel-createNewLeaderboardEntry-creation-button"
        class="small-nav-buttons"
        @click="cancelNewLeaderboardEntryCreation">
        {{ $t('button.cancel') }} 
    </SmallNavButton>
    <SmallNavButton
        id="create-createNewLeaderboardEntry-button"
        class="small-nav-buttons"
        @click="createNewLeaderboardEntry">
        {{ $t('button.createNewLeaderBoardEntry') }} 
    </SmallNavButton>
  </div>
</template>

<script setup lang="ts">
import SmallNavButton from '@/components/SmallNavButton.vue';
import {ref} from 'vue';
import {useLeaderboardStore} from "@/stores/Leaderboard/leaderboardStore";
import type {LeaderboardEntry} from "@/stores/Leaderboard/LeaderboardDTD";
import { useI18n } from 'vue-i18n';

const yourName = ref('');
const errorMessage = ref('');
const leaderboardStore = useLeaderboardStore()

const { t } = useI18n();

const emit = defineEmits<{
  (event: 'cancelNewLeaderboardEntryCreation', value: boolean): void;
  (event: 'createNewLeaderboardEntry', value: string): void;
  (event: 'entryCreated'): void;
}>()

const props = defineProps<{
  playedTime: string
}>()

/**
 * Emits an event to cancel the new leaderboard entry creation process.
 *
 * @function cancelNewLeaderboardEntryCreation
 * @returns {void}
 */
const cancelNewLeaderboardEntryCreation = () => {
  errorMessage.value = "";
  emit('cancelNewLeaderboardEntryCreation', false);
}

/**
 * Creates a new leaderboard entry with the specified data (name, duration, releaseDate).
 * Validates the new leaderboard entry name before attempting to create the new leaderboard entry.
 * Alerts the user if there are any validation errors or if the new leaderboard entry creation fails.
 *
 * @async
 * @function createLobby
 * @throws {Error} Throws an alert if there is an error creating the new leaderboard entry.
 * @returns {void}
 */
const createNewLeaderboardEntry = async () => {
  if (!yourName.value.trim()) {
    errorMessage.value = t('newLeaderBoardEntry.error.playerNameEmpty');
    return;
  }

  const today = new Date()
  const data: LeaderboardEntry = {
    name: yourName.value,
    duration: props.playedTime,
    releaseDate: today.toISOString().slice(0, 10)
  }

  try{
    emit('entryCreated')
    await leaderboardStore.addNewLeaderboardEntry(data)
    cancelNewLeaderboardEntryCreation()
  } catch (error){
    console.error('Error:', error);
    alert("Error creating new leaderboard entry!");
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
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
  width: 90%;
  overflow: hidden;
}

input::placeholder {
  color: var(--secondary-text-color);
  font-weight: bold;
}

#input-textmessage {
  padding-bottom: 10px;
  font-weight: bold;
}

#form-box {
  z-index: 2;
  position: absolute;
  left: 50%;
  top: 25%;
  transform: translateX(-50%);
  width: 70%;
  max-width: 600px;
  height: 30rem;

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
  top: 35%;
  width: 100%;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--background-for-text-color);
}

#form > input {
  font-size: 1.2rem;
  width: 70%;
  height: 2rem;
  margin-top: 0.7rem;
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
  font-size: 0.9rem;
  font-weight: bold;
  padding: 0.7rem;
}

#cancel-createNewLeaderboardEntry-creation-button {
  left: 5%;
}

#create-createNewLeaderboardEntry-button {
  right: 5%;
}

#cancel-createNewLeaderboardEntry-creation-button:hover,
#create-createNewLeaderboardEntry-button:hover {
  background: var(--primary-highlight-color);
}
</style>
