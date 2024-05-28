<script lang="ts">
    import { courseIdToUrlParam, instructorNameToUrlParam, spliceCourseCode } from "$lib/utils";
    import { writable } from "svelte/store";
    import { goto } from "$app/navigation";
    import type { SearchResults } from "$lib/model/SearchResults";
    import SearchBar from "./SearchBar.svelte";
    import { twMerge } from "tailwind-merge";
    import SearchResult from "./SearchResult.svelte";
    import ExploreButton from "./ExploreButton.svelte";
    import { v4 as uuidv4 } from "uuid";

    export let results: SearchResults;
    export let handleInputChange: (query: string) => void;
    export let onResultClick: () => void = () => {};

    const searchSelected = writable(false);
    const selectedIndex = writable(0);

    const handleKeyDown = (event: KeyboardEvent) => {
        const length = results.courses.length + results.instructors.length;

        if (event.key === 'ArrowUp') {
            event.preventDefault();
            selectedIndex.update((prevIndex) =>
                prevIndex > 0 ? prevIndex - 1 : length - 1
            );
        } else if (event.key === 'ArrowDown') {
            event.preventDefault();
            selectedIndex.update((prevIndex) =>
                prevIndex < length - 1 ? prevIndex + 1 : 0
            );
        }

        if ($selectedIndex > -1 && event.key === 'Enter') {
            goto(
                $selectedIndex < results.courses.length
                    ? `/course/${courseIdToUrlParam(results.courses[$selectedIndex]._id)}`
                    : `/instructor/${instructorNameToUrlParam(results.instructors[$selectedIndex - results.courses.length])}`
            );
            if (onResultClick) {
                onResultClick();
                event.currentTarget?.blur();
            }
        }
    };
</script>

<div class='relative'>
    <SearchBar
            bind:value={results.query}
            handleInputChange={handleInputChange}
            inputStyle={twMerge(
            'block w-full bg-gray-100 border border-gray-300 shadow-sm p-3 pl-10 text-sm text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500 lg:min-w-[570px] dark:border-gray-700 rounded-sm',
            $searchSelected ? 'border-b-1' : ''
        )}
            on:keydown={handleKeyDown}
            placeholder='Search for courses, subjects or instructors'
            searchSelected={searchSelected}
    />
    {#if $searchSelected}
        <div class="absolute top-full z-50 w-full overflow-hidden bg-white shadow-md dark:bg-neutral-800">
            {#each results.courses as result, index (uuidv4())}
                <SearchResult
                        {index}
                        query={results.query}
                        selectedIndex={$selectedIndex}
                        text={`${spliceCourseCode(result._id, ' ')} - ${result.title}`}
                        type="course"
                        url={`/course/${courseIdToUrlParam(result._id)}`}
                        on:click={onResultClick}
                />
            {/each}
            {#each results.instructors as result, index (uuidv4())}
                <SearchResult
                        index={results.courses.length + index}
                        query={results.query}
                        selectedIndex={$selectedIndex}
                        text={result}
                        type="instructor"
                        url={`/instructor/${instructorNameToUrlParam(result)}`}
                        on:click={onResultClick}
                />
            {/each}
            <ExploreButton />
        </div>
    {/if}
</div>