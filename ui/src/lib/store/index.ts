import {writable} from "svelte/store";
import type {SearchResults} from "$lib/model/SearchResults";

export const searchResults = writable<SearchResults>({
        query: '',
        courses: [],
        instructors: [],
    });

export const mobileMenuOpen = writable(false);