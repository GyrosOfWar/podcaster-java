import HistoryEntry from "./HistoryEntry";

export default class User {
  id: number;
  name: string;
  email: string;
  history: Array<HistoryEntry>;

  static fromJSON(obj: any): User {
    return new User(
      obj.id,
      obj.name,
      obj.email,
      obj.history ? obj.history.map((h: any) => HistoryEntry.fromJSON(h)) : []
    );
  }

  constructor(id: number, name: string, email: string, history: Array<HistoryEntry>) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.history = history;
  }
}