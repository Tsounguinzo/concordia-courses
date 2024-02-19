<script lang="ts">
    import {Flame, UserRound} from "lucide-svelte";
    import type {Writable} from "svelte/store";

    export let icon: 'flame' | 'user';
    export let rating: number;
    export let name: string;
    export let fieldValue: Writable<number>;
</script>

    {#each Array(5) as _, i }
        <button class='cursor-pointer' on:click={() => fieldValue.set(i + 1)}>
        {#if icon === 'flame' }
            <Flame class={i < rating ? 'fill-blue-600 ' : 'fill-gray-200'} size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {:else }
            <UserRound class={i < rating ? 'fill-blue-600 ' : 'fill-gray-200'} size={22} strokeWidth={0} id={`${name}-star-${i}`}/>
        {/if}
            </button>
    {/each}
