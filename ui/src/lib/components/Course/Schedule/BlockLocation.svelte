<script lang="ts">
    import Tooltip from "$lib/components/Tooltip.svelte";
    import * as buildingCodes from '$lib/data/buildingCodes.json';

    export let roomCode: string
    export let buildingCode: string

    const code = buildingCode as keyof typeof buildingCode
    let show = false;
</script>

<span class='relative whitespace-nowrap'>
      <Tooltip {show} text={(buildingCode === "") ? "ONLINE" : buildingCodes[code]}>
        <p class='inline-block cursor-default text-sm leading-7 sm:text-base'
           on:mouseenter={() => show = true}
           on:mouseleave={() => show = false}>
          {roomCode}
        </p>
      </Tooltip>
</span>