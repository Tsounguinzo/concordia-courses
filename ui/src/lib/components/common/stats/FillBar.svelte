<script lang="ts">
    import {writable} from "svelte/store";
    import {onMount} from "svelte";
    import {twMerge} from "tailwind-merge";

    export let width: number;
    export let percentage: number;
    export let text: string = '';
    export let variant: 'small' | 'medium' | 'large' = 'medium';

    const loaded = writable(false);

    onMount(() => {
        loaded.set(true);
    });

    let dynamicPercentage = 0;
    setTimeout(() => dynamicPercentage = percentage, 200);
</script>

<div
      class={twMerge(
        'relative overflow-hidden rounded-md bg-gray-300 dark:bg-gray-600',
        variant === 'large' ? 'h-5' : 'h-4'
      )}
      style={`width: ${width}px;`}
    >
      <div
        class={twMerge(
          'bg-primary transition-all duration-1000 ease-in-out',
          variant === 'large' ? 'h-5' : 'h-4'
        )}
        style={`width: ${!$loaded ? 0 : (dynamicPercentage / 100) * width}px; `}
      />
      <div class='absolute inset-y-0 flex w-full justify-center text-sm font-bold leading-4 text-white'>
        {text}
      </div>
    </div>