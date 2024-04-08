<script lang="ts">
    import type {Writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";

    export let className: string = "";
    export let selectedClass: string;
    export let sortBy: Writable<string>;
    export let name: string;

    $: selected = $sortBy === name;

    const selectedColor = selectedClass ?? 'text-blue-600 bg-gray-300 dark:bg-neutral-700';

    const unselectedColor = 'hover:bg-gray-200 hover:dark:bg-neutral-600 text-gray-600 dark:text-gray-300';
</script>

<button class={twMerge(
        'rounded-full px-3 py-1 text-sm font-medium tracking-wider transition duration-150 ease-in-out',
        selected ? selectedColor : unselectedColor,
        className
      )}
        on:click={() => sortBy.set(name)}
>
    <span class='flex items-center gap-x-2'>
        <slot/>
        {name}
    </span>
</button>