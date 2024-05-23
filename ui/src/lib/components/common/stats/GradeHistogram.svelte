<script lang="ts">

    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {twMerge} from "tailwind-merge";
    import HistogramBar from "./HistogramBar.svelte";
    import type {GradeDistribution} from "$lib/model/GradeDistribution";

    export let data: GradeDistribution;
    export let width: number;
    export let height: number;
    export let gap: number = 0;
    export let caption: string = 'Distribution';

    const loaded = writable(false);

    onMount(() => {
        loaded.set(true);
    });

    const gradeLabels = [
        'A+', 'A', 'A-', 'B+', 'B', 'B-',
        'C+', 'C', 'C-', 'D+', 'D', 'D-',
        'F', 'FNS', 'R', 'NR'
    ];

    $: distribution = data.grades.reduce((acc, { grade, count }) => {
        const index = gradeLabels.indexOf(grade);
        if (index !== -1) {
            acc[index] += count;
        }
        return acc;
    }, Array(gradeLabels.length).fill(0));
</script>

<div class={twMerge('relative w-fit', $$props.class)}>
    <div class='flex items-end' style={`width: ${width}px; height: ${height <= 100 ? height : 100}px;`}>
        {#each distribution as count, index (index)}
            <div class='flex flex-col items-center text-xs'>
                <HistogramBar
                        width={width / distribution.length - gap}
                        height={!$loaded ? 0 : (count / data.classSize) * (height - 50)}
                        count={count}
                        gap={gap}
                />
                <div class='font-medium text-gray-500 dark:text-gray-400'>
                    {gradeLabels[index]}
                </div>
            </div>
        {/each}
    </div>
    <div class='absolute bottom-4 h-[1px] w-full bg-gray-300 dark:bg-gray-600'/>
</div>
<div class={twMerge('text-center my-4 text-xs font-medium uppercase tracking-wider text-gray-600 dark:text-gray-400', $$props.class)}>
    {caption}
</div>