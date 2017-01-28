export default class Error {
    timestamp?: number;
    status?: number;
    error?: string;
    exception?: string;
    message: string;
    path?: string;

    static notLoggedIn(): Error {
        const e = new Error();
        e.message = "Not logged in!";
        return e;
    }
}