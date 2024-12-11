<script lang="ts">
    import {fade, fly, slide, scale} from 'svelte/transition';
    import {elasticOut, cubicOut} from 'svelte/easing';
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
        const handleClick = event => {
            if (node && !node.contains(event.target) && !event.defaultPrevented) {
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

<div class="min-h-screen px-4 py-8 transition-colors duration-500">
    <div class="max-w-7xl mx-auto">
        <!-- Hero Section -->
        {#if searchState.isEmpty && !searchState.searchAttempted}
            <div
                    class="text-center space-y-6 mb-12"
                    in:fly="{{ y: 20, duration: 800, easing: elasticOut }}"
            >
                <h1
                        class="text-6xl font-bold tracking-tight bg-clip-text text-transparent
                    bg-gradient-to-r from-primary-600 to-primary-400"
                        style="filter: drop-shadow(0 0 20px rgba(99, 102, 241, 0.2))"
                >
                    Discover Reviews
                </h1>
                <p
                        class="text-xl text-gray-600 dark:text-gray-400 max-w-2xl mx-auto
                    transform transition-all duration-300"
                        style="opacity: {focusedInput ? 0.6 : 1};
                    transform: scale({focusedInput ? 0.95 : 1})"
                >
                    Find the wildest reviews, e.g "racist", "not too bad for a prof", ...
                </p>
            </div>
        {/if}

        <!-- Parent relative container -->
        <div class="relative max-w-2xl mx-auto mb-16">
            <!-- Search Form -->
            <form on:submit={handleSubmit} class="relative">
                <div class="relative flex items-center">
                    <!-- Search Icon on the left inside input container -->
                    <div class="absolute left-3 text-gray-400 pointer-events-none">
                        <Search class="w-5 h-5"/>
                    </div>

                    <!-- Input Field -->
                    <input
                            type="text"
                            bind:value={input}
                            on:focus={() => focusedInput = true}
                            on:blur={() => focusedInput = false}
                            placeholder="Search reviews..."
                            class="w-full pl-10 pr-24 py-2 text-lg rounded-full bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-50 placeholder-gray-500 border border-gray-300 dark:border-gray-700 focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 transition-all duration-300"
                    />

                    <!-- Clear button -->
                    {#if input}
                        <button
                                type="button"
                                class="absolute right-14 p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-all duration-200"
                                on:click={clearSearch}
                        >
                            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path d="M6 18L18 6M6 6l12 12" stroke-width="2" stroke-linecap="round"/>
                            </svg>
                        </button>
                    {/if}

                    <!-- Submit (Search) button -->
                    <button
                            type="submit"
                            disabled={!input.trim() || searchState.isLoading}
                            class="absolute right-2 h-9 px-3 bg-primary-500 hover:bg-primary-600 text-white rounded-full text-sm font-medium transition-colors duration-200 disabled:opacity-50 flex items-center justify-center"
                    >
                        {#if searchState.isLoading}
                            <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        {:else}
                            <ChevronUp class="w-4 h-4" />
                        {/if}
                    </button>
                </div>
            </form>

            <!-- Limit Selector -->
            <div
                    class="absolute right-12 mt-4 rounded-lg flex items-center bg-white/90 dark:bg-gray-800/90 text-gray-900 dark:text-gray-50"
                    use:clickOutside
            >
                <div class="relative">
                    <button
                            type="button"
                            class="px-3 py-2 text-sm font-medium text-gray-600 dark:text-gray-300
                            hover:text-primary-500 dark:hover:text-primary-400
                            transition-colors duration-200"
                            on:click={() => isLimitMenuOpen = !isLimitMenuOpen}
                    >
                        {resultLimit} results
                        <svg
                                class="inline-block w-4 h-4 ml-1 transition-transform duration-200"
                                class:transform={isLimitMenuOpen}
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
                                class="absolute right-0 mt-2 py-2 w-48 bg-white dark:bg-gray-800
                                rounded-lg shadow-xl border border-gray-200 dark:border-gray-700
                                z-50"
                                in:scale="{{ duration: 200, start: 0.95 }}"
                                out:scale="{{ duration: 150 }}"
                        >
                            <!-- Custom input -->
                            <div class="px-4 py-2 border-b border-gray-200 dark:border-gray-700">
                                <input
                                        type="number"
                                        min="1"
                                        max="100"
                                        bind:value={resultLimit}
                                        on:change={() => updateLimit(resultLimit)}
                                        class="w-full px-2 py-1 text-sm rounded border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-100"
                                        placeholder="1-100"
                                />
                            </div>

                            <!-- Preset options -->
                            {#each limitOptions as limit}
                                <button
                                        type="button"
                                        class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100
                                        dark:hover:bg-gray-700 transition-colors duration-150
                                        {resultLimit === limit ? 'text-primary-500 font-medium' : 'text-gray-700 dark:text-gray-300'}"
                                        on:click={() => updateLimit(limit)}
                                >
                                    {limit} results
                                </button>
                            {/each}
                        </div>
                    {/if}
                </div>
            </div>
        </div>

        <!-- Results Section -->
        {#if searchState.isLoading}
            <div class="flex justify-center items-center py-12" in:fade>
                <div class="relative">
                    <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-500 rounded-full animate-spin"/>
                    <div class="absolute inset-0 flex items-center justify-center">
                        <div class="w-4 h-4 bg-primary-500 rounded-full animate-pulse"/>
                    </div>
                </div>
            </div>
        {:else if reviews.length > 0}
            <div
                    class="text-center mb-8 text-gray-600 dark:text-gray-400"
                    in:slide="{{ duration: 400 }}"
            >
                Found <span class="font-bold text-primary-500">{$resultCount}</span> reviews
            </div>

            <div class="columns-1 md:columns-2 min-[1300px]:columns-3">
                {#each reviews as review, i (review._id)}
                    <button
                            class="w-full mb-2"
                            in:fly="{{ y: 50, duration: 600, delay: i * 100 }}"
                            on:click={() => handleClick(review)}
                    >
                        <ReviewComponent {review}/>
                    </button>
                {/each}
            </div>
        {:else if searchState.searchAttempted}
            <div
                    class="text-center py-12 text-gray-600 dark:text-gray-400"
                    in:scale="{{ duration: 400, easing: cubicOut }}"
            >
                <p class="text-lg mb-2">No reviews found for "{input}"</p>
                <p class="text-sm">Try different words or check your spelling</p>
            </div>
        {/if}
    </div>
</div>
