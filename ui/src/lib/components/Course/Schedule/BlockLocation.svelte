<script lang="ts">
    import Tooltip from "$lib/components/Tooltip.svelte";
    import * as buildingCodes from '$lib/data/buildingCodes.json';
    import {writable} from "svelte/store";

    export let roomCode: string
    export let buildingCode: string

    const code = buildingCode as keyof typeof buildingCode
    const show = writable(false);
</script>

<span class='relative whitespace-nowrap'>
      <Tooltip {show} text={(buildingCode === "") ? "ONLINE" : buildingCodes[code]}>
        <p class='inline-block cursor-default text-sm leading-7 sm:text-base'
           on:mouseenter={() => show.set(true)}
           on:mouseleave={() => show.set(false)}>
          {roomCode}
        </p>
      </Tooltip>
</span>