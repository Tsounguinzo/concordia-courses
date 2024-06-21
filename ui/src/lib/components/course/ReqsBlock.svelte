<script lang="ts">
    import TextProcessor from "$lib/components/common/TextProcessor.svelte";

    export let title: string;
    export let text: string;
</script>

<div>
    <h2 class='mb-2 mt-1 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
        {title}
    </h2>

    {#if text}
        <div class='text-gray-500 dark:text-gray-400'>
            <TextProcessor {text}/>
        </div>
    {:else }
        <p class='text-gray-500 dark:text-gray-400'>
            This course has no {title}.
        </p>
    {/if}
</div>