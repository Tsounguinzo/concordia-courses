<script lang="ts">
    import {fade, fly, scale} from 'svelte/transition';
    import {cubicOut} from 'svelte/easing';
    import {repo} from '$lib/repo';
    import type {Review} from "$lib/model/Review";
    import ReviewComponent from "$lib/components/review/Review.svelte";
    import {spring} from 'svelte/motion';
    import {onMount} from 'svelte';
    import {Search, ChevronUp} from "lucide-svelte";

    interface SearchState {
        isLoading: boolean;
        hasError: boolean;
        errorMessage: string;
        isEmpty: boolean;
        searchAttempted: boolean;
    }

    let input = '';
    let reviews: Review[] = [];
    let focusedInput = false;
    let isLimitMenuOpen = false;
    let resultLimit = 10; // Default value

    let searchState: SearchState = {
        isLoading: false,
        hasError: false,
        errorMessage: '',
        isEmpty: true,
        searchAttempted: false
    };
    const noop = () => {
    };
    const noopLikesUpdate = (_likes: number) => {
    };

    onMount(() => {
        const savedLimit = localStorage.getItem('searchResultLimit');
        if (savedLimit) {
            resultLimit = parseInt(savedLimit);
        }
    });

    const resultCount = spring(0, {
        stiffness: 0.1,
        damping: 0.8
    });

    $: resultCount.set(reviews.length);

    function updateLimit(newLimit: number) {
        resultLimit = Math.max(1, Math.min(100, newLimit));
        localStorage.setItem('searchResultLimit', resultLimit.toString());
        isLimitMenuOpen = false;

        // If we have an active search, refresh results
        if (input.trim() && searchState.searchAttempted) {
            handleSubmit(new Event('submit'));
        }
    }

    async function handleSubmit(event: Event) {
        event.preventDefault();
        if (input.trim()) {
            try {
                searchState.isLoading = true;
                searchState.hasError = false;
                searchState.searchAttempted = true;
                reviews = await repo.getReviewsByFTS(encodeURIComponent(input.trim()), resultLimit);
            } catch (error) {
                searchState.hasError = true;
                searchState.errorMessage = 'Failed to fetch reviews. Please try again.';
            } finally {
                searchState.isLoading = false;
                searchState.isEmpty = reviews.length === 0;
            }
        }
    }

    function clearSearch() {
        input = '';
        reviews = [];
        searchState.isEmpty = true;
        searchState.hasError = false;
        searchState.searchAttempted = false;
    }

    const getRoute = (review: Review): string => {
        switch (review.type) {
            case 'course':
                return `/course/${review.courseId}#review-${review._id}`;
            case 'instructor':
                return `/instructor/${review.instructorId}#review-${review._id}`;
            case 'school':
                return `/school/${review.schoolId}#review-${review._id}`;
            default:
                return '#';
        }
    };

    function handleClick(review: Review) {
        const route = getRoute(review);
        window.location.href = route;
    }

    const limitOptions = [10, 25, 50, 75, 100];

    function clickOutside(node: HTMLElement) {
        const handleClick = (event: MouseEvent) => {
            const target = event.target as Node | null;
            if (node && target && !node.contains(target) && !event.defaultPrevented) {
                isLimitMenuOpen = false;
            }
        };

        document.addEventListener('click', handleClick, true);

        return {
            destroy() {
                document.removeEventListener('click', handleClick, true);
            }
        };
    }
</script>

