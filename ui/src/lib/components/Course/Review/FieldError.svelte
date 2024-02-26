<script lang="ts">
    import {getContext} from "svelte";
    export let name: string;
    const errors = getContext('errors')
    $: error = $errors[name]

</script>
{#if error}
    <div class='text-sm italic text-red-400'>
        {$errors[name]}
    </div>
{/if}
