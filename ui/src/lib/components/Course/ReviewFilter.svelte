<script lang="ts">
    import FilterToggle from "$lib/components/Filter/FilterToggle.svelte";
    import ResetButton from "$lib/components/Filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/Filter/Autocomplete.svelte";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {RefreshCw} from "lucide-svelte";
    import type {Review} from "$lib/model/Review";
    import _ from "lodash";
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
    const uniqueInstructors = _.uniq(course?.instructors.map((ins) => ins.name));

    if (sortBy) {
        allReviews.set(
            $allReviews
                .filter(
                    (review: Review) =>
                        $selectedInstructor === '' ||
                        review.instructors
                            .map((ins) => ins.toLowerCase())
                            .includes($selectedInstructor.toLowerCase())
                )
                .sort((a: Review, b: Review) => {
                    switch ($sortBy) {
                        case 'Most Recent':
                            return (
                                parseInt(b.timestamp.$date.$numberLong, 10) -
                                parseInt(a.timestamp.$date.$numberLong, 10)
                            );
                        case 'Least Recent':
                            return (
                                parseInt(a.timestamp.$date.$numberLong, 10) -
                                parseInt(b.timestamp.$date.$numberLong, 10)
                            );
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
                            return (
                                parseInt(b.timestamp.$date.$numberLong, 10) -
                                parseInt(a.timestamp.$date.$numberLong, 10)
                            );
                    }
                })
        );
        showAllReviews.set(false);
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
                                    options={sortTypes}
                                    storeValue={sortBy}
                            />
                        </div>
                    </div>
                    {#if uniqueInstructors.length > 0}
                        <div class='w-3/5'>
                            <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                                Instructor
                            </h2>
                            <div class='relative z-10'>
                                <Autocomplete
                                        options={uniqueInstructors}
                                        storeValue={selectedInstructor}
                                />
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
            <ResetButton className='absolute -top-4 right-2 ml-auto'>
                <button on:click={() => {sortBy.set('Most Recent');}}>
                    <RefreshCw class={'h-5 w-5 text-gray-500 dark:text-neutral-400'}/>
                </button>
            </ResetButton>
        </div>
    </FilterToggle>
</div>