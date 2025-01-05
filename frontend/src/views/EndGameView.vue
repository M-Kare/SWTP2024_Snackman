<template>
  <div class="start-page">
    <div class="background"></div>
    <div class="overlay"></div>
    <h1 class="title">Game End!</h1>
    <MapButton class="map-exportieren-button" @click="downloadMap">Map exportieren</MapButton>
    <div v-if="feedbackMessage" :class="['feedback-message', feedbackClass]">
      {{ feedbackMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
  import MapButton from '@/components/MapButton.vue';
  import { ref } from 'vue';

  const feedbackMessage = ref('');
  const feedbackClass = ref('');
    
  const downloadMap = async () => {
    try{
      const response = await fetch(`/api/download`);

      if(!response.ok) throw new Error('Failed to download map.');

      const blob = await response.blob();
      const link = document.createElement('a');   // Create an anchor element to trigger the download
      const url = URL.createObjectURL(blob);
      link.href = url;
      link.download = 'SnackManMap.txt';
      document.body.appendChild(link);            // Append the link to the DOM
      link.click();                               // Simulate a click to start the download        
      document.body.removeChild(link);            // Remove the link after the download

      // Revoke the object URL to free up memory
      URL.revokeObjectURL(url);
      
      // Success feedback
      feedbackMessage.value = 'Map saved';
      feedbackClass.value = 'success';
      console.log('Download Map sucessful!');     
    } catch(error: any){
      console.error('Error downloading file:', error);

      // Failure feedback
      feedbackMessage.value = 'Map not saved';
      feedbackClass.value = 'error';
    }

    // Clear feedback after 3 seconds
    setTimeout(() => {
      feedbackMessage.value = '';
      feedbackClass.value = '';
    }, 3000);
  }

</script>

<style scoped>
  .start-page {
    width: 100vw;
    height: 100vh;
    position: relative;
    overflow: hidden;
  }

  .background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('@/assets/main_menu-bg.jpg');
    background-size: cover;
    background-position: center;
    z-index: 1;
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 2;
  }

  .title {
    position: absolute;
    top: 50px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 10rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
    z-index: 3;
  }

  .map-exportieren-button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
  }

  .feedback-message {
    position: absolute;
    left: 50%;
    top: 60%;
    transform: translate(-50%, -40%);
    font-size: 2rem;
    font-weight: bold;
    z-index: 3;
    padding: 10px 20px;
    border-radius: 5px;
    text-align: center;
    animation: fadeIn 0.5s;
  }

  .feedback-message.success {
    color: #fff;
    background-color: #50C878;
  }

  .feedback-message.error {
    color: #fff;
    background-color: #C70039;
  }

  /* Fade-in animation */
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
</style>