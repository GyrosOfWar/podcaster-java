export class Token {
    token: string;

    constructor(token: string) {
        this.token = token;
    }

    static fromJSON(json: string) {
        return new Token(JSON.parse(json).token);
    }
}

export function login(username: string, password: string, success: (t: Token) => void, error: (s: String) => void): void {
    const data = {username: username, password: password};
    const xhr = new XMLHttpRequest();
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.open("POST", "/auth/token");
    xhr.onload = function () {
        if (xhr.status == 200) {
            const tokenObj = Token.fromJSON(xhr.responseText);
            localStorage.setItem("token", tokenObj.token);
            success(tokenObj);
        } else {
            error(xhr.responseText);
        }
    };
    xhr.onerror = function () {
        error(xhr.responseText);
    }
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

export function logout(callback: Function | undefined): void {
    localStorage.removeItem("token");
    if (callback) {
        callback();
    }
}