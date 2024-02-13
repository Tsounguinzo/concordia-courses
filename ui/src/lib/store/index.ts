import {writable} from "svelte/store";
import type {SearchResults} from "$lib/model/SearchResults";

export const searchSelected = writable(false);
export const searchResults = writable<SearchResults>({
        query: '',
        courses: [],
        instructors: [],
    });

export const mobileMenuOpen = writable(false);