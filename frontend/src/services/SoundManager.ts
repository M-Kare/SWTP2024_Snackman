import * as THREE from "three";
import {SoundType} from "@/services/SoundTypes";

export class SoundManager {
    private static characterSounds = new Map<string, THREE.PositionalAudio>();

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

    public static playSound(soundType: SoundType) {
        const sound = SoundManager.characterSounds.get(soundType);
        if (sound) {
            if (!sound.isPlaying)
                sound.play()
        } else {
            console.warn(`Sound with key '${soundType}' not found.`);
        }
    }
}
