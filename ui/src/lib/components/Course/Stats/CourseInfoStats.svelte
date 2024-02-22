<script lang="ts">
    import type {Review} from "$lib/model/Review";
    import {onMount} from "svelte";
    import {writable} from "svelte/store";
    import _ from "lodash";
    import {twMerge} from "tailwind-merge";
    import {round2Decimals} from "$lib/utils";
    import Stat from "./Stat.svelte";
    import Histogram from "$lib/components/Course/Stats/Histogram.svelte";

    export let allReviews: Review[];
    export let className: string = '';
    export let variant: 'small' | 'medium' | 'large' = 'small';

    /* if (allReviews.length === 0) {
        return null;
    }

     */

    const useMediaQuery = (query: string) => {

        const matches =  writable(false);

        onMount(() => {
            window
                .matchMedia(query)
                .addEventListener('change', (e) => matches.set(e.matches));
        });

        return matches;
    };

    const lg = useMediaQuery('(min-width: 1024px)');

    const ratings = allReviews.map((r) => r.rating);
    const averageRating = _.sum(ratings) / allReviews.length;
    const difficulties = allReviews.map((r) => r.difficulty);
    const averageDifficulty = _.sum(difficulties) / allReviews.length;

</script>

<div
      class={twMerge(
        'flex gap-x-4 bg-transparent',
        variant === 'large'
          ? 'flex-col gap-y-1 lg:flex-row lg:gap-x-2'
          : 'flex-row',
        className
      )}
    >
      <div class='md:rounded-xl md:p-2'>
        <Stat
          title='Rating'
          value={round2Decimals(averageRating)}
          icon='user'
          variant={variant}
        />
        <div class='py-2' />
        <Histogram
          width={180}
          height={lg ? 132 : 80}
          data={ratings}
          max={5}
          gap={10}
          className='mx-auto hidden sm:block'
        />
      </div>
      <div class='py-1.5' />
      <div class='md:rounded-xl md:p-2'>
        <Stat
          title='Difficulty'
          value={round2Decimals(averageDifficulty)}
          icon='flame'
          variant={variant}
        />
        <div class='py-2' />
        <Histogram
          width={180}
          height={lg ? 132 : 80}
          data={difficulties}
          max={5}
          gap={10}
          className='mx-auto hidden sm:block'
        />
      </div>
    </div>