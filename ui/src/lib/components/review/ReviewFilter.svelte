<script lang="ts">
    import FilterToggle from "$lib/components/common/filter/FilterToggle.svelte";
    import ResetButton from "$lib/components/common/filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/common/filter/Autocomplete.svelte";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {Eraser} from "lucide-svelte";
    import {createEventDispatcher} from "svelte";
    import type {Course} from "$lib/model/Course";
    import type {Instructor} from "$lib/model/Instructor";

    export let showAllReviews: Writable<boolean>;
    export let type: 'course' | 'instructor' = 'course';
    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    let reset = false;

    const sortTypes = [
        'Most Recent',
        'Least Recent',
        `Best ${type === 'course' ? 'Experience' : 'Rating'}`,
        `Worst ${type === 'course' ? 'Experience' : 'Rating'}`,
        'Hardest',
        'Easiest',
        'Most Liked',
        'Most Disliked',
    ] as const;
    type ReviewSortType = (typeof sortTypes)[number];
    const sortBy = writable<ReviewSortType>('Most Recent');
    const selectedInstructor = writable<string>('');
    const selectedCourse = writable<string>('');

    const dispatch = createEventDispatcher();

    $: if (sortBy || selectedInstructor || selectedCourse) {
        showAllReviews.set(false);
        dispatch('sortChanged', [$sortBy, $selectedInstructor, $selectedCourse]);
    }

    function resetFilters() {
        reset = true;

        setTimeout(() => {
            reset = false;
        }, 500);
    }
</script>

<div class='flex flex-col rounded-lg dark:bg-neutral-900 dark:text-gray-200'>
    <FilterToggle>
        <div class='py-2'/>
        <div class='relative'>
            <div class='p-1'>
                <div class='flex gap-x-4'>
                    <div class='w-2/5'>
                        <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                            Sort By
                        </h2>
                        <div class='relative z-10'>
                            <Autocomplete
                                    {reset}
                                    options={sortTypes}
                                    storeValue={sortBy}
                            />
                        </div>
                    </div>
                    {#if type === 'course'}
                        {#if course && course?.instructors && course?.instructors.length}
                            <div class='w-2/5'>
                                <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                                    Instructor
                                </h2>
                                <div class='relative z-10'>
                                    <Autocomplete
                                            {reset}
                                            options={['', ...course?.instructors]}
                                            storeValue={selectedInstructor}
                                    />
                                </div>
                            </div>
                        {/if}
                    {:else}
                        {#if instructor && instructor?.courses && instructor?.courses.length}
                            <div class='w-2/5'>
                                <h2 class='mb-2 text-sm font-medium text-gray-600 dark:text-gray-400'>
                                    Course
                                </h2>
                                <div class='relative z-10'>
                                    <Autocomplete
                                            {reset}
                                            options={['', ...instructor?.courses.map(c => c.subject + ' ' + c.catalog)]}
                                            storeValue={selectedCourse}
                                    />
                                </div>
                            </div>
                        {/if}
                    {/if}
                </div>
            </div>
            <ResetButton class='absolute -top-4 right-2 ml-auto'>
                <button on:click={resetFilters} class:erase-effect={reset}>
                    <Eraser class='h-5 w-5 text-gray-500 dark:text-neutral-400'/>
                </button>
            </ResetButton>
        </div>
    </FilterToggle>
</div>