<script>
    import { getContext } from 'svelte'
    import { persist } from '$lib/persist';

    const handleSubmit = getContext('handleSubmit')
    const handleReset = getContext('handleReset')
</script>

<form use:persist={{key: 'review-form'}} on:submit|preventDefault={handleSubmit} on:reset|preventDefault={handleReset} {...$$props}>
    <slot />
</form>