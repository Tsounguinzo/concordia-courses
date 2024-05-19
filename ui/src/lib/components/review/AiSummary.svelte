<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Instructor} from "$lib/model/Instructor";
    import {timeFrame} from "$lib/utils";
    import {onMount} from "svelte";
    import MarkDown from "$lib/components/common/MarkDown.svelte";
    import {Minus, Plus} from "lucide-svelte";

    export let instructor: Instructor;

    let showSummary = false;
    let now: Date = new Date();
    let displayTime: string;

    $: displayTime = timeFrame(new Date(instructor?.lastSummaryUpdate), now);

    onMount(() => {
        const interval = setInterval(() => {
            now = new Date();
        }, 1000); // Update every second

        return () => {
            clearInterval(interval);
        };
    });
</script>

<div class={twMerge(
  'relative flex w-full flex-col gap-4 bg-slate-50 px-6 py-3 mb-3 rounded-md dark:border-b-gray-600 dark:bg-neutral-800',
  $$props.class
)}>
    <div class="flex justify-between items-center">
        <div class="flex-grow flex justify-center">
            <div class="flex flex-col items-center">
                <div class='text-3xl font-bold text-gray-800 dark:text-gray-200'>
                    AI Summary
                </div>
                <div class="text-gray-600 dark:text-gray-400 text-center">
                    Based on {instructor.reviewCount <= 50 ? instructor.reviewCount : '50 random'} reviews
                </div>
            </div>
        </div>
        <button
                class="w-8 h-8 rounded-full flex items-center justify-center bg-slate-200 dark:bg-neutral-700 text-gray-800 dark:text-gray-200"
                on:click={() => showSummary = !showSummary}
        >
      <span class="summary-toggle" class:open={showSummary}>
        {#if showSummary}
          <Minus/>
        {:else}
          <Plus/>
        {/if}
      </span>
        </button>
    </div>
    <div class="relative flex w-full flex-col summary-content items-center" class:open={showSummary} class:closed={!showSummary}>
        <div class='mt-2 px-2 bg-primary-400 rounded-lg w-fit'>
            Powered By Chat GPT (gpt-4o)
        </div>
        <div class='text-gray-500 mt-2'>
            last update: {displayTime}
        </div>
        <div class='overflow-y-auto ml-1 mr-4 mt-2 hyphens-auto text-left text-gray-800 dark:text-gray-300 scrollbar-hide'>
            <MarkDown source={instructor.aiSummary}/>
        </div>
    </div>
</div>

<style>
    .summary-toggle {
        transition: transform 0.3s ease-in-out;
    }

    .summary-toggle.open {
        transform: rotate(180deg);
    }

    .summary-content {
        transition: max-height 0.3s ease-in-out;
    }

    .summary-content.closed {
        max-height: 0;
        overflow: hidden;
    }

    .summary-content.open {
        max-height: 1000px; /* or any large enough value */
    }
</style>