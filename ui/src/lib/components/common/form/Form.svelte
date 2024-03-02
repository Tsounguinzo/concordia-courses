<script lang="ts">
    import { getContext } from 'svelte'
    import { persist } from '$lib/persist';

    const handleSubmit = getContext('handleSubmit')
    const handleReset = getContext('handleReset')

    export let storageKey: string;
</script>

<form use:persist={{key: storageKey}} on:submit|preventDefault={handleSubmit} on:reset|preventDefault={handleReset} {...$$props}>
    <slot />
</form>