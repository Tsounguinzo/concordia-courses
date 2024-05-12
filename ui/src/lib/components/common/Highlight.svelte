<script lang="ts">
    import escapeRegExp from 'lodash/escapeRegExp';

    export let text: string;
    export let query: string;

    $: parts = text?.split(new RegExp(`(${escapeRegExp(query)})`, 'gi'));
</script>

<span class={$$props.class}>
        {#each parts as part, i}
            <span class:underline={part.toLowerCase().trim() === query?.toLowerCase().trim() && query !== ''}>{part}</span>
        {/each}
</span>

