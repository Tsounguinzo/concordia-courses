<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Block} from "$lib/model/Schedule";

    export let block: Block

    let days = new Map([
        ["M", block.mondays === 'Y'],
        ["T", block.tuesdays === 'Y'],
        ["W", block.wednesdays === 'Y'],
        ["T", block.thursdays === 'Y'],
        ["F", block.fridays === 'Y'],
        ["S", block.saturdays === 'Y'],
        ["S", block.sundays === 'Y']
    ]);
</script>

<div class='flex gap-1'>
    {#each ['M', 'T', 'W', 'T', 'F', 'S', 'S'] as day}
        <span class={twMerge('sm:text-base text-sm',
                days.get(day)
                    ? 'font-semibold text-gray-800 dark:text-gray-100'
                    : 'text-gray-400 font-extralight dark:text-gray-400'
            )}
        >
          {day}
        </span>
    {/each}
</div>