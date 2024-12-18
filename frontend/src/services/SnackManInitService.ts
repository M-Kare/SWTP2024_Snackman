import type { IPlayerDTD } from "@/stores/IPlayerDTD"

export async function fetchSnackManFromBackend(uuid:String): Promise<IPlayerDTD> {
    // rest endpoint from backend
    const response = await fetch(`/api/snackman?uuid=${uuid}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
    }})
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  }
