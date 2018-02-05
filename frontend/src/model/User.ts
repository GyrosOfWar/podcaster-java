import HistoryEntry from "./HistoryEntry";

export default interface User {
  id: number;
  name: string;
  email: string;
  history: Array<HistoryEntry>;
}