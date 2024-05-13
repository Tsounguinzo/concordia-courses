<script lang="ts">
    import {writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";
    import tags from '$lib/data/tags.json'

    export let name: string;
    export let limit: number;
    export let selectedTags: string[];
    export let setFieldValue: (name: string, value: any) => void;

    const selections = writable<string[]>(selectedTags);

    const selectedColor = 'bg-primary-200 text-primary-900';
    const unselectedColor = 'bg-gray-200 dark:bg-neutral-700 text-gray-600 dark:text-gray-300';

    function updateSelections(tagName: string) {
        selections.update(current => {
            const index = current.indexOf(tagName);
            if (index !== -1) {
                return current.filter(n => n !== tagName); // Remove item if already selected
            } else if (current.length < limit) {
                return [...current, tagName]; // Add item if less than the limit are selected
            }
            return current; // Return current without changes if 3 items are already selected
        });
    }

    selections.subscribe((currentSelections) => {
        setFieldValue(name, currentSelections);
    });
</script>
<div class='flex flex-wrap gap-2'>
{#each tags as tag, i (i)}
        <button
                type="button"
                class={twMerge(
        'rounded-full px-2 py-1 text-xs font-medium tracking-wider transition duration-150 ease-in-out w-fit',
        $selections?.includes(tag) ? selectedColor : unselectedColor,
        $$props.class
    )}
                on:click={() => updateSelections(tag)}
        >
                {tag}
        </button>
{/each}
</div>