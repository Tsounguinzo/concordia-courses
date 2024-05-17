<script lang="ts">
    import { writable } from "svelte/store";
    import { CornerDownLeft, Layers, User } from "lucide-svelte";
    import { twMerge } from "tailwind-merge";
    import Highlight from "$lib/components/common/Highlight.svelte";
    import { highlightResultStyle } from "$lib/constants";
    import {goto} from "$app/navigation";

    export let index: number;
    export let query: string = '';
    export let selectedIndex: number;
    export let text: string;
    export let type: 'course' | 'instructor';
    export let url: string;

    const hovering = writable(false);
    $: toHighlight = $hovering || selectedIndex === index;

    function handleClick() {
        goto(url);
    }

    function handleKeydown(event: KeyboardEvent) {
        if (event.key === 'Enter' || event.key === ' ') {
            handleClick();
        }
    }
</script>

<button
        type="button"
        class='w-full text-left cursor-pointer'
        on:mouseenter={() => hovering.set(true)}
        on:mouseleave={() => hovering.set(false)}
        on:click={handleClick}
        on:keydown={handleKeydown}
>
    <div class={twMerge(
        'flex items-center justify-between border-gray-200 p-3 dark:border-neutral-700 transition-all duration-75',
        toHighlight ? highlightResultStyle : 'bg-gray-100 dark:bg-neutral-800'
    )}>
        <div class="flex items-center">
            <div class='mr-2'>
                {#if type === 'course'}
                    <Layers class='dark:text-gray-200 w-4 h-4 sm:w-5 sm:h-5 md:w-6 md:h-6'/>
                {:else}
                    <User class='dark:text-gray-200 w-4 h-4 sm:w-5 sm:h-5 md:w-6 md:h-6'/>
                {/if}
            </div>
            <Highlight
                    class='dark:text-gray-200 text-xs sm:text-sm md:text-base'
                    query={query?.trim()}
                    text={text}
            />
        </div>
        <CornerDownLeft class="text-gray-500 flex-shrink-0 w-4 h-4 sm:w-5 sm:h-5 md:w-6 md:h-6"/>
    </div>
</button>
