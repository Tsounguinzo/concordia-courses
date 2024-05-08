<script lang="ts">
    import {writable} from "svelte/store";
    import {Layers} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import {highlightResultStyle} from "$lib/constants";

    let hovering = writable(false)
</script>

<a href="/explore"
        on:mouseenter={() => hovering.set(true)}
        on:mouseleave={() => hovering.set(false)}
>
    <div class={twMerge(
          'flex items-center p-3 text-left dark:border-gray-600 dark:bg-neutral-800 dark:text-gray-200 transition-all duration-75',
          $hovering ? highlightResultStyle : 'bg-gray-100 dark:bg-neutral-800'
        )}
    >
        <Layers class='dark:text-gray-200'/>
        <div class='z-50 ml-2 dark:text-gray-200'>Explore all courses and instructors</div>
    </div>
</a>