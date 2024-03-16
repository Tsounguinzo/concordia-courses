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

    const {courses, coursesIndex} = getSearchIndex();

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
        );
    }

</script>
<Seo title="StudyHub | Home" description="Home page of concordia.courses" />
<div class='relative isolate px-6 pt-14 lg:px-8'>
    <div class='mx-auto max-w-2xl py-8'>
        <div class='hidden sm:mb-8 sm:flex sm:justify-center'></div>
        <div class='text-center'>
            <h1 class='mb-6 py-3 text-left text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-200 md:text-5xl'>
                Explore and review thousands of courses, straight from Concordia students!
            </h1>
            <CourseSearchBar
                    results={$searchResults}
                    handleInputChange={handleInputChange}
            />
        </div>
    </div>
</div>