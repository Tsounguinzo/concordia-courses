<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Block} from "$lib/model/Schedule";

    export let block: Block

    $: days = new Map([
        ["M", block.mondays === 'Y'],
        ["Tu", block.tuesdays === 'Y'],
        ["W", block.wednesdays === 'Y'],
        ["Th", block.thursdays === 'Y'],
        ["F", block.fridays === 'Y'],
        ["Sa", block.saturdays === 'Y'],
        ["Su", block.sundays === 'Y']
    ]);
</script>

<div class='flex gap-1'>
    {#each ['M', 'Tu', 'W', 'Th', 'F', 'Sa', 'Su'] as day}
        <span class={twMerge('sm:text-base text-sm',
                days.get(day)
                    ? 'font-semibold text-gray-800 dark:text-gray-100'
                    : 'text-gray-400 font-extralight dark:text-gray-400'
            )}
        >
          {day.charAt(0)}
        </span>
    {/each}
</div>