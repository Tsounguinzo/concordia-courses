<script lang="ts">
    import {twMerge} from "tailwind-merge";

    export let text: string;
    export let offset: { x: number; y: number } = {x: 0, y: 0};
    export let show: boolean = false;
</script>

<div class='relative'>
    <div class:opacity={show && !!text}
         class={twMerge('opacity-0 text-nowrap absolute z-10 min-w-fit -translate-x-0 -translate-y-full rounded-md bg-white p-2 text-center text-xs font-medium text-gray-700 dark:bg-neutral-500 dark:text-gray-100', $$props.class)}
         style={`left: ${offset.x}px; top: ${offset.y}px;`}>
        {text}
    </div>
    <slot/>
</div>

<style>
    .opacity {
        opacity: 1;
    }
</style>