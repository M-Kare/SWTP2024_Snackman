import * as THREE from 'three'
import {SnackType} from '@/stores/Snack/ISnackDTD'
import {ChickenThickness} from '@/stores/Chicken/IChickenDTD'

import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import ghostGLB from '@/assets/ghost.glb'
import strawberryGLB from '@/assets/strawberry.glb'
import orangeGLB from '@/assets/orange.glb'
import cherryGLB from '@/assets/cherry.glb'
import appleGLB from '@/assets/apple.glb'
import eggGLB from '@/assets/yoshiegg.glb'

/**
 * for creating the objects in the map
 * objects are rendered by the GameMapRenderer.ts
 */
export const GameObjectRenderer = () => {
  const GROUNDSIZE = 1000
  const loader = new GLTFLoader()

  let ghostModel: THREE.Group | null = null 

  const snackModels = {
    [SnackType.STRAWBERRY]: strawberryGLB,
    [SnackType.ORANGE]: orangeGLB,
    [SnackType.CHERRY]: cherryGLB,
    [SnackType.APPLE]: appleGLB,
    [SnackType.EGG]: eggGLB,
    [SnackType.EMPTY]: null,
  };

  const snackModelCache: { [key in SnackType]?: THREE.Group } = {};

  async function createSnackOnFloor(
    xPosition: number,
    zPosition: number,
    sideLength: number,
    type: SnackType
  ): Promise<THREE.Object3D> {
    const modelPath = snackModels[type];
    if (!modelPath) {
      console.error(`No model found for SnackType: ${type}`);
      return new THREE.Mesh(
        new THREE.BoxGeometry(sideLength / 3, 1, sideLength / 3),
        new THREE.MeshStandardMaterial({ color: 'gray', opacity: 0.5, transparent: true })
      );
    }
  
    if (!snackModelCache[type]) {
      try {
        const gltf = await loader.loadAsync(modelPath);
        const snackModel = gltf.scene;
  
        // Calculate scaling
        const box = new THREE.Box3().setFromObject(snackModel);
        const size = new THREE.Vector3();
        box.getSize(size);
  
        const maxDimension = Math.max(size.x, size.y, size.z);
        const scale = (sideLength / 3) / maxDimension; // Standardised scaling based on `sideLength`.
        snackModel.scale.set(scale, scale, scale);
  
        const yOffset = box.min.y * scale; // Bottom edge of the model after scaling
        snackModel.position.y -= yOffset;
  
        snackModel.traverse((child: any) => {
          if (child.isMesh) {
            child.castShadow = true;
            child.receiveShadow = true;
          }
        });
  
        snackModelCache[type] = snackModel;
      } catch (error) {
        console.error(`Error loading snack model for ${type}:`, error);
        return new THREE.Mesh(
          new THREE.BoxGeometry(sideLength / 3, 1, sideLength / 3),
          new THREE.MeshStandardMaterial({ color: 'gray', opacity: 0.5, transparent: true })
        );
      }
    }
  
    const clonedSnack = snackModelCache[type]!.clone();
    clonedSnack.position.set(xPosition, 0, zPosition);
    return clonedSnack;
  }

  const createChickenOnFloor = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
    thickness: ChickenThickness,
  ) => {
    const color = 'black'
    const CHICKEN_WIDTH_AND_DEPTH = sideLength / 2
    const CHICKEN_HEIGHT = 15
    const scale =
      ChickenThickness[thickness as unknown as keyof typeof ChickenThickness]

    //@TODO add correct chicken-material-design
    const chickenMaterial = new THREE.MeshStandardMaterial({ color: color })
    const chickenGeometry = new THREE.BoxGeometry(
      CHICKEN_WIDTH_AND_DEPTH * scale,
      CHICKEN_HEIGHT,
      CHICKEN_WIDTH_AND_DEPTH * scale,
    )
    const chicken = new THREE.Mesh(chickenGeometry, chickenMaterial)
    chicken.castShadow = true
    chicken.receiveShadow = true

    chicken.position.set(xPosition, 0, zPosition)

    return chicken
  }

  async function createGhostOnFloor(
    xPosition: number,
    zPosition: number,
    yPosition: number,
    sideLength: number
  ): Promise<THREE.Object3D> {
    const placeholder = new THREE.Mesh(
      new THREE.SphereGeometry(0.5, 16, 16),
      new THREE.MeshStandardMaterial({ color: 'gray', opacity: 0.5, transparent: true })
    )
    placeholder.position.set(xPosition, yPosition, zPosition)
  
    // If the model is already loaded, clone it
    if (ghostModel) {
      const clonedGhost = ghostModel.clone()
      clonedGhost.position.set(xPosition, yPosition, zPosition)
      clonedGhost.scale.set(0.5, 0.5, 0.5)
      clonedGhost.traverse((child: any) => {
        if (child.isMesh) {
          child.castShadow = true
          child.receiveShadow = true
        }
      })
      return clonedGhost
    }
  
    // Load model asynchronously and replace placeholder
    return new Promise((resolve, reject) => {
      loader.load(
        ghostGLB,
        (gltf) => {
          ghostModel = gltf.scene
          ghostModel.position.set(xPosition, yPosition, zPosition)
          ghostModel.scale.set(0.5, 0.5, 0.5)
          ghostModel.traverse((child: any) => {
            if (child.isMesh) {
              child.castShadow = true
              child.receiveShadow = true
            }
          })
          resolve(ghostModel.clone())
        },
        undefined,
      )
    })
  }

  const createFloorSquare = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
  ) => {
    // TODO squareHeight is set for seeing it actually in game
    const squareHeight = 0.1
    const squareMaterial = new THREE.MeshStandardMaterial({ color: 'green' })
    const squareGeometry = new THREE.BoxGeometry(
      sideLength,
      squareHeight,
      sideLength,
    )
    const square = new THREE.Mesh(squareGeometry, squareMaterial)

    // Position the square
    square.position.set(xPosition, 0, zPosition)

    return square
  }

  const createGround = () => {
    // ground setup
    const groundTexture = new THREE.TextureLoader().load('./textures/ground_white_comic_2.jpg')
    groundTexture.wrapS = THREE.RepeatWrapping;
    groundTexture.wrapT = THREE.RepeatWrapping;
    groundTexture.repeat.set(1000, 1000);
    const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE)
    const groundMaterial = new THREE.MeshStandardMaterial({
                              map: groundTexture,
                              color: 0xffffff,
                              emissive: 0x000000,
                              roughness: 0.7,
                              metalness: 0.1,
                            })
    const ground = new THREE.Mesh(groundGeometry, groundMaterial)
    ground.castShadow = true
    ground.receiveShadow = true
    ground.rotateX(-Math.PI / 2)
    ground.position.set(0, 0, 0)

    return ground
  }

  /**
   * create a single wall with given x-, y-, z-position, height and sidelengthm
   */
  const createWall = (
    xPosition: number,
    yPosition: number,
    zPosition: number,
    height: number,
    sideLength: number,
  ) => {
    //frontend/public/textures/pngtree_cartoon_style_seamless_textured_surface_square.jpg
    const wallTexture = new THREE.TextureLoader().load('./textures/pngtree_cartoon_style_seamless_textured_surface_square.jpg')
    wallTexture.wrapS = THREE.RepeatWrapping;
    wallTexture.wrapT = THREE.RepeatWrapping;
    wallTexture.repeat.set(1, 1.87);

    const wallMaterial = new THREE.MeshStandardMaterial({
                                  map: wallTexture,
                                  color: 0xFFDE9D,
                                  emissive: 0x000000,
                                  roughness: 0.7,
                                  metalness: 0.1,
                                })
    const wallGeometry = new THREE.BoxGeometry(sideLength, height, sideLength)
    const wall = new THREE.Mesh(wallGeometry, wallMaterial)

    // Position the wall
    wall.position.set(xPosition, yPosition, zPosition)

    return wall
  }

  return {
    createSnackOnFloor,
    createChickenOnFloor,
    createFloorSquare,
    createGround,
    createWall,
    createGhostOnFloor
  }
}
