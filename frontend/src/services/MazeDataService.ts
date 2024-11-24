/**
 * fetches maze data from backend, sends a GET request to '/api/maze'
 * and returns the parsed JSON data as an IMazeDTD object
 */
export async function fetchMazeDataFromBackend(): Promise<IMazeDTD> {
  // rest endpoint from backend
  const response = await fetch('/api/maze')
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }
  return await response.json()
}
