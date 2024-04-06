<script lang="ts">
    import type {Review} from "$lib/model/Review";
    import {onMount} from "svelte";
    import {writable} from "svelte/store";
    import sum from "lodash/sum";
    import {twMerge} from "tailwind-merge";
    import {round2Decimals} from "$lib/utils";
    import Stat from "./Stat.svelte";
    import Histogram from "./Histogram.svelte";

    export let allReviews: Review[];
    export let className: string = '';
    export let variant: 'small' | 'medium' | 'large' = 'small';

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

    $: experience = allReviews.map((r) => r.experience);
    $: averageExperience = sum(experience) / allReviews.length;
    $: difficulties = allReviews.map((r) => r.difficulty);
    $: averageDifficulty = sum(difficulties) / allReviews.length;

</script>

{#if allReviews.length}
    {#key allReviews}
        <div class={twMerge('flex gap-x-4 bg-transparent' ,className,
        variant === 'large'
          ? 'flex-col gap-y-1 lg:flex-row lg:gap-x-2'
          : 'flex-row'
      )}
        >
            <div class='md:rounded-xl md:p-2'>
                <Stat
                        title='Experience Score'
                        value={round2Decimals(averageExperience)}
                        icon='star'
                        variant={variant}
                />
                <div class='py-2'/>
                <Histogram
                        width={180}
                        height={lg ? 132 : 80}
                        data={experience}
                        max={5}
                        gap={10}
                        className='mx-auto hidden sm:block'
                        caption="Experience Distribution"
                />
            </div>
            <div class='py-1.5'/>
            <div class='md:rounded-xl md:p-2'>
                <Stat
                        title='Difficulty Score'
                        value={round2Decimals(averageDifficulty)}
                        icon='flame'
                        variant={variant}
                />
                <div class='py-2'/>
                <Histogram
                        width={180}
                        height={lg ? 132 : 80}
                        data={difficulties}
                        max={5}
                        gap={10}
                        className='mx-auto hidden sm:block'
                        caption="Difficulty Distribution"
                />
            </div>
        </div>
    {/key}
{/if}