<script lang="ts">
    import type {Writable} from "svelte/store";
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    import {page} from "$app/stores";

    export let openAddReview: Writable<boolean>;
    export let type: 'course' | 'instructor' | 'school' = 'course';

    const user =  $page.data.user
    let promptLogin = false;
    let message = '';
    const displayPrompt = (promptMessage: string) => {
        message = promptMessage;
        promptLogin = true;
        setTimeout(() => promptLogin = false, 3000);
    };

    const handleClick = () => {
        if (!user) {
            displayPrompt("LogIn to your account")
        } else if (!user?.verified) {
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
    <button class='mt-2 rounded-lg bg-blue-700 px-3 py-2 text-sm font-medium text-white transition duration-200 hover:bg-blue-500 sm:text-base'
            on:click={handleClick}>
        <Tooltip text={message} show={promptLogin} offset={{x: -15, y: -15}}/>
        Leave a review
    </button>
</div>