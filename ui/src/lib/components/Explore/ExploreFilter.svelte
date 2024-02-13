<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import FilterButton from "$lib/components/Explore/FilterButton.svelte";
    import ResetButton from "$lib/components/Explore/ResetButton.svelte";
    import Autocomplete from "$lib/components/Explore/Autocomplete.svelte";
    import MultiSelect from "$lib/components/Explore/MultiSelect.svelte";
    import courseCodes from '$lib/data/courseCodes.json'
    import type {CourseTerm} from "$lib/types";
    import {levelsOptions, sortByOptions, termColorMap, termsOptions} from "$lib/types";
    import type {Writable} from "svelte/store";
    import {RefreshCw} from "lucide-svelte";

    const termToIcon = (term: CourseTerm) => {
        switch (term) {
            case 'Fall':
                return `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="brown" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-leaf"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></svg>`;
            case 'Winter':
                return `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-snowflake"><line x1="2" x2="22" y1="12" y2="12"/><line x1="12" x2="12" y1="2" y2="22"/><path d="m20 16-4-4 4-4"/><path d="m4 8 4 4-4 4"/><path d="m16 4-4 4-4-4"/><path d="m8 20 4-4 4 4"/></svg>`;
            case 'Summer':
                return `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="orange" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-sun"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`;
        }
    };

    export let selectedSubjects: Writable<string[]>;
    export let selectedLevels: Writable<string[]>;
    export let selectedTerms: Writable<string[]>;
    export let sortBy: Writable<string>;
    export let variant: 'mobile' | 'desktop';

    $: console.log('terms', $selectedTerms)
    $: console.log('levels', $selectedLevels)
    $: console.log('subjects', $selectedSubjects)

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
                    icon={termToIcon(term)}
                    selectedClass={termColorMap[term.toLowerCase()]}
                    name={term}
                    isSelected={$selectedTerms.includes(term)}
                    selections={selectedTerms}
            />
        {/each}
    </div>
</div>