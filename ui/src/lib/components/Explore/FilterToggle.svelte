<script lang="ts">
    import {createDisclosure} from 'svelte-headlessui'
    import {ChevronUp} from "lucide-svelte";

    const filter = createDisclosure({label: 'filter', expanded: true})
</script>


<button use:filter.button
        class="flex w-full justify-between rounded-lg bg-slate-200 px-4 py-2 text-blue-500 dark:bg-neutral-700"
>
    <div class='text-sm font-medium text-gray-600 dark:text-gray-400'>
        Filter...
    </div>
    <ChevronUp class="h-5 w-5 text-gray-500 {$filter.expanded ? 'rotate-180 transform' : ''}"/>
</button>
{#if $filter.expanded}
    <div use:filter.panel>
        <div class='py-1'/>
        <slot/>
    </div>
{/if}
