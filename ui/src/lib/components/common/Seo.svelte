<script lang="ts">
    import {page} from '$app/stores';
    import {MetaTags} from "svelte-meta-tags";

    export let title: string;
    export let description: string;
    export let type: string = "WebPage";
    export let ogDescription: string = 'Student Insights on Instructors and Rigor of Courses at Concordia University';
    export let ogTitle: string = "Reviews of Concordia Courses and Instructors";
    export let ogImage: string = "og-image.png";
    export let ogImageAlt: string = 'concordia.courses Home page Snapshot';

    $: url = $page.url.href;
</script>

<MetaTags
        title={title}
        titleTemplate="%s | Concordia Courses"
        description={description}
        canonical={url}
        openGraph={{
            type: 'website',
            url: `${url}`,
            locale: 'en_CA',
            title: ogTitle,
            description: ogDescription,
            images: [
                {
                    url: `https://concordia.courses/${ogImage}`,
                    width: 800,
                    height: 600,
                    alt: ogImageAlt,
                },
            ],
            siteName: 'Concordia Courses'
        }}
        twitter={{
            handle: '@ConcordiaCourses',
            site: '@ConcordiaCourses',
            cardType: 'summary_large_image',
            title: ogTitle,
            description: ogDescription,
            image: `https://concordia.courses/${ogImage}`,
            imageAlt: ogImageAlt,
        }}
/>

<svelte:head>
    {@html `<script type="application/ld+json">
  {
      "@context": "http://schema.org",
      "@type": "${type}",
        "name": "${title}",
        "description": "${description}",
        "url": "${url}",
      }
    </script>`}
</svelte:head>