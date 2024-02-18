<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import FilterButton from "$lib/components/Filter/FilterButton.svelte";
    import ResetButton from "$lib/components/Filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/Filter/Autocomplete.svelte";
    import MultiSelect from "$lib/components/Explore/MultiSelect.svelte";
    import courseCodes from '$lib/data/courseCodes.json'
    import {sortByOptions, termsOptions} from "$lib/types";
    import {levelsOptions, termColorMap, termToIcon} from "$lib/utils";
    import type {Writable} from "svelte/store";
    import {RefreshCw} from "lucide-svelte";

    export let selectedSubjects: Writable<string[]>;
    export let selectedLevels: Writable<string[]>;
    export let selectedTerms: Writable<string[]>;
    export let sortBy: Writable<string>;
    export let variant: 'mobile' | 'desktop';


</script>

<div class={twMerge(
        variant === 'mobile' ? 'w-full' : 'w-[340px]',
        'relative flex h-fit flex-col flex-wrap rounded-lg bg-slate-50 px-8 py-6 dark:bg-neutral-800 dark:text-gray-200'
      )}>
    <ResetButton className='absolute right-4 top-4'>
        <button on:click={() => {
          selectedSubjects.set([]);
          selectedLevels.set([]);
          selectedTerms.set([]);
        }}>
            <RefreshCw class={'h-5 w-5 text-gray-500 dark:text-neutral-400'}/>
        </button>
    </ResetButton>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Sort By</h1>
    <div class='py-1'/>
    <div class='relative z-20'>
        <Autocomplete options={sortByOptions} storeValue={sortBy}/>
    </div>
    <div class='py-2.5'/>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Subject</h1>
    <div class='py-1'/>
    <div class='relative z-10'><MultiSelect options={courseCodes} values={selectedSubjects}/>
    </div>
    <div class='py-2.5'/>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Level</h1>
    <div class='py-1'/>
    <div class='flex flex-wrap gap-2 py-1'>
        {#each levelsOptions as level, i (i)}
            <FilterButton name={level}
                    isSelected={$selectedLevels.includes(level)}
                    selections={selectedLevels}
            />
        {/each}
    </div>
    <div class='py-2.5'/>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Term</h1>
    <div class='py-1'/>
    <div class='flex flex-wrap gap-2'>
        {#each termsOptions as term, i (i)}
            <FilterButton
                    icon={termToIcon(term, variant === 'mobile' ? 'small' : 'large')}
                    selectedClass={termColorMap[(term === 'Fall/Winter') ? term.replace('/','_').toLowerCase() : term.toLowerCase()]}
                    name={term}
                    isSelected={$selectedTerms.includes(term)}
                    selections={selectedTerms}
            />
        {/each}
    </div>
</div>