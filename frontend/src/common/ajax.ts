import * as auth from "./auth";
import Error, { notLoggedIn, parseError } from "../model/Error";

export function getWithAuth(url: string, success: (a: any) => void, error: (e: Error) => void) {
  const token = auth.getToken();
  if (token === null) {
    error(notLoggedIn());
    return;
  }

  const xhr = new XMLHttpRequest();
  xhr.open("GET", url);
  xhr.setRequestHeader("Authorization", "Bearer " + token.token);
  xhr.onload = function() {
    if (xhr.status < 400) {
      success(JSON.parse(xhr.responseText));
    } else {
      error(parseError(xhr.responseText));
    }
  };
  xhr.onerror = function() {
    error(parseError(xhr.responseText));
  };

  xhr.send();
}

export function postWithAuth(url: string, body?: string, success?: (a: any) => void, error?: (e: Error) => void) {
  const token = auth.getToken();
  if (token === null) {
    if (error) {
      error(notLoggedIn());
    }
    return;
  }
  const xhr = new XMLHttpRequest();
  xhr.open("POST", url);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.setRequestHeader("Authorization", "Bearer " + token.token);
  xhr.onload = function() {
    if (xhr.status < 400) {
      if (success) { success(JSON.parse(xhr.responseText)); }
    } else {
      if (error) {
        error(JSON.parse(xhr.responseText));
      }
    }
  };
  xhr.onerror = function() {
    if (error) {
      error(parseError(xhr.responseText));
    }
  };

  xhr.send(body);
}

export function deleteWithAuth(url: string, success?: (a: any) => void, error?: (e: Error) => void) {
  const token = auth.getToken();
  if (token === null) {
    if (error) {
      error(notLoggedIn());
    }
    return;
  }
  const xhr = new XMLHttpRequest();
  xhr.open("DELETE", url);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.setRequestHeader("Authorization", "Bearer " + token.token);
  xhr.onload = function() {
    if (xhr.status < 400) {
      if (success) { success(JSON.parse(xhr.responseText)); }
    } else {
      if (error) {
        error(parseError(xhr.responseText));
      }
    }
  };
  xhr.onerror = function() {
    if (error) {
      error(parseError(xhr.responseText));
    }
  };

  xhr.send();
}