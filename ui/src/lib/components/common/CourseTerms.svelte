<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {
        addAcademicYear,
        termColorMap,
        termToIcon,
        variantToSize
    } from "$lib/utils";
    import {X} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import Highlight from "./Highlight.svelte";

    export let course: Course;
    export let variant: 'large' | 'small';
    export let query: string = '';
</script>

{#if course.terms.length === 0}
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
{:else }
    <div class='mr-auto flex flex-wrap gap-x-2'>
        {#each course.terms as term, i (i)}

            <div class={twMerge(
                'relative my-1.5 rounded-full text-sm dark:bg-neutral-700',
                variant === 'small' ? 'px-2 py-1' : 'max-w-fit p-1',
                termColorMap[term]
              )}
            >
                <div class='flex items-center space-x-1.5 whitespace-nowrap dark:text-gray-400'>
                    {@html termToIcon(term, variant)}
                    <div class={twMerge('pr-1 font-medium dark:text-gray-200')}>
                        <Highlight text={addAcademicYear(term)} {query}/>
                    </div>
                </div>
            </div>
        {/each}
    </div>
{/if}