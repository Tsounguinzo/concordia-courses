<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {filterCurrentInstructors, getCurrentTerms, uniqueTermInstructors} from "$lib/utils";
    import {X} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import Highlight from "$lib/components/Highlight.svelte";
    import {termColorMap} from "$lib/types";
    import Tooltip from "$lib/components/Tooltip.svelte";


    export let course: Course;
    export let variant: 'large' | 'small';
    export let query: string;

    const variantToSize = (variant: 'small' | 'large') => {
        return variant === 'small' ? 20 : 18;
    };

    const termToIcon = (term: string, variant: 'small' | 'large') => {
        type IconMap = { [key: string]: string };
        const size = variantToSize(variant);

        const icons: IconMap = {
            fall: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="brown" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-leaf"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></svg>`,
            winter: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-snowflake"><line x1="2" x2="22" y1="12" y2="12"/><line x1="12" x2="12" y1="2" y2="22"/><path d="m20 16-4-4 4-4"/><path d="m4 8 4 4-4 4"/><path d="m16 4-4 4-4-4"/><path d="m8 20 4-4 4 4"/></svg>`,
            summer: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="orange" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-sun"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
        };


        return icons[term.split(' ')[0].toLowerCase()];
    };

    const instructors = filterCurrentInstructors(uniqueTermInstructors(course));

    const currentlyOfferedTerms = course.terms.filter((c) =>
        getCurrentTerms().includes(c));

</script>

{#if currentlyOfferedTerms.length === 0}
    <div class='my-1.5 w-fit text-sm'>
        <div class='rounded-xl bg-gray-200 p-1 dark:bg-neutral-700'>
            <div class='flex items-center space-x-1'>
                <X size={variantToSize(variant)} class='text-gray-700 dark:text-gray-200'/>
                <div class='pr-1 font-medium text-gray-800 dark:text-gray-200'>
                    Not Offered
                </div>
            </div>
        </div>
    </div>
{/if}

<div class='mr-auto flex flex-wrap gap-x-2'>
    {#each instructors as instructor, i (i)}
        {@const term = instructor.term.split(' ')[0].toLowerCase()}
        <a
                class={twMerge(
              instructor.name === 'No Instructor Assigned'
                ? 'pointer-events-none'
                : ''
            )}
                href={`/instructor/${encodeURIComponent(instructor.name)}`}
        >
            <div
                    class={twMerge(
                'relative my-1.5 rounded-full text-sm dark:bg-neutral-700',
                variant === 'small' ? 'px-2 py-1' : 'max-w-fit p-1',
                termColorMap[term]
              )}
            >
                <div class='flex items-center space-x-1.5 whitespace-nowrap dark:text-gray-400'>
                    {#if variant === 'large'}
                        <Tooltip text={instructor.term}>
                            <div>{@html termToIcon(instructor.term, variant)}</div>
                        </Tooltip>
                    {:else }
                        <div>
                            {@html termToIcon(instructor.term, variant)}
                        </div>
                    {/if}
                    <div class={twMerge('pr-1 font-medium dark:text-gray-200')}>
                        <Highlight
                                text={instructor.name}
                                query={query}
                        />
                    </div>
                </div>
            </div>
        </a>
    {/each}
</div>