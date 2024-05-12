<script lang="ts">
    import { cn } from '$lib/utils';
</script>

<div
        class={cn(
		'h-96 w-96 [transform-style:preserve-3d]  [&>*]:[transform-style:preserve-3d]',
		$$props.class
	)}
>
    <slot />
</div>
