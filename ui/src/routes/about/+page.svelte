<script lang="ts">
    import source from "$lib/data/about.md?raw";
    import MarkDown from "$lib/components/common/MarkDown.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
</script>

<Seo title="About" description="Learn about concordia.courses" ogImage="og-image-about" ogImageAlt="concordia.courses About page Snapshot" ogDescription="Learn about concordia.courses" ogTitle="About | Concordia.courses"/>
<MarkDown {source}/>