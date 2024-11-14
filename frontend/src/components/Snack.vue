<script setup lang="ts">
import { onMounted } from 'vue'
import * as THREE from 'three'

const props = withDefaults(defineProps<{
  xPos: number,
  yPos: number,
  zPos: number,
  color?: string,
  visible?: boolean,
}>(), {
  color: 'red',
  visible: true
});

const emit = defineEmits<{
  (event: 'meshCreated', mesh: THREE.Mesh): void
}>()

const createMesh = () => {
  const BOX_SIZE = 1
  const boxGeometry = new THREE.BoxGeometry(BOX_SIZE, BOX_SIZE, BOX_SIZE)
  const boxMaterial = new THREE.MeshMatcapMaterial({ color: props.color })
  const snack = new THREE.Mesh(boxGeometry, boxMaterial)
  snack.position.set(props.xPos, props.yPos, props.zPos)
  return snack
}

onMounted(() => {
  const mesh = createMesh()
  emit('meshCreated', mesh)
})
</script>
