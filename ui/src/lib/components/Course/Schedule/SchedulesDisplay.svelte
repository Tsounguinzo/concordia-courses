<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ScheduleRow from "$lib/components/Course/Schedule/ScheduleRow.svelte";
    import {ArrowDown} from "lucide-svelte";
    import {sortTerms} from "$lib/utils";
    import _ from "lodash";
    import {writable} from "svelte/store";
    import type {Writable} from "svelte/store";
    import type {Block} from "$lib/model/Schedule";

    export let course: Course;
    export let className: string = '';

    let schedules = course.schedules;

    const offeredTerms = sortTerms(
        _.uniq(schedules.map((schedule) => schedule?.term))
    );

    const selectedTerm = writable(offeredTerms?.at(0));
    const showAll = writable(false);
    const blocks: Writable<Block[] | undefined> = writable($selectedTerm ? schedules.filter(term => term?.term === $selectedTerm)[0]?.blocks : undefined);

    $: if (course) {
        selectedTerm.set(offeredTerms?.at(0))
    }

    $: if (course || $selectedTerm) {
        blocks.set($selectedTerm ? schedules.filter(term => term?.term === $selectedTerm)[0]?.blocks : undefined)
    }

</script>
{#if schedules.length && (!$selectedTerm || $blocks !== undefined)}
    <div class={twMerge('flex flex-col text-gray-800 shadow-sm lg:border-t-0',className)}>
        <div class='flex'>
            {#each offeredTerms as term, i (i)}
                <button
                        class={twMerge(
                    `flex-1 cursor-pointer p-2 text-center font-medium transition duration-300 ease-in-out dark:text-gray-200 border-b sm:text-base text-sm dark:border-b-neutral-600`,
                    term === $selectedTerm
                        ? 'bg-slate-50 dark:bg-neutral-800'
                        : 'bg-slate-200 dark:bg-neutral-600 hover:bg-slate-100 dark:hover:bg-neutral-700',
                    i === 0 ? 'rounded-tl-lg' : '',
                    i === offeredTerms.length - 1 ? 'rounded-tr-lg' : ''
                )}
                        on:click={() => {
                    selectedTerm.set(term);
                    showAll.set(false);
                }}
                >
                    {term}
                </button>
            {/each}
        </div>
        <div class='flex flex-col rounded-b-lg bg-slate-50 dark:bg-neutral-800 dark:text-gray-200'>
            <table>
                {#if $blocks?.length <= 5 || $showAll}
                    {#each $blocks as block}
                        <ScheduleRow {block}/>
                    {/each}
                {:else }
                    {#each $blocks.slice(0, 5) as block}
                        <ScheduleRow {block}/>
                    {/each}
                {/if}
            </table>
            {#if $blocks?.length > 5}
                <div class='flex flex-row justify-center'>
                    <button
                            class='flex flex-row items-center justify-center py-2 text-center font-medium transition duration-300 ease-in-out hover:cursor-pointer dark:text-gray-200'
                            on:click={() => showAll.set(!$showAll)}
                    >
                        {$showAll ? 'Show less' : 'Show all'}
                        <ArrowDown
                                class={`${$showAll ? 'rotate-180' : ''} mx-2 h-5 w-5 text-gray-900 dark:text-gray-300`}/>
                    </button>
                </div>
            {/if}
        </div>
    </div>
{/if}