import * as moment from "moment";

export function capitalize(str: string) {
  return str.charAt(0).toUpperCase() + str.substr(1);
}

export function formatDuration(obj: moment.Duration | string): string {
  function pad(n: number): string {
    return n < 10 ? "0" + n : n.toString();
  }
  let duration;
  if (typeof duration === "string") {
    duration = moment.duration(obj);
  } else {
    duration = obj as moment.Duration;
  }

  const hr = duration.hours();
  const min = duration.minutes();
  const secs = duration.seconds();
  return `${pad(hr)}:${pad(min)}:${pad(secs)}`;
}

export function getFormattedElapsedTime(lastPosition: moment.Duration | string, duration: moment.Duration | string) {
  return `${formatDuration(lastPosition)} / ${formatDuration(duration)}`;
}

export function getThumbnailUrl(id: number, type: string, size: number): string {
  return "/api/images/" + type + "/" + id + "?size=" + size;
}