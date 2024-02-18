<script lang="ts">

    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {twMerge} from "tailwind-merge";
    import HistogramBar from "$lib/components/Course/Stats/HistogramBar.svelte";

    export let data: number[];
    export let max: number;
    export let width: number;
    export let height: number;
    export let gap: number = 0;
    export let className: string = '';

    const loaded = writable(false);

    onMount(() => {
        loaded.set(true);
    });

    $: distribution = data.reduce((acc, curr) => {
        acc[curr - 1]++;
        return acc;
    }, Array(max).fill(0))
</script>

<div class={twMerge('relative w-fit', className)}>
    <div class='flex items-end' style={`width: ${width}px; height: ${height}px;`}>
        {#each distribution as count, index (index)}
            <div class='flex flex-col items-center text-xs'>
                <HistogramBar
                        width={width / distribution.length - gap}
                        height={!$loaded ? 0 : (count / data.length) * (height - 12)}
                        count={count}
                        gap={gap}
                />
                <div class='font-medium text-gray-500 dark:text-gray-400'>
                    {index + 1}
                </div>
            </div>
        {/each}
    </div>
    <div class='absolute bottom-4 h-[1px] w-full bg-gray-300 dark:bg-gray-600'/>
</div>