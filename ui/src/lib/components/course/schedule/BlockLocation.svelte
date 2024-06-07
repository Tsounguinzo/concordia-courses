<script lang="ts">
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    export let locationCode: string

    let show = false;

    function locationLongName(locationCode: string): string {
        switch (locationCode) {
            case 'SGW': return 'Sir George Williams – downtown';
            case 'LOY': return 'Loyola';
            case 'ONL': return 'Online';
            case 'PI': return 'Power Institute';
            default: return locationCode;
        }
    }
</script>

<span class='relative whitespace-nowrap'>
      <Tooltip {show} text={locationLongName(locationCode)}>
        <p class='inline-block cursor-default text-sm leading-7 sm:text-base'
           on:mouseenter={() => show = true}
           on:mouseleave={() => show = false}>
          {locationCode}
        </p>
      </Tooltip>
</span>