<script lang="ts">
    import {Flame, Star} from "lucide-svelte";

    export let icon: 'flame' | 'star';
    export let rating: number;
</script>

<div class='flex'>
    {#each Array(5) as _, i }
        {#if icon === 'flame' }
            <Flame class={i < rating
            ? 'fill-blue-600 stroke-none'
            : 'stroke-gray-300 dark:stroke-gray-600'} size={20}/>
        {:else }
            <Star class={i < rating
            ? 'fill-blue-600 stroke-none'
            : 'stroke-gray-300 dark:stroke-gray-600'} size={20}/>
        {/if}
    {/each}
</div>
