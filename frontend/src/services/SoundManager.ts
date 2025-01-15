import * as THREE from "three";
import { SoundType } from "@/services/SoundTypes";

export class SoundManager {
    private static listener: THREE.AudioListener;
    private static characterSounds = new Map<SoundType, THREE.PositionalAudio>();
    private static backgroundSounds = new Map<SoundType, THREE.Audio>();

    public static initSoundmanager(camera: THREE.Camera) {
        this.listener = new THREE.AudioListener();
        camera.add(this.listener);

        this.initBackgroundSounds()
    }

    private static loadAudio(bufferPath: string, callback: (buffer: AudioBuffer) => void) {
        const audioLoader = new THREE.AudioLoader();
        audioLoader.load(bufferPath, callback, undefined, (error) => {
            console.error(`Fehler beim Laden der Audiodatei '${bufferPath}':`, error);
        });
    }

    public static attachEatingSound(hashOrModel: THREE.Group<THREE.Object3DEventMap>) {
        const eatingSound = new THREE.PositionalAudio(this.listener);

        this.loadAudio('src/assets/sounds/eatingSounds/collect_snack_sound.mp3', (buffer) => {
            eatingSound.setBuffer(buffer);
            eatingSound.setRefDistance(20);
        });

        this.characterSounds.set(SoundType.EAT_SNACK, eatingSound);
        hashOrModel.add(eatingSound);
    }

    private static initBackgroundSounds() {
        const sound = new THREE.Audio(this.listener);

        this.loadAudio('src/assets/sounds/backgroundMusic/Game_End_Sound.mp3', (buffer) => {
            sound.setBuffer(buffer);
            sound.setVolume(0.5);
        });

        this.backgroundSounds.set(SoundType.GAME_END, sound);
    }

    public static playSound(soundType: SoundType) {
        const sound = this.characterSounds.get(soundType) || this.backgroundSounds.get(soundType);

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
