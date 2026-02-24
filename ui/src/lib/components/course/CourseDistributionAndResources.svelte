<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {GradeDistribution} from "$lib/model/GradeDistribution";
    import GradeHistogram from "$lib/components/common/stats/GradeHistogram.svelte";
    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {convertGradePointsToLetter} from "$lib/utils";

    export let gradeDistribution: GradeDistribution | undefined | null = undefined;
    export let resourceLinks: string[] | undefined = undefined;
    export let courseCode: string;
    const VISIBLE_LINKS_COUNT = 5;
    let showAllLinks = false;

     const useMediaQuery = (query: string) => {
        const matches = writable(false);

        onMount(() => {
            const mediaQueryList = window.matchMedia(query);
            matches.set(mediaQueryList.matches);

            const handler = (e: MediaQueryListEvent) => matches.set(e.matches);
            mediaQueryList.addEventListener('change', handler);

            return () => {
                mediaQueryList.removeEventListener('change', handler);
            };
        });

        return matches;
    };

    const isSmallScreen = useMediaQuery('(max-width: 650px)');

    const isXSmallScreen = useMediaQuery('(max-width: 450px)');

    $: normalizedLinks = (resourceLinks ?? []).filter((link) => typeof link === "string" && link.trim().length > 0);
    $: displayedLinks = showAllLinks ? normalizedLinks : normalizedLinks.slice(0, VISIBLE_LINKS_COUNT);
    $: hasMoreLinks = normalizedLinks.length > VISIBLE_LINKS_COUNT;
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
        {#if normalizedLinks.length > 0}
            <div class='rounded-md bg-slate-200 px-3 py-3 dark:bg-neutral-900'>
                <div class='flex items-center justify-between gap-2'>
                    <p class='my-auto text-sm dark:text-gray-200 sm:text-base'>
                        Study resources
                    </p>
                    <span class='text-xs text-gray-600 dark:text-gray-300 sm:text-sm'>
                        {normalizedLinks.length} link{normalizedLinks.length === 1 ? '' : 's'}
                    </span>
                </div>
                <div class='mt-3 space-y-2'>
                    {#each displayedLinks as link, index}
                        <a
                                href={link}
                                target="_blank"
                                rel="noopener noreferrer"
                                class='block truncate rounded-md bg-white px-3 py-2 text-sm text-primary-700 underline-offset-2 transition hover:underline dark:bg-neutral-800 dark:text-primary-500'
                                aria-label={`Open resource link ${index + 1} for ${courseCode}`}
                        >
                            {link}
                        </a>
                    {/each}
                </div>
                {#if hasMoreLinks}
                    <button
                            class='mt-3 rounded-lg bg-primary-700 px-3 py-2 text-xs font-medium text-white transition duration-200 hover:bg-primary sm:text-sm'
                            on:click={() => (showAllLinks = !showAllLinks)}
                    >
                        {showAllLinks ? 'Show fewer links' : `Show all ${normalizedLinks.length} links`}
                    </button>
                {/if}
            </div>
        {/if}
    </div>
{/key}
