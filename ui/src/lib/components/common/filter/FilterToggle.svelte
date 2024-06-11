<script lang="ts">
    import {ChevronUp} from "lucide-svelte";

    let expanded = false
</script>


<button on:click|stopPropagation={() => expanded = !expanded}
        class="flex w-full justify-between rounded-lg bg-slate-50 px-4 py-2 text-primary dark:bg-neutral-800"
>
    <div class='text-sm font-medium text-gray-600 dark:text-gray-400'>
        Filter...
    </div>
    <ChevronUp class="h-5 w-5 text-gray-500 {expanded ? 'transform' : 'rotate-180'}"/>
</button>
{#if expanded}
    <div>
        <div class='py-1'/>
        <slot/>
    </div>
{/if}
