<script lang="ts">
    import type {Writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";
    import {getContext} from "svelte";

    export let selectedClass: string;
    export let sortBy: Writable<string>;
    export let name: string;
    const i18n = getContext('i18n');

    $: selected = $sortBy === name;

    const selectedColor = selectedClass ?? 'text-primary-600 bg-gray-300 dark:bg-neutral-700';

    const unselectedColor = 'hover:bg-gray-200 hover:dark:bg-neutral-600 text-gray-600 dark:text-gray-300';
</script>

<button class={twMerge(
        'rounded-full px-3 py-1 text-sm font-medium tracking-wider transition duration-150 ease-in-out',
        selected ? selectedColor : unselectedColor,
        $$props.class
      )}
        on:click={() => sortBy.set(name)}
>
    <span class='flex items-center gap-x-2'>
        <slot/>
        {$i18n.t(name)}
    </span>
</button>