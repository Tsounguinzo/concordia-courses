<script lang="ts">
    import {onMount} from "svelte";
    import {writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";
    import {round2Decimals} from "$lib/utils";
    import Stat from "./Stat.svelte";
    import Histogram from "./Histogram.svelte";

    export let avgDifficulty: number;
    export let experienceAndRatingDistribution: number[];
    export let avgExperienceAndRating: number;
    export let difficultyDistribution: number[];
    export let reviewsCount: number;
    export let variant: 'small' | 'medium' | 'large' = 'small';
    export let type: 'course' | 'instructor' = 'course';

    const useMediaQuery = (query: string) => {

        const matches = writable(false);

        onMount(() => {
            window
                .matchMedia(query)
                .addEventListener('change', (e) => matches.set(e.matches));
        });

        return matches;
    };

    const lg = useMediaQuery('(min-width: 1024px)');
</script>

{#if reviewsCount > 0}
    {#key difficultyDistribution}
        <div class={twMerge('flex gap-x-4 bg-transparent' ,$$props.class,
        variant === 'large'
          ? 'flex-col gap-y-1 lg:flex-row lg:gap-x-2'
          : 'flex-row'
      )}
        >
            <div class='md:rounded-xl md:p-2'>
                <Stat
                        title={`${type === 'course' ? 'Experience' : 'Clarity Rating'} Score`}
                        value={round2Decimals(avgExperienceAndRating)}
                        icon='star'
                        variant={variant}
                />
                <div class='py-2'/>
                <Histogram
                        width={180}
                        height={$lg ? 132 : 80}
                        distribution={experienceAndRatingDistribution}
                        gap={10}
                        class='mx-auto hidden sm:block'
                        caption={`${type === 'course' ? 'Experience' : 'Clarity Rating'} Distribution`}
                />
            </div>
            <div class='py-1.5'/>
            <div class='md:rounded-xl md:p-2'>
                <Stat
                        title='Difficulty Score'
                        value={round2Decimals(avgDifficulty)}
                        icon='flame'
                        variant={variant}
                />
                <div class='py-2'/>
                <Histogram
                        width={180}
                        height={$lg ? 132 : 80}
                        distribution={difficultyDistribution}
                        gap={10}
                        class='mx-auto hidden sm:block'
                        caption="Difficulty Distribution"
                />
            </div>
        </div>
    {/key}
{/if}