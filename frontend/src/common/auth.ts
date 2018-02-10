import Error, { parseError } from "../model/Error";

export class Token {
  token: string;

  static fromJSON(json: string) {
    return new Token(JSON.parse(json).token);
  }

  constructor(token: string) {
    this.token = token;
  }
}

export function login(username: string, password: string,
                      success: (t: Token) => void,
                      error: (e: Error) => void): void {
  const data = { username: username, password: password };
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/auth/token");
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function() {
    if (xhr.status === 200) {
      const tokenObj = Token.fromJSON(xhr.responseText);
      localStorage.setItem("token", tokenObj.token);
      success(tokenObj);
    } else {
      error(parseError(xhr.responseText));
    }
  };
  xhr.onerror = function() {
    error(parseError(xhr.responseText));
  };
  xhr.send(JSON.stringify(data));
}

export function isLoggedIn(): boolean {
  return localStorage.getItem("token") !== null;
}

export function getToken(): Token | null {
  const token = localStorage.getItem("token");
  if (token === null) {
    return null;
  } else {
    return new Token(token);
  }
}

export function logout(): void {
  localStorage.removeItem("token");
}