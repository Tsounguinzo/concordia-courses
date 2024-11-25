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
            <a href="https://flyfast.io" class="p-3 bg-gradient-to-r from-blue-500 to-emerald-400 rounded-lg font-semibold max-sm:text-sm">
                Try out my new project, FlyFast.io! 🚀
            </a>
            <div class='flex flex-col gap-6 text-center'>
            <CourseSearchBar
                    results={$searchResults}
                    handleInputChange={handleInputChange}
            />
                <a href="/reviews-feed" class='mx-auto rounded-md bg-slate-50 px-3 py-2 text-sm font-semibold text-gray-500 hover:bg-gray-100 dark:bg-neutral-800 dark:text-gray-200 dark:hover:bg-neutral-700'>
                    Explore reviews feed <span aria-hidden='true'>&rarr;</span>
                </a>
            </div>
        </div>
    </div>
    <div class="flex items-center mt-1">
        <a href="/search" class='rounded-md mx-auto bg-slate-50 px-3 py-2 text-sm font-semibold text-gray-500 hover:bg-gray-100 dark:bg-neutral-800 dark:text-gray-200 dark:hover:bg-neutral-700'>
            Search by review content <span class="rounded-lg bg-primary-500 text-white p-1 ml-1">New</span>
        </a>
    </div>
</div>