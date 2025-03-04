import courses from '$lib/data/searchCourses.json';
import instructors from '$lib/data/instructorsList.json';
import {generateSitemapEntries} from "$lib/utils";

export async function GET() {

    const urls = generateSitemapEntries(courses, instructors);


    return new Response(
        `
		<?xml version="1.0" encoding="UTF-8" ?>
		<urlset
			xmlns="https://www.sitemaps.org/schemas/sitemap/0.9"
			xmlns:xhtml="https://www.w3.org/1999/xhtml"
			xmlns:mobile="https://www.google.com/schemas/sitemap-mobile/1.0"
			xmlns:news="https://www.google.com/schemas/sitemap-news/0.9"
			xmlns:image="https://www.google.com/schemas/sitemap-image/1.1"
			xmlns:video="https://www.google.com/schemas/sitemap-video/1.1"
		>
		${
            urls.map(
                (url) => 
                    `<url>
                        <loc>${url.loc}</loc>
                        <changefreq>${url.changefreq}</changefreq>
                        <priority>${url.priority}</priority>
                     </url>`
            ).join('')
        }
		<url>
			<loc>https://concordia.courses</loc>
			<changefreq>yearly</changefreq>
			<priority>1.0</priority>
		</url>
		<url>
			<loc>https://concordia.courses/about</loc>
			<changefreq>monthly</changefreq>
			<priority>0.7</priority>
		</url>
		<url>
			<loc>https://concordia.courses/explore</loc>
			<changefreq>monthly</changefreq>
			<priority>0.9</priority>
		</url>
		<url>
			<loc>https://concordia.courses/reviews-feed</loc>
			<changefreq>daily</changefreq>
			<priority>1.0</priority>
		</url>
		<url>
			<loc>https://concordia.courses/search</loc>
			<changefreq>daily</changefreq>
			<priority>0.8</priority>
		</url>
		<url>
			<loc>https://concordia.courses/login</loc>
			<changefreq>yearly</changefreq>
			<priority>0.6</priority>
		</url>
		</urlset>`.trim(),
        {
            headers: {
                'Content-Type': 'application/xml'
            }
        }
    );
}