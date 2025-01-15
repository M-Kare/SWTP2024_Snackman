import * as THREE from "three";
import {SoundType} from "@/services/SoundTypes";

export class SoundManager {
    private static characterSounds = new Map<string, THREE.PositionalAudio>();
    private static backgroundSounds = new Map<string, THREE.Audio>()

    public static attachEatingSoundToCamera(camera: THREE.Camera, hashOrModel: THREE.Group<THREE.Object3DEventMap>) {
        const listener = new THREE.AudioListener();
        camera.add(listener)

        const eatingSound = new THREE.PositionalAudio(listener)
        const audioLoader = new THREE.AudioLoader();

        audioLoader.load('src/assets/sounds/eatingSounds/collect_snack_sound.mp3', function (buffer) {
            eatingSound.setBuffer(buffer)
            eatingSound.setRefDistance(20)

        })

        SoundManager.characterSounds.set(SoundType.EAT_SNACK, eatingSound)
        hashOrModel.add(eatingSound)
    }

    public static initBackgroundSounds(camera: THREE.Camera) {
        const listener = new THREE.AudioListener();
        const sound = new THREE.Audio(listener);

        const audioLoader = new THREE.AudioLoader();
        audioLoader.load('src/assets/sounds/backgroundMusic/Game_End_Sound.mp3', function (buffer) {
            sound.setBuffer(buffer);
            sound.setVolume(0.5);
        })

        SoundManager.backgroundSounds.set(SoundType.GAME_END, sound)
    }

    public static playSound(soundType: SoundType) {
        const sound = SoundManager.characterSounds.get(soundType) || SoundManager.backgroundSounds.get(soundType);

        if (sound) {
            if (!sound.isPlaying) {
                try {
                    sound.play();
                } catch (error) {
                    console.error(`Fehler beim Abspielen des Sounds '${soundType}':`, error);
                }
            }
        } else {
            console.warn(`Sound mit Schlüssel '${soundType}' nicht gefunden.`);
        }
    }

}
