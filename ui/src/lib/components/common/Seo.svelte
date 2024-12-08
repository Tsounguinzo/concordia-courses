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
    export let keywords: string[] | undefined = undefined;

    $: url = $page.url.href;
</script>

<MetaTags
        title={title}
        titleTemplate="%s at Concordia University | Concordia Courses"
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
    {@html `
<meta name="author" content="Beaudelaire Tsoungui Nzodoumkouo, zhengxuan zhao, Russ Aulakh" />
<meta name="keywords"
      content="${keywords?.join(', ')}, Concordia, Courses, Instructors, Reviews, Ratings, Rigor, Difficulty, Student, Insights, University, Montreal, Quebec, Canada, Concordia University, CU, Professor, Concordia Course, Concordia Instructor, Concordia Professor" />
<script type="application/ld+json">
  {
     "@context": "http://schema.org",
     "@type": "${type}",
     "name": "${title}",
     "description": "${description}",
     "url": "${url}",
  }
</script>

<script>
    (function(h,o,t,j,a,r){
        h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
        h._hjSettings={hjid:4980443,hjsv:6};
        a=o.getElementsByTagName('head')[0];
        r=o.createElement('script');r.async=1;
        r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
        a.appendChild(r);
    })(window,document,'https://static.hotjar.com/c/hotjar-','.js?sv=');
</script>
`}
</svelte:head>