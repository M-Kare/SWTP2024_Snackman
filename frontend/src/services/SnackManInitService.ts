import type { IPlayerInitDTD } from "@/stores/Player/IPlayerInitDTD"

export async function fetchSnackManFromBackend(): Promise<IPlayerInitDTD> {
    // rest endpoint from backend
    const response = await fetch('/api/snackman')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  }
