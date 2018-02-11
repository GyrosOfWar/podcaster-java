import * as auth from "./auth";
import { notLoggedIn, parseError } from "../model/Error";

export default function fetchWithAuth<T>(url: string, init?: RequestInit): Promise<T> {
  const token = auth.getToken();
  if (token === null) {
    return Promise.reject(notLoggedIn());
  }
  const options = Object.assign({}, init, {
    headers: new Headers({
      "Authorization": "Bearer " + token.token,
      "Content-Type": "application/json"
    })
  });
  return fetch(url, options)
    .then(res => {
      if (res.ok) {
        return res.json();
      } else {
        return Promise.reject({ message: res.statusText });
      }
    })
    .catch(err => parseError(err));
}