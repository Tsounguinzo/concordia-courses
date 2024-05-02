<script lang="ts">
    import CourseSearchBar from "$lib/components/Search/CourseSearchBar.svelte";
    import {getSearchIndex, updateSearchResults} from "$lib/searchIndex";
    import {page} from "$app/stores";
    import {onMount} from "svelte";
    import {toast} from "svelte-sonner";
    import {searchResults} from "$lib/store";
    import Seo from "$lib/components/common/Seo.svelte";


    const alerts: Map<string, string> = new Map([
        ['invalidMail', 'Please use a Concordia email address to authenticate.'],
    ]);

    const { courses, instructors, coursesIndex, instructorsIndex } = getSearchIndex();

    const searchParams = $page.url.searchParams;

    onMount(() => {
        const err = searchParams.get('err');
        if (err === null) return;
        toast.error(alerts.get(err) ?? '');
    });

    const handleInputChange = (query: string) => {
        updateSearchResults(
            query,
            courses,
            coursesIndex,
            instructors,
            instructorsIndex,
        );
    }

</script>
<Seo title="Home" description="Home page of concordia.courses" />
<div class='relative px-6 pt-14 lg:px-8'>
    <div class='mx-auto max-w-2xl py-8'>
        <div class='hidden sm:mb-8 sm:flex sm:justify-center'></div>
        <div class='text-center'>
            <h1 class='mb-6 py-3 text-left text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-200 md:text-5xl'>
                Share and explore student insights on courses and instructors @ Concordia.
            </h1>
            <div class='flex flex-col gap-6 text-center'>
            <CourseSearchBar
                    results={$searchResults}
                    handleInputChange={handleInputChange}
            />
                <a href="/reviews-feed" class='mx-auto cursor-pointer text-sm text-gray-500 underline underline-offset-4 hover:text-gray-400 dark:text-gray-400 dark:hover:text-gray-500 md:text-base'>
                    Explore reviews feed <span aria-hidden='true'>&rarr;</span>
                </a>
            </div>
        </div>
    </div>
</div>