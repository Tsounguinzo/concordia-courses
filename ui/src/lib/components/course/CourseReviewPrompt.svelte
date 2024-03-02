<script lang="ts">
    import type {Writable} from "svelte/store";

    export let openAddReview: Writable<boolean>;
</script>

<div class='flex h-fit justify-between rounded-md px-3 py-2 dark:bg-neutral-900'>
    <p class='my-auto text-sm dark:text-gray-200 sm:text-base'>
        Taken this course?{' '}
    </p>
    <button class='rounded-lg bg-blue-700 px-3 py-2 text-sm font-medium text-white transition duration-200 hover:bg-blue-500 sm:text-base'
            on:click={() => openAddReview.set(true)}>
        Leave a review
    </button>
</div>