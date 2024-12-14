export const highlightResultStyle = 'bg-primary-50 border-l-primary border-l-4 dark:bg-primary-100 dark:border-l-primary-600 dark:bg-neutral-600';

export const navigationItems = [
  { name: 'Home', href: '/' },
  { name: 'Explore', href: '/explore' },
  { name: 'Reviews', href: '/reviews-feed' },
  { name: 'About', href: '/about' },
  { name: 'Search', href: '/search' },
  { name: 'Grade Distribution', href: '/grades' },
];

export const backendUrl = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';