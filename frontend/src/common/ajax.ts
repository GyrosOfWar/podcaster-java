import * as auth from "../auth/auth";

export function getWithAuth(url: string, success: (a: any) => void, error: (s: string) => void) {
    const token = auth.getToken();
    if (token === null) {
        error("Not logged in!");
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("GET", url);
    xhr.setRequestHeader("Authorization", "Bearer " + token.token);
    xhr.onload = function () {
        if (xhr.status < 400) {
            success(JSON.parse(xhr.responseText));
        } else {
            error("Error: " + xhr.responseText);
        }
    };
    xhr.onerror = function () {
        error("Error: " + xhr.responseText);
    };
    xhr.send();
}

export function postWithAuth(url: string, body: string, success: (a: any) => void, error: (s: string) => void) {
    const token = auth.getToken();
    if (token === null) {
        error("Not logged in!");
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", "Bearer " + token.token);
    xhr.onload = function () {
        if (xhr.status < 400) {
            success(JSON.parse(xhr.responseText));
        } else {
            error("Error: " + xhr.responseText);
        }
    }
    xhr.onerror = function () {
        error("Error: " + xhr.responseText);
    }

    xhr.send(body);
}