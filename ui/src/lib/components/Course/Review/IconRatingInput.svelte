<script lang="ts">
    import {Flame, UserRound} from "lucide-svelte";
    import type {Writable} from "svelte/store";

    export let icon: 'flame' | 'user';
    export let name: string;
    export let fieldValue: Writable<number>;

    let hoveredIndex = -1;
</script>

    {#each Array(5) as _, i }
        <button class='cursor-pointer w-fit'
                on:click|preventDefault={() => fieldValue.set(i + 1)}
                on:mouseenter={() => hoveredIndex = i}
                on:mouseleave={() => hoveredIndex = -1}>
        {#if icon === 'flame' }
            <Flame class="transition-colors duration-75 {i <= hoveredIndex || i < $fieldValue ? 'fill-blue-600' : 'fill-gray-200'}" size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {:else }
            <UserRound class="transition-colors duration-75 {i <= hoveredIndex || i < $fieldValue ? 'fill-blue-600' : 'fill-gray-200'}" size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {/if}
            </button>
    {/each}
