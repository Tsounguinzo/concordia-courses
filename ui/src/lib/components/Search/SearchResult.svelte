<script lang="ts">
    import {writable} from "svelte/store";
    import {Layers, User} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import Highlight from "$lib/components/Highlight.svelte";

    export let index: number;
    export let query: string = '';
    export let selectedIndex: number;
    export let text: string;
    export let type: 'course' | 'instructor';
    export let url: string;

    const hovering = writable(false);
    $: toHighlight = $hovering || selectedIndex === index;

    const highlightResultStyle = 'bg-red-50 border-l-red-500 border-l-4 dark:bg-red-100 dark:border-l-red-600 dark:bg-neutral-600';

</script>

<a
      href={url}
      class='cursor-pointer'
      on:mouseenter={() => hovering.set(true)} on:mouseleave={() => hovering.set(false)}
      on:click
    >
      <div
        class={twMerge(
          'flex border-gray-200 p-3 text-left dark:border-neutral-700 transition-all duration-75',
          toHighlight ? highlightResultStyle : 'bg-gray-100 dark:bg-neutral-800'
        )}
      >
        <div class='mr-2 w-6'>
            {#if type === 'course'}
                <Layers class='dark:text-gray-200' />
                {:else }
                <User class='dark:text-gray-200' />
                {/if}
        </div>
        <Highlight
          className='dark:text-gray-200'
          query={query?.trim()}
          text={text}
        />
      </div>
    </a>