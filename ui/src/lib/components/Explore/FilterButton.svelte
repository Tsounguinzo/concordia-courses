<script lang="ts">
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";

    export let icon: string = "";
    export let className: string = "";
    export let selectedClass: string;
    export let isSelected: boolean;
    export let name: string;
    export let selections: Writable<string[]>;

    const selected = writable(isSelected);

    if (isSelected !== $selected) selected.set(isSelected);

    const selectedColor = selectedClass ?? 'bg-blue-200 text-blue-900';

    const unselectedColor = 'bg-gray-100 dark:bg-neutral-700 text-gray-600 dark:text-gray-300';
</script>

<button class={twMerge(
        'rounded-full px-2 py-1 text-sm font-medium tracking-wider transition duration-150 ease-in-out',
        $selected ? selectedColor : unselectedColor,
        className
      )}
        on:click={() => {
        selected.set(!$selected);
        if ($selected) {
            selections.set($selections.filter((select) => select !== name));
        } else {
            selections.set($selections.concat(name));
        }
      }}
>
    <div class='flex items-center gap-x-2'>
        {@html icon}
        {name}
    </div>
</button>