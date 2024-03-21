<script lang="ts">
    import source from "$lib/data/about.md?raw";
    import MarkDown from "$lib/components/common/MarkDown.svelte";
</script>

<MarkDown {source}/>