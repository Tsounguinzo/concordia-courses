<script lang="ts">
    import FilterToggle from "$lib/components/common/filter/FilterToggle.svelte";
    import ResetButton from "$lib/components/common/filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/common/filter/Autocomplete.svelte";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {RefreshCw} from "lucide-svelte";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";

    export let allReviews: Writable<Review[]>;
    export let course: Course | null;
    export let showAllReviews: Writable<boolean>;
    export let selectedInstructor: Writable<string>;

    const sortTypes = [
        'Most Recent',
        'Least Recent',
        'Highest Rating',
        'Lowest Rating',
        'Hardest',
        'Easiest',
        'Most Liked',
        'Most Disliked',
    ] as const;
    type ReviewSortType = (typeof sortTypes)[number];
    const sortBy = writable<ReviewSortType>('Most Recent');

    if (sortBy) {
        allReviews.set(
            $allReviews
                .filter(
                    (review: Review) => $selectedInstructor === '' || review.instructor === $selectedInstructor.toLowerCase())
                .sort((a: Review, b: Review) => {
                    switch ($sortBy) {
                        case 'Most Recent':
                            return b.timestamp - a.timestamp
                        case 'Least Recent':
                            return a.timestamp - b.timestamp
                        case 'Highest Rating':
                            return b.rating - a.rating;
                        case 'Lowest Rating':
                            return a.rating - b.rating;
                        case 'Hardest':
                            return b.difficulty - a.difficulty;
                        case 'Easiest':
                            return a.difficulty - b.difficulty;
                        case 'Most Liked':
                            return b.likes - a.likes;
                        case 'Most Disliked':
                            return a.likes - b.likes;
                        default:
                            return b.timestamp - a.timestamp
                    }
                })
        );
        showAllReviews.set(false);
    }

    let rotate = false;

    function resetFilters() {
        sortBy.set('Most Recent')
        rotate = true;

        setTimeout(() => {
            rotate = false;
        }, 500);
    }
</script>

<div class='flex flex-col rounded-lg dark:bg-neutral-900 dark:text-gray-200'>
    <FilterToggle>
        <div class='py-2'/>
        <div class='relative'>
            <div class='p-1'>
                <div class='flex gap-x-2'>
                    <div class='w-2/5'>
                        <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                            Sort By
                        </h2>
                        <div class='relative z-10'>
                            <Autocomplete
                                    reset={rotate}
                                    options={sortTypes}
                                    storeValue={sortBy}
                            />
                        </div>
                    </div>
                    <!--{#if false}
                        <div class='w-3/5'>
                            <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                                Instructor
                            </h2>
                            <div class='relative z-10'>
                                <Autocomplete
                                        options={[]}
                                        storeValue={selectedInstructor}
                                />
                            </div>
                        </div>
                    {/if}-->
                </div>
            </div>
            <ResetButton className='absolute -top-4 right-2 ml-auto'>
                <button on:click={resetFilters} class:rotate-once={rotate}>
                    <RefreshCw class={'h-5 w-5 text-gray-500 dark:text-neutral-400'}/>
                </button>
            </ResetButton>
        </div>
    </FilterToggle>
</div>