import * as THREE from "three";
import {SoundType} from "@/services/SoundTypes";
import {Object3D} from "three";

export class SoundManager {
  private static listener: THREE.AudioListener;
  private static characterSounds = new Map<SoundType, THREE.PositionalAudio>();
  private static backgroundSounds = new Map<SoundType, THREE.Audio>();
  private static chickenSoundsToChooseFrom = new Array<THREE.PositionalAudio>();
  private static chickenSounds = new Array<THREE.PositionalAudio>();

  public static async initSoundmanager(camera: THREE.Camera): Promise<void> {
    console.log("InitSoundmanager");
    this.listener = new THREE.AudioListener();
    camera.add(this.listener);

    await this.initSounds();
  }

  private static async loadAudioAsync(bufferPath: string): Promise<AudioBuffer> {
    return new Promise((resolve, reject) => {
      const audioLoader = new THREE.AudioLoader();
      audioLoader.load(
        bufferPath,
        (buffer) => resolve(buffer),
        undefined,
        (error) => reject(`Error loading audio file '${bufferPath}': ${error}`)
      );
    });
  }

  public static attachSoundToModelOrMesh(
    meshOrModel: THREE.Group<THREE.Object3DEventMap> | THREE.Mesh | Object3D,
    soundType: SoundType
  ) {
    if (soundType === SoundType.CHICKEN && this.chickenSoundsToChooseFrom.length > 0) {
      const randomIndex = Math.floor(Math.random() * this.chickenSoundsToChooseFrom.length);
      const selectedSound = this.chickenSoundsToChooseFrom[randomIndex];

      const soundClone = new THREE.PositionalAudio(selectedSound.listener);
      if (selectedSound.buffer) {
        soundClone.setBuffer(selectedSound.buffer);
      } else {
        console.error("The selected sound's buffer is null and cannot be set.");
      }
      soundClone.setRefDistance(3) // maximum volume at x units of distance
      soundClone.setMaxDistance(12)
      soundClone.setRolloffFactor(1) // how quickly the volume decreases with increasing distance
      soundClone.setDistanceModel('linear') // decrease in volume (linear is a good choice for games)
      soundClone.setLoop(true)
      soundClone.setVolume(0.4)

      meshOrModel.add(soundClone);

      this.chickenSounds.push(soundClone)
      console.log("Random chicken sound attached to mesh.");
    } else {
      console.warn("No sounds available or invalid sound type.");
    }
  }

  public static async initSounds(): Promise<void> {
    await Promise.all([
      this.initGameEndSound(),
      this.initEatSnackSound(),
      this.initChickenSounds(),
      this.initGhostHitsSnackmanSound()
    ]);
    console.log("All sounds initialized.");
  }

  public static stopAllInGameSounds() {
    this.chickenSounds.forEach((chickenSound) => {
      chickenSound.stop();
    });
  }

  public static playSound(soundType: SoundType) {
    console.log("SoundType: " + soundType)

    if (soundType === SoundType.CHICKEN) {
      this.chickenSounds.forEach((chickenSound) => {
        console.log("Chickens are playing")
        chickenSound.play();
      });
    } else {
      const sound = this.characterSounds.get(soundType) || this.backgroundSounds.get(soundType);

      if (sound) {
        if (!sound.isPlaying) {
          try {
            sound.play();
          } catch (error) {
            console.error(`Error playing sound '${soundType}':`, error);
          }
        }
      } else {
        console.warn(`Sound with key '${soundType}' not found.`);
      }
    }
  }

  private static async initGameEndSound(): Promise<void> {
    const sound = new THREE.Audio(this.listener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/backgroundMusic/Game_End_Sound.mp3');
    sound.setBuffer(buffer);
    sound.setVolume(0.1);

    this.backgroundSounds.set(SoundType.GAME_END, sound);
    console.log("Endgame sound initialized.");
  }

  private static async initEatSnackSound(): Promise<void> {
    const sound = new THREE.PositionalAudio(this.listener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/snackman/collect_snack_sound.mp3');
    sound.setBuffer(buffer);
    sound.setRefDistance(20);

    this.characterSounds.set(SoundType.EAT_SNACK, sound);
  }

  private static async initGhostHitsSnackmanSound(): Promise<void> {
    const sound = new THREE.PositionalAudio(this.listener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/snackman/hitByGhostSound.mp3');
    sound.setBuffer(buffer);
    sound.setRefDistance(20);

    this.characterSounds.set(SoundType.GHOST_SCARES_SNACKMAN, sound);
    console.log("Ghost hits snackman sound initialized.");
  }

  private static async initChickenSounds(): Promise<void> {
    const chickenPaths = [
      "src/assets/sounds/chicken/chicken-clucking.ogg",
      "src/assets/sounds/chicken/chicken_noises.ogg",
    ];

    const promises = chickenPaths.map(async (path) => {
      try {
        const buffer = await this.loadAudioAsync(path);
        const sound = new THREE.PositionalAudio(this.listener);
        sound.setBuffer(buffer);
        this.chickenSoundsToChooseFrom.push(sound);
      } catch (error) {
        console.error(`Error loading chicken sound '${path}':`, error);
      }
    });

    await Promise.all(promises);

    //Add scaredSound for chicken
    await this.initScaredChickenSound();

    console.log("All chicken sounds initialized.");
  }

  private static async initScaredChickenSound() {
    const chickenScaredSoundPath = "src/assets/sounds/chicken/chicken-single-alarm-call.mp3"

    const promiseChickenScaredSound = await this.loadAudioAsync(chickenScaredSoundPath)
    const scaredChickenSound = new THREE.PositionalAudio(this.listener)
    scaredChickenSound.setBuffer(promiseChickenScaredSound);
    scaredChickenSound.setRefDistance(15);
    scaredChickenSound.setDistanceModel('linear')
    scaredChickenSound.setVolume(0.8);

    console.log("Scare chicken sound initialized.");
    this.characterSounds.set(SoundType.GHOST_SCARES_CHICKEN, scaredChickenSound)
  }
}
