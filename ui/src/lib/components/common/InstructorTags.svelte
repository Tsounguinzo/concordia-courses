<script lang="ts">
    import {
        variantToSize
    } from "$lib/utils";
    import {X} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import Highlight from "./Highlight.svelte";
    import type {Instructor} from "$lib/model/Instructor";

    export let instructor: Instructor;
    export let variant: 'large' | 'small';
    export let query: string = '';
</script>

{#if instructor.tags.length === 0}
    <div class='my-1.5 w-fit text-sm'>
        <div class='rounded-xl bg-gray-200 p-1 dark:bg-neutral-700'>
            <div class='flex items-center space-x-1'>
                <X size={variantToSize(variant)} class='text-gray-700 dark:text-gray-200'/>
                <div class='pr-1 font-medium text-gray-800 dark:text-gray-200'>
                    No Tags
                </div>
            </div>
        </div>
    </div>
{:else }
    <div class='mr-auto flex flex-wrap gap-x-2'>
        {#each instructor.tags as tag, i (i)}

            <div class={twMerge(
                'relative my-1.5 bg-gray-200 rounded-full text-sm dark:bg-neutral-700',
                variant === 'small' ? 'px-2 py-1' : 'max-w-fit p-1',
                $$props.class
              )}
            >
                <div class='flex items-center space-x-1.5 whitespace-nowrap dark:text-gray-400'>
                    <div class={twMerge('pr-1 font-medium dark:text-gray-200')}>
                        <Highlight text={tag} {query}/>
                    </div>
                </div>
            </div>
        {/each}
    </div>
{/if}