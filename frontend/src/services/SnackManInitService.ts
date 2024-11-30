import type { IPlayerDTD } from "@/stores/IPlayerDTD"
import type { ISnackDTD } from "@/stores/Snack/ISnackDTD"

export async function fetchSnackManFromBackend(): Promise<IPlayerDTD> {
    // rest endpoint from backend
    const response = await fetch('/api/snackman')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  }