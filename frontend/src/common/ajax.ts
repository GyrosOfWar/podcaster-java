import * as auth from "./auth";
import Error from "../model/Error";

export function getWithAuth(url: string, success: (a: any) => void, error: (e: Error) => void) {
  const token = auth.getToken();
  if (token === null) {
    error(Error.notLoggedIn());
    return;
  }

  const xhr = new XMLHttpRequest();
  xhr.open("GET", url);
  xhr.setRequestHeader("Authorization", "Bearer " + token.token);
  xhr.onload = function () {
    if (xhr.status < 400) {
      success(JSON.parse(xhr.responseText));
    } else {
      error(Error.fromJSON(JSON.parse(xhr.responseText)));
    }
  };
  xhr.onerror = function () {
    error(Error.fromJSON(JSON.parse(xhr.responseText)));
  };
  xhr.send();
}

export function postWithAuth(url: string, body: string | null, success?: (a: any) => void, error?: (e: Error) => void) {
  const token = auth.getToken();
  if (token === null) {
    error && error(Error.notLoggedIn());
    return;
  }
  const xhr = new XMLHttpRequest();
  xhr.open("POST", url);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.setRequestHeader("Authorization", "Bearer " + token.token);
  xhr.onload = function () {
    if (xhr.status < 400) {
      success && success(JSON.parse(xhr.responseText));
    } else {
      error && error(Error.fromJSON(JSON.parse(xhr.responseText)));
    }
  };
  xhr.onerror = function () {
    error && error(Error.fromJSON(JSON.parse(xhr.responseText)));
  };
  if (body === null) {
    xhr.send();
  } else {
    xhr.send(body);
  }
}