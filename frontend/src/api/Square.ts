import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

export async  function getSquare(): Promise<ISquareDTD>{
  const url = 'api/square'

  const squareResponse = await fetch(url)

  if ( !squareResponse.ok)throw new Error(squareResponse.statusText)
  const squareData = await squareResponse.json()

  return squareData
}
