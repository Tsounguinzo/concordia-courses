<script lang="ts">
    import CourseSearchBar from "$lib/components/Search/CourseSearchBar.svelte";
    import {getSearchIndex, updateSearchResults} from "$lib/searchIndex";
    import {page} from "$app/stores";
    import {onMount} from "svelte";
    import {toast, Toaster} from "svelte-sonner";
    import {searchResults} from "$lib/store";


    const alerts: Map<string, string> = new Map([
        ['invalidMail', 'Please use a Concordia email address to authenticate.'],
    ]);

    const {courses, instructors, coursesIndex, instructorsIndex} = getSearchIndex();

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
            instructors,
            coursesIndex,
            instructorsIndex
        );
    }

</script>
<div class='relative isolate px-6 pt-14 lg:px-8'>
    <div class='mx-auto max-w-2xl py-8'>
        <div class='hidden sm:mb-8 sm:flex sm:justify-center'></div>
        <div class='text-center'>
            <h1 class='mb-6 py-3 text-left text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-200 md:text-5xl'>
                Explore and give reviews for thousands of courses by Concordia students.
            </h1>
            <CourseSearchBar
                    results={$searchResults}
                    handleInputChange={handleInputChange}
            />
        </div>
    </div>
</div>