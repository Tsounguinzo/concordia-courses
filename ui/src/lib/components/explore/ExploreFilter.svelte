<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import FilterButton from "$lib/components/common/filter/FilterButton.svelte";
    import ResetButton from "$lib/components/common/filter/ResetButton.svelte";
    import Autocomplete from "$lib/components/common/filter/Autocomplete.svelte";
    import MultiSelect from "$lib/components/explore/MultiSelect.svelte";
    import courseCodes from '$lib/data/courseCodes.json'
    import tags from '$lib/data/tags.json'
    import departments from '$lib/data/faculty_department.json'
    import {getSortByOptions, termsOptions} from "$lib/types";
    import {levelsOptions, moreLevelsOptions, termColorMap, termToIcon} from "$lib/utils";
    import type {Writable} from "svelte/store";
    import {Eraser} from "lucide-svelte";
    import Seperator from "$lib/components/common/Seperator.svelte";

    export let selectedSubjects: Writable<string[]>;
    export let selectedLevels: Writable<string[]>;
    export let selectedTerms: Writable<string[]>;
    export let selectedDepartments: Writable<string[]>;
    export let selectedTags: Writable<string[]>;
    export let sortBy: Writable<string>;
    export let variant: 'mobile' | 'desktop';
    export let instructorsModeOn: Writable<boolean>;

    let reset = false;
    const departmentsOptions = departments.map(department => department.deparmentDescription);
    $: $instructorsModeOn, resetFilters();
    $: sortByOptions = getSortByOptions($instructorsModeOn);

    function resetFilters() {
        selectedSubjects.set([]);
        selectedLevels.set([]);
        selectedTerms.set([]);
        selectedDepartments.set([]);
        selectedTags.set([]);
        sortBy.set('')
        reset = true;

        setTimeout(() => {
            reset = false;
        }, 500);
    }
</script>

<div class={twMerge(
        variant === 'mobile' ? 'w-full' : 'w-[340px]',
        'relative flex h-fit flex-col flex-wrap rounded-lg bg-slate-50 px-8 py-6 dark:bg-neutral-800 dark:text-gray-200'
      )}>
    <ResetButton class='absolute right-4 top-4'>
        <button on:click={resetFilters} class:erase-effect={reset}>
            <Eraser class={'h-5 w-5 text-gray-500 dark:text-neutral-400'}/>
        </button>
    </ResetButton>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Sort By</h1>
    <div class='py-1'/>
    <div class='relative z-40'>
        <Autocomplete {reset} options={sortByOptions} storeValue={sortBy}/>
    </div>
    <div class='py-2.5'/>
    {#if $instructorsModeOn}
        <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Departments</h1>
        <div class='py-1'/>
        <div class='relative z-30'>
            <MultiSelect {reset} options={departmentsOptions}
                         values={selectedDepartments}/>
        </div>
        <div class='py-2.5'/>
        <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Tags</h1>
        <div class='py-1'/>
        <div class='relative z-20'>
            <MultiSelect {reset} options={tags} values={selectedTags}/>
        </div>
        <div class='py-2.5'/>
    {/if}
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Course Subject</h1>
    <div class='py-1'/>
    <div class='relative z-10'>
        <MultiSelect {reset} options={courseCodes} values={selectedSubjects}/>
    </div>
    <div class='py-2.5'/>
    <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Course Level</h1>
    <div class='py-1'/>
    <div class='flex flex-wrap gap-2 py-1'>
        {#each levelsOptions as level, i (i)}
            <FilterButton name={level}
                          isSelected={$selectedLevels.includes(level)}
                          selections={selectedLevels}
            />
        {/each}
    </div>
    <Seperator text="More"/>
    <div class='flex flex-wrap gap-2 py-1'>
        {#each moreLevelsOptions as level, i (i)}
            <FilterButton name={level}
                          isSelected={$selectedLevels.includes(level)}
                          selections={selectedLevels}
            />
        {/each}
    </div>
    {#if !$instructorsModeOn}
        <div class='py-2.5'/>
        <h1 class='text-sm font-semibold text-gray-600 dark:text-gray-400'>Term</h1>
        <div class='py-1'/>
        <div class='flex flex-wrap gap-2'>
            {#each termsOptions as term, i (i)}
                {@const termWithoutYear = term.split(" ")[0]}
                <FilterButton
                        icon={termToIcon(termWithoutYear, variant === 'mobile' ? 'small' : 'large')}
                        selectedClass={termColorMap[(termWithoutYear === 'Fall/Winter') ? termWithoutYear.replace('/','_').toLowerCase() : termWithoutYear.toLowerCase()]}
                        name={term}
                        isSelected={$selectedTerms.includes(term)}
                        selections={selectedTerms}
                />
            {/each}
        </div>
    {/if}
</div>