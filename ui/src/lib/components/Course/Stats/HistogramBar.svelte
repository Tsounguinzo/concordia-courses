<script lang="ts">
    import Tooltip from "$lib/components/Tooltip.svelte";
    import {writable} from "svelte/store";

    export let width: number;
    export let height: number;
    export let count: number;
    export let gap: number;
    const show = writable(false);
</script>

<Tooltip {show} text={count.toString()} offset={{ x: 4, y: -8 }}>
    <div class='ml-0.5 rounded-t-sm bg-blue-500 transition-all duration-700 ease-in-out'
         style={`width: ${width}px; height: ${height}px; margin-left: ${gap / 2}px; margin-right: ${gap / 2}px;`}
         on:mouseenter={() => show.set(true)}
         on:mouseleave={() => show.set(false)}
    />
</Tooltip>