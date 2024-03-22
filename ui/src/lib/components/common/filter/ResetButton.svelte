<script lang="ts">
    import {twMerge} from "tailwind-merge";

    export let className: string = ''
</script>

<div class={twMerge(
        'flex h-8 w-8 items-center justify-center rounded-full bg-slate-200 dark:bg-neutral-700',
        className ?? ''
      )}
>
    <slot/>
</div>