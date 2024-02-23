<script lang="ts">
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import Transition from "svelte-transition";
    import {twMerge} from "tailwind-merge";

    export let text: string;
    export let offset: { x: number; y: number } = {x: 0, y: 0};
    export let className: string = '';

    export let show: Writable<boolean> = writable(false);
</script>

<span class='relative'>
      <Transition
              show={$show && !!text}
              enter='transition-opacity duration-200'
              enterFrom='opacity-0'
              enterTo='opacity-100'
              leave='transition-opacity duration-200'
              leaveFrom='opacity-100'
              leaveTo='opacity-0'
      >
        <div class={twMerge('absolute z-10 min-w-fit -translate-x-0 -translate-y-full rounded-md bg-white p-2 text-center text-xs font-medium text-gray-700 dark:bg-neutral-500 dark:text-gray-100',className)}
        style={`left: ${offset.x}px; top: ${offset.y}px;`}>
            {text}
        </div>
      </Transition>
    <slot/>
</span>