import type {ISnackDTD} from "@/stores/Snack/ISnackDTD";

export async function getSnacks(): Promise<ISnackDTD[]>{
  const url = '/api/snack'
  const snackResponse = await fetch(url)

  if(!snackResponse.ok) throw new Error(snackResponse.statusText)
  const snackData = await snackResponse.json()

  return snackData
}
