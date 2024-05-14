<script lang="ts">
    import Spinning from "$lib/components/common/loader/Spinning.svelte";

    export let isSubmitting: boolean = false;

</script>

<button type="submit"
        class="ml-auto w-fit rounded-md bg-primary-600 px-4 py-2 disabled:px-2 font-medium text-white transition duration-300 hover:bg-primary-800 disabled:bg-gray-500 disabled:cursor-not-allowed"
        disabled={isSubmitting}
        on:click>
    Submit
    {#if isSubmitting}
        <Spinning class="p-1 m-0 h-7 w-7"/>
    {/if}
</button>