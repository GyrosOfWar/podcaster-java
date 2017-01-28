import HistoryEntry from "./HistoryEntry";

export default class User {
    id: number;
    name: string;
    email: string;
    history: Array<HistoryEntry>;
}