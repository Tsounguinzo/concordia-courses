<script lang="ts">
    import source from "$lib/data/tos.md?raw";
    import MarkDown from "$lib/components/common/MarkDown.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
</script>

<Seo title="Terms of Service" description="Terms of Service for concordia.courses" ogImage="og-image-tos" ogImageAlt="concordia.courses Terms of Service page Snapshot" ogDescription="Terms of Service for concordia.courses" ogTitle="Terms of Service | Concordia.courses"/>
<MarkDown {source}/>