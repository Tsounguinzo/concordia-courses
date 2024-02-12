import {writable} from "svelte/store";

export const highlightResultStyle = 'bg-red-50 border-l-red-500 border-l-4 dark:bg-red-100 dark:border-l-red-600 dark:bg-neutral-600';
export const hoveringResults = writable(false)