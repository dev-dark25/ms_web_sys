export function dateFormat(date: Date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const hour = hours >= 10 ? hours : '0' + hours;
  const minutes = date.getMinutes();
  const minute = minutes >= 10 ? minutes : '0' + minutes;
  const seconds = date.getSeconds();
  const second = seconds >= 10 ? seconds : '0' + seconds;
  const week = date.getDay();
  const weekArr = [
    '星期日',
    '星期一',
    '星期二',
    '星期三',
    '星期四',
    '星期五',
    '星期六'
  ];
  return `${year}-${month}-${day} ${hour}:${minute}:${second} ${weekArr[week]}`;
}
