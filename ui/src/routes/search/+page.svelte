<script lang="ts">
    import {writable} from 'svelte/store';
    import {repo} from '$lib/repo';
    import {goto} from "$app/navigation";
    import ReviewComponent from "$lib/components/review/Review.svelte";
    import type {Review} from "$lib/model/Review";


    let input = '';
    const isSearching = writable(false);
    const showResults = writable(false);

    function handleInput(event) {
        input = event.target.value;
    }

    $: isEmptySearch = true


    let reviews: Review[] = [];
    let query: string = '';

    let showEmptyScreen: boolean = false;
    let isLoading: boolean = false;
    let isFirstRender: boolean = true;

    $: if (isFirstRender && query && query.trim().length > 0) {
        isFirstRender = false;
    }

    async function handleSubmit() {
        isLoading = true;

        const queryResults = await repo.getReviewsByFTS(input);
        reviews = [...reviews, ...queryResults];

        input = '';
    }

    function handleClear() {
        isLoading = false;
        reviews = [];
        input = '';
        goto('/search');
    }

</script>

<div>
    {#if reviews}
        <div class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {#each reviews as review (review._id)}
                <ReviewComponent {review} />
            {/each}
        </div>
    {/if}
</div>
<div class={`transition-all ease-in-out duration-300 flex flex-col items-center ${isEmptySearch ? 'justify-center' : 'justify-end'} min-h-[calc(100vh-19vh)]`}>
    {#if isEmptySearch}
        <h1 class="text-4xl font-bold mb-2 text-gray-800 dark:text-gray-300">Discover Smarter Search</h1>
        <p class="mb-8 text-lg font-semibold text-gray-500 dark:text-gray-400">Easily find reviews you like with our search tool.</p>
    {/if}
    <form on:submit={handleSubmit} class="w-full max-w-2xl mx-auto">
        <div class="relative flex items-center">
            <input
                    autofocus
                    type="text"
                    bind:value={input}
                    on:input={handleInput}
                    placeholder="Search reviews by content..."
                    class="w-full py-3 px-4 pr-12 rounded-full bg-neutral-200 dark:bg-neutral-800 text-gray-900 dark:text-gray-50 placeholder-gray-500 focus:outline-none focus:ring-2 dark:focus:ring-gray-200 focus:ring-gray-700"
            />
            <button
                    type="submit"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-60 dark:hover:text-gray-200"
                    aria-label="Submit search"
            >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
            </button>
        </div>
    </form>
</div>