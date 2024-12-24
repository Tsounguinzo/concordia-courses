import {type Writable, writable} from "svelte/store";
import type {SearchResults} from "$lib/model/SearchResults";

export const searchResults = writable<SearchResults>({
        query: '',
        courses: [],
        instructors: [],
    });

export const mobileMenuOpen = writable(false);
export const visitorId: Writable<string|null> = writable(null);

export function writeOnceStore(initialValue: any) {
    let hasBeenSet = false;
    const { subscribe, set } = writable(initialValue);

    return {
        subscribe,
        set(value: any) {
            if (!hasBeenSet) {
                hasBeenSet = true;
                set(value);
            } else {
                throw new Error("This store can only be assigned once.");
            }
        }
    };
}