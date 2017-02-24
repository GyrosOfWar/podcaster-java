import * as moment from "moment";

export function capitalize(str: string) {
  return str.charAt(0).toUpperCase() + str.substr(1);
}

export function formatDuration(duration: moment.Duration): string {
  function pad(n: number): string {
    return n < 10 ? "0" + n : n.toString();
  }

  const hr = duration.hours();
  const min = duration.minutes();
  const secs = duration.seconds();
  return `${pad(hr)}:${pad(min)}:${pad(secs)}`;
}
