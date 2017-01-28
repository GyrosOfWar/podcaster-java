export default class Error {
    timestamp?: number;
    status?: number;
    error?: string;
    exception?: string;
    message: string;
    path?: string;

    static fromJSON(obj: any): Error {
        return new Error(
            obj.message,
            obj.timestamp,
            obj.status,
            obj.error,
            obj.exception,
            obj.path
        );
    }

    static notLoggedIn(): Error {
        return new Error("Not logged in");
    }

    constructor(message: string, timestamp?: number, status?: number,
                error?: string, exception?: string, path?: string) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
    }
}