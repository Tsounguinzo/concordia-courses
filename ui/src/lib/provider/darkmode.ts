import {writable} from 'svelte/store';

export const darkModeOn = writable(true);

export const setTheme = (dark: boolean) => {
    darkModeOn.set(dark);
    localStorage.setItem('theme', dark ? 'dark' : 'light');
};

export const getTheme = () => {
    return localStorage.getItem('theme') ?? 'light';
};


