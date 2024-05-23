<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {GradeDistribution} from "$lib/model/GradeDistribution";
    import GradeHistogram from "$lib/components/common/stats/GradeHistogram.svelte";
    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {convertGradePointsToLetter} from "$lib/utils";

    export let gradeDistribution: GradeDistribution | undefined | null = undefined;
    export let notesUrl: string | undefined = undefined;
    export let courseCode: string;

    const handleClick = () => {
        const link = document.createElement('a');
        link.href = notesUrl;
        link.download = `${courseCode}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

     const useMediaQuery = (query: string) => {
        const matches = writable(false);

        onMount(() => {
            const mediaQueryList = window.matchMedia(query);
            matches.set(mediaQueryList.matches);

            const handler = (e) => matches.set(e.matches);
            mediaQueryList.addEventListener('change', handler);

            return () => {
                mediaQueryList.removeEventListener('change', handler);
            };
        });

        return matches;
    };

    const isSmallScreen = useMediaQuery('(max-width: 650px)');

    const isXSmallScreen = useMediaQuery('(max-width: 450px)');
</script>

{#key gradeDistribution}
    <div class={twMerge('relative w-full rounded-md bg-slate-50 shadow-sm dark:bg-neutral-800 flex-row items-center justify-center p-6',$$props.class)}>
        {#if gradeDistribution}
            <GradeHistogram
                    width={$isXSmallScreen ? 300 : $isSmallScreen ? 400 : 500}
                    height={200}
                    data={gradeDistribution}
                    gap={10}
                    class='mx-auto'
                    caption={`${gradeDistribution.term} ${gradeDistribution.year}:  Class Avg (${convertGradePointsToLetter(gradeDistribution.classAverage)}), Size (${gradeDistribution.classSize})`}
            />
        {/if}
        {#if notesUrl}
            <div class='flex h-fit justify-between rounded-md px-3 py-2 bg-slate-200 dark:bg-neutral-900 items-center'>
                <p class='my-auto text-sm dark:text-gray-200 sm:text-base'>
                    Need study notes?
                    {' '}
                </p>
                <button class='rounded-lg bg-primary-700 px-3 py-2 text-sm font-medium text-white transition duration-200 hover:bg-primary sm:text-base'
                        on:click={handleClick}>
                    Download Notes
                </button>
            </div>
        {/if}
    </div>
{/key}