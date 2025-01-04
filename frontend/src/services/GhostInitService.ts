import type { IPlayerInitDTD } from "@/stores/Player/IPlayerInitDTD"

export async function fetchGhostFromBackend(ghostId: number): Promise<IPlayerInitDTD>{
  const response = await fetch('/api/ghost?id=0' )
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }
  return await response.json()
}
