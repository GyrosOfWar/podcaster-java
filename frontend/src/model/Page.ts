export default class Page<T> {
  content: Array<T>;
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: string;
  numberOfElements: number;
  first: boolean;

  static fromJSON<T>(obj: any, transform: (obj: any) => T): Page<T> {
    return new Page<T>(
      obj.content.map(transform),
      obj.totalPages,
      obj.totalElements,
      obj.last,
      obj.size,
      obj.number,
      obj.sort,
      obj.numberOfElements,
      obj.first
    );
  }

  withNewContent(data: Array<T>): Page<T> {
    const newData: Array<T> = [... data, ...this.content].slice(0, this.size);
    return new Page<T>(
      newData,
      this.totalPages,
      this.totalElements,
      this.last,
      this.size,
      this.number,
      this.sort,
      this.numberOfElements,
      this.first
    );
  }

  constructor(content: Array<T>, totalPages: number, totalElements: number, last: boolean, size: number,
              _number: number, sort: string, numberOfElements: number, first: boolean) {
    this.content = content;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.last = last;
    this.size = size;
    this.number = _number;
    this.sort = sort;
    this.numberOfElements = numberOfElements;
    this.first = first;
  }
}