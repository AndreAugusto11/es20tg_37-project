export function getNameInitials(name: string): string {
  let nameArr, first, last: string;
  nameArr = name.split(' ');
  first = nameArr[0][0].toUpperCase();
  last = nameArr[nameArr.length - 1][0].toUpperCase();
  return first + last;
}
