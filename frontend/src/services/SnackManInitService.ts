import type { IPlayerInitDTD } from "@/stores/IPlayerInitDTD"

export async function fetchSnackManFromBackend(uuid:String): Promise<IPlayerInitDTD> {
    // rest endpoint from backend
    const response = await fetch(`/api/snackman/${uuid}`)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  }