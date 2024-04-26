<script lang="ts">
    import {Flame, Star} from "lucide-svelte";
    export let icon: 'flame' | 'star';
    export let name: string;
    export let rating: number;
    export let setFieldValue: (name: string, value: any) => void;

    let hoveredIndex = -1;
</script>

<div class="flex flex-row gap-1.5">
    {#each Array(5) as _, i }
        <button class='cursor-pointer'
                on:blur
                on:click|preventDefault={() => setFieldValue(name, i + 1)}
                on:mouseenter={() => hoveredIndex = i}
                on:mouseleave={() => hoveredIndex = -1}>
        {#if icon === 'flame' }
            <Flame class="transition-colors duration-75 {i <= hoveredIndex || i < rating ? 'fill-blue-600' : 'fill-gray-200'}" size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {:else }
            <Star class="transition-colors duration-75 {i <= hoveredIndex || i < rating ? 'fill-blue-600' : 'fill-gray-200'}" size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {/if}
            </button>
    {/each}
</div>