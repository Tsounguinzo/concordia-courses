<script lang="ts">
    import {createQuery} from '@tanstack/svelte-query';
    import {writable} from 'svelte/store';
    import {repo} from '$lib/repo';
    import {ArrowRight} from "lucide-svelte";
    import type {Review} from "$lib/model/Review";
    import {goto} from "$app/navigation";
    import {afterUpdate, onMount} from "svelte";

    let input = '';
    const isSearching = writable(false);
    const showResults = writable(false);

    const searchQuery = createQuery({
        queryKey: ['search', input],
        queryFn: async () => {
            if (input.trim() === '') return [];
            return repo.getReviewsByFTS(input)
        }
    });

    function handleSearch() {

    }

    function handleInput(event) {
        input = event.target.value;
    }

    $: isEmptySearch = !input && !$searchQuery.isLoading && !$searchQuery.data;


    let reviews: Review[] = [];
    let query: string = '';

    let showEmptyScreen: boolean = false;
    let isLoading: boolean = false;
    let inputRef: HTMLTextAreaElement;
    let isFirstRender: boolean = true;

    $: if (isFirstRender && query && query.trim().length > 0) {
        handleQuerySubmit(query);
        isFirstRender = false;
    }

    async function handleQuerySubmit(query: string) {
        input = query;
        isLoading = true;

        const queryResults = await repo.getReviewsByFTS(query);
        reviews = [...reviews, ...queryResults];
    }

    function handleSubmit(e: Event) {
        e.preventDefault();
        handleQuerySubmit(input);
    }

    function handleClear() {
        isLoading = false;
        reviews = [];
        input = '';
        goto('/search');
    }

    onMount(() => {
        inputRef.focus();
    });

    function handleHeightChange(height: number) {
        if (!inputRef) return;

        const initialHeight = 70;
        const initialBorder = 32;
        const multiple = (height - initialHeight) / 20;

        const newBorder = initialBorder - 4 * multiple;
        inputRef.style.borderRadius = `${Math.max(8, newBorder)}px`;
    }
</script>

<div class={`transition-all ease-in-out duration-300 flex flex-col items-center ${isEmptySearch ? 'justify-center' : 'justify-end'} min-h-[calc(100vh-19vh)]`}>
    {#if isEmptySearch}
        <h1 class="text-4xl font-bold mb-2 text-gray-800 dark:text-gray-300">Discover Smarter Search</h1>
        <p class="mb-8 text-lg font-semibold text-gray-500 dark:text-gray-400">Easily find reviews you like with our search tool.</p>
    {/if}
    <form on:submit|preventDefault={handleSearch} class="w-full max-w-2xl mx-auto">
        <div class="relative flex items-center">
            <input
                    type="text"
                    bind:value={input}
                    on:input={handleInput}
                    placeholder="Search reviews by content..."
                    class="w-full py-3 px-4 pr-12 rounded-full bg-gray-100 text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-gray-200"
            />
            <button
                    type="submit"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    aria-label="Submit search"
            >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
            </button>
        </div>
    </form>
</div>

{#if $searchQuery.data && $searchQuery.data?.length > 0}
    <div class="results">
        <ul>
            {#each $searchQuery.data as result}
                <li>{result}</li>
            {/each}
        </ul>
    </div>
{:else if $searchQuery.data && !$searchQuery.isLoading && $searchQuery.data?.length === 0}
    <div class="results">
        <p>No results found.</p>
    </div>
{/if}

{#if $searchQuery.isLoading}
    <div class="mt-2">
        <p>Searching...</p>
    </div>
{/if}