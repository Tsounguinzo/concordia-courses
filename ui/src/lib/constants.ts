export const highlightResultStyle = 'bg-blue-50 border-l-blue-500 border-l-4 dark:bg-blue-100 dark:border-l-blue-600 dark:bg-neutral-600';

export const navigationItems = [
  { name: 'Home', href: '/' },
  { name: 'Explore', href: '/explore' },
  { name: 'Reviews', href: '/reviews-feed' },
  { name: 'About', href: '/about' },
];

export const backendUrl = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';