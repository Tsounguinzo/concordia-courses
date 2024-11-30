<script lang="ts">
    import source from "$lib/data/privacy.md?raw";
    import MarkDown from "$lib/components/common/MarkDown.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
</script>

<Seo title="Privacy" description="Privacy policy of concordia.courses" ogImage="og-image-privacy" ogImageAlt="concordia.courses Privacy page Snapshot" ogDescription="Privacy policy of concordia.courses" ogTitle="Privacy | Concordia.courses"/>
<MarkDown {source}/>