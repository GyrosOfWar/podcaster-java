export default interface Page<T> {
  content: Array<T>;
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: string;
  numberOfElements: number;
  first: boolean;
}