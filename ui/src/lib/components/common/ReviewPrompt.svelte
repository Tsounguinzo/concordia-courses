<script lang="ts">
    import type {Writable} from "svelte/store";
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    import {page} from "$app/stores";
    import {visitorId} from "$lib/store";

    export let openAddReview: Writable<boolean>;
    export let hasUserReviewed: boolean;
    export let type: 'course' | 'instructor' | 'school' = 'course';

    const user =  $page.data.user
    $: visitor = $visitorId;
    let promptVerification = false;
    let message = '';
    const displayPrompt = (promptMessage: string) => {
        message = promptMessage;
        promptVerification = true;
        setTimeout(() => promptVerification = false, 3000);
    };

    const handleClick = () => {
        if (!visitor) {
            displayPrompt("Try refresh the page")
        } else if (user && !user?.verified) {
            displayPrompt("Verify your account")
        } else {
            openAddReview.set(true)
        }
    };
</script>
<div class='flex h-fit justify-between rounded-md px-3 py-2 dark:bg-neutral-900'>
    <p class='my-auto text-sm dark:text-gray-200 sm:text-base'>
        {#if type === 'course'}
            {'Taken this course?'}
        {:else if type === 'instructor'}
            {'Had this instructor?'}
        {:else if type === 'school'}
            {'Attended this school?'}
        {/if}
        {' '}
    </p>
    <button class='mt-2 rounded-lg bg-primary-700 px-3 py-2 text-sm font-medium text-white transition duration-200 hover:bg-primary sm:text-base disabled:bg-gray-400'
            on:click={handleClick} disabled={hasUserReviewed}>
        <Tooltip text={message} show={promptVerification} offset={{x: -15, y: -15}}/>
        {hasUserReviewed ? 'Already Reviewed' : 'Leave a Review'}
    </button>
</div>