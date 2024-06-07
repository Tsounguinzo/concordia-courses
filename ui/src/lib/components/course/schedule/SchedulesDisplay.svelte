<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ScheduleRow from "./ScheduleRow.svelte";
    import {ArrowDown} from "lucide-svelte";
    import {sortTerms} from "$lib/utils";
    import type {Block, Schedule} from "$lib/model/Schedule";

    export let course: Course;

    $: schedules = course?.schedules ?? [];

    function getSessionDisplayName(term: string, session: string): string {
        if (!term.startsWith('Summer')) return '';
        switch (session) {
            case '6H1':
                return 'Summer I';
            case '6H2':
                return 'Summer II';
            case '13W':
                return 'Full Summer';
            default:
                return '';
        }
    }

    // Group schedules by term and session with type safety
    type GroupedSchedules = Record<string, Record<string, Block[]>>;

    $: groupedSchedules = schedules.reduce((acc: GroupedSchedules, schedule: Schedule) => {
        const term = schedule.term;

        if (!acc[term]) {
            acc[term] = {};
        }

        schedule.blocks.forEach( (block: Block) => {
            const sessionDisplayName = getSessionDisplayName(schedule.term, block.session);

            if (!sessionDisplayName) {
                // Handle regular terms without session keys
                if (!acc[term].Regular) {
                    acc[term].Regular = [];
                }
                acc[term].Regular.push(block);
            } else {
                // Handle summer sessions with session keys
                if (!acc[term][sessionDisplayName]) {
                    acc[term][sessionDisplayName] = [];
                }
                acc[term][sessionDisplayName].push(block);
            }
        });

        return acc;
    }, {});

    $: offeredTerms = sortTerms(Object.keys(groupedSchedules));
    $: selectedTerm = offeredTerms?.[0];
    $: selectedSessions = groupedSchedules[selectedTerm] ?? {};
    let showAll: Record<string, boolean> = {};

    function toggleShowAll(sessionKey: string) {
        showAll = { ...showAll, [sessionKey]: !showAll[sessionKey] };
    }

</script>
{#if schedules?.length && (selectedTerm || Object.keys(selectedSessions).length)}
    <div class={twMerge('flex flex-col text-gray-800 shadow-sm lg:border-t-0',$$props.class)}>
        <div class='flex'>
            {#each offeredTerms as term, i (i)}
                <button
                        class={twMerge(
                    `flex-1 cursor-pointer p-2 text-center font-medium transition duration-300 ease-in-out dark:text-gray-200 border-b sm:text-base text-sm dark:border-b-neutral-600`,
                    term === selectedTerm
                        ? 'bg-primary'
                        : 'bg-slate-200 dark:bg-neutral-600 hover:bg-slate-100 dark:hover:bg-neutral-700',
                    i === 0 ? 'rounded-tl-lg' : '',
                    i === offeredTerms.length - 1 ? 'rounded-tr-lg' : ''
                )}
                        on:click={() => {
                    selectedTerm = term;
                    showAll = {};
                }}
                >
                    {term}
                </button>
            {/each}
        </div>
        <div class='flex flex-col rounded-b-lg bg-slate-50 dark:bg-neutral-800 dark:text-gray-200'>
            <table>
                {#each Object.keys(selectedSessions) as sessionKey}
                    {#if sessionKey !== 'Regular'}
                        <tr>
                            <td colspan="100%" class="bg-primary text-white text-center font-bold p-2">{sessionKey}</td>
                        </tr>
                    {/if}
                    {#if selectedSessions[sessionKey]?.length <= 5 || showAll[sessionKey]}
                        {#each selectedSessions[sessionKey] as block}
                            <ScheduleRow {block}/>
                        {/each}
                    {:else}
                        {#each selectedSessions[sessionKey]?.slice(0, 5) as block}
                            <ScheduleRow {block}/>
                        {/each}
                    {/if}
                    {#if selectedSessions[sessionKey]?.length > 5}
                        <tr>
                            <td colspan="100%" align="center">
                                <button
                                        class='flex flex-row items-center justify-center py-2 text-center font-medium transition duration-300 ease-in-out hover:cursor-pointer dark:text-gray-200'
                                        on:click={() => toggleShowAll(sessionKey)}
                                >
                                    {showAll[sessionKey] ? 'Show less' : 'Show all'}
                                    <ArrowDown
                                            class={`${showAll[sessionKey] ? 'rotate-180' : ''} mx-2 h-5 w-5 text-gray-900 dark:text-gray-300`}/>
                                </button>
                            </td>
                        </tr>
                    {/if}
                {/each}
            </table>
        </div>
    </div>
{/if}