import type { ISnackDTD } from "@/stores/Snack/ISnackDTD";

export async function removeSnack(snack: ISnackDTD): Promise<boolean> {
  const url = 'api/snack';

  try {
    const response = await fetch(url, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(snack),
    });
    if (!response.ok) {
      console.error('Error while removing snack.', response.statusText);
      return false;
    }
    return true;
  } catch (error) {
    console.error('Network error.', error);
    return false;
  }
}
