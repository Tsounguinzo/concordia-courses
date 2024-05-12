<script lang="ts">
    import CourseSearchBar from "$lib/components/Search/CourseSearchBar.svelte";
    import {getSearchIndex, updateSearchResults} from "$lib/searchIndex";
    import {searchResults} from "$lib/store";
    import Seo from "$lib/components/common/Seo.svelte";

    const { courses, instructors, coursesIndex, instructorsIndex } = getSearchIndex();

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
                Give, Share and explore reviews on instructors and courses @ Concordia.
            </h1>
            <div class='flex flex-col gap-6 text-center'>
            <CourseSearchBar
                    results={$searchResults}
                    handleInputChange={handleInputChange}
            />
                <a href="/reviews-feed" class='mx-auto cursor-pointer text-sm text-gray-800 underline underline-offset-4 hover:text-gray-500 dark:text-gray-400 dark:hover:text-gray-500 md:text-base'>
                    Explore reviews feed <span aria-hidden='true'>&rarr;</span>
                </a>
            </div>
        </div>
    </div>
</div>