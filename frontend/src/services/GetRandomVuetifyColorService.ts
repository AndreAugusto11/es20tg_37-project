const colors = [
  'red',
  'pink',
  'purple',
  'deep-purple',
  'indigo',
  'blue',
  'light-blue',
  'cyan',
  'teal',
  'green',
  'light-green',
  'lime',
  'yellow darken-2',
  'amber',
  'orange',
  'deep-orange',
  'brown',
  'blue-grey'
];

export function getRandomVuetifyColor(name: string): string {
  return colors[name.length % colors.length];
}
