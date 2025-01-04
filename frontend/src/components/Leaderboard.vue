<template>
  <div v-if="show" id="darken-background"></div>
  <PopUp class="popup-box"
         v-if="show"
         @hidePopUp="$emit('close')">

    <p class="info-heading"> Leaderboard </p>
    <p class="info-text"> This is the current leaderboard: </p>
    <table>
      <thead>
      <tr>
        <td>Name</td>
        <td>Duration</td>
        <td>Date</td>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>Test</td>
        <td>Test</td>
        <td>Test</td>
      </tr>
      </tbody>
    </table>
  </PopUp>
</template>

<script setup lang="ts">
import PopUp from "@/components/PopUp.vue";

defineProps({
  show: {
    type: Boolean,
    required: true
  }
})

defineEmits(['close'])
</script>

<style scoped>

.info-heading {
  font-size: 3rem;
  font-weight: bold;
}

.info-text {
  font-size: 1.8rem;
  padding: 1.2rem;
}

.info-text {
  font-size: 1.8rem;
  padding: 1.2rem;
}

#darken-background {
  z-index: 1;
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 50%);

  transition: background 0.3s ease;
}

#popup-box {
  z-index: 10;
  position: absolute;
  top: 25%;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  max-width: 600px;
  height: 16rem;
  background:#ffffff;
  border-radius: 0.3rem;
  text-align: center;
  padding: 1rem;
  color: #000000;
}

table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
  overflow-x: auto;
}

thead th:nth-child(1) {
  width: 30%;
}

thead th:nth-child(2) {
  width: 20%;
}

thead th:nth-child(3) {
  width: 15%;
}

th,
td {
  padding: 20px;
  white-space: nowrap;
}

</style>
