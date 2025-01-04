<template>
  <div id="form-box">
    <h1 id="title"> New Leaderboard entry</h1>

    <form id="form" @submit.prevent="createNewLeaderboardEntry">
      <label>Enter your name: </label>
      <input v-model.trim="yourName" type="text">
      <p
          id="error-message"
          v-if="errorMessage">
        {{ errorMessage }}
      </p>
    </form>

    <SmallNavButton
        id="cancel-createNewLeaderboardEntry-creation-button"
        class="small-nav-buttons"
        @click="cancelNewLeaderboardEntryCreation">
      Cancel
    </SmallNavButton>
    <SmallNavButton
        id="create-createNewLeaderboardEntry-button"
        class="small-nav-buttons"
        @click="createNewLeaderboardEntry">
      Create new leaderboard entry
    </SmallNavButton>
  </div>
</template>

<script setup lang="ts">
import SmallNavButton from '@/components/SmallNavButton.vue';
import { ref } from 'vue';
import {useLeaderboardStore} from "@/stores/Leaderboard/leaderboardStore";
import type {LeaderboardEntry} from "@/stores/Leaderboard/LeaderboardDTD";

const yourName = ref('');
const errorMessage = ref('');

const leaderboardStore = useLeaderboardStore()

const emit = defineEmits<{
  (event: 'cancelNewLeaderboardEntryCreation', value: boolean): void;
  (event: 'createNewLeaderboardEntry', value: string): void;
}>()

const cancelNewLeaderboardEntryCreation = () => {
  errorMessage.value = "";
  emit('cancelNewLeaderboardEntryCreation', false);
}

const createNewLeaderboardEntry = async () => {
  if (!yourName.value.trim()) {
    errorMessage.value = "Your name name cannot be empty";
    return;
  }

  // TODO replace default data here
  const data: LeaderboardEntry = {
    name: yourName.value,
    duration: "00:00",
    releaseDate: "2025-01-01"
  }

  try{
    await leaderboardStore.addNewLeaderboardEntry(data)
    cancelNewLeaderboardEntryCreation()
  } catch (error: any){
    console.error('Error:', error);
    alert("Error creating new leaderboard entry!");
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
  padding: 0.5rem;
  height: 2rem;
}

#cancel-createNewLeaderboardEntry-creation-button {
  left: 5%;
}

#create-createNewLeaderboardEntry-button {
  right: 5%;
}
#cancel-createNewLeaderboardEntry-creation-button:hover, #create-createNewLeaderboardEntry-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>
