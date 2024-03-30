<script lang="ts">
    import FilterToggle from "$lib/components/common/filter/FilterToggle.svelte";
    import ResetButton from "$lib/components/common/filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/common/filter/Autocomplete.svelte";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {Eraser} from "lucide-svelte";
    import {createEventDispatcher} from "svelte";

    export let showAllReviews: Writable<boolean>;
    let reset = false;

    const sortTypes = [
        'Most Recent',
        'Least Recent',
        'Best Experience',
        'Worst Experience',
        'Hardest',
        'Easiest',
        'Most Liked',
        'Most Disliked',
    ] as const;
    type ReviewSortType = (typeof sortTypes)[number];
    const sortBy = writable<ReviewSortType>('Most Recent');

    const dispatch = createEventDispatcher();

    $: if(sortBy){
        showAllReviews.set(false);
        dispatch('sortChanged', $sortBy);
    }

    function resetFilters() {
        sortBy.set('Most Recent')
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
                <div class='flex gap-x-2'>
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
                <button on:click={resetFilters} class:erase-effect={reset}>
                    <Eraser class='h-5 w-5 text-gray-500 dark:text-neutral-400'/>
                </button>
            </ResetButton>
        </div>
    </FilterToggle>
</div>