<div class="flex w-full justify-center py-8">
    <div class="mx-4 w-full max-w-6xl space-y-6">
        <section class="flex w-full flex-col rounded-md bg-slate-50 p-6 dark:bg-neutral-800">
            <h1 class="text-center text-3xl font-semibold text-gray-800 dark:text-gray-200">
                Search review content
            </h1>
            <p
                    class="mx-auto mt-2 max-w-2xl text-center text-gray-600 transition-all duration-300 dark:text-gray-400"
                    style="opacity: {focusedInput ? 0.75 : 1}"
            >
                Find reviews by keywords in their written content, then open the original course or instructor review.
            </p>

            <form on:submit={handleSubmit} class="mt-6 flex flex-col gap-3 lg:flex-row lg:items-start">
                <div class="relative flex-1">
                    <div class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-400">
                        <Search class="h-5 w-5"/>
                    </div>
                    <input
                            type="text"
                            bind:value={input}
                            on:focus={() => focusedInput = true}
                            on:blur={() => focusedInput = false}
                            placeholder="Search review content..."
                            class="w-full rounded-md border bg-gray-50 p-3 pl-10 pr-24 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white"
                    />
                    {#if input}
                        <button
                                type="button"
                                class="absolute right-12 top-1/2 -translate-y-1/2 p-1 text-gray-500 transition-colors duration-200 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                                on:click={clearSearch}
                        >
                            <svg class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path d="M6 18L18 6M6 6l12 12" stroke-width="2" stroke-linecap="round"/>
                            </svg>
                        </button>
                    {/if}
                    <button
                            type="submit"
                            disabled={!input.trim() || searchState.isLoading}
                            class="absolute right-1 top-1/2 flex h-10 w-10 -translate-y-1/2 items-center justify-center rounded-md bg-primary-600 text-white transition duration-300 hover:bg-primary-800 disabled:cursor-not-allowed disabled:bg-gray-500"
                    >
                        {#if searchState.isLoading}
                            <div class="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"/>
                        {:else}
                            <ChevronUp class="h-4 w-4"/>
                        {/if}
                    </button>
                </div>

                <div class="relative w-full lg:w-auto" use:clickOutside>
                    <button
                            type="button"
                            class="flex h-[46px] w-full items-center justify-between rounded-md border bg-gray-100 px-4 text-sm font-medium text-gray-700 dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 lg:w-[150px]"
                            on:click={() => isLimitMenuOpen = !isLimitMenuOpen}
                    >
                        {resultLimit} results
                        <svg
                                class="h-4 w-4 transition-transform duration-200"
                                class:rotate-180={isLimitMenuOpen}
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                        >
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                    </button>

                    {#if isLimitMenuOpen}
                        <div
                                class="absolute right-0 z-20 mt-2 w-full rounded-md border bg-white py-2 shadow-lg dark:border-neutral-600 dark:bg-neutral-800 lg:w-48"
                                in:scale="{{ duration: 200, start: 0.95 }}"
                                out:scale="{{ duration: 150 }}"
                        >
                            <div class="border-b border-gray-200 px-4 py-2 dark:border-neutral-600">
                                <input
                                        type="number"
                                        min="1"
                                        max="100"
                                        bind:value={resultLimit}
                                        on:change={() => updateLimit(resultLimit)}
                                        class="w-full rounded-md border bg-gray-50 px-2 py-1 text-sm outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200"
                                        placeholder="1-100"
                                />
                            </div>
                            {#each limitOptions as limit}
                                <button
                                        type="button"
                                        class="w-full px-4 py-2 text-left text-sm transition-colors duration-150 hover:bg-gray-100 dark:hover:bg-neutral-700 {resultLimit === limit ? 'font-medium text-primary-500' : 'text-gray-700 dark:text-gray-300'}"
                                        on:click={() => updateLimit(limit)}
                                >
                                    {limit} results
                                </button>
                            {/each}
                        </div>
                    {/if}
                </div>
            </form>
        </section>

        {#if searchState.hasError}
            <div class="rounded-md bg-red-100 p-3 text-center text-red-700 dark:bg-red-900 dark:text-red-100" in:fade>
                {searchState.errorMessage}
            </div>
        {/if}

        {#if searchState.isLoading}
            <div class="flex items-center justify-center py-10" in:fade>
                <div class="h-10 w-10 animate-spin rounded-full border-4 border-primary-200 border-t-primary-500"/>
            </div>
        {:else if reviews.length > 0}
            <div class="text-center text-gray-600 dark:text-gray-400" in:fade>
                Found <span class="font-bold text-primary-500">{$resultCount}</span> reviews
            </div>

            <div class="columns-1 md:columns-2 min-[1300px]:columns-3">
                {#each reviews as review, i (review._id)}
                    <button
                            class="mb-2 w-full break-inside-avoid text-left"
                            in:fly="{{ y: 20, duration: 350, delay: i * 25 }}"
                            on:click={() => handleClick(review)}
                    >
                        <ReviewComponent
                                {review}
                                canModify={false}
                                handleDelete={noop}
                                interactions={[]}
                                updateLikes={noopLikesUpdate}
                                useTaughtBy={(review.type ?? 'course') === 'course'}
                        />
                    </button>
                {/each}
            </div>
        {:else if searchState.searchAttempted}
            <div
                    class="rounded-md bg-slate-50 py-10 text-center text-gray-600 dark:bg-neutral-800 dark:text-gray-400"
                    in:scale="{{ duration: 300, easing: cubicOut }}"
            >
                <p class="mb-2 text-lg">No reviews found for "{input}"</p>
                <p class="text-sm">Try different words or check your spelling.</p>
            </div>
        {/if}
    </div>
</div>
