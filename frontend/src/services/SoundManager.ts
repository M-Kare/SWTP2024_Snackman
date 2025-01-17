import * as THREE from "three";
import {SoundType} from "@/services/SoundTypes";
import {Object3D} from "three";

export class SoundManager {
  private static ingameListener: THREE.AudioListener;
  private static lobbyListener: THREE.AudioListener;
  private static characterSounds = new Map<SoundType, THREE.PositionalAudio>();
  private static backgroundSounds = new Map<SoundType, THREE.Audio>();
  private static chickenSoundsToChooseFrom = new Array<THREE.PositionalAudio>();
  private static chickenSounds = new Array<THREE.PositionalAudio>();

  public static async initSoundmanager(camera: THREE.Camera): Promise<void> {
    console.log("InitSoundmanager");
    this.ingameListener = new THREE.AudioListener();
    camera.add(this.ingameListener);


    await this.initSounds();
  }

  public static async initBackgroundMusicManager(): Promise<void> {
    console.log("InitBackgroundMusicManager")
    this.lobbyListener = new THREE.AudioListener();
    await this.initBackgroundSounds();
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

    this.backgroundSounds.get(SoundType.INGAME_BACKGROUND)?.stop();
  }

  public static stopLobbySound() {
    this.backgroundSounds.get(SoundType.LOBBY_MUSIC)?.stop();
  }

  public static playSound(soundType: SoundType) {
    if (soundType === SoundType.CHICKEN) {
      this.chickenSounds.forEach((chickenSound) => {
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
    const sound = new THREE.Audio(this.ingameListener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/backgroundMusic/Game_End_Sound.mp3');
    sound.setBuffer(buffer);
    sound.setVolume(0.1);

    this.backgroundSounds.set(SoundType.GAME_END, sound);
    console.log("Endgame sound initialized.");
  }

  private static async initBackgroundSounds(): Promise<void> {
    await this.initIngameBackgroundMusic()
    await this.initLobbyBackgroundMusic()
  }

  private static async initIngameBackgroundMusic(): Promise<void> {
    const ingameMusic = new THREE.Audio(this.lobbyListener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/backgroundMusic/ingameBackgroundMusic.mp3');
    ingameMusic.setBuffer(buffer);
    ingameMusic.setVolume(0.2);
    ingameMusic.setLoop(true);

    this.backgroundSounds.set(SoundType.INGAME_BACKGROUND, ingameMusic);
    console.log("Ingame music initialized.");
  }

  private static async initLobbyBackgroundMusic(): Promise<void> {
    const lobbyMusic = new THREE.Audio(this.lobbyListener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/backgroundMusic/lobbyMusic.mp3');
    lobbyMusic.setBuffer(buffer);
    lobbyMusic.setVolume(0.3);
    lobbyMusic.setLoop(true);

    this.backgroundSounds.set(SoundType.LOBBY_MUSIC, lobbyMusic);
    console.log("Lobby music initialized.");
  }

  private static async initEatSnackSound(): Promise<void> {
    const sound = new THREE.PositionalAudio(this.ingameListener);
    const buffer = await this.loadAudioAsync('src/assets/sounds/snackman/collect_snack_sound.mp3');
    sound.setBuffer(buffer);
    sound.setRefDistance(20);

    this.characterSounds.set(SoundType.EAT_SNACK, sound);
  }

  private static async initGhostHitsSnackmanSound(): Promise<void> {
    const sound = new THREE.PositionalAudio(this.ingameListener);
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
        const sound = new THREE.PositionalAudio(this.ingameListener);
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
    const scaredChickenSound = new THREE.PositionalAudio(this.ingameListener)
    scaredChickenSound.setBuffer(promiseChickenScaredSound);
    scaredChickenSound.setRefDistance(15);
    scaredChickenSound.setDistanceModel('linear')
    scaredChickenSound.setVolume(0.8);

    console.log("Scare chicken sound initialized.");
    this.characterSounds.set(SoundType.GHOST_SCARES_CHICKEN, scaredChickenSound)
  }
}
