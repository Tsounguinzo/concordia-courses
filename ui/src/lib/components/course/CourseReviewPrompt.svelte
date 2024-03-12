<script lang="ts">
    import type {Writable} from "svelte/store";
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    import {useAuth} from "$lib/auth";

    export let openAddReview: Writable<boolean>;

    const user =  useAuth();
    let promptLogin = false;
    const displayLoginPrompt = () => {
        promptLogin = true;
        setTimeout(() => promptLogin = false, 3000);
    };

    const handleClick = () => {
        user ?
            openAddReview.set(true)
            : displayLoginPrompt();
    };
</script>
<div class='flex h-fit justify-between rounded-md px-3 py-2 dark:bg-neutral-900'>
    <p class='my-auto text-sm dark:text-gray-200 sm:text-base'>
        Taken this course?{' '}
    </p>
    <button class='mt-2 rounded-lg bg-blue-700 px-3 py-2 text-sm font-medium text-white transition duration-200 hover:bg-blue-500 sm:text-base'
            on:click={handleClick}>
        <Tooltip text="You must be logged in" show={promptLogin} offset={{x: 0, y: -15}}/>
        Leave a review
    </button>
</div>