export default interface Error {
  timestamp?: number;
  status?: number;
  error?: string;
  exception?: string;
  message: string;
  path?: string;
}

export function notLoggedIn(): Error {
  return { message: "Not logged in" };
}