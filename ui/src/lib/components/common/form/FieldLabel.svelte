<script lang="ts">
    export let For: string;
</script>
<label class='mb-1 mt-4 text-xs uppercase tracking-wider text-gray-500 dark:text-gray-400' for="{For}">
    <slot/>
</label>