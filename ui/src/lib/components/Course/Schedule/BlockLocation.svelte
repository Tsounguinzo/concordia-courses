<script lang="ts">
    import Tooltip from "$lib/components/Tooltip.svelte";
    import * as buildingCodes from '$lib/data/buildingCodes.json';

    export let roomCode: string
    export let buildingCode: string

    const code = buildingCode as keyof typeof buildingCode
</script>

<span class='relative whitespace-nowrap'>
      <Tooltip text={(buildingCode === '') ? 'On-Line' : buildingCodes[code]}>
        <p class='inline-block cursor-default text-sm leading-7 sm:text-base'>
          {roomCode}
        </p>
      </Tooltip>
</span>