<script lang="ts">
    import Tooltip from "$lib/components/common/Tooltip.svelte";

    export let width: number;
    export let height: number;
    export let count: number;
    export let gap: number;
    let show = false;

    let dynamicHeight = 0;
    setTimeout(() => dynamicHeight = height, 200);

</script>

<Tooltip {show} text={count.toString()} offset={{ x: 4, y: -8 }}>
    <p class='ml-0.5 rounded-t-sm bg-primary transition-all duration-700 ease-in-out'
         style={`width: ${width}px; height: ${dynamicHeight}px; margin-left: ${gap / 2}px; margin-right: ${gap / 2}px;`}
         on:mouseenter={() => show = true}
         on:mouseleave={() => show = false}
    ></p>
</Tooltip>