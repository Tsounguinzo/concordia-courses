<script lang="ts">
    import _ from 'lodash';

    export let text: string;
    export let query: string;
    export let className: string = '';

    $: parts = text.split(new RegExp(`(${_.escapeRegExp(query)})`, 'gi'));
</script>

<span class={className}>
        {#each parts as part, i}
            <span class:underline={part.toLowerCase().trim() === query?.toLowerCase().trim() && query !== ''}>{part}</span>
        {/each}
</span>

