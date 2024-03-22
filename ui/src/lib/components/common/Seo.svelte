<script lang="ts">
    import {page} from '$app/stores';
    import {MetaTags} from "svelte-meta-tags";

    export let title: string;
    export let description: string;
    export let type: string = "WebPage";

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
            title: 'Concordia Course Reviews',
            description: 'Student-led reviews of Concordia University courses. Get insights into classes, and course demands.',
            images: [
                {
                    url: 'https://concordia.courses/og-image.png',
                    width: 800,
                    height: 600,
                    alt: 'Concordia Course Explore Snapshot'
                },
            ],
            siteName: 'Concordia Courses'
        }}
        twitter={{
            handle: '@ConcordiaCourses',
            site: '@ConcordiaCourses',
            cardType: 'summary_large_image',
            title: 'Concordia Course Reviews',
            description: 'Student perspectives on courses.',
            image: 'https://concordia.courses/og-image.pngg',
            imageAlt: 'Concordia Course Explore Snapshot'
